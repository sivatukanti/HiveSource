// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.store.access.StoreCostResult;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import java.util.Properties;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerate;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.impl.store.access.conglomerate.GenericCostController;

public class HeapCostController extends GenericCostController implements StoreCostController
{
    long num_pages;
    long num_rows;
    long page_size;
    long row_size;
    
    public void init(final OpenConglomerate openConglomerate) throws StandardException {
        super.init(openConglomerate);
        final ContainerHandle container = openConglomerate.getContainer();
        this.num_rows = container.getEstimatedRowCount(0);
        if (this.num_rows == 0L) {
            this.num_rows = 1L;
        }
        this.num_pages = container.getEstimatedPageCount(0);
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "");
        container.getContainerProperties(properties);
        this.page_size = Integer.parseInt(properties.getProperty("derby.storage.pageSize"));
        this.row_size = this.num_pages * this.page_size / this.num_rows;
    }
    
    public double getFetchFromRowLocationCost(final FormatableBitSet set, final int n) throws StandardException {
        final double n2 = this.row_size * 0.004;
        final long n3 = this.row_size / this.page_size + 1L;
        double n4;
        if ((n & 0x1) == 0x0) {
            n4 = n2 + 1.5 * n3;
        }
        else {
            n4 = n2 + 0.17 * n3;
        }
        return n4;
    }
    
    public void getScanCost(final int n, final long n2, final int n3, final boolean b, final FormatableBitSet set, final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final int n4, final DataValueDescriptor[] array3, final int n5, final boolean b2, final int n6, final StoreCostResult storeCostResult) throws StandardException {
        final long estimatedRowCount = (n2 < 0L) ? this.num_rows : n2;
        final double n7 = this.num_pages * 1.5 + estimatedRowCount * this.row_size * 0.004;
        long n8 = estimatedRowCount - this.num_pages;
        if (n8 < 0L) {
            n8 = 0L;
        }
        double estimatedCost;
        if (n == 2) {
            estimatedCost = n7 + n8 * 0.12;
        }
        else {
            estimatedCost = n7 + n8 * 0.14;
        }
        storeCostResult.setEstimatedCost(estimatedCost);
        storeCostResult.setEstimatedRowCount(estimatedRowCount);
    }
}
