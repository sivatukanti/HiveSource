// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.FormatableBitSet;
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
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.util.ByteArray;

public class CopyRowsOperation extends PhysicalPageOperation
{
    protected int num_rows;
    protected int destSlot;
    protected int[] recordIds;
    protected int[] reservedSpace;
    protected transient ByteArray preparedLog;
    
    public CopyRowsOperation(final RawTransaction rawTransaction, final BasePage basePage, final BasePage basePage2, final int destSlot, final int num_rows, final int n, final int[] recordIds) throws StandardException {
        super(basePage);
        this.num_rows = num_rows;
        this.destSlot = destSlot;
        this.recordIds = recordIds;
        try {
            this.reservedSpace = new int[num_rows];
            for (int i = 0; i < num_rows; ++i) {
                this.reservedSpace[i] = basePage2.getReservedCount(i + n);
            }
            this.writeOptionalDataToBuffer(rawTransaction, basePage2, n);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public CopyRowsOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.num_rows);
        CompressedNumber.writeInt(objectOutput, this.destSlot);
        for (int i = 0; i < this.num_rows; ++i) {
            CompressedNumber.writeInt(objectOutput, this.recordIds[i]);
            CompressedNumber.writeInt(objectOutput, this.reservedSpace[i]);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.num_rows = CompressedNumber.readInt(objectInput);
        this.destSlot = CompressedNumber.readInt(objectInput);
        this.recordIds = new int[this.num_rows];
        this.reservedSpace = new int[this.num_rows];
        for (int i = 0; i < this.num_rows; ++i) {
            this.recordIds[i] = CompressedNumber.readInt(objectInput);
            this.reservedSpace[i] = CompressedNumber.readInt(objectInput);
        }
    }
    
    public int getTypeFormatId() {
        return 210;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        for (int i = 0; i < this.num_rows; ++i) {
            this.page.storeRecord(logInstant, this.destSlot + i, true, limitObjectInput);
            if (this.reservedSpace[i] > 0) {
                this.page.reserveSpaceForSlot(logInstant, this.destSlot + i, this.reservedSpace[i]);
            }
        }
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        for (int i = this.num_rows - 1; i >= 0; --i) {
            basePage.purgeRecord(logInstant, basePage.findRecordById(this.recordIds[i], i), this.recordIds[i]);
        }
        basePage.setAuxObject(null);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoMe(transaction, basePage, logInstant, limitObjectInput);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction, final BasePage basePage, final int n) throws StandardException, IOException {
        final DynamicByteArrayOutputStream logBuffer = rawTransaction.getLogBuffer();
        final int position = logBuffer.getPosition();
        final int[] array = new int[this.num_rows];
        int n2 = logBuffer.getPosition();
        for (int i = 0; i < this.num_rows; ++i) {
            basePage.logRecord(i + n, 0, this.recordIds[i], null, logBuffer, null);
            array[i] = logBuffer.getPosition() - n2;
            n2 = logBuffer.getPosition();
            final int[] array2 = array;
            final int n3 = i;
            array2[n3] += this.reservedSpace[i];
        }
        if (!this.page.spaceForCopy(this.num_rows, array)) {
            throw StandardException.newException("XSDA3.S");
        }
        final int n4 = logBuffer.getPosition() - position;
        logBuffer.setPosition(position);
        this.preparedLog = new ByteArray(logBuffer.getByteArray(), position, n4);
    }
    
    public String toString() {
        return null;
    }
}
