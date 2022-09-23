// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

import java.util.Hashtable;

public class ShExLockable implements Lockable
{
    public boolean lockerAlwaysCompatible() {
        return true;
    }
    
    public boolean requestCompatible(final Object o, final Object o2) {
        final ShExQual shExQual = (ShExQual)o;
        final ShExQual shExQual2 = (ShExQual)o2;
        return shExQual.getLockState() == 0 && shExQual2.getLockState() == 0;
    }
    
    public void lockEvent(final Latch latch) {
    }
    
    public void unlockEvent(final Latch latch) {
    }
    
    public boolean lockAttributes(final int n, final Hashtable hashtable) {
        if ((n & 0x4) == 0x0) {
            return false;
        }
        hashtable.put("CONTAINERID", new Long(-1L));
        hashtable.put("LOCKNAME", this.toString());
        hashtable.put("TYPE", "ShExLockable");
        return true;
    }
}
