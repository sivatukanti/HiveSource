// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.store.raw.RowLock;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.locks.LockFactory;

public class RowLocking2nohold extends RowLocking2
{
    protected RowLocking2nohold(final LockFactory lockFactory) {
        super(lockFactory);
    }
    
    public boolean lockRecordForRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return this.lf.zeroDurationlockObject(transaction.getCompatibilitySpace(), recordHandle, b2 ? RowLock.RU2 : RowLock.RS2, b ? -2 : 0);
    }
    
    public void unlockRecordAfterRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) {
    }
}
