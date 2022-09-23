// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.ContainerLock;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.RowLock;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.store.raw.LockingPolicy;

public class RowLocking3 extends NoLocking
{
    private static final LockingPolicy NO_LOCK;
    protected final LockFactory lf;
    
    protected RowLocking3(final LockFactory lf) {
        this.lf = lf;
    }
    
    protected RowLock getReadLockType() {
        return RowLock.RS3;
    }
    
    protected RowLock getUpdateLockType() {
        return RowLock.RU3;
    }
    
    protected RowLock getWriteLockType() {
        return RowLock.RX3;
    }
    
    public boolean lockContainer(final Transaction transaction, final ContainerHandle containerHandle, final boolean b, final boolean b2) throws StandardException {
        final boolean lockObject = this.lf.lockObject(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), b2 ? ContainerLock.CIX : ContainerLock.CIS, b ? -2 : 0);
        if (lockObject && (this.lf.isLockHeld(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), ContainerLock.CX) || (!b2 && this.lf.isLockHeld(transaction.getCompatibilitySpace(), transaction, containerHandle.getId(), ContainerLock.CS)))) {
            containerHandle.setLockingPolicy(RowLocking3.NO_LOCK);
        }
        return lockObject;
    }
    
    public boolean lockRecordForRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.lockObject(transaction.getCompatibilitySpace(), transaction, recordHandle, b2 ? this.getUpdateLockType() : this.getReadLockType(), b ? -2 : 0);
    }
    
    public boolean zeroDurationLockRecordForWrite(final Transaction transaction, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.zeroDurationlockObject(transaction.getCompatibilitySpace(), recordHandle, b ? RowLock.RIP : this.getWriteLockType(), b2 ? -2 : 0);
    }
    
    public boolean lockRecordForWrite(final Transaction transaction, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.lockObject(transaction.getCompatibilitySpace(), transaction, recordHandle, b ? RowLock.RI : this.getWriteLockType(), b2 ? -2 : 0);
    }
    
    public int getMode() {
        return 1;
    }
    
    static {
        NO_LOCK = new NoLocking();
    }
}
