// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.List;
import java.util.Map;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.Latch;

class Lock implements Latch, Control
{
    private final CompatibilitySpace space;
    private final Lockable ref;
    private final Object qualifier;
    int count;
    
    protected Lock(final CompatibilitySpace space, final Lockable ref, final Object qualifier) {
        this.space = space;
        this.ref = ref;
        this.qualifier = qualifier;
    }
    
    public final Lockable getLockable() {
        return this.ref;
    }
    
    public final CompatibilitySpace getCompatabilitySpace() {
        return this.space;
    }
    
    public final Object getQualifier() {
        return this.qualifier;
    }
    
    public final int getCount() {
        return this.count;
    }
    
    final Lock copy() {
        return new Lock(this.space, this.ref, this.qualifier);
    }
    
    void grant() {
        ++this.count;
        this.ref.lockEvent(this);
    }
    
    int unlock(int count) {
        if (count > this.count) {
            count = this.count;
        }
        this.count -= count;
        if (this.count == 0) {
            this.ref.unlockEvent(this);
        }
        return count;
    }
    
    public final int hashCode() {
        return this.ref.hashCode() ^ this.space.hashCode();
    }
    
    public final boolean equals(final Object o) {
        if (o instanceof Lock) {
            final Lock lock = (Lock)o;
            return this.space == lock.space && this.ref.equals(lock.ref) && this.qualifier == lock.qualifier;
        }
        return false;
    }
    
    public LockControl getLockControl() {
        return new LockControl(this, this.ref);
    }
    
    public Lock getLock(final CompatibilitySpace compatibilitySpace, final Object o) {
        if (this.space == compatibilitySpace && this.qualifier == o) {
            return this;
        }
        return null;
    }
    
    public Control shallowClone() {
        return this;
    }
    
    public ActiveLock firstWaiter() {
        return null;
    }
    
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    public boolean unlock(final Latch latch, int count) {
        if (count == 0) {
            count = latch.getCount();
        }
        this.unlock(count);
        return false;
    }
    
    public void addWaiters(final Map map) {
    }
    
    public Lock getFirstGrant() {
        return this;
    }
    
    public List getGranted() {
        return null;
    }
    
    public List getWaiting() {
        return null;
    }
    
    public boolean isGrantable(final boolean b, final CompatibilitySpace compatibilitySpace, final Object o) {
        return (this.space == compatibilitySpace && this.ref.lockerAlwaysCompatible()) || this.ref.requestCompatible(o, this.qualifier);
    }
}
