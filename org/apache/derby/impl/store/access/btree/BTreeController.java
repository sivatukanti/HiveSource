// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.impl.store.access.conglomerate.ConglomerateUtil;
import java.util.Properties;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.ConglomerateController;

public class BTreeController extends OpenBTree implements ConglomerateController
{
    transient DataValueDescriptor[] scratch_template;
    boolean get_insert_row_lock;
    private static final int NO_MATCH = 0;
    private static final int MATCH_FOUND = 1;
    private static final int RESCAN_REQUIRED = 2;
    
    public BTreeController() {
        this.scratch_template = null;
    }
    
    private boolean reclaim_deleted_rows(final OpenBTree openBTree, final long n) throws StandardException {
        boolean b = false;
        ControlRow value = null;
        try {
            if ((value = ControlRow.get(openBTree, n)) == null) {
                return false;
            }
            final LeafControlRow leafControlRow = (LeafControlRow)value;
            final BTreeLockingPolicy lockingPolicy = openBTree.getLockingPolicy();
            if (leafControlRow.page.recordCount() - 1 - leafControlRow.page.nonDeletedRecordCount() > 0) {
                final Page page = leafControlRow.page;
                final FetchDescriptor fetchDescriptorConstant = RowUtil.getFetchDescriptorConstant(this.scratch_template.length - 1);
                for (int i = page.recordCount() - 1; i > 0; --i) {
                    if (page.isDeletedAtSlot(i) && lockingPolicy.lockScanCommittedDeletedRow(openBTree, leafControlRow, this.scratch_template, fetchDescriptorConstant, i)) {
                        page.purgeAtSlot(i, 1, true);
                        b = true;
                    }
                }
            }
        }
        catch (ClassCastException ex) {}
        finally {
            if (value != null) {
                if (b) {
                    value.page.setRepositionNeeded();
                }
                else {
                    value.release();
                }
            }
        }
        return b;
    }
    
    private long start_xact_and_dosplit(final boolean b, final long n, final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final int n2) throws StandardException {
        final TransactionManager internalTransaction = this.init_open_user_scans.getInternalTransaction();
        int n3 = 1;
        if (b) {
            ConglomerateController lockTable = null;
            try {
                lockTable = this.getConglomerate().lockTable(internalTransaction, 132, 6, 4);
            }
            catch (StandardException ex) {}
            if (lockTable != null) {
                final OpenBTree openBTree = new OpenBTree();
                openBTree.init(this.init_open_user_scans, internalTransaction, null, internalTransaction.getRawStoreXact(), false, 132, 6, this.getConglomerate().getBtreeLockingPolicy(internalTransaction.getRawStoreXact(), 6, 1, 4, lockTable, openBTree), this.getConglomerate(), null, null);
                n3 = (this.reclaim_deleted_rows(openBTree, n) ? 0 : 1);
                openBTree.close();
            }
        }
        long split = n;
        if (n3 != 0) {
            final OpenBTree openBTree2 = new OpenBTree();
            openBTree2.init(this.init_open_user_scans, internalTransaction, null, internalTransaction.getRawStoreXact(), false, this.getOpenMode(), 5, this.getConglomerate().getBtreeLockingPolicy(internalTransaction.getRawStoreXact(), this.init_lock_level, 1, 4, null, openBTree2), this.getConglomerate(), null, null);
            split = ControlRow.get(openBTree2, 1L).splitFor(openBTree2, array, null, array2, n2);
            openBTree2.close();
        }
        internalTransaction.commit();
        internalTransaction.destroy();
        return split;
    }
    
    private int comparePreviousRecord(int n, LeafControlRow leafControlRow, final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        int n2 = 0;
        final LeafControlRow leafControlRow2 = leafControlRow;
        while (leafControlRow != null) {
            if (n == 0) {
                final LeafControlRow leafControlRow3 = leafControlRow;
                try {
                    leafControlRow = (LeafControlRow)leafControlRow.getLeftSibling(this);
                    if (n2 != 0) {
                        leafControlRow3.release();
                    }
                    n2 = 1;
                    if (leafControlRow == null) {
                        return 0;
                    }
                    n = leafControlRow.page.recordCount() - 1;
                    continue;
                }
                catch (WaitError waitError) {
                    if (n2 != 0) {
                        leafControlRow3.release();
                    }
                    leafControlRow2.release();
                    return 2;
                }
            }
            if (leafControlRow.page.fetchFromSlot(null, n, array, null, true) != null) {
                final int compareRowsForInsert = this.compareRowsForInsert(array, array2, leafControlRow, n);
                if (compareRowsForInsert != 1 || !leafControlRow.page.isDeletedAtSlot(n)) {
                    if (n2 != 0) {
                        if (compareRowsForInsert == 2) {
                            leafControlRow2.release();
                        }
                        if (compareRowsForInsert != 2) {
                            leafControlRow.release();
                        }
                    }
                    return compareRowsForInsert;
                }
            }
            --n;
        }
        return 0;
    }
    
