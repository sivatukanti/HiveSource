// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import javax.transaction.Synchronization;
import javax.transaction.xa.XAResource;
import javax.transaction.SystemException;
import java.sql.SQLException;
import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import java.sql.Connection;
import java.lang.ref.WeakReference;

public class TransactionContext
{
    private final TransactionRegistry transactionRegistry;
    private final WeakReference transactionRef;
    private Connection sharedConnection;
    
    public TransactionContext(final TransactionRegistry transactionRegistry, final Transaction transaction) {
        if (transactionRegistry == null) {
            throw new NullPointerException("transactionRegistry is null");
        }
        if (transaction == null) {
            throw new NullPointerException("transaction is null");
        }
        this.transactionRegistry = transactionRegistry;
        this.transactionRef = new WeakReference((T)transaction);
    }
    
    public Connection getSharedConnection() {
        return this.sharedConnection;
    }
    
    public void setSharedConnection(final Connection sharedConnection) throws SQLException {
        if (this.sharedConnection != null) {
            throw new IllegalStateException("A shared connection is alredy set");
        }
        final Transaction transaction = this.getTransaction();
        try {
            final XAResource xaResource = this.transactionRegistry.getXAResource(sharedConnection);
            transaction.enlistResource(xaResource);
        }
        catch (RollbackException e2) {}
        catch (SystemException e) {
            throw (SQLException)new SQLException("Unable to enlist connection the transaction").initCause(e);
        }
        this.sharedConnection = sharedConnection;
    }
    
    public void addTransactionContextListener(final TransactionContextListener listener) throws SQLException {
        try {
            this.getTransaction().registerSynchronization(new Synchronization() {
                @Override
                public void beforeCompletion() {
                }
                
                @Override
                public void afterCompletion(final int status) {
                    listener.afterCompletion(TransactionContext.this, status == 3);
                }
            });
        }
        catch (RollbackException e2) {}
        catch (Exception e) {
            throw (SQLException)new SQLException("Unable to register transaction context listener").initCause(e);
        }
    }
    
    public boolean isActive() throws SQLException {
        try {
            final Transaction transaction = (Transaction)this.transactionRef.get();
            if (transaction == null) {
                return false;
            }
            final int status = transaction.getStatus();
            return status == 0 || status == 1;
        }
        catch (SystemException e) {
            throw (SQLException)new SQLException("Unable to get transaction status").initCause(e);
        }
    }
    
    private Transaction getTransaction() throws SQLException {
        final Transaction transaction = (Transaction)this.transactionRef.get();
        if (transaction == null) {
            throw new SQLException("Unable to enlist connection because the transaction has been garbage collected");
        }
        return transaction;
    }
}
