// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import java.util.Hashtable;
import org.apache.derby.iapi.services.locks.Latch;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.util.Matchable;

public final class ContainerKey implements Matchable, Lockable
{
    private final long segmentId;
    private final long containerId;
    
    public ContainerKey(final long segmentId, final long containerId) {
        this.segmentId = segmentId;
        this.containerId = containerId;
    }
    
    public long getContainerId() {
        return this.containerId;
    }
    
    public long getSegmentId() {
        return this.segmentId;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeLong(objectOutput, this.segmentId);
        CompressedNumber.writeLong(objectOutput, this.containerId);
    }
    
    public static ContainerKey read(final ObjectInput objectInput) throws IOException {
        return new ContainerKey(CompressedNumber.readLong(objectInput), CompressedNumber.readLong(objectInput));
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ContainerKey) {
            final ContainerKey containerKey = (ContainerKey)o;
            return this.containerId == containerKey.containerId && this.segmentId == containerKey.segmentId;
        }
        return false;
    }
    
    public int hashCode() {
        return (int)(this.segmentId ^ this.containerId);
    }
    
    public String toString() {
        return "Container(" + this.segmentId + ", " + this.containerId + ")";
    }
    
    public boolean match(final Object o) {
        if (this.equals(o)) {
            return true;
        }
        if (o instanceof PageKey) {
            return this.equals(((PageKey)o).getContainerId());
        }
        return o instanceof RecordHandle && this.equals(((RecordHandle)o).getContainerId());
    }
    
    public void lockEvent(final Latch latch) {
    }
    
    public boolean requestCompatible(final Object o, final Object o2) {
        return ((ContainerLock)o).isCompatible((ContainerLock)o2);
    }
    
    public boolean lockerAlwaysCompatible() {
        return true;
    }
    
    public void unlockEvent(final Latch latch) {
    }
    
    public boolean lockAttributes(final int n, final Hashtable hashtable) {
        if ((n & 0x2) == 0x0) {
            return false;
        }
        hashtable.put("CONTAINERID", new Long(this.getContainerId()));
        hashtable.put("LOCKNAME", "Tablelock");
        hashtable.put("TYPE", "TABLE");
        return true;
    }
}
