// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextImpl;

final class RAMTransactionContext extends ContextImpl
{
    private RAMTransaction transaction;
    private final boolean abortAll;
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        boolean b = false;
        if (!this.abortAll && t instanceof StandardException) {
            final StandardException ex = (StandardException)t;
            if (ex.getSeverity() < 30000) {
                return;
            }
            if (ex.getSeverity() >= 40000) {
                b = true;
            }
        }
        else {
            b = true;
        }
        if (this.transaction != null) {
            try {
                this.transaction.invalidateConglomerateCache();
            }
            catch (StandardException ex2) {}
            this.transaction.closeControllers(true);
        }
        if (b) {
            this.transaction = null;
            this.popMe();
        }
    }
    
    RAMTransactionContext(final ContextManager contextManager, final String s, final RAMTransaction transaction, final boolean abortAll) throws StandardException {
        super(contextManager, s);
        this.abortAll = abortAll;
        (this.transaction = transaction).setContext(this);
    }
    
    RAMTransaction getTransaction() {
        return this.transaction;
    }
    
    void setTransaction(final RAMTransaction transaction) {
        this.transaction = transaction;
    }
}
