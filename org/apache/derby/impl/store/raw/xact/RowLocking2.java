// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.RowLock;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.ContainerLock;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.store.raw.LockingPolicy;

public class RowLocking2 extends RowLockingRR
{
    private static final LockingPolicy NO_LOCK;
    
    protected RowLocking2(final LockFactory lockFactory) {
        super(lockFactory);
    }
    
    public boolean lockContainer(final Transaction transaction, final ContainerHandle containerHandle, final boolean b, final boolean b2) throws StandardException {
        final ContainerLock containerLock = b2 ? ContainerLock.CIX : ContainerLock.CIS;
        final Object o = b2 ? transaction : containerHandle.getUniqueId();
        final boolean lockObject = this.lf.lockObject(transaction.getCompatibilitySpace(), o, containerHandle.getId(), containerLock, b ? -2 : 0);
        if (lockObject) {
            if (this.lf.isLockHeld(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), ContainerLock.CX)) {
                this.lf.unlockGroup(transaction.getCompatibilitySpace(), containerHandle.getUniqueId());
                containerHandle.setLockingPolicy(RowLocking2.NO_LOCK);
            }
            else if (!b2 && this.lf.isLockHeld(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), ContainerLock.CS)) {
                this.lf.transfer(transaction.getCompatibilitySpace(), o, transaction);
                containerHandle.setLockingPolicy(RowLocking2.NO_LOCK);
            }
        }
        return lockObject;
    }
    
    public boolean lockRecordForRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.lockObject(transaction.getCompatibilitySpace(), containerHandle.getUniqueId(), recordHandle, b2 ? RowLock.RU2 : RowLock.RS2, b ? -2 : 0);
    }
    
    public void unlockRecordAfterRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        this.lf.unlock(transaction.getCompatibilitySpace(), containerHandle.getUniqueId(), recordHandle, b ? RowLock.RU2 : RowLock.RS2);
    }
    
    public void unlockContainer(final Transaction transaction, final ContainerHandle containerHandle) {
        this.lf.unlockGroup(transaction.getCompatibilitySpace(), containerHandle.getUniqueId());
    }
    
    static {
        NO_LOCK = new NoLocking();
    }
}
