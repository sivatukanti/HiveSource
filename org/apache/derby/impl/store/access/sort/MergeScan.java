// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Enumeration;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;
import java.util.Vector;

public class MergeScan extends SortScan
{
    protected SortBuffer sortBuffer;
    protected Vector mergeRuns;
    protected StreamContainerHandle[] openScans;
    private SortObserver sortObserver;
    
    MergeScan(final MergeSort mergeSort, final TransactionManager tran, final SortBuffer sortBuffer, final Vector mergeRuns, final SortObserver sortObserver, final boolean b) {
        super(mergeSort, tran, b);
        this.sortBuffer = sortBuffer;
        this.mergeRuns = mergeRuns;
        this.tran = tran;
        this.sortObserver = sortObserver;
    }
    
    public boolean next() throws StandardException {
        this.current = this.sortBuffer.removeFirst();
        if (this.current != null) {
            this.mergeARow(this.sortBuffer.getLastAux());
        }
        return this.current != null;
    }
    
    public void close() {
        if (this.openScans != null) {
            for (int i = 0; i < this.openScans.length; ++i) {
                if (this.openScans[i] != null) {
                    this.openScans[i].close();
                }
                this.openScans[i] = null;
            }
            this.openScans = null;
        }
        if (super.sort != null) {
            this.sort.doneScanning(this, this.sortBuffer, this.mergeRuns);
            this.sortBuffer = null;
            this.mergeRuns = null;
        }
        super.close();
    }
    
    public boolean closeForEndTransaction(final boolean b) {
        if (!this.hold || b) {
            this.close();
            return true;
        }
        return false;
    }
    
    public boolean init(final TransactionManager transactionManager) throws StandardException {
        this.sortBuffer.reset();
        this.openScans = new StreamContainerHandle[this.mergeRuns.size()];
        if (this.openScans == null) {
            return false;
        }
        int n = 0;
        final Enumeration<Long> elements = this.mergeRuns.elements();
        while (elements.hasMoreElements()) {
            this.openScans[n++] = transactionManager.getRawStoreXact().openStreamContainer(-1, elements.nextElement(), this.hold);
        }
        for (int i = 0; i < this.openScans.length; ++i) {
            this.mergeARow(i);
        }
        return true;
    }
    
    void mergeARow(final int nextAux) throws StandardException {
        DataValueDescriptor[] arrayClone;
        do {
            arrayClone = this.sortObserver.getArrayClone();
            if (!this.openScans[nextAux].fetchNext(arrayClone)) {
                this.openScans[nextAux].close();
                this.openScans[nextAux] = null;
                return;
            }
            this.sortBuffer.setNextAux(nextAux);
        } while (this.sortBuffer.insert(arrayClone) == 1);
    }
}
