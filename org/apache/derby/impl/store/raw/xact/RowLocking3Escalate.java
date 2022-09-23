// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.ContainerLock;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.locks.LockFactory;

public class RowLocking3Escalate extends ContainerLocking3
{
    protected RowLocking3Escalate(final LockFactory lockFactory) {
        super(lockFactory);
    }
    
    public boolean lockContainer(final Transaction transaction, final ContainerHandle containerHandle, final boolean b, final boolean b2) throws StandardException {
        boolean b3 = false;
        if (this.lf.isLockHeld(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), ContainerLock.CIX)) {
            b3 = true;
        }
        if (!super.lockContainer(transaction, containerHandle, b, b3)) {
            return false;
        }
        this.lf.unlockGroup(transaction.getCompatibilitySpace(), transaction, new EscalateContainerKey(containerHandle.getId()));
        return true;
    }
}
