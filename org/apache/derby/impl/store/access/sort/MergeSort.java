// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.store.raw.Transaction;
import java.util.Enumeration;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.conglomerate.ScanControllerRowSource;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import java.util.Properties;
import java.util.Vector;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.Sort;

class MergeSort implements Sort
{
    private static final int STATE_CLOSED = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_INSERTING = 2;
    private static final int STATE_DONE_INSERTING = 3;
    private static final int STATE_SCANNING = 4;
    private static final int STATE_DONE_SCANNING = 5;
    private int state;
    protected DataValueDescriptor[] template;
    protected ColumnOrdering[] columnOrdering;
    protected int[] columnOrderingMap;
    protected boolean[] columnOrderingAscendingMap;
    protected boolean[] columnOrderingNullsLowMap;
    SortObserver sortObserver;
    protected boolean alreadyInOrder;
    private MergeInserter inserter;
    private Scan scan;
    private Vector mergeRuns;
    private SortBuffer sortBuffer;
    int sortBufferMax;
    int sortBufferMin;
    static Properties properties;
    
    MergeSort() {
        this.state = 0;
        this.inserter = null;
        this.scan = null;
        this.mergeRuns = null;
        this.sortBuffer = null;
    }
    
    public SortController open(final TransactionManager transactionManager) throws StandardException {
        this.state = 2;
        this.inserter = new MergeInserter();
        if (!this.inserter.initialize(this, transactionManager)) {
            throw StandardException.newException("XSAS6.S");
        }
        return this.inserter;
    }
    
    public ScanController openSortScan(final TransactionManager transactionManager, final boolean b) throws StandardException {
        if (this.mergeRuns == null || this.mergeRuns.size() == 0) {
            this.scan = new SortBufferScan(this, transactionManager, this.sortBuffer, b);
            this.sortBuffer = null;
        }
        else {
            this.mergeRuns.addElement(new Long(this.createMergeRun(transactionManager, this.sortBuffer)));
            if (this.mergeRuns.size() > 512 || this.mergeRuns.size() > this.sortBuffer.capacity()) {
                this.multiStageMerge(transactionManager);
            }
            final MergeScan scan = new MergeScan(this, transactionManager, this.sortBuffer, this.mergeRuns, this.sortObserver, b);
            if (!scan.init(transactionManager)) {
                throw StandardException.newException("XSAS6.S");
            }
            this.scan = scan;
            this.sortBuffer = null;
            this.mergeRuns = null;
        }
        this.state = 4;
        return this.scan;
    }
    
    public ScanControllerRowSource openSortRowSource(final TransactionManager transactionManager) throws StandardException {
        ScanControllerRowSource scanControllerRowSource;
        if (this.mergeRuns == null || this.mergeRuns.size() == 0) {
            this.scan = new SortBufferRowSource(this.sortBuffer, transactionManager, this.sortObserver, false, this.sortBufferMax);
            scanControllerRowSource = (ScanControllerRowSource)this.scan;
            this.sortBuffer = null;
        }
        else {
            this.mergeRuns.addElement(new Long(this.createMergeRun(transactionManager, this.sortBuffer)));
            if (this.mergeRuns.size() > 512 || this.mergeRuns.size() > this.sortBuffer.capacity()) {
                this.multiStageMerge(transactionManager);
            }
            final MergeScanRowSource scan = new MergeScanRowSource(this, transactionManager, this.sortBuffer, this.mergeRuns, this.sortObserver, false);
            if (!scan.init(transactionManager)) {
                throw StandardException.newException("XSAS6.S");
            }
            this.scan = scan;
            scanControllerRowSource = scan;
            this.sortBuffer = null;
            this.mergeRuns = null;
        }
        this.state = 4;
        return scanControllerRowSource;
    }
    
    public void drop(final TransactionController transactionController) throws StandardException {
        if (this.inserter != null) {
            this.inserter.completedInserts();
        }
        this.inserter = null;
        if (this.scan != null) {
            this.scan.close();
            this.scan = null;
        }
        if (this.sortBuffer != null) {
            this.sortBuffer.close();
            this.sortBuffer = null;
        }
        this.template = null;
        this.columnOrdering = null;
        this.sortObserver = null;
        this.dropMergeRuns((TransactionManager)transactionController);
        this.state = 0;
    }
    
    private boolean checkColumnOrdering(final DataValueDescriptor[] array, final ColumnOrdering[] array2) {
        final int length = array.length;
        final boolean[] array3 = new boolean[length];
        for (int i = 0; i < array2.length; ++i) {
            final int columnId = array2[i].getColumnId();
            if (columnId < 0 || columnId >= length) {
                return false;
            }
            if (array3[columnId]) {
                return false;
            }
            array3[columnId] = true;
            if (RowUtil.getColumn(array, null, columnId) == null) {
                return false;
            }
        }
        return true;
    }
    
