// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

abstract class JoinResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public int rowsSeenLeft;
    public int rowsSeenRight;
    public int rowsReturned;
    public long restrictionTime;
    protected boolean isRightOpen;
    protected ExecRow leftRow;
    protected ExecRow rightRow;
    protected ExecRow mergedRow;
    public NoPutResultSet leftResultSet;
    protected int leftNumCols;
    public NoPutResultSet rightResultSet;
    protected int rightNumCols;
    protected GeneratedMethod restriction;
    public boolean oneRowRightSide;
    public boolean notExistsRightSide;
    String userSuppliedOptimizerOverrides;
    
    JoinResultSet(final NoPutResultSet leftResultSet, final int leftNumCols, final NoPutResultSet rightResultSet, final int rightNumCols, final Activation activation, final GeneratedMethod restriction, final int n, final boolean oneRowRightSide, final boolean notExistsRightSide, final double n2, final double n3, final String userSuppliedOptimizerOverrides) {
        super(activation, n, n2, n3);
        this.leftResultSet = leftResultSet;
        this.leftNumCols = leftNumCols;
        this.rightResultSet = rightResultSet;
        this.rightNumCols = rightNumCols;
        this.restriction = restriction;
        this.oneRowRightSide = oneRowRightSide;
        this.notExistsRightSide = notExistsRightSide;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.recordConstructorTime();
    }
    
    void clearScanState() {
        this.leftRow = null;
        this.rightRow = null;
        this.mergedRow = null;
    }
    
    public void openCore() throws StandardException {
        this.clearScanState();
        this.beginTime = this.getCurrentTimeMillis();
        this.leftResultSet.openCore();
        try {
            this.leftRow = this.leftResultSet.getNextRowCore();
            if (this.leftRow != null) {
                this.openRight();
                ++this.rowsSeenLeft;
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
    
    public void reopenCore() throws StandardException {
        this.clearScanState();
        this.leftResultSet.reopenCore();
        this.leftRow = this.leftResultSet.getNextRowCore();
        if (this.leftRow != null) {
            this.openRight();
            ++this.rowsSeenLeft;
        }
        else if (this.isRightOpen) {
            this.closeRight();
        }
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void close() throws StandardException {
        if (this.isOpen) {
            this.leftResultSet.close();
            if (this.isRightOpen) {
                this.closeRight();
            }
            super.close();
        }
        this.clearScanState();
    }
    
    public void finish() throws StandardException {
        this.leftResultSet.finish();
        this.rightResultSet.finish();
        super.finish();
    }
    
    public RowLocation getRowLocation() {
        return null;
    }
    
    public ExecRow getCurrentRow() {
        return null;
    }
    
    protected void openRight() throws StandardException {
        if (this.isRightOpen) {
            this.rightResultSet.reopenCore();
        }
        else {
            this.rightResultSet.openCore();
            this.isRightOpen = true;
        }
    }
    
    protected void closeRight() throws StandardException {
        this.rightResultSet.close();
        this.isRightOpen = false;
    }
}
