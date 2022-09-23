// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;

public final class ActiveLock extends Lock
{
    byte wakeUpNow;
    boolean potentiallyGranted;
    protected boolean canSkip;
    
    protected ActiveLock(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o) {
        super(compatibilitySpace, lockable, o);
    }
    
    protected boolean setPotentiallyGranted() {
        return !this.potentiallyGranted && (this.potentiallyGranted = true);
    }
    
    protected void clearPotentiallyGranted() {
        this.potentiallyGranted = false;
    }
    
    protected synchronized byte waitForGrant(final int n) throws StandardException {
        if (this.wakeUpNow == 0) {
            try {
                if (n == -1) {
                    this.wait();
                }
                else if (n > 0) {
                    this.wait(n);
                }
            }
            catch (InterruptedException ex) {
                this.wakeUpNow = 3;
            }
        }
        final byte wakeUpNow = this.wakeUpNow;
        this.wakeUpNow = 0;
        return wakeUpNow;
    }
    
    protected synchronized void wakeUp(final byte wakeUpNow) {
        if (this.wakeUpNow != 2) {
            this.wakeUpNow = wakeUpNow;
        }
        this.notify();
    }
}
