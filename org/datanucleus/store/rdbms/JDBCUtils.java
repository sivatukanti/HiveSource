// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.Connection;
import org.datanucleus.util.NucleusLogger;
import java.sql.SQLWarning;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class JDBCUtils
{
    private static final Localiser LOCALISER;
    private static Map supportedJdbcTypesById;
    private static Map unsupportedJdbcTypesById;
    
    public static final int[] getJDBCTypes() {
        final int[] types = new int[JDBCUtils.supportedJdbcTypesById.size() + JDBCUtils.unsupportedJdbcTypesById.size()];
        int i = 0;
        final Iterator suppIter = JDBCUtils.supportedJdbcTypesById.keySet().iterator();
        while (suppIter.hasNext()) {
            types[i++] = suppIter.next();
        }
        final Iterator unsuppIter = JDBCUtils.unsupportedJdbcTypesById.keySet().iterator();
        while (unsuppIter.hasNext()) {
            types[i++] = unsuppIter.next();
        }
        return types;
    }
    
    public static String getNameForJDBCType(final int jdbcType) {
        String typeName = JDBCUtils.supportedJdbcTypesById.get(jdbcType);
        if (typeName == null) {
            typeName = JDBCUtils.unsupportedJdbcTypesById.get(jdbcType);
        }
        return typeName;
    }
    
    public static int getJDBCTypeForName(final String typeName) {
        if (typeName == null) {
            return 0;
        }
        final Set entries = JDBCUtils.supportedJdbcTypesById.entrySet();
        for (final Map.Entry entry : entries) {
            if (typeName.equalsIgnoreCase(entry.getValue())) {
                return entry.getKey();
            }
        }
        return 0;
    }
    
    public static String getSubprotocolForURL(final String url) {
        final StringTokenizer tokeniser = new StringTokenizer(url, ":");
        tokeniser.nextToken();
        return tokeniser.nextToken();
    }
    
    public static void logWarnings(SQLWarning warning) {
        while (warning != null) {
            NucleusLogger.DATASTORE.warn(JDBCUtils.LOCALISER.msg("052700", warning.getMessage()));
            warning = warning.getNextWarning();
        }
    }
    
    public static void logWarnings(final Connection conn) {
        try {
            logWarnings(conn.getWarnings());
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(JDBCUtils.LOCALISER.msg("052701", conn), e);
        }
    }
    
    public static void logWarnings(final Statement stmt) {
        try {
            logWarnings(stmt.getWarnings());
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(JDBCUtils.LOCALISER.msg("052702", stmt), e);
        }
    }
    
    public static void logWarnings(final ResultSet rs) {
        try {
            logWarnings(rs.getWarnings());
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(JDBCUtils.LOCALISER.msg("052703", rs), e);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
        JDBCUtils.supportedJdbcTypesById = new HashMap();
        JDBCUtils.unsupportedJdbcTypesById = new HashMap();
        JDBCUtils.supportedJdbcTypesById.put(-5, "BIGINT");
        JDBCUtils.supportedJdbcTypesById.put(-7, "BIT");
        JDBCUtils.supportedJdbcTypesById.put(2004, "BLOB");
        JDBCUtils.supportedJdbcTypesById.put(16, "BOOLEAN");
        JDBCUtils.supportedJdbcTypesById.put(1, "CHAR");
        JDBCUtils.supportedJdbcTypesById.put(2005, "CLOB");
        JDBCUtils.supportedJdbcTypesById.put(70, "DATALINK");
        JDBCUtils.supportedJdbcTypesById.put(91, "DATE");
        JDBCUtils.supportedJdbcTypesById.put(3, "DECIMAL");
        JDBCUtils.supportedJdbcTypesById.put(8, "DOUBLE");
        JDBCUtils.supportedJdbcTypesById.put(6, "FLOAT");
        JDBCUtils.supportedJdbcTypesById.put(4, "INTEGER");
        JDBCUtils.supportedJdbcTypesById.put(-4, "LONGVARBINARY");
        JDBCUtils.supportedJdbcTypesById.put(-1, "LONGVARCHAR");
        JDBCUtils.supportedJdbcTypesById.put(2, "NUMERIC");
        JDBCUtils.supportedJdbcTypesById.put(7, "REAL");
        JDBCUtils.supportedJdbcTypesById.put(5, "SMALLINT");
        JDBCUtils.supportedJdbcTypesById.put(92, "TIME");
        JDBCUtils.supportedJdbcTypesById.put(93, "TIMESTAMP");
        JDBCUtils.supportedJdbcTypesById.put(-6, "TINYINT");
        JDBCUtils.supportedJdbcTypesById.put(-3, "VARBINARY");
        JDBCUtils.supportedJdbcTypesById.put(12, "VARCHAR");
        JDBCUtils.supportedJdbcTypesById.put(-9, "NVARCHAR");
        JDBCUtils.supportedJdbcTypesById.put(-15, "NCHAR");
        JDBCUtils.supportedJdbcTypesById.put(2011, "NCLOB");
        JDBCUtils.unsupportedJdbcTypesById.put(2003, "ARRAY");
        JDBCUtils.unsupportedJdbcTypesById.put(-2, "BINARY");
        JDBCUtils.unsupportedJdbcTypesById.put(2001, "DISTINCT");
        JDBCUtils.unsupportedJdbcTypesById.put(2000, "JAVA_OBJECT");
        JDBCUtils.unsupportedJdbcTypesById.put(0, "NULL");
        JDBCUtils.unsupportedJdbcTypesById.put(1111, "OTHER");
        JDBCUtils.unsupportedJdbcTypesById.put(2006, "REF");
        JDBCUtils.unsupportedJdbcTypesById.put(2002, "STRUCT");
    }
}
