// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.Type;
import org.apache.hive.service.cli.FetchOrientation;
import java.util.ArrayList;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import java.util.Iterator;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.ColumnDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import java.util.Map;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.regex.Pattern;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;

public class GetColumnsOperation extends MetadataOperation
{
    private static final TableSchema RESULT_SET_SCHEMA;
    private final String catalogName;
    private final String schemaName;
    private final String tableName;
    private final String columnName;
    private final RowSet rowSet;
    
    protected GetColumnsOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String tableName, final String columnName) {
        super(parentSession, OperationType.GET_COLUMNS);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.rowSet = RowSetFactory.create(GetColumnsOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        try {
            final IMetaStoreClient metastoreClient = this.getParentSession().getMetaStoreClient();
            final String schemaPattern = this.convertSchemaPattern(this.schemaName);
            final String tablePattern = this.convertIdentifierPattern(this.tableName, true);
            Pattern columnPattern = null;
            if (this.columnName != null) {
                columnPattern = Pattern.compile(this.convertIdentifierPattern(this.columnName, false));
            }
            final List<String> dbNames = metastoreClient.getDatabases(schemaPattern);
            Collections.sort(dbNames);
            final Map<String, List<String>> db2Tabs = new HashMap<String, List<String>>();
            for (final String dbName : dbNames) {
                final List<String> tableNames = metastoreClient.getTables(dbName, tablePattern);
                Collections.sort(tableNames);
                db2Tabs.put(dbName, tableNames);
            }
            if (this.isAuthV2Enabled()) {
                final List<HivePrivilegeObject> privObjs = this.getPrivObjs(db2Tabs);
                final String cmdStr = "catalog : " + this.catalogName + ", schemaPattern : " + this.schemaName + ", tablePattern : " + this.tableName;
                this.authorizeMetaGets(HiveOperationType.GET_COLUMNS, privObjs, cmdStr);
            }
            for (final Map.Entry<String, List<String>> dbTabs : db2Tabs.entrySet()) {
                final String dbName2 = dbTabs.getKey();
                final List<String> tableNames2 = dbTabs.getValue();
                for (final Table table : metastoreClient.getTableObjectsByName(dbName2, tableNames2)) {
                    final TableSchema schema = new TableSchema(metastoreClient.getSchema(dbName2, table.getTableName()));
                    for (final ColumnDescriptor column : schema.getColumnDescriptors()) {
                        if (columnPattern != null && !columnPattern.matcher(column.getName()).matches()) {
                            continue;
                        }
                        final Object[] rowData = { null, table.getDbName(), table.getTableName(), column.getName(), column.getType().toJavaSQLType(), column.getTypeName(), column.getTypeDescriptor().getColumnSize(), null, column.getTypeDescriptor().getDecimalDigits(), column.getType().getNumPrecRadix(), 1, column.getComment(), null, null, null, null, column.getOrdinalPosition(), "YES", null, null, null, null, "NO" };
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
    
    private List<HivePrivilegeObject> getPrivObjs(final Map<String, List<String>> db2Tabs) {
        final List<HivePrivilegeObject> privObjs = new ArrayList<HivePrivilegeObject>();
        for (final Map.Entry<String, List<String>> dbTabs : db2Tabs.entrySet()) {
            for (final String tabName : dbTabs.getValue()) {
                privObjs.add(new HivePrivilegeObject(HivePrivilegeObject.HivePrivilegeObjectType.TABLE_OR_VIEW, (String)dbTabs.getKey(), tabName));
            }
        }
        return privObjs;
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        return GetColumnsOperation.RESULT_SET_SCHEMA;
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
        RESULT_SET_SCHEMA = new TableSchema().addPrimitiveColumn("TABLE_CAT", Type.STRING_TYPE, "Catalog name. NULL if not applicable").addPrimitiveColumn("TABLE_SCHEM", Type.STRING_TYPE, "Schema name").addPrimitiveColumn("TABLE_NAME", Type.STRING_TYPE, "Table name").addPrimitiveColumn("COLUMN_NAME", Type.STRING_TYPE, "Column name").addPrimitiveColumn("DATA_TYPE", Type.INT_TYPE, "SQL type from java.sql.Types").addPrimitiveColumn("TYPE_NAME", Type.STRING_TYPE, "Data source dependent type name, for a UDT the type name is fully qualified").addPrimitiveColumn("COLUMN_SIZE", Type.INT_TYPE, "Column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.").addPrimitiveColumn("BUFFER_LENGTH", Type.TINYINT_TYPE, "Unused").addPrimitiveColumn("DECIMAL_DIGITS", Type.INT_TYPE, "The number of fractional digits").addPrimitiveColumn("NUM_PREC_RADIX", Type.INT_TYPE, "Radix (typically either 10 or 2)").addPrimitiveColumn("NULLABLE", Type.INT_TYPE, "Is NULL allowed").addPrimitiveColumn("REMARKS", Type.STRING_TYPE, "Comment describing column (may be null)").addPrimitiveColumn("COLUMN_DEF", Type.STRING_TYPE, "Default value (may be null)").addPrimitiveColumn("SQL_DATA_TYPE", Type.INT_TYPE, "Unused").addPrimitiveColumn("SQL_DATETIME_SUB", Type.INT_TYPE, "Unused").addPrimitiveColumn("CHAR_OCTET_LENGTH", Type.INT_TYPE, "For char types the maximum number of bytes in the column").addPrimitiveColumn("ORDINAL_POSITION", Type.INT_TYPE, "Index of column in table (starting at 1)").addPrimitiveColumn("IS_NULLABLE", Type.STRING_TYPE, "\"NO\" means column definitely does not allow NULL values; \"YES\" means the column might allow NULL values. An empty string means nobody knows.").addPrimitiveColumn("SCOPE_CATALOG", Type.STRING_TYPE, "Catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)").addPrimitiveColumn("SCOPE_SCHEMA", Type.STRING_TYPE, "Schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)").addPrimitiveColumn("SCOPE_TABLE", Type.STRING_TYPE, "Table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)").addPrimitiveColumn("SOURCE_DATA_TYPE", Type.SMALLINT_TYPE, "Source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)").addPrimitiveColumn("IS_AUTO_INCREMENT", Type.STRING_TYPE, "Indicates whether this column is auto incremented.");
    }
}
