// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.util.NucleusLogger;
import java.util.HashSet;
import org.datanucleus.util.StringUtils;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.exceptions.NucleusException;
import java.sql.Connection;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.HashMap;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.Map;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.schema.StoreSchemaHandler;

public class RDBMSSchemaHandler implements StoreSchemaHandler
{
    protected static final Localiser LOCALISER;
    protected final long COLUMN_INFO_EXPIRATION_MS = 300000L;
    protected final RDBMSStoreManager storeMgr;
    protected Map<String, StoreSchemaData> schemaDataByName;
    
    public RDBMSSchemaHandler(final RDBMSStoreManager storeMgr) {
        this.schemaDataByName = new HashMap<String, StoreSchemaData>();
        this.storeMgr = storeMgr;
    }
    
    @Override
    public void clear() {
        this.schemaDataByName.clear();
    }
    
    @Override
    public void createSchema(final Object connection, final String schemaName) {
        throw new NucleusUserException("DataNucleus doesnt currently support creation of schemas for RDBMS");
    }
    
    @Override
    public void deleteSchema(final Object connection, final String schemaName) {
        throw new NucleusUserException("DataNucleus doesnt currently support deletion of schemas for RDBMS");
    }
    
    @Override
    public StoreSchemaData getSchemaData(final Object connection, final String name, final Object[] values) {
        if (values == null) {
            if (name.equalsIgnoreCase("types")) {
                StoreSchemaData info = this.schemaDataByName.get("types");
                if (info == null) {
                    info = this.getRDBMSTypesInfo((Connection)connection);
                }
                return info;
            }
            if (name.equalsIgnoreCase("tables")) {
                final StoreSchemaData info = this.schemaDataByName.get("tables");
                if (info == null) {}
                return info;
            }
            throw new NucleusException("Attempt to get schema information for component " + name + " but this is not supported by RDBMSSchemaHandler");
        }
        else if (values.length == 1) {
            if (name.equalsIgnoreCase("foreign-keys") && values[0] instanceof Table) {
                return this.getRDBMSTableFKInfoForTable((Connection)connection, (Table)values[0]);
            }
            if (name.equalsIgnoreCase("primary-keys") && values[0] instanceof Table) {
                return this.getRDBMSTablePKInfoForTable((Connection)connection, (Table)values[0]);
            }
            if (name.equalsIgnoreCase("indices") && values[0] instanceof Table) {
                return this.getRDBMSTableIndexInfoForTable((Connection)connection, (Table)values[0]);
            }
            if (name.equalsIgnoreCase("columns") && values[0] instanceof Table) {
                return this.getRDBMSTableInfoForTable((Connection)connection, (Table)values[0]);
            }
            return this.getSchemaData(connection, name, null);
        }
        else {
            if (values.length != 2) {
                if (values.length == 3) {
                    if (name.equalsIgnoreCase("columns") && values[0] instanceof String && values[1] instanceof String && values[2] instanceof String) {
                        return this.getRDBMSTableInfoForTable((Connection)connection, (String)values[0], (String)values[1], (String)values[2]);
                    }
                    if (name.equalsIgnoreCase("indices") && values[0] instanceof String && values[1] instanceof String && values[2] instanceof String) {
                        return this.getRDBMSTableIndexInfoForTable((Connection)connection, (String)values[0], (String)values[1], (String)values[2]);
                    }
                    if (name.equalsIgnoreCase("primary-keys") && values[0] instanceof String && values[1] instanceof String && values[2] instanceof String) {
                        return this.getRDBMSTablePKInfoForTable((Connection)connection, (String)values[0], (String)values[1], (String)values[2]);
                    }
                    if (name.equalsIgnoreCase("foreign-keys") && values[0] instanceof String && values[1] instanceof String && values[2] instanceof String) {
                        return this.getRDBMSTableFKInfoForTable((Connection)connection, (String)values[0], (String)values[1], (String)values[2]);
                    }
                }
                throw new NucleusException("Attempt to get schema information for component " + name + " but this is not supported by RDBMSSchemaHandler");
            }
            if (name.equalsIgnoreCase("tables")) {
                return this.getRDBMSSchemaInfoForCatalogSchema((Connection)connection, (String)values[0], (String)values[1]);
            }
            if (name.equalsIgnoreCase("column") && values[0] instanceof Table && values[1] instanceof String) {
                return this.getRDBMSColumnInfoForColumn((Connection)connection, (Table)values[0], (String)values[1]);
            }
            return this.getSchemaData(connection, name, null);
        }
    }
    
