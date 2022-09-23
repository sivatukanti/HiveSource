// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.util.Comparator;
import org.apache.hive.service.cli.thrift.TGetInfoReq;
import org.apache.hive.service.cli.thrift.TGetInfoType;
import java.util.List;
import org.apache.hive.service.cli.thrift.TGetTypeInfoResp;
import org.apache.hive.service.cli.thrift.TGetTypeInfoReq;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hive.service.cli.thrift.TGetTablesResp;
import org.apache.hive.service.cli.thrift.TGetTablesReq;
import org.apache.hive.service.cli.thrift.TGetTableTypesResp;
import org.apache.hive.service.cli.thrift.TGetTableTypesReq;
import org.apache.hive.service.cli.thrift.TGetSchemasResp;
import org.apache.hive.service.cli.thrift.TGetSchemasReq;
import java.sql.RowIdLifetime;
import java.util.Arrays;
import org.apache.hive.service.cli.thrift.TGetFunctionsResp;
import org.apache.hive.service.cli.thrift.TGetFunctionsReq;
import java.util.jar.Attributes;
import org.apache.hive.service.cli.thrift.TGetInfoResp;
import org.apache.hive.service.cli.GetInfoType;
import org.apache.hive.service.cli.thrift.TGetColumnsResp;
import org.apache.hive.service.cli.thrift.TGetColumnsReq;
import org.apache.hive.service.cli.thrift.TGetCatalogsResp;
import java.sql.Connection;
import org.apache.thrift.TException;
import org.apache.hive.service.cli.thrift.TGetCatalogsReq;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import org.apache.hive.service.cli.thrift.TCLIService;
import java.sql.DatabaseMetaData;

public class HiveDatabaseMetaData implements DatabaseMetaData
{
    private final HiveConnection connection;
    private final TCLIService.Iface client;
    private final TSessionHandle sessHandle;
    private static final String CATALOG_SEPARATOR = ".";
    private static final char SEARCH_STRING_ESCAPE = '\\';
    private static final int maxColumnNameLength = 128;
    private String dbVersion;
    
    public HiveDatabaseMetaData(final HiveConnection connection, final TCLIService.Iface client, final TSessionHandle sessHandle) {
        this.dbVersion = null;
        this.connection = connection;
        this.client = client;
        this.sessHandle = sessHandle;
    }
    
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean deletesAreDetected(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }
    
    @Override
    public String getCatalogTerm() throws SQLException {
        return "instance";
    }
    