    private int compareNextRecord(int n, LeafControlRow leafControlRow, final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        int n2 = 0;
        final LeafControlRow leafControlRow2 = leafControlRow;
        while (leafControlRow != null) {
            if (n >= leafControlRow.page.recordCount()) {
                final LeafControlRow leafControlRow3 = leafControlRow;
                leafControlRow = (LeafControlRow)leafControlRow.getRightSibling(this);
                if (n2 != 0) {
                    leafControlRow3.release();
                }
                n2 = 1;
                if (leafControlRow == null) {
                    return 0;
                }
                n = 1;
            }
            else {
                if (leafControlRow.page.fetchFromSlot(null, n, array, null, true) != null) {
                    final int compareRowsForInsert = this.compareRowsForInsert(array, array2, leafControlRow, n);
                    if (compareRowsForInsert != 1 || !leafControlRow.page.isDeletedAtSlot(n)) {
                        if (n2 != 0) {
                            if (compareRowsForInsert == 2) {
                                leafControlRow2.release();
                            }
                            if (compareRowsForInsert != 2) {
                                leafControlRow.release();
                            }
                        }
                        return compareRowsForInsert;
                    }
                }
                ++n;
            }
        }
        return 0;
    }
    
    private int compareRowsForInsert(final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final LeafControlRow leafControlRow, final int n) throws StandardException {
        for (int i = 0; i < array.length - 1; ++i) {
            if (!array[i].equals(array2[i])) {
                return 0;
            }
        }
        final DataValueDescriptor[] get_template = this.runtime_mem.get_template(this.getRawTran());
        if (!this.getLockingPolicy().lockNonScanRowOnPage(leafControlRow, n, RowUtil.getFetchDescriptorConstant(get_template.length - 1), get_template, (RowLocation)this.scratch_template[this.scratch_template.length - 1], 1)) {
            return 2;
        }
        return 1;
    }
    
    private int compareLeftAndRightSiblings(final DataValueDescriptor[] array, final int n, final LeafControlRow leafControlRow) throws StandardException {
        if (this.getConglomerate().isUniqueWithDuplicateNulls()) {
            final int n2 = array.length - 1;
            final boolean b = false;
            for (int i = 0; i < n2; ++i) {
                if (array[i].isNull()) {
                    return 0;
                }
            }
            if (!b) {
                final DataValueDescriptor[] get_template = this.runtime_mem.get_template(this.getRawTran());
                final int comparePreviousRecord = this.comparePreviousRecord(n - 1, leafControlRow, get_template, array);
                if (comparePreviousRecord > 0) {
                    return comparePreviousRecord;
                }
                return this.compareNextRecord(n, leafControlRow, get_template, array);
            }
        }
        return 0;
    }
    
