// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.LockingPolicy;

class NoLocking implements LockingPolicy
{
    protected NoLocking() {
    }
    
    public boolean lockContainer(final Transaction transaction, final ContainerHandle containerHandle, final boolean b, final boolean b2) throws StandardException {
        return true;
    }
    
    public void unlockContainer(final Transaction transaction, final ContainerHandle containerHandle) {
    }
    
    public boolean lockRecordForRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return true;
    }
    
    public boolean zeroDurationLockRecordForWrite(final Transaction transaction, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return true;
    }
    
    public boolean lockRecordForWrite(final Transaction transaction, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        return true;
    }
    
    public void unlockRecordAfterRead(final Transaction transaction, final ContainerHandle containerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
    }
    
    public int getMode() {
        return 0;
    }
}
