// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.AuxObject;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.util.ByteArray;

public final class InsertOperation extends LogicalPageOperation
{
    protected int doMeSlot;
    protected byte insertFlag;
    protected transient int startColumn;
    protected transient ByteArray preparedLog;
    
    public InsertOperation(final RawTransaction rawTransaction, final BasePage basePage, final int doMeSlot, final int n, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte insertFlag, final int startColumn, final boolean b, final int n2, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final int n3, final int n4) throws StandardException {
        super(basePage, logicalUndo, n);
        this.doMeSlot = doMeSlot;
        this.insertFlag = insertFlag;
        this.startColumn = startColumn;
        try {
            this.writeOptionalDataToBuffer(rawTransaction, dynamicByteArrayOutputStream, array, set, b, n2, n3, n4);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public InsertOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.doMeSlot);
        objectOutput.writeByte(this.insertFlag);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.doMeSlot = CompressedNumber.readInt(objectInput);
        this.insertFlag = objectInput.readByte();
    }
    
    public int getTypeFormatId() {
        return 103;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.storeRecord(logInstant, this.doMeSlot, true, limitObjectInput);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final int n, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        final int recordById = basePage.findRecordById(n, 0);
        if ((this.insertFlag & 0x2) != 0x0) {
            basePage.purgeRecord(logInstant, recordById, n);
            final RawTransaction rawTransaction = (RawTransaction)transaction;
            if (rawTransaction.handlesPostTerminationWork() && basePage.isOverflowPage() && basePage.recordCount() == 0) {
                rawTransaction.addPostTerminationWork(new ReclaimSpace(2, (PageKey)basePage.getIdentity(), rawTransaction.getDataFactory(), true));
            }
        }
        else {
            basePage.setDeleteStatus(logInstant, recordById, true);
        }
        basePage.setAuxObject(null);
    }
    
    public void restoreLoggedRow(final Object[] array, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        Page page = null;
        try {
            page = this.getContainer().getPage(this.getPageId().getPageNumber());
            ((BasePage)page).restoreRecordFromStream(limitObjectInput, array);
        }
        finally {
            if (page != null) {
                page.unlatch();
            }
        }
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.insertFlag |= 0x2;
        this.undoMe(transaction, basePage, this.recordId, logInstant, limitObjectInput);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    public int getNextStartColumn() {
        return this.startColumn;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final Object[] array, final FormatableBitSet set, final boolean b, int n, int n2, final int n3) throws StandardException, IOException {
        DynamicByteArrayOutputStream logBuffer;
        if (dynamicByteArrayOutputStream != null) {
            logBuffer = dynamicByteArrayOutputStream;
        }
        else {
            n = -1;
            n2 = -1;
            logBuffer = rawTransaction.getLogBuffer();
        }
        if (b) {
            this.startColumn = this.page.logLongColumn(this.doMeSlot, this.recordId, array[0], logBuffer);
        }
        else {
            this.startColumn = this.page.logRow(this.doMeSlot, true, this.recordId, array, set, logBuffer, this.startColumn, this.insertFlag, n, n2, n3);
        }
        final int beginPosition = logBuffer.getBeginPosition();
        this.preparedLog = new ByteArray(logBuffer.getByteArray(), beginPosition, logBuffer.getPosition() - beginPosition);
    }
    
    public String toString() {
        return null;
    }
}
