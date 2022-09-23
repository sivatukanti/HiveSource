// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.sql.Activation;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class SortResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public int rowsInput;
    public int rowsReturned;
    public boolean distinct;
    public NoPutResultSet source;
    private ColumnOrdering[] order;
    private ColumnOrdering[] savedOrder;
    private SortObserver observer;
    private ExecRow sortTemplateRow;
    public boolean isInSortedOrder;
    private NoPutResultSet originalSource;
    private int maxRowSize;
    private ScanController scanController;
    private ExecRow sortResultRow;
    private ExecRow currSortedRow;
    private boolean nextCalled;
    private int numColumns;
    private long genericSortId;
    private boolean dropGenericSort;
    private boolean sorted;
    public Properties sortProperties;
    
    public SortResultSet(final NoPutResultSet set, final boolean distinct, final boolean isInSortedOrder, final int n, final Activation activation, final int n2, final int maxRowSize, final int n3, final double n4, final double n5) throws StandardException {
        super(activation, n3, n4, n5);
        this.sortProperties = new Properties();
        this.distinct = distinct;
        this.isInSortedOrder = isInSortedOrder;
        this.source = set;
        this.originalSource = set;
        this.maxRowSize = maxRowSize;
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        this.sortTemplateRow = ((ExecRowBuilder)preparedStatement.getSavedObject(n2)).build(activation.getExecutionFactory());
        this.order = (ColumnOrdering[])((FormatableArrayHolder)preparedStatement.getSavedObject(n)).getArray(ColumnOrdering.class);
        this.savedOrder = this.order;
        this.observer = new BasicSortObserver(true, distinct, this.sortTemplateRow, true);
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.nextCalled = false;
        this.beginTime = this.getCurrentTimeMillis();
        this.order = this.savedOrder;
        this.sortResultRow = this.sortTemplateRow.getClone();
        this.source.openCore();
        try {
            if (this.isInSortedOrder && this.distinct) {
                this.currSortedRow = this.getNextRowFromRS();
                if (this.currSortedRow != null) {
                    this.currSortedRow = this.currSortedRow.getClone();
                }
            }
            else {
                this.scanController = this.loadSorter();
                this.sorted = true;
            }
        }
        catch (StandardException ex) {
            this.isOpen = true;
            try {
                this.close();
            }
            catch (StandardException ex2) {}
            throw ex;
        }
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    private ScanController loadSorter() throws StandardException {
        final boolean b = this.order.length == 0 || this.isInSortedOrder;
        final int n = (int)this.optimizerEstimatedRowCount;
        final TransactionController transactionController = this.getTransactionController();
        final long sort = transactionController.createSort(null, this.sortTemplateRow.getRowArray(), this.order, this.observer, b, n, this.maxRowSize);
        final SortController openSort = transactionController.openSort(sort);
        this.genericSortId = sort;
        this.dropGenericSort = true;
        ExecRow nextRowFromRS;
        while ((nextRowFromRS = this.getNextRowFromRS()) != null) {
            openSort.insert(nextRowFromRS.getRowArray());
        }
        this.source.close();
        this.sortProperties = openSort.getSortInfo().getAllSortInfo(this.sortProperties);
        openSort.completedInserts();
        return transactionController.openSortScan(sort, this.activation.getResultSetHoldability());
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (!this.isOpen) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isInSortedOrder || !this.distinct) {
            final ExecRow nextRowFromRS = this.getNextRowFromRS();
            if (nextRowFromRS != null) {
                this.setCurrentRow(nextRowFromRS);
                ++this.rowsReturned;
            }
            this.nextTime += this.getElapsedMillis(this.beginTime);
            return nextRowFromRS;
        }
        if (this.currSortedRow == null) {
            this.nextTime += this.getElapsedMillis(this.beginTime);
            return null;
        }
        if (!this.nextCalled) {
            this.nextCalled = true;
            this.numColumns = this.currSortedRow.getRowArray().length;
            this.nextTime += this.getElapsedMillis(this.beginTime);
            ++this.rowsReturned;
            this.setCurrentRow(this.currSortedRow);
            return this.currSortedRow;
        }
        for (ExecRow execRow = this.getNextRowFromRS(); execRow != null; execRow = this.getNextRowFromRS()) {
            if (!this.filterRow(this.currSortedRow, execRow)) {
                this.setCurrentRow(this.currSortedRow = execRow.getClone());
                this.nextTime += this.getElapsedMillis(this.beginTime);
                ++this.rowsReturned;
                return this.currSortedRow;
            }
        }
        this.currSortedRow = null;
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return null;
    }
    
    private boolean filterRow(final ExecRow execRow, final ExecRow execRow2) throws StandardException {
        for (int i = 1; i <= this.numColumns; ++i) {
            if (!execRow.getColumn(i).compare(2, execRow2.getColumn(i), true, true)) {
                return false;
            }
        }
        return true;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.sortResultRow = null;
            this.closeSource();
            if (this.dropGenericSort) {
                this.getTransactionController().dropSort(this.genericSortId);
                this.dropGenericSort = false;
            }
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
        this.isOpen = false;
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.originalSource.getTimeSpent(1);
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        if (!this.isOpen) {
            return null;
        }
        final RowLocation rowLocationTemplate = this.scanController.newRowLocationTemplate();
        this.scanController.fetchLocation(rowLocationTemplate);
        return rowLocationTemplate;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return this.currentRow;
    }
    
    private ExecRow getNextRowFromRS() throws StandardException {
        return (this.scanController == null) ? this.getRowFromResultSet() : this.getRowFromSorter();
    }
    
    private ExecRow getRowFromResultSet() throws StandardException {
        final ExecRow nextRowCore;
        if ((nextRowCore = this.source.getNextRowCore()) != null) {
            ++this.rowsInput;
        }
        return nextRowCore;
    }
    
    private ExecRow getRowFromSorter() throws StandardException {
        ExecRow sortResultRow = null;
        if (this.scanController.next()) {
            this.currentRow = this.sortResultRow;
            sortResultRow = this.sortResultRow;
            this.scanController.fetch(sortResultRow.getRowArray());
        }
        return sortResultRow;
    }
    
    private void closeSource() throws StandardException {
        if (this.scanController == null) {
            this.source.close();
        }
        else {
            this.scanController.close();
            this.scanController = null;
        }
    }
}
