// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;

public class RISetChecker
{
    private GenericRIChecker[] checkers;
    
    public RISetChecker(final TransactionController transactionController, final FKInfo[] array) throws StandardException {
        if (array == null) {
            return;
        }
        this.checkers = new GenericRIChecker[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.checkers[i] = ((array[i].type == 1) ? new ForeignKeyRIChecker(transactionController, array[i]) : new ReferencedKeyRIChecker(transactionController, array[i]));
        }
    }
    
    void reopen() throws StandardException {
    }
    
    public void doPKCheck(final ExecRow execRow, final boolean b) throws StandardException {
        if (this.checkers == null) {
            return;
        }
        for (int i = 0; i < this.checkers.length; ++i) {
            if (this.checkers[i] instanceof ReferencedKeyRIChecker) {
                this.checkers[i].doCheck(execRow, b);
            }
        }
    }
    
    public void doFKCheck(final ExecRow execRow) throws StandardException {
        if (this.checkers == null) {
            return;
        }
        for (int i = 0; i < this.checkers.length; ++i) {
            if (this.checkers[i] instanceof ForeignKeyRIChecker) {
                this.checkers[i].doCheck(execRow);
            }
        }
    }
    
    public void doRICheck(final int n, final ExecRow execRow, final boolean b) throws StandardException {
        this.checkers[n].doCheck(execRow, b);
    }
    
    public void close() throws StandardException {
        if (this.checkers == null) {
            return;
        }
        for (int i = 0; i < this.checkers.length; ++i) {
            this.checkers[i].close();
        }
    }
}
