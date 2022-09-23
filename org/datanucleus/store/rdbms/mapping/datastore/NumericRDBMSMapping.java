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
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class NumericRDBMSMapping extends AbstractDatastoreMapping
{
    private static final int INT_MAX_DECIMAL_DIGITS = 10;
    
    public NumericRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        if (this.column != null) {
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping) {
                final Object[] validValues = ((SingleFieldMapping)this.getJavaTypeMapping()).getValidValues(0);
                if (validValues != null) {
                    final String constraints = this.storeMgr.getDatastoreAdapter().getCheckConstraintForValues(this.column.getIdentifier(), validValues, this.column.isNullable());
                    this.column.setConstraints(constraints);
                }
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                if (this.column.getColumnMetaData().getLength() == null) {
                    this.column.getColumnMetaData().setLength(10);
                }
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BOOLEAN)) {
                this.column.getColumnMetaData().setLength(1);
                final StringBuffer constraints2 = new StringBuffer("CHECK (" + this.column.getIdentifier() + " IN (1,0)");
                if (this.column.isNullable()) {
                    constraints2.append(" OR " + this.column.getIdentifier() + " IS NULL");
                }
                constraints2.append(')');
                this.column.setConstraints(constraints2.toString());
                this.column.checkDecimal();
            }
        }
        this.initTypeInfo();
    }
    
    @Override
    public boolean isIntegerBased() {
        return true;
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(2, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(2);
    }
    
    @Override
    public void setChar(final PreparedStatement ps, final int param, final char value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "char", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public char getChar(final ResultSet rs, final int param) {
        char value;
        try {
            value = (char)rs.getInt(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "char", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = rs.getInt(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setByte(final PreparedStatement ps, final int param, final byte value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "byte", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public byte getByte(final ResultSet rs, final int param) {
        byte value;
        try {
            value = rs.getByte(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "byte", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int param, final boolean value) {
        try {
            ps.setBoolean(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "boolean", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public boolean getBoolean(final ResultSet rs, final int param) {
        boolean value;
        try {
            value = rs.getBoolean(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "boolean", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setDouble(final PreparedStatement ps, final int param, final double value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "double", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public double getDouble(final ResultSet rs, final int param) {
        double value;
        try {
            value = rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "double", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int param, final float value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "float", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public float getFloat(final ResultSet rs, final int param) {
        float value;
        try {
            value = (float)rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "float", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setShort(final PreparedStatement ps, final int param, final short value) {
        try {
            ps.setShort(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "short", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public short getShort(final ResultSet rs, final int param) {
        short value;
        try {
            value = rs.getShort(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "short", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        long value;
        try {
            value = rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "long", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Byte) {
                ps.setInt(param, (byte)value);
            }
            else if (value instanceof Integer) {
                ps.setInt(param, (int)value);
            }
            else if (value instanceof Character) {
                final String s = value.toString();
                ps.setInt(param, s.charAt(0));
            }
            else if (value instanceof String) {
                final String s = (String)value;
                ps.setInt(param, s.charAt(0));
            }
            else if (value instanceof Long) {
                ps.setLong(param, (long)value);
            }
            else if (value instanceof Float) {
                ps.setFloat(param, (float)value);
            }
            else if (value instanceof Double) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Short) {
                ps.setShort(param, (short)value);
            }
            else if (value instanceof BigDecimal) {
                ps.setBigDecimal(param, (BigDecimal)value);
            }
            else if (value instanceof Boolean) {
                ps.setBoolean(param, (boolean)value);
            }
            else {
                ps.setBigDecimal(param, new BigDecimal((BigInteger)value));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Numeric", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        try {
            final BigDecimal value = rs.getBigDecimal(param);
            if (value == null) {
                return null;
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(Number.class.getName())) {
                return value;
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_MATH_BIGINTEGER)) {
                return value.toBigInteger();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                return value.intValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_LONG)) {
                return value.longValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BOOLEAN)) {
                return value.intValue() == 1;
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BYTE)) {
                return value.byteValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_SHORT)) {
                return value.shortValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_FLOAT)) {
                return value.floatValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_DOUBLE)) {
                return value.doubleValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_CHARACTER)) {
                return (char)value.intValue();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_STRING)) {
                return Character.valueOf((char)value.intValue()).toString();
            }
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_MATH_BIGDECIMAL)) {
                return value;
            }
            return value.longValue();
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(NumericRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Numeric", "" + param, this.column, e.getMessage()), e);
        }
    }
}
