// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.StoreCostResult;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.StoreCostController;

public class BTreeCostController extends OpenBTree implements StoreCostController
{
    private static final double BTREE_CACHED_FETCH_BY_KEY_PER_LEVEL = 0.2705;
    private static final double BTREE_SORTMERGE_FETCH_BY_KEY_PER_LEVEL = 0.716;
    private static final double BTREE_UNCACHED_FETCH_BY_KEY_PER_LEVEL = 1.5715;
    TransactionManager init_xact_manager;
    Transaction init_rawtran;
    Conglomerate init_conglomerate;
    long num_pages;
    long num_rows;
    long page_size;
    int tree_height;
    
    public void init(final TransactionManager transactionManager, final BTree bTree, final Transaction transaction) throws StandardException {
        super.init(transactionManager, transactionManager, null, transaction, false, 8, 5, null, bTree, null, null);
        this.num_pages = this.container.getEstimatedPageCount(0);
        this.num_rows = this.container.getEstimatedRowCount(0) - this.num_pages;
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "");
        this.container.getContainerProperties(properties);
        this.page_size = Integer.parseInt(properties.getProperty("derby.storage.pageSize"));
        this.tree_height = this.getHeight();
    }
    
    public void close() throws StandardException {
        super.close();
    }
    
    public double getFetchFromRowLocationCost(final FormatableBitSet set, final int n) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public double getFetchFromFullKeyCost(final FormatableBitSet set, final int n) throws StandardException {
        double n2;
        if ((n & 0x1) == 0x0) {
            n2 = 1.5715;
        }
        else {
            n2 = 0.716;
        }
        return n2 * this.tree_height;
    }
    
    public void getScanCost(final int n, final long n2, final int n3, final boolean b, final FormatableBitSet set, final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final int n4, final DataValueDescriptor[] array3, final int n5, final boolean b2, final int n6, final StoreCostResult storeCostResult) throws StandardException {
        ControlRow controlRow = null;
        final long n7 = (n2 < 0L) ? this.num_rows : n2;
        try {
            float left_fraction;
            if (array2 == null) {
                left_fraction = 0.0f;
            }
            else {
                final SearchParameters searchParameters = new SearchParameters(array2, (n4 == 1) ? 1 : -1, array, this, true);
                controlRow = ControlRow.get(this, 1L).search(searchParameters);
                controlRow.release();
                controlRow = null;
                left_fraction = searchParameters.left_fraction;
            }
            float left_fraction2;
            if (array3 == null) {
                left_fraction2 = 1.0f;
            }
            else {
                final SearchParameters searchParameters2 = new SearchParameters(array3, (n5 == 1) ? 1 : -1, array, this, true);
                controlRow = ControlRow.get(this, 1L).search(searchParameters2);
                controlRow.release();
                controlRow = null;
                left_fraction2 = searchParameters2.left_fraction;
            }
            float n8 = left_fraction2 - left_fraction;
            if (n8 < 0.0f) {
                n8 = 0.0f;
            }
            if (n8 > 1.0f) {
                n8 = 1.0f;
            }
            float a = n7 * n8;
            if (a < 1.0f) {
                a = 1.0f;
            }
            final double n9 = this.getFetchFromFullKeyCost(set, n6) + this.num_pages * n8 * 1.5;
            long n10 = (long)a - this.num_pages;
            if (n10 < 0L) {
                n10 = 0L;
            }
            double n11;
            if (n == 2) {
                n11 = n9 + n10 * 0.12;
            }
            else {
                n11 = n9 + n10 * 0.14;
            }
            storeCostResult.setEstimatedCost(n11 + a * ((n7 == 0L) ? 4L : (this.num_pages * this.page_size / n7)) * 0.004);
            storeCostResult.setEstimatedRowCount(Math.round(a));
        }
        finally {
            if (controlRow != null) {
                controlRow.release();
            }
        }
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
}
