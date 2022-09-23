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
import javax.sql.ConnectionPoolDataSource;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericObjectPool;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.naming.NamingException;
import org.datanucleus.store.rdbms.datasource.dbcp.SQLNestedException;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class PerUserPoolDataSource extends InstanceKeyDataSource
{
    private static final long serialVersionUID = -3104731034410444060L;
    private int defaultMaxActive;
    private int defaultMaxIdle;
    private int defaultMaxWait;
    Map perUserDefaultAutoCommit;
    Map perUserDefaultTransactionIsolation;
    Map perUserMaxActive;
    Map perUserMaxIdle;
    Map perUserMaxWait;
    Map perUserDefaultReadOnly;
    private transient Map managers;
    
    public PerUserPoolDataSource() {
        this.defaultMaxActive = 8;
        this.defaultMaxIdle = 8;
        this.defaultMaxWait = (int)Math.min(2147483647L, -1L);
        this.perUserDefaultAutoCommit = null;
        this.perUserDefaultTransactionIsolation = null;
        this.perUserMaxActive = null;
        this.perUserMaxIdle = null;
        this.perUserMaxWait = null;
        this.perUserDefaultReadOnly = null;
        this.managers = new HashMap();
    }
    
    @Override
    public void close() {
        final Iterator poolIter = this.managers.values().iterator();
        while (poolIter.hasNext()) {
            try {
                poolIter.next().getPool().close();
            }
            catch (Exception closePoolException) {}
        }
        InstanceKeyObjectFactory.removeInstance(this.instanceKey);
    }
    
    public int getDefaultMaxActive() {
        return this.defaultMaxActive;
    }
    
    public void setDefaultMaxActive(final int maxActive) {
        this.assertInitializationAllowed();
        this.defaultMaxActive = maxActive;
    }
    
    public int getDefaultMaxIdle() {
        return this.defaultMaxIdle;
    }
    
    public void setDefaultMaxIdle(final int defaultMaxIdle) {
        this.assertInitializationAllowed();
        this.defaultMaxIdle = defaultMaxIdle;
    }
    
    public int getDefaultMaxWait() {
        return this.defaultMaxWait;
    }
    
    public void setDefaultMaxWait(final int defaultMaxWait) {
        this.assertInitializationAllowed();
        this.defaultMaxWait = defaultMaxWait;
    }
    
    public Boolean getPerUserDefaultAutoCommit(final String key) {
        Boolean value = null;
        if (this.perUserDefaultAutoCommit != null) {
            value = this.perUserDefaultAutoCommit.get(key);
        }
        return value;
    }
    
    public void setPerUserDefaultAutoCommit(final String username, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultAutoCommit == null) {
            this.perUserDefaultAutoCommit = new HashMap();
        }
        this.perUserDefaultAutoCommit.put(username, value);
    }
    
    public Integer getPerUserDefaultTransactionIsolation(final String username) {
        Integer value = null;
        if (this.perUserDefaultTransactionIsolation != null) {
            value = this.perUserDefaultTransactionIsolation.get(username);
        }
        return value;
    }
    
    public void setPerUserDefaultTransactionIsolation(final String username, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultTransactionIsolation == null) {
            this.perUserDefaultTransactionIsolation = new HashMap();
        }
        this.perUserDefaultTransactionIsolation.put(username, value);
    }
    
    public Integer getPerUserMaxActive(final String username) {
        Integer value = null;
        if (this.perUserMaxActive != null) {
            value = this.perUserMaxActive.get(username);
        }
        return value;
    }
    
    public void setPerUserMaxActive(final String username, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxActive == null) {
            this.perUserMaxActive = new HashMap();
        }
        this.perUserMaxActive.put(username, value);
    }
    
    public Integer getPerUserMaxIdle(final String username) {
        Integer value = null;
        if (this.perUserMaxIdle != null) {
            value = this.perUserMaxIdle.get(username);
        }
        return value;
    }
    
    public void setPerUserMaxIdle(final String username, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxIdle == null) {
            this.perUserMaxIdle = new HashMap();
        }
        this.perUserMaxIdle.put(username, value);
    }
    
    public Integer getPerUserMaxWait(final String username) {
        Integer value = null;
        if (this.perUserMaxWait != null) {
            value = this.perUserMaxWait.get(username);
        }
        return value;
    }
    
    public void setPerUserMaxWait(final String username, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxWait == null) {
            this.perUserMaxWait = new HashMap();
        }
        this.perUserMaxWait.put(username, value);
    }
    
    public Boolean getPerUserDefaultReadOnly(final String username) {
        Boolean value = null;
        if (this.perUserDefaultReadOnly != null) {
            value = this.perUserDefaultReadOnly.get(username);
        }
        return value;
    }
    
    public void setPerUserDefaultReadOnly(final String username, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultReadOnly == null) {
            this.perUserDefaultReadOnly = new HashMap();
        }
        this.perUserDefaultReadOnly.put(username, value);
    }
    
    public int getNumActive() {
        return this.getNumActive(null, null);
    }
    
    public int getNumActive(final String username, final String password) {
        final ObjectPool pool = this.getPool(this.getPoolKey(username, password));
        return (pool == null) ? 0 : pool.getNumActive();
    }
    
    public int getNumIdle() {
        return this.getNumIdle(null, null);
    }
    
    public int getNumIdle(final String username, final String password) {
        final ObjectPool pool = this.getPool(this.getPoolKey(username, password));
        return (pool == null) ? 0 : pool.getNumIdle();
    }
    
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(final String username, final String password) throws SQLException {
        final PoolKey key = this.getPoolKey(username, password);
        PooledConnectionManager manager;
        ObjectPool pool;
        synchronized (this) {
            manager = this.managers.get(key);
            if (manager == null) {
                try {
                    this.registerPool(username, password);
                    manager = this.managers.get(key);
                }
                catch (NamingException e) {
                    throw new SQLNestedException("RegisterPool failed", e);
                }
            }
            pool = ((CPDSConnectionFactory)manager).getPool();
        }
        PooledConnectionAndInfo info = null;
        try {
            info = (PooledConnectionAndInfo)pool.borrowObject();
        }
        catch (NoSuchElementException ex) {
            throw new SQLNestedException("Could not retrieve connection info from pool", ex);
        }
        catch (Exception e2) {
            try {
                this.testCPDS(username, password);
            }
            catch (Exception ex2) {
                throw (SQLException)new SQLException("Could not retrieve connection info from pool").initCause(ex2);
            }
            manager.closePool(username);
            synchronized (this) {
                this.managers.remove(key);
            }
            try {
                this.registerPool(username, password);
                pool = this.getPool(key);
            }
            catch (NamingException ne) {
                throw new SQLNestedException("RegisterPool failed", ne);
            }
            try {
                info = (PooledConnectionAndInfo)pool.borrowObject();
            }
            catch (Exception ex2) {
                throw (SQLException)new SQLException("Could not retrieve connection info from pool").initCause(ex2);
            }
        }
        return info;
    }
    
    @Override
    protected void setupDefaults(final Connection con, final String username) throws SQLException {
        boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (username != null) {
            final Boolean userMax = this.getPerUserDefaultAutoCommit(username);
            if (userMax != null) {
                defaultAutoCommit = userMax;
            }
        }
        boolean defaultReadOnly = this.isDefaultReadOnly();
        if (username != null) {
            final Boolean userMax2 = this.getPerUserDefaultReadOnly(username);
            if (userMax2 != null) {
                defaultReadOnly = userMax2;
            }
        }
        int defaultTransactionIsolation = this.getDefaultTransactionIsolation();
        if (username != null) {
            final Integer userMax3 = this.getPerUserDefaultTransactionIsolation(username);
            if (userMax3 != null) {
                defaultTransactionIsolation = userMax3;
            }
        }
        if (con.getAutoCommit() != defaultAutoCommit) {
            con.setAutoCommit(defaultAutoCommit);
        }
        if (defaultTransactionIsolation != -1) {
            con.setTransactionIsolation(defaultTransactionIsolation);
        }
        if (con.isReadOnly() != defaultReadOnly) {
            con.setReadOnly(defaultReadOnly);
        }
    }
    
    @Override
    protected PooledConnectionManager getConnectionManager(final UserPassKey upkey) {
        return this.managers.get(this.getPoolKey(upkey.getUsername(), upkey.getPassword()));
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final Reference ref = new Reference(this.getClass().getName(), PerUserPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.instanceKey));
        return ref;
    }
    
    private PoolKey getPoolKey(final String username, final String password) {
        return new PoolKey(this.getDataSourceName(), username);
    }
    
    private synchronized void registerPool(final String username, final String password) throws NamingException, SQLException {
        final ConnectionPoolDataSource cpds = this.testCPDS(username, password);
        Integer userMax = this.getPerUserMaxActive(username);
        final int maxActive = (userMax == null) ? this.getDefaultMaxActive() : userMax;
        userMax = this.getPerUserMaxIdle(username);
        final int maxIdle = (userMax == null) ? this.getDefaultMaxIdle() : userMax;
        userMax = this.getPerUserMaxWait(username);
        final int maxWait = (userMax == null) ? this.getDefaultMaxWait() : userMax;
        final GenericObjectPool pool = new GenericObjectPool(null);
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxIdle);
        pool.setMaxWait(maxWait);
        pool.setWhenExhaustedAction(this.whenExhaustedAction(maxActive, maxWait));
        pool.setTestOnBorrow(this.getTestOnBorrow());
        pool.setTestOnReturn(this.getTestOnReturn());
        pool.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        pool.setNumTestsPerEvictionRun(this.getNumTestsPerEvictionRun());
        pool.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        pool.setTestWhileIdle(this.getTestWhileIdle());
        final CPDSConnectionFactory factory = new CPDSConnectionFactory(cpds, pool, this.getValidationQuery(), this.isRollbackAfterValidation(), username, password);
        final Object old = this.managers.put(this.getPoolKey(username, password), factory);
        if (old != null) {
            throw new IllegalStateException("Pool already contains an entry for this user/password: " + username);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            final PerUserPoolDataSource oldDS = (PerUserPoolDataSource)new PerUserPoolDataSourceFactory().getObjectInstance(this.getReference(), null, null, null);
            this.managers = oldDS.managers;
        }
        catch (NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }
    
    private GenericObjectPool getPool(final PoolKey key) {
        final CPDSConnectionFactory mgr = this.managers.get(key);
        return (mgr == null) ? null : ((GenericObjectPool)mgr.getPool());
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }
}
