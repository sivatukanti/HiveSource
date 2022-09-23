// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.RowLock;
import org.apache.derby.iapi.services.locks.LockFactory;

public class RowLockingRR extends RowLocking3
{
    protected RowLockingRR(final LockFactory lockFactory) {
        super(lockFactory);
    }
    
    protected RowLock getReadLockType() {
        return RowLock.RS2;
    }
    
    protected RowLock getUpdateLockType() {
        return RowLock.RU2;
    }
    
    protected RowLock getWriteLockType() {
        return RowLock.RX2;
    }
    
    public void unlockRecordAfterRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        if (!b2) {
            this.lf.unlock(transaction.getCompatibilitySpace(), transaction, recordHandle, b ? RowLock.RU2 : RowLock.RS2);
        }
    }
}
