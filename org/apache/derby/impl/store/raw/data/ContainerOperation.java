// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.Undoable;

public class ContainerOperation extends ContainerBasicOperation implements Undoable
{
    protected byte operation;
    protected transient boolean hasCreateByteArray;
    protected ByteArray createByteArray;
    protected static final byte CREATE = 1;
    protected static final byte DROP = 2;
    protected static final byte REMOVE = 4;
    
    protected ContainerOperation(final RawContainerHandle rawContainerHandle, final byte operation) throws StandardException {
        super(rawContainerHandle);
        this.hasCreateByteArray = true;
        this.operation = operation;
    }
    
    public ContainerOperation() {
        this.hasCreateByteArray = true;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeByte(this.operation);
        if (this.operation == 1) {
            try {
                this.createByteArray = this.containerHdl.logCreateContainerInfo();
            }
            catch (StandardException ex) {
                throw new IOException(ex.toString());
            }
            this.createByteArray.writeExternal(objectOutput);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.operation = objectInput.readByte();
        if (this.operation == 1 && this.hasCreateByteArray) {
            (this.createByteArray = new ByteArray()).readExternal(objectInput);
        }
    }
    
    public int getTypeFormatId() {
        return 242;
    }
    
    protected RawContainerHandle findContainerForRedoRecovery(final RawTransaction rawTransaction) throws StandardException {
        rawTransaction.reCreateContainerForRedoRecovery(this.containerId.getSegmentId(), this.containerId.getContainerId(), this.createByteArray);
        return rawTransaction.openDroppedContainer(this.containerId, null);
    }
    
    public final void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        switch (this.operation) {
            case 2: {
                this.containerHdl.dropContainer(logInstant, true);
                break;
            }
            case 4: {
                this.containerHdl.removeContainer(logInstant);
                break;
            }
        }
        this.releaseResource(transaction);
    }
    
    public void undoMe(final Transaction transaction, final RawContainerHandle rawContainerHandle, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        switch (this.operation) {
            case 2: {
                rawContainerHandle.dropContainer(logInstant, false);
                break;
            }
            case 1: {
                rawContainerHandle.removeContainer(logInstant);
                break;
            }
        }
        this.releaseResource(transaction);
    }
    
    public Compensation generateUndo(final Transaction transaction, final LimitObjectInput limitObjectInput) throws StandardException {
        if (this.operation == 4) {
            return null;
        }
        return new ContainerUndoOperation(this.findContainer(transaction), this);
    }
    
    public String toString() {
        return null;
    }
}
