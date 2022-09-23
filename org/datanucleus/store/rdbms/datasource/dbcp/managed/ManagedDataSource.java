// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolingDataSource;

public class ManagedDataSource extends PoolingDataSource
{
    private TransactionRegistry transactionRegistry;
    
    public ManagedDataSource() {
    }
    
    public ManagedDataSource(final ObjectPool pool, final TransactionRegistry transactionRegistry) {
        super(pool);
        this.transactionRegistry = transactionRegistry;
    }
    
    public void setTransactionRegistry(final TransactionRegistry transactionRegistry) {
        if (this.transactionRegistry != null) {
            throw new IllegalStateException("TransactionRegistry already set");
        }
        if (transactionRegistry == null) {
            throw new NullPointerException("TransactionRegistry is null");
        }
        this.transactionRegistry = transactionRegistry;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (this._pool == null) {
            throw new IllegalStateException("Pool has not been set");
        }
        if (this.transactionRegistry == null) {
            throw new IllegalStateException("TransactionRegistry has not been set");
        }
        final Connection connection = new ManagedConnection(this._pool, this.transactionRegistry, this.isAccessToUnderlyingConnectionAllowed());
        return connection;
    }
}
