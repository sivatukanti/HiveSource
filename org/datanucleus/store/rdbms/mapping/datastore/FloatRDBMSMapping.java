// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class FloatRDBMSMapping extends DoubleRDBMSMapping
{
    public FloatRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(6, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(6);
    }
    
    @Override
    public float getFloat(final ResultSet rs, final int param) {
        float value;
        try {
            value = rs.getFloat(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(FloatRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FloatRDBMSMapping.LOCALISER_RDBMS.msg("055001", "float", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int param, final float value) {
        try {
            ps.setFloat(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FloatRDBMSMapping.LOCALISER_RDBMS.msg("055002", "float", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Integer) {
                ps.setFloat(param, (float)value);
            }
            else if (value instanceof Long) {
                ps.setFloat(param, (float)value);
            }
            else if (value instanceof Short) {
                ps.setFloat(param, (float)value);
            }
            else if (value instanceof BigInteger) {
                ps.setFloat(param, ((BigInteger)value).floatValue());
            }
            else if (value instanceof BigDecimal) {
                ps.setFloat(param, ((BigDecimal)value).floatValue());
            }
            else if (value instanceof Character) {
                final String s = value.toString();
                ps.setFloat(param, s.charAt(0));
            }
            else if (value instanceof Float) {
                ps.setFloat(param, (float)value);
            }
            else if (value instanceof Double) {
                ps.setDouble(param, (double)value);
            }
            else {
                ps.setFloat(param, ((Double)value).floatValue());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FloatRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final float d = rs.getFloat(param);
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                value = (rs.wasNull() ? null : Integer.valueOf((int)d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_LONG)) {
                value = (rs.wasNull() ? null : Long.valueOf((long)d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_FLOAT)) {
                value = (rs.wasNull() ? null : new Float(d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_DOUBLE)) {
                final double dbl = rs.getDouble(param);
                value = (rs.wasNull() ? null : Double.valueOf(dbl));
            }
            else {
                value = (rs.wasNull() ? null : Double.valueOf(d));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FloatRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
