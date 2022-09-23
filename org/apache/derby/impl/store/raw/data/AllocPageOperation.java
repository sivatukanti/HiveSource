// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;

public final class AllocPageOperation extends PhysicalPageOperation
{
    protected long newPageNumber;
    protected int doStatus;
    protected int undoStatus;
    
    public AllocPageOperation(final AllocPage allocPage, final long newPageNumber, final int doStatus, final int undoStatus) throws StandardException {
        super(allocPage);
        this.newPageNumber = newPageNumber;
        this.doStatus = doStatus;
        this.undoStatus = undoStatus;
    }
    
    public AllocPageOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeLong(objectOutput, this.newPageNumber);
        CompressedNumber.writeInt(objectOutput, this.doStatus);
        CompressedNumber.writeInt(objectOutput, this.undoStatus);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.newPageNumber = CompressedNumber.readLong(objectInput);
        this.doStatus = CompressedNumber.readInt(objectInput);
        this.undoStatus = CompressedNumber.readInt(objectInput);
    }
    
    public int getTypeFormatId() {
        return 111;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)this.page).setPageStatus(logInstant, this.newPageNumber, this.doStatus);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)basePage).setPageStatus(logInstant, this.newPageNumber, this.undoStatus);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
    }
    
    public String toString() {
        return null;
    }
}
