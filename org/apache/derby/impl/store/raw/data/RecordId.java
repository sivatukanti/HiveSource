// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.util.Hashtable;
import org.apache.derby.iapi.store.raw.RowLock;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.RecordHandle;

public final class RecordId implements RecordHandle
{
    private final PageKey pageId;
    private final int recordId;
    private transient int slotNumberHint;
    
    public RecordId(final ContainerKey containerKey, final long n, final int recordId) {
        this.pageId = new PageKey(containerKey, n);
        this.recordId = recordId;
    }
    
    public RecordId(final PageKey pageId, final int recordId) {
        this.pageId = pageId;
        this.recordId = recordId;
    }
    
    public RecordId(final PageKey pageId, final int recordId, final int slotNumberHint) {
        this.pageId = pageId;
        this.recordId = recordId;
        this.slotNumberHint = slotNumberHint;
    }
    
    public int getId() {
        return this.recordId;
    }
    
    public long getPageNumber() {
        return this.pageId.getPageNumber();
    }
    
    public Object getPageId() {
        return this.pageId;
    }
    
    public ContainerKey getContainerId() {
        return this.pageId.getContainerId();
    }
    
    public int getSlotNumberHint() {
        return this.slotNumberHint;
    }
    
    public void lockEvent(final Latch latch) {
    }
    
    public boolean requestCompatible(final Object o, final Object o2) {
        return ((RowLock)o).isCompatible((RowLock)o2);
    }
    
    public boolean lockerAlwaysCompatible() {
        return true;
    }
    
    public void unlockEvent(final Latch latch) {
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof RecordId)) {
            return false;
        }
        final RecordId recordId = (RecordId)o;
        return this.recordId == recordId.recordId && this.pageId.equals(recordId.pageId);
    }
    
    public int hashCode() {
        return 89 * (89 * 7 + this.pageId.hashCode()) + this.recordId;
    }
    
    public String toString() {
        return null;
    }
    
    public boolean lockAttributes(final int n, final Hashtable hashtable) {
        if ((n & 0x2) == 0x0) {
            return false;
        }
        hashtable.put("CONTAINERID", new Long(this.pageId.getContainerId().getContainerId()));
        hashtable.put("LOCKNAME", "(" + this.pageId.getPageNumber() + "," + this.recordId + ")");
        hashtable.put("TYPE", "ROW");
        return true;
    }
}
