// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.SpaceInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerateScratchSpace;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;

public class OpenBTree
{
    private BTree init_conglomerate;
    private TransactionManager init_xact_manager;
    private Transaction init_rawtran;
    private int init_openmode;
    protected int init_lock_level;
    private boolean init_hold;
    private BTreeLockingPolicy init_btree_locking_policy;
    protected ContainerHandle container;
    protected long err_containerid;
    protected TransactionManager init_open_user_scans;
    protected LogicalUndo btree_undo;
    protected OpenConglomerateScratchSpace runtime_mem;
    
    public OpenBTree() {
        this.init_open_user_scans = null;
        this.btree_undo = null;
    }
    
    public final TransactionManager getXactMgr() {
        return this.init_xact_manager;
    }
    
    public final Transaction getRawTran() {
        return this.init_rawtran;
    }
    
    public final int getLockLevel() {
        return this.init_lock_level;
    }
    
    public final ContainerHandle getContainer() {
        return this.container;
    }
    
    public final int getOpenMode() {
        return this.init_openmode;
    }
    
    public final BTree getConglomerate() {
        return this.init_conglomerate;
    }
    
    public final boolean getHold() {
        return this.init_hold;
    }
    
    public final BTreeLockingPolicy getLockingPolicy() {
        return this.init_btree_locking_policy;
    }
    
    public final void setLockingPolicy(final BTreeLockingPolicy init_btree_locking_policy) {
        this.init_btree_locking_policy = init_btree_locking_policy;
    }
    
    public final boolean isClosed() {
        return this.container == null;
    }
    
    public final OpenConglomerateScratchSpace getRuntimeMem() {
        return this.runtime_mem;
    }
    
    public long getEstimatedRowCount() throws StandardException {
        if (this.container == null) {
            this.reopen();
        }
        final long estimatedRowCount = this.container.getEstimatedRowCount(0);
        return (estimatedRowCount == 0L) ? 1L : estimatedRowCount;
    }
    
    public void setEstimatedRowCount(final long n) throws StandardException {
        if (this.container == null) {
            this.reopen();
        }
        this.container.setEstimatedRowCount(n, 0);
    }
    
    public void checkConsistency() throws StandardException {
        ControlRow value = null;
        try {
            if (this.container == null) {
                throw StandardException.newException("XSCB8.S", new Long(this.err_containerid));
            }
            value = ControlRow.get(this, 1L);
            value.checkConsistency(this, null, true);
        }
        finally {
            if (value != null) {
                value.release();
            }
        }
    }
    
    public boolean isTableLocked() {
        return this.init_lock_level == 7;
    }
    
    public void init(final TransactionManager init_open_user_scans, final TransactionManager init_xact_manager, final ContainerHandle container, final Transaction init_rawtran, final boolean init_hold, int init_openmode, final int init_lock_level, final BTreeLockingPolicy init_btree_locking_policy, final BTree init_conglomerate, final LogicalUndo btree_undo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        if (this.container != null) {
            this.close();
        }
        this.err_containerid = init_conglomerate.id.getContainerId();
        this.init_btree_locking_policy = init_btree_locking_policy;
        if (init_conglomerate.isTemporary()) {
            init_openmode |= 0x800;
        }
        if (container == null) {
            this.container = init_rawtran.openContainer(init_conglomerate.id, null, init_openmode);
        }
        else {
            this.container = container;
        }
        if (this.container == null) {
            throw StandardException.newException("XSCB1.S", new Long(this.err_containerid));
        }
        this.init_conglomerate = init_conglomerate;
        this.init_xact_manager = init_xact_manager;
        this.init_rawtran = init_rawtran;
        this.init_openmode = init_openmode;
        this.init_lock_level = init_lock_level;
        this.init_hold = init_hold;
        this.init_open_user_scans = init_open_user_scans;
        this.btree_undo = btree_undo;
        this.runtime_mem = (OpenConglomerateScratchSpace)((dynamicCompiledOpenConglomInfo != null) ? dynamicCompiledOpenConglomInfo : ((OpenConglomerateScratchSpace)init_conglomerate.getDynamicCompiledConglomInfo()));
    }
    
    public ContainerHandle reopen() throws StandardException {
        if (this.container == null) {
            this.container = this.init_xact_manager.getRawStoreXact().openContainer(this.init_conglomerate.id, null, this.init_openmode);
        }
        return this.container;
    }
    
    public void close() throws StandardException {
        if (this.container != null) {
            this.container.close();
        }
        this.container = null;
    }
    
    void isIndexableRowConsistent(final DataValueDescriptor[] array) throws StandardException {
    }
    
    public ContainerHandle getContainerHandle() {
        return this.container;
    }
    
    public int getHeight() throws StandardException {
        ControlRow value = null;
        try {
            value = ControlRow.get(this, 1L);
            return value.getLevel() + 1;
        }
        finally {
            if (value != null) {
                value.release();
            }
        }
    }
    
    public void debugConglomerate() throws StandardException {
        ControlRow value = null;
        try {
            value = ControlRow.get(this, 1L);
            value.printTree(this);
        }
        finally {
            if (value != null) {
                value.release();
            }
        }
    }
    
    public static boolean test_errors(final OpenBTree openBTree, final String s, final BTreeRowPosition bTreeRowPosition, final BTreeLockingPolicy bTreeLockingPolicy, final LeafControlRow leafControlRow, final boolean b) throws StandardException {
        return b;
    }
    
    public SpaceInfo getSpaceInfo() throws StandardException {
        return this.container.getSpaceInfo();
    }
    
    public boolean[] getColumnSortOrderInfo() throws StandardException {
        return this.init_conglomerate.ascDescInfo;
    }
}
