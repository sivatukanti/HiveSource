// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.AuxObject;
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

public final class InitPageOperation extends PhysicalPageOperation
{
    protected int nextRecordId;
    protected int initFlag;
    protected int pageFormatId;
    protected long pageOffset;
    protected boolean reuse;
    protected boolean overflowPage;
    
    public InitPageOperation(final BasePage basePage, final int initFlag, final int pageFormatId, final long pageOffset) throws StandardException {
        super(basePage);
        this.initFlag = initFlag;
        this.pageFormatId = pageFormatId;
        this.pageOffset = pageOffset;
        if ((this.initFlag & 0x4) == 0x0) {
            this.nextRecordId = basePage.newRecordId();
        }
        else {
            this.nextRecordId = 6;
        }
    }
    
    public InitPageOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.nextRecordId);
        CompressedNumber.writeInt(objectOutput, this.initFlag);
        CompressedNumber.writeLong(objectOutput, this.pageOffset);
        objectOutput.writeInt(this.pageFormatId);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.nextRecordId = CompressedNumber.readInt(objectInput);
        this.initFlag = CompressedNumber.readInt(objectInput);
        this.pageOffset = CompressedNumber.readLong(objectInput);
        this.pageFormatId = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 241;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.initPage(logInstant, (byte)1, this.nextRecordId, (this.initFlag & 0x2) != 0x0, (this.initFlag & 0x1) != 0x0);
    }
    
    protected BasePage getPageForRedoRecovery(final Transaction transaction) throws StandardException {
        final BasePage pageForRedoRecovery = super.getPageForRedoRecovery(transaction);
        if (pageForRedoRecovery != null) {
            return pageForRedoRecovery;
        }
        return (BasePage)this.containerHdl.reCreatePageForRedoRecovery(this.pageFormatId, this.getPageId().getPageNumber(), this.pageOffset);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        basePage.setPageStatus(logInstant, (byte)2);
        basePage.setAuxObject(null);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoMe(transaction, basePage, logInstant, limitObjectInput);
    }
    
    public String toString() {
        return null;
    }
}
