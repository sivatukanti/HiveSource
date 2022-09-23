// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class DoubleRDBMSMapping extends AbstractDatastoreMapping
{
    public DoubleRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
    public boolean isDecimalBased() {
        return true;
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(8, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(8);
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = (int)rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055001", "long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        long value;
        try {
            value = rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055002", "long", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setDouble(final PreparedStatement ps, final int param, final double value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055001", "double", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public double getDouble(final ResultSet rs, final int param) {
        double value;
        try {
            value = rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055002", "double", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public float getFloat(final ResultSet rs, final int param) {
        float value;
        try {
            value = (float)rs.getDouble(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055001", "float", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int param, final float value) {
        try {
            ps.setDouble(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055002", "float", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Integer) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Long) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Short) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Float) {
                ps.setDouble(param, (double)value);
            }
            else if (value instanceof Character) {
                final String s = value.toString();
                ps.setDouble(param, s.charAt(0));
            }
            else if (value instanceof BigInteger) {
                ps.setDouble(param, ((BigInteger)value).doubleValue());
            }
            else if (value instanceof BigDecimal) {
                ps.setDouble(param, ((BigDecimal)value).doubleValue());
            }
            else {
                ps.setDouble(param, (double)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final double d = rs.getDouble(param);
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
                value = (rs.wasNull() ? null : Integer.valueOf((int)d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_LONG)) {
                value = (rs.wasNull() ? null : Long.valueOf((long)d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_FLOAT)) {
                value = (rs.wasNull() ? null : new Float(d));
            }
            else {
                value = (rs.wasNull() ? null : Double.valueOf(d));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DoubleRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
