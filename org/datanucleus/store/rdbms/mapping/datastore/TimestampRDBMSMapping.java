// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.util.TypeConversionHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.Calendar;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class TimestampRDBMSMapping extends AbstractDatastoreMapping
{
    public TimestampRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        if (this.column != null) {
            this.column.checkPrimitive();
        }
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(93, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(93);
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            final Calendar cal = this.storeMgr.getCalendarForDateTimezone();
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Date) {
                if (cal != null) {
                    ps.setTimestamp(param, new Timestamp(((Date)value).getTime()), cal);
                }
                else {
                    ps.setTimestamp(param, new Timestamp(((Date)value).getTime()));
                }
            }
            else if (value instanceof Time) {
                if (cal != null) {
                    ps.setTimestamp(param, new Timestamp(((Time)value).getTime()), cal);
                }
                else {
                    ps.setTimestamp(param, new Timestamp(((Time)value).getTime()));
                }
            }
            else if (value instanceof java.sql.Date) {
                if (cal != null) {
                    ps.setTimestamp(param, new Timestamp(((java.sql.Date)value).getTime()), cal);
                }
                else {
                    ps.setTimestamp(param, new Timestamp(((java.sql.Date)value).getTime()));
                }
            }
            else if (value instanceof Calendar) {
                if (cal != null) {
                    ps.setTimestamp(param, new Timestamp(((Calendar)value).getTime().getTime()), cal);
                }
                else {
                    ps.setTimestamp(param, new Timestamp(((Calendar)value).getTime().getTime()));
                }
            }
            else if (cal != null) {
                ps.setTimestamp(param, (Timestamp)value, cal);
            }
            else {
                ps.setTimestamp(param, (Timestamp)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TimestampRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Timestamp", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    protected Timestamp getTimestamp(final ResultSet rs, final int param) {
        final Calendar cal = this.storeMgr.getCalendarForDateTimezone();
        Timestamp value;
        try {
            if (cal != null) {
                value = rs.getTimestamp(param, cal);
            }
            else {
                value = rs.getTimestamp(param);
            }
        }
        catch (SQLException e) {
            try {
                final String s = rs.getString(param);
                if (rs.wasNull()) {
                    value = null;
                }
                else {
                    value = ((s == null) ? null : TypeConversionHelper.stringToTimestamp(s, cal));
                }
            }
            catch (SQLException nestedEx) {
                throw new NucleusDataStoreException(TimestampRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Timestamp", "" + param, this.column, e.getMessage()), nestedEx);
            }
        }
        return value;
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        final Timestamp value = this.getTimestamp(rs, param);
        if (value == null) {
            return null;
        }
        if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_UTIL_DATE)) {
            return new Date(this.getDatastoreAdapter().getAdapterTime(value));
        }
        if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_SQL_DATE)) {
            return new java.sql.Date(this.getDatastoreAdapter().getAdapterTime(value));
        }
        if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_SQL_TIME)) {
            return new Time(this.getDatastoreAdapter().getAdapterTime(value));
        }
        return new Timestamp(this.getDatastoreAdapter().getAdapterTime(value));
    }
}
