// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.IOException;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.Undoable;

public abstract class PhysicalPageOperation extends PageBasicOperation implements Undoable
{
    protected PhysicalPageOperation(final BasePage basePage) {
        super(basePage);
    }
    
    public PhysicalPageOperation() {
    }
    
    public Compensation generateUndo(final Transaction transaction, final LimitObjectInput limitObjectInput) throws StandardException {
        final BasePage findpage = this.findpage(transaction);
        findpage.preDirty();
        return new PhysicalUndoOperation(findpage, this);
    }
    
    public abstract void undoMe(final Transaction p0, final BasePage p1, final LogInstant p2, final LimitObjectInput p3) throws StandardException, IOException;
}
