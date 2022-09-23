// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.Date;
import java.sql.Time;
import java.util.Calendar;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class TimeRDBMSMapping extends AbstractDatastoreMapping
{
    public TimeRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(92, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(92);
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Calendar) {
                ps.setTime(param, new Time(((Calendar)value).getTime().getTime()));
            }
            else if (value instanceof Date) {
                ps.setTime(param, new Time(((Date)value).getTime()));
            }
            else {
                ps.setTime(param, (Time)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TimeRDBMSMapping.LOCALISER_RDBMS.msg("055001", "java.sql.Time", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    protected Time getTime(final ResultSet rs, final int param) {
        Time value;
        try {
            value = rs.getTime(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(TimeRDBMSMapping.LOCALISER_RDBMS.msg("055002", "java.sql.Time", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        final Time value = this.getTime(rs, param);
        if (value == null) {
            return null;
        }
        return value;
    }
}
