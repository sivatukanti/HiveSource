// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Dictionary;
import org.apache.derby.iapi.sql.dictionary.UserDescriptor;
import java.io.Serializable;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import java.sql.Statement;
import org.apache.derby.impl.sql.catalog.XPLAINSortPropsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINStatementTimingsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINTableDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINStatementDescriptor;
import org.apache.derby.iapi.services.cache.CacheManager;
import java.util.Random;
import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.impl.load.Import;
import org.apache.derby.impl.load.Export;
import org.apache.derby.impl.sql.execute.JarUtil;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.db.ConsistencyChecker;
import org.apache.derby.iapi.db.Factory;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.db.PropertyInfo;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.derby.impl.jdbc.EmbedDatabaseMetaData;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.apache.derby.impl.jdbc.Util;
import java.util.Properties;
import org.apache.derby.jdbc.InternalDriver;
import java.sql.Connection;
import org.apache.derby.iapi.services.i18n.MessageService;

public class SystemProcedures
{
    private static final int SQL_BEST_ROWID = 1;
    private static final int SQL_ROWVER = 2;
    private static final String DRIVER_TYPE_OPTION = "DATATYPE";
    private static final String ODBC_DRIVER_OPTION = "'ODBC'";
    public static final String SQLERRMC_MESSAGE_DELIMITER;
    private static final double LOG10;
    
    public static void SQLCAMESSAGE(final int n, final short n2, final String s, final String s2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final String s3, String substring, final String s4, final String s5, final String[] array, final int[] array2) {
        int n9 = 1;
        for (int n10 = 0; s.indexOf(SystemProcedures.SQLERRMC_MESSAGE_DELIMITER, n10) != -1; n10 = s.indexOf(SystemProcedures.SQLERRMC_MESSAGE_DELIMITER, n10) + SystemProcedures.SQLERRMC_MESSAGE_DELIMITER.length(), ++n9) {}
        if (n9 == 1) {
            MessageService.getLocalizedMessage(n, n2, s, s2, n3, n4, n5, n6, n7, n8, s3, substring, s4, s5, array, array2);
        }
        else {
            int beginIndex = 0;
            final String[] array3 = new String[2];
            for (int i = 0; i < n9; ++i) {
                final int index = s.indexOf(SystemProcedures.SQLERRMC_MESSAGE_DELIMITER, beginIndex);
                String s6;
                if (i == n9 - 1) {
                    s6 = s.substring(beginIndex);
                }
                else {
                    s6 = s.substring(beginIndex, index);
                }
                if (i > 0) {
                    substring = s6.substring(0, 5);
                    s6 = s6.substring(6);
                    final StringBuffer sb = new StringBuffer();
                    final int n11 = 0;
                    array[n11] = sb.append(array[n11]).append(" SQLSTATE: ").append(substring).append(": ").toString();
                }
                MessageService.getLocalizedMessage(n, (short)s6.length(), s6, s2, n3, n4, n5, n6, n7, n8, s3, substring, s4, s5, array3, array2);
                if (array2[0] == 0) {
                    if (i == 0) {
                        array[0] = array3[0];
                    }
                    else {
                        final StringBuffer sb2 = new StringBuffer();
                        final int n12 = 0;
                        array[n12] = sb2.append(array[n12]).append(array3[0]).toString();
                    }
                }
                beginIndex = index + SystemProcedures.SQLERRMC_MESSAGE_DELIMITER.length();
            }
        }
    }
    
    private static Connection getDefaultConn() throws SQLException {
        final InternalDriver activeDriver = InternalDriver.activeDriver();
        if (activeDriver != null) {
            final Connection connect = activeDriver.connect("jdbc:default:connection", null, 0);
            if (connect != null) {
                return connect;
            }
        }
        throw Util.noCurrentConnection();
    }
    
    private static DatabaseMetaData getDMD() throws SQLException {
        return getDefaultConn().getMetaData();
    }
    
    public static void SQLPROCEDURES(final String s, final String s2, final String s3, final String s4, final ResultSet[] array) throws SQLException {
        array[0] = (isForODBC(s4) ? ((EmbedDatabaseMetaData)getDMD()).getProceduresForODBC(s, s2, s3) : getDMD().getProcedures(s, s2, s3));
    }
    
