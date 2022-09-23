// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;

public abstract class SortScan extends Scan
{
    protected MergeSort sort;
    protected TransactionManager tran;
    protected DataValueDescriptor[] current;
    protected boolean hold;
    
    SortScan(final MergeSort sort, final TransactionManager tran, final boolean hold) {
        this.sort = null;
        this.tran = null;
        this.sort = sort;
        this.tran = tran;
        this.hold = hold;
    }
    
    public final boolean fetchNext(final DataValueDescriptor[] array) throws StandardException {
        final boolean next = this.next();
        if (next) {
            this.fetch(array);
        }
        return next;
    }
    
    public final void fetch(final DataValueDescriptor[] array) throws StandardException {
        if (this.current == null) {
            throw StandardException.newException("XSAS1.S");
        }
        this.sort.checkColumnTypes(array);
        System.arraycopy(this.current, 0, array, 0, array.length);
    }
    
    public final void fetchWithoutQualify(final DataValueDescriptor[] array) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void close() {
        this.sort = null;
        this.current = null;
        this.tran.closeMe(this);
    }
}
