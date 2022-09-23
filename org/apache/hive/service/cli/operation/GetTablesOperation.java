// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.FetchOrientation;
import java.util.Iterator;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObjectUtils;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import java.util.Collection;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.ArrayList;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.RowSet;
import java.util.List;

public class GetTablesOperation extends MetadataOperation
{
    private final String catalogName;
    private final String schemaName;
    private final String tableName;
    private final List<String> tableTypes;
    private final RowSet rowSet;
    private final TableTypeMapping tableTypeMapping;
    private static final TableSchema RESULT_SET_SCHEMA;
    
    protected GetTablesOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) {
        super(parentSession, OperationType.GET_TABLES);
        this.tableTypes = new ArrayList<String>();
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        final String tableMappingStr = this.getParentSession().getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_TABLE_TYPE_MAPPING);
        this.tableTypeMapping = TableTypeMappingFactory.getTableTypeMapping(tableMappingStr);
        if (tableTypes != null) {
            this.tableTypes.addAll(tableTypes);
        }
        this.rowSet = RowSetFactory.create(GetTablesOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        try {
            final IMetaStoreClient metastoreClient = this.getParentSession().getMetaStoreClient();
            final String schemaPattern = this.convertSchemaPattern(this.schemaName);
            final List<String> matchingDbs = metastoreClient.getDatabases(schemaPattern);
            if (this.isAuthV2Enabled()) {
                final List<HivePrivilegeObject> privObjs = (List<HivePrivilegeObject>)HivePrivilegeObjectUtils.getHivePrivDbObjects((List)matchingDbs);
                final String cmdStr = "catalog : " + this.catalogName + ", schemaPattern : " + this.schemaName;
                this.authorizeMetaGets(HiveOperationType.GET_TABLES, privObjs, cmdStr);
            }
            final String tablePattern = this.convertIdentifierPattern(this.tableName, true);
            for (final String dbName : metastoreClient.getDatabases(schemaPattern)) {
                final List<String> tableNames = metastoreClient.getTables(dbName, tablePattern);
                for (final Table table : metastoreClient.getTableObjectsByName(dbName, tableNames)) {
                    final Object[] rowData = { "", table.getDbName(), table.getTableName(), this.tableTypeMapping.mapToClientType(table.getTableType()), table.getParameters().get("comment") };
                    if (this.tableTypes.isEmpty() || this.tableTypes.contains(this.tableTypeMapping.mapToClientType(table.getTableType()))) {
                        this.rowSet.addRow(rowData);
                    }
                }
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
        return GetTablesOperation.RESULT_SET_SCHEMA;
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
        RESULT_SET_SCHEMA = new TableSchema().addStringColumn("TABLE_CAT", "Catalog name. NULL if not applicable.").addStringColumn("TABLE_SCHEM", "Schema name.").addStringColumn("TABLE_NAME", "Table name.").addStringColumn("TABLE_TYPE", "The table type, e.g. \"TABLE\", \"VIEW\", etc.").addStringColumn("REMARKS", "Comments about the table.");
    }
}
