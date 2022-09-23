// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class SetOpResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    private final NoPutResultSet leftSource;
    private final NoPutResultSet rightSource;
    private final Activation activation;
    private final int opType;
    private final boolean all;
    private final int resultSetNumber;
    private DataValueDescriptor[] prevCols;
    private ExecRow leftInputRow;
    private ExecRow rightInputRow;
    private final int[] intermediateOrderByColumns;
    private final int[] intermediateOrderByDirection;
    private final boolean[] intermediateOrderByNullsLow;
    private int rowsSeenLeft;
    private int rowsSeenRight;
    private int rowsReturned;
    
    SetOpResultSet(final NoPutResultSet leftSource, final NoPutResultSet rightSource, final Activation activation, final int resultSetNumber, final long n, final double n2, final int opType, final boolean all, final int n3, final int n4, final int n5) {
        super(activation, resultSetNumber, (double)n, n2);
        this.leftSource = leftSource;
        this.rightSource = rightSource;
        this.activation = activation;
        this.resultSetNumber = resultSetNumber;
        this.opType = opType;
        this.all = all;
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        this.intermediateOrderByColumns = (int[])preparedStatement.getSavedObject(n3);
        this.intermediateOrderByDirection = (int[])preparedStatement.getSavedObject(n4);
        this.intermediateOrderByNullsLow = (boolean[])preparedStatement.getSavedObject(n5);
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.leftSource.openCore();
        try {
            this.rightSource.openCore();
            this.rightInputRow = this.rightSource.getNextRowCore();
        }
        catch (StandardException ex) {
            this.isOpen = true;
            try {
                this.close();
            }
            catch (StandardException ex2) {}
            throw ex;
        }
        if (this.rightInputRow != null) {
            ++this.rowsSeenRight;
        }
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            while ((this.leftInputRow = this.leftSource.getNextRowCore()) != null) {
                ++this.rowsSeenLeft;
                final DataValueDescriptor[] rowArray = this.leftInputRow.getRowArray();
                if (!this.all) {
                    if (this.isDuplicate(rowArray)) {
                        continue;
                    }
                    this.prevCols = this.leftInputRow.getRowArrayClone();
                }
                int compare = 0;
                while (this.rightInputRow != null && (compare = this.compare(rowArray, this.rightInputRow.getRowArray())) > 0) {
                    this.rightInputRow = this.rightSource.getNextRowCore();
                    if (this.rightInputRow != null) {
                        ++this.rowsSeenRight;
                    }
                }
                if (this.rightInputRow == null || compare < 0) {
                    if (this.opType == 2) {
                        break;
                    }
                    continue;
                }
                else {
                    if (this.all) {
                        this.rightInputRow = this.rightSource.getNextRowCore();
                        if (this.rightInputRow != null) {
                            ++this.rowsSeenRight;
                        }
                    }
                    if (this.opType == 1) {
                        break;
                    }
                    continue;
                }
            }
        }
        this.setCurrentRow(this.leftInputRow);
        if (this.currentRow != null) {
            ++this.rowsReturned;
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return this.currentRow;
    }
    
    private void advanceRightPastDuplicates(final DataValueDescriptor[] array) throws StandardException {
        while ((this.rightInputRow = this.rightSource.getNextRowCore()) != null) {
            ++this.rowsSeenRight;
            if (this.compare(array, this.rightInputRow.getRowArray()) == 0) {
                continue;
            }
        }
    }
    
    private int compare(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        for (int i = 0; i < this.intermediateOrderByColumns.length; ++i) {
            final int n = this.intermediateOrderByColumns[i];
            if (array[n].compare(1, array2[n], true, this.intermediateOrderByNullsLow[i], false)) {
                return -1 * this.intermediateOrderByDirection[i];
            }
            if (!array[n].compare(2, array2[n], true, this.intermediateOrderByNullsLow[i], false)) {
                return this.intermediateOrderByDirection[i];
            }
        }
        return 0;
    }
    
    private boolean isDuplicate(final DataValueDescriptor[] array) throws StandardException {
        if (this.prevCols == null) {
            return false;
        }
        for (int i = 0; i < this.intermediateOrderByColumns.length; ++i) {
            final int n = this.intermediateOrderByColumns[i];
            if (!array[n].compare(2, this.prevCols[n], true, false)) {
                return false;
            }
        }
        return true;
    }
    
    public ExecRow getCurrentRow() {
        return this.currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.prevCols = null;
            this.leftSource.close();
            this.rightSource.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.leftSource.finish();
        this.rightSource.finish();
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.leftSource.getTimeSpent(1) - this.rightSource.getTimeSpent(1);
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return ((CursorResultSet)this.leftSource).getRowLocation();
    }
    
    public int getOpType() {
        return this.opType;
    }
    
    public int getResultSetNumber() {
        return this.resultSetNumber;
    }
    
    public NoPutResultSet getLeftSourceInput() {
        return this.leftSource;
    }
    
    public NoPutResultSet getRightSourceInput() {
        return this.rightSource;
    }
    
    public int getRowsSeenLeft() {
        return this.rowsSeenLeft;
    }
    
    public int getRowsSeenRight() {
        return this.rowsSeenRight;
    }
    
    public int getRowsReturned() {
        return this.rowsReturned;
    }
}
