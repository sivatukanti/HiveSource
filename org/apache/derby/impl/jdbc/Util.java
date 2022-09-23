// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.services.property.PropertyUtil;
import java.sql.BatchUpdateException;
import org.apache.derby.iapi.services.info.JVMInfo;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.error.ErrorStringBuilder;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.sql.SQLException;

public abstract class Util
{
    private static SQLExceptionFactory exceptionFactory;
    private static int logSeverityLevel;
    
    public static void logAndThrowSQLException(final SQLException ex) throws SQLException {
        if (ex.getErrorCode() >= Util.logSeverityLevel) {
            logSQLException(ex);
        }
        throw ex;
    }
    
    public static void logSQLException(final SQLException ex) {
        if (ex == null) {
            return;
        }
        final String message = ex.getMessage();
        final String sqlState = ex.getSQLState();
        if (sqlState != null && sqlState.equals("08004") && message != null && message.equals("Connection refused : java.lang.OutOfMemoryError")) {
            return;
        }
        logError("\nERROR " + ex.getSQLState() + ": " + ex.getMessage() + "\n", ex);
    }
    
    private static void logError(final String s, final Throwable t) {
        final HeaderPrintWriter stream = Monitor.getStream();
        if (stream == null) {
            t.printStackTrace();
            return;
        }
        final ErrorStringBuilder errorStringBuilder = new ErrorStringBuilder(stream.getHeader());
        errorStringBuilder.append(s);
        errorStringBuilder.stackTrace(t);
        stream.print(errorStringBuilder.get().toString());
        stream.flush();
        errorStringBuilder.reset();
    }
    
    private static SQLException newEmbedSQLException(final String s, final Object[] array, final SQLException ex, final int n, final Throwable t) {
        return Util.exceptionFactory.getSQLException(MessageService.getCompleteMessage(s, array), s, ex, n, t, array);
    }
    
    public static SQLException newEmbedSQLException(final String s, final Object[] array, final int n) {
        return newEmbedSQLException(s, array, null, n, null);
    }
    
    private static SQLException newEmbedSQLException(final String s, final Object[] array, final int n, final Throwable t) {
        return newEmbedSQLException(s, array, null, n, t);
    }
    
    private static SQLException newEmbedSQLException(final String s, final int n) {
        return newEmbedSQLException(s, null, null, n, null);
    }
    
    public static void ASSERT(final EmbedConnection embedConnection, final boolean b, final String s) throws SQLException {
    }
    
    static void THROWASSERT(final EmbedConnection embedConnection, final String s) throws SQLException {
    }
    
    public static void checkForSupportedDataType(final int n) throws SQLException {
        if (!isSupportedType(n)) {
            throw generateCsSQLException("0A000.S.7", typeName(n));
        }
    }
    
    public static void checkSupportedRaiseStandard(final int n) throws StandardException {
        if (!isSupportedType(n)) {
            throw StandardException.newException("0A000.S.7", typeName(n));
        }
    }
    
