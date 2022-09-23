// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class BulkTableScanResultSet extends TableScanResultSet implements CursorResultSet
{
    private DataValueDescriptor[][] rowArray;
    private int curRowPosition;
    private int numRowsInArray;
    private static int OUT_OF_ROWS;
    
    BulkTableScanResultSet(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final Activation activation, final int n2, final int n3, final GeneratedMethod generatedMethod, final int n4, final GeneratedMethod generatedMethod2, final int n5, final boolean b, final Qualifier[][] array, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n6, final int n7, final int n8, final boolean b4, final int n9, final int n10, final boolean b5, final boolean b6, final double n11, final double n12) throws StandardException {
        super(n, staticCompiledOpenConglomInfo, activation, n2, n3, generatedMethod, n4, generatedMethod2, n5, b, array, s, s2, s3, b2, b3, n6, n7, n8, b4, n9, adjustBulkFetchSize(activation, n10, b5), b6, n11, n12);
    }
    
    private static int adjustBulkFetchSize(final Activation activation, final int n, final boolean b) {
        if (b && activation.getResultSetHoldability()) {
            return 1;
        }
        return n;
    }
    
    protected void openScanController(TransactionController transactionController) throws StandardException {
        final DataValueDescriptor[] array = (DataValueDescriptor[])((this.startPosition == null) ? null : this.startPosition.getRowArray());
        final DataValueDescriptor[] array2 = (DataValueDescriptor[])((this.stopPosition == null) ? null : this.stopPosition.getRowArray());
        if (this.qualifiers != null) {
            this.clearOrderableCache(this.qualifiers);
        }
        if (transactionController == null) {
            transactionController = this.activation.getTransactionController();
        }
        this.scanController = transactionController.openCompiledScan(this.activation.getResultSetHoldability(), this.forUpdate ? 4 : 0, this.lockMode, this.isolationLevel, this.accessedCols, array, this.startSearchOperator, this.qualifiers, array2, this.stopSearchOperator, this.scoci, this.dcoci);
        this.scanControllerOpened = true;
        this.rowsThisScan = 0L;
        this.activation.informOfRowCount(this, this.scanController.getEstimatedRowCount());
    }
    
    public void openCore() throws StandardException {
        super.openCore();
        this.beginTime = this.getCurrentTimeMillis();
        (this.rowArray = new DataValueDescriptor[this.rowsPerRead][])[0] = this.candidate.getRowArrayClone();
        this.numRowsInArray = 0;
        this.curRowPosition = -1;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        super.reopenCore();
        this.numRowsInArray = 0;
        this.curRowPosition = -1;
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow currentRow = null;
        this.checkCancellationFlag();
        this.beginTime = this.getCurrentTimeMillis();
        Label_0205: {
            if (this.isOpen && this.scanControllerOpened) {
                if (this.currentRow == null) {
                    this.currentRow = this.getCompactRow(this.candidate, this.accessedCols, this.isKeyed);
                }
                while (this.curRowPosition < this.numRowsInArray - 1 || this.reloadArray() != BulkTableScanResultSet.OUT_OF_ROWS) {
                    while (++this.curRowPosition < this.numRowsInArray) {
                        this.candidate.setRowArray(this.rowArray[this.curRowPosition]);
                        this.currentRow = this.setCompactRow(this.candidate, this.currentRow);
                        ++this.rowsSeen;
                        ++this.rowsThisScan;
                        if (!this.skipRow(this.candidate)) {
                            currentRow = this.currentRow;
                            break Label_0205;
                        }
                        ++this.rowsFiltered;
                    }
                }
                this.clearCurrentRow();
                this.setRowCountIfPossible(this.rowsThisScan);
                return null;
            }
        }
        this.setCurrentRow(currentRow);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    private int reloadArray() throws StandardException {
        this.curRowPosition = -1;
        return this.numRowsInArray = ((GroupFetchScanController)this.scanController).fetchNextGroup(this.rowArray, null);
    }
    
    public void close() throws StandardException {
        super.close();
        this.numRowsInArray = -1;
        this.curRowPosition = -1;
        this.rowArray = null;
    }
    
    protected boolean canGetInstantaneousLocks() {
        return !this.forUpdate;
    }
    
    public boolean requiresRelocking() {
        return this.isolationLevel == 2 || this.isolationLevel == 3 || this.isolationLevel == 1;
    }
    
    static {
        BulkTableScanResultSet.OUT_OF_ROWS = 0;
    }
}
