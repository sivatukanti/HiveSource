// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

import java.util.Hashtable;

public interface Lockable
{
    void lockEvent(final Latch p0);
    
    boolean requestCompatible(final Object p0, final Object p1);
    
    boolean lockerAlwaysCompatible();
    
    void unlockEvent(final Latch p0);
    
    boolean lockAttributes(final int p0, final Hashtable p1);
}
