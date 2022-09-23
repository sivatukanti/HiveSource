// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.ScanInfo;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;

public abstract class BTreeScan extends OpenBTree implements ScanManager
{
    protected Transaction init_rawtran;
    protected boolean init_forUpdate;
    protected FormatableBitSet init_scanColumnList;
    protected DataValueDescriptor[] init_template;
    protected DataValueDescriptor[] init_startKeyValue;
    protected int init_startSearchOperator;
    protected Qualifier[][] init_qualifier;
    protected DataValueDescriptor[] init_stopKeyValue;
    protected int init_stopSearchOperator;
    protected boolean init_hold;
    protected FetchDescriptor init_fetchDesc;
    protected FetchDescriptor init_lock_fetch_desc;
    BTreeRowPosition scan_position;
    protected boolean init_useUpdateLocks;
    protected static final int SCAN_INIT = 1;
    protected static final int SCAN_INPROGRESS = 2;
    protected static final int SCAN_DONE = 3;
    protected static final int SCAN_HOLD_INIT = 4;
    protected static final int SCAN_HOLD_INPROGRESS = 5;
    protected int scan_state;
    protected int stat_numpages_visited;
    protected int stat_numrows_visited;
    protected int stat_numrows_qualified;
    protected int stat_numdeleted_rows_visited;
    protected int lock_operation;
    protected DataValueDescriptor[][] fetchNext_one_slot_array;
    
    public BTreeScan() {
        this.init_rawtran = null;
        this.init_startSearchOperator = 0;
        this.init_qualifier = null;
        this.init_stopSearchOperator = 0;
        this.init_useUpdateLocks = false;
        this.scan_state = 1;
        this.stat_numpages_visited = 0;
        this.stat_numrows_visited = 0;
        this.stat_numrows_qualified = 0;
        this.stat_numdeleted_rows_visited = 0;
        this.fetchNext_one_slot_array = new DataValueDescriptor[1][];
    }
    
    protected abstract int fetchRows(final BTreeRowPosition p0, final DataValueDescriptor[][] p1, final RowLocation[] p2, final BackingStoreHashtable p3, final long p4, final int[] p5) throws StandardException;
    
    private void initScanParams(final DataValueDescriptor[] init_startKeyValue, final int init_startSearchOperator, Qualifier[][] init_qualifier, final DataValueDescriptor[] init_stopKeyValue, final int init_stopSearchOperator) throws StandardException {
        this.init_startKeyValue = init_startKeyValue;
        if (RowUtil.isRowEmpty(this.init_startKeyValue)) {
            this.init_startKeyValue = null;
        }
        this.init_startSearchOperator = init_startSearchOperator;
        if (init_qualifier != null && init_qualifier.length == 0) {
            init_qualifier = null;
        }
        this.init_qualifier = init_qualifier;
        this.init_stopKeyValue = init_stopKeyValue;
        if (RowUtil.isRowEmpty(this.init_stopKeyValue)) {
            this.init_stopKeyValue = null;
        }
        this.init_stopSearchOperator = init_stopSearchOperator;
        (this.scan_position = new BTreeRowPosition(this)).init();
        (this.scan_position.current_lock_template = new DataValueDescriptor[this.init_template.length])[this.init_template.length - 1] = (this.scan_position.current_lock_row_loc = (RowLocation)this.init_template[this.init_template.length - 1].cloneValue(false));
    }
    
