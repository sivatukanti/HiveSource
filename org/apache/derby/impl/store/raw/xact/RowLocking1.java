// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.locks.LockFactory;

public class RowLocking1 extends RowLocking2
{
    protected RowLocking1(final LockFactory lockFactory) {
        super(lockFactory);
    }
    
    public boolean lockRecordForRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return !b2 || super.lockRecordForRead(transaction, containerHandle, recordHandle, b, b2);
    }
    
    public void unlockRecordAfterRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        if (b) {
            super.unlockRecordAfterRead(transaction, containerHandle, recordHandle, b, b2);
        }
    }
}
