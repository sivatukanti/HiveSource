// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ConstantAction;

class LockTableConstantAction implements ConstantAction
{
    private final String fullTableName;
    private final long conglomerateNumber;
    private final boolean exclusiveMode;
    
    LockTableConstantAction(final String fullTableName, final long conglomerateNumber, final boolean exclusiveMode) {
        this.fullTableName = fullTableName;
        this.conglomerateNumber = conglomerateNumber;
        this.exclusiveMode = exclusiveMode;
    }
    
    public String toString() {
        return "LOCK TABLE " + this.fullTableName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final TransactionController transactionController = activation.getTransactionController();
        try {
            transactionController.openConglomerate(this.conglomerateNumber, false, this.exclusiveMode ? 68 : 64, 7, 5).close();
        }
        catch (StandardException exception) {
            exception.getMessageId();
            if (exception.isLockTimeoutOrDeadlock()) {
                exception = StandardException.newException("X0X02.S", exception, this.fullTableName, this.exclusiveMode ? "EXCLUSIVE" : "SHARE");
            }
            throw exception;
        }
    }
}