    private int doIns(final DataValueDescriptor[] array) throws StandardException {
        int n = 0;
        int resultSlot = 0;
        int n2 = 0;
        int n3 = 0;
        if (this.scratch_template == null) {
            this.scratch_template = this.runtime_mem.get_template(this.getRawTran());
        }
        final SearchParameters searchParameters = new SearchParameters(array, 1, this.scratch_template, this, false);
        final FetchDescriptor fetchDescriptorConstant = RowUtil.getFetchDescriptorConstant(this.scratch_template.length - 1);
        final RowLocation rowLocation = (RowLocation)this.scratch_template[this.scratch_template.length - 1];
        if (this.get_insert_row_lock) {
            this.getLockingPolicy().lockNonScanRow(this.getConglomerate(), null, null, array, 3);
        }
        LeafControlRow leafControlRow;
        while (true) {
            leafControlRow = (LeafControlRow)ControlRow.get(this, 1L).search(searchParameters);
            if (!this.getLockingPolicy().lockNonScanPreviousRow(leafControlRow, searchParameters.resultExact ? searchParameters.resultSlot : (searchParameters.resultSlot + 1), fetchDescriptorConstant, this.scratch_template, rowLocation, this, 5, 1)) {
                continue;
            }
            if (searchParameters.resultExact) {
                n = (resultSlot = searchParameters.resultSlot);
                if (this.getConglomerate().nKeyFields != this.getConglomerate().nUniqueColumns && !this.getLockingPolicy().lockNonScanRowOnPage(leafControlRow, n, fetchDescriptorConstant, this.scratch_template, rowLocation, 1)) {
                    continue;
                }
                if (!leafControlRow.page.isDeletedAtSlot(n)) {
                    n2 = 1;
                    break;
                }
                if (this.getConglomerate().nKeyFields == this.getConglomerate().nUniqueColumns) {
                    leafControlRow.page.deleteAtSlot(n, false, this.btree_undo);
                    break;
                }
                if (this.getConglomerate().nUniqueColumns != this.getConglomerate().nKeyFields - 1) {
                    throw StandardException.newException("XSCB3.S");
                }
                leafControlRow.page.deleteAtSlot(n, false, this.btree_undo);
                boolean b = true;
                try {
                    if (this.runtime_mem.hasCollatedTypes()) {
                        for (int nKeyFields = this.getConglomerate().nKeyFields, i = 0; i < nKeyFields; ++i) {
                            leafControlRow.page.updateFieldAtSlot(n, i, RowUtil.getColumn(array, null, i), this.btree_undo);
                        }
                    }
                    else {
                        final int n4 = this.getConglomerate().nKeyFields - 1;
                        leafControlRow.page.updateFieldAtSlot(n, n4, RowUtil.getColumn(array, null, n4), this.btree_undo);
                    }
                }
                catch (StandardException ex) {
                    if (!ex.getMessageId().equals("XSDA3.S")) {
                        throw ex;
                    }
                    b = false;
                    leafControlRow.page.deleteAtSlot(n, true, this.btree_undo);
                }
                if (b) {
                    break;
                }
            }
            else if (leafControlRow.page.recordCount() - 1 < BTree.maxRowsPerPage) {
                n = searchParameters.resultSlot + 1;
                resultSlot = n + 1;
                if (this.getConglomerate().isUniqueWithDuplicateNulls()) {
                    final int compareLeftAndRightSiblings = this.compareLeftAndRightSiblings(array, n, leafControlRow);
                    if (compareLeftAndRightSiblings == 1) {
                        n2 = 1;
                        break;
                    }
                    if (compareLeftAndRightSiblings == 2) {
                        continue;
                    }
                }
                if (leafControlRow.page.insertAtSlot(n, array, null, this.btree_undo, (byte)1, 50) != null) {
                    break;
                }
                if (leafControlRow.page.recordCount() <= 2) {
                    throw StandardException.newException("XSCB6.S");
                }
            }
            if (this.getConglomerate().isUniqueWithDuplicateNulls()) {
                final int compareLeftAndRightSiblings2 = this.compareLeftAndRightSiblings(array, n, leafControlRow);
                if (compareLeftAndRightSiblings2 == 1) {
                    n2 = 1;
                    break;
                }
                if (compareLeftAndRightSiblings2 == 2) {
                    continue;
                }
            }
            int n5 = 0;
            if (n == 1) {
                n5 |= 0x4;
                if (leafControlRow.isLeftmostLeaf()) {
                    n5 |= 0x8;
                }
            }
            else if (n == leafControlRow.page.recordCount()) {
                n5 |= 0x1;
                if (leafControlRow.isRightmostLeaf()) {
                    n5 |= 0x2;
                }
            }
            final long pageNumber = leafControlRow.page.getPageNumber();
            if (leafControlRow.page.recordCount() - leafControlRow.page.nonDeletedRecordCount() <= 0) {
                n3 = 1;
            }
            final BranchRow branchRowFromOldLeafRow = BranchRow.createBranchRowFromOldLeafRow(array, pageNumber);
            leafControlRow.release();
            this.start_xact_and_dosplit(n3 == 0, pageNumber, this.scratch_template, branchRowFromOldLeafRow.getRow(), n5);
            n3 = 1;
        }
        leafControlRow.last_search_result = resultSlot;
        leafControlRow.release();
        return n2;
    }
    
