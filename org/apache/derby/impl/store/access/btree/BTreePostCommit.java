// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.services.daemon.Serviceable;

class BTreePostCommit implements Serviceable
{
    private AccessFactory access_factory;
    private long page_number;
    protected BTree btree;
    
    BTreePostCommit(final AccessFactory access_factory, final BTree btree, final long page_number) {
        this.access_factory = null;
        this.page_number = -1L;
        this.btree = null;
        this.access_factory = access_factory;
        this.btree = btree;
        this.page_number = page_number;
    }
    
    public boolean serviceASAP() {
        return true;
    }
    
    public boolean serviceImmediately() {
        return false;
    }
    
    private final void doShrink(final OpenBTree openBTree, final DataValueDescriptor[] array) throws StandardException {
        ControlRow.get(openBTree, 1L).shrinkFor(openBTree, array);
    }
    
    private final OpenBTree openIndex(final TransactionManager transactionManager, final int n, final int n2) throws StandardException {
        final OpenBTree openBTree = new OpenBTree();
        openBTree.init(null, transactionManager, null, transactionManager.getRawStoreXact(), false, 132, n, this.btree.getBtreeLockingPolicy(transactionManager.getRawStoreXact(), n, n2, 4, this.btree.lockTable(transactionManager, 132, n, 4), openBTree), this.btree, null, null);
        return openBTree;
    }
    
    public int performWork(final ContextManager contextManager) throws StandardException {
        boolean b = false;
        final TransactionManager internalTransaction = ((TransactionManager)this.access_factory.getAndNameTransaction(contextManager, "SystemTransaction")).getInternalTransaction();
        OpenBTree openBTree = null;
        try {
            openBTree = this.openIndex(internalTransaction, 7, 2);
            final DataValueDescriptor[] purgeCommittedDeletes = this.purgeCommittedDeletes(openBTree, this.page_number);
            if (purgeCommittedDeletes != null) {
                this.doShrink(openBTree, purgeCommittedDeletes);
            }
        }
        catch (StandardException ex) {
            if (ex.isLockTimeoutOrDeadlock()) {
                try {
                    openBTree = this.openIndex(internalTransaction, 6, 1);
                    this.purgeRowLevelCommittedDeletes(openBTree);
                }
                catch (StandardException ex2) {
                    if (ex2.isLockTimeoutOrDeadlock()) {
                        b = true;
                    }
                }
            }
        }
        finally {
            if (openBTree != null) {
                openBTree.close();
            }
            internalTransaction.commit();
            internalTransaction.destroy();
        }
        return b ? 2 : 1;
    }
    
    private final DataValueDescriptor[] getShrinkKey(final OpenBTree openBTree, final ControlRow controlRow, final int n) throws StandardException {
        final DataValueDescriptor[] template = openBTree.getConglomerate().createTemplate(openBTree.getRawTran());
        controlRow.page.fetchFromSlot(null, n, template, null, true);
        return template;
    }
    
    private final DataValueDescriptor[] purgeCommittedDeletes(final OpenBTree openBTree, final long n) throws StandardException {
        ControlRow noWait = null;
        DataValueDescriptor[] shrinkKey = null;
        try {
            noWait = ControlRow.getNoWait(openBTree, n);
            if (noWait != null) {
                final Page page = noWait.page;
                if (page.recordCount() - 1 - page.nonDeletedRecordCount() > 0) {
                    for (int i = page.recordCount() - 1; i > 0; --i) {
                        if (page.isDeletedAtSlot(i)) {
                            if (page.recordCount() == 2) {
                                shrinkKey = this.getShrinkKey(openBTree, noWait, i);
                            }
                            page.purgeAtSlot(i, 1, true);
                            page.setRepositionNeeded();
                        }
                    }
                }
                if (page.recordCount() == 1) {}
            }
        }
        finally {
            if (noWait != null) {
                noWait.release();
            }
        }
        return shrinkKey;
    }
    
    private final void purgeRowLevelCommittedDeletes(final OpenBTree openBTree) throws StandardException {
        final LeafControlRow leafControlRow = (LeafControlRow)ControlRow.getNoWait(openBTree, this.page_number);
        if (leafControlRow == null) {
            return;
        }
        final BTreeLockingPolicy lockingPolicy = openBTree.getLockingPolicy();
        if (leafControlRow.page.recordCount() - 1 - leafControlRow.page.nonDeletedRecordCount() > 0) {
            final DataValueDescriptor[] get_template = openBTree.getRuntimeMem().get_template(openBTree.getRawTran());
            final Page page = leafControlRow.page;
            final FetchDescriptor fetchDescriptorConstant = RowUtil.getFetchDescriptorConstant(get_template.length - 1);
            for (int i = page.recordCount() - 1; i > 0; --i) {
                if (page.isDeletedAtSlot(i) && lockingPolicy.lockScanCommittedDeletedRow(openBTree, leafControlRow, get_template, fetchDescriptorConstant, i)) {
                    page.purgeAtSlot(i, 1, true);
                    page.setRepositionNeeded();
                }
            }
        }
    }
}
