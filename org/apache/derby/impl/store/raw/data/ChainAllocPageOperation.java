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

public final class ChainAllocPageOperation extends PhysicalPageOperation
{
    protected long newAllocPageNum;
    protected long newAllocPageOffset;
    
    public ChainAllocPageOperation(final AllocPage allocPage, final long newAllocPageNum, final long newAllocPageOffset) throws StandardException {
        super(allocPage);
        this.newAllocPageNum = newAllocPageNum;
        this.newAllocPageOffset = newAllocPageOffset;
    }
    
    public ChainAllocPageOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeLong(objectOutput, this.newAllocPageNum);
        CompressedNumber.writeLong(objectOutput, this.newAllocPageOffset);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.newAllocPageNum = CompressedNumber.readLong(objectInput);
        this.newAllocPageOffset = CompressedNumber.readLong(objectInput);
    }
    
    public int getTypeFormatId() {
        return 97;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)this.page).chainNextAllocPage(logInstant, this.newAllocPageNum, this.newAllocPageOffset);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)basePage).chainNextAllocPage(logInstant, -1L, 0L);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
    }
    
    public String toString() {
        return null;
    }
}
