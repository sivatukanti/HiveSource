// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.OutputStream;
import org.apache.derby.iapi.store.raw.AuxObject;
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
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.util.ByteArray;

public final class UpdateOperation extends PhysicalPageOperation
{
    protected int doMeSlot;
    protected int recordId;
    protected transient int nextColumn;
    protected transient ByteArray preparedLog;
    
    public UpdateOperation(final RawTransaction rawTransaction, final BasePage basePage, final int doMeSlot, final int recordId, final Object[] array, final FormatableBitSet set, final int n, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final int n2, final RecordHandle recordHandle) throws StandardException {
        super(basePage);
        this.doMeSlot = doMeSlot;
        this.recordId = recordId;
        this.nextColumn = -1;
        try {
            this.writeOptionalDataToBuffer(rawTransaction, dynamicByteArrayOutputStream, array, set, n, n2, recordHandle);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public UpdateOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.doMeSlot);
        CompressedNumber.writeInt(objectOutput, this.recordId);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.doMeSlot = CompressedNumber.readInt(objectInput);
        this.recordId = CompressedNumber.readInt(objectInput);
    }
    
    public int getTypeFormatId() {
        return 108;
    }
    
    public int getNextStartColumn() {
        return this.nextColumn;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.storeRecord(logInstant, this.doMeSlot, false, limitObjectInput);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        final int recordById = basePage.findRecordById(this.recordId, 0);
        basePage.skipRecord(limitObjectInput);
        basePage.storeRecord(logInstant, recordById, false, limitObjectInput);
        basePage.setAuxObject(null);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction, DynamicByteArrayOutputStream logBuffer, final Object[] array, final FormatableBitSet set, final int n, final int n2, final RecordHandle recordHandle) throws StandardException, IOException {
        if (n == -1) {
            logBuffer = rawTransaction.getLogBuffer();
        }
        logBuffer.getPosition();
        this.nextColumn = this.page.logRow(this.doMeSlot, false, this.recordId, array, set, logBuffer, 0, (byte)8, n, n2, 100);
        FormatableBitSet set2 = set;
        if (this.nextColumn != -1 && set != null) {
            final int numberFields = this.page.getHeaderAtSlot(this.doMeSlot).getNumberFields();
            set2 = new FormatableBitSet(set);
            final int n3 = this.nextColumn + numberFields;
            set2.grow(n3);
            for (int i = this.nextColumn; i < n3; ++i) {
                set2.set(i);
            }
        }
        this.page.logRecord(this.doMeSlot, 1, this.recordId, set2, logBuffer, recordHandle);
        final int beginPosition = logBuffer.getBeginPosition();
        final int n4 = logBuffer.getPosition() - beginPosition;
        logBuffer.setPosition(beginPosition);
        this.preparedLog = new ByteArray(logBuffer.getByteArray(), beginPosition, n4);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoMe(transaction, basePage, logInstant, limitObjectInput);
    }
    
    public String toString() {
        return null;
    }
}
