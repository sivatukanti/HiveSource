// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.services.locks.ShExLockable;

class CacheLock extends ShExLockable
{
    private PropertyConglomerate pc;
    
    CacheLock(final PropertyConglomerate pc) {
        this.pc = pc;
    }
    
    public void unlockEvent(final Latch latch) {
        super.unlockEvent(latch);
        this.pc.resetCache();
    }
}
