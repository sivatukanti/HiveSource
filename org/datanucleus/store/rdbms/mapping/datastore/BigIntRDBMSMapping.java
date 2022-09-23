// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import java.util.Date;
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

public class BigIntRDBMSMapping extends AbstractDatastoreMapping
{
    public BigIntRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
        }
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(-5, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(-5);
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value), e);
        }
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = (int)rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        long value;
        try {
            value = rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "long", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int exprIndex, final String value) {
        this.setLong(ps, exprIndex, Long.parseLong(value));
    }
    
    @Override
    public String getString(final ResultSet resultSet, final int exprIndex) {
        return Long.toString(this.getLong(resultSet, exprIndex));
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null && !StringUtils.isWhitespace(this.column.getDefaultValue().toString())) {
                    ps.setLong(param, Long.valueOf(this.column.getDefaultValue().toString().trim()));
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else if (value instanceof Character) {
                final String s = value.toString();
                ps.setInt(param, s.charAt(0));
            }
            else if (value instanceof String) {
                final String s = (String)value;
                ps.setInt(param, s.charAt(0));
            }
            else if (value instanceof Date) {
                ps.setLong(param, ((Date)value).getTime());
            }
            else {
                ps.setLong(param, ((Number)value).longValue());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final String str = rs.getString(param);
            if (rs.wasNull()) {
                value = null;
            }
            else {
                try {
                    value = Long.valueOf(str);
                }
                catch (NumberFormatException nfe) {
                    value = new Double(str).longValue();
                }
                if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_UTIL_DATE)) {
                    value = new Date((long)value);
                }
            }
        }
        catch (SQLException e) {
            final String msg = BigIntRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Long", "" + param, this.column, e.getMessage());
            throw new NucleusDataStoreException(msg, e);
        }
        return value;
    }
}
