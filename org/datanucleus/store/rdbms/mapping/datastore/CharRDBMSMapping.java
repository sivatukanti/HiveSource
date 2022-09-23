// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.ClassNameConstants;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class CharRDBMSMapping extends AbstractDatastoreMapping
{
    private static final ThreadLocal<FormatterInfo> formatterThreadInfo;
    
    public CharRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    protected void initialize() {
        if (this.column != null) {
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping && this.column.getColumnMetaData().getLength() == null) {
                final SingleFieldMapping m = (SingleFieldMapping)this.getJavaTypeMapping();
                if (m.getDefaultLength(0) > 0) {
                    this.column.getColumnMetaData().setLength(m.getDefaultLength(0));
                }
            }
            this.column.getColumnMetaData().setJdbcType("CHAR");
            this.column.checkString();
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping) {
                final Object[] validValues = ((SingleFieldMapping)this.getJavaTypeMapping()).getValidValues(0);
                if (validValues != null) {
                    final String constraints = this.getDatastoreAdapter().getCheckConstraintForValues(this.column.getIdentifier(), validValues, this.column.isNullable());
                    this.column.setConstraints(constraints);
                }
            }
            if (this.getJavaTypeMapping().getJavaType() == Boolean.class) {
                this.column.getColumnMetaData().setLength(1);
                final StringBuffer constraints2 = new StringBuffer("CHECK (" + this.column.getIdentifier() + " IN ('Y','N')");
                if (this.column.isNullable()) {
                    constraints2.append(" OR " + this.column.getIdentifier() + " IS NULL");
                }
                constraints2.append(')');
                this.column.setConstraints(constraints2.toString());
            }
            final SQLTypeInfo typeInfo = this.getTypeInfo();
            final int maxlength = typeInfo.getPrecision();
            if ((this.column.getColumnMetaData().getLength() <= 0 || this.column.getColumnMetaData().getLength() > maxlength) && typeInfo.isAllowsPrecisionSpec()) {
                throw new NucleusUserException("String max length of " + this.column.getColumnMetaData().getLength() + " is outside the acceptable range [0, " + maxlength + "] for column \"" + this.column.getIdentifier() + "\"");
            }
        }
        this.initTypeInfo();
    }
    
    @Override
    public boolean isStringBased() {
        return true;
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(1, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(1);
    }
    
    @Override
    public void setChar(final PreparedStatement ps, final int param, char value) {
        try {
            if (value == '\0' && !this.getDatastoreAdapter().supportsOption("PersistOfUnassignedChar")) {
                value = ' ';
                NucleusLogger.DATASTORE.warn(CharRDBMSMapping.LOCALISER_RDBMS.msg("055008"));
            }
            ps.setString(param, Character.valueOf(value).toString());
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "char", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public char getChar(final ResultSet rs, final int param) {
        char value;
        try {
            final String str = rs.getString(param);
            if (str == null) {
                return '\0';
            }
            value = str.charAt(0);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "char", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, String value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setString(param, this.column.getDefaultValue().toString().trim());
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else if (value.length() == 0) {
                if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.persistEmptyStringAsNull")) {
                    ps.setString(param, null);
                }
                else {
                    if (this.getDatastoreAdapter().supportsOption("NullEqualsEmptyString")) {
                        value = this.getDatastoreAdapter().getSurrogateForEmptyStrings();
                    }
                    ps.setString(param, value);
                }
            }
            else {
                if (this.column != null) {
                    final Integer colLength = this.column.getColumnMetaData().getLength();
                    if (colLength != null && colLength < value.length()) {
                        final String action = this.storeMgr.getStringProperty("datanucleus.rdbms.stringLengthExceededAction");
                        if (action.equals("EXCEPTION")) {
                            throw new NucleusUserException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055007", value, this.column.getIdentifier().toString(), "" + (int)colLength)).setFatal();
                        }
                        if (action.equals("TRUNCATE")) {
                            value = value.substring(0, colLength);
                        }
                    }
                }
                ps.setString(param, value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        try {
            String value = rs.getString(param);
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
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + param, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int param, final boolean value) {
        try {
            ps.setString(param, value ? "Y" : "N");
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "boolean", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public boolean getBoolean(final ResultSet rs, final int param) {
        boolean value;
        try {
            final String s = rs.getString(param);
            if (s == null) {
                if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                    throw new NullValueException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                return false;
            }
            else if (s.equals("Y")) {
                value = true;
            }
            else {
                if (!s.equals("N")) {
                    throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                value = false;
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "boolean", "" + param, this.column, e.getMessage()), e);
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
                ps.setString(param, ((boolean)value) ? "Y" : "N");
            }
            else if (value instanceof Time) {
                ps.setString(param, ((Time)value).toString());
            }
            else if (value instanceof Date) {
                ps.setString(param, ((Date)value).toString());
            }
            else if (value instanceof java.util.Date) {
                ps.setString(param, this.getJavaUtilDateFormat().format((java.util.Date)value));
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
                ps.setString(param, (String)value);
            }
            else {
                ps.setString(param, value.toString());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final String s = rs.getString(param);
            if (s == null) {
                value = null;
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BOOLEAN)) {
                if (s.equals("Y")) {
                    value = Boolean.TRUE;
                }
                else {
                    if (!s.equals("N")) {
                        throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
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
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        catch (ParseException e2) {
            throw new NucleusDataStoreException(CharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e2.getMessage()), e2);
        }
        return value;
    }
    
    public SimpleDateFormat getJavaUtilDateFormat() {
        final FormatterInfo formatInfo = CharRDBMSMapping.formatterThreadInfo.get();
        if (formatInfo.formatter == null) {
            final Calendar cal = this.storeMgr.getCalendarForDateTimezone();
            formatInfo.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            if (cal != null) {
                formatInfo.formatter.setTimeZone(cal.getTimeZone());
            }
        }
        return formatInfo.formatter;
    }
    
    static {
        formatterThreadInfo = new ThreadLocal<FormatterInfo>() {
            @Override
            protected FormatterInfo initialValue() {
                return new FormatterInfo();
            }
        };
    }
    
    static class FormatterInfo
    {
        SimpleDateFormat formatter;
    }
}
