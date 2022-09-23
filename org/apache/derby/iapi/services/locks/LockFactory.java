// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

import java.util.Enumeration;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertySetCallback;

public interface LockFactory extends PropertySetCallback
{
    CompatibilitySpace createCompatibilitySpace(final LockOwner p0);
    
    boolean lockObject(final CompatibilitySpace p0, final Object p1, final Lockable p2, final Object p3, final int p4) throws StandardException;
    
    int unlock(final CompatibilitySpace p0, final Object p1, final Lockable p2, final Object p3);
    
    void unlockGroup(final CompatibilitySpace p0, final Object p1);
    
    void unlockGroup(final CompatibilitySpace p0, final Object p1, final Matchable p2);
    
    void transfer(final CompatibilitySpace p0, final Object p1, final Object p2);
    
    boolean anyoneBlocked();
    
    boolean areLocksHeld(final CompatibilitySpace p0, final Object p1);
    
    boolean areLocksHeld(final CompatibilitySpace p0);
    
    boolean zeroDurationlockObject(final CompatibilitySpace p0, final Lockable p1, final Object p2, final int p3) throws StandardException;
    
    boolean isLockHeld(final CompatibilitySpace p0, final Object p1, final Lockable p2, final Object p3);
    
    int getWaitTimeout();
    
    void setLimit(final CompatibilitySpace p0, final Object p1, final int p2, final Limit p3);
    
    void clearLimit(final CompatibilitySpace p0, final Object p1);
    
    Enumeration makeVirtualLockTable();
}
