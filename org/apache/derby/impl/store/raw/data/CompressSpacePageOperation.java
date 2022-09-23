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

public class CompressSpacePageOperation extends PhysicalPageOperation
{
    protected int newHighestPage;
    protected int num_pages_truncated;
    
    public CompressSpacePageOperation(final AllocPage allocPage, final int newHighestPage, final int num_pages_truncated) throws StandardException {
        super(allocPage);
        this.newHighestPage = newHighestPage;
        this.num_pages_truncated = num_pages_truncated;
    }
    
    public CompressSpacePageOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        if (!(this instanceof CompressSpacePageOperation10_2)) {
            objectOutput.writeInt(this.newHighestPage);
            CompressedNumber.writeInt(objectOutput, this.num_pages_truncated);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        if (!(this instanceof CompressSpacePageOperation10_2)) {
            this.newHighestPage = objectInput.readInt();
            this.num_pages_truncated = CompressedNumber.readInt(objectInput);
        }
    }
    
    public int getTypeFormatId() {
        return 465;
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)this.page).compressSpace(logInstant, this.newHighestPage, this.num_pages_truncated);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        ((AllocPage)basePage).undoCompressSpace(logInstant, this.newHighestPage, this.num_pages_truncated);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
    }
    
    public String toString() {
        return null;
    }
}
