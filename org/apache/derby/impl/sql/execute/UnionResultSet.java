// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class UnionResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public int rowsSeenLeft;
    public int rowsSeenRight;
    public int rowsReturned;
    private int whichSource;
    private int source1FinalRowCount;
    public NoPutResultSet source1;
    public NoPutResultSet source2;
    
    public UnionResultSet(final NoPutResultSet source1, final NoPutResultSet source2, final Activation activation, final int n, final double n2, final double n3) {
        super(activation, n, n2, n3);
        this.whichSource = 1;
        this.source1FinalRowCount = -1;
        this.source1 = source1;
        this.source2 = source2;
        this.recordConstructorTime();
    }
    
    public ResultDescription getResultDescription() {
        return this.source1.getResultDescription();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source1.openCore();
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow currentRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            switch (this.whichSource) {
                case 1: {
                    currentRow = this.source1.getNextRowCore();
                    if (currentRow != null) {
                        ++this.rowsSeenLeft;
                        break;
                    }
                    this.source1.close();
                    this.whichSource = 2;
                    this.source2.openCore();
                    currentRow = this.source2.getNextRowCore();
                    if (currentRow != null) {
                        ++this.rowsSeenRight;
                        break;
                    }
                    break;
                }
                case 2: {
                    currentRow = this.source2.getNextRowCore();
                    if (currentRow != null) {
                        ++this.rowsSeenRight;
                        break;
                    }
                    break;
                }
            }
        }
        this.setCurrentRow(currentRow);
        if (currentRow != null) {
            ++this.rowsReturned;
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            switch (this.whichSource) {
                case 1: {
                    this.source1.close();
                    break;
                }
                case 2: {
                    this.source2.close();
                    this.source1FinalRowCount = -1;
                    this.whichSource = 1;
                    break;
                }
            }
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.source1.finish();
        this.source2.finish();
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source1.getTimeSpent(1) - this.source2.getTimeSpent(1);
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        switch (this.whichSource) {
            case 1: {
                return ((CursorResultSet)this.source1).getRowLocation();
            }
            case 2: {
                return ((CursorResultSet)this.source2).getRowLocation();
            }
            default: {
                return null;
            }
        }
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        ExecRow currentRow = null;
        switch (this.whichSource) {
            case 1: {
                currentRow = ((CursorResultSet)this.source1).getCurrentRow();
                break;
            }
            case 2: {
                currentRow = ((CursorResultSet)this.source2).getCurrentRow();
                break;
            }
        }
        this.setCurrentRow(currentRow);
        return currentRow;
    }
}
