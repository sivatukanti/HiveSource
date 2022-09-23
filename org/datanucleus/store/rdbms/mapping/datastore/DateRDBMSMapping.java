// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassNameConstants;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.Date;
import java.util.Calendar;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class DateRDBMSMapping extends AbstractDatastoreMapping
{
    public DateRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
            return this.storeMgr.getSQLTypeInfoForJDBCType(91, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(91);
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, this.getTypeInfo().getDataType());
            }
            else if (value instanceof Calendar) {
                ps.setDate(param, new Date(((Calendar)value).getTime().getTime()));
            }
            else if (value instanceof java.util.Date) {
                ps.setDate(param, new Date(((java.util.Date)value).getTime()));
            }
            else {
                ps.setDate(param, (Date)value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DateRDBMSMapping.LOCALISER_RDBMS.msg("055001", "java.sql.Date", "" + value), e);
        }
    }
    
    protected Date getDate(final ResultSet rs, final int param) {
        Date value;
        try {
            value = rs.getDate(param);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(DateRDBMSMapping.LOCALISER_RDBMS.msg("055002", "java.sql.Date", "" + param), e);
        }
        return value;
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        final Date value = this.getDate(rs, param);
        if (value == null) {
            return null;
        }
        if (this.getJavaTypeMapping().getJavaType().getName().equals(ClassNameConstants.JAVA_UTIL_DATE)) {
            return new java.util.Date(value.getTime());
        }
        return value;
    }
}
