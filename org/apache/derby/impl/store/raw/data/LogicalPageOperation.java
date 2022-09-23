// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.LogicalUndoable;

public abstract class LogicalPageOperation extends PageBasicOperation implements LogicalUndoable
{
    protected LogicalUndo undo;
    protected int recordId;
    
    public LogicalPageOperation() {
    }
    
    protected LogicalPageOperation(final BasePage basePage, final LogicalUndo undo, final int recordId) {
        super(basePage);
        this.undo = undo;
        this.recordId = recordId;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.recordId);
        objectOutput.writeObject(this.undo);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.recordId = CompressedNumber.readInt(objectInput);
        this.undo = (LogicalUndo)objectInput.readObject();
    }
    
    public Compensation generateUndo(final Transaction transaction, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        if (this.undo == null) {
            final BasePage findpage = this.findpage(transaction);
            findpage.preDirty();
            return new LogicalUndoOperation(findpage, this.recordId, this);
        }
        final BasePage logicalPage = this.findLogicalPage(transaction, this.undo, limitObjectInput);
        logicalPage.preDirty();
        return new LogicalUndoOperation(logicalPage, this.recordId, this);
    }
    
    public ContainerHandle getContainer() {
        return this.containerHdl;
    }
    
    public void resetRecordHandle(final RecordHandle recordHandle) {
        this.resetPageNumber(recordHandle.getPageNumber());
        this.recordId = recordHandle.getId();
    }
    
    public RecordHandle getRecordHandle() {
        return new RecordId(this.getPageId(), this.recordId);
    }
    
    public void reclaimPrepareLocks(final Transaction transaction, final LockingPolicy lockingPolicy) throws StandardException {
        final ContainerHandle openContainer = transaction.openContainer(this.getPageId().getContainerId(), lockingPolicy, 196);
        if (openContainer != null) {
            openContainer.close();
        }
        lockingPolicy.lockRecordForWrite(transaction, this.getRecordHandle(), false, false);
        this.releaseResource(transaction);
    }
    
    private BasePage findLogicalPage(final Transaction transaction, final LogicalUndo logicalUndo, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.releaseResource(transaction);
        boolean b = false;
        try {
            this.containerHdl = ((RawTransaction)transaction).openDroppedContainer(this.getPageId().getContainerId(), null);
            this.page = (BasePage)logicalUndo.findUndo(transaction, this, limitObjectInput);
            b = true;
        }
        finally {
            if (!b && this.containerHdl != null) {
                this.containerHdl.close();
                this.containerHdl = null;
            }
        }
        this.foundHere = true;
        return this.page;
    }
    
    public abstract void undoMe(final Transaction p0, final BasePage p1, final int p2, final LogInstant p3, final LimitObjectInput p4) throws StandardException, IOException;
}