    void checkColumnTypes(final DataValueDescriptor[] array) throws StandardException {
        if (this.template.length != array.length) {
            throw StandardException.newException("XSAS3.S");
        }
    }
    
    protected int compare(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        final int length = this.columnOrdering.length;
        int i = 0;
        while (i < length) {
            final int n = this.columnOrderingMap[i];
            final int compare;
            if ((compare = array[n].compare(array2[n], this.columnOrderingNullsLowMap[i])) != 0) {
                if (this.columnOrderingAscendingMap[i]) {
                    return compare;
                }
                return -compare;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    public void initialize(final DataValueDescriptor[] template, final ColumnOrdering[] columnOrdering, final SortObserver sortObserver, final boolean alreadyInOrder, final long n, final int n2) throws StandardException {
        this.template = template;
        this.columnOrdering = columnOrdering;
        this.sortObserver = sortObserver;
        this.alreadyInOrder = alreadyInOrder;
        this.columnOrderingMap = new int[columnOrdering.length];
        this.columnOrderingAscendingMap = new boolean[columnOrdering.length];
        this.columnOrderingNullsLowMap = new boolean[columnOrdering.length];
        for (int i = 0; i < columnOrdering.length; ++i) {
            this.columnOrderingMap[i] = columnOrdering[i].getColumnId();
            this.columnOrderingAscendingMap[i] = columnOrdering[i].getIsAscending();
            this.columnOrderingNullsLowMap[i] = columnOrdering[i].getIsNullsOrderedLow();
        }
        this.inserter = null;
        this.scan = null;
        this.mergeRuns = null;
        this.sortBuffer = null;
        this.sortBufferMax = n2;
        if (n > n2) {
            this.sortBufferMin = n2;
        }
        else {
            this.sortBufferMin = (int)n;
        }
        this.state = 1;
    }
    
    void doneInserting(final MergeInserter mergeInserter, final SortBuffer sortBuffer, final Vector mergeRuns) {
        this.sortBuffer = sortBuffer;
        this.mergeRuns = mergeRuns;
        this.inserter = null;
        this.state = 3;
    }
    
    void doneScanning(final Scan scan, final SortBuffer sortBuffer) {
        this.sortBuffer = sortBuffer;
        this.scan = null;
        this.state = 5;
    }
    
    void doneScanning(final Scan scan, final SortBuffer sortBuffer, final Vector mergeRuns) {
        this.mergeRuns = mergeRuns;
        this.doneScanning(scan, sortBuffer);
    }
    
    void dropMergeRuns(final TransactionManager transactionManager) {
        if (this.mergeRuns != null) {
            final Enumeration<Long> elements = (Enumeration<Long>)this.mergeRuns.elements();
            try {
                final Transaction rawStoreXact = transactionManager.getRawStoreXact();
                final long n = -1L;
                while (elements.hasMoreElements()) {
                    rawStoreXact.dropStreamContainer(n, elements.nextElement());
                }
            }
            catch (StandardException ex) {}
            this.mergeRuns = null;
        }
    }
    
    private void multiStageMerge(final TransactionManager transactionManager) throws StandardException {
        int capacity = this.sortBuffer.capacity();
        if (capacity > 512) {
            capacity = 512;
        }
        while (this.mergeRuns.size() > capacity) {
            final Vector vector = new Vector<Long>(capacity);
            final Vector mergeRuns = new Vector<Long>(this.mergeRuns.size() - capacity);
            final Enumeration<Long> elements = this.mergeRuns.elements();
            while (elements.hasMoreElements()) {
                final Long n = elements.nextElement();
                if (vector.size() < capacity) {
                    vector.addElement(n);
                }
                else {
                    mergeRuns.addElement(n);
                }
            }
            this.mergeRuns = mergeRuns;
            final MergeScanRowSource mergeScanRowSource = new MergeScanRowSource(this, transactionManager, this.sortBuffer, vector, this.sortObserver, false);
            if (!mergeScanRowSource.init(transactionManager)) {
                throw StandardException.newException("XSAS6.S");
            }
            final Transaction rawStoreXact = transactionManager.getRawStoreXact();
            final int n2 = -1;
            this.mergeRuns.addElement(new Long(rawStoreXact.addAndLoadStreamContainer(n2, MergeSort.properties, mergeScanRowSource)));
            final Enumeration<Long> elements2 = vector.elements();
            while (elements2.hasMoreElements()) {
                rawStoreXact.dropStreamContainer(n2, elements2.nextElement());
            }
        }
    }
    
    long createMergeRun(final TransactionManager transactionManager, final SortBuffer sortBuffer) throws StandardException {
        return transactionManager.getRawStoreXact().addAndLoadStreamContainer(-1, MergeSort.properties, new SortBufferRowSource(sortBuffer, null, this.sortObserver, true, this.sortBufferMax));
    }
    
    static {
        MergeSort.properties = null;
        (MergeSort.properties = new Properties()).put("derby.storage.streamFileBufferSize", "16384");
    }
}
