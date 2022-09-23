// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.loader.GeneratedMethod;

class NestedLoopLeftOuterJoinResultSet extends NestedLoopJoinResultSet
{
    protected GeneratedMethod emptyRowFun;
    private boolean wasRightOuterJoin;
    private boolean matchRight;
    private boolean returnedEmptyRight;
    private ExecRow rightEmptyRow;
    public int emptyRightRowsReturned;
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow mergedRow = null;
        int n = 0;
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        if (this.returnedEmptyRight) {
            this.leftRow = this.leftResultSet.getNextRowCore();
            if (this.leftRow == null) {
                this.closeRight();
            }
            else {
                ++this.rowsSeenLeft;
                this.openRight();
            }
            this.returnedEmptyRight = false;
        }
        while (this.leftRow != null && n == 0) {
            this.rightRow = this.rightResultSet.getNextRowCore();
            if (this.rightRow == null) {
                if (!this.matchRight) {
                    n = 1;
                    this.returnedEmptyRight = true;
                    if (this.rightEmptyRow == null) {
                        this.rightEmptyRow = (ExecRow)this.emptyRowFun.invoke(this.activation);
                    }
                    this.getMergedRow(this.leftRow, this.rightEmptyRow);
                    ++this.emptyRightRowsReturned;
                }
                else {
                    this.matchRight = false;
                    this.leftRow = this.leftResultSet.getNextRowCore();
                    if (this.leftRow == null) {
                        this.closeRight();
                    }
                    else {
                        ++this.rowsSeenLeft;
                        this.openRight();
                    }
                }
            }
            else {
                ++this.rowsSeenRight;
                if (this.restriction != null) {
                    final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)this.restriction.invoke(this.activation);
                    if (dataValueDescriptor.isNull() || !dataValueDescriptor.getBoolean()) {
                        ++this.rowsFiltered;
                        continue;
                    }
                }
                this.matchRight = true;
                this.getMergedRow(this.leftRow, this.rightRow);
                n = 1;
            }
        }
        if (n != 0) {
            mergedRow = this.mergedRow;
            this.setCurrentRow(this.mergedRow);
            ++this.rowsReturned;
        }
        else {
            this.clearCurrentRow();
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return mergedRow;
    }
    
    protected void getMergedRow(ExecRow execRow, ExecRow execRow2) throws StandardException {
        int n;
        int n2;
        if (this.wasRightOuterJoin) {
            final ExecRow execRow3 = execRow;
            execRow = execRow2;
            execRow2 = execRow3;
            n = this.rightNumCols;
            n2 = this.leftNumCols;
        }
        else {
            n = this.leftNumCols;
            n2 = this.rightNumCols;
        }
        if (this.mergedRow == null) {
            this.mergedRow = this.getExecutionFactory().getValueRow(n + n2);
        }
        int i;
        int n3;
        for (i = 1, n3 = 1; i <= n; ++i, ++n3) {
            DataValueDescriptor dataValueDescriptor = execRow.getColumn(i);
            if (dataValueDescriptor != null && dataValueDescriptor.hasStream()) {
                dataValueDescriptor = dataValueDescriptor.cloneValue(false);
            }
            this.mergedRow.setColumn(n3, dataValueDescriptor);
        }
        for (int j = 1; j <= n2; ++j, ++n3) {
            DataValueDescriptor dataValueDescriptor2 = execRow2.getColumn(j);
            if (dataValueDescriptor2 != null && dataValueDescriptor2.hasStream()) {
                dataValueDescriptor2 = dataValueDescriptor2.cloneValue(false);
            }
            this.mergedRow.setColumn(n3, dataValueDescriptor2);
        }
    }
    
    void clearScanState() {
        this.matchRight = false;
        this.returnedEmptyRight = false;
        this.rightEmptyRow = null;
        this.emptyRightRowsReturned = 0;
        super.clearScanState();
    }
    
    NestedLoopLeftOuterJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final Activation activation, final GeneratedMethod generatedMethod, final int n3, final GeneratedMethod emptyRowFun, final boolean wasRightOuterJoin, final boolean b, final boolean b2, final double n4, final double n5, final String s) {
        super(set, n, set2, n2, activation, generatedMethod, n3, b, b2, n4, n5, s);
        this.matchRight = false;
        this.returnedEmptyRight = false;
        this.rightEmptyRow = null;
        this.emptyRightRowsReturned = 0;
        this.emptyRowFun = emptyRowFun;
        this.wasRightOuterJoin = wasRightOuterJoin;
    }
}
