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

class B2IRowLockingRR extends B2IRowLocking3 implements BTreeLockingPolicy
{
    B2IRowLockingRR(final Transaction transaction, final int n, final LockingPolicy lockingPolicy, final ConglomerateController conglomerateController, final OpenBTree openBTree) {
        super(transaction, n, lockingPolicy, conglomerateController, openBTree);
    }
    
    public boolean lockScanRow(final OpenBTree openBTree, final BTreeRowPosition bTreeRowPosition, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final boolean b, final boolean b2, final int n) throws StandardException {
        return this._lockScanRow(openBTree, bTreeRowPosition, !b, fetchDescriptor, array, rowLocation, b, b2, n);
    }
    
    public void unlockScanRecordAfterRead(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
        if (!bTreeRowPosition.current_rh_qualified) {
            this.base_cc.unlockRowAfterRead(bTreeRowPosition.current_lock_row_loc, b, bTreeRowPosition.current_rh_qualified);
        }
    }
}
