// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.ExecutionContext;
import java.util.Map;

public class TransactionManager
{
    private boolean containerManagedConnections;
    private Map<ExecutionContext, Transaction> txnForExecutionContext;
    
    public TransactionManager() {
        this.containerManagedConnections = false;
        this.txnForExecutionContext = new ConcurrentHashMap<ExecutionContext, Transaction>();
    }
    
    public void setContainerManagedConnections(final boolean flag) {
        this.containerManagedConnections = flag;
    }
    
    public void begin(final ExecutionContext ec) {
        if (this.txnForExecutionContext.get(ec) != null) {
            throw new NucleusTransactionException("Invalid state. Transaction has already started");
        }
        this.txnForExecutionContext.put(ec, new Transaction());
    }
    
    public void commit(final ExecutionContext ec) {
        final Transaction tx = this.txnForExecutionContext.get(ec);
        if (tx == null) {
            throw new NucleusTransactionException("Invalid state. Transaction does not exist");
        }
        try {
            if (!this.containerManagedConnections) {
                tx.commit();
            }
        }
        finally {
            this.txnForExecutionContext.remove(ec);
        }
    }
    
    public void rollback(final ExecutionContext ec) {
        final Transaction tx = this.txnForExecutionContext.get(ec);
        if (tx == null) {
            throw new NucleusTransactionException("Invalid state. Transaction does not exist");
        }
        try {
            if (!this.containerManagedConnections) {
                tx.rollback();
            }
        }
        finally {
            this.txnForExecutionContext.remove(ec);
        }
    }
    
    public Transaction getTransaction(final ExecutionContext ec) {
        if (ec == null) {
            return null;
        }
        return this.txnForExecutionContext.get(ec);
    }
    
    public void setRollbackOnly(final ExecutionContext ec) {
        final Transaction tx = this.txnForExecutionContext.get(ec);
        if (tx == null) {
            throw new NucleusTransactionException("Invalid state. Transaction does not exist");
        }
        tx.setRollbackOnly();
    }
    
    public void setTransactionTimeout(final ExecutionContext ec, final int millis) {
        throw new UnsupportedOperationException();
    }
    
    public void resume(final ExecutionContext ec, final Transaction tx) {
        throw new UnsupportedOperationException();
    }
    
    public Transaction suspend(final ExecutionContext ec) {
        throw new UnsupportedOperationException();
    }
}
