// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class BooleanRDBMSMapping extends AbstractDatastoreMapping
{
    public BooleanRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public boolean isBooleanBased() {
        return true;
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(16, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(16);
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int param, final boolean value) {
        try {
            ps.setBoolean(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055001", "boolean", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public boolean getBoolean(final ResultSet rs, final int param) {
        boolean value;
        try {
            value = rs.getBoolean(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Boolean", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, final String value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else {
                ps.setBoolean(param, value.equals("Y"));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        String value;
        try {
            value = (rs.getBoolean(param) ? "Y" : "N");
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055002", "String", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof String) {
                ps.setBoolean(param, value.equals("Y"));
            }
            else {
                if (!(value instanceof Boolean)) {
                    throw new NucleusUserException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055004", value, this.column));
                }
                ps.setBoolean(param, (boolean)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final boolean b = rs.getBoolean(param);
            if (rs.wasNull()) {
                value = null;
            }
            else if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_LANG_STRING)) {
                value = (b ? "Y" : "N");
            }
            else {
                value = (b ? Boolean.TRUE : Boolean.FALSE);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BooleanRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
