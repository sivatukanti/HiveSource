// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.managed;

import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolingConnection;
import org.datanucleus.store.rdbms.datasource.dbcp.AbandonedConfig;
import java.util.Collection;
import org.datanucleus.store.rdbms.datasource.dbcp.ConnectionFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPoolFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolableConnectionFactory;

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
