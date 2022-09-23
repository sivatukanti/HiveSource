// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.text.ParseException;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.ClassNameConstants;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class NCharRDBMSMapping extends CharRDBMSMapping
{
    public NCharRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(-15, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(-15);
    }
    
    @Override
    public void setChar(final PreparedStatement ps, final int param, char value) {
        try {
            if (value == '\0' && !this.getDatastoreAdapter().supportsOption("PersistOfUnassignedChar")) {
                value = ' ';
                NucleusLogger.DATASTORE.warn(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055008"));
            }
            ps.setNString(param, Character.valueOf(value).toString());
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "char", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public char getChar(final ResultSet rs, final int param) {
        char value;
        try {
            value = rs.getNString(param).charAt(0);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "char", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, String value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setNString(param, this.column.getDefaultValue().toString().trim());
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else if (value.length() == 0) {
                if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.persistEmptyStringAsNull")) {
                    ps.setNString(param, null);
                }
                else {
                    if (this.getDatastoreAdapter().supportsOption("NullEqualsEmptyString")) {
                        value = this.getDatastoreAdapter().getSurrogateForEmptyStrings();
                    }
                    ps.setNString(param, value);
                }
            }
            else {
                if (this.column != null) {
                    final Integer colLength = this.column.getColumnMetaData().getLength();
                    if (colLength != null && colLength < value.length()) {
                        final String action = this.storeMgr.getStringProperty("datanucleus.rdbms.stringLengthExceededAction");
                        if (action.equals("EXCEPTION")) {
                            throw new NucleusUserException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055007", value, this.column.getIdentifier().toString(), "" + (int)colLength)).setFatal();
                        }
                        if (action.equals("TRUNCATE")) {
                            value = value.substring(0, colLength);
                        }
                    }
                }
                ps.setNString(param, value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        try {
            String value = rs.getNString(param);
            if (value == null) {
                return value;
            }
            if (this.getDatastoreAdapter().supportsOption("NullEqualsEmptyString") && value.equals(this.getDatastoreAdapter().getSurrogateForEmptyStrings())) {
                return "";
            }
            if (this.column.getJdbcType() == 1 && this.getDatastoreAdapter().supportsOption("CharColumnsPaddedWithSpaces")) {
                int numPaddingChars = 0;
                for (int i = value.length() - 1; i >= 0 && value.charAt(i) == ' '; --i) {
                    ++numPaddingChars;
                }
                if (numPaddingChars > 0) {
                    value = value.substring(0, value.length() - numPaddingChars);
                }
            }
            return value;
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + param, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int param, final boolean value) {
        try {
            ps.setNString(param, value ? "Y" : "N");
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "boolean", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public boolean getBoolean(final ResultSet rs, final int param) {
        boolean value;
        try {
            final String s = rs.getNString(param);
            if (s == null) {
                if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                    throw new NullValueException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                return false;
            }
            else if (s.equals("Y")) {
                value = true;
            }
            else {
                if (!s.equals("N")) {
                    throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                value = false;
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "boolean", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Boolean) {
                ps.setNString(param, ((boolean)value) ? "Y" : "N");
            }
            else if (value instanceof Time) {
                ps.setNString(param, ((Time)value).toString());
            }
            else if (value instanceof Date) {
                ps.setNString(param, ((Date)value).toString());
            }
            else if (value instanceof java.util.Date) {
                ps.setNString(param, this.getJavaUtilDateFormat().format((java.util.Date)value));
            }
            else if (value instanceof Timestamp) {
                final Calendar cal = this.storeMgr.getCalendarForDateTimezone();
                if (cal != null) {
                    ps.setTimestamp(param, (Timestamp)value, cal);
                }
                else {
                    ps.setTimestamp(param, (Timestamp)value);
                }
            }
            else if (value instanceof String) {
                ps.setNString(param, (String)value);
            }
            else {
                ps.setNString(param, value.toString());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final String s = rs.getNString(param);
            if (s == null) {
                value = null;
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BOOLEAN)) {
                if (s.equals("Y")) {
                    value = Boolean.TRUE;
                }
                else {
                    if (!s.equals("N")) {
                        throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                    }
                    value = Boolean.FALSE;
                }
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_CHARACTER)) {
                value = s.charAt(0);
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_SQL_TIME)) {
                value = Time.valueOf(s);
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_SQL_DATE)) {
                value = Date.valueOf(s);
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_UTIL_DATE)) {
                value = this.getJavaUtilDateFormat().parse(s);
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_SQL_TIMESTAMP)) {
                final Calendar cal = this.storeMgr.getCalendarForDateTimezone();
                value = TypeConversionHelper.stringToTimestamp(s, cal);
            }
            else {
                value = s;
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        catch (ParseException e2) {
            throw new NucleusDataStoreException(NCharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e2.getMessage()), e2);
        }
        return value;
    }
}
