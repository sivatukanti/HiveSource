// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;

public class OracleSerialisedObjectMapping extends SerialisedMapping implements MappingCallbacks
{
    @Override
    public void postFetch(final ObjectProvider sm) {
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
        byte[] bytes = new byte[0];
        final Object value = op.provideField(this.mmd.getAbsoluteFieldNumber());
        if (value != null) {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                bytes = baos.toByteArray();
            }
            catch (IOException ex) {}
        }
        OracleBlobRDBMSMapping.updateBlobColumn(op, this.getTable(), this.getDatastoreMapping(0), bytes);
    }
    
    @Override
    public void postInsert(final ObjectProvider op) {
    }
    
    @Override
    public void postUpdate(final ObjectProvider op) {
        this.insertPostProcessing(op);
    }
    
    @Override
    public void preDelete(final ObjectProvider op) {
    }
}
