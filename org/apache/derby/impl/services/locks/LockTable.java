// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Map;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;

interface LockTable
{
    Lock lockObject(final CompatibilitySpace p0, final Lockable p1, final Object p2, final int p3) throws StandardException;
    
    void unlock(final Latch p0, final int p1);
    
    Lock unlockReference(final CompatibilitySpace p0, final Lockable p1, final Object p2, final Map p3);
    
    void oneMoreWaiter();
    
    void oneLessWaiter();
    
    boolean anyoneBlocked();
    
    boolean zeroDurationLockObject(final CompatibilitySpace p0, final Lockable p1, final Object p2, final int p3) throws StandardException;
    
    Map shallowClone();
    
    void setDeadlockTimeout(final int p0);
    
    void setWaitTimeout(final int p0);
    
    int getWaitTimeout();
    
    void setDeadlockTrace(final boolean p0);
    
    void addWaiters(final Map p0);
}
