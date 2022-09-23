// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

abstract class DMLVTIResultSet extends DMLWriteResultSet
{
    NoPutResultSet sourceResultSet;
    NoPutResultSet savedSource;
    UpdatableVTIConstantAction constants;
    TransactionController tc;
    ResultDescription resultDescription;
    private int numOpens;
    boolean firstExecute;
    
    public ResultDescription getResultDescription() {
        return this.resultDescription;
    }
    
    DMLVTIResultSet(final NoPutResultSet sourceResultSet, final Activation activation) throws StandardException {
        super(activation);
        this.sourceResultSet = sourceResultSet;
        this.constants = (UpdatableVTIConstantAction)this.constantAction;
        this.tc = activation.getTransactionController();
        this.resultDescription = this.sourceResultSet.getResultDescription();
    }
    
    public void open() throws StandardException {
        this.setup();
        this.firstExecute = (this.numOpens == 0);
        this.rowCount = 0L;
        if (this.numOpens++ == 0) {
            this.sourceResultSet.openCore();
        }
        else {
            this.sourceResultSet.reopenCore();
        }
        this.openCore();
        if (this.lcc.getRunTimeStatisticsMode()) {
            this.savedSource = this.sourceResultSet;
        }
        this.cleanUp();
        this.endTime = this.getCurrentTimeMillis();
    }
    
    protected abstract void openCore() throws StandardException;
    
    public void cleanUp() throws StandardException {
        if (null != this.sourceResultSet) {
            this.sourceResultSet.close();
        }
        this.numOpens = 0;
        super.close();
    }
    
    public void finish() throws StandardException {
        this.sourceResultSet.finish();
        super.finish();
    }
}
