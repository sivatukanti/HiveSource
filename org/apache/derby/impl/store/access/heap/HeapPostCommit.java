// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.services.daemon.Serviceable;

class HeapPostCommit implements Serviceable
{
    private AccessFactory access_factory;
    private Heap heap;
    private long page_number;
    
    HeapPostCommit(final AccessFactory access_factory, final Heap heap, final long page_number) {
        this.access_factory = null;
        this.heap = null;
        this.page_number = -1L;
        this.access_factory = access_factory;
        this.heap = heap;
        this.page_number = page_number;
    }
    
    private final void purgeCommittedDeletes(final HeapController heapController, final long n) throws StandardException {
        final Page userPageWait = heapController.getUserPageWait(n);
        boolean b = false;
        if (userPageWait != null) {
            try {
                if (userPageWait.recordCount() - userPageWait.nonDeletedRecordCount() > 0) {
                    for (int i = userPageWait.recordCount() - 1; i >= 0; --i) {
                        if (userPageWait.isDeletedAtSlot(i) && heapController.lockRowAtSlotNoWaitExclusive(userPageWait.fetchFromSlot(null, i, RowUtil.EMPTY_ROW, RowUtil.EMPTY_ROW_FETCH_DESCRIPTOR, true))) {
                            b = true;
                            userPageWait.purgeAtSlot(i, 1, false);
                        }
                    }
                }
                if (userPageWait.recordCount() == 0) {
                    b = true;
                    heapController.removePage(userPageWait);
                }
            }
            finally {
                if (!b) {
                    userPageWait.unlatch();
                }
            }
        }
    }
    
    public boolean serviceASAP() {
        return true;
    }
    
    public boolean serviceImmediately() {
        return false;
    }
    
    public int performWork(final ContextManager contextManager) throws StandardException {
        final TransactionManager internalTransaction = ((TransactionManager)this.access_factory.getAndNameTransaction(contextManager, "SystemTransaction")).getInternalTransaction();
        boolean b = false;
        try {
            this.purgeCommittedDeletes((HeapController)this.heap.open(internalTransaction, internalTransaction.getRawStoreXact(), false, 132, 6, internalTransaction.getRawStoreXact().newLockingPolicy(1, 4, true), this.heap, null), this.page_number);
        }
        catch (StandardException ex) {
            if (ex.isLockTimeoutOrDeadlock()) {
                b = true;
            }
        }
        internalTransaction.commitNoSync(1);
        internalTransaction.destroy();
        return b ? 2 : 1;
    }
}
