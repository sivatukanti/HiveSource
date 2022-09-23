// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.datasources;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.io.ObjectInputStream;
import java.sql.Connection;
import javax.sql.ConnectionPoolDataSource;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericKeyedObjectPool;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import java.sql.SQLException;
import javax.naming.NamingException;
import org.datanucleus.store.rdbms.datasource.dbcp.SQLNestedException;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;

public class SharedPoolDataSource extends InstanceKeyDataSource
{
    private static final long serialVersionUID = -8132305535403690372L;
    private int maxActive;
    private int maxIdle;
    private int maxWait;
    private transient KeyedObjectPool pool;
    private transient KeyedCPDSConnectionFactory factory;
    
    public SharedPoolDataSource() {
        this.maxActive = 8;
        this.maxIdle = 8;
        this.maxWait = (int)Math.min(2147483647L, -1L);
        this.pool = null;
        this.factory = null;
    }
    
    @Override
    public void close() throws Exception {
        if (this.pool != null) {
            this.pool.close();
        }
        InstanceKeyObjectFactory.removeInstance(this.instanceKey);
    }
    
    public int getMaxActive() {
        return this.maxActive;
    }
    
    public void setMaxActive(final int maxActive) {
        this.assertInitializationAllowed();
        this.maxActive = maxActive;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.assertInitializationAllowed();
        this.maxIdle = maxIdle;
    }
    
    public int getMaxWait() {
        return this.maxWait;
    }
    
    public void setMaxWait(final int maxWait) {
        this.assertInitializationAllowed();
        this.maxWait = maxWait;
    }
    
    public int getNumActive() {
        return (this.pool == null) ? 0 : this.pool.getNumActive();
    }
    
    public int getNumIdle() {
        return (this.pool == null) ? 0 : this.pool.getNumIdle();
    }
    
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(final String username, final String password) throws SQLException {
        synchronized (this) {
            if (this.pool == null) {
                try {
                    this.registerPool(username, password);
                }
                catch (NamingException e) {
                    throw new SQLNestedException("RegisterPool failed", e);
                }
            }
        }
        PooledConnectionAndInfo info = null;
        final UserPassKey key = new UserPassKey(username, password);
        try {
            info = (PooledConnectionAndInfo)this.pool.borrowObject(key);
        }
        catch (Exception e2) {
            throw new SQLNestedException("Could not retrieve connection info from pool", e2);
        }
        return info;
    }
    
    @Override
    protected PooledConnectionManager getConnectionManager(final UserPassKey upkey) {
        return this.factory;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final Reference ref = new Reference(this.getClass().getName(), SharedPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.instanceKey));
        return ref;
    }
    
    private void registerPool(final String username, final String password) throws NamingException, SQLException {
        final ConnectionPoolDataSource cpds = this.testCPDS(username, password);
        final GenericKeyedObjectPool tmpPool = new GenericKeyedObjectPool(null);
        tmpPool.setMaxActive(this.getMaxActive());
        tmpPool.setMaxIdle(this.getMaxIdle());
        tmpPool.setMaxWait(this.getMaxWait());
        tmpPool.setWhenExhaustedAction(this.whenExhaustedAction(this.maxActive, this.maxWait));
        tmpPool.setTestOnBorrow(this.getTestOnBorrow());
        tmpPool.setTestOnReturn(this.getTestOnReturn());
        tmpPool.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        tmpPool.setNumTestsPerEvictionRun(this.getNumTestsPerEvictionRun());
        tmpPool.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        tmpPool.setTestWhileIdle(this.getTestWhileIdle());
        this.pool = tmpPool;
        this.factory = new KeyedCPDSConnectionFactory(cpds, this.pool, this.getValidationQuery(), this.isRollbackAfterValidation());
    }
    
    @Override
    protected void setupDefaults(final Connection con, final String username) throws SQLException {
        final boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (con.getAutoCommit() != defaultAutoCommit) {
            con.setAutoCommit(defaultAutoCommit);
        }
        final int defaultTransactionIsolation = this.getDefaultTransactionIsolation();
        if (defaultTransactionIsolation != -1) {
            con.setTransactionIsolation(defaultTransactionIsolation);
        }
        final boolean defaultReadOnly = this.isDefaultReadOnly();
        if (con.isReadOnly() != defaultReadOnly) {
            con.setReadOnly(defaultReadOnly);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            final SharedPoolDataSource oldDS = (SharedPoolDataSource)new SharedPoolDataSourceFactory().getObjectInstance(this.getReference(), null, null, null);
            this.pool = oldDS.pool;
        }
        catch (NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }
}
