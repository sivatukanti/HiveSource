// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.types.DataValueDescriptor;

public class BranchRow
{
    public static final long DUMMY_PAGE_NUMBER = -1L;
    private DataValueDescriptor[] branchrow;
    
    private BranchRow() {
        this.branchrow = null;
    }
    
    private BranchRow(final Transaction transaction, final BTree bTree) throws StandardException {
        this.branchrow = null;
        this.branchrow = bTree.createBranchTemplate(transaction, new SQLLongint(-1L));
    }
    
    private SQLLongint getChildPage() {
        return (SQLLongint)this.branchrow[this.branchrow.length - 1];
    }
    
    public static BranchRow createEmptyTemplate(final Transaction transaction, final BTree bTree) throws StandardException {
        return new BranchRow(transaction, bTree);
    }
    
    public BranchRow createBranchRowFromOldBranchRow(final long n) {
        final BranchRow branchRow = new BranchRow();
        branchRow.branchrow = new DataValueDescriptor[this.branchrow.length];
        System.arraycopy(this.branchrow, 0, branchRow.branchrow, 0, branchRow.branchrow.length - 1);
        branchRow.branchrow[branchRow.branchrow.length - 1] = new SQLLongint(n);
        return branchRow;
    }
    
    public static BranchRow createBranchRowFromOldLeafRow(final DataValueDescriptor[] array, final long n) {
        final BranchRow branchRow = new BranchRow();
        System.arraycopy(array, 0, branchRow.branchrow = new DataValueDescriptor[array.length + 1], 0, array.length);
        branchRow.branchrow[branchRow.branchrow.length - 1] = new SQLLongint(n);
        return branchRow;
    }
    
    protected DataValueDescriptor[] getRow() {
        return this.branchrow;
    }
    
    protected void setPageNumber(final long value) {
        this.getChildPage().setValue(value);
    }
    
    public String toString() {
        return null;
    }
}
