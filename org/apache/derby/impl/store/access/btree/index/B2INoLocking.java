// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.impl.store.access.btree.BTreeRowPosition;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.impl.store.access.btree.LeafControlRow;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;

public class B2INoLocking implements BTreeLockingPolicy
{
    public B2INoLocking(final Transaction transaction, final int n, final LockingPolicy lockingPolicy, final ConglomerateController conglomerateController, final OpenBTree openBTree) {
    }
    
    protected B2INoLocking() {
    }
    
    public boolean lockScanCommittedDeletedRow(final OpenBTree openBTree, final LeafControlRow leafControlRow, final DataValueDescriptor[] array, final FetchDescriptor fetchDescriptor, final int n) throws StandardException {
        return true;
    }
    
    public boolean lockScanRow(final OpenBTree openBTree, final BTreeRowPosition bTreeRowPosition, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final boolean b, final boolean b2, final int n) throws StandardException {
        return true;
    }
    
    public void unlockScanRecordAfterRead(final BTreeRowPosition bTreeRowPosition, final boolean b) throws StandardException {
    }
    
    public boolean lockNonScanPreviousRow(final LeafControlRow leafControlRow, final int n, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final OpenBTree openBTree, final int n2, final int n3) throws StandardException {
        return true;
    }
    
    public boolean lockNonScanRow(final BTree bTree, final LeafControlRow leafControlRow, final LeafControlRow leafControlRow2, final DataValueDescriptor[] array, final int n) throws StandardException {
        return true;
    }
    
    public boolean lockNonScanRowOnPage(final LeafControlRow leafControlRow, final int n, final FetchDescriptor fetchDescriptor, final DataValueDescriptor[] array, final RowLocation rowLocation, final int n2) throws StandardException {
        return true;
    }
}
