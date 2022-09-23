// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.util.Properties;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.HashSet;
import java.util.List;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class GroupedAggregateResultSet extends GenericAggregateResultSet implements CursorResultSet
{
    public int rowsInput;
    public int rowsReturned;
    private ColumnOrdering[] order;
    public boolean hasDistinctAggregate;
    public boolean isInSortedOrder;
    private int numDistinctAggs;
    private int maxRowSize;
    private ScanController scanController;
    private ExecIndexRow sourceExecIndexRow;
    private ExecIndexRow sortResultRow;
    private boolean resultsComplete;
    private List finishedResults;
    private ExecIndexRow[] resultRows;
    private HashSet[][] distinctValues;
    private boolean rollup;
    private boolean usingAggregateObserver;
    private long genericSortId;
    private TransactionController tc;
    public Properties sortProperties;
    
    GroupedAggregateResultSet(final NoPutResultSet set, final boolean isInSortedOrder, final int n, final int n2, final Activation activation, final int n3, final int n4, final int n5, final double n6, final double n7, final boolean rollup) throws StandardException {
        super(set, n, activation, n3, n5, n6, n7);
        this.numDistinctAggs = 0;
        this.usingAggregateObserver = false;
        this.sortProperties = new Properties();
        this.isInSortedOrder = isInSortedOrder;
        this.rollup = rollup;
        this.finishedResults = new ArrayList();
        this.order = (ColumnOrdering[])((FormatableArrayHolder)activation.getPreparedStatement().getSavedObject(n2)).getArray(ColumnOrdering.class);
        this.hasDistinctAggregate = this.aggInfoList.hasDistinct();
        this.usingAggregateObserver = (!isInSortedOrder && !this.rollup && !this.hasDistinctAggregate);
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.sortResultRow = (ExecIndexRow)this.getRowTemplate().getClone();
        this.sourceExecIndexRow = (ExecIndexRow)this.getRowTemplate().getClone();
        this.source.openCore();
        try {
            if (!this.isInSortedOrder) {
                this.scanController = this.loadSorter();
            }
            final ExecIndexRow nextRowFromRS = this.getNextRowFromRS();
            this.resultsComplete = (nextRowFromRS == null);
            if (this.usingAggregateObserver) {
                if (nextRowFromRS != null) {
                    this.finishedResults.add(this.finishAggregation(nextRowFromRS).getClone());
                }
            }
            else if (!this.resultsComplete) {
                if (this.rollup) {
                    this.resultRows = new ExecIndexRow[this.numGCols() + 1];
                }
                else {
                    this.resultRows = new ExecIndexRow[1];
                }
                if (this.aggInfoList.hasDistinct()) {
                    this.distinctValues = new HashSet[this.resultRows.length][this.aggregates.length];
                }
                for (int i = 0; i < this.resultRows.length; ++i) {
                    this.initializeVectorAggregation(this.resultRows[i] = (ExecIndexRow)nextRowFromRS.getClone());
                    if (this.aggInfoList.hasDistinct()) {
                        this.distinctValues[i] = new HashSet[this.aggregates.length];
                    }
                    this.initializeDistinctMaps(i, true);
                }
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
        final int n = (int)this.optimizerEstimatedRowCount;
        final ExecIndexRow rowTemplate = this.getRowTemplate();
        this.tc = this.getTransactionController();
        BasicSortObserver basicSortObserver;
        if (this.usingAggregateObserver) {
            basicSortObserver = new AggregateSortObserver(true, this.aggregates, this.aggregates, rowTemplate);
        }
        else {
            basicSortObserver = new BasicSortObserver(true, false, rowTemplate, true);
        }
        this.genericSortId = this.tc.createSort(null, rowTemplate.getRowArray(), this.order, basicSortObserver, false, n, this.maxRowSize);
        final SortController openSort = this.tc.openSort(this.genericSortId);
        ExecIndexRow nextRowFromRS;
        while ((nextRowFromRS = this.getNextRowFromRS()) != null) {
            openSort.insert(nextRowFromRS.getRowArray());
        }
        this.source.close();
        openSort.completedInserts();
        this.sortProperties = openSort.getSortInfo().getAllSortInfo(this.sortProperties);
        if (this.aggInfoList.hasDistinct()) {
            this.numDistinctAggs = 1;
        }
        return this.tc.openSortScan(this.genericSortId, this.activation.getResultSetHoldability());
    }
    
    private int numGCols() {
        return this.order.length - this.numDistinctAggs;
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (!this.isOpen) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (this.finishedResults.size() > 0) {
            return this.makeCurrent(this.finishedResults.remove(0));
        }
        if (this.resultsComplete) {
            return null;
        }
        ExecIndexRow execIndexRow = this.getNextRowFromRS();
        if (execIndexRow == null) {
            return this.finalizeResults();
        }
        if (this.usingAggregateObserver) {
            return this.finishAggregation(execIndexRow);
        }
        while (execIndexRow != null) {
            final ExecIndexRow execIndexRow2 = this.resultRows[this.resultRows.length - 1];
            final ExecRow clone = execIndexRow.getClone();
            this.initializeVectorAggregation(execIndexRow);
            final int sameGroupingValues = this.sameGroupingValues(execIndexRow2, execIndexRow);
            for (int i = 0; i < this.resultRows.length; ++i) {
                if (this.rollup ? (i <= sameGroupingValues) : (sameGroupingValues == this.numGCols())) {
                    this.mergeVectorAggregates(execIndexRow, this.resultRows[i], i);
                }
                else {
                    this.setRollupColumnsToNull(this.resultRows[i], i);
                    this.finishedResults.add(this.finishAggregation(this.resultRows[i]));
                    this.initializeVectorAggregation(this.resultRows[i] = (ExecIndexRow)clone.getClone());
                    this.initializeDistinctMaps(i, false);
                }
            }
            if (this.finishedResults.size() > 0) {
                this.nextTime += this.getElapsedMillis(this.beginTime);
                ++this.rowsReturned;
                return this.makeCurrent(this.finishedResults.remove(0));
            }
            execIndexRow = this.getNextRowFromRS();
        }
        return this.finalizeResults();
    }
    
    private ExecRow makeCurrent(final Object o) throws StandardException {
        final ExecRow currentRow = (ExecRow)o;
        this.setCurrentRow(currentRow);
        return currentRow;
    }
    
    private ExecRow finalizeResults() throws StandardException {
        this.resultsComplete = true;
        if (!this.usingAggregateObserver) {
            for (int i = 0; i < this.resultRows.length; ++i) {
                this.setRollupColumnsToNull(this.resultRows[i], i);
                this.finishedResults.add(this.finishAggregation(this.resultRows[i]));
            }
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        if (this.finishedResults.size() > 0) {
            return this.makeCurrent(this.finishedResults.remove(0));
        }
        return null;
    }
    
    private int sameGroupingValues(final ExecRow execRow, final ExecRow execRow2) throws StandardException {
        for (int i = 0; i < this.numGCols(); ++i) {
            if (!execRow.getColumn(this.order[i].getColumnId() + 1).compare(2, execRow2.getColumn(this.order[i].getColumnId() + 1), true, true)) {
                return i;
            }
        }
        return this.numGCols();
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.sortResultRow = null;
            this.sourceExecIndexRow = null;
            this.closeSource();
            if (!this.isInSortedOrder) {
                this.tc.dropSort(this.genericSortId);
            }
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
        this.isOpen = false;
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
    
    private ExecIndexRow getNextRowFromRS() throws StandardException {
        return (this.scanController == null) ? this.getRowFromResultSet() : this.getRowFromSorter();
    }
    
    private ExecIndexRow getRowFromResultSet() throws StandardException {
        ExecIndexRow sourceExecIndexRow = null;
        final ExecRow nextRowCore;
        if ((nextRowCore = this.source.getNextRowCore()) != null) {
            ++this.rowsInput;
            this.sourceExecIndexRow.execRowToExecIndexRow(nextRowCore);
            sourceExecIndexRow = this.sourceExecIndexRow;
        }
        return sourceExecIndexRow;
    }
    
    private void setRollupColumnsToNull(final ExecRow execRow, final int n) throws StandardException {
        for (int n2 = this.resultRows.length - n - 1, i = 0; i < n2; ++i) {
            execRow.getColumn(this.order[this.numGCols() - 1 - i].getColumnId() + 1).setToNull();
        }
    }
    
    private ExecIndexRow getRowFromSorter() throws StandardException {
        ExecRow indexableRow = null;
        if (this.scanController.next()) {
            this.currentRow = this.sortResultRow;
            indexableRow = this.getExecutionFactory().getIndexableRow(this.currentRow);
            this.scanController.fetch(indexableRow.getRowArray());
        }
        return (ExecIndexRow)indexableRow;
    }
    
    public void closeSource() throws StandardException {
        if (this.scanController == null) {
            this.source.close();
        }
        else {
            this.scanController.close();
            this.scanController = null;
        }
    }
    
    private void initializeVectorAggregation(final ExecRow execRow) throws StandardException {
        for (int length = this.aggregates.length, i = 0; i < length; ++i) {
            final GenericAggregator genericAggregator = this.aggregates[i];
            genericAggregator.initialize(execRow);
            genericAggregator.accumulate(execRow, execRow);
        }
    }
    
    private void mergeVectorAggregates(final ExecRow execRow, final ExecRow execRow2, final int n) throws StandardException {
        for (int i = 0; i < this.aggregates.length; ++i) {
            final GenericAggregator genericAggregator = this.aggregates[i];
            if (((AggregatorInfo)this.aggInfoList.elementAt(i)).isDistinct()) {
                final DataValueDescriptor inputColumnValue = genericAggregator.getInputColumnValue(execRow);
                if (inputColumnValue.getString() != null) {
                    if (this.distinctValues[n][i].contains(inputColumnValue.getString())) {
                        continue;
                    }
                    this.distinctValues[n][i].add(inputColumnValue.getString());
                }
            }
            genericAggregator.merge(execRow, execRow2);
        }
    }
    
    private void initializeDistinctMaps(final int n, final boolean b) throws StandardException {
        for (int i = 0; i < this.aggregates.length; ++i) {
            if (((AggregatorInfo)this.aggInfoList.elementAt(i)).isDistinct()) {
                if (b) {
                    this.distinctValues[n][i] = new HashSet();
                }
                else {
                    this.distinctValues[n][i].clear();
                }
                this.distinctValues[n][i].add(this.aggregates[i].getInputColumnValue(this.resultRows[n]).getString());
            }
        }
    }
    
    private void dumpAllRows(final int i) throws StandardException {
        System.out.println("dumpAllRows(" + i + "/" + this.resultRows.length + "):");
        for (int j = 0; j < this.resultRows.length; ++j) {
            System.out.println(this.dumpRow(this.resultRows[j]));
        }
    }
    
    private String dumpRow(final ExecRow execRow) throws StandardException {
        if (execRow == null) {
            return "<NULL ROW>";
        }
        final StringBuffer sb = new StringBuffer();
        for (int nColumns = execRow.nColumns(), i = 0; i < nColumns; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            final DataValueDescriptor column = execRow.getColumn(i + 1);
            sb.append(column.getString());
            if (column instanceof ExecAggregator) {
                sb.append("[").append(((ExecAggregator)column).getResult().getString()).append("]");
            }
        }
        return sb.toString();
    }
}
