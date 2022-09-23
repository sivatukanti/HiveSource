// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.FetchOrientation;
import java.util.Iterator;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import java.util.List;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;

public class GetSchemasOperation extends MetadataOperation
{
    private final String catalogName;
    private final String schemaName;
    private static final TableSchema RESULT_SET_SCHEMA;
    private RowSet rowSet;
    
    protected GetSchemasOperation(final HiveSession parentSession, final String catalogName, final String schemaName) {
        super(parentSession, OperationType.GET_SCHEMAS);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.rowSet = RowSetFactory.create(GetSchemasOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        if (this.isAuthV2Enabled()) {
            final String cmdStr = "catalog : " + this.catalogName + ", schemaPattern : " + this.schemaName;
            this.authorizeMetaGets(HiveOperationType.GET_SCHEMAS, null, cmdStr);
        }
        try {
            final IMetaStoreClient metastoreClient = this.getParentSession().getMetaStoreClient();
            final String schemaPattern = this.convertSchemaPattern(this.schemaName);
            for (final String dbName : metastoreClient.getDatabases(schemaPattern)) {
                this.rowSet.addRow(new Object[] { dbName, "" });
            }
            this.setState(OperationState.FINISHED);
        }
        catch (Exception e) {
            this.setState(OperationState.ERROR);
            throw new HiveSQLException(e);
        }
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        return GetSchemasOperation.RESULT_SET_SCHEMA;
    }
    
    @Override
    public RowSet getNextRowSet(final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        this.validateDefaultFetchOrientation(orientation);
        if (orientation.equals(FetchOrientation.FETCH_FIRST)) {
            this.rowSet.setStartOffset(0L);
        }
        return this.rowSet.extractSubset((int)maxRows);
    }
    
    static {
        RESULT_SET_SCHEMA = new TableSchema().addStringColumn("TABLE_SCHEM", "Schema name.").addStringColumn("TABLE_CATALOG", "Catalog name.");
    }
}