    @Override
    public StoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    public String getTableType(final Connection conn, final Table table) throws SQLException {
        String tableType = null;
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        final String[] c = splitTableIdentifierName(dba.getCatalogSeparator(), table.getIdentifier().getIdentifierName());
        String catalogName = table.getCatalogName();
        String schemaName = table.getSchemaName();
        String tableName = table.getIdentifier().getIdentifierName();
        if (c[0] != null) {
            catalogName = c[0];
        }
        if (c[1] != null) {
            schemaName = c[1];
        }
        if (c[2] != null) {
            tableName = c[2];
        }
        catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalogName);
        schemaName = this.getIdentifierForUseWithDatabaseMetaData(schemaName);
        tableName = this.getIdentifierForUseWithDatabaseMetaData(tableName);
        try {
            final ResultSet rs = conn.getMetaData().getTables(catalogName, schemaName, tableName, null);
            try {
                final boolean insensitive = this.identifiersCaseInsensitive();
                while (rs.next()) {
                    if ((insensitive && tableName.equalsIgnoreCase(rs.getString(3))) || (!insensitive && tableName.equals(rs.getString(3)))) {
                        tableType = rs.getString(4).toUpperCase();
                        break;
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown finding table type using DatabaseMetaData.getTables()", sqle);
        }
        return tableType;
    }
    
    protected RDBMSTypesInfo getRDBMSTypesInfo(final Connection conn) {
        final RDBMSTypesInfo info = new RDBMSTypesInfo();
        try {
            if (conn == null) {
                return null;
            }
            final DatabaseMetaData dmd = conn.getMetaData();
            final ResultSet rs = dmd.getTypeInfo();
            try {
                final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
                while (rs.next()) {
                    final SQLTypeInfo sqlType = dba.newSQLTypeInfo(rs);
                    if (sqlType != null) {
                        final String key = "" + sqlType.getDataType();
                        JDBCTypeInfo jdbcType = (JDBCTypeInfo)info.getChild(key);
                        if (jdbcType == null) {
                            jdbcType = new JDBCTypeInfo(sqlType.getDataType());
                            jdbcType.addChild(sqlType);
                            info.addChild(jdbcType);
                        }
                        else {
                            jdbcType.addChild(sqlType);
                        }
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown retrieving type information from datastore", sqle);
        }
        this.schemaDataByName.put("types", info);
        return info;
    }
    
    protected RDBMSTableFKInfo getRDBMSTableFKInfoForTable(final Connection conn, final Table table) {
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        final String[] c = splitTableIdentifierName(dba.getCatalogSeparator(), table.getIdentifier().getIdentifierName());
        String catalogName = table.getCatalogName();
        String schemaName = table.getSchemaName();
        String tableName = table.getIdentifier().getIdentifierName();
        if (c[0] != null) {
            catalogName = c[0];
        }
        if (c[1] != null) {
            schemaName = c[1];
        }
        if (c[2] != null) {
            tableName = c[2];
        }
        catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalogName);
        schemaName = this.getIdentifierForUseWithDatabaseMetaData(schemaName);
        tableName = this.getIdentifierForUseWithDatabaseMetaData(tableName);
        return this.getRDBMSTableFKInfoForTable(conn, catalogName, schemaName, tableName);
    }
    
    protected RDBMSTableFKInfo getRDBMSTableFKInfoForTable(final Connection conn, final String catalogName, final String schemaName, final String tableName) {
        final RDBMSTableFKInfo info = new RDBMSTableFKInfo(catalogName, schemaName, tableName);
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        try {
            final ResultSet rs = conn.getMetaData().getImportedKeys(catalogName, schemaName, tableName);
            try {
                while (rs.next()) {
                    final ForeignKeyInfo fki = dba.newFKInfo(rs);
                    if (!info.getChildren().contains(fki)) {
                        info.addChild(fki);
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown while querying foreign keys for table=" + tableName, sqle);
        }
        return info;
    }
    
    protected RDBMSTablePKInfo getRDBMSTablePKInfoForTable(final Connection conn, final Table table) {
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        final String[] c = splitTableIdentifierName(dba.getCatalogSeparator(), table.getIdentifier().getIdentifierName());
        String catalogName = table.getCatalogName();
        String schemaName = table.getSchemaName();
        String tableName = table.getIdentifier().getIdentifierName();
        if (c[0] != null) {
            catalogName = c[0];
        }
        if (c[1] != null) {
            schemaName = c[1];
        }
        if (c[2] != null) {
            tableName = c[2];
        }
        catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalogName);
        schemaName = this.getIdentifierForUseWithDatabaseMetaData(schemaName);
        tableName = this.getIdentifierForUseWithDatabaseMetaData(tableName);
        return this.getRDBMSTablePKInfoForTable(conn, catalogName, schemaName, tableName);
    }
    
    protected RDBMSTablePKInfo getRDBMSTablePKInfoForTable(final Connection conn, final String catalogName, final String schemaName, final String tableName) {
        final RDBMSTablePKInfo info = new RDBMSTablePKInfo(catalogName, schemaName, tableName);
        try {
            final ResultSet rs = conn.getMetaData().getPrimaryKeys(catalogName, schemaName, tableName);
            try {
                while (rs.next()) {
                    final PrimaryKeyInfo pki = new PrimaryKeyInfo(rs);
                    if (!info.getChildren().contains(pki)) {
                        info.addChild(pki);
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown while querying primary keys for table=" + tableName, sqle);
        }
        return info;
    }
    
    protected RDBMSTableIndexInfo getRDBMSTableIndexInfoForTable(final Connection conn, final Table table) {
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        final String[] c = splitTableIdentifierName(dba.getCatalogSeparator(), table.getIdentifier().getIdentifierName());
        String catalogName = table.getCatalogName();
        String schemaName = table.getSchemaName();
        String tableName = table.getIdentifier().getIdentifierName();
        if (c[0] != null) {
            catalogName = c[0];
        }
        if (c[1] != null) {
            schemaName = c[1];
        }
        if (c[2] != null) {
            tableName = c[2];
        }
        catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalogName);
        schemaName = this.getIdentifierForUseWithDatabaseMetaData(schemaName);
        tableName = this.getIdentifierForUseWithDatabaseMetaData(tableName);
        return this.getRDBMSTableIndexInfoForTable(conn, catalogName, schemaName, tableName);
    }
    
    protected RDBMSTableIndexInfo getRDBMSTableIndexInfoForTable(final Connection conn, final String catalogName, final String schemaName, final String tableName) {
        final RDBMSTableIndexInfo info = new RDBMSTableIndexInfo(catalogName, schemaName, tableName);
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        try {
            String schemaNameTmp = schemaName;
            if (schemaName == null && this.storeMgr.getSchemaName() != null) {
                schemaNameTmp = this.storeMgr.getSchemaName();
                schemaNameTmp = this.getIdentifierForUseWithDatabaseMetaData(schemaNameTmp);
            }
            ResultSet rs = dba.getExistingIndexes(conn, catalogName, schemaNameTmp, tableName);
            if (rs == null) {
                rs = conn.getMetaData().getIndexInfo(catalogName, schemaName, tableName, false, true);
            }
            try {
                while (rs.next()) {
                    final IndexInfo idxInfo = new IndexInfo(rs);
                    if (!info.getChildren().contains(idxInfo)) {
                        info.addChild(idxInfo);
                    }
                }
            }
            finally {
                if (rs != null) {
                    final Statement st = rs.getStatement();
                    rs.close();
                    if (st != null) {
                        st.close();
                    }
                }
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown while querying indices for table=" + tableName, sqle);
        }
        return info;
    }
    
    protected RDBMSSchemaInfo getRDBMSSchemaInfoForCatalogSchema(final Connection conn, final String catalog, final String schema) {
        if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.omitDatabaseMetaDataGetColumns")) {
            return null;
        }
        final RDBMSSchemaInfo schemaInfo = new RDBMSSchemaInfo(catalog, schema);
        ResultSet rs = null;
        try {
            final String catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalog);
            final String schemaName = this.getIdentifierForUseWithDatabaseMetaData(schema);
            rs = this.storeMgr.getDatastoreAdapter().getColumns(conn, catalogName, schemaName, null, null);
            while (rs.next()) {
                String colCatalogName = rs.getString(1);
                String colSchemaName = rs.getString(2);
                final String colTableName = rs.getString(3);
                if (StringUtils.isWhitespace(colTableName)) {
                    throw new NucleusDataStoreException("Invalid 'null' table name identifier returned by database. Check with your JDBC driver vendor (ref:DatabaseMetaData.getColumns).");
                }
                if (rs.wasNull() || (colCatalogName != null && colCatalogName.length() < 1)) {
                    colCatalogName = null;
                }
                if (rs.wasNull() || (colSchemaName != null && colSchemaName.length() < 1)) {
                    colSchemaName = null;
                }
                final String tableKey = this.getTableKeyInRDBMSSchemaInfo(catalog, schema, colTableName);
                RDBMSTableInfo table = (RDBMSTableInfo)schemaInfo.getChild(tableKey);
                if (table == null) {
                    table = new RDBMSTableInfo(colCatalogName, colSchemaName, colTableName);
                    table.addProperty("table_key", tableKey);
                    schemaInfo.addChild(table);
                }
                final RDBMSColumnInfo col = this.storeMgr.getDatastoreAdapter().newRDBMSColumnInfo(rs);
                table.addChild(col);
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown obtaining schema column information from datastore", sqle);
        }
        finally {
            try {
                if (rs != null) {
                    final Statement stmt = rs.getStatement();
                    rs.close();
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            catch (SQLException sqle2) {
                throw new NucleusDataStoreException("Exception thrown closing results of DatabaseMetaData.getColumns()", sqle2);
            }
        }
        return schemaInfo;
    }
    
    protected RDBMSTableInfo getRDBMSTableInfoForTable(final Connection conn, final Table table) {
        final String[] c = splitTableIdentifierName(this.storeMgr.getDatastoreAdapter().getCatalogSeparator(), table.getIdentifier().getIdentifierName());
        String catalogName = table.getCatalogName();
        String schemaName = table.getSchemaName();
        String tableName = table.getIdentifier().getIdentifierName();
        if (c[0] != null) {
            catalogName = c[0];
        }
        if (c[1] != null) {
            schemaName = c[1];
        }
        if (c[2] != null) {
            tableName = c[2];
        }
        catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalogName);
        schemaName = this.getIdentifierForUseWithDatabaseMetaData(schemaName);
        tableName = this.getIdentifierForUseWithDatabaseMetaData(tableName);
        return this.getRDBMSTableInfoForTable(conn, catalogName, schemaName, tableName);
    }
    
    protected RDBMSTableInfo getRDBMSTableInfoForTable(final Connection conn, final String catalogName, final String schemaName, final String tableName) {
        RDBMSSchemaInfo info = (RDBMSSchemaInfo)this.getSchemaData(conn, "tables", null);
        if (info == null) {
            info = new RDBMSSchemaInfo(this.storeMgr.getCatalogName(), this.storeMgr.getSchemaName());
            this.schemaDataByName.put("tables", info);
        }
        final String tableKey = this.getTableKeyInRDBMSSchemaInfo(catalogName, schemaName, tableName);
        RDBMSTableInfo tableInfo = (RDBMSTableInfo)info.getChild(tableKey);
        if (tableInfo != null) {
            final long time = (long)tableInfo.getProperty("time");
            final long now = System.currentTimeMillis();
            if (now < time + 300000L) {
                return tableInfo;
            }
        }
        final boolean insensitiveIdentifiers = this.identifiersCaseInsensitive();
        final Collection tableNames = new HashSet();
        final Collection tables = this.storeMgr.getManagedTables(catalogName, schemaName);
        if (tables.size() > 0) {
            for (final Table tbl : tables) {
                tableNames.add(insensitiveIdentifiers ? tbl.getIdentifier().getIdentifierName().toLowerCase() : tbl.getIdentifier().getIdentifierName());
            }
        }
        tableNames.add(insensitiveIdentifiers ? tableName.toLowerCase() : tableName);
        this.refreshTableData(conn, catalogName, schemaName, tableNames);
        tableInfo = (RDBMSTableInfo)info.getChild(tableKey);
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            if (tableInfo == null || tableInfo.getNumberOfChildren() == 0) {
                NucleusLogger.DATASTORE_SCHEMA.info(RDBMSSchemaHandler.LOCALISER.msg("050030", tableName));
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.debug(RDBMSSchemaHandler.LOCALISER.msg("050032", tableName, "" + tableInfo.getNumberOfChildren()));
            }
        }
        return tableInfo;
    }
    
    protected RDBMSColumnInfo getRDBMSColumnInfoForColumn(final Connection conn, final Table table, final String columnName) {
        RDBMSColumnInfo colInfo = null;
        final RDBMSTableInfo tableInfo = this.getRDBMSTableInfoForTable(conn, table);
        if (tableInfo != null) {
            colInfo = (RDBMSColumnInfo)tableInfo.getChild(columnName);
            if (colInfo == null) {}
        }
        return colInfo;
    }
    
    private void refreshTableData(final Object connection, final String catalog, final String schema, final Collection tableNames) {
        if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.omitDatabaseMetaDataGetColumns")) {
            return;
        }
        if (tableNames == null || tableNames.size() == 0) {
            return;
        }
        RDBMSSchemaInfo info = (RDBMSSchemaInfo)this.getSchemaData(connection, "tables", null);
        if (info == null) {
            info = new RDBMSSchemaInfo(this.storeMgr.getCatalogName(), this.storeMgr.getSchemaName());
            this.schemaDataByName.put("tables", info);
        }
        final Long now = System.currentTimeMillis();
        ResultSet rs = null;
        final HashSet tablesProcessed = new HashSet();
        try {
            final Connection conn = (Connection)connection;
            final String catalogName = this.getIdentifierForUseWithDatabaseMetaData(catalog);
            final String schemaName = this.getIdentifierForUseWithDatabaseMetaData(schema);
            if (tableNames.size() == 1) {
                final String tableName = this.getIdentifierForUseWithDatabaseMetaData(tableNames.iterator().next());
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(RDBMSSchemaHandler.LOCALISER.msg("050028", tableName, catalogName, schemaName));
                }
                rs = this.storeMgr.getDatastoreAdapter().getColumns(conn, catalogName, schemaName, tableName, null);
            }
            else {
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(RDBMSSchemaHandler.LOCALISER.msg("050028", StringUtils.collectionToString(tableNames), catalogName, schemaName));
                }
                rs = this.storeMgr.getDatastoreAdapter().getColumns(conn, catalogName, schemaName, null, null);
            }
            final boolean insensitiveIdentifiers = this.identifiersCaseInsensitive();
            while (rs.next()) {
                String colCatalogName = rs.getString(1);
                String colSchemaName = rs.getString(2);
                final String colTableName = rs.getString(3);
                if (StringUtils.isWhitespace(colTableName)) {
                    throw new NucleusDataStoreException("Invalid 'null' table name identifier returned by database. Check with your JDBC driver vendor (ref:DatabaseMetaData.getColumns).");
                }
                if (rs.wasNull() || (colCatalogName != null && colCatalogName.length() < 1)) {
                    colCatalogName = null;
                }
                if (rs.wasNull() || (colSchemaName != null && colSchemaName.length() < 1)) {
                    colSchemaName = null;
                }
                String colTableNameToCheck = colTableName;
                if (insensitiveIdentifiers) {
                    colTableNameToCheck = colTableName.toLowerCase();
                }
                if (!tableNames.contains(colTableNameToCheck)) {
                    continue;
                }
                final String tableKey = this.getTableKeyInRDBMSSchemaInfo(catalog, schema, colTableName);
                RDBMSTableInfo table = (RDBMSTableInfo)info.getChild(tableKey);
                if (tablesProcessed.add(tableKey)) {
                    if (table == null) {
                        table = new RDBMSTableInfo(colCatalogName, colSchemaName, colTableName);
                        table.addProperty("table_key", tableKey);
                        info.addChild(table);
                    }
                    else {
                        table.clearChildren();
                    }
                    table.addProperty("time", now);
                }
                final RDBMSColumnInfo col = this.storeMgr.getDatastoreAdapter().newRDBMSColumnInfo(rs);
                table.addChild(col);
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown obtaining schema column information from datastore", sqle);
        }
        finally {
            try {
                if (rs != null) {
                    final Statement stmt = rs.getStatement();
                    rs.close();
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            catch (SQLException sqle2) {
                throw new NucleusDataStoreException("Exception thrown closing results of DatabaseMetaData.getColumns()", sqle2);
            }
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(RDBMSSchemaHandler.LOCALISER.msg("050029", catalog, schema, "" + tablesProcessed.size(), "" + (System.currentTimeMillis() - now)));
        }
    }
    
    private String getTableKeyInRDBMSSchemaInfo(final String catalog, final String schema, final String table) {
        final DatastoreIdentifier fullyQualifiedTableName = this.storeMgr.getIdentifierFactory().newTableIdentifier(table);
        fullyQualifiedTableName.setCatalogName(catalog);
        fullyQualifiedTableName.setSchemaName(schema);
        return fullyQualifiedTableName.getFullyQualifiedName(true);
    }
    
    private static String[] splitTableIdentifierName(final String separator, final String name) {
        final String[] result = new String[3];
        final int p = name.indexOf(separator);
        if (p < 0) {
            result[2] = name;
        }
        else {
            final int p2 = name.indexOf(separator, p + separator.length());
            if (p2 < 0) {
                result[1] = name.substring(0, p);
                result[2] = name.substring(p + separator.length());
            }
            else {
                result[0] = name.substring(0, p);
                result[1] = name.substring(p + separator.length(), p2);
                result[2] = name.substring(p2 + separator.length());
            }
        }
        if (result[1] != null && result[1].length() < 1) {
            result[1] = null;
        }
        if (result[0] != null && result[0].length() < 1) {
            result[0] = null;
        }
        return result;
    }
    
    private String getIdentifierForUseWithDatabaseMetaData(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.replace(this.storeMgr.getDatastoreAdapter().getIdentifierQuoteString(), "");
    }
    
    private boolean identifiersCaseInsensitive() {
        final DatastoreAdapter dba = this.storeMgr.getDatastoreAdapter();
        return !dba.supportsOption("MixedCaseSensitiveIdentifiers") && !dba.supportsOption("MixedCaseQuotedSensitiveIdentifiers");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
