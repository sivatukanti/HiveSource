// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.BTreeRowPosition;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;

class B2IRowLocking2 extends B2IRowLockingRR implements BTreeLockingPolicy
{
    B2IRowLocking2(final Transaction transaction, final int n, final LockingPolicy lockingPolicy, final ConglomerateController conglomerateController, final OpenBTree openBTree) {
        super(transaction, n, lockingPolicy, conglomerateController, openBTree);
    }
    
    public void unlockScanRecordAfterRead(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
        this.base_cc.unlockRowAfterRead(bTreeRowPosition.current_lock_row_loc, b, false);
    }
}
