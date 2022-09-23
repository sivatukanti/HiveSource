// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;

class NestedLoopJoinResultSet extends JoinResultSet
{
    private boolean returnedRowMatchingRightSide;
    private ExecRow rightTemplate;
    
    void clearScanState() {
        this.returnedRowMatchingRightSide = false;
        super.clearScanState();
    }
    
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
        if (!this.isRightOpen && this.leftRow != null) {
            this.leftRow = this.leftResultSet.getNextRowCore();
            if (this.leftRow == null) {
                this.closeRight();
            }
            else {
                ++this.rowsSeenLeft;
                this.openRight();
            }
        }
        while (this.leftRow != null && n == 0) {
            if (this.oneRowRightSide && this.returnedRowMatchingRightSide) {
                this.rightRow = null;
                this.returnedRowMatchingRightSide = false;
            }
            else {
                this.rightRow = this.rightResultSet.getNextRowCore();
                if (this.notExistsRightSide) {
                    if (this.rightRow == null) {
                        this.rightRow = this.rightTemplate;
                    }
                    else {
                        this.rightRow = null;
                    }
                }
                this.returnedRowMatchingRightSide = (this.rightRow != null);
            }
            if (this.rightRow == null) {
                this.leftRow = this.leftResultSet.getNextRowCore();
                if (this.leftRow == null) {
                    this.closeRight();
                }
                else {
                    ++this.rowsSeenLeft;
                    this.openRight();
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
                if (this.mergedRow == null) {
                    this.mergedRow = this.getExecutionFactory().getValueRow(this.leftNumCols + this.rightNumCols);
                }
                int i;
                int n2;
                for (i = 1, n2 = 1; i <= this.leftNumCols; ++i, ++n2) {
                    DataValueDescriptor dataValueDescriptor2 = this.leftRow.getColumn(i);
                    if (dataValueDescriptor2 != null && dataValueDescriptor2.hasStream()) {
                        dataValueDescriptor2 = dataValueDescriptor2.cloneValue(false);
                    }
                    this.mergedRow.setColumn(n2, dataValueDescriptor2);
                }
                if (!this.notExistsRightSide) {
                    for (int j = 1; j <= this.rightNumCols; ++j, ++n2) {
                        DataValueDescriptor dataValueDescriptor3 = this.rightRow.getColumn(j);
                        if (dataValueDescriptor3 != null && dataValueDescriptor3.hasStream()) {
                            dataValueDescriptor3 = dataValueDescriptor3.cloneValue(false);
                        }
                        this.mergedRow.setColumn(n2, dataValueDescriptor3);
                    }
                }
                this.setCurrentRow(this.mergedRow);
                n = 1;
            }
        }
        if (n != 0) {
            mergedRow = this.mergedRow;
            ++this.rowsReturned;
        }
        else {
            this.clearCurrentRow();
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return mergedRow;
    }
    
    public void close() throws StandardException {
        if (this.isOpen) {
            this.beginTime = this.getCurrentTimeMillis();
            this.clearCurrentRow();
            super.close();
            this.returnedRowMatchingRightSide = false;
            this.closeTime += this.getElapsedMillis(this.beginTime);
        }
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.leftResultSet.getTimeSpent(1) - this.rightResultSet.getTimeSpent(1);
        }
        return n2;
    }
    
    NestedLoopJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final Activation activation, final GeneratedMethod generatedMethod, final int n3, final boolean b, final boolean b2, final double n4, final double n5, final String s) {
        super(set, n, set2, n2, activation, generatedMethod, n3, b, b2, n4, n5, s);
        this.returnedRowMatchingRightSide = false;
        if (b2) {
            this.rightTemplate = this.getExecutionFactory().getValueRow(n2);
        }
    }
}
