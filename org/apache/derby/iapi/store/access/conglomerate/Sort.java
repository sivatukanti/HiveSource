// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.SortController;

public interface Sort
{
    SortController open(final TransactionManager p0) throws StandardException;
    
    ScanController openSortScan(final TransactionManager p0, final boolean p1) throws StandardException;
    
    ScanControllerRowSource openSortRowSource(final TransactionManager p0) throws StandardException;
    
    void drop(final TransactionController p0) throws StandardException;
}
