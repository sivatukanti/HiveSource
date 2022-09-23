// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.impl.store.access.btree.BTreeForwardScan;

public class B2IForwardScan extends BTreeForwardScan
{
    private ConglomerateController base_cc_for_locking;
    private int init_isolation_level;
    
    B2IForwardScan() {
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
    
    public ContainerHandle reopen() throws StandardException {
        final ContainerHandle reopen = super.reopen();
        final B2I b2I = (B2I)this.getConglomerate();
        this.base_cc_for_locking = this.getXactMgr().openConglomerate(b2I.baseConglomerateId, false, this.getOpenMode() | 0x40, this.init_lock_level, this.init_isolation_level);
        this.setLockingPolicy(b2I.getBtreeLockingPolicy(this.getXactMgr().getRawStoreXact(), this.getLockLevel(), this.getOpenMode(), this.init_isolation_level, this.base_cc_for_locking, this));
        return reopen;
    }
    
    public void init(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final int init_isolation_level, final boolean b2, final FormatableBitSet set, final DataValueDescriptor[] array, final int n3, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n4, final B2I b2I, final B2IUndo b2IUndo, final B2IStaticCompiledInfo b2IStaticCompiledInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final int n5 = n | 0x40;
        if (b2IStaticCompiledInfo != null) {
            this.base_cc_for_locking = transactionManager.openCompiledConglomerate(false, n5, n2, init_isolation_level, b2IStaticCompiledInfo.base_table_static_info, ((Conglomerate)b2IStaticCompiledInfo.getConglom()).getDynamicCompiledConglomInfo());
        }
        else {
            this.base_cc_for_locking = transactionManager.openConglomerate(b2I.baseConglomerateId, false, n5, n2, init_isolation_level);
        }
        super.init(transactionManager, transaction, b, n, n2, b2I.getBtreeLockingPolicy(transaction, n2, n, init_isolation_level, this.base_cc_for_locking, this), set, array, n3, array2, array3, n4, b2I, b2IUndo, b2IStaticCompiledInfo, dynamicCompiledOpenConglomInfo);
        this.init_isolation_level = init_isolation_level;
    }
}
