// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.Qualifier;

class LastIndexKeyResultSet extends ScanResultSet
{
    protected long conglomId;
    protected int startSearchOperator;
    protected int stopSearchOperator;
    protected Qualifier[][] qualifiers;
    public String tableName;
    public String userSuppliedOptimizerOverrides;
    public String indexName;
    protected boolean runTimeStatisticsOn;
    public String stopPositionString;
    public boolean coarserLock;
    public boolean returnedRow;
    
    public LastIndexKeyResultSet(final Activation activation, final int n, final int n2, final long conglomId, final String tableName, final String userSuppliedOptimizerOverrides, final String indexName, final int n3, final int n4, final boolean b, final int n5, final double n6, final double n7) throws StandardException {
        super(activation, n, n2, n4, b, n5, n3, n6, n7);
        this.conglomId = conglomId;
        this.tableName = tableName;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.indexName = indexName;
        this.runTimeStatisticsOn = this.getLanguageConnectionContext().getRunTimeStatisticsMode();
        activation.informOfRowCount(this, 1L);
        this.recordConstructorTime();
    }
    
    boolean canGetInstantaneousLocks() {
        return true;
    }
    
    public void openCore() throws StandardException {
        final ExecRow clone = this.candidate.getClone();
        this.beginTime = this.getCurrentTimeMillis();
        this.isOpen = true;
        final TransactionController transactionController = this.activation.getTransactionController();
        this.initIsolationLevel();
        if (transactionController.fetchMaxOnBtree(this.conglomId, 0, this.lockMode, this.isolationLevel, this.accessedCols, clone.getRowArray())) {
            this.setCurrentRow(this.getCompactRow(clone, this.accessedCols, true));
        }
        else {
            this.clearCurrentRow();
        }
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (this.returnedRow || !this.isOpen) {
            this.clearCurrentRow();
        }
        else {
            this.returnedRow = true;
        }
        return this.currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.isOpen = false;
            this.returnedRow = false;
            this.clearCurrentRow();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2;
        }
        return n2;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return this.currentRow;
    }
}
