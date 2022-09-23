// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class ScalarAggregateResultSet extends GenericAggregateResultSet implements CursorResultSet
{
    public int rowsInput;
    public boolean singleInputRow;
    protected boolean isInSortedOrder;
    protected ExecIndexRow sourceExecIndexRow;
    private boolean nextSatisfied;
    protected int countOfRows;
    
    ScalarAggregateResultSet(final NoPutResultSet set, final boolean isInSortedOrder, final int n, final Activation activation, final int n2, final int n3, final boolean singleInputRow, final double n4, final double n5) throws StandardException {
        super(set, n, activation, n2, n3, n4, n5);
        this.isInSortedOrder = isInSortedOrder;
        this.singleInputRow = singleInputRow;
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.sourceExecIndexRow = (ExecIndexRow)this.getRowTemplate().getClone();
        this.source.openCore();
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (this.nextSatisfied) {
            this.clearCurrentRow();
            return null;
        }
        ExecIndexRow finishAggregation = null;
        final boolean b = this.singleInputRow && this.aggregates[0].getAggregatorInfo().aggregateName.equals("MIN");
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            ExecIndexRow rowFromResultSet;
            while ((rowFromResultSet = this.getRowFromResultSet(false)) != null) {
                if (finishAggregation == null) {
                    finishAggregation = (ExecIndexRow)((this.singleInputRow && b) ? rowFromResultSet : rowFromResultSet.getClone());
                    this.initializeScalarAggregation(finishAggregation);
                }
                else {
                    this.accumulateScalarAggregation(rowFromResultSet, finishAggregation, false);
                }
                if (this.singleInputRow && (b || !finishAggregation.getColumn(this.aggregates[0].aggregatorColumnId).isNull())) {
                    break;
                }
            }
            if (this.countOfRows == 0) {
                finishAggregation = this.finishAggregation(finishAggregation);
                this.setCurrentRow(finishAggregation);
                ++this.countOfRows;
            }
        }
        this.nextSatisfied = true;
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return finishAggregation;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.countOfRows = 0;
            this.sourceExecIndexRow = null;
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
        this.nextSatisfied = false;
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
        return null;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return this.currentRow;
    }
    
    public ExecIndexRow getRowFromResultSet(final boolean b) throws StandardException {
        ExecIndexRow sourceExecIndexRow = null;
        final ExecRow nextRowCore;
        if ((nextRowCore = this.source.getNextRowCore()) != null) {
            ++this.rowsInput;
            this.sourceExecIndexRow.execRowToExecIndexRow(b ? nextRowCore.getClone() : nextRowCore);
            sourceExecIndexRow = this.sourceExecIndexRow;
        }
        return sourceExecIndexRow;
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        ++this.numOpens;
        this.countOfRows = 0;
        this.nextSatisfied = false;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    protected void accumulateScalarAggregation(final ExecRow execRow, final ExecRow execRow2, final boolean b) throws StandardException {
        for (int length = this.aggregates.length, i = 0; i < length; ++i) {
            final GenericAggregator genericAggregator = this.aggregates[i];
            if (b && !genericAggregator.getAggregatorInfo().isDistinct()) {
                genericAggregator.merge(execRow, execRow2);
            }
            else {
                genericAggregator.accumulate(execRow, execRow2);
            }
        }
    }
    
    private void initializeScalarAggregation(final ExecRow execRow) throws StandardException {
        for (int length = this.aggregates.length, i = 0; i < length; ++i) {
            final GenericAggregator genericAggregator = this.aggregates[i];
            genericAggregator.initialize(execRow);
            genericAggregator.accumulate(execRow, execRow);
        }
    }
}
