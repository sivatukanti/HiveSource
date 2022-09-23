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

public final class PurgeOperation extends PhysicalPageOperation
{
    protected int slot;
    protected int num_rows;
    protected int[] recordIds;
    protected transient ByteArray preparedLog;
    
    public PurgeOperation(final RawTransaction rawTransaction, final BasePage basePage, final int slot, final int num_rows, final int[] recordIds, final boolean b) throws StandardException {
        super(basePage);
        this.slot = slot;
        this.num_rows = num_rows;
        this.recordIds = recordIds;
        try {
            this.writeOptionalDataToBuffer(rawTransaction, b);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public PurgeOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.slot);
        CompressedNumber.writeInt(objectOutput, this.num_rows);
        for (int i = 0; i < this.num_rows; ++i) {
            CompressedNumber.writeInt(objectOutput, this.recordIds[i]);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.slot = CompressedNumber.readInt(objectInput);
        this.num_rows = CompressedNumber.readInt(objectInput);
        this.recordIds = new int[this.num_rows];
        for (int i = 0; i < this.num_rows; ++i) {
            this.recordIds[i] = CompressedNumber.readInt(objectInput);
        }
    }
    
    public int getTypeFormatId() {
        return 106;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        for (int i = this.num_rows - 1; i >= 0; --i) {
            this.page.purgeRecord(logInstant, this.slot + i, this.recordIds[i]);
        }
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        for (int i = 0; i < this.num_rows; ++i) {
            basePage.storeRecord(logInstant, this.slot + i, true, limitObjectInput);
        }
        basePage.setAuxObject(null);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.undoMe(transaction, basePage, logInstant, limitObjectInput);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction, final boolean b) throws StandardException, IOException {
        final DynamicByteArrayOutputStream logBuffer = rawTransaction.getLogBuffer();
        final int position = logBuffer.getPosition();
        for (int i = 0; i < this.num_rows; ++i) {
            if (b) {
                this.page.logRecord(i + this.slot, 0, this.recordIds[i], null, logBuffer, null);
            }
            else {
                this.page.logRecord(i + this.slot, 2, this.recordIds[i], null, logBuffer, null);
            }
        }
        final int n = logBuffer.getPosition() - position;
        logBuffer.setPosition(position);
        this.preparedLog = new ByteArray(logBuffer.getByteArray(), position, n);
    }
    
    public String toString() {
        return null;
    }
}
