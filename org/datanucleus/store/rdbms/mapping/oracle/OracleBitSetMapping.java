// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import java.io.IOException;
import java.sql.SQLException;
import org.datanucleus.util.TypeConversionHelper;
import java.util.BitSet;
import org.datanucleus.store.rdbms.mapping.datastore.BlobImpl;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.io.Serializable;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;
import org.datanucleus.store.rdbms.mapping.java.BitSetMapping;

public class OracleBitSetMapping extends BitSetMapping implements MappingCallbacks
{
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
        final Object value = op.provideField(this.mmd.getAbsoluteFieldNumber());
        if (value == null) {
            return;
        }
        byte[] bytes = new byte[0];
        try {
            if (this.mmd.isSerialized()) {
                if (!(value instanceof Serializable)) {
                    throw new NucleusDataStoreException(OracleBitSetMapping.LOCALISER.msg("055005", value.getClass().getName()));
                }
                final BlobImpl b = new BlobImpl(value);
                bytes = b.getBytes(0L, (int)b.length());
            }
            else if (value instanceof BitSet) {
                bytes = TypeConversionHelper.getByteArrayFromBooleanArray(TypeConversionHelper.getBooleanArrayFromBitSet((BitSet)value));
            }
            else {
                if (!(value instanceof Serializable)) {
                    throw new NucleusDataStoreException(OracleBitSetMapping.LOCALISER.msg("055005", value.getClass().getName()));
                }
                final BlobImpl b = new BlobImpl(value);
                bytes = b.getBytes(0L, (int)b.length());
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(OracleBitSetMapping.LOCALISER.msg("055001", "Object", "" + value, this.mmd, e.getMessage()), e);
        }
        catch (IOException ex) {}
        OracleBlobRDBMSMapping.updateBlobColumn(op, this.getTable(), this.getDatastoreMapping(0), bytes);
    }
    
    @Override
    public void postInsert(final ObjectProvider op) {
    }
    
    @Override
    public void postUpdate(final ObjectProvider op) {
        this.insertPostProcessing(op);
    }
    
    public void deleteDependent(final ObjectProvider op) {
    }
    
    @Override
    public void postFetch(final ObjectProvider op) {
    }
    
    @Override
    public void preDelete(final ObjectProvider op) {
    }
}