    protected void positionAtStartForForwardScan(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        while (true) {
            final ControlRow value = ControlRow.get(this, 1L);
            this.stat_numpages_visited += value.getLevel() + 1;
            boolean b = true;
            if (this.init_startKeyValue == null) {
                bTreeRowPosition.current_leaf = (LeafControlRow)value.searchLeft(this);
                bTreeRowPosition.current_slot = 0;
            }
            else {
                final SearchParameters searchParameters = new SearchParameters(this.init_startKeyValue, (this.init_startSearchOperator == 1) ? 1 : -1, this.init_template, this, false);
                bTreeRowPosition.current_leaf = (LeafControlRow)value.search(searchParameters);
                bTreeRowPosition.current_slot = searchParameters.resultSlot;
                if (searchParameters.resultExact && this.init_startSearchOperator == 1) {
                    --bTreeRowPosition.current_slot;
                    if (this.getConglomerate().nUniqueColumns < this.getConglomerate().nKeyFields) {
                        b = false;
                    }
                }
            }
            int n = 0;
            if (b) {
                n = (this.getLockingPolicy().lockScanRow(this, bTreeRowPosition, this.init_lock_fetch_desc, bTreeRowPosition.current_lock_template, bTreeRowPosition.current_lock_row_loc, true, this.init_forUpdate, this.lock_operation) ? 0 : 1);
            }
            if (n == 0) {
                break;
            }
            bTreeRowPosition.init();
        }
        this.scan_state = 2;
    }
    
    protected void positionAtNextPage(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        bTreeRowPosition.next_leaf = (LeafControlRow)bTreeRowPosition.current_leaf.getRightSibling(this);
        if (bTreeRowPosition.current_rh != null) {
            this.getLockingPolicy().unlockScanRecordAfterRead(bTreeRowPosition, this.init_forUpdate);
        }
        bTreeRowPosition.current_leaf.release();
        bTreeRowPosition.current_leaf = bTreeRowPosition.next_leaf;
        bTreeRowPosition.current_slot = 0;
        bTreeRowPosition.current_rh = null;
    }
    
    protected void positionAtPreviousPage() throws StandardException, WaitError {
        final BTreeRowPosition scan_position = this.scan_position;
        LeafControlRow current_leaf;
        LeafControlRow leafControlRow;
        for (current_leaf = (LeafControlRow)scan_position.current_leaf.getLeftSibling(this); current_leaf != null && isEmpty(current_leaf.page); current_leaf = leafControlRow) {
            try {
                leafControlRow = (LeafControlRow)current_leaf.getLeftSibling(this);
            }
            finally {
                current_leaf.release();
            }
        }
        if (scan_position.current_rh != null) {
            this.getLockingPolicy().unlockScanRecordAfterRead(scan_position, this.init_forUpdate);
        }
        scan_position.current_leaf.release();
        scan_position.current_leaf = current_leaf;
        scan_position.current_slot = ((scan_position.current_leaf == null) ? -1 : scan_position.current_leaf.page.recordCount());
        scan_position.current_rh = null;
    }
    
    static boolean isEmpty(final Page page) throws StandardException {
        return page.recordCount() <= 1;
    }
    
    abstract void positionAtStartPosition(final BTreeRowPosition p0) throws StandardException;
    
    protected void positionAtDoneScanFromClose(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        if (bTreeRowPosition.current_rh != null && !bTreeRowPosition.current_rh_qualified && (bTreeRowPosition.current_leaf == null || bTreeRowPosition.current_leaf.page == null)) {
            this.getLockingPolicy().unlockScanRecordAfterRead(bTreeRowPosition, this.init_forUpdate);
        }
        bTreeRowPosition.current_slot = -1;
        bTreeRowPosition.current_rh = null;
        bTreeRowPosition.current_positionKey = null;
        this.scan_state = 3;
    }
    
    protected void positionAtDoneScan(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        bTreeRowPosition.current_slot = -1;
        bTreeRowPosition.current_rh = null;
        bTreeRowPosition.current_positionKey = null;
        this.scan_state = 3;
    }
    
