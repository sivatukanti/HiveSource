// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public final class InvalidatePageOperation extends PhysicalPageOperation
{
    public InvalidatePageOperation(final BasePage basePage) {
        super(basePage);
    }
    
    public InvalidatePageOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
    }
    
    public int getTypeFormatId() {
        return 113;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.setPageStatus(logInstant, (byte)2);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        basePage.setPageStatus(logInstant, (byte)1);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoMe(transaction, basePage, logInstant, limitObjectInput);
    }
    
    public String toString() {
        return null;
    }
}
