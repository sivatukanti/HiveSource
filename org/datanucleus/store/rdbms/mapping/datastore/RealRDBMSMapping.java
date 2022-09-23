// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.store.rdbms.exceptions.NullValueException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class RealRDBMSMapping extends AbstractDatastoreMapping
{
    public RealRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
            return this.storeMgr.getSQLTypeInfoForJDBCType(7, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(7);
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int param, final float value) {
        try {
            ps.setFloat(param, value);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055001", "float", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public float getFloat(final ResultSet rs, final int param) {
        float value;
        try {
            value = rs.getFloat(param);
            if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                throw new NullValueException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
            }
        }
        catch (SQLException e) {
            try {
                value = Float.parseFloat(rs.getString(param));
                if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                    throw new NullValueException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                return value;
            }
            catch (SQLException e2) {
                try {
                    throw new NucleusDataStoreException("Can't get float result: param = " + param + " - " + rs.getString(param), e);
                }
                catch (SQLException e3) {
                    throw new NucleusDataStoreException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055002", "float", "" + param, this.column, e.getMessage()), e);
                }
            }
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
                ps.setFloat(param, (float)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object value;
        try {
            final float f = rs.getFloat(param);
            value = (rs.wasNull() ? null : new Float(f));
        }
        catch (SQLException e) {
            try {
                value = new Float(Float.parseFloat(rs.getString(param)));
                if ((this.column == null || this.column.getColumnMetaData() == null || !this.column.getColumnMetaData().isAllowsNull()) && rs.wasNull()) {
                    throw new NullValueException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055003", this.column));
                }
                return value;
            }
            catch (SQLException e2) {
                try {
                    throw new NucleusDataStoreException("Can't get float result: param = " + param + " - " + rs.getString(param), e);
                }
                catch (SQLException e3) {
                    throw new NucleusDataStoreException(RealRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
                }
            }
        }
        return value;
    }
}
