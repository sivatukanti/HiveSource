// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Blob;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class BlobRDBMSMapping extends AbstractLargeBinaryRDBMSMapping
{
    public BlobRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(2004, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(2004);
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        byte[] bytes = null;
        try {
            bytes = rs.getBytes(param);
            if (bytes == null) {
                return null;
            }
        }
        catch (SQLException sqle3) {
            try {
                final Blob blob = rs.getBlob(param);
                if (blob == null) {
                    return null;
                }
                bytes = blob.getBytes(1L, (int)blob.length());
                if (bytes == null) {
                    return null;
                }
                return this.getObjectForBytes(bytes, param);
            }
            catch (SQLException sqle2) {
                throw new NucleusDataStoreException(BlobRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, sqle2.getMessage()), sqle2);
            }
        }
        return this.getObjectForBytes(bytes, param);
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        super.setObject(ps, param, value);
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, final String value) {
        try {
            if (this.getDatastoreAdapter().supportsOption("BlobSetUsingSetString")) {
                if (value == null) {
                    if (this.column.isDefaultable() && this.column.getDefaultValue() != null) {
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
            else if (value == null) {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setBlob(param, new BlobImpl(this.column.getDefaultValue().toString().trim()));
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
            }
            else {
                ps.setBlob(param, new BlobImpl(value));
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BlobRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
        catch (IOException e2) {
            throw new NucleusDataStoreException(BlobRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e2.getMessage()), e2);
        }
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        String value;
        try {
            if (this.getDatastoreAdapter().supportsOption("BlobSetUsingSetString")) {
                value = rs.getString(param);
            }
            else {
                final byte[] bytes = rs.getBytes(param);
                if (bytes == null) {
                    value = null;
                }
                else {
                    final BlobImpl blob = new BlobImpl(bytes);
                    value = (String)blob.getObject();
                }
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BlobRDBMSMapping.LOCALISER_RDBMS.msg("055002", "String", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
}
