// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.impl.store.access.btree.WaitError;
import org.apache.derby.impl.store.access.btree.ControlRow;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.impl.store.access.btree.BTreeRowPosition;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.LeafControlRow;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;

class B2IRowLocking3 implements BTreeLockingPolicy
{
    protected ConglomerateController base_cc;
    protected OpenBTree open_btree;
    private Transaction rawtran;
    
    B2IRowLocking3(final Transaction rawtran, final int n, final LockingPolicy lockingPolicy, final ConglomerateController base_cc, final OpenBTree open_btree) {
        this.rawtran = rawtran;
        this.base_cc = base_cc;
        this.open_btree = open_btree;
    }
    
    private boolean lockPreviousToFirstKey(final LeafControlRow leafControlRow, final LeafControlRow leafControlRow2, final int n, final int n2) throws StandardException {
        final boolean lockRow = this.base_cc.lockRow(1L, 3, n, false, n2);
        if (!lockRow) {
            leafControlRow.release();
            if (leafControlRow2 != null) {
                leafControlRow2.release();
            }
            this.base_cc.lockRow(1L, 3, n, true, n2);
        }
        return lockRow;
    }
    
    private boolean lockRowOnPage(final LeafControlRow leafControlRow, final LeafControlRow leafControlRow2, final int n, final BTreeRowPosition bTreeRowPosition, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final int n2, final int n3) throws StandardException {
        leafControlRow.getPage().fetchFromSlot(null, n, array, fetchDescriptor, true);
        final boolean lockRow = this.base_cc.lockRow(rowLocation, n2, false, n3);
        if (!lockRow) {
            if (bTreeRowPosition != null) {
                bTreeRowPosition.saveMeAndReleasePage();
            }
            else if (leafControlRow != null) {
                leafControlRow.release();
            }
            if (leafControlRow2 != null) {
                leafControlRow2.release();
            }
            this.base_cc.lockRow(rowLocation, n2, true, n3);
        }
        return lockRow;
    }
    
    private boolean searchLeftAndLockPreviousKey(LeafControlRow leafControlRow, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final OpenBTree openBTree, final int n, final int n2) throws StandardException {
        boolean b = false;
        LeafControlRow leafControlRow2;
        try {
            leafControlRow2 = (LeafControlRow)leafControlRow.getLeftSibling(openBTree);
        }
        catch (WaitError waitError) {
            final long getleftSiblingPageNumber = leafControlRow.getleftSiblingPageNumber();
            leafControlRow.release();
            leafControlRow = null;
            leafControlRow2 = (LeafControlRow)ControlRow.get(openBTree, getleftSiblingPageNumber);
            b = true;
        }
    Label_0215:
        while (true) {
            try {
                while (leafControlRow2.getPage().recordCount() <= 1) {
                    if (leafControlRow2.isLeftmostLeaf()) {
                        if (!this.lockPreviousToFirstKey(leafControlRow2, leafControlRow, n, n2)) {
                            leafControlRow2 = null;
                            leafControlRow = null;
                            b = true;
                        }
                        break Label_0215;
                    }
                    final LeafControlRow leafControlRow3 = (LeafControlRow)leafControlRow2.getLeftSibling(openBTree);
                    leafControlRow2.release();
                    leafControlRow2 = leafControlRow3;
                }
                if (!this.lockRowOnPage(leafControlRow2, leafControlRow, leafControlRow2.getPage().recordCount() - 1, null, fetchDescriptor, array, rowLocation, n, n2)) {
                    leafControlRow2 = null;
                    leafControlRow = null;
                    b = true;
                }
                break;
            }
            catch (WaitError waitError2) {
                final long getleftSiblingPageNumber2 = leafControlRow2.getleftSiblingPageNumber();
                if (leafControlRow != null) {
                    leafControlRow.release();
                    leafControlRow = null;
                }
                leafControlRow2.release();
                leafControlRow2 = (LeafControlRow)ControlRow.get(openBTree, getleftSiblingPageNumber2);
                b = true;
                continue;
            }
        }
        if (leafControlRow2 != null) {
            leafControlRow2.release();
        }
        return !b;
    }
    
    protected boolean _lockScanRow(final OpenBTree openBTree, final BTreeRowPosition bTreeRowPosition, final boolean b, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final boolean b2, final boolean b3, final int n) throws StandardException {
        boolean b4 = false;
        if (b) {
            if (bTreeRowPosition.current_slot == 0) {
                b4 = !this.lockNonScanPreviousRow(bTreeRowPosition.current_leaf, 1, fetchDescriptor, array, rowLocation, openBTree, n, 2);
            }
            else {
                b4 = !this.lockRowOnPage(bTreeRowPosition.current_leaf, null, bTreeRowPosition.current_slot, bTreeRowPosition, fetchDescriptor, array, rowLocation, n, 2);
            }
        }
        return !b4;
    }
    
    public boolean lockScanCommittedDeletedRow(final OpenBTree openBTree, final LeafControlRow leafControlRow, final DataValueDescriptor[] array, final FetchDescriptor fetchDescriptor, final int n) throws StandardException {
        final RowLocation rowLocation = (RowLocation)array[((B2I)openBTree.getConglomerate()).rowLocationColumn];
        leafControlRow.getPage().fetchFromSlot(null, n, array, fetchDescriptor, true);
        return this.base_cc.lockRow(rowLocation, 1, false, 2);
    }
    
    public boolean lockScanRow(final OpenBTree openBTree, final BTreeRowPosition bTreeRowPosition, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final boolean b, final boolean b2, final int n) throws StandardException {
        return this._lockScanRow(openBTree, bTreeRowPosition, true, fetchDescriptor, array, rowLocation, b, b2, n);
    }
    
    public void unlockScanRecordAfterRead(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
    }
    
    public boolean lockNonScanPreviousRow(final LeafControlRow leafControlRow, final int n, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final OpenBTree openBTree, final int n2, final int n3) throws StandardException {
        boolean b;
        if (n > 1) {
            b = this.lockRowOnPage(leafControlRow, null, n - 1, null, fetchDescriptor, array, rowLocation, n2, n3);
        }
        else if (leafControlRow.isLeftmostLeaf()) {
            b = this.lockPreviousToFirstKey(leafControlRow, null, n2, n3);
        }
        else {
            b = this.searchLeftAndLockPreviousKey(leafControlRow, fetchDescriptor, array, rowLocation, openBTree, n2, n3);
        }
        return b;
    }
    
    public boolean lockNonScanRow(final BTree bTree, final LeafControlRow leafControlRow, final LeafControlRow leafControlRow2, final DataValueDescriptor[] array, final int n) throws StandardException {
        final B2I b2I = (B2I)bTree;
        final boolean lockRow = this.base_cc.lockRow((RowLocation)array[b2I.rowLocationColumn], n, false, 2);
        if (!lockRow) {
            if (leafControlRow != null) {
                leafControlRow.release();
            }
            if (leafControlRow2 != null) {
                leafControlRow2.release();
            }
            this.base_cc.lockRow((RowLocation)array[b2I.rowLocationColumn], n, true, 2);
        }
        return lockRow;
    }
    
    public boolean lockNonScanRowOnPage(final LeafControlRow leafControlRow, final int n, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final int n2) throws StandardException {
        return this.lockRowOnPage(leafControlRow, null, n, null, fetchDescriptor, array, rowLocation, n2, 2);
    }
}