    @Override
    public ResultSet getCatalogs() throws SQLException {
        TGetCatalogsResp catalogResp;
        try {
            catalogResp = this.client.GetCatalogs(new TGetCatalogsReq(this.sessHandle));
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(catalogResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(catalogResp.getOperationHandle()).build();
    }
    
    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    private String convertPattern(final String pattern) {
        if (pattern == null) {
            return ".*";
        }
        final StringBuilder result = new StringBuilder(pattern.length());
        boolean escaped = false;
        for (int i = 0, len = pattern.length(); i < len; ++i) {
            final char c = pattern.charAt(i);
            if (escaped) {
                if (c != '\\') {
                    escaped = false;
                }
                result.append(c);
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == '%') {
                result.append(".*");
            }
            else if (c == '_') {
                result.append('.');
            }
            else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
    
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        final TGetColumnsReq colReq = new TGetColumnsReq();
        colReq.setSessionHandle(this.sessHandle);
        colReq.setCatalogName(catalog);
        colReq.setSchemaName(schemaPattern);
        colReq.setTableName(tableNamePattern);
        colReq.setColumnName(columnNamePattern);
        TGetColumnsResp colResp;
        try {
            colResp = this.client.GetColumns(colReq);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(colResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(colResp.getOperationHandle()).build();
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
    
    @Override
    public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema, final String primaryTable, final String foreignCatalog, final String foreignSchema, final String foreignTable) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return Utils.getVersionPart(this.getDatabaseProductVersion(), 0);
    }
    
    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return Utils.getVersionPart(this.getDatabaseProductVersion(), 1);
    }
    
    @Override
    public String getDatabaseProductName() throws SQLException {
        final TGetInfoResp resp = this.getServerInfo(GetInfoType.CLI_DBMS_NAME.toTGetInfoType());
        return resp.getInfoValue().getStringValue();
    }
    
    @Override
    public String getDatabaseProductVersion() throws SQLException {
        if (this.dbVersion != null) {
            return this.dbVersion;
        }
        final TGetInfoResp resp = this.getServerInfo(GetInfoType.CLI_DBMS_VER.toTGetInfoType());
        return this.dbVersion = resp.getInfoValue().getStringValue();
    }
    
    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return 0;
    }
    
    @Override
    public int getDriverMajorVersion() {
        return HiveDriver.getMajorDriverVersion();
    }
    
    @Override
    public int getDriverMinorVersion() {
        return HiveDriver.getMinorDriverVersion();
    }
    
    @Override
    public String getDriverName() throws SQLException {
        return HiveDriver.fetchManifestAttribute(Attributes.Name.IMPLEMENTATION_TITLE);
    }
    
    @Override
    public String getDriverVersion() throws SQLException {
        return HiveDriver.fetchManifestAttribute(Attributes.Name.IMPLEMENTATION_VERSION);
    }
    
    @Override
    public ResultSet getExportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "";
    }
    
    @Override
    public ResultSet getFunctionColumns(final String arg0, final String arg1, final String arg2, final String arg3) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getFunctions(final String catalogName, final String schemaPattern, final String functionNamePattern) throws SQLException {
        final TGetFunctionsReq getFunctionsReq = new TGetFunctionsReq();
        getFunctionsReq.setSessionHandle(this.sessHandle);
        getFunctionsReq.setCatalogName(catalogName);
        getFunctionsReq.setSchemaName(schemaPattern);
        getFunctionsReq.setFunctionName(functionNamePattern);
        TGetFunctionsResp funcResp;
        try {
            funcResp = this.client.GetFunctions(getFunctionsReq);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(funcResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(funcResp.getOperationHandle()).build();
    }
    
    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return " ";
    }
    
    @Override
    public ResultSet getImportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setEmptyResultSet(true).setSchema(Arrays.asList("PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"), Arrays.asList("STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "SMALLINT", "SMALLINT", "SMALLINT", "STRING", "STRING", "STRING")).build();
    }
    
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schema, final String table, final boolean unique, final boolean approximate) throws SQLException {
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setEmptyResultSet(true).setSchema(Arrays.asList("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION"), Arrays.asList("STRING", "STRING", "STRING", "BOOLEAN", "STRING", "STRING", "SHORT", "SHORT", "STRING", "STRING", "INT", "INT", "STRING")).build();
    }
    
    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 3;
    }
    
    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 128;
    }
    
    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxColumnsInTable() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxConnections() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxCursorNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxIndexLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxRowSize() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxStatementLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxStatements() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxTableNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxTablesInSelect() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxUserNameLength() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getNumericFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String table) throws SQLException {
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setEmptyResultSet(true).setSchema(Arrays.asList("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"), Arrays.asList("STRING", "STRING", "STRING", "STRING", "INT", "STRING")).build();
    }
    
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern, final String procedureNamePattern, final String columnNamePattern) throws SQLException {
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setEmptyResultSet(true).setSchema(Arrays.asList("PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SPECIFIC_NAME"), Arrays.asList("STRING", "STRING", "STRING", "STRING", "SMALLINT", "INT", "STRING", "INT", "INT", "SMALLINT", "SMALLINT", "SMALLINT", "STRING", "STRING", "INT", "INT", "INT", "INT", "STRING", "STRING")).build();
    }
    
    @Override
    public String getProcedureTerm() throws SQLException {
        return new String("UDF");
    }
    
    @Override
    public ResultSet getProcedures(final String catalog, final String schemaPattern, final String procedureNamePattern) throws SQLException {
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setEmptyResultSet(true).setSchema(Arrays.asList("PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "RESERVERD", "RESERVERD", "RESERVERD", "REMARKS", "PROCEDURE_TYPE", "SPECIFIC_NAME"), Arrays.asList("STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "SMALLINT", "STRING")).build();
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getSQLKeywords() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getSQLStateType() throws SQLException {
        return 2;
    }
    
    @Override
    public String getSchemaTerm() throws SQLException {
        return "database";
    }
    
    @Override
    public ResultSet getSchemas() throws SQLException {
        return this.getSchemas(null, null);
    }
    
    @Override
    public ResultSet getSchemas(final String catalog, String schemaPattern) throws SQLException {
        final TGetSchemasReq schemaReq = new TGetSchemasReq();
        schemaReq.setSessionHandle(this.sessHandle);
        if (catalog != null) {
            schemaReq.setCatalogName(catalog);
        }
        if (schemaPattern == null) {
            schemaPattern = "%";
        }
        schemaReq.setSchemaName(schemaPattern);
        TGetSchemasResp schemaResp;
        try {
            schemaResp = this.client.GetSchemas(schemaReq);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(schemaResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(schemaResp.getOperationHandle()).build();
    }
    
    @Override
    public String getSearchStringEscape() throws SQLException {
        return String.valueOf('\\');
    }
    
    @Override
    public String getStringFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getSystemFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getTableTypes() throws SQLException {
        TGetTableTypesResp tableTypeResp;
        try {
            tableTypeResp = this.client.GetTableTypes(new TGetTableTypesReq(this.sessHandle));
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(tableTypeResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(tableTypeResp.getOperationHandle()).build();
    }
    
    @Override
    public ResultSet getTables(final String catalog, String schemaPattern, final String tableNamePattern, final String[] types) throws SQLException {
        if (schemaPattern == null) {
            schemaPattern = "%";
        }
        final TGetTablesReq getTableReq = new TGetTablesReq(this.sessHandle);
        getTableReq.setTableName(tableNamePattern);
        if (types != null) {
            getTableReq.setTableTypes(Arrays.asList(types));
        }
        if (schemaPattern != null) {
            getTableReq.setSchemaName(schemaPattern);
        }
        TGetTablesResp getTableResp;
        try {
            getTableResp = this.client.GetTables(getTableReq);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(getTableResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(getTableResp.getOperationHandle()).build();
    }
    
    public static String toJdbcTableType(final String hivetabletype) {
        if (hivetabletype == null) {
            return null;
        }
        if (hivetabletype.equals(TableType.MANAGED_TABLE.toString())) {
            return "TABLE";
        }
        if (hivetabletype.equals(TableType.VIRTUAL_VIEW.toString())) {
            return "VIEW";
        }
        if (hivetabletype.equals(TableType.EXTERNAL_TABLE.toString())) {
            return "EXTERNAL TABLE";
        }
        return hivetabletype;
    }
    
    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        final TGetTypeInfoReq getTypeInfoReq = new TGetTypeInfoReq();
        getTypeInfoReq.setSessionHandle(this.sessHandle);
        TGetTypeInfoResp getTypeInfoResp;
        try {
            getTypeInfoResp = this.client.GetTypeInfo(getTypeInfoReq);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(getTypeInfoResp.getStatus());
        return new HiveQueryResultSet.Builder(this.connection).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(getTypeInfoResp.getOperationHandle()).build();
    }
    
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) throws SQLException {
        return new HiveMetaDataResultSet(Arrays.asList("TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "CLASS_NAME", "DATA_TYPE", "REMARKS", "BASE_TYPE"), Arrays.asList("STRING", "STRING", "STRING", "STRING", "INT", "STRING", "INT"), null) {
            @Override
            public boolean next() throws SQLException {
                return false;
            }
            
            @Override
            public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
                throw new SQLException("Method not supported");
            }
            
            @Override
            public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
                throw new SQLException("Method not supported");
            }
        };
    }
    
    @Override
    public String getURL() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getUserName() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean insertsAreDetected(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isCatalogAtStart() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean othersDeletesAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean othersInsertsAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean othersUpdatesAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean ownDeletesAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean ownInsertsAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean ownUpdatesAreVisible(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsConvert() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsConvert(final int fromType, final int toType) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsNamedParameters() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsResultSetHoldability(final int holdability) throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsResultSetType(final int type) throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsStatementPooling() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsTransactionIsolationLevel(final int level) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsUnion() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true;
    }
    
    @Override
    public boolean updatesAreDetected(final int type) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean usesLocalFiles() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    public static void main(final String[] args) throws SQLException {
        final HiveDatabaseMetaData meta = new HiveDatabaseMetaData(null, null, null);
        System.out.println("DriverName: " + meta.getDriverName());
        System.out.println("DriverVersion: " + meta.getDriverVersion());
    }
    
    private TGetInfoResp getServerInfo(final TGetInfoType type) throws SQLException {
        final TGetInfoReq req = new TGetInfoReq(this.sessHandle, type);
        TGetInfoResp resp;
        try {
            resp = this.client.GetInfo(req);
        }
        catch (TException e) {
            throw new SQLException(e.getMessage(), "08S01", e);
        }
        Utils.verifySuccess(resp.getStatus());
        return resp;
    }
    
    private class GetColumnsComparator implements Comparator<JdbcColumn>
    {
        @Override
        public int compare(final JdbcColumn o1, final JdbcColumn o2) {
            final int compareName = o1.getTableName().compareTo(o2.getTableName());
            if (compareName != 0) {
                return compareName;
            }
            if (o1.getOrdinalPos() > o2.getOrdinalPos()) {
                return 1;
            }
            if (o1.getOrdinalPos() < o2.getOrdinalPos()) {
                return -1;
            }
            return 0;
        }
    }
    
    private class GetTablesComparator implements Comparator<JdbcTable>
    {
        @Override
        public int compare(final JdbcTable o1, final JdbcTable o2) {
            final int compareType = o1.getType().compareTo(o2.getType());
            if (compareType == 0) {
                return o1.getTableName().compareTo(o2.getTableName());
            }
            return compareType;
        }
    }
}
