// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.math.BigInteger;
import java.math.BigDecimal;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class DecimalRDBMSMapping extends AbstractDatastoreMapping
{
    private static final int INT_MAX_DECIMAL_DIGITS = 10;
    private static final int LONG_MAX_DECIMAL_DIGITS = 19;
    
    public DecimalRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        if (this.column != null && this.column.getColumnMetaData().getLength() == null) {
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                this.column.getColumnMetaData().setLength(10);
                this.column.checkDecimal();
            }
            else {
                this.column.getColumnMetaData().setLength(Math.min(this.getTypeInfo().getPrecision(), 19));
                this.column.checkDecimal();
            }
        }
        this.initTypeInfo();
    }
    
    @Override
    public boolean isDecimalBased() {
        return true;
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(3, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(3);
    }
    
    @Override
    public void setDouble(final PreparedStatement ps, final int param, final double value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055001", "double", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int param, final float value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055002", "float", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public double getDouble(final ResultSet rs, final int param) {
        double value;
        try {
            value = rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055002", "double", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public float getFloat(final ResultSet rs, final int param) {
        float value;
        try {
            value = (float)rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055001", "float", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = rs.getInt(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055001", "long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        long value;
        try {
            value = rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055002", "long", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setInt(param, Integer.valueOf(this.column.getDefaultValue().toString()));
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else if (value instanceof Integer) {
                ps.setBigDecimal(param, new BigDecimal((int)value));
            }
            else if (value instanceof Long) {
                ps.setBigDecimal(param, new BigDecimal((long)value));
            }
            else if (value instanceof BigDecimal) {
                ps.setBigDecimal(param, (BigDecimal)value);
            }
            else if (value instanceof Float) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Double) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof BigInteger) {
                ps.setBigDecimal(param, new BigDecimal((BigInteger)value));
            }
            else {
                ps.setInt(param, (int)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                value = rs.getBigDecimal(param);
                value = ((value == null) ? null : Integer.valueOf(((BigDecimal)value).toBigInteger().intValue()));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_LONG)) {
                value = rs.getBigDecimal(param);
                value = ((value == null) ? null : Long.valueOf(((BigDecimal)value).toBigInteger().longValue()));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_MATH_BIGINTEGER)) {
                value = rs.getBigDecimal(param);
                value = ((value == null) ? null : ((BigDecimal)value).toBigInteger());
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_MATH_BIGDECIMAL)) {
                value = rs.getBigDecimal(param);
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_FLOAT)) {
                final double d = rs.getDouble(param);
                value = (rs.wasNull() ? null : new Float(d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_DOUBLE)) {
                final double d = rs.getDouble(param);
                value = (rs.wasNull() ? null : new Double(d));
            }
            else {
                final int i = rs.getInt(param);
                value = (rs.wasNull() ? null : Integer.valueOf(i));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DecimalRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
