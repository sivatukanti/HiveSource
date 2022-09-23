// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.SQLLongint;

public class BranchControlRow extends ControlRow
{
    protected SQLLongint left_child_page;
    transient SQLLongint child_pageno_buf;
    private static final int CR_LEFTCHILD = 7;
    private static final int CR_COLID_LAST = 7;
    private static final int CR_NCOLUMNS = 8;
    protected static final FormatableBitSet CR_LEFTCHILD_BITMAP;
    
    public BranchControlRow() {
        this.left_child_page = null;
        this.child_pageno_buf = null;
    }
    
    public BranchControlRow(final OpenBTree openBTree, final Page page, final int n, final ControlRow controlRow, final boolean b, final long n2) throws StandardException {
        super(openBTree, page, n, controlRow, b);
        this.left_child_page = null;
        this.child_pageno_buf = null;
        this.left_child_page = new SQLLongint(n2);
        this.row[7] = this.left_child_page;
        this.child_pageno_buf = new SQLLongint();
    }
    
    protected final void controlRowInit() {
        this.child_pageno_buf = new SQLLongint();
    }
    
    public boolean isLeftmostLeaf() throws StandardException {
        return false;
    }
    
    public boolean isRightmostLeaf() throws StandardException {
        return false;
    }
    
    protected final int getNumberOfControlRowColumns() {
        return 8;
    }
    
    public static long restartSplitFor(final OpenBTree openBTree, final DataValueDescriptor[] array, final BranchControlRow branchControlRow, final ControlRow controlRow, final DataValueDescriptor[] array2, final DataValueDescriptor[] array3, final int n) throws StandardException {
        branchControlRow.release();
        controlRow.release();
        return ControlRow.get(openBTree, 1L).splitFor(openBTree, array, null, array2, n);
    }
    
    public ControlRow search(final SearchParameters searchParameters) throws StandardException {
        ControlRow childPageAtSlot = null;
        boolean b = true;
        try {
            this.searchForEntry(searchParameters);
            if (searchParameters.searchForOptimizer) {
                final float n = (float)searchParameters.resultSlot;
                final int recordCount = this.page.recordCount();
                if (this.getIsRoot()) {
                    searchParameters.current_fraction = 1.0f;
                    searchParameters.left_fraction = 0.0f;
                }
                searchParameters.left_fraction += searchParameters.current_fraction * (n / recordCount);
                searchParameters.current_fraction *= 1.0f / recordCount;
            }
            childPageAtSlot = this.getChildPageAtSlot(searchParameters.btree, searchParameters.resultSlot);
            this.release();
            b = false;
            return childPageAtSlot.search(searchParameters);
        }
        finally {
            if (b) {
                if (childPageAtSlot != null) {
                    childPageAtSlot.release();
                }
                if (this.page.isLatched()) {
                    this.release();
                }
            }
        }
    }
    
    protected ControlRow searchLeft(final OpenBTree openBTree) throws StandardException {
        ControlRow leftChild = null;
        boolean b = true;
        try {
            leftChild = this.getLeftChild(openBTree);
            this.release();
            b = false;
            return leftChild.searchLeft(openBTree);
        }
        finally {
            if (b) {
                if (leftChild != null) {
                    leftChild.release();
                }
                if (this.page.isLatched()) {
                    this.release();
                }
            }
        }
    }
    
    protected ControlRow searchRight(final OpenBTree openBTree) throws StandardException {
        ControlRow rightChild = null;
        boolean b = true;
        try {
            rightChild = this.getRightChild(openBTree);
            this.release();
            b = false;
            return rightChild.searchRight(openBTree);
        }
        finally {
            if (b) {
                if (rightChild != null) {
                    rightChild.release();
                }
                if (this.page.isLatched()) {
                    this.release();
                }
            }
        }
    }
    
