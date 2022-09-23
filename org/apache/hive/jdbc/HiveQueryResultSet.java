// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import org.apache.commons.logging.LogFactory;
import java.sql.ResultSetMetaData;
import org.apache.hive.service.cli.thrift.TRowSet;
import org.apache.hive.service.cli.thrift.TFetchResultsResp;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.thrift.TFetchResultsReq;
import org.apache.hive.service.cli.thrift.TFetchOrientation;
import java.util.Collection;
import org.apache.hive.service.cli.thrift.TTableSchema;
import org.apache.hive.service.cli.thrift.TGetResultSetMetadataResp;
import org.apache.hive.service.cli.thrift.TCLIServiceConstants;
import org.apache.hive.service.cli.thrift.TTypeEntry;
import org.apache.hive.service.cli.thrift.TColumnDesc;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.thrift.TGetResultSetMetadataReq;
import org.apache.hive.service.cli.thrift.TTypeQualifiers;
import org.apache.hive.service.cli.thrift.TTypeQualifierValue;
import org.apache.hive.service.cli.thrift.TPrimitiveTypeEntry;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import java.util.Iterator;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TCLIService;
import org.apache.commons.logging.Log;

public class HiveQueryResultSet extends HiveBaseResultSet
{
    public static final Log LOG;
    private TCLIService.Iface client;
    private TOperationHandle stmtHandle;
    private TSessionHandle sessHandle;
    private int maxRows;
    private int fetchSize;
    private int rowsFetched;
    private RowSet fetchedRows;
    private Iterator<Object[]> fetchedRowsItr;
    private boolean isClosed;
    private boolean emptyResultSet;
    private boolean isScrollable;
    private boolean fetchFirst;
    private final TProtocolVersion protocol;
    private ReentrantLock transportLock;
    
    protected HiveQueryResultSet(final Builder builder) throws SQLException {
        this.rowsFetched = 0;
        this.isClosed = false;
        this.emptyResultSet = false;
        this.isScrollable = false;
        this.fetchFirst = false;
        this.statement = builder.statement;
        this.client = builder.client;
        this.stmtHandle = builder.stmtHandle;
        this.sessHandle = builder.sessHandle;
        this.fetchSize = builder.fetchSize;
        this.transportLock = builder.transportLock;
        this.columnNames = new ArrayList<String>();
        this.normalizedColumnNames = new ArrayList<String>();
        this.columnTypes = new ArrayList<String>();
        this.columnAttributes = new ArrayList<JdbcColumnAttributes>();
        if (builder.retrieveSchema) {
            this.retrieveSchema();
        }
        else {
            this.setSchema(builder.colNames, builder.colTypes, builder.colAttributes);
        }
        this.emptyResultSet = builder.emptyResultSet;
        if (builder.emptyResultSet) {
            this.maxRows = 0;
        }
        else {
            this.maxRows = builder.maxRows;
        }
        this.isScrollable = builder.isScrollable;
        this.protocol = builder.getProtocolVersion();
    }
    
    private static JdbcColumnAttributes getColumnAttributes(final TPrimitiveTypeEntry primitiveTypeEntry) {
        JdbcColumnAttributes ret = null;
        if (primitiveTypeEntry.isSetTypeQualifiers()) {
            final TTypeQualifiers tq = primitiveTypeEntry.getTypeQualifiers();
            switch (primitiveTypeEntry.getType()) {
                case CHAR_TYPE:
                case VARCHAR_TYPE: {
                    final TTypeQualifierValue val = tq.getQualifiers().get("characterMaximumLength");
                    if (val != null) {
                        ret = new JdbcColumnAttributes(val.getI32Value(), 0);
                        break;
                    }
                    break;
                }
                case DECIMAL_TYPE: {
                    final TTypeQualifierValue prec = tq.getQualifiers().get("precision");
                    final TTypeQualifierValue scale = tq.getQualifiers().get("scale");
                    ret = new JdbcColumnAttributes((prec == null) ? 10 : prec.getI32Value(), (scale == null) ? 0 : scale.getI32Value());
                    break;
                }
            }
        }
        return ret;
    }
    