    public static void SQLFUNCTIONS(final String s, final String s2, final String s3, final String s4, final ResultSet[] array) throws SQLException {
        array[0] = ((EmbedDatabaseMetaData)getDMD()).getFunctions(s, s2, s3);
    }
    
    public static void SQLTABLES(final String s, final String s2, final String s3, final String str, final String s4, final ResultSet[] array) throws SQLException {
        final String option = getOption("GETCATALOGS", s4);
        if (option != null && option.trim().equals("1")) {
            array[0] = getDMD().getCatalogs();
            return;
        }
        final String option2 = getOption("GETTABLETYPES", s4);
        if (option2 != null && option2.trim().equals("1")) {
            array[0] = getDMD().getTableTypes();
            return;
        }
        final String option3 = getOption("GETSCHEMAS", s4);
        if (option3 != null) {
            final String trim = option3.trim();
            if (trim.equals("1")) {
                array[0] = getDMD().getSchemas();
                return;
            }
            if (trim.equals("2")) {
                array[0] = ((EmbedDatabaseMetaData)getDMD()).getSchemas(s, s2);
                return;
            }
        }
        String[] array2 = null;
        if (str != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(str, "',");
            array2 = new String[stringTokenizer.countTokens()];
            int n = 0;
            while (stringTokenizer.hasMoreTokens()) {
                array2[n] = stringTokenizer.nextToken();
                ++n;
            }
        }
        array[0] = getDMD().getTables(s, s2, s3, array2);
    }
    
    public static void SQLFOREIGNKEYS(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final ResultSet[] array) throws SQLException {
        final String option = getOption("EXPORTEDKEY", s7);
        final String option2 = getOption("IMPORTEDKEY", s7);
        if (option2 != null && option2.trim().equals("1")) {
            array[0] = getDMD().getImportedKeys(s4, s5, s6);
        }
        else if (option != null && option.trim().equals("1")) {
            array[0] = getDMD().getExportedKeys(s, s2, s3);
        }
        else {
            array[0] = (isForODBC(s7) ? ((EmbedDatabaseMetaData)getDMD()).getCrossReferenceForODBC(s, s2, s3, s4, s5, s6) : getDMD().getCrossReference(s, s2, s3, s4, s5, s6));
        }
    }
    
    private static String getOption(final String str, final String s) {
        if (s == null) {
            return null;
        }
        final int lastIndex = s.lastIndexOf(str);
        if (lastIndex < 0) {
            return null;
        }
        final int index = s.indexOf(61, lastIndex);
        if (index < 0) {
            return null;
        }
        final int index2 = s.indexOf(59, index);
        if (index2 < 0) {
            return s.substring(index + 1);
        }
        return s.substring(index + 1, index2);
    }
    
    public static void SQLPROCEDURECOLS(final String s, final String s2, final String s3, final String s4, final String s5, final ResultSet[] array) throws SQLException {
        array[0] = (isForODBC(s5) ? ((EmbedDatabaseMetaData)getDMD()).getProcedureColumnsForODBC(s, s2, s3, s4) : getDMD().getProcedureColumns(s, s2, s3, s4));
    }
    
    public static void SQLFUNCTIONPARAMS(final String s, final String s2, final String s3, final String s4, final String s5, final ResultSet[] array) throws SQLException {
        array[0] = ((EmbedDatabaseMetaData)getDMD()).getFunctionColumns(s, s2, s3, s4);
    }
    
    public static void SQLCOLUMNS(final String s, final String s2, final String s3, final String s4, final String s5, final ResultSet[] array) throws SQLException {
        array[0] = (isForODBC(s5) ? ((EmbedDatabaseMetaData)getDMD()).getColumnsForODBC(s, s2, s3, s4) : getDMD().getColumns(s, s2, s3, s4));
    }
    
    public static void SQLCOLPRIVILEGES(final String s, final String s2, final String s3, final String s4, final String s5, final ResultSet[] array) throws SQLException {
        array[0] = getDMD().getColumnPrivileges(s, s2, s3, s4);
    }
    
    public static void SQLTABLEPRIVILEGES(final String s, final String s2, final String s3, final String s4, final ResultSet[] array) throws SQLException {
        array[0] = getDMD().getTablePrivileges(s, s2, s3);
    }
    
    public static void SQLPRIMARYKEYS(final String s, final String s2, final String s3, final String s4, final ResultSet[] array) throws SQLException {
        array[0] = getDMD().getPrimaryKeys(s, s2, s3);
    }
    