    private static boolean isSupportedType(final int n) {
        switch (n) {
            case -16:
            case -15:
            case -9:
            case -8:
            case 0:
            case 70:
            case 1111:
            case 2001:
            case 2002:
            case 2003:
            case 2006:
            case 2009:
            case 2011:
            case 2012: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    static SQLException newException(final String s, final Object o, final Object o2, final Object o3) {
        return newEmbedSQLException(s, new Object[] { o, o2, o3 }, StandardException.getSeverityFromIdentifier(s));
    }
    
    public static SQLException generateCsSQLException(final String s) {
        return newEmbedSQLException(s, StandardException.getSeverityFromIdentifier(s));
    }
    
    public static SQLException generateCsSQLException(final String s, final Object o) {
        return newEmbedSQLException(s, new Object[] { o }, StandardException.getSeverityFromIdentifier(s));
    }
    
    public static SQLException generateCsSQLException(final String s, final Object o, final Object o2) {
        return newEmbedSQLException(s, new Object[] { o, o2 }, StandardException.getSeverityFromIdentifier(s));
    }
    
    public static SQLException generateCsSQLException(final String s, final Object o, final Object o2, final Object o3) {
        return newEmbedSQLException(s, new Object[] { o, o2, o3 }, StandardException.getSeverityFromIdentifier(s));
    }
    
    static SQLException generateCsSQLException(final String s, final Object o, final Throwable t) {
        return newEmbedSQLException(s, new Object[] { o }, StandardException.getSeverityFromIdentifier(s), t);
    }
    
    public static SQLException generateCsSQLException(final StandardException ex) {
        return Util.exceptionFactory.getSQLException(ex.getMessage(), ex.getMessageId(), null, ex.getSeverity(), ex, ex.getArguments());
    }
    
    public static SQLException noCurrentConnection() {
        return newEmbedSQLException("08003", StandardException.getSeverityFromIdentifier("08003"));
    }
    
    static SQLException seeNextException(final String s, final Object[] array, final SQLException ex) {
        return newEmbedSQLException(s, array, ex, StandardException.getSeverityFromIdentifier(s), null);
    }
    
    public static SQLException javaException(final Throwable t) {
        String message = t.getMessage();
        if (message == null) {
            message = "";
        }
        final String name = t.getClass().getName();
        SQLException ex = null;
        final Throwable cause = t.getCause();
        if (cause != null) {
            if (cause instanceof SQLException) {
                ex = (SQLException)cause;
            }
            else if (cause instanceof StandardException) {
                ex = generateCsSQLException((StandardException)cause);
            }
            else {
                ex = javaException(cause);
            }
        }
        final SQLException embedSQLException = newEmbedSQLException("XJ001.U", new Object[] { name, message }, ex, 0, t);
        if (embedSQLException.getErrorCode() >= Util.logSeverityLevel) {
            logSQLException(embedSQLException);
        }
        return embedSQLException;
    }
    
    public static SQLException policyNotReloaded(final Throwable t) {
        return newEmbedSQLException("XK000.S", new Object[] { t.getMessage() }, StandardException.getSeverityFromIdentifier("XK000.S"), t);
    }
    
    public static SQLException notImplemented() {
        return notImplemented(MessageService.getTextMessage("J008"));
    }
    
    public static SQLException notImplemented(final String s) {
        return newEmbedSQLException("0A000.S", new Object[] { s }, StandardException.getSeverityFromIdentifier("0A000.S"));
    }
    
    static SQLException setStreamFailure(final IOException ex) {
        String s = ex.getMessage();
        if (s == null) {
            s = ex.getClass().getName();
        }
        return generateCsSQLException("XJ022.S", s, ex);
    }
    
    static SQLException typeMisMatch(final int n) {
        return newEmbedSQLException("XJ020.S", new Object[] { typeName(n) }, StandardException.getSeverityFromIdentifier("XJ020.S"));
    }
    
    static SQLException newBatchUpdateException(final String reason, final String sqlState, final int n, final long[] array, final Throwable cause) {
        if (JVMInfo.JDK_ID >= 9) {
            try {
                return BatchUpdateException.class.getConstructor(String.class, String.class, Integer.TYPE, array.getClass(), Throwable.class).newInstance(reason, sqlState, new Integer(n), array, cause);
            }
            catch (Exception ex) {
                logError("\nERROR " + ex.getMessage() + "\n", ex);
            }
        }
        final BatchUpdateException ex2 = new BatchUpdateException(reason, sqlState, n, squashLongs(array));
        if (cause instanceof SQLException) {
            ex2.setNextException((SQLException)cause);
        }
        ex2.initCause(cause);
        return ex2;
    }
    
    public static int[] squashLongs(final long[] array) {
        final int n = (array == null) ? 0 : array.length;
        final int[] array2 = new int[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = (int)array[i];
        }
        return array2;
    }
    
    static IOException newIOException(final Throwable cause) {
        final IOException ex = new IOException(cause.getMessage());
        ex.initCause(cause);
        return ex;
    }
    
    public static void setExceptionFactory(final SQLExceptionFactory exceptionFactory) {
        Util.exceptionFactory = exceptionFactory;
    }
    
    public static SQLExceptionFactory getExceptionFactory() {
        return Util.exceptionFactory;
    }
    
    public static String typeName(final int i) {
        switch (i) {
            case 2003: {
                return "ARRAY";
            }
            case -7: {
                return "CHAR () FOR BIT DATA";
            }
            case 16: {
                return "BOOLEAN";
            }
            case 70: {
                return "DATALINK";
            }
            case -6: {
                return "TINYINT";
            }
            case 5: {
                return "SMALLINT";
            }
            case 4: {
                return "INTEGER";
            }
            case -5: {
                return "BIGINT";
            }
            case 6: {
                return "FLOAT";
            }
            case 7: {
                return "REAL";
            }
            case 8: {
                return "DOUBLE";
            }
            case 2: {
                return "NUMERIC";
            }
            case 3: {
                return "DECIMAL";
            }
            case 1: {
                return "CHAR";
            }
            case 12: {
                return "VARCHAR";
            }
            case -1: {
                return "LONGVARCHAR";
            }
            case 2005: {
                return "CLOB";
            }
            case 91: {
                return "DATE";
            }
            case 92: {
                return "TIME";
            }
            case 93: {
                return "TIMESTAMP";
            }
            case -2: {
                return "BINARY";
            }
            case -3: {
                return "VARBINARY";
            }
            case -4: {
                return "LONGVARBINARY";
            }
            case 2004: {
                return "BLOB";
            }
            case 1111: {
                return "OTHER";
            }
            case 2000: {
                return "Types.JAVA_OBJECT";
            }
            case 2006: {
                return "REF";
            }
            case 2012: {
                return "REF CURSOR";
            }
            case -8: {
                return "ROWID";
            }
            case 2002: {
                return "STRUCT";
            }
            case 456: {
                return "XML";
            }
            case 2009: {
                return "SQLXML";
            }
            default: {
                return String.valueOf(i);
            }
        }
    }
    
    static {
        Util.exceptionFactory = new SQLExceptionFactory();
        Util.logSeverityLevel = PropertyUtil.getSystemInt("derby.stream.error.logSeverityLevel", 40000);
    }
}