    private void retrieveSchema() throws SQLException {
        try {
            final TGetResultSetMetadataReq metadataReq = new TGetResultSetMetadataReq(this.stmtHandle);
            TGetResultSetMetadataResp metadataResp;
            if (this.transportLock == null) {
                metadataResp = this.client.GetResultSetMetadata(metadataReq);
            }
            else {
                this.transportLock.lock();
                try {
                    metadataResp = this.client.GetResultSetMetadata(metadataReq);
                }
                finally {
                    this.transportLock.unlock();
                }
            }
            Utils.verifySuccess(metadataResp.getStatus());
            final StringBuilder namesSb = new StringBuilder();
            final StringBuilder typesSb = new StringBuilder();
            final TTableSchema schema = metadataResp.getSchema();
            if (schema == null || !schema.isSetColumns()) {
                return;
            }
            this.setSchema(new TableSchema(schema));
            final List<TColumnDesc> columns = schema.getColumns();
            for (int pos = 0; pos < schema.getColumnsSize(); ++pos) {
                if (pos != 0) {
                    namesSb.append(",");
                    typesSb.append(",");
                }
                final String columnName = columns.get(pos).getColumnName();
                this.columnNames.add(columnName);
                this.normalizedColumnNames.add(columnName.toLowerCase());
                final TPrimitiveTypeEntry primitiveTypeEntry = columns.get(pos).getTypeDesc().getTypes().get(0).getPrimitiveEntry();
                final String columnTypeName = TCLIServiceConstants.TYPE_NAMES.get(primitiveTypeEntry.getType());
                this.columnTypes.add(columnTypeName);
                this.columnAttributes.add(getColumnAttributes(primitiveTypeEntry));
            }
        }
        catch (SQLException eS) {
            throw eS;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new SQLException("Could not create ResultSet: " + ex.getMessage(), ex);
        }
    }
    
    private void setSchema(final List<String> colNames, final List<String> colTypes, final List<JdbcColumnAttributes> colAttributes) {
        this.columnNames.addAll(colNames);
        this.columnTypes.addAll(colTypes);
        this.columnAttributes.addAll(colAttributes);
        for (final String colName : colNames) {
            this.normalizedColumnNames.add(colName.toLowerCase());
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (this.statement != null && this.statement instanceof HiveStatement) {
            final HiveStatement s = (HiveStatement)this.statement;
            s.closeClientOperation();
        }
        this.client = null;
        this.stmtHandle = null;
        this.sessHandle = null;
        this.isClosed = true;
    }
    
    @Override
    public boolean next() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        if (this.emptyResultSet || (this.maxRows > 0 && this.rowsFetched >= this.maxRows)) {
            return false;
        }
        try {
            TFetchOrientation orientation = TFetchOrientation.FETCH_NEXT;
            if (this.fetchFirst) {
                orientation = TFetchOrientation.FETCH_FIRST;
                this.fetchedRows = null;
                this.fetchedRowsItr = null;
                this.fetchFirst = false;
            }
            if (this.fetchedRows == null || !this.fetchedRowsItr.hasNext()) {
                final TFetchResultsReq fetchReq = new TFetchResultsReq(this.stmtHandle, orientation, this.fetchSize);
                TFetchResultsResp fetchResp;
                if (this.transportLock == null) {
                    fetchResp = this.client.FetchResults(fetchReq);
                }
                else {
                    this.transportLock.lock();
                    try {
                        fetchResp = this.client.FetchResults(fetchReq);
                    }
                    finally {
                        this.transportLock.unlock();
                    }
                }
                Utils.verifySuccessWithInfo(fetchResp.getStatus());
                final TRowSet results = fetchResp.getResults();
                this.fetchedRows = RowSetFactory.create(results, this.protocol);
                this.fetchedRowsItr = this.fetchedRows.iterator();
            }
            final String rowStr = "";
            if (!this.fetchedRowsItr.hasNext()) {
                return false;
            }
            this.row = this.fetchedRowsItr.next();
            ++this.rowsFetched;
            if (HiveQueryResultSet.LOG.isDebugEnabled()) {
                HiveQueryResultSet.LOG.debug("Fetched row string: " + rowStr);
            }
        }
        catch (SQLException eS) {
            throw eS;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new SQLException("Error retrieving next row", ex);
        }
        return true;
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        return super.getMetaData();
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        this.fetchSize = rows;
    }
    
