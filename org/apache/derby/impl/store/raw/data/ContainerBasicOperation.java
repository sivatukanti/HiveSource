// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.util.ByteArray;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.Loggable;

public abstract class ContainerBasicOperation implements Loggable
{
    private long containerVersion;
    protected ContainerKey containerId;
    protected transient RawContainerHandle containerHdl;
    private transient boolean foundHere;
    
    protected ContainerBasicOperation(final RawContainerHandle containerHdl) throws StandardException {
        this.containerHdl = null;
        this.foundHere = false;
        this.containerHdl = containerHdl;
        this.containerId = containerHdl.getId();
        this.containerVersion = containerHdl.getContainerVersion();
    }
    
    public ContainerBasicOperation() {
        this.containerHdl = null;
        this.foundHere = false;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.containerId.writeExternal(objectOutput);
        CompressedNumber.writeLong(objectOutput, this.containerVersion);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.containerId = ContainerKey.read(objectInput);
        this.containerVersion = CompressedNumber.readLong(objectInput);
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public void releaseResource(final Transaction transaction) {
        if (!this.foundHere) {
            return;
        }
        if (this.containerHdl != null) {
            this.containerHdl.close();
            this.containerHdl = null;
        }
        this.foundHere = false;
    }
    
    public int group() {
        return 256;
    }
    
    protected RawContainerHandle findContainer(final Transaction transaction) throws StandardException {
        this.releaseResource(transaction);
        final RawTransaction rawTransaction = (RawTransaction)transaction;
        this.containerHdl = rawTransaction.openDroppedContainer(this.containerId, null);
        if (rawTransaction.inRollForwardRecovery() && this.containerHdl == null) {
            this.containerHdl = this.findContainerForRedoRecovery(rawTransaction);
        }
        if (this.containerHdl == null) {
            throw StandardException.newException("40XD2", this.containerId);
        }
        this.foundHere = true;
        return this.containerHdl;
    }
    
    protected RawContainerHandle findContainerForRedoRecovery(final RawTransaction rawTransaction) throws StandardException {
        return null;
    }
    
    public boolean needsRedo(final Transaction transaction) throws StandardException {
        this.findContainer(transaction);
        final long containerVersion = this.containerHdl.getContainerVersion();
        if (containerVersion == this.containerVersion) {
            return true;
        }
        this.releaseResource(transaction);
        return containerVersion > this.containerVersion && false;
    }
    
    public String toString() {
        return null;
    }
}
