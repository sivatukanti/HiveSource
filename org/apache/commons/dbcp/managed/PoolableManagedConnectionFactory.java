// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import org.apache.commons.pool.KeyedObjectPool;
import java.sql.Connection;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.dbcp.PoolingConnection;
import org.apache.commons.dbcp.AbandonedConfig;
import java.util.Collection;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.dbcp.PoolableConnectionFactory;

public class PoolableManagedConnectionFactory extends PoolableConnectionFactory
{
    private final TransactionRegistry transactionRegistry;
    
    public PoolableManagedConnectionFactory(final XAConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit) {
        super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit);
        this.transactionRegistry = connFactory.getTransactionRegistry();
    }
    
    public PoolableManagedConnectionFactory(final XAConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final Collection connectionInitSqls, final Boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        super(connFactory, pool, stmtPoolFactory, validationQuery, validationQueryTimeout, connectionInitSqls, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation, defaultCatalog, config);
        this.transactionRegistry = connFactory.getTransactionRegistry();
    }
    
    @Override
    public synchronized Object makeObject() throws Exception {
        Connection conn = this._connFactory.createConnection();
        if (conn == null) {
            throw new IllegalStateException("Connection factory returned null from createConnection");
        }
        this.initializeConnection(conn);
        if (null != this._stmtPoolFactory) {
            final KeyedObjectPool stmtpool = this._stmtPoolFactory.createPool();
            conn = new PoolingConnection(conn, stmtpool);
            stmtpool.setFactory((KeyedPoolableObjectFactory)conn);
        }
        return new PoolableManagedConnection(this.transactionRegistry, conn, this._pool, this._config);
    }
}
