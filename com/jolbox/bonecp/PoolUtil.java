// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Array;
import java.sql.Ref;
import java.sql.Clob;
import java.sql.Blob;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class PoolUtil
{
    protected static Class<Throwable> sqlExceptionClass;
    private static final String exceptionClass = "java.sql.SQLException";
    
    public static String fillLogParams(final String sql, final Map<Object, Object> logParams) {
        final StringBuilder result = new StringBuilder();
        final Map<Object, Object> tmpLogParam = (logParams == null) ? new HashMap<Object, Object>() : logParams;
        final Iterator<Object> it = tmpLogParam.values().iterator();
        boolean inQuote = false;
        boolean inQuote2 = false;
        final char[] sqlChar = (sql != null) ? sql.toCharArray() : new char[0];
        for (int i = 0; i < sqlChar.length; ++i) {
            if (sqlChar[i] == '\'') {
                inQuote = !inQuote;
            }
            if (sqlChar[i] == '\"') {
                inQuote2 = !inQuote2;
            }
            if (sqlChar[i] == '?' && !inQuote && !inQuote2) {
                if (it.hasNext()) {
                    result.append(prettyPrint(it.next()));
                }
                else {
                    result.append('?');
                }
            }
            else {
                result.append(sqlChar[i]);
            }
        }
        return result.toString();
    }
    
    protected static String safePrint(final Object... o) {
        final StringBuilder sb = new StringBuilder();
        for (final Object obj : o) {
            sb.append((obj != null) ? obj : "null");
        }
        return sb.toString();
    }
    
    protected static String prettyPrint(final Object obj) {
        final StringBuilder sb = new StringBuilder();
        if (obj == null) {
            sb.append("NULL");
        }
        else if (obj instanceof Blob) {
            sb.append(formatLogParam((Blob)obj));
        }
        else if (obj instanceof Clob) {
            sb.append(formatLogParam((Clob)obj));
        }
        else if (obj instanceof Ref) {
            sb.append(formatLogParam((Ref)obj));
        }
        else if (obj instanceof Array) {
            sb.append(formatLogParam((Array)obj));
        }
        else if (obj instanceof String) {
            sb.append("'" + obj.toString() + "'");
        }
        else {
            sb.append(obj.toString());
        }
        return sb.toString();
    }
    
    private static String formatLogParam(final Blob obj) {
        String result = "";
        try {
            result = "(blob of length " + obj.length() + ")";
        }
        catch (SQLException e) {
            result = "(blob of unknown length)";
        }
        return result;
    }
    
    private static String formatLogParam(final Clob obj) {
        String result = "";
        try {
            result = "(cblob of length " + obj.length() + ")";
        }
        catch (SQLException e) {
            result = "(cblob of unknown length)";
        }
        return result;
    }
    
    private static String formatLogParam(final Array obj) {
        String result = "";
        try {
            result = "(array of type" + obj.getBaseTypeName().length() + ")";
        }
        catch (SQLException e) {
            result = "(array of unknown type)";
        }
        return result;
    }
    
    private static String formatLogParam(final Ref obj) {
        String result = "";
        try {
            result = "(ref of type" + obj.getBaseTypeName().length() + ")";
        }
        catch (SQLException e) {
            result = "(ref of unknown type)";
        }
        return result;
    }
    
    public static String stringifyException(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String result = "";
        result = "------\r\n" + sw.toString() + "------\r\n";
        return result;
    }
    
    public static SQLException generateSQLException(final String reason, final Throwable t) {
        try {
            if (PoolUtil.sqlExceptionClass == null) {
                PoolUtil.sqlExceptionClass = (Class<Throwable>)Class.forName("java.sql.SQLException");
            }
            return PoolUtil.sqlExceptionClass.getConstructor(String.class, Throwable.class).newInstance(reason, t);
        }
        catch (Exception e) {
            return new SQLException(stringifyException(t));
        }
    }
}
