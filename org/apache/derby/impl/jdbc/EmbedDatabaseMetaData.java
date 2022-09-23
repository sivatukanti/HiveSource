// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.security.AccessController;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import java.sql.Connection;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.util.InterruptStatus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.impl.sql.execute.GenericConstantActionFactory;
import java.security.PrivilegedAction;
import java.sql.DatabaseMetaData;

public class EmbedDatabaseMetaData extends ConnectionChild implements DatabaseMetaData, PrivilegedAction
{
    private static final int ILLEGAL_UDT_TYPE = 0;
    private final String url;
    private GenericConstantActionFactory constantActionFactory;
    private static Properties queryDescriptions;
    private static Properties queryDescriptions_net;
    
    public EmbedDatabaseMetaData(final EmbedConnection embedConnection, final String url) throws SQLException {
        super(embedConnection);
        this.url = url;
    }
    
    private Properties getQueryDescriptions(final boolean b) {
        final Properties properties = b ? EmbedDatabaseMetaData.queryDescriptions_net : EmbedDatabaseMetaData.queryDescriptions;
        if (properties != null) {
            return properties;
        }
        this.loadQueryDescriptions();
        return b ? EmbedDatabaseMetaData.queryDescriptions_net : EmbedDatabaseMetaData.queryDescriptions;
    }
    
    private void PBloadQueryDescriptions() {
        final String[] array = { "metadata.properties", "/org/apache/derby/impl/sql/catalog/metadata_net.properties" };
        final Properties[] array2 = new Properties[array.length];
        for (int i = 0; i < array.length; ++i) {
            try {
                array2[i] = new Properties();
                final InputStream resourceAsStream = this.getClass().getResourceAsStream(array[i]);
                array2[i].load(resourceAsStream);
                resourceAsStream.close();
            }
            catch (IOException ex) {}
        }
        EmbedDatabaseMetaData.queryDescriptions = array2[0];
        EmbedDatabaseMetaData.queryDescriptions_net = array2[1];
    }
    
    public boolean allProceduresAreCallable() {
        return true;
    }
    
    public boolean allTablesAreSelectable() {
        return true;
    }
    
    public final String getURL() {
        if (this.url == null) {
            return this.url;
        }
        final int index = this.url.indexOf(59);
        if (index == -1) {
            return this.url;
        }
        return this.url.substring(0, index);
    }
    
    public String getUserName() {
        return this.getEmbedConnection().getTR().getUserName();
    }
    
    public boolean isReadOnly() {
        return this.getLanguageConnectionContext().getDatabase().isReadOnly();
    }
    
    public boolean nullsAreSortedHigh() {
        return true;
    }
    
    public boolean nullsAreSortedLow() {
        return false;
    }
    
    public boolean nullsAreSortedAtStart() {
        return false;
    }
    
    public boolean nullsAreSortedAtEnd() {
        return false;
    }
    
    public String getDatabaseProductName() {
        return Monitor.getMonitor().getEngineVersion().getProductName();
    }
    
    public String getDatabaseProductVersion() {
        return Monitor.getMonitor().getEngineVersion().getVersionBuildString(true);
    }
    
    public String getDriverName() {
        return "Apache Derby Embedded JDBC Driver";
    }
    
    public String getDriverVersion() {
        return this.getDatabaseProductVersion();
    }
    
    public int getDriverMajorVersion() {
        return this.getEmbedConnection().getLocalDriver().getMajorVersion();
    }
    
    public int getDriverMinorVersion() {
        return this.getEmbedConnection().getLocalDriver().getMinorVersion();
    }
    
    public boolean usesLocalFiles() {
        return true;
    }
    
    public boolean usesLocalFilePerTable() {
        return true;
    }
    
    public boolean supportsMixedCaseIdentifiers() {
        return false;
    }
    
