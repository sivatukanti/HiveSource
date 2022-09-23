// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import java.util.List;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;

public class GetTableTypesOperation extends MetadataOperation
{
    protected static TableSchema RESULT_SET_SCHEMA;
    private final RowSet rowSet;
    private final TableTypeMapping tableTypeMapping;
    
    protected GetTableTypesOperation(final HiveSession parentSession) {
        super(parentSession, OperationType.GET_TABLE_TYPES);
        final String tableMappingStr = this.getParentSession().getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_TABLE_TYPE_MAPPING);
        this.tableTypeMapping = TableTypeMappingFactory.getTableTypeMapping(tableMappingStr);
        this.rowSet = RowSetFactory.create(GetTableTypesOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        if (this.isAuthV2Enabled()) {
            this.authorizeMetaGets(HiveOperationType.GET_TABLETYPES, null);
        }
        try {
            for (final TableType type : TableType.values()) {
                this.rowSet.addRow(new String[] { this.tableTypeMapping.mapToClientType(type.toString()) });
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
        return GetTableTypesOperation.RESULT_SET_SCHEMA;
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
        GetTableTypesOperation.RESULT_SET_SCHEMA = new TableSchema().addStringColumn("TABLE_TYPE", "Table type name.");
    }
}
