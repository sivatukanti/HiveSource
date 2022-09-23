// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.SortObserver;
import java.util.Vector;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.conglomerate.ScanControllerRowSource;

public class MergeScanRowSource extends MergeScan implements ScanControllerRowSource
{
    MergeScanRowSource(final MergeSort mergeSort, final TransactionManager transactionManager, final SortBuffer sortBuffer, final Vector vector, final SortObserver sortObserver, final boolean b) {
        super(mergeSort, transactionManager, sortBuffer, vector, sortObserver, b);
    }
    
    public boolean next() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        final DataValueDescriptor[] removeFirst = this.sortBuffer.removeFirst();
        if (removeFirst != null) {
            this.mergeARow(this.sortBuffer.getLastAux());
        }
        return removeFirst;
    }
    
    public boolean needsRowLocation() {
        return false;
    }
    
    public boolean needsToClone() {
        return false;
    }
    
    public void rowLocation(final RowLocation rowLocation) {
    }
    
    public FormatableBitSet getValidColumns() {
        return null;
    }
    
    public void closeRowSource() {
        this.close();
    }
}
