// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.impl.store.access.btree.BTreeMaxScan;

public class B2IMaxScan extends BTreeMaxScan
{
    private ConglomerateController base_cc_for_locking;
    
    B2IMaxScan() {
    }
    
    public void close() throws StandardException {
        super.close();
        if (this.base_cc_for_locking != null) {
            this.base_cc_for_locking.close();
            this.base_cc_for_locking = null;
        }
    }
    
    public boolean closeForEndTransaction(final boolean b) throws StandardException {
        final boolean closeForEndTransaction = super.closeForEndTransaction(b);
        if (this.base_cc_for_locking != null) {
            this.base_cc_for_locking.close();
            this.base_cc_for_locking = null;
        }
        return closeForEndTransaction;
    }
    
    public void init(final TransactionManager transactionManager, final Transaction transaction, final int n, final int n2, final LockingPolicy lockingPolicy, final int n3, final boolean b, final FormatableBitSet set, final B2I b2I, final B2IUndo b2IUndo) throws StandardException {
        this.base_cc_for_locking = transactionManager.openConglomerate(b2I.baseConglomerateId, false, n | 0x40, n2, n3);
        super.init(transactionManager, transaction, false, n, n2, b2I.getBtreeLockingPolicy(transaction, n2, n, n3, this.base_cc_for_locking, this), set, null, 0, null, null, 0, b2I, b2IUndo, null, null);
    }
}
