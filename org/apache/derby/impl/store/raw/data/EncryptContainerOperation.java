// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.util.ByteArray;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.Undoable;

public class EncryptContainerOperation implements Undoable
{
    private ContainerKey containerId;
    
    protected EncryptContainerOperation(final RawContainerHandle rawContainerHandle) throws StandardException {
        this.containerId = rawContainerHandle.getId();
    }
    
    public EncryptContainerOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.containerId.writeExternal(objectOutput);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.containerId = ContainerKey.read(objectInput);
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public void releaseResource(final Transaction transaction) {
    }
    
    public int group() {
        return 256;
    }
    
    public boolean needsRedo(final Transaction transaction) throws StandardException {
        return false;
    }
    
    public int getTypeFormatId() {
        return 459;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        this.releaseResource(transaction);
    }
    
    public void undoMe(final Transaction transaction) throws StandardException {
        new EncryptOrDecryptData((BaseDataFileFactory)((RawTransaction)transaction).getDataFactory()).restoreContainer(this.containerId);
        this.releaseResource(transaction);
    }
    
    public Compensation generateUndo(final Transaction transaction, final LimitObjectInput limitObjectInput) throws StandardException {
        return new EncryptContainerUndoOperation(this);
    }
    
    public String toString() {
        return null;
    }
}