    public static void SQLGETTYPEINFO(final short n, final String s, final ResultSet[] array) throws SQLException {
        array[0] = (isForODBC(s) ? ((EmbedDatabaseMetaData)getDMD()).getTypeInfoForODBC() : getDMD().getTypeInfo());
    }
    
    public static void SQLSTATISTICS(final String s, final String s2, final String s3, final short n, final short n2, final String s4, final ResultSet[] array) throws SQLException {
        final boolean b = n == 0;
        final boolean b2 = n2 == 1;
        array[0] = (isForODBC(s4) ? ((EmbedDatabaseMetaData)getDMD()).getIndexInfoForODBC(s, s2, s3, b, b2) : getDMD().getIndexInfo(s, s2, s3, b, b2));
    }
    
    public static void SQLSPECIALCOLUMNS(final short n, final String s, final String s2, final String s3, final short n2, final short n3, final String s4, final ResultSet[] array) throws SQLException {
        final boolean b = n3 == 1;
        if (n == 1) {
            array[0] = (isForODBC(s4) ? ((EmbedDatabaseMetaData)getDMD()).getBestRowIdentifierForODBC(s, s2, s3, n2, b) : getDMD().getBestRowIdentifier(s, s2, s3, n2, b));
        }
        else {
            array[0] = (isForODBC(s4) ? ((EmbedDatabaseMetaData)getDMD()).getVersionColumnsForODBC(s, s2, s3) : getDMD().getVersionColumns(s, s2, s3));
        }
    }
    
    public static void SQLUDTS(final String s, final String s2, final String s3, final String str, final String s4, final ResultSet[] array) throws SQLException {
        int[] array2 = null;
        if (str != null && str.length() > 0) {
            final StringTokenizer stringTokenizer = new StringTokenizer(str, " \t\n\t,");
            final int countTokens = stringTokenizer.countTokens();
            array2 = new int[countTokens];
            String nextToken = "";
            try {
                for (int i = 0; i < countTokens; ++i) {
                    nextToken = stringTokenizer.nextToken();
                    array2[i] = Integer.parseInt(nextToken);
                }
            }
            catch (NumberFormatException ex) {
                throw new SQLException("Invalid type, " + nextToken + ", passed to getUDTs.");
            }
            catch (NoSuchElementException ex2) {
                throw new SQLException("Internal failure: NoSuchElementException in getUDTs.");
            }
        }
        array[0] = getDMD().getUDTs(s, s2, s3, array2);
    }
    
    public static void METADATA(final ResultSet[] array) throws SQLException {
        array[0] = ((EmbedDatabaseMetaData)getDMD()).getClientCachedMetaData();
    }
    
    private static boolean isForODBC(final String s) {
        final String option = getOption("DATATYPE", s);
        return option != null && option.toUpperCase().equals("'ODBC'");
    }
    
    public static void SYSCS_SET_DATABASE_PROPERTY(final String s, final String s2) throws SQLException {
        PropertyInfo.setDatabaseProperty(s, s2);
    }
    
