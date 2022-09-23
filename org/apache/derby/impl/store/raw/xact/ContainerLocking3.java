// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.ContainerLock;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.locks.LockFactory;

public class ContainerLocking3 extends NoLocking
{
    protected final LockFactory lf;
    
    protected ContainerLocking3(final LockFactory lf) {
        this.lf = lf;
    }
    
    public boolean lockContainer(final Transaction transaction, final ContainerHandle containerHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.lockObject(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), b2 ? ContainerLock.CX : ContainerLock.CS, b ? -2 : 0);
    }
    
    public int getMode() {
        return 2;
    }
}
