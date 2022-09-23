// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import javax.transaction.Transaction;
import javax.transaction.SystemException;
import java.sql.SQLException;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.util.WeakHashMap;
import java.util.Map;
import javax.transaction.TransactionManager;

public class TransactionRegistry
{
    private final TransactionManager transactionManager;
    private final Map caches;
    private final Map xaResources;
    
    public TransactionRegistry(final TransactionManager transactionManager) {
        this.caches = new WeakHashMap();
        this.xaResources = new WeakHashMap();
        this.transactionManager = transactionManager;
    }
    
    public synchronized void registerConnection(final Connection connection, final XAResource xaResource) {
        if (connection == null) {
            throw new NullPointerException("connection is null");
        }
        if (xaResource == null) {
            throw new NullPointerException("xaResource is null");
        }
        this.xaResources.put(connection, xaResource);
    }
    
    public synchronized XAResource getXAResource(final Connection connection) throws SQLException {
        if (connection == null) {
            throw new NullPointerException("connection is null");
        }
        final XAResource xaResource = this.xaResources.get(connection);
        if (xaResource == null) {
            throw new SQLException("Connection does not have a registered XAResource " + connection);
        }
        return xaResource;
    }
    
    public TransactionContext getActiveTransactionContext() throws SQLException {
        Transaction transaction = null;
        try {
            transaction = this.transactionManager.getTransaction();
            if (transaction == null) {
                return null;
            }
            final int status = transaction.getStatus();
            if (status != 0 && status != 1) {
                return null;
            }
        }
        catch (SystemException e) {
            throw (SQLException)new SQLException("Unable to determine current transaction ").initCause(e);
        }
        synchronized (this) {
            TransactionContext cache = this.caches.get(transaction);
            if (cache == null) {
                cache = new TransactionContext(this, transaction);
                this.caches.put(transaction, cache);
            }
            return cache;
        }
    }
    
    public synchronized void unregisterConnection(final Connection connection) {
        this.xaResources.remove(connection);
    }
}