    public static String SYSCS_GET_DATABASE_PROPERTY(final String s) throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        try {
            return PropertyUtil.getDatabaseProperty(currentLCC.getTransactionExecute(), s);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void SYSCS_UPDATE_STATISTICS(final String s, final String s2, final String s3) throws SQLException {
        final StringBuffer sb = new StringBuffer();
        sb.append("alter table ");
        sb.append(basicSchemaTableValidation(s, s2));
        if (s3 != null && s3.length() == 0) {
            throw PublicAPI.wrapStandardException(StandardException.newException("42X65", s3));
        }
        if (s3 == null) {
            sb.append(" all update statistics ");
        }
        else {
            sb.append(" update statistics " + IdUtil.normalToDelimited(s3));
        }
        final Connection defaultConn = getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement(sb.toString());
        prepareStatement.executeUpdate();
        prepareStatement.close();
        defaultConn.close();
    }
    
    public static void SYSCS_DROP_STATISTICS(final String s, final String s2, final String s3) throws SQLException {
        final StringBuffer sb = new StringBuffer();
        sb.append("alter table ");
        sb.append(basicSchemaTableValidation(s, s2));
        if (s3 != null && s3.length() == 0) {
            throw PublicAPI.wrapStandardException(StandardException.newException("42X65", s3));
        }
        if (s3 == null) {
            sb.append(" all drop statistics ");
        }
        else {
            sb.append(" statistics drop " + IdUtil.normalToDelimited(s3));
        }
        final Connection defaultConn = getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement(sb.toString());
        prepareStatement.executeUpdate();
        prepareStatement.close();
        defaultConn.close();
    }
    
    private static String basicSchemaTableValidation(final String s, final String s2) throws SQLException {
        if (s != null && s.length() == 0) {
            throw PublicAPI.wrapStandardException(StandardException.newException("42Y07", s));
        }
        if (s2 == null || s2.length() == 0) {
            throw PublicAPI.wrapStandardException(StandardException.newException("42X05", s2));
        }
        return IdUtil.mkQualifiedName(s, s2);
    }
    
    public static void SYSCS_COMPRESS_TABLE(final String s, final String s2, final short n) throws SQLException {
        final StringBuffer sb = new StringBuffer();
        sb.append("alter table ");
        sb.append(basicSchemaTableValidation(s, s2));
        sb.append(" compress" + ((n != 0) ? " sequential" : ""));
        final Connection defaultConn = getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement(sb.toString());
        prepareStatement.executeUpdate();
        prepareStatement.close();
        defaultConn.close();
    }
    
    public static void SYSCS_FREEZE_DATABASE() throws SQLException {
        Factory.getDatabaseOfConnection().freeze();
    }
    
    public static void SYSCS_UNFREEZE_DATABASE() throws SQLException {
        Factory.getDatabaseOfConnection().unfreeze();
    }
    
    public static void SYSCS_CHECKPOINT_DATABASE() throws SQLException {
        Factory.getDatabaseOfConnection().checkpoint();
    }
    
    public static void SYSCS_BACKUP_DATABASE(final String s) throws SQLException {
        Factory.getDatabaseOfConnection().backup(s, true);
    }
    
    public static void SYSCS_BACKUP_DATABASE_NOWAIT(final String s) throws SQLException {
        Factory.getDatabaseOfConnection().backup(s, false);
    }
    
    public static void SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE(final String s, final short n) throws SQLException {
        Factory.getDatabaseOfConnection().backupAndEnableLogArchiveMode(s, n != 0, true);
    }
    
    public static void SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE_NOWAIT(final String s, final short n) throws SQLException {
        Factory.getDatabaseOfConnection().backupAndEnableLogArchiveMode(s, n != 0, false);
    }
    
    public static void SYSCS_DISABLE_LOG_ARCHIVE_MODE(final short n) throws SQLException {
        Factory.getDatabaseOfConnection().disableLogArchiveMode(n != 0);
    }
    
    public static void SYSCS_SET_RUNTIMESTATISTICS(final short n) throws SQLException {
        ConnectionUtil.getCurrentLCC().setRunTimeStatisticsMode(n != 0);
    }
    
    public static void SYSCS_SET_STATISTICS_TIMING(final short n) throws SQLException {
        ConnectionUtil.getCurrentLCC().setStatisticsTiming(n != 0);
    }
    
    public static int SYSCS_CHECK_TABLE(final String s, final String s2) throws SQLException {
        return ConsistencyChecker.checkTable(s, s2) ? 1 : 0;
    }
    
    public static void SYSCS_INPLACE_COMPRESS_TABLE(final String s, final String s2, final short n, final short n2, final short n3) throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        final TransactionController transactionExecute = currentLCC.getTransactionExecute();
        try {
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(s2, dataDictionary.getSchemaDescriptor(s, transactionExecute, true), transactionExecute);
            if (tableDescriptor != null && tableDescriptor.getTableType() == 5) {
                return;
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
        final String string = "alter table " + IdUtil.normalToDelimited(s) + "." + IdUtil.normalToDelimited(s2) + " compress inplace" + ((n != 0) ? " purge" : "") + ((n2 != 0) ? " defragment" : "") + ((n3 != 0) ? " truncate_end" : "");
        final Connection defaultConn = getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement(string);
        prepareStatement.executeUpdate();
        prepareStatement.close();
        defaultConn.close();
    }
    
    public static String SYSCS_GET_RUNTIMESTATISTICS() throws SQLException {
        final RunTimeStatistics runTimeStatisticsObject = ConnectionUtil.getCurrentLCC().getRunTimeStatisticsObject();
        if (runTimeStatisticsObject == null) {
            return null;
        }
        return runTimeStatisticsObject.toString();
    }
    
    public static void INSTALL_JAR(final String s, final String s2, final int n) throws SQLException {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final String[] multiPartSQLIdentifier = IdUtil.parseMultiPartSQLIdentifier(s2.trim());
            String currentSchemaName;
            String s3;
            if (multiPartSQLIdentifier.length == 1) {
                currentSchemaName = currentLCC.getCurrentSchemaName();
                s3 = multiPartSQLIdentifier[0];
            }
            else {
                currentSchemaName = multiPartSQLIdentifier[0];
                s3 = multiPartSQLIdentifier[1];
            }
            checkJarSQLName(s3);
            JarUtil.install(currentLCC, currentSchemaName, s3, s);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void REPLACE_JAR(final String s, final String s2) throws SQLException {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final String[] multiPartSQLIdentifier = IdUtil.parseMultiPartSQLIdentifier(s2.trim());
            String currentSchemaName;
            String s3;
            if (multiPartSQLIdentifier.length == 1) {
                currentSchemaName = currentLCC.getCurrentSchemaName();
                s3 = multiPartSQLIdentifier[0];
            }
            else {
                currentSchemaName = multiPartSQLIdentifier[0];
                s3 = multiPartSQLIdentifier[1];
            }
            checkJarSQLName(s3);
            JarUtil.replace(currentLCC, currentSchemaName, s3, s);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void REMOVE_JAR(final String s, final int n) throws SQLException {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final String[] multiPartSQLIdentifier = IdUtil.parseMultiPartSQLIdentifier(s.trim());
            String currentSchemaName;
            String s2;
            if (multiPartSQLIdentifier.length == 1) {
                currentSchemaName = currentLCC.getCurrentSchemaName();
                s2 = multiPartSQLIdentifier[0];
            }
            else {
                currentSchemaName = multiPartSQLIdentifier[0];
                s2 = multiPartSQLIdentifier[1];
            }
            checkJarSQLName(s2);
            JarUtil.drop(currentLCC, currentSchemaName, s2);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    private static void checkJarSQLName(final String s) throws StandardException {
        if (s.length() == 0 || s.indexOf(58) != -1) {
            throw StandardException.newException("XCXA0.S");
        }
    }
    
    public static void SYSCS_EXPORT_TABLE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        Export.exportTable(defaultConn, s, s2, s3, s4, s5, s6);
        defaultConn.commit();
    }
    
    public static void SYSCS_EXPORT_TABLE_LOBS_TO_EXTFILE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        Export.exportTable(defaultConn, s, s2, s3, s4, s5, s6, s7);
        defaultConn.commit();
    }
    
    public static void SYSCS_EXPORT_QUERY(final String s, final String s2, final String s3, final String s4, final String s5) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        Export.exportQuery(defaultConn, s, s2, s3, s4, s5);
        defaultConn.commit();
    }
    
    public static void SYSCS_EXPORT_QUERY_LOBS_TO_EXTFILE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        Export.exportQuery(defaultConn, s, s2, s3, s4, s5, s6);
        defaultConn.commit();
    }
    
    public static void SYSCS_IMPORT_TABLE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final short n) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        try {
            Import.importTable(defaultConn, s, s2, s3, s4, s5, s6, n, false);
        }
        catch (SQLException ex) {
            rollBackAndThrowSQLException(defaultConn, ex);
        }
        defaultConn.commit();
    }
    
    private static void rollBackAndThrowSQLException(final Connection connection, final SQLException ex) throws SQLException {
        try {
            connection.rollback();
        }
        catch (SQLException nextException) {
            ex.setNextException(nextException);
        }
        throw ex;
    }
    
    public static void SYSCS_IMPORT_TABLE_LOBS_FROM_EXTFILE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final short n) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        try {
            Import.importTable(defaultConn, s, s2, s3, s4, s5, s6, n, true);
        }
        catch (SQLException ex) {
            rollBackAndThrowSQLException(defaultConn, ex);
        }
        defaultConn.commit();
    }
    
    public static void SYSCS_IMPORT_DATA(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final short n) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        try {
            Import.importData(defaultConn, s, s2, s3, s4, s5, s6, s7, s8, n, false);
        }
        catch (SQLException ex) {
            rollBackAndThrowSQLException(defaultConn, ex);
        }
        defaultConn.commit();
    }
    
    public static void SYSCS_IMPORT_DATA_LOBS_FROM_EXTFILE(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final short n) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        try {
            Import.importData(defaultConn, s, s2, s3, s4, s5, s6, s7, s8, n, true);
        }
        catch (SQLException ex) {
            rollBackAndThrowSQLException(defaultConn, ex);
        }
        defaultConn.commit();
    }
    
    public static void SYSCS_BULK_INSERT(final String s, final String s2, final String s3, final String s4) throws SQLException {
        final PreparedStatement prepareStatement = getDefaultConn().prepareStatement("insert into " + IdUtil.mkQualifiedName(s, s2) + " --DERBY-PROPERTIES insertMode=bulkInsert \n" + "select * from new " + IdUtil.normalToDelimited(s3) + "(" + StringUtil.quoteStringLiteral(s) + ", " + StringUtil.quoteStringLiteral(s2) + ", " + StringUtil.quoteStringLiteral(s4) + ")" + " as t");
        prepareStatement.executeUpdate();
        prepareStatement.close();
    }
    
    public static void SYSCS_RELOAD_SECURITY_POLICY() throws SQLException {
        if (System.getSecurityManager() == null) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    Policy.getPolicy().refresh();
                    return null;
                }
            });
        }
        catch (SecurityException ex) {
            throw Util.policyNotReloaded(ex);
        }
    }
    
    public static double PI() {
        return 3.141592653589793;
    }
    
    public static double LOG10(final double n) {
        return StrictMath.log(n) / SystemProcedures.LOG10;
    }
    
    public static double COT(final double n) {
        return 1.0 / StrictMath.tan(n);
    }
    
    public static double COSH(final double a) {
        return (StrictMath.exp(a) + StrictMath.exp(-a)) / 2.0;
    }
    
    public static double SINH(final double a) {
        return (StrictMath.exp(a) - StrictMath.exp(-a)) / 2.0;
    }
    
    public static double TANH(final double n) {
        return (StrictMath.exp(n) - StrictMath.exp(-n)) / (StrictMath.exp(n) + StrictMath.exp(-n));
    }
    
    public static int SIGN(final double n) {
        return (n < 0.0) ? -1 : ((n > 0.0) ? 1 : 0);
    }
    
    public static double RAND(final int n) {
        return new Random(n).nextDouble();
    }
    
    public static void SYSCS_SET_USER_ACCESS(final String s, final String s2) throws SQLException {
        try {
            if (s == null) {
                throw StandardException.newException("28502", s);
            }
            String s3;
            if ("FULLACCESS".equals(s2)) {
                s3 = "derby.database.fullAccessUsers";
            }
            else if ("READONLYACCESS".equals(s2)) {
                s3 = "derby.database.readOnlyAccessUsers";
            }
            else {
                if (s2 != null) {
                    throw StandardException.newException("XCZ00.S", s2);
                }
                s3 = null;
            }
            removeFromAccessList("derby.database.fullAccessUsers", s);
            removeFromAccessList("derby.database.readOnlyAccessUsers", s);
            if (s3 != null) {
                SYSCS_SET_DATABASE_PROPERTY(s3, IdUtil.appendNormalToList(s, SYSCS_GET_DATABASE_PROPERTY(s3)));
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    private static void removeFromAccessList(final String s, final String s2) throws SQLException, StandardException {
        final String syscs_GET_DATABASE_PROPERTY = SYSCS_GET_DATABASE_PROPERTY(s);
        if (syscs_GET_DATABASE_PROPERTY != null) {
            SYSCS_SET_DATABASE_PROPERTY(s, IdUtil.deleteId(s2, syscs_GET_DATABASE_PROPERTY));
        }
    }
    
    public static String SYSCS_GET_USER_ACCESS(final String s) throws SQLException {
        try {
            if (s == null) {
                throw StandardException.newException("28502", s);
            }
            if (IdUtil.idOnList(s, SYSCS_GET_DATABASE_PROPERTY("derby.database.fullAccessUsers"))) {
                return "FULLACCESS";
            }
            if (IdUtil.idOnList(s, SYSCS_GET_DATABASE_PROPERTY("derby.database.readOnlyAccessUsers"))) {
                return "READONLYACCESS";
            }
            final String syscs_GET_DATABASE_PROPERTY = SYSCS_GET_DATABASE_PROPERTY("derby.database.defaultConnectionMode");
            String sqlToUpperCase;
            if (syscs_GET_DATABASE_PROPERTY != null) {
                sqlToUpperCase = StringUtil.SQLToUpperCase(syscs_GET_DATABASE_PROPERTY);
            }
            else {
                sqlToUpperCase = "FULLACCESS";
            }
            return sqlToUpperCase;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void SYSCS_INVALIDATE_STORED_STATEMENTS() throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        final DataDictionary dataDictionary = currentLCC.getDataDictionary();
        try {
            dataDictionary.invalidateAllSPSPlans(currentLCC);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void SYSCS_EMPTY_STATEMENT_CACHE() throws SQLException {
        final CacheManager statementCache = ConnectionUtil.getCurrentLCC().getLanguageConnectionFactory().getStatementCache();
        if (statementCache != null) {
            statementCache.ageOut();
        }
    }
    
    public static void SYSCS_SET_XPLAIN_MODE(final int n) throws SQLException, StandardException {
        ConnectionUtil.getCurrentLCC().setXplainOnlyMode(n != 0);
    }
    
    public static int SYSCS_GET_XPLAIN_MODE() throws SQLException, StandardException {
        return ConnectionUtil.getCurrentLCC().getXplainOnlyMode() ? 1 : 0;
    }
    
    public static void SYSCS_SET_XPLAIN_SCHEMA(final String xplainSchema) throws SQLException, StandardException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        currentLCC.getTransactionExecute();
        if (xplainSchema == null || xplainSchema.trim().length() == 0) {
            currentLCC.setXplainSchema(null);
            return;
        }
        final boolean runTimeStatisticsMode = currentLCC.getRunTimeStatisticsMode();
        currentLCC.setRunTimeStatisticsMode(false);
        createXplainSchema(xplainSchema);
        createXplainTable(currentLCC, xplainSchema, new XPLAINStatementDescriptor());
        createXplainTable(currentLCC, xplainSchema, new XPLAINStatementTimingsDescriptor());
        createXplainTable(currentLCC, xplainSchema, new XPLAINResultSetDescriptor());
        createXplainTable(currentLCC, xplainSchema, new XPLAINResultSetTimingsDescriptor());
        createXplainTable(currentLCC, xplainSchema, new XPLAINScanPropsDescriptor());
        createXplainTable(currentLCC, xplainSchema, new XPLAINSortPropsDescriptor());
        currentLCC.setRunTimeStatisticsMode(runTimeStatisticsMode);
        currentLCC.setXplainSchema(xplainSchema);
    }
    
    private static boolean hasSchema(final Connection connection, final String s) throws SQLException {
        ResultSet schemas;
        boolean equals;
        for (schemas = connection.getMetaData().getSchemas(), equals = false; schemas.next() && !equals; equals = s.equals(schemas.getString("TABLE_SCHEM"))) {}
        schemas.close();
        return equals;
    }
    
    private static boolean hasTable(final Connection connection, final String s, final String s2) throws SQLException {
        final ResultSet tables = connection.getMetaData().getTables(null, s, s2, new String[] { "TABLE" });
        final boolean next = tables.next();
        tables.close();
        return next;
    }
    
    private static void createXplainSchema(final String s) throws SQLException {
        final Connection defaultConn = getDefaultConn();
        if (!hasSchema(defaultConn, s)) {
            final String normalToDelimited = IdUtil.normalToDelimited(s);
            final Statement statement = defaultConn.createStatement();
            statement.executeUpdate("CREATE SCHEMA " + normalToDelimited);
            statement.close();
        }
        defaultConn.close();
    }
    
    private static void createXplainTable(final LanguageConnectionContext languageConnectionContext, final String s, final XPLAINTableDescriptor xplainTableDescriptor) throws SQLException {
        final String[] tableDDL = xplainTableDescriptor.getTableDDL(s);
        final Connection defaultConn = getDefaultConn();
        if (!hasTable(defaultConn, s, xplainTableDescriptor.getCatalogName())) {
            final Statement statement = defaultConn.createStatement();
            for (int i = 0; i < tableDDL.length; ++i) {
                statement.executeUpdate(tableDDL[i]);
            }
            statement.close();
        }
        final String tableInsert = xplainTableDescriptor.getTableInsert();
        defaultConn.prepareStatement(tableInsert).close();
        defaultConn.close();
        languageConnectionContext.setXplainStatement(xplainTableDescriptor.getCatalogName(), tableInsert);
    }
    
    public static String SYSCS_GET_XPLAIN_SCHEMA() throws SQLException, StandardException {
        final String xplainSchema = ConnectionUtil.getCurrentLCC().getXplainSchema();
        if (xplainSchema == null) {
            return "";
        }
        return xplainSchema;
    }
    
    public static void SYSCS_CREATE_USER(String normalizeUserName, final String s) throws SQLException {
        normalizeUserName = normalizeUserName(normalizeUserName);
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        final TransactionController transactionExecute = currentLCC.getTransactionExecute();
        try {
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
            if (!authorizationDatabaseOwner.equals(normalizeUserName)) {
                if (dataDictionary.getUser(authorizationDatabaseOwner) == null) {
                    throw StandardException.newException("4251K");
                }
            }
            else if (!authorizationDatabaseOwner.equals(currentLCC.getStatementContext().getSQLSessionContext().getCurrentUser())) {
                throw StandardException.newException("4251D");
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
        addUser(normalizeUserName, s, transactionExecute);
    }
    
    public static void addUser(final String anObject, final String s, final TransactionController transactionController) throws SQLException {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            dataDictionary.startWriting(currentLCC);
            dataDictionary.addDescriptor(makeUserDescriptor(dataDictionary, transactionController, anObject, s), null, 22, false, transactionController);
            if (dataDictionary.getAuthorizationDatabaseOwner().equals(anObject)) {
                transactionController.setProperty("derby.authentication.provider", "NATIVE::LOCAL", true);
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    private static UserDescriptor makeUserDescriptor(final DataDictionary dataDictionary, final TransactionController transactionController, final String s, final String s2) throws StandardException {
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final PasswordHasher passwordHasher = dataDictionary.makePasswordHasher(transactionController.getProperties());
        if (passwordHasher == null) {
            throw StandardException.newException("4251G");
        }
        return dataDescriptorGenerator.newUserDescriptor(s, passwordHasher.encodeHashingScheme(), passwordHasher.hashPasswordIntoString(s, s2).toCharArray(), new Timestamp(new Date().getTime()));
    }
    
    public static void SYSCS_RESET_PASSWORD(final String s, final String s2) throws SQLException {
        resetAuthorizationIDPassword(normalizeUserName(s), s2);
    }
    
    private static void resetAuthorizationIDPassword(final String s, final String s2) throws SQLException {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            final TransactionController transactionExecute = currentLCC.getTransactionExecute();
            checkLegalUser(dataDictionary, s);
            dataDictionary.startWriting(currentLCC);
            dataDictionary.updateUser(makeUserDescriptor(dataDictionary, transactionExecute, s, s2), transactionExecute);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static void SYSCS_MODIFY_PASSWORD(final String s) throws SQLException {
        resetAuthorizationIDPassword(ConnectionUtil.getCurrentLCC().getStatementContext().getSQLSessionContext().getCurrentUser(), s);
    }
    
    public static void SYSCS_DROP_USER(String normalizeUserName) throws SQLException {
        normalizeUserName = normalizeUserName(normalizeUserName);
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            if (dataDictionary.getAuthorizationDatabaseOwner().equals(normalizeUserName)) {
                throw StandardException.newException("4251F");
            }
            checkLegalUser(dataDictionary, normalizeUserName);
            dataDictionary.startWriting(currentLCC);
            dataDictionary.dropUser(normalizeUserName, currentLCC.getTransactionExecute());
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    private static void checkLegalUser(final DataDictionary dataDictionary, final String s) throws StandardException {
        if (dataDictionary.getUser(s) == null) {
            throw StandardException.newException("XK001.S");
        }
    }
    
    private static String normalizeUserName(final String s) throws SQLException {
        try {
            return IdUtil.getUserAuthorizationId(s);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public static Long SYSCS_PEEK_AT_SEQUENCE(final String s, final String s2) throws SQLException {
        try {
            return ConnectionUtil.getCurrentLCC().getDataDictionary().peekAtSequence(s, s2);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    static {
        SQLERRMC_MESSAGE_DELIMITER = new String(new char[] { '\u0014', '\u0014', '\u0014' });
        LOG10 = StrictMath.log(10.0);
    }
}
