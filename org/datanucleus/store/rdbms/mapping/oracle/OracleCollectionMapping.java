// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import org.datanucleus.ExecutionContext;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import org.datanucleus.store.types.SCOUtils;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.java.CollectionMapping;

public class OracleCollectionMapping extends CollectionMapping
{
    @Override
    public void postInsert(final ObjectProvider ownerOP) {
        if (this.containerIsStoredInSingleColumn()) {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final Collection value = (Collection)ownerOP.provideField(this.mmd.getAbsoluteFieldNumber());
            SCOUtils.validateObjectsForWriting(ec, value);
            byte[] bytes = new byte[0];
            if (value != null) {
                try {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(value);
                    bytes = baos.toByteArray();
                }
                catch (IOException ex) {}
            }
            OracleBlobRDBMSMapping.updateBlobColumn(ownerOP, this.getTable(), this.getDatastoreMapping(0), bytes);
        }
        else {
            super.postInsert(ownerOP);
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider ownerOP) {
        if (this.containerIsStoredInSingleColumn()) {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final Collection value = (Collection)ownerOP.provideField(this.mmd.getAbsoluteFieldNumber());
            SCOUtils.validateObjectsForWriting(ec, value);
            this.postInsert(ownerOP);
        }
        else {
            super.postUpdate(ownerOP);
        }
    }
}
