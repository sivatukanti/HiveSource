// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.conglomerate.ScanControllerRowSource;

public class SortBufferRowSource extends Scan implements ScanControllerRowSource
{
    SortBuffer sortBuffer;
    protected TransactionManager tran;
    private int maxFreeListSize;
    private boolean writingToDisk;
    private SortObserver sortObserver;
    
    SortBufferRowSource(final SortBuffer sortBuffer, final TransactionManager tran, final SortObserver sortObserver, final boolean writingToDisk, final int maxFreeListSize) {
        this.sortBuffer = null;
        this.tran = null;
        this.sortBuffer = sortBuffer;
        this.tran = tran;
        this.sortObserver = sortObserver;
        this.writingToDisk = writingToDisk;
        this.maxFreeListSize = maxFreeListSize;
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() {
        if (this.sortBuffer == null) {
            return null;
        }
        final DataValueDescriptor[] removeFirst = this.sortBuffer.removeFirst();
        if (removeFirst != null && this.writingToDisk) {
            this.sortObserver.addToFreeList(removeFirst, this.maxFreeListSize);
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
    
    public void close() {
        if (this.sortBuffer != null) {
            this.sortBuffer.close();
            this.sortBuffer = null;
        }
        this.tran.closeMe(this);
    }
    
    public boolean closeForEndTransaction(final boolean b) {
        this.close();
        return true;
    }
    
    public void closeRowSource() {
        this.close();
    }
    
    public boolean next() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void fetchWithoutQualify(final DataValueDescriptor[] array) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void fetch(final DataValueDescriptor[] array) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public final boolean fetchNext(final DataValueDescriptor[] array) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
}
