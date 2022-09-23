// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Undoable;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.Compensation;

public class LogicalUndoOperation extends PageBasicOperation implements Compensation
{
    protected int recordId;
    private transient LogicalPageOperation undoOp;
    
    protected LogicalUndoOperation(final BasePage basePage) {
        super(basePage);
        this.undoOp = null;
    }
    
    public LogicalUndoOperation(final BasePage basePage, final int recordId, final LogicalPageOperation undoOp) {
        super(basePage);
        this.undoOp = null;
        this.undoOp = undoOp;
        this.recordId = recordId;
    }
    
    public LogicalUndoOperation() {
        this.undoOp = null;
    }
    
    public int getTypeFormatId() {
        return 104;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.recordId);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.recordId = CompressedNumber.readInt(objectInput);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
    }
    
    public void setUndoOp(final Undoable undoable) {
        this.undoOp = (LogicalPageOperation)undoable;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoOp.undoMe(transaction, this.page, this.recordId, logInstant, limitObjectInput);
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
    
    public final ByteArray getPreparedLog() {
        return null;
    }
    
    public String toString() {
        return null;
    }
}