    protected boolean shrinkFor(final OpenBTree openBTree, final DataValueDescriptor[] array) throws StandardException {
        boolean b = false;
        try {
            final SearchParameters searchParameters = new SearchParameters(array, 1, BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate()).getRow(), openBTree, false);
            this.searchForEntry(searchParameters);
            if (this.getChildPageAtSlot(searchParameters.btree, searchParameters.resultSlot).shrinkFor(openBTree, array)) {
                if (searchParameters.resultSlot != 0) {
                    this.page.purgeAtSlot(searchParameters.resultSlot, 1, true);
                }
                else if (this.page.recordCount() > 1) {
                    this.setLeftChildPageno(this.getChildPageIdAtSlot(openBTree, 1));
                    this.page.purgeAtSlot(1, 1, true);
                }
                else if (this.getIsRoot()) {
                    final LeafControlRow leafControlRow = new LeafControlRow(openBTree, this.page, null, true);
                    leafControlRow.page.updateAtSlot(0, leafControlRow.getRow(), null);
                    leafControlRow.release();
                    b = true;
                }
                else if (this.unlink(openBTree)) {
                    b = true;
                }
            }
        }
        finally {
            if (!b) {
                this.release();
            }
        }
        return b;
    }
    
    protected long splitFor(final OpenBTree openBTree, final DataValueDescriptor[] array, BranchControlRow branchControlRow, final DataValueDescriptor[] array2, final int n) throws StandardException {
        if (this.page.recordCount() - 1 < BTree.maxRowsPerPage && this.page.spaceForInsert(array2, null, 50)) {
            if (branchControlRow != null) {
                branchControlRow.release();
            }
            final SearchParameters searchParameters = new SearchParameters(array2, 1, BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate()).getRow(), openBTree, false);
            this.searchForEntry(searchParameters);
            return this.getChildPageAtSlot(openBTree, searchParameters.resultSlot).splitFor(openBTree, array, this, array2, n);
        }
        if (this.page.recordCount() == 1) {
            throw StandardException.newException("XSCB6.S");
        }
        if (this.getIsRoot()) {
            growRoot(openBTree, array, this);
            branchControlRow = (BranchControlRow)ControlRow.get(openBTree, 1L);
            return branchControlRow.splitFor(openBTree, array, null, array2, n);
        }
        int n2 = (this.page.recordCount() - 1) / 2 + 1;
        if ((n & 0x4) != 0x0) {
            n2 = 1;
        }
        else if ((n & 0x1) != 0x0) {
            n2 = this.page.recordCount() - 1;
        }
        final BranchRow emptyTemplate = BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate());
        this.page.fetchFromSlot(null, n2, emptyTemplate.getRow(), null, true);
        final BranchRow branchRowFromOldBranchRow = emptyTemplate.createBranchRowFromOldBranchRow(-1L);
        if (!branchControlRow.page.spaceForInsert(branchRowFromOldBranchRow.getRow(), null, 50)) {
            return restartSplitFor(openBTree, array, branchControlRow, this, branchRowFromOldBranchRow.getRow(), array2, n);
        }
        final ControlRow childPageAtSlot = this.getChildPageAtSlot(openBTree, n2);
        final BranchControlRow allocate = allocate(openBTree, childPageAtSlot, this.getLevel(), branchControlRow);
        allocate.linkRight(openBTree, this);
        childPageAtSlot.release();
        branchRowFromOldBranchRow.setPageNumber(allocate.page.getPageNumber());
        final SearchParameters searchParameters2 = new SearchParameters(branchRowFromOldBranchRow.getRow(), 1, BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate()).getRow(), openBTree, false);
        branchControlRow.searchForEntry(searchParameters2);
        if (branchControlRow.page.insertAtSlot(searchParameters2.resultSlot + 1, branchRowFromOldBranchRow.getRow(), null, null, (byte)((byte)((false | true) ? 1 : 0) | 0x2), 50) == null) {
            throw StandardException.newException("XSCB6.S");
        }
        final int n3 = this.page.recordCount() - (n2 + 1);
        if (n3 > 0) {
            this.page.copyAndPurge(allocate.page, n2 + 1, n3, 1);
        }
        this.page.purgeAtSlot(n2, 1, true);
        allocate.fixChildrensParents(openBTree, null);
        openBTree.getXactMgr().commit();
        BranchControlRow branchControlRow2;
        if (ControlRow.compareIndexRowToKey(array2, emptyTemplate.getRow(), emptyTemplate.getRow().length - 1, 0, openBTree.getConglomerate().ascDescInfo) >= 0) {
            branchControlRow2 = allocate;
            this.release();
        }
        else {
            branchControlRow2 = this;
            allocate.release();
        }
        return branchControlRow2.splitFor(openBTree, array, branchControlRow, array2, n);
    }
    
    public int checkConsistency(final OpenBTree openBTree, final ControlRow controlRow, final boolean b) throws StandardException {
        this.checkGeneric(openBTree, controlRow, b);
        if (b) {
            this.checkChildOrderAgainstRowOrder(openBTree);
        }
        int checkChildren = 0;
        if (b) {
            checkChildren = this.checkChildren(openBTree);
        }
        return checkChildren + 1;
    }
    
    private int checkChildren(final OpenBTree openBTree) throws StandardException {
        final int n = 0;
        ControlRow controlRow = null;
        try {
            controlRow = this.getLeftChild(openBTree);
            int n2 = n + controlRow.checkConsistency(openBTree, this, true);
            controlRow.release();
            controlRow = null;
            for (int recordCount = this.page.recordCount(), i = 1; i < recordCount; ++i) {
                controlRow = this.getChildPageAtSlot(openBTree, i);
                n2 += controlRow.checkConsistency(openBTree, this, true);
                controlRow.release();
                controlRow = null;
            }
            return n2;
        }
        finally {
            if (controlRow != null) {
                controlRow.release();
            }
        }
    }
    
    private void checkChildOrderAgainstRowOrder(final OpenBTree openBTree) throws StandardException {
        ControlRow childPageAtSlot = null;
        ControlRow leftChild = null;
        try {
            leftChild = this.getLeftChild(openBTree);
            for (int recordCount = this.page.recordCount(), i = 1; i < recordCount; ++i) {
                childPageAtSlot = this.getChildPageAtSlot(openBTree, i);
                leftChild.getrightSiblingPageNumber();
                childPageAtSlot.getleftSiblingPageNumber();
                leftChild.release();
                leftChild = childPageAtSlot;
                childPageAtSlot = null;
            }
            leftChild.release();
            leftChild = null;
        }
        finally {
            if (leftChild != null) {
                leftChild.release();
            }
            if (childPageAtSlot != null) {
                childPageAtSlot.release();
            }
        }
    }
    
    public void printTree(final OpenBTree openBTree) throws StandardException {
    }
    
    private static void growRoot(final OpenBTree openBTree, final DataValueDescriptor[] array, final BranchControlRow branchControlRow) throws StandardException {
        ControlRow leftChild = null;
        BranchControlRow allocate = null;
        try {
            leftChild = branchControlRow.getLeftChild(openBTree);
            allocate = allocate(openBTree, leftChild, branchControlRow.getLevel(), branchControlRow);
            branchControlRow.page.copyAndPurge(allocate.page, 1, branchControlRow.page.recordCount() - 1, 1);
            branchControlRow.setLeftChild(allocate);
            branchControlRow.setLevel(branchControlRow.getLevel() + 1);
            allocate.fixChildrensParents(openBTree, leftChild);
            openBTree.getXactMgr().commit();
        }
        finally {
            branchControlRow.release();
            if (allocate != null) {
                allocate.release();
            }
            if (leftChild != null) {
                leftChild.release();
            }
        }
    }
    
    private static BranchControlRow allocate(final OpenBTree openBTree, final ControlRow controlRow, final int n, final ControlRow controlRow2) throws StandardException {
        final Page addPage = openBTree.container.addPage();
        final BranchControlRow branchControlRow = new BranchControlRow(openBTree, addPage, n, controlRow2, false, controlRow.page.getPageNumber());
        addPage.insertAtSlot(0, branchControlRow.getRow(), null, null, (byte)((false | true) ? 1 : 0), 50);
        return branchControlRow;
    }
    
    protected void setLeftChildPageno(final long value) throws StandardException {
        if (this.left_child_page == null) {
            this.left_child_page = new SQLLongint(value);
        }
        else {
            this.left_child_page.setValue(value);
        }
        this.page.updateFieldAtSlot(0, 7, this.left_child_page, null);
    }
    
    protected void setLeftChild(final ControlRow controlRow) throws StandardException {
        this.setLeftChildPageno(controlRow.page.getPageNumber());
    }
    
    private void fixChildrensParents(final OpenBTree openBTree, final ControlRow controlRow) throws StandardException {
        ControlRow controlRow2 = null;
        try {
            if (controlRow == null) {
                controlRow2 = this.getLeftChild(openBTree);
                controlRow2.setParent(this.page.getPageNumber());
                controlRow2.release();
                controlRow2 = null;
            }
            else {
                controlRow.setParent(this.page.getPageNumber());
            }
            for (int recordCount = this.page.recordCount(), i = 1; i < recordCount; ++i) {
                controlRow2 = this.getChildPageAtSlot(openBTree, i);
                controlRow2.setParent(this.page.getPageNumber());
                controlRow2.release();
                controlRow2 = null;
            }
        }
        finally {
            if (controlRow2 != null) {
                controlRow2.release();
            }
        }
    }
    
    private long getChildPageIdAtSlot(final OpenBTree openBTree, final int n) throws StandardException {
        long n2;
        if (n == 0) {
            n2 = this.getLeftChildPageno();
        }
        else {
            this.page.fetchFieldFromSlot(n, openBTree.getConglomerate().nKeyFields, this.child_pageno_buf);
            n2 = this.child_pageno_buf.getLong();
        }
        return n2;
    }
    
    protected ControlRow getChildPageAtSlot(final OpenBTree openBTree, final int n) throws StandardException {
        ControlRow controlRow;
        if (n == 0) {
            controlRow = this.getLeftChild(openBTree);
        }
        else {
            this.page.fetchFieldFromSlot(n, openBTree.getConglomerate().nKeyFields, this.child_pageno_buf);
            controlRow = ControlRow.get(openBTree, this.child_pageno_buf.getLong());
        }
        return controlRow;
    }
    
    public ControlRow getLeftChild(final OpenBTree openBTree) throws StandardException {
        return ControlRow.get(openBTree, this.getLeftChildPageno());
    }
    
    protected ControlRow getRightChild(final OpenBTree openBTree) throws StandardException {
        final int recordCount = this.page.recordCount();
        return (recordCount == 1) ? ControlRow.get(openBTree, this.getLeftChildPageno()) : this.getChildPageAtSlot(openBTree, recordCount - 1);
    }
    
    long getLeftChildPageno() throws StandardException {
        if (this.left_child_page == null) {
            this.left_child_page = new SQLLongint();
            this.scratch_row[7] = this.left_child_page;
            this.fetchDesc.setValidColumns(BranchControlRow.CR_LEFTCHILD_BITMAP);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.left_child_page.getLong();
    }
    
    public int getTypeFormatId() {
        return 134;
    }
    
    public DataValueDescriptor[] getRowTemplate(final OpenBTree openBTree) throws StandardException {
        return BranchRow.createEmptyTemplate(openBTree.getRawTran(), openBTree.getConglomerate()).getRow();
    }
    
    public String toString() {
        return null;
    }
    
    static {
        (CR_LEFTCHILD_BITMAP = new FormatableBitSet(8)).set(7);
    }
}
