// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import java.util.Collection;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.query.QueryResult;
import java.sql.ResultSet;
import org.datanucleus.store.query.QueryNotUniqueException;
import org.datanucleus.store.query.NoQueryResultsException;
import java.util.Iterator;
import org.datanucleus.store.connection.ManagedConnection;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.datanucleus.metadata.StoredProcQueryParameterMode;
import java.sql.Connection;
import java.util.HashMap;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import java.sql.CallableStatement;
import org.datanucleus.store.query.AbstractStoredProcedureQuery;

public class StoredProcedureQuery extends AbstractStoredProcedureQuery
{
    CallableStatement stmt;
    
    public StoredProcedureQuery(final StoreManager storeMgr, final ExecutionContext ec, final StoredProcedureQuery query) {
        super(storeMgr, ec, query);
    }
    
    public StoredProcedureQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec, (String)null);
    }
    
    public StoredProcedureQuery(final StoreManager storeMgr, final ExecutionContext ec, final String procName) {
        super(storeMgr, ec, procName);
    }
    
    @Override
    protected void compileInternal(final Map parameterValues) {
        final DatastoreAdapter dba = ((RDBMSStoreManager)this.storeMgr).getDatastoreAdapter();
        if (!dba.supportsOption("StoredProcs")) {
            throw new NucleusUserException("This RDBMS does not support stored procedures!");
        }
    }
    
    @Override
    public boolean processesRangeInDatastoreQuery() {
        return true;
    }
    
    @Override
    protected Object executeQuery(final Map parameters) {
        this.inputParameters = new HashMap();
        if (this.implicitParameters != null) {
            this.inputParameters.putAll(this.implicitParameters);
        }
        if (parameters != null) {
            this.inputParameters.putAll(parameters);
        }
        this.prepareDatastore();
        final boolean failed = true;
        long start = 0L;
        if (this.ec.getStatistics() != null) {
            start = System.currentTimeMillis();
            this.ec.getStatistics().queryBegin();
        }
        try {
            return this.performExecute(this.inputParameters);
        }
        finally {
            if (this.ec.getStatistics() != null) {
                if (failed) {
                    this.ec.getStatistics().queryExecutedWithError();
                }
                else {
                    this.ec.getStatistics().queryExecuted(System.currentTimeMillis() - start);
                }
            }
        }
    }
    
    @Override
    protected Object performExecute(final Map parameters) {
        try {
            final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
            final ManagedConnection mconn = storeMgr.getConnection(this.ec);
            try {
                final Connection conn = (Connection)mconn.getConnection();
                final StringBuffer stmtStr = new StringBuffer("CALL " + this.procedureName);
                stmtStr.append("(");
                if (this.storedProcParams != null && !this.storedProcParams.isEmpty()) {
                    final Iterator<StoredProcedureParameter> paramIter = this.storedProcParams.iterator();
                    while (paramIter.hasNext()) {
                        paramIter.next();
                        stmtStr.append("?");
                        if (paramIter.hasNext()) {
                            stmtStr.append(",");
                        }
                    }
                }
                stmtStr.append(")");
                this.stmt = conn.prepareCall(stmtStr.toString());
                boolean hasOutputParams = false;
                if (this.storedProcParams != null && !this.storedProcParams.isEmpty()) {
                    for (final StoredProcedureParameter param : this.storedProcParams) {
                        if (param.getMode() == StoredProcQueryParameterMode.IN || param.getMode() == StoredProcQueryParameterMode.INOUT) {
                            if (param.getType() == Integer.class) {
                                if (param.getName() != null) {
                                    this.stmt.setInt(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setInt(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Long.class) {
                                if (param.getName() != null) {
                                    this.stmt.setLong(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setLong(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Short.class) {
                                if (param.getName() != null) {
                                    this.stmt.setShort(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setShort(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Double.class) {
                                if (param.getName() != null) {
                                    this.stmt.setDouble(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setDouble(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Float.class) {
                                if (param.getName() != null) {
                                    this.stmt.setFloat(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setFloat(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Boolean.class) {
                                if (param.getName() != null) {
                                    this.stmt.setBoolean(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setBoolean(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == String.class) {
                                if (param.getName() != null) {
                                    this.stmt.setString(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setString(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == Date.class) {
                                if (param.getName() != null) {
                                    this.stmt.setDate(param.getName(), parameters.get(param.getName()));
                                }
                                else {
                                    this.stmt.setDate(param.getPosition(), parameters.get(param.getPosition()));
                                }
                            }
                            else if (param.getType() == BigInteger.class) {
                                if (param.getName() != null) {
                                    this.stmt.setLong(param.getName(), parameters.get(param.getName()).longValue());
                                }
                                else {
                                    this.stmt.setLong(param.getPosition(), parameters.get(param.getPosition()).longValue());
                                }
                            }
                            else {
                                if (param.getType() != BigDecimal.class) {
                                    throw new NucleusException("Dont currently support stored proc input params of type " + param.getType());
                                }
                                if (param.getName() != null) {
                                    this.stmt.setDouble(param.getName(), parameters.get(param.getName()).doubleValue());
                                }
                                else {
                                    this.stmt.setDouble(param.getPosition(), parameters.get(param.getPosition()).doubleValue());
                                }
                            }
                        }
                        if (param.getMode() == StoredProcQueryParameterMode.OUT || param.getMode() == StoredProcQueryParameterMode.INOUT) {
                            if (param.getType() == Integer.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 4);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 4);
                                }
                            }
                            else if (param.getType() == Long.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 4);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 4);
                                }
                            }
                            else if (param.getType() == Short.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 4);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 4);
                                }
                            }
                            else if (param.getType() == Double.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 8);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 8);
                                }
                            }
                            else if (param.getType() == Float.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 6);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 6);
                                }
                            }
                            else if (param.getType() == Boolean.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 16);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 16);
                                }
                            }
                            else if (param.getType() == String.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 12);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 12);
                                }
                            }
                            else if (param.getType() == Date.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 91);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 91);
                                }
                            }
                            else if (param.getType() == BigInteger.class) {
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), -5);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), -5);
                                }
                            }
                            else {
                                if (param.getType() != BigDecimal.class) {
                                    throw new NucleusException("Dont currently support stored proc output params of type " + param.getType());
                                }
                                if (param.getName() != null) {
                                    this.stmt.registerOutParameter(param.getName(), 8);
                                }
                                else {
                                    this.stmt.registerOutParameter(param.getPosition(), 8);
                                }
                            }
                            hasOutputParams = true;
                        }
                    }
                }
                if (NucleusLogger.DATASTORE_NATIVE.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_NATIVE.debug(stmtStr.toString());
                }
                final boolean hasResultSet = this.stmt.execute();
                if (hasOutputParams) {
                    for (final StoredProcedureParameter param2 : this.storedProcParams) {
                        if (param2.getMode() == StoredProcQueryParameterMode.OUT || param2.getMode() == StoredProcQueryParameterMode.INOUT) {
                            Object value = null;
                            if (param2.getType() == Integer.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getInt(param2.getName());
                                }
                                else {
                                    value = this.stmt.getInt(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Long.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getLong(param2.getName());
                                }
                                else {
                                    value = this.stmt.getLong(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Short.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getShort(param2.getName());
                                }
                                else {
                                    value = this.stmt.getShort(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Double.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getDouble(param2.getName());
                                }
                                else {
                                    value = this.stmt.getDouble(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Float.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getFloat(param2.getName());
                                }
                                else {
                                    value = this.stmt.getFloat(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Boolean.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getBoolean(param2.getName());
                                }
                                else {
                                    value = this.stmt.getBoolean(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == String.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getString(param2.getName());
                                }
                                else {
                                    value = this.stmt.getString(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == Date.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getDate(param2.getName());
                                }
                                else {
                                    value = this.stmt.getDate(param2.getPosition());
                                }
                            }
                            else if (param2.getType() == BigInteger.class) {
                                if (param2.getName() != null) {
                                    value = this.stmt.getLong(param2.getName());
                                }
                                else {
                                    value = this.stmt.getLong(param2.getPosition());
                                }
                            }
                            else {
                                if (param2.getType() != BigDecimal.class) {
                                    throw new NucleusUserException("Dont currently support output parameters of type=" + param2.getType());
                                }
                                if (param2.getName() != null) {
                                    value = this.stmt.getDouble(param2.getName());
                                }
                                else {
                                    value = this.stmt.getDouble(param2.getPosition());
                                }
                            }
                            if (this.outputParamValues == null) {
                                this.outputParamValues = new HashMap();
                            }
                            if (param2.getName() != null) {
                                this.outputParamValues.put(param2.getName(), value);
                            }
                            else {
                                this.outputParamValues.put(param2.getPosition(), value);
                            }
                        }
                    }
                }
                return hasResultSet;
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(StoredProcedureQuery.LOCALISER.msg("059027", this.procedureName), e);
        }
    }
    
    @Override
    public boolean hasMoreResults() {
        if (this.stmt == null) {
            throw new NucleusUserException("Cannot check for more results until the stored procedure has been executed");
        }
        try {
            return this.stmt.getMoreResults();
        }
        catch (SQLException sqle) {
            return false;
        }
    }
    
    @Override
    public int getUpdateCount() {
        if (this.stmt == null) {
            throw new NucleusUserException("Cannot check for update count until the stored procedure has been executed");
        }
        final ManagedConnection mconn = this.storeMgr.getConnection(this.ec);
        try {
            ++this.resultSetNumber;
            return this.stmt.getUpdateCount();
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception from CallableStatement.getUpdateCount", sqle);
        }
        finally {
            mconn.release();
        }
    }
    
    @Override
    public Object getNextResults() {
        if (this.stmt == null) {
            throw new NucleusUserException("Cannot check for more results until the stored procedure has been executed");
        }
        final ManagedConnection mconn = this.storeMgr.getConnection(this.ec);
        try {
            ++this.resultSetNumber;
            final ResultSet rs = this.stmt.getResultSet();
            final QueryResult qr = this.getResultsForResultSet((RDBMSStoreManager)this.storeMgr, rs, mconn);
            if (this.shouldReturnSingleRow()) {
                try {
                    if (qr == null || qr.size() == 0) {
                        throw new NoQueryResultsException("No query results were returned");
                    }
                    final Iterator qrIter = qr.iterator();
                    final Object firstRow = qrIter.next();
                    if (qrIter.hasNext()) {
                        throw new QueryNotUniqueException();
                    }
                    return firstRow;
                }
                finally {
                    if (qr != null) {
                        this.close(qr);
                    }
                }
            }
            return qr;
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception from CallableStatement.getResultSet", sqle);
        }
        finally {
            mconn.release();
        }
    }
    
    protected QueryResult getResultsForResultSet(final RDBMSStoreManager storeMgr, final ResultSet rs, final ManagedConnection mconn) throws SQLException {
        ResultObjectFactory rof = null;
        if (this.resultMetaDatas != null) {
            rof = new ResultMetaDataROF(storeMgr, this.resultMetaDatas[this.resultSetNumber]);
        }
        else {
            rof = RDBMSQueryUtils.getResultObjectFactoryForNoCandidateClass(storeMgr, rs, (this.resultClasses != null) ? this.resultClasses[this.resultSetNumber] : null);
        }
        final String resultSetType = RDBMSQueryUtils.getResultSetTypeForQuery(this);
        AbstractRDBMSQueryResult qr = null;
        if (resultSetType.equals("scroll-insensitive") || resultSetType.equals("scroll-sensitive")) {
            qr = new ScrollableQueryResult(this, rof, rs, null);
        }
        else {
            qr = new ForwardQueryResult(this, rof, rs, null);
        }
        qr.initialise();
        final QueryResult qr2 = qr;
        final ManagedConnection mconn2 = mconn;
        final ManagedConnectionResourceListener listener = new ManagedConnectionResourceListener() {
            @Override
            public void transactionFlushed() {
            }
            
            @Override
            public void transactionPreClose() {
                qr2.disconnect();
            }
            
            @Override
            public void managedConnectionPreClose() {
                if (!StoredProcedureQuery.this.ec.getTransaction().isActive()) {
                    qr2.disconnect();
                }
            }
            
            @Override
            public void managedConnectionPostClose() {
            }
            
            @Override
            public void resourcePostClose() {
                mconn2.removeListener(this);
            }
        };
        mconn.addListener(listener);
        qr.addConnectionListener(listener);
        return qr;
    }
}
