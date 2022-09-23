// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.impl.store.access.btree.BTreeController;

public class B2IController extends BTreeController
{
    private ConglomerateController base_cc_for_locking;
    
    B2IController() {
    }
    
    void init(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final boolean b2, final B2I b2I, final B2IUndo b2IUndo, final B2IStaticCompiledInfo b2IStaticCompiledInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final int n3 = n | 0x40;
        if (b2IStaticCompiledInfo != null) {
            this.base_cc_for_locking = transactionManager.openCompiledConglomerate(false, n3, n2, 4, b2IStaticCompiledInfo.base_table_static_info, ((Conglomerate)b2IStaticCompiledInfo.getConglom()).getDynamicCompiledConglomInfo());
        }
        else {
            this.base_cc_for_locking = transactionManager.openConglomerate(b2I.baseConglomerateId, false, n3, n2, 4);
        }
        BTreeLockingPolicy bTreeLockingPolicy;
        if (n2 == 7) {
            bTreeLockingPolicy = new B2ITableLocking3(transaction, n2, lockingPolicy, this.base_cc_for_locking, this);
        }
        else if (n2 == 6) {
            bTreeLockingPolicy = new B2IRowLocking3(transaction, n2, lockingPolicy, this.base_cc_for_locking, this);
        }
        else {
            bTreeLockingPolicy = null;
        }
        super.init(transactionManager, b, null, transaction, n, n2, bTreeLockingPolicy, b2I, b2IUndo, b2IStaticCompiledInfo, dynamicCompiledOpenConglomInfo);
    }
    
    public void close() throws StandardException {
        super.close();
        if (this.base_cc_for_locking != null) {
            this.base_cc_for_locking.close();
            this.base_cc_for_locking = null;
        }
    }
    
    public int insert(final DataValueDescriptor[] array) throws StandardException {
        return super.insert(array);
    }
}
