// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.Sort;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Properties;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.access.SortCostController;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.access.conglomerate.SortFactory;

public class ExternalSortFactory implements SortFactory, ModuleControl, ModuleSupportable, SortCostController
{
    private boolean userSpecified;
    private int defaultSortBufferMax;
    private int sortBufferMax;
    private static final String IMPLEMENTATIONID = "sort external";
    private static final String FORMATUUIDSTRING = "D2976090-D9F5-11d0-B54D-00A024BF8879";
    private UUID formatUUID;
    private static final int DEFAULT_SORTBUFFERMAX = 1024;
    private static final int MINIMUM_SORTBUFFERMAX = 4;
    protected static final int DEFAULT_MEM_USE = 1048576;
    protected static final int DEFAULT_MAX_MERGE_RUN = 512;
    private static final int SORT_ROW_OVERHEAD = 44;
    
    public ExternalSortFactory() {
        this.formatUUID = null;
    }
    
    public Properties defaultProperties() {
        return new Properties();
    }
    
    public boolean supportsImplementation(final String s) {
        return s.equals("sort external");
    }
    
    public String primaryImplementationType() {
        return "sort external";
    }
    
    public boolean supportsFormat(final UUID uuid) {
        return uuid.equals(this.formatUUID);
    }
    
    public UUID primaryFormat() {
        return this.formatUUID;
    }
    
    protected MergeSort getMergeSort() {
        return new MergeSort();
    }
    
    public Sort createSort(final TransactionController transactionController, final int n, final Properties properties, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final SortObserver sortObserver, final boolean b, final long n2, int n3) throws StandardException {
        final MergeSort mergeSort = this.getMergeSort();
        if (!this.userSpecified) {
            if (n3 > 0) {
                n3 += 44 + array.length * 16 + 8;
                this.sortBufferMax = 1048576 / n3;
            }
            else {
                this.sortBufferMax = this.defaultSortBufferMax;
            }
            if (n2 > this.sortBufferMax && n2 * 1.1 < this.sortBufferMax * 2) {
                this.sortBufferMax = (int)(n2 / 2L + n2 / 10L);
            }
            if (this.sortBufferMax < 4) {
                this.sortBufferMax = 4;
            }
        }
        else {
            this.sortBufferMax = this.defaultSortBufferMax;
        }
        mergeSort.initialize(array, array2, sortObserver, b, n2, this.sortBufferMax);
        return mergeSort;
    }
    
    public SortCostController openSortCostController() throws StandardException {
        return this;
    }
    
    public void close() {
    }
    
    public double getSortCost(final DataValueDescriptor[] array, final ColumnOrdering[] array2, final boolean b, final long n, final long n2, final int n3) throws StandardException {
        if (n == 0L) {
            return 0.0;
        }
        return 1.0 + 0.32 * n * Math.log((double)n);
    }
    
    public boolean canSupport(final Properties properties) {
        if (properties == null) {
            return false;
        }
        final String property = properties.getProperty("derby.access.Conglomerate.type");
        return property != null && this.supportsImplementation(property);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.formatUUID = Monitor.getMonitor().getUUIDFactory().recreateUUID("D2976090-D9F5-11d0-B54D-00A024BF8879");
        this.defaultSortBufferMax = PropertyUtil.getSystemInt("derby.storage.sortBufferMax", 0, Integer.MAX_VALUE, 0);
        if (this.defaultSortBufferMax == 0) {
            this.userSpecified = false;
            this.defaultSortBufferMax = 1024;
        }
        else {
            this.userSpecified = true;
            if (this.defaultSortBufferMax < 4) {
                this.defaultSortBufferMax = 4;
            }
        }
    }
    
    public void stop() {
    }
}
