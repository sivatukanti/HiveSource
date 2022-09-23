// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

public final class SinglePool extends AbstractPool
{
    protected LockTable createLockTable() {
        return new LockSet(this);
    }
}
