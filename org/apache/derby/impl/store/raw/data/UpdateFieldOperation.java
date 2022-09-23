// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.FormatableBitSet;
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
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.util.ByteArray;

public final class UpdateFieldOperation extends LogicalPageOperation
{
    protected int doMeSlot;
    protected int fieldId;
    protected transient ByteArray preparedLog;
    
    public UpdateFieldOperation(final RawTransaction rawTransaction, final BasePage basePage, final int doMeSlot, final int n, final int fieldId, final Object o, final LogicalUndo logicalUndo) throws StandardException {
        super(basePage, logicalUndo, n);
        this.doMeSlot = doMeSlot;
        this.fieldId = fieldId;
        try {
            this.writeOptionalDataToBuffer(rawTransaction, o);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public UpdateFieldOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.doMeSlot);
        CompressedNumber.writeInt(objectOutput, this.fieldId);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.doMeSlot = CompressedNumber.readInt(objectInput);
        this.fieldId = CompressedNumber.readInt(objectInput);
    }
    
    public int getTypeFormatId() {
        return 109;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.storeField(logInstant, this.doMeSlot, this.fieldId, limitObjectInput);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final int n, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        final int recordById = basePage.findRecordById(n, 0);
        basePage.skipField(limitObjectInput);
        basePage.storeField(logInstant, recordById, this.fieldId, limitObjectInput);
        basePage.setAuxObject(null);
    }
    
    public void restoreLoggedRow(final Object[] array, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        BasePage basePage = null;
        try {
            basePage = (BasePage)this.getContainer().getPage(this.getPageId().getPageNumber());
            basePage.skipField(limitObjectInput);
            basePage.skipField(limitObjectInput);
            basePage.restoreRecordFromStream(limitObjectInput, array);
        }
        finally {
            if (basePage != null) {
                basePage.unlatch();
            }
        }
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        final int recordById = basePage.findRecordById(this.recordId, 0);
        basePage.skipField(limitObjectInput);
        basePage.storeField(logInstant, recordById, this.fieldId, limitObjectInput);
        basePage.setAuxObject(null);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction, final Object o) throws StandardException, IOException {
        final DynamicByteArrayOutputStream logBuffer = rawTransaction.getLogBuffer();
        final int position = logBuffer.getPosition();
        this.page.logColumn(this.doMeSlot, this.fieldId, o, logBuffer, 100);
        this.page.logField(this.doMeSlot, this.fieldId, logBuffer);
        if (this.undo != null) {
            this.page.logRecord(this.doMeSlot, 0, this.recordId, null, logBuffer, null);
        }
        final int n = logBuffer.getPosition() - position;
        logBuffer.setPosition(position);
        this.preparedLog = new ByteArray(logBuffer.getByteArray(), position, n);
    }
    
    public String toString() {
        return null;
    }
}
