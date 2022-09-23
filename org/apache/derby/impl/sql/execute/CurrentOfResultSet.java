// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class CurrentOfResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    private boolean next;
    private RowLocation rowLocation;
    private CursorResultSet cursor;
    private CursorResultSet target;
    private ExecRow sparseRow;
    private final String cursorName;
    
    CurrentOfResultSet(final String cursorName, final Activation activation, final int n) {
        super(activation, n, 0.0, 0.0);
        this.cursorName = cursorName;
    }
    
    public void openCore() throws StandardException {
        this.getCursor();
        this.next = false;
        this.isOpen = true;
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (this.isOpen) {
            if (!this.next) {
                this.next = true;
                final ExecRow currentRow = this.cursor.getCurrentRow();
                if (currentRow == null) {
                    throw StandardException.newException("24000");
                }
                this.rowLocation = this.cursor.getRowLocation();
                this.currentRow = this.target.getCurrentRow();
                if (this.rowLocation == null || (currentRow != null && this.currentRow == null)) {
                    this.activation.addWarning(StandardException.newWarning("01001"));
                    return null;
                }
                if (this.target instanceof TableScanResultSet) {
                    final TableScanResultSet set = (TableScanResultSet)this.target;
                    if (set.indexCols != null && this.currentRow != null) {
                        this.currentRow = this.getSparseRow(this.currentRow, set.indexCols);
                    }
                }
            }
            else {
                this.currentRow = null;
                this.rowLocation = null;
            }
        }
        else {
            this.currentRow = null;
            this.rowLocation = null;
        }
        this.setCurrentRow(this.currentRow);
        return this.currentRow;
    }
    
    private ExecRow getSparseRow(final ExecRow execRow, final int[] array) throws StandardException {
        if (this.sparseRow == null) {
            int n = 1;
            for (int i = 0; i < array.length; ++i) {
                final int n2 = (array[i] > 0) ? array[i] : (-array[i]);
                if (n2 > n) {
                    n = n2;
                }
            }
            this.sparseRow = new ValueRow(n);
        }
        for (int j = 1; j <= array.length; ++j) {
            this.sparseRow.setColumn((array[j - 1] > 0) ? array[j - 1] : (-array[j - 1]), execRow.getColumn(j));
        }
        return this.sparseRow;
    }
    
    public void close() throws StandardException {
        if (this.isOpen) {
            this.clearCurrentRow();
            this.next = false;
            super.close();
        }
    }
    
    public void finish() throws StandardException {
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        return 0L;
    }
    
    public RowLocation getRowLocation() {
        return this.rowLocation;
    }
    
    public ExecRow getCurrentRow() {
        return this.currentRow;
    }
    
    private void getCursor() throws StandardException {
        if (this.cursor != null && this.cursor.isClosed()) {
            this.cursor = null;
            this.target = null;
        }
        if (this.cursor == null) {
            final CursorActivation lookupCursorActivation = this.getLanguageConnectionContext().lookupCursorActivation(this.cursorName);
            if (lookupCursorActivation != null) {
                this.cursor = lookupCursorActivation.getCursorResultSet();
                this.target = lookupCursorActivation.getTargetResultSet();
                this.activation.setForUpdateIndexScan(lookupCursorActivation.getForUpdateIndexScan());
                if (lookupCursorActivation.getHeapConglomerateController() != null) {
                    lookupCursorActivation.getHeapConglomerateController().close();
                }
                lookupCursorActivation.setHeapConglomerateController(this.activation.getHeapConglomerateController());
            }
        }
        if (this.cursor == null || this.cursor.isClosed()) {
            throw StandardException.newException("42X30", this.cursorName);
        }
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
        ((NoPutResultSet)this.cursor).updateRow(execRow, rowChanger);
    }
    
    public void markRowAsDeleted() throws StandardException {
        ((NoPutResultSet)this.cursor).markRowAsDeleted();
    }
}