    public boolean storesUpperCaseIdentifiers() {
        return true;
    }
    
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }
    
    public boolean storesMixedCaseIdentifiers() {
        return false;
    }
    
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return true;
    }
    
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean storesMixedCaseQuotedIdentifiers() {
        return true;
    }
    
    public String getIdentifierQuoteString() {
        return "\"";
    }
    
    public String getSQLKeywords() {
        return "ALIAS,BIGINT,BOOLEAN,CALL,CLASS,COPY,DB2J_DEBUG,EXECUTE,EXPLAIN,FILE,FILTER,GETCURRENTCONNECTION,INDEX,INSTANCEOF,METHOD,NEW,OFF,PROPERTIES,RECOMPILE,RENAME,RUNTIMESTATISTICS,STATEMENT,STATISTICS,TIMING,WAIT";
    }
    
    public String getNumericFunctions() {
        return "ABS,ACOS,ASIN,ATAN,ATAN2,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MOD,PI,RADIANS,RAND,SIGN,SIN,SQRT,TAN";
    }
    
    public String getStringFunctions() {
        return "CONCAT,LENGTH,LCASE,LOCATE,LTRIM,RTRIM,SUBSTRING,UCASE";
    }
    
    public String getSystemFunctions() {
        return "USER";
    }
    
    public String getTimeDateFunctions() {
        return "CURDATE,CURTIME,HOUR,MINUTE,MONTH,SECOND,TIMESTAMPADD,TIMESTAMPDIFF,YEAR";
    }
    
    public String getSearchStringEscape() {
        return "";
    }
    
    public String getExtraNameCharacters() {
        return "";
    }
    
    public boolean supportsAlterTableWithAddColumn() {
        return true;
    }
    
    public boolean supportsAlterTableWithDropColumn() {
        return true;
    }
    
    public boolean supportsColumnAliasing() {
        return true;
    }
    
    public boolean nullPlusNonNullIsNull() {
        return true;
    }
    
    public boolean supportsConvert() {
        return false;
    }
    
    public boolean supportsConvert(final int n, final int n2) {
        return false;
    }
    
    public boolean supportsTableCorrelationNames() {
        return true;
    }
    
    public boolean supportsDifferentTableCorrelationNames() {
        return true;
    }
    
    public boolean supportsExpressionsInOrderBy() {
        return true;
    }
    
    public boolean supportsOrderByUnrelated() {
        return false;
    }
    
    public boolean supportsGroupBy() {
        return true;
    }
    
    public boolean supportsGroupByUnrelated() {
        return true;
    }
    
    public boolean supportsGroupByBeyondSelect() {
        return true;
    }
    
    public boolean supportsLikeEscapeClause() {
        return true;
    }
    
    public boolean supportsMultipleResultSets() {
        return true;
    }
    
    public boolean supportsMultipleTransactions() {
        return true;
    }
    
    public boolean supportsNonNullableColumns() {
        return true;
    }
    
    public boolean supportsMinimumSQLGrammar() {
        return true;
    }
    
    public boolean supportsCoreSQLGrammar() {
        return false;
    }
    
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }
    
    public boolean supportsANSI92EntryLevelSQL() {
        return true;
    }
    
    public boolean supportsANSI92IntermediateSQL() {
        return false;
    }
    
    public boolean supportsANSI92FullSQL() {
        return false;
    }
    
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }
    
    public boolean supportsOuterJoins() {
        return true;
    }
    
    public boolean supportsFullOuterJoins() {
        return false;
    }
    
    public boolean supportsLimitedOuterJoins() {
        return true;
    }
    
    public String getSchemaTerm() {
        return "SCHEMA";
    }
    
    public String getProcedureTerm() {
        return "PROCEDURE";
    }
    
    public String getCatalogTerm() {
        return "CATALOG";
    }
    
    public boolean isCatalogAtStart() {
        return false;
    }
    
    public String getCatalogSeparator() {
        return "";
    }
    
    public boolean supportsSchemasInDataManipulation() {
        return true;
    }
    
    public boolean supportsSchemasInProcedureCalls() {
        return true;
    }
    
    public boolean supportsSchemasInTableDefinitions() {
        return true;
    }
    
    public boolean supportsSchemasInIndexDefinitions() {
        return true;
    }
    
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return true;
    }
    
    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }
    
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }
    
    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }
    
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }
    
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }
    
    public boolean supportsPositionedDelete() {
        return true;
    }
    
    public boolean supportsPositionedUpdate() {
        return true;
    }
    
    public boolean supportsRefCursors() {
        return false;
    }
    
    public boolean supportsSelectForUpdate() {
        return true;
    }
    
    public boolean supportsStoredProcedures() {
        return true;
    }
    
    public boolean supportsSubqueriesInComparisons() {
        return true;
    }
    
    public boolean supportsSubqueriesInExists() {
        return true;
    }
    
    public boolean supportsSubqueriesInIns() {
        return true;
    }
    
    public boolean supportsSubqueriesInQuantifieds() {
        return true;
    }
    
    public boolean supportsCorrelatedSubqueries() {
        return true;
    }
    
    public boolean supportsUnion() {
        return true;
    }
    
    public boolean supportsUnionAll() {
        return true;
    }
    
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }
    
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }
    
    public boolean supportsOpenStatementsAcrossCommit() {
        return true;
    }
    
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }
    
    public int getMaxBinaryLiteralLength() {
        return 0;
    }
    
    public int getMaxCharLiteralLength() {
        return 0;
    }
    
    public int getMaxColumnNameLength() {
        return 128;
    }
    
    public int getMaxColumnsInGroupBy() {
        return 0;
    }
    
    public int getMaxColumnsInIndex() {
        return 0;
    }
    
    public int getMaxColumnsInOrderBy() {
        return 0;
    }
    
    public int getMaxColumnsInSelect() {
        return 0;
    }
    
    public int getMaxColumnsInTable() {
        return 0;
    }
    
    public int getMaxConnections() {
        return 0;
    }
    
    public int getMaxCursorNameLength() {
        return 128;
    }
    
    public int getMaxIndexLength() {
        return 0;
    }
    
    public long getMaxLogicalLobSize() {
        return 0L;
    }
    
    public int getMaxSchemaNameLength() {
        return 128;
    }
    
    public int getMaxProcedureNameLength() {
        return 128;
    }
    
    public int getMaxCatalogNameLength() {
        return 0;
    }
    
    public int getMaxRowSize() {
        return 0;
    }
    
    public boolean doesMaxRowSizeIncludeBlobs() {
        return true;
    }
    
    public int getMaxStatementLength() {
        return 0;
    }
    
    public int getMaxStatements() {
        return 0;
    }
    
    public int getMaxTableNameLength() {
        return 128;
    }
    
    public int getMaxTablesInSelect() {
        return 0;
    }
    
    public int getMaxUserNameLength() {
        return 128;
    }
    
    public int getDefaultTransactionIsolation() {
        return 2;
    }
    
    public boolean supportsTransactions() {
        return true;
    }
    
    public boolean supportsTransactionIsolationLevel(final int n) {
        return n == 8 || n == 4 || n == 2 || n == 1;
    }
    
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return true;
    }
    
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }
    
    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }
    
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }
    
    public ResultSet getProcedures(final String s, final String s2, final String s3) throws SQLException {
        return this.doGetProcs(s, s2, s3, "getProcedures40");
    }
    
    public ResultSet getProceduresForODBC(final String s, final String s2, final String s3) throws SQLException {
        return this.doGetProcs(s, s2, s3, "odbc_getProcedures");
    }
    
    public ResultSet getFunctions(final String s, final String s2, final String s3) throws SQLException {
        return this.doGetProcs(s, s2, s3, "getFunctions");
    }
    
    private ResultSet doGetProcs(final String s, final String s2, final String s3, final String s4) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery(s4);
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, swapNull(s3));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getProcedureColumns(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.doGetProcCols(s, s2, s3, s4, "getProcedureColumns40");
    }
    
    public ResultSet getProcedureColumnsForODBC(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.doGetProcCols(s, s2, s3, s4, "odbc_getProcedureColumns");
    }
    
    public ResultSet getFunctionColumns(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.doGetProcCols(s, s2, s3, s4, "getFunctionColumns");
    }
    
    private ResultSet doGetProcCols(final String s, final String s2, final String s3, final String s4, final String s5) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery(s5);
        preparedQuery.setString(1, swapNull(s2));
        preparedQuery.setString(2, swapNull(s3));
        preparedQuery.setString(3, swapNull(s4));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getTables(final String s, final String s2, final String s3, String[] array) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery("getTables");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, swapNull(s3));
        if (array == null) {
            array = new String[] { "TABLE", "VIEW", "SYNONYM", "SYSTEM TABLE" };
        }
        final String[] array2 = new String[4];
        for (int i = 0; i < 4; ++i) {
            array2[i] = null;
        }
        for (int j = 0; j < array.length; ++j) {
            if ("TABLE".equals(array[j])) {
                array2[0] = "T";
            }
            else if ("VIEW".equals(array[j])) {
                array2[1] = "V";
            }
            else if ("SYNONYM".equals(array[j])) {
                array2[2] = "A";
            }
            else if ("SYSTEM TABLE".equals(array[j]) || "SYSTEM_TABLE".equals(array[j])) {
                array2[3] = "S";
            }
        }
        for (int k = 0; k < 4; ++k) {
            if (array2[k] == null) {
                preparedQuery.setNull(k + 4, 1);
            }
            else {
                preparedQuery.setString(k + 4, array2[k]);
            }
        }
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getSchemas() throws SQLException {
        return this.getSchemas(null, null);
    }
    
    public ResultSet getCatalogs() throws SQLException {
        return this.getSimpleQuery("getCatalogs");
    }
    
    public ResultSet getTableTypes() throws SQLException {
        return this.getSimpleQuery("getTableTypes");
    }
    
    public ResultSet getColumns(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.doGetCols(s, s2, s3, s4, "getColumns");
    }
    
    public ResultSet getColumnsForODBC(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.doGetCols(s, s2, s3, s4, "odbc_getColumns");
    }
    
    private ResultSet doGetCols(final String s, final String s2, final String s3, final String s4, final String s5) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery(s5);
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, swapNull(s3));
        preparedQuery.setString(4, swapNull(s4));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getColumnPrivileges(final String s, final String s2, final String s3, final String s4) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getColumnPrivileges");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        preparedQuery.setString(4, swapNull(s4));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getTablePrivileges(final String s, final String s2, final String s3) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery("getTablePrivileges");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, swapNull(s3));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getBestRowIdentifier(final String s, final String s2, final String s3, final int n, final boolean b) throws SQLException {
        return this.doGetBestRowId(s, s2, s3, n, b, "");
    }
    
    public ResultSet getBestRowIdentifierForODBC(final String s, final String s2, final String s3, final int n, final boolean b) throws SQLException {
        return this.doGetBestRowId(s, s2, s3, n, b, "odbc_");
    }
    
    private ResultSet doGetBestRowId(String s, String s2, final String s3, final int n, final boolean b, final String s4) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        int n2 = 0;
        if (b) {
            n2 = 1;
        }
        if (s == null) {
            s = "%";
        }
        if (s2 == null) {
            s2 = "%";
        }
        if (n < 0 || n > 2) {
            return this.getPreparedQuery("getBestRowIdentifierEmpty").executeQuery();
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getBestRowIdentifierPrimaryKey");
        preparedQuery.setString(1, s);
        preparedQuery.setString(2, s2);
        preparedQuery.setString(3, s3);
        final ResultSet executeQuery = preparedQuery.executeQuery();
        final boolean next = executeQuery.next();
        String s5 = "";
        if (next) {
            s5 = executeQuery.getString(1);
        }
        executeQuery.close();
        preparedQuery.close();
        if (next) {
            final PreparedStatement preparedQuery2 = this.getPreparedQuery(s4 + "getBestRowIdentifierPrimaryKeyColumns");
            preparedQuery2.setString(1, s5);
            preparedQuery2.setString(2, s5);
            return preparedQuery2.executeQuery();
        }
        final PreparedStatement preparedQuery3 = this.getPreparedQuery("getBestRowIdentifierUniqueConstraint");
        preparedQuery3.setString(1, s);
        preparedQuery3.setString(2, s2);
        preparedQuery3.setString(3, s3);
        final ResultSet executeQuery2 = preparedQuery3.executeQuery();
        final boolean next2 = executeQuery2.next();
        if (next2) {
            s5 = executeQuery2.getString(1);
        }
        executeQuery2.close();
        preparedQuery3.close();
        if (next2) {
            final PreparedStatement preparedQuery4 = this.getPreparedQuery(s4 + "getBestRowIdentifierUniqueKeyColumns");
            preparedQuery4.setString(1, s5);
            preparedQuery4.setString(2, s5);
            preparedQuery4.setInt(3, n2);
            return preparedQuery4.executeQuery();
        }
        final PreparedStatement preparedQuery5 = this.getPreparedQuery("getBestRowIdentifierUniqueIndex");
        preparedQuery5.setString(1, s);
        preparedQuery5.setString(2, s2);
        preparedQuery5.setString(3, s3);
        final ResultSet executeQuery3 = preparedQuery5.executeQuery();
        final boolean next3 = executeQuery3.next();
        long long1 = 0L;
        if (next3) {
            long1 = executeQuery3.getLong(1);
        }
        executeQuery3.close();
        preparedQuery5.close();
        if (next3) {
            final PreparedStatement preparedQuery6 = this.getPreparedQuery(s4 + "getBestRowIdentifierUniqueIndexColumns");
            preparedQuery6.setLong(1, long1);
            preparedQuery6.setInt(2, n2);
            return preparedQuery6.executeQuery();
        }
        final PreparedStatement preparedQuery7 = this.getPreparedQuery(s4 + "getBestRowIdentifierAllColumns");
        preparedQuery7.setString(1, s);
        preparedQuery7.setString(2, s2);
        preparedQuery7.setString(3, s3);
        preparedQuery7.setInt(4, n);
        preparedQuery7.setInt(5, n2);
        return preparedQuery7.executeQuery();
    }
    
    public ResultSet getVersionColumns(final String s, final String s2, final String s3) throws SQLException {
        return this.doGetVersionCols(s, s2, s3, "getVersionColumns");
    }
    
    public ResultSet getVersionColumnsForODBC(final String s, final String s2, final String s3) throws SQLException {
        return this.doGetVersionCols(s, s2, s3, "odbc_getVersionColumns");
    }
    
    private ResultSet doGetVersionCols(final String s, final String s2, final String s3, final String s4) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery(s4);
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        return preparedQuery.executeQuery();
    }
    
    private boolean notInSoftUpgradeMode() throws SQLException {
        if (this.getEmbedConnection().isClosed()) {
            throw Util.noCurrentConnection();
        }
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        boolean checkVersion;
        try {
            checkVersion = languageConnectionContext.getDataDictionary().checkVersion(-1, null);
            InterruptStatus.restoreIntrFlagIfSeen();
        }
        catch (Throwable t) {
            throw this.handleException(t);
        }
        return checkVersion;
    }
    
    public ResultSet getPrimaryKeys(final String s, final String s2, final String s3) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery("getPrimaryKeys");
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getImportedKeys(final String s, final String s2, final String s3) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getImportedKeys");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getExportedKeys(final String s, final String s2, final String s3) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getCrossReference");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        preparedQuery.setString(4, swapNull(null));
        preparedQuery.setString(5, swapNull(null));
        preparedQuery.setString(6, swapNull(null));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getCrossReference(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws SQLException {
        if (s3 == null || s6 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getCrossReference");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        preparedQuery.setString(4, swapNull(s4));
        preparedQuery.setString(5, swapNull(s5));
        preparedQuery.setString(6, s6);
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getCrossReferenceForODBC(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery("odbc_getCrossReference");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, swapNull(s3));
        preparedQuery.setString(4, swapNull(s4));
        preparedQuery.setString(5, swapNull(s5));
        preparedQuery.setString(6, swapNull(s6));
        return preparedQuery.executeQuery();
    }
    
    public ResultSet getTypeInfo() throws SQLException {
        return this.getTypeInfoMinion("getTypeInfo");
    }
    
    public ResultSet getTypeInfoForODBC() throws SQLException {
        return this.getTypeInfoMinion("odbc_getTypeInfo");
    }
    
    private ResultSet getTypeInfoMinion(final String s) throws SQLException {
        try {
            final boolean checkVersion = this.getLanguageConnectionContext().getDataDictionary().checkVersion(190, null);
            final PreparedStatement preparedQuery = this.getPreparedQuery(s);
            preparedQuery.setBoolean(1, checkVersion);
            return preparedQuery.executeQuery();
        }
        catch (StandardException ex) {
            throw this.handleException(ex);
        }
    }
    
    public ResultSet getIndexInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2) throws SQLException {
        return this.doGetIndexInfo(s, s2, s3, b, b2, "getIndexInfo");
    }
    
    public ResultSet getIndexInfoForODBC(final String s, final String s2, final String s3, final boolean b, final boolean b2) throws SQLException {
        return this.doGetIndexInfo(s, s2, s3, b, b2, "odbc_getIndexInfo");
    }
    
    private ResultSet doGetIndexInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2, final String s4) throws SQLException {
        if (s3 == null) {
            throw Util.generateCsSQLException("XJ103.S");
        }
        int n = 0;
        if (b2) {
            n = 1;
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery(s4);
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        preparedQuery.setString(3, s3);
        preparedQuery.setBoolean(4, b);
        preparedQuery.setInt(5, n);
        return preparedQuery.executeQuery();
    }
    
    public boolean supportsResultSetType(final int n) {
        return n == 1003 || n == 1004;
    }
    
    public boolean supportsResultSetConcurrency(final int n, final int n2) {
        return n != 1005;
    }
    
    public boolean ownUpdatesAreVisible(final int n) {
        return n == 1004;
    }
    
    public boolean ownDeletesAreVisible(final int n) {
        return n == 1004;
    }
    
    public boolean ownInsertsAreVisible(final int n) {
        return false;
    }
    
    public boolean othersUpdatesAreVisible(final int n) {
        return n == 1003;
    }
    
    public boolean othersDeletesAreVisible(final int n) {
        return n == 1003;
    }
    
    public boolean othersInsertsAreVisible(final int n) {
        return n == 1003;
    }
    
    public boolean updatesAreDetected(final int n) {
        return n == 1004;
    }
    
    public boolean deletesAreDetected(final int n) {
        return n == 1004;
    }
    
    public boolean insertsAreDetected(final int n) {
        return false;
    }
    
    public boolean supportsBatchUpdates() {
        return true;
    }
    
    public ResultSet getUDTs(final String s, final String s2, final String s3, final int[] array) throws SQLException {
        int n = 0;
        if (array == null) {
            n = 2000;
        }
        else if (array.length > 0) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == 2000) {
                    n = 2000;
                }
            }
        }
        final PreparedStatement preparedQuery = this.getPreparedQuery("getUDTs");
        preparedQuery.setInt(1, 2000);
        preparedQuery.setString(2, swapNull(s));
        preparedQuery.setString(3, swapNull(s2));
        preparedQuery.setString(4, swapNull(s3));
        preparedQuery.setInt(5, n);
        return preparedQuery.executeQuery();
    }
    
    public Connection getConnection() {
        return this.getEmbedConnection().getApplicationConnection();
    }
    
    public boolean supportsStatementPooling() {
        return false;
    }
    
    public boolean supportsSavepoints() {
        return true;
    }
    
    public boolean supportsNamedParameters() {
        return false;
    }
    
    public boolean supportsMultipleOpenResults() {
        return true;
    }
    
    public boolean supportsGetGeneratedKeys() {
        return false;
    }
    
    public boolean supportsResultSetHoldability(final int n) {
        return true;
    }
    
    public int getResultSetHoldability() {
        return 1;
    }
    
    public int getDatabaseMajorVersion() {
        final ProductVersionHolder engineVersion = Monitor.getMonitor().getEngineVersion();
        if (engineVersion == null) {
            return -1;
        }
        return engineVersion.getMajorVersion();
    }
    
    public int getDatabaseMinorVersion() {
        final ProductVersionHolder engineVersion = Monitor.getMonitor().getEngineVersion();
        if (engineVersion == null) {
            return -1;
        }
        return engineVersion.getMinorVersion();
    }
    
    public int getJDBCMajorVersion() {
        return 3;
    }
    
    public int getJDBCMinorVersion() {
        return 0;
    }
    
    public int getSQLStateType() {
        return 2;
    }
    
    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }
    
    public ResultSet getSuperTypes(final String s, final String s2, final String s3) throws SQLException {
        return this.getSimpleQuery("getSuperTypes");
    }
    
    public ResultSet getSuperTables(final String s, final String s2, final String s3) throws SQLException {
        return this.getSimpleQuery("getSuperTables");
    }
    
    public ResultSet getAttributes(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.getSimpleQuery("getAttributes");
    }
    
    public ResultSet getClientInfoProperties() throws SQLException {
        return this.getSimpleQuery("getClientInfoProperties");
    }
    
    public ResultSet getSchemas(final String s, final String s2) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery("getSchemas");
        preparedQuery.setString(1, swapNull(s));
        preparedQuery.setString(2, swapNull(s2));
        return preparedQuery.executeQuery();
    }
    
    public boolean generatedKeyAlwaysReturned() {
        return true;
    }
    
    public ResultSet getPseudoColumns(final String s, final String s2, final String s3, final String s4) throws SQLException {
        return this.getSimpleQuery("getPseudoColumns");
    }
    
    public ResultSet getClientCachedMetaData() throws SQLException {
        return this.getSimpleQuery("METADATA", true);
    }
    
    private ResultSet getSimpleQuery(final String s, final boolean b) throws SQLException {
        final PreparedStatement preparedQuery = this.getPreparedQuery(s, b);
        if (preparedQuery == null) {
            return null;
        }
        return preparedQuery.executeQuery();
    }
    
    protected ResultSet getSimpleQuery(final String s) throws SQLException {
        return this.getSimpleQuery(s, false);
    }
    
    private PreparedStatement getPreparedQueryUsingSystemTables(final String key, final boolean b) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            PreparedStatement prepareSPS = null;
            try {
                final String property = this.getQueryDescriptions(b).getProperty(key);
                if (property == null) {
                    throw Util.notImplemented(key);
                }
                prepareSPS = this.prepareSPS(key, property, b);
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnectionContext());
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            finally {
                this.restoreContextStack();
            }
            return prepareSPS;
        }
    }
    
    private PreparedStatement getPreparedQuery(final String s, final boolean b) throws SQLException {
        PreparedStatement preparedStatement;
        if (this.notInSoftUpgradeMode() && !this.isReadOnly()) {
            preparedStatement = this.getPreparedQueryUsingSystemTables(s, b);
        }
        else {
            try {
                preparedStatement = this.getEmbedConnection().prepareMetaDataStatement(this.getQueryFromDescription(s, b));
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
        }
        return preparedStatement;
    }
    
    protected PreparedStatement getPreparedQuery(final String s) throws SQLException {
        return this.getPreparedQuery(s, false);
    }
    
    private String getQueryFromDescription(String key, final boolean b) throws StandardException {
        if (!this.getLanguageConnectionContext().getDataDictionary().checkVersion(140, null)) {
            if (key.equals("getColumnPrivileges")) {
                key = "getColumnPrivileges_10_1";
            }
            if (key.equals("getTablePrivileges")) {
                key = "getTablePrivileges_10_1";
            }
        }
        return this.getQueryDescriptions(b).getProperty(key);
    }
    
    private PreparedStatement prepareSPS(final String str, final String s, final boolean b) throws StandardException, SQLException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        languageConnectionContext.beginNestedTransaction(true);
        final DataDictionary dataDictionary = this.getLanguageConnectionContext().getDataDictionary();
        final SPSDescriptor spsDescriptor = dataDictionary.getSPSDescriptor(str, b ? dataDictionary.getSysIBMSchemaDescriptor() : dataDictionary.getSystemSchemaDescriptor());
        languageConnectionContext.commitNestedTransaction();
        if (spsDescriptor == null) {
            throw Util.notImplemented(str);
        }
        return this.getEmbedConnection().prepareMetaDataStatement("EXECUTE STATEMENT " + (b ? "SYSIBM" : "SYS") + ".\"" + str + "\"");
    }
    
    protected static final String swapNull(final String s) {
        return (s == null) ? "%" : s;
    }
    
    private LanguageConnectionContext getLanguageConnectionContext() {
        return this.getEmbedConnection().getLanguageConnection();
    }
    
    private void loadQueryDescriptions() {
        AccessController.doPrivileged((PrivilegedAction<Object>)this);
    }
    
    public final Object run() {
        this.PBloadQueryDescriptions();
        return null;
    }
}
