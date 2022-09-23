// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.List;
import java.util.Map;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.Lockable;

public interface Control
{
    Lockable getLockable();
    
    LockControl getLockControl();
    
    Lock getLock(final CompatibilitySpace p0, final Object p1);
    
    Control shallowClone();
    
    ActiveLock firstWaiter();
    
    boolean isEmpty();
    
    boolean unlock(final Latch p0, final int p1);
    
    void addWaiters(final Map p0);
    
    Lock getFirstGrant();
    
    List getGranted();
    
    List getWaiting();
    
    boolean isGrantable(final boolean p0, final CompatibilitySpace p1, final Object p2);
}
