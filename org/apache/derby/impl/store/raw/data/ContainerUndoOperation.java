// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.Undoable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.Compensation;

public class ContainerUndoOperation extends ContainerBasicOperation implements Compensation
{
    private transient ContainerOperation undoOp;
    
    public ContainerUndoOperation(final RawContainerHandle rawContainerHandle, final ContainerOperation undoOp) throws StandardException {
        super(rawContainerHandle);
        this.undoOp = undoOp;
    }
    
    public ContainerUndoOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
    }
    
    public int getTypeFormatId() {
        return 107;
    }
    
    public void setUndoOp(final Undoable undoable) {
        this.undoOp = (ContainerOperation)undoable;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoOp.undoMe(transaction, this.containerHdl, logInstant, limitObjectInput);
        this.releaseResource(transaction);
    }
    
    public void releaseResource(final Transaction transaction) {
        if (this.undoOp != null) {
            this.undoOp.releaseResource(transaction);
        }
        super.releaseResource(transaction);
    }
    
    public int group() {
        return super.group() | 0x4 | 0x100;
    }
}
