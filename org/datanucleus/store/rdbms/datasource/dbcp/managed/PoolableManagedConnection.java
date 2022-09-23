// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import java.sql.SQLException;
import org.datanucleus.store.rdbms.datasource.dbcp.AbandonedConfig;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolableConnection;

public class PoolableManagedConnection extends PoolableConnection
{
    private final TransactionRegistry transactionRegistry;
    
    public PoolableManagedConnection(final TransactionRegistry transactionRegistry, final Connection conn, final ObjectPool pool, final AbandonedConfig config) {
        super(conn, pool, config);
        this.transactionRegistry = transactionRegistry;
    }
    
    public PoolableManagedConnection(final TransactionRegistry transactionRegistry, final Connection conn, final ObjectPool pool) {
        super(conn, pool);
        this.transactionRegistry = transactionRegistry;
    }
    
    @Override
    public void reallyClose() throws SQLException {
        try {
            super.reallyClose();
        }
        finally {
            this.transactionRegistry.unregisterConnection(this);
        }
    }
}
