// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
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

public class IntegerRDBMSMapping extends AbstractDatastoreMapping
{
    public IntegerRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
            return this.storeMgr.getSQLTypeInfoForJDBCType(4, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(4);
    }
    
    @Override
    public void setChar(final PreparedStatement ps, final int param, final char value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055001", "char", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public char getChar(final ResultSet rs, final int param) {
        char value;
        try {
            value = (char)rs.getInt(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055002", "char", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int param, final int value) {
        try {
            ps.setInt(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055001", "int", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public int getInt(final ResultSet rs, final int param) {
        int value;
        try {
            value = rs.getInt(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055002", "int", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int param, final long value) {
        try {
            ps.setLong(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055001", "long", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public long getLong(final ResultSet rs, final int param) {
        long value;
        try {
            value = rs.getLong(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055002", "long", "" + param, this.column, e.getMessage()), e);
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
            else {
                ps.setLong(param, ((Number)value).longValue());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final long i = rs.getLong(param);
            if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_CHARACTER)) {
                value = (rs.wasNull() ? null : Character.valueOf((char)i));
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_STRING)) {
                value = (rs.wasNull() ? null : Character.valueOf((char)i).toString());
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_LONG)) {
                value = (rs.wasNull() ? null : Long.valueOf(i));
            }
            else {
                value = (rs.wasNull() ? null : Integer.valueOf((int)i));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(IntegerRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
