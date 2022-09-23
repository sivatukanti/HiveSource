// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class LongVarcharRDBMSMapping extends AbstractDatastoreMapping
{
    public LongVarcharRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    @Override
    public boolean isStringBased() {
        return true;
    }
    
    private void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(-1, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(-1);
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, final String value) {
        try {
            if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setString(param, this.column.getDefaultValue().toString().trim());
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else {
                ps.setString(param, value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(LongVarcharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        String value;
        try {
            value = rs.getString(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(LongVarcharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "String", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else {
                ps.setString(param, (String)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(LongVarcharRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final String s = rs.getString(param);
            value = (rs.wasNull() ? null : s);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(LongVarcharRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
