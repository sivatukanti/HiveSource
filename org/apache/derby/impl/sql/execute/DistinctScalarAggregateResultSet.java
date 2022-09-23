// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.SortObserver;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ColumnOrdering;

class DistinctScalarAggregateResultSet extends ScalarAggregateResultSet
{
    private ColumnOrdering[] order;
    private int maxRowSize;
    private boolean dropDistinctAggSort;
    private long sortId;
    private ScanController scanController;
    private ExecIndexRow sortResultRow;
    private boolean sorted;
    
    DistinctScalarAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final Activation activation, final int n3, final int maxRowSize, final int n4, final boolean b2, final double n5, final double n6) throws StandardException {
        super(set, b, n, activation, n3, n4, b2, n5, n6);
        this.order = (ColumnOrdering[])((FormatableArrayHolder)activation.getPreparedStatement().getSavedObject(n2)).getArray(ColumnOrdering.class);
        this.maxRowSize = maxRowSize;
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.sortResultRow = (ExecIndexRow)this.getRowTemplate().getClone();
        this.sourceExecIndexRow = (ExecIndexRow)this.getRowTemplate().getClone();
        this.source.openCore();
        try {
            this.scanController = this.loadSorter();
        }
        catch (StandardException ex) {
            this.isOpen = true;
            try {
                this.close();
            }
            catch (StandardException ex2) {}
            throw ex;
        }
        this.sorted = true;
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecIndexRow finishAggregation = null;
        boolean b = true;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            ExecIndexRow rowFromResultSet;
            while ((rowFromResultSet = this.getRowFromResultSet(b)) != null) {
                if (finishAggregation == null) {
                    b = false;
                    finishAggregation = (ExecIndexRow)rowFromResultSet.getClone();
                }
                else {
                    this.accumulateScalarAggregation(rowFromResultSet, finishAggregation, true);
                }
            }
            if (this.countOfRows == 0) {
                finishAggregation = this.finishAggregation(finishAggregation);
                this.setCurrentRow(finishAggregation);
                ++this.countOfRows;
            }
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return finishAggregation;
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.scanController != null) {
            this.scanController.close();
            this.scanController = null;
        }
        this.source.reopenCore();
        this.scanController = this.loadSorter();
        this.sorted = true;
        ++this.numOpens;
        this.countOfRows = 0;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void close() throws StandardException {
        super.close();
        this.closeSource();
    }
    
    public ExecIndexRow getRowFromResultSet(final boolean b) throws StandardException {
        ExecRow indexableRow = null;
        if (this.scanController.next()) {
            this.currentRow = (b ? this.sortResultRow.getClone() : this.sortResultRow);
            indexableRow = this.getExecutionFactory().getIndexableRow(this.currentRow);
            this.scanController.fetch(indexableRow.getRowArray());
        }
        return (ExecIndexRow)indexableRow;
    }
    
    protected void closeSource() throws StandardException {
        if (this.scanController != null) {
            if (this.dropDistinctAggSort) {
                try {
                    this.getTransactionController().dropSort(this.sortId);
                }
                catch (StandardException ex) {}
                this.dropDistinctAggSort = false;
            }
            this.scanController.close();
            this.scanController = null;
        }
        this.source.close();
    }
    
    private ScanController loadSorter() throws StandardException {
        final ExecIndexRow rowTemplate = this.getRowTemplate();
        final int n = (int)this.optimizerEstimatedRowCount;
        final TransactionController transactionController = this.getTransactionController();
        this.sortId = transactionController.createSort(null, rowTemplate.getRowArray(), this.order, new AggregateSortObserver(true, this.getSortAggregators(this.aggInfoList, true, this.activation.getLanguageConnectionContext(), this.source), this.aggregates, rowTemplate), false, n, this.maxRowSize);
        final SortController openSort = transactionController.openSort(this.sortId);
        this.dropDistinctAggSort = true;
        ExecRow nextRowCore;
        while ((nextRowCore = this.source.getNextRowCore()) != null) {
            openSort.insert(nextRowCore.getRowArray());
            ++this.rowsInput;
        }
        openSort.completedInserts();
        this.scanController = transactionController.openSortScan(this.sortId, this.activation.getResultSetHoldability());
        final int rowsInput = this.rowsInput;
        return this.scanController;
    }
}
