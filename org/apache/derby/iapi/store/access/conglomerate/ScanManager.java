// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.store.access.ScanController;

public interface ScanManager extends ScanController, GroupFetchScanController
{
    boolean closeForEndTransaction(final boolean p0) throws StandardException;
    
    void fetchSet(final long p0, final int[] p1, final BackingStoreHashtable p2) throws StandardException;
}
