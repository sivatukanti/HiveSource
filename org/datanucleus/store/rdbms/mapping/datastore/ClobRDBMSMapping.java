// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.io.BufferedReader;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class ClobRDBMSMapping extends LongVarcharRDBMSMapping
{
    public ClobRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(2005, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(2005);
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, final String value) {
        if (this.getDatastoreAdapter().supportsOption("ClobSetUsingSetString")) {
            super.setString(ps, param, value);
        }
        else {
            this.setObject(ps, param, value);
        }
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        if (this.getDatastoreAdapter().supportsOption("ClobSetUsingSetString")) {
            super.setObject(ps, param, value);
        }
        else {
            try {
                if (value == null) {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
                else {
                    ps.setClob(param, new ClobImpl((String)value));
                }
            }
            catch (SQLException e) {
                throw new NucleusDataStoreException(ClobRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
            }
            catch (IOException e2) {
                throw new NucleusDataStoreException(ClobRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e2.getMessage()), e2);
            }
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        if (this.getDatastoreAdapter().supportsOption("ClobSetUsingSetString")) {
            return super.getString(rs, param);
        }
        return (String)this.getObject(rs, param);
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        if (this.getDatastoreAdapter().supportsOption("ClobSetUsingSetString")) {
            return super.getObject(rs, param);
        }
        Object value;
        try {
            final Clob clob = rs.getClob(param);
            if (!rs.wasNull()) {
                final BufferedReader br = new BufferedReader(clob.getCharacterStream());
                try {
                    final StringBuffer sb = new StringBuffer();
                    int c;
                    while ((c = br.read()) != -1) {
                        sb.append((char)c);
                    }
                    value = sb.toString();
                }
                finally {
                    br.close();
                }
            }
            else {
                value = null;
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(ClobRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        catch (IOException e2) {
            throw new NucleusDataStoreException(ClobRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e2.getMessage()), e2);
        }
        return value;
    }
}
