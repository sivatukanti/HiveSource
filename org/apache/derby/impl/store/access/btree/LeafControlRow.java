// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Page;

public class LeafControlRow extends ControlRow
{
    public LeafControlRow() {
    }
    
    LeafControlRow(final OpenBTree openBTree, final Page page, final ControlRow controlRow, final boolean b) throws StandardException {
        super(openBTree, page, 0, controlRow, b);
    }
    
    private static LeafControlRow allocate(final OpenBTree openBTree, final ControlRow controlRow) throws StandardException {
        final Page addPage = openBTree.container.addPage();
        final LeafControlRow leafControlRow = new LeafControlRow(openBTree, addPage, controlRow, false);
        addPage.insertAtSlot(0, leafControlRow.getRow(), null, null, (byte)((false | true) ? 1 : 0), 50);
        return leafControlRow;
    }
    
    private float get_left_nondeleted_rowcnt(final int n) throws StandardException {
        int n2 = 0;
        for (int i = 1; i <= n; ++i) {
            if (!this.page.isDeletedAtSlot(i)) {
                ++n2;
            }
        }
        return (float)n2;
    }
    
    protected final void controlRowInit() {
    }
    
    public static void initEmptyBtree(final OpenBTree openBTree) throws StandardException {
        final Page page = openBTree.container.getPage(1L);
        page.insertAtSlot(0, new LeafControlRow(openBTree, page, null, true).getRow(), null, null, (byte)((false | true) ? 1 : 0), 50);
        page.unlatch();
    }
    
    protected final int getNumberOfControlRowColumns() {
        return 7;
    }
    
    public boolean isLeftmostLeaf() throws StandardException {
        return this.getleftSiblingPageNumber() == -1L;
    }
    
    public boolean isRightmostLeaf() throws StandardException {
        return this.getrightSiblingPageNumber() == -1L;
    }
    
    public ControlRow search(final SearchParameters searchParameters) throws StandardException {
        this.searchForEntry(searchParameters);
        if (searchParameters.searchForOptimizer) {
            int resultSlot = searchParameters.resultSlot;
            if (searchParameters.resultExact && searchParameters.partial_key_match_op == 1) {
                --resultSlot;
            }
            final float get_left_nondeleted_rowcnt = this.get_left_nondeleted_rowcnt(resultSlot);
            final int nonDeletedRecordCount = this.page.nonDeletedRecordCount();
            if (this.getIsRoot()) {
                searchParameters.current_fraction = 1.0f;
                searchParameters.left_fraction = 0.0f;
            }
            if (nonDeletedRecordCount > 1) {
                searchParameters.left_fraction += searchParameters.current_fraction * (get_left_nondeleted_rowcnt / (nonDeletedRecordCount - 1));
            }
            if (nonDeletedRecordCount > 1) {
                searchParameters.current_fraction *= 1.0f / (nonDeletedRecordCount - 1);
            }
        }
        return this;
    }
    
    protected ControlRow searchLeft(final OpenBTree openBTree) throws StandardException {
        return this;
    }
    
    protected ControlRow searchRight(final OpenBTree openBTree) throws StandardException {
        return this;
    }
    
    protected boolean shrinkFor(final OpenBTree openBTree, final DataValueDescriptor[] array) throws StandardException {
        boolean unlink = false;
        try {
            if (this.page.recordCount() == 1 && !this.getIsRoot()) {
                unlink = this.unlink(openBTree);
            }
        }
        finally {
            if (!unlink) {
                this.release();
            }
        }
        return unlink;
    }
    
    protected long splitFor(final OpenBTree openBTree, final DataValueDescriptor[] array, final BranchControlRow branchControlRow, final DataValueDescriptor[] array2, final int n) throws StandardException {
        final long pageNumber = this.page.getPageNumber();
        if (this.page.recordCount() - 1 < BTree.maxRowsPerPage && this.page.spaceForInsert(array2, null, 50)) {
            openBTree.getXactMgr().commit();
            if (branchControlRow != null) {
                branchControlRow.release();
            }
            this.release();
            return pageNumber;
        }
        if (this.getIsRoot()) {
            growRoot(openBTree, array, this);
            return ControlRow.get(openBTree, 1L).splitFor(openBTree, array, null, array2, n);
        }
        int n2 = (this.page.recordCount() - 1) / 2 + 1;
        if ((n & 0x4) != 0x0) {
            n2 = 1;
        }
        else if ((n & 0x1) != 0x0) {
            n2 = this.page.recordCount() - 1;
        }
        final DataValueDescriptor[] template = openBTree.getConglomerate().createTemplate(openBTree.getRawTran());
        this.page.fetchFromSlot(null, n2, template, null, true);
        final BranchRow branchRowFromOldLeafRow = BranchRow.createBranchRowFromOldLeafRow(template, -1L);
        if (!branchControlRow.page.spaceForInsert(branchRowFromOldLeafRow.getRow(), null, 50)) {
            return BranchControlRow.restartSplitFor(openBTree, array, branchControlRow, this, branchRowFromOldLeafRow.getRow(), array2, n);
        }
        final LeafControlRow allocate = allocate(openBTree, branchControlRow);
        branchRowFromOldLeafRow.setPageNumber(allocate.page.getPageNumber());
        allocate.linkRight(openBTree, this);
        final int n3 = this.page.recordCount() - n2;
        if (n3 != 0) {
            this.page.copyAndPurge(allocate.page, n2, n3, 1);
        }
        final SearchParameters searchParameters = new SearchParameters(branchRowFromOldLeafRow.getRow(), 1, BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate()).getRow(), openBTree, false);
        branchControlRow.searchForEntry(searchParameters);
        if (branchControlRow.page.insertAtSlot(searchParameters.resultSlot + 1, branchRowFromOldLeafRow.getRow(), null, null, (byte)((byte)((false | true) ? 1 : 0) | 0x2), 50) == null) {
            throw StandardException.newException("XSCB6.S");
        }
        this.page.setRepositionNeeded();
        openBTree.getXactMgr().commit();
        branchControlRow.release();
        this.release();
        final long pageNumber2 = allocate.page.getPageNumber();
        allocate.release();
        return pageNumber2;
    }
    
    private static void growRoot(final OpenBTree openBTree, final DataValueDescriptor[] array, final LeafControlRow leafControlRow) throws StandardException {
        final LeafControlRow allocate = allocate(openBTree, leafControlRow);
        leafControlRow.page.copyAndPurge(allocate.page, 1, leafControlRow.page.recordCount() - 1, 1);
        final BranchControlRow branchControlRow = new BranchControlRow(openBTree, leafControlRow.page, 1, null, true, allocate.page.getPageNumber());
        final ControlRow controlRow = null;
        branchControlRow.page.updateAtSlot(0, branchControlRow.getRow(), null);
        branchControlRow.page.setRepositionNeeded();
        openBTree.getXactMgr().commit();
        if (branchControlRow != null) {
            branchControlRow.release();
        }
        if (controlRow != null) {
            controlRow.release();
        }
        if (allocate != null) {
            allocate.release();
        }
    }
    
    protected ControlRow getLeftChild(final OpenBTree openBTree) throws StandardException {
        return null;
    }
    
    protected ControlRow getRightChild(final OpenBTree openBTree) throws StandardException {
        return null;
    }
    
    public int checkConsistency(final OpenBTree openBTree, final ControlRow controlRow, final boolean b) throws StandardException {
        this.checkGeneric(openBTree, controlRow, b);
        return 1;
    }
    
    public void printTree(final OpenBTree openBTree) throws StandardException {
    }
    
    public int getTypeFormatId() {
        return 133;
    }
}
