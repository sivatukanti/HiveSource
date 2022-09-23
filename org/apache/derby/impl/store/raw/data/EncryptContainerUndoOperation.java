// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.Undoable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.Compensation;

public class EncryptContainerUndoOperation implements Compensation
{
    private transient EncryptContainerOperation undoOp;
    
    public EncryptContainerUndoOperation(final EncryptContainerOperation undoOp) {
        this.undoOp = undoOp;
    }
    
    public EncryptContainerUndoOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
    }
    
    public int getTypeFormatId() {
        return 460;
    }
    
    public void setUndoOp(final Undoable undoable) {
        this.undoOp = (EncryptContainerOperation)undoable;
    }
    
    public boolean needsRedo(final Transaction transaction) throws StandardException {
        return true;
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoOp.undoMe(transaction);
        this.releaseResource(transaction);
    }
    
    public void releaseResource(final Transaction transaction) {
        if (this.undoOp != null) {
            this.undoOp.releaseResource(transaction);
        }
    }
    
    public int group() {
        return 260;
    }
    
    public String toString() {
        return null;
    }
}
