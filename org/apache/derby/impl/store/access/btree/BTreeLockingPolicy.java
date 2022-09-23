// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface BTreeLockingPolicy
{
    boolean lockScanCommittedDeletedRow(final OpenBTree p0, final LeafControlRow p1, final DataValueDescriptor[] p2, final FetchDescriptor p3, final int p4) throws StandardException;
    
    boolean lockScanRow(final OpenBTree p0, final BTreeRowPosition p1, final FetchDescriptor p2, final DataValueDescriptor[] p3, final RowLocation p4, final boolean p5, final boolean p6, final int p7) throws StandardException;
    
    void unlockScanRecordAfterRead(final BTreeRowPosition p0, final boolean p1) throws StandardException;
    
    boolean lockNonScanPreviousRow(final LeafControlRow p0, final int p1, final FetchDescriptor p2, final DataValueDescriptor[] p3, final RowLocation p4, final OpenBTree p5, final int p6, final int p7) throws StandardException;
    
    boolean lockNonScanRow(final BTree p0, final LeafControlRow p1, final LeafControlRow p2, final DataValueDescriptor[] p3, final int p4) throws StandardException;
    
    boolean lockNonScanRowOnPage(final LeafControlRow p0, final int p1, final FetchDescriptor p2, final DataValueDescriptor[] p3, final RowLocation p4, final int p5) throws StandardException;
}