    private boolean do_load_insert(final DataValueDescriptor[] array, final LeafControlRow leafControlRow, final int n) throws StandardException {
        boolean b = false;
        if (leafControlRow.page.recordCount() - 1 < BTree.maxRowsPerPage) {
            if (leafControlRow.page.insertAtSlot(n, array, null, this.btree_undo, (byte)1, 50) != null) {
                b = true;
            }
            else if (leafControlRow.page.recordCount() <= 2) {
                throw StandardException.newException("XSCB6.S");
            }
        }
        return b;
    }
    
    private LeafControlRow do_load_split(final DataValueDescriptor[] array, final LeafControlRow leafControlRow) throws StandardException {
        final BranchRow branchRowFromOldLeafRow = BranchRow.createBranchRowFromOldLeafRow(array, leafControlRow.page.getPageNumber());
        final long pageNumber = leafControlRow.page.getPageNumber();
        leafControlRow.release();
        return (LeafControlRow)ControlRow.get(this, this.start_xact_and_dosplit(false, pageNumber, this.scratch_template, branchRowFromOldLeafRow.getRow(), 3));
    }
    
    public void init(final TransactionManager transactionManager, final boolean b, final ContainerHandle containerHandle, final Transaction transaction, final int n, final int n2, final BTreeLockingPolicy bTreeLockingPolicy, final BTree bTree, final LogicalUndo logicalUndo, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        this.get_insert_row_lock = ((n & 0x4000) == 0x0);
        super.init(transactionManager, transactionManager, containerHandle, transaction, b, n, n2, bTreeLockingPolicy, bTree, logicalUndo, dynamicCompiledOpenConglomInfo);
    }
    
    public void close() throws StandardException {
        super.close();
        if (this.getXactMgr() != null) {
            this.getXactMgr().closeMe(this);
        }
    }
    
    public boolean closeForEndTransaction(final boolean b) throws StandardException {
        super.close();
        if (!this.getHold() || b) {
            if (this.getXactMgr() != null) {
                this.getXactMgr().closeMe(this);
            }
            return true;
        }
        return false;
    }
    
    public int insert(final DataValueDescriptor[] array) throws StandardException {
        if (this.isClosed()) {
            if (!this.getHold()) {
                throw StandardException.newException("XSCB8.S", new Long(this.err_containerid));
            }
            this.reopen();
        }
        return this.doIns(array);
    }
    
    public boolean isKeyed() {
        return true;
    }
    
    public void getTableProperties(final Properties properties) throws StandardException {
        if (this.container == null) {
            throw StandardException.newException("XSCB8.S", new Long(this.err_containerid));
        }
        this.container.getContainerProperties(properties);
    }
    
    public Properties getInternalTablePropertySet(final Properties properties) throws StandardException {
        final Properties rawStorePropertySet = ConglomerateUtil.createRawStorePropertySet(properties);
        this.getTableProperties(rawStorePropertySet);
        return rawStorePropertySet;
    }
    
    public long load(final TransactionManager transactionManager, final boolean b, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        long n = 0L;
        if (this.scratch_template == null) {
            this.scratch_template = this.runtime_mem.get_template(this.getRawTran());
        }
        try {
            LeafControlRow do_load_split = (LeafControlRow)ControlRow.get(this, 1L);
            int recordCount = 1;
            rowLocationRetRowSource.getValidColumns();
            DataValueDescriptor[] nextRowFromRowSource;
            while ((nextRowFromRowSource = rowLocationRetRowSource.getNextRowFromRowSource()) != null) {
                ++n;
                while (!this.do_load_insert(nextRowFromRowSource, do_load_split, recordCount)) {
                    do_load_split = this.do_load_split(nextRowFromRowSource, do_load_split);
                    recordCount = do_load_split.page.recordCount();
                }
                ++recordCount;
            }
            do_load_split.release();
            if (!this.getConglomerate().isTemporary()) {
                this.container.flushContainer();
            }
        }
        finally {
            this.close();
        }
        return n;
    }
    
    public boolean delete(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean fetch(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean fetch(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set, final boolean b) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public void insertAndFetchLocation(final DataValueDescriptor[] array, final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean lockRow(final RowLocation rowLocation, final int n, final boolean b, final int n2) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean lockRow(final long n, final int n2, final int n3, final boolean b, final int n4) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public void unlockRowAfterRead(final RowLocation rowLocation, final boolean b, final boolean b2) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean replace(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
}
