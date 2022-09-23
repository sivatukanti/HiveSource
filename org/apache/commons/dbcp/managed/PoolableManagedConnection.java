// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import java.sql.SQLException;
import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.pool.ObjectPool;
import java.sql.Connection;
import org.apache.commons.dbcp.PoolableConnection;

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