    protected boolean process_qualifier(final DataValueDescriptor[] array) throws StandardException {
        boolean b = true;
        for (int i = 0; i < this.init_qualifier[0].length; ++i) {
            final Qualifier qualifier = this.init_qualifier[0][i];
            b = array[qualifier.getColumnId()].compare(qualifier.getOperator(), qualifier.getOrderable(), qualifier.getOrderedNulls(), qualifier.getUnknownRV());
            if (qualifier.negateCompareResult()) {
                b = !b;
            }
            if (!b) {
                return false;
            }
        }
        for (int j = 1; j < this.init_qualifier.length; ++j) {
            b = false;
            for (int k = 0; k < this.init_qualifier[j].length; ++k) {
                final Qualifier qualifier2 = this.init_qualifier[j][k];
                b = array[qualifier2.getColumnId()].compare(qualifier2.getOperator(), qualifier2.getOrderable(), qualifier2.getOrderedNulls(), qualifier2.getUnknownRV());
                if (qualifier2.negateCompareResult()) {
                    b = !b;
                }
                if (b) {
                    break;
                }
            }
            if (!b) {
                break;
            }
        }
        return b;
    }
    
    protected boolean reposition(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSCB4.S", new Integer(this.scan_state));
        }
        if (bTreeRowPosition.current_positionKey == null) {
            throw StandardException.newException("XSCB7.S", new Boolean(bTreeRowPosition.current_rh == null), new Boolean(bTreeRowPosition.current_positionKey == null));
        }
        if (bTreeRowPosition.current_rh != null) {
            final Page page = this.container.getPage(bTreeRowPosition.current_rh.getPageNumber());
            if (page != null) {
                final ControlRow controlRowForPage = ControlRow.getControlRowForPage(this.container, page);
                if (controlRowForPage instanceof LeafControlRow && !controlRowForPage.page.isRepositionNeeded(bTreeRowPosition.versionWhenSaved)) {
                    bTreeRowPosition.current_leaf = (LeafControlRow)controlRowForPage;
                    bTreeRowPosition.current_slot = controlRowForPage.page.getSlotNumber(bTreeRowPosition.current_rh);
                    bTreeRowPosition.current_positionKey = null;
                    return true;
                }
                controlRowForPage.release();
            }
        }
        final SearchParameters searchParameters = new SearchParameters(bTreeRowPosition.current_positionKey, 1, this.init_template, this, false);
        bTreeRowPosition.current_leaf = (LeafControlRow)ControlRow.get(this, 1L).search(searchParameters);
        if (!searchParameters.resultExact && !b) {
            bTreeRowPosition.current_leaf.release();
            bTreeRowPosition.current_leaf = null;
            return false;
        }
        bTreeRowPosition.current_slot = searchParameters.resultSlot;
        if (bTreeRowPosition.current_rh != null) {
            bTreeRowPosition.current_rh = bTreeRowPosition.current_leaf.page.getRecordHandleAtSlot(bTreeRowPosition.current_slot);
        }
        bTreeRowPosition.current_positionKey = null;
        return true;
    }
    
    public void init(final TransactionManager transactionManager, final Transaction init_rawtran, final boolean init_hold, final int n, final int n2, final BTreeLockingPolicy bTreeLockingPolicy, final FormatableBitSet init_scanColumnList, final DataValueDescriptor[] array, final int n3, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n4, final BTree bTree, final LogicalUndo logicalUndo, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        super.init(transactionManager, transactionManager, null, init_rawtran, init_hold, n, n2, bTreeLockingPolicy, bTree, logicalUndo, dynamicCompiledOpenConglomInfo);
        this.init_rawtran = init_rawtran;
        this.init_forUpdate = ((n & 0x4) == 0x4);
        this.init_useUpdateLocks = ((n & 0x1000) != 0x0);
        this.init_hold = init_hold;
        this.init_template = this.runtime_mem.get_template(this.getRawTran());
        this.init_scanColumnList = init_scanColumnList;
        this.init_lock_fetch_desc = RowUtil.getFetchDescriptorConstant(this.init_template.length - 1);
        this.init_fetchDesc = new FetchDescriptor(this.init_template.length, this.init_scanColumnList, null);
        this.initScanParams(array, n3, array2, array3, n4);
        this.lock_operation = (this.init_forUpdate ? 1 : 0);
        if (this.init_useUpdateLocks) {
            this.lock_operation |= 0x8;
        }
    }
    
    public void close() throws StandardException {
        this.positionAtDoneScanFromClose(this.scan_position);
        super.close();
        this.init_rawtran = null;
        this.init_template = null;
        this.init_startKeyValue = null;
        this.init_qualifier = null;
        this.init_stopKeyValue = null;
        this.getXactMgr().closeMe(this);
    }
    
    public boolean delete() throws StandardException {
        boolean b = false;
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        try {
            if (!this.reposition(this.scan_position, false)) {
                throw StandardException.newException("XSAM6.S", new Long(this.err_containerid), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
            }
            if (this.init_useUpdateLocks && !this.getLockingPolicy().lockScanRow(this, this.scan_position, this.init_lock_fetch_desc, this.scan_position.current_lock_template, this.scan_position.current_lock_row_loc, false, this.init_forUpdate, this.lock_operation) && !this.reposition(this.scan_position, false)) {
                throw StandardException.newException("XSAM6.S", new Long(this.err_containerid), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
            }
            if (this.scan_position.current_leaf.page.isDeletedAtSlot(this.scan_position.current_slot)) {
                b = false;
            }
            else {
                this.scan_position.current_leaf.page.deleteAtSlot(this.scan_position.current_slot, true, this.btree_undo);
                b = true;
            }
            if (this.scan_position.current_leaf.page.nonDeletedRecordCount() == 1 && (!this.scan_position.current_leaf.getIsRoot() || this.scan_position.current_leaf.getLevel() != 0)) {
                this.getXactMgr().addPostCommitWork(new BTreePostCommit(this.getXactMgr().getAccessManager(), this.getConglomerate(), this.scan_position.current_leaf.page.getPageNumber()));
            }
        }
        finally {
            if (this.scan_position.current_leaf != null) {
                this.savePositionAndReleasePage();
            }
        }
        return b;
    }
    
    public void didNotQualify() throws StandardException {
    }
    
    public boolean doesCurrentPositionQualify() throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        try {
            return this.reposition(this.scan_position, false) && !this.scan_position.current_leaf.page.isDeletedAtSlot(this.scan_position.current_slot);
        }
        finally {
            if (this.scan_position.current_leaf != null) {
                this.savePositionAndReleasePage();
            }
        }
    }
    
    private void fetch(final DataValueDescriptor[] array, final boolean b) throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        try {
            if (!this.reposition(this.scan_position, false)) {
                throw StandardException.newException("XSAM6.S", new Long(this.err_containerid), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
            }
            this.scan_position.current_rh = this.scan_position.current_leaf.page.fetchFromSlot(null, this.scan_position.current_slot, array, b ? this.init_fetchDesc : null, true);
            if (this.scan_position.current_leaf.page.isDeletedAtSlot(this.scan_position.current_slot)) {}
        }
        finally {
            if (this.scan_position.current_leaf != null) {
                this.savePositionAndReleasePage();
            }
        }
    }
    
    public boolean isHeldAfterCommit() throws StandardException {
        return this.scan_state == 4 || this.scan_state == 5;
    }
    
    public void fetch(final DataValueDescriptor[] array) throws StandardException {
        this.fetch(array, true);
    }
    
    public void fetchWithoutQualify(final DataValueDescriptor[] array) throws StandardException {
        this.fetch(array, false);
    }
    
    public ScanInfo getScanInfo() throws StandardException {
        return new BTreeScanInfo(this);
    }
    
    public boolean isCurrentPositionDeleted() throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        boolean b;
        try {
            b = (this.reposition(this.scan_position, false) && this.scan_position.current_leaf.page.isDeletedAtSlot(this.scan_position.current_slot));
        }
        finally {
            if (this.scan_position.current_leaf != null) {
                this.savePositionAndReleasePage();
            }
        }
        return b;
    }
    
    public boolean isKeyed() {
        return true;
    }
    
    public boolean positionAtRowLocation(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean next() throws StandardException {
        this.fetchNext_one_slot_array[0] = this.runtime_mem.get_scratch_row(this.getRawTran());
        return this.fetchRows(this.scan_position, this.fetchNext_one_slot_array, null, null, 1L, null) == 1;
    }
    
    public boolean fetchNext(final DataValueDescriptor[] array) throws StandardException {
        this.fetchNext_one_slot_array[0] = array;
        return this.fetchRows(this.scan_position, this.fetchNext_one_slot_array, null, null, 1L, null) == 1;
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2) throws StandardException {
        return this.fetchRows(this.scan_position, array, array2, null, array.length, null);
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2, final RowLocation[] array3) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public void fetchSet(final long n, final int[] array, final BackingStoreHashtable backingStoreHashtable) throws StandardException {
        this.fetchRows(this.scan_position, null, null, backingStoreHashtable, n, array);
    }
    
    public final void reopenScan(final DataValueDescriptor[] array, final int n, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n2) throws StandardException {
        if (this.scan_position.current_rh != null) {
            this.getLockingPolicy().unlockScanRecordAfterRead(this.scan_position, this.init_forUpdate);
        }
        this.scan_position.current_slot = -1;
        this.scan_position.current_rh = null;
        this.scan_position.current_positionKey = null;
        this.initScanParams(array, n, array2, array3, n2);
        if (!this.init_hold) {
            this.scan_state = 1;
        }
        else {
            this.scan_state = ((this.container != null) ? 1 : 4);
        }
    }
    
    public void reopenScanByRowLocation(final RowLocation rowLocation, final Qualifier[][] array) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public void fetchLocation(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean replace(final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean closeForEndTransaction(final boolean b) throws StandardException {
        if (!this.init_hold || b) {
            this.positionAtDoneScan(this.scan_position);
            super.close();
            this.init_rawtran = null;
            this.init_template = null;
            this.init_startKeyValue = null;
            this.init_qualifier = null;
            this.init_stopKeyValue = null;
            this.getXactMgr().closeMe(this);
            return true;
        }
        if (this.scan_state == 2) {
            this.scan_position.current_rh = null;
            this.scan_state = 5;
        }
        else if (this.scan_state == 1) {
            this.scan_state = 4;
        }
        super.close();
        return false;
    }
    
    void savePositionAndReleasePage(final DataValueDescriptor[] array, final int[] array2) throws StandardException {
        final Page page = this.scan_position.current_leaf.getPage();
        try {
            final DataValueDescriptor[] keyTemplate = this.scan_position.getKeyTemplate();
            FetchDescriptor fetchDescriptorForSaveKey = null;
            boolean b = false;
            if (array != null) {
                int n = 0;
                for (int n2 = (array2 == null) ? array.length : array2.length, i = 0; i < n2; ++i) {
                    if (array2 == null || array2[i] != 0) {
                        keyTemplate[i].setValue(array[i]);
                        ++n;
                    }
                }
                if (n < keyTemplate.length) {
                    fetchDescriptorForSaveKey = this.scan_position.getFetchDescriptorForSaveKey(array2, keyTemplate.length);
                }
                else {
                    b = true;
                }
            }
            if (!b) {
                page.fetchFromSlot(null, this.scan_position.current_slot, keyTemplate, fetchDescriptorForSaveKey, true);
            }
            this.scan_position.current_positionKey = keyTemplate;
            this.scan_position.versionWhenSaved = page.getPageVersion();
            this.scan_position.current_slot = -1;
        }
        finally {
            this.scan_position.current_leaf.release();
            this.scan_position.current_leaf = null;
        }
    }
    
    void savePositionAndReleasePage() throws StandardException {
        this.savePositionAndReleasePage(null, null);
    }
    
    public RecordHandle getCurrentRecordHandleForDebugging() {
        return this.scan_position.current_rh;
    }
    
    public String toString() {
        return null;
    }
}
