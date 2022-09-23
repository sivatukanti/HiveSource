// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import java.sql.SQLException;
import java.sql.Connection;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.dbcp.DelegatingConnection;

public class ManagedConnection extends DelegatingConnection
{
    private final ObjectPool pool;
    private final TransactionRegistry transactionRegistry;
    private final boolean accessToUnderlyingConnectionAllowed;
    private TransactionContext transactionContext;
    private boolean isSharedConnection;
    
    public ManagedConnection(final ObjectPool pool, final TransactionRegistry transactionRegistry, final boolean accessToUnderlyingConnectionAllowed) throws SQLException {
        super((Connection)null);
        this.pool = pool;
        this.transactionRegistry = transactionRegistry;
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
        this.updateTransactionStatus();
    }
    
    @Override
    protected void checkOpen() throws SQLException {
        super.checkOpen();
        this.updateTransactionStatus();
    }
    
    private void updateTransactionStatus() throws SQLException {
        if (this.transactionContext != null) {
            if (this.transactionContext.isActive()) {
                if (this.transactionContext != this.transactionRegistry.getActiveTransactionContext()) {
                    throw new SQLException("Connection can not be used while enlisted in another transaction");
                }
                return;
            }
            else {
                this.transactionComplete();
            }
        }
        this.transactionContext = this.transactionRegistry.getActiveTransactionContext();
        if (this.transactionContext != null && this.transactionContext.getSharedConnection() != null) {
            final Connection connection = this.getDelegateInternal();
            this.setDelegate(null);
            if (connection != null) {
                try {
                    this.pool.returnObject(connection);
                }
                catch (Exception ignored) {
                    try {
                        this.pool.invalidateObject(connection);
                    }
                    catch (Exception ex) {}
                }
            }
            this.transactionContext.addTransactionContextListener(new CompletionListener());
            this.setDelegate(this.transactionContext.getSharedConnection());
            this.isSharedConnection = true;
        }
        else {
            if (this.getDelegateInternal() == null) {
                try {
                    final Connection connection = (Connection)this.pool.borrowObject();
                    this.setDelegate(connection);
                }
                catch (Exception e) {
                    throw (SQLException)new SQLException("Unable to acquire a new connection from the pool").initCause(e);
                }
            }
            if (this.transactionContext != null) {
                this.transactionContext.addTransactionContextListener(new CompletionListener());
                try {
                    this.transactionContext.setSharedConnection(this.getDelegateInternal());
                }
                catch (SQLException e2) {
                    this.transactionContext = null;
                    throw e2;
                }
            }
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (!this._closed) {
            try {
                if (this.transactionContext == null) {
                    this.getDelegateInternal().close();
                }
            }
            finally {
                this._closed = true;
            }
        }
    }
    
    protected void transactionComplete() {
        this.transactionContext = null;
        if (this.isSharedConnection) {
            this.setDelegate(null);
            this.isSharedConnection = false;
        }
        final Connection delegate = this.getDelegateInternal();
        if (this._closed && delegate != null) {
            try {
                this.setDelegate(null);
                if (!delegate.isClosed()) {
                    delegate.close();
                }
            }
            catch (SQLException ignored) {}
            finally {
                this._closed = true;
            }
        }
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Auto-commit can not be set while enrolled in a transaction");
        }
        super.setAutoCommit(autoCommit);
    }
    
    @Override
    public void commit() throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.commit();
    }
    
    @Override
    public void rollback() throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.rollback();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Read-only can not be set while enrolled in a transaction");
        }
        super.setReadOnly(readOnly);
    }
    
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    @Override
    public Connection getDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return this.getDelegateInternal();
        }
        return null;
    }
    
    @Override
    public Connection getInnermostDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return super.getInnermostDelegateInternal();
        }
        return null;
    }
    
    protected class CompletionListener implements TransactionContextListener
    {
        @Override
        public void afterCompletion(final TransactionContext completedContext, final boolean commited) {
            if (completedContext == ManagedConnection.this.transactionContext) {
                ManagedConnection.this.transactionComplete();
            }
        }
    }
}
