// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;

public class SortBufferScan extends SortScan
{
    protected SortBuffer sortBuffer;
    
    SortBufferScan(final MergeSort mergeSort, final TransactionManager transactionManager, final SortBuffer sortBuffer, final boolean b) {
        super(mergeSort, transactionManager, b);
        this.sortBuffer = sortBuffer;
    }
    
    public boolean next() throws StandardException {
        super.current = this.sortBuffer.removeFirst();
        return super.current != null;
    }
    
    public boolean closeForEndTransaction(final boolean b) {
        if (b || !this.hold) {
            this.close();
            return true;
        }
        return false;
    }
    
    public void close() {
        if (super.sort != null) {
            this.sort.doneScanning(this, this.sortBuffer);
            this.sortBuffer = null;
        }
        super.close();
    }
}
