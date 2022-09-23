// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;

class BackingStoreHashTableFromScan extends BackingStoreHashtable
{
    private ScanManager open_scan;
    
    public BackingStoreHashTableFromScan(final TransactionController transactionController, final long n, final int n2, final int n3, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array, final int n5, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n6, final long n7, final int[] array4, final boolean b, final long n8, final long n9, final int n10, final float n11, final boolean b2, final boolean b3, final boolean b4) throws StandardException {
        super(transactionController, null, array4, b, n8, n9, n10, n11, b3, b4);
        (this.open_scan = (ScanManager)transactionController.openScan(n, false, n2, n3, n4, set, array, n5, array2, array3, n6)).fetchSet(n7, array4, this);
        if (b2) {
            final Properties auxillaryRuntimeStats = new Properties();
            this.open_scan.getScanInfo().getAllScanInfo(auxillaryRuntimeStats);
            this.setAuxillaryRuntimeStats(auxillaryRuntimeStats);
        }
    }
    
    public void close() throws StandardException {
        this.open_scan.close();
        super.close();
    }
}
