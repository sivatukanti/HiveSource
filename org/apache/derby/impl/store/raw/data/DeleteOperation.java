// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.Page;
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

public final class DeleteOperation extends LogicalPageOperation
{
    protected int doMeSlot;
    protected boolean delete;
    protected transient ByteArray preparedLog;
    
    public DeleteOperation(final RawTransaction rawTransaction, final BasePage basePage, final int doMeSlot, final int n, final boolean delete, final LogicalUndo logicalUndo) throws StandardException {
        super(basePage, logicalUndo, n);
        this.doMeSlot = doMeSlot;
        this.delete = delete;
        try {
            this.writeOptionalDataToBuffer(rawTransaction);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public DeleteOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.doMeSlot);
        objectOutput.writeBoolean(this.delete);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.doMeSlot = CompressedNumber.readInt(objectInput);
        this.delete = objectInput.readBoolean();
    }
    
    public int getTypeFormatId() {
        return 101;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.setDeleteStatus(logInstant, this.doMeSlot, this.delete);
    }
    
    public void undoMe(final Transaction transaction, final BasePage basePage, final int n, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        basePage.setDeleteStatus(logInstant, basePage.findRecordById(n, 0), !this.delete);
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
        basePage.setDeleteStatus(logInstant, basePage.findRecordById(this.recordId, 0), !this.delete);
        basePage.setAuxObject(null);
    }
    
    public ByteArray getPreparedLog() {
        return this.preparedLog;
    }
    
    private void writeOptionalDataToBuffer(final RawTransaction rawTransaction) throws StandardException, IOException {
        final DynamicByteArrayOutputStream logBuffer = rawTransaction.getLogBuffer();
        final int position = logBuffer.getPosition();
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
