// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Enumeration;

class LockList implements Enumeration
{
    private Enumeration lockGroup;
    
    LockList(final Enumeration lockGroup) {
        this.lockGroup = lockGroup;
    }
    
    public boolean hasMoreElements() {
        return this.lockGroup.hasMoreElements();
    }
    
    public Object nextElement() {
        return this.lockGroup.nextElement().getLockable();
    }
}
