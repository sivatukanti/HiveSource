// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.util.ByteArray;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.Undoable;
import org.apache.derby.iapi.store.raw.Compensation;

public class PhysicalUndoOperation extends PageBasicOperation implements Compensation
{
    private transient PhysicalPageOperation undoOp;
    
    protected PhysicalUndoOperation(final BasePage basePage) {
        super(basePage);
    }
    
    public PhysicalUndoOperation(final BasePage basePage, final PhysicalPageOperation undoOp) {
        super(basePage);
        this.undoOp = undoOp;
    }
    
    public PhysicalUndoOperation() {
    }
    
    public int getTypeFormatId() {
        return 105;
    }
    
    public void setUndoOp(final Undoable undoable) {
        this.undoOp = (PhysicalPageOperation)undoable;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoOp.undoMe(transaction, this.page, logInstant, limitObjectInput);
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
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
    }
    
    public String toString() {
        return null;
    }
}
