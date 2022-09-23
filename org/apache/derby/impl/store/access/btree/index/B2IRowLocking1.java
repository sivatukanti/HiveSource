// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.impl.store.access.btree.BTreeRowPosition;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;

class B2IRowLocking1 extends B2IRowLocking2 implements BTreeLockingPolicy
{
    B2IRowLocking1(final Transaction transaction, final int n, final LockingPolicy lockingPolicy, final ConglomerateController conglomerateController, final OpenBTree openBTree) {
        super(transaction, n, lockingPolicy, conglomerateController, openBTree);
    }
    
    public boolean lockScanRow(final OpenBTree openBTree, final BTreeRowPosition bTreeRowPosition, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final boolean b, final boolean b2, final int n) throws StandardException {
        return this._lockScanRow(openBTree, bTreeRowPosition, b2 && !b, fetchDescriptor, array, rowLocation, b, b2, n);
    }
    
    public void unlockScanRecordAfterRead(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
        if (b) {
            super.unlockScanRecordAfterRead(bTreeRowPosition, b);
        }
    }
}
