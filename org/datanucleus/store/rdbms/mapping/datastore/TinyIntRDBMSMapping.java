// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusException;
import java.math.BigInteger;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class TinyIntRDBMSMapping extends AbstractDatastoreMapping
{
    public TinyIntRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        if (this.column != null) {
            this.column.checkPrimitive();
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping) {
                final Object[] validValues = ((SingleFieldMapping)this.getJavaTypeMapping()).getValidValues(0);
                if (validValues != null) {
                    final String constraints = this.storeMgr.getDatastoreAdapter().getCheckConstraintForValues(this.column.getIdentifier(), validValues, this.column.isNullable());
                    this.column.setConstraints(constraints);
                }
            }
            if (this.getJavaTypeMapping().getJavaType() == Boolean.class) {
                final StringBuffer constraints2 = new StringBuffer("CHECK (" + this.column.getIdentifier() + " IN (0,1)");
                if (this.column.isNullable()) {
                    constraints2.append(" OR " + this.column.getIdentifier() + " IS NULL");
                }
                constraints2.append(')');
                this.column.setConstraints(constraints2.toString());
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
            return this.storeMgr.getSQLTypeInfoForJDBCType(-6, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(-6);
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int param, final boolean value) {
        try {
            ps.setInt(param, value ? 1 : 0);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "boolean", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public boolean getBoolean(final ResultSet rs, final int param) {
        boolean value;
        try {
            final int intValue = rs.getInt(param);
            if (intValue == 0) {
                value = false;
            }
            else {
                if (intValue != 1) {
                    throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055006", "Types.TINYINT", "" + intValue));
                }
                value = true;
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Boolean", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = rs.getInt(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        int value;
        try {
            value = rs.getInt(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setByte(final PreparedStatement ps, final int param, final byte value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "byte", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public byte getByte(final ResultSet rs, final int param) {
        byte value;
        try {
            value = rs.getByte(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "byte", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null && !StringUtils.isWhitespace(this.column.getDefaultValue().toString())) {
                    ps.setInt(param, Integer.valueOf(this.column.getDefaultValue().toString()));
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else if (value instanceof Byte) {
                ps.setInt(param, (short)value);
            }
            else if (value instanceof BigInteger) {
                ps.setInt(param, ((BigInteger)value).shortValue());
            }
            else {
                if (!(value instanceof Boolean)) {
                    throw new NucleusException("TinyIntRDBMSMapping.setObject called for " + StringUtils.toJVMIDString(value) + " but not supported");
                }
                ps.setInt(param, ((boolean)value) ? 1 : 0);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Byte", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final int d = rs.getInt(param);
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_MATH_BIGINTEGER)) {
                value = (rs.wasNull() ? null : BigInteger.valueOf(d));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_BOOLEAN)) {
                value = (rs.wasNull() ? null : ((d == 1) ? Boolean.TRUE : Boolean.FALSE));
            }
            else {
                value = (rs.wasNull() ? null : Byte.valueOf(rs.getByte(param)));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TinyIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Byte", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
