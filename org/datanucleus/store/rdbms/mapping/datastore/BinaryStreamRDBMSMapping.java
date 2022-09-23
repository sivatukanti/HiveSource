// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.store.rdbms.mapping.java.FileMapping;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.io.File;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class BinaryStreamRDBMSMapping extends AbstractDatastoreMapping
{
    public BinaryStreamRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(-4, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(-4);
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        try {
            if (value == null) {
                ps.setNull(param, -4);
            }
            else {
                if (!(value instanceof File)) {
                    throw new NucleusDataStoreException("setObject unsupported for java type " + value.getClass().getName());
                }
                final File file = (File)value;
                ps.setBinaryStream(param, new FileInputStream(file), (int)file.length());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BinaryStreamRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
        catch (IOException e2) {
            throw new NucleusDataStoreException(BinaryStreamRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e2.getMessage()), e2);
        }
    }
    
    @Override
    public Object getObject(final ResultSet resultSet, final int param) {
        Object so = null;
        try {
            final InputStream is = resultSet.getBinaryStream(param);
            if (!resultSet.wasNull()) {
                if (!(this.getJavaTypeMapping() instanceof FileMapping)) {
                    throw new NucleusDataStoreException("getObject unsupported for java type mapping of type " + this.getJavaTypeMapping());
                }
                so = StreamableSpooler.instance().spoolStream(is);
            }
        }
        catch (IOException e) {
            throw new NucleusDataStoreException(BinaryStreamRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
        }
        catch (SQLException e2) {
            throw new NucleusDataStoreException(BinaryStreamRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e2.getMessage()), e2);
        }
        return so;
    }
}