    @Override
    public int getType() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        if (this.isScrollable) {
            return 1004;
        }
        return 1003;
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        return this.fetchSize;
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        if (!this.isScrollable) {
            throw new SQLException("Method not supported for TYPE_FORWARD_ONLY resultset");
        }
        this.fetchFirst = true;
        this.rowsFetched = 0;
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Resultset is closed");
        }
        return this.rowsFetched == 0;
    }
    
    @Override
    public int getRow() throws SQLException {
        return this.rowsFetched;
    }
    
    static {
        LOG = LogFactory.getLog(HiveQueryResultSet.class);
    }
    
    public static class Builder
    {
        private final Connection connection;
        private final Statement statement;
        private TCLIService.Iface client;
        private TOperationHandle stmtHandle;
        private TSessionHandle sessHandle;
        private int maxRows;
        private boolean retrieveSchema;
        private List<String> colNames;
        private List<String> colTypes;
        private List<JdbcColumnAttributes> colAttributes;
        private int fetchSize;
        private boolean emptyResultSet;
        private boolean isScrollable;
        private ReentrantLock transportLock;
        
        public Builder(final Statement statement) throws SQLException {
            this.client = null;
            this.stmtHandle = null;
            this.sessHandle = null;
            this.maxRows = 0;
            this.retrieveSchema = true;
            this.fetchSize = 50;
            this.emptyResultSet = false;
            this.isScrollable = false;
            this.transportLock = null;
            this.statement = statement;
            this.connection = statement.getConnection();
        }
        
        public Builder(final Connection connection) {
            this.client = null;
            this.stmtHandle = null;
            this.sessHandle = null;
            this.maxRows = 0;
            this.retrieveSchema = true;
            this.fetchSize = 50;
            this.emptyResultSet = false;
            this.isScrollable = false;
            this.transportLock = null;
            this.statement = null;
            this.connection = connection;
        }
        
        public Builder setClient(final TCLIService.Iface client) {
            this.client = client;
            return this;
        }
        
        public Builder setStmtHandle(final TOperationHandle stmtHandle) {
            this.stmtHandle = stmtHandle;
            return this;
        }
        
        public Builder setSessionHandle(final TSessionHandle sessHandle) {
            this.sessHandle = sessHandle;
            return this;
        }
        
        public Builder setMaxRows(final int maxRows) {
            this.maxRows = maxRows;
            return this;
        }
        
        public Builder setSchema(final List<String> colNames, final List<String> colTypes) {
            final List<JdbcColumnAttributes> colAttributes = new ArrayList<JdbcColumnAttributes>();
            for (int idx = 0; idx < colTypes.size(); ++idx) {
                colAttributes.add(null);
            }
            return this.setSchema(colNames, colTypes, colAttributes);
        }
        
        public Builder setSchema(final List<String> colNames, final List<String> colTypes, final List<JdbcColumnAttributes> colAttributes) {
            (this.colNames = new ArrayList<String>()).addAll(colNames);
            (this.colTypes = new ArrayList<String>()).addAll(colTypes);
            (this.colAttributes = new ArrayList<JdbcColumnAttributes>()).addAll(colAttributes);
            this.retrieveSchema = false;
            return this;
        }
        
        public Builder setFetchSize(final int fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }
        
        public Builder setEmptyResultSet(final boolean emptyResultSet) {
            this.emptyResultSet = emptyResultSet;
            return this;
        }
        
        public Builder setScrollable(final boolean setScrollable) {
            this.isScrollable = setScrollable;
            return this;
        }
        
        public Builder setTransportLock(final ReentrantLock transportLock) {
            this.transportLock = transportLock;
            return this;
        }
        
        public HiveQueryResultSet build() throws SQLException {
            return new HiveQueryResultSet(this);
        }
        
        public TProtocolVersion getProtocolVersion() throws SQLException {
            return ((HiveConnection)this.connection).getProtocol();
        }
    }
}
