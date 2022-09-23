// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.ResultSet;
import java.util.Iterator;
import java.sql.Statement;
import java.sql.SQLException;
import org.apache.commons.pool.KeyedObjectPool;
import java.sql.Connection;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import java.util.Collection;
import org.apache.commons.pool.PoolableObjectFactory;

public class PoolableConnectionFactory implements PoolableObjectFactory
{
    protected volatile ConnectionFactory _connFactory;
    protected volatile String _validationQuery;
    protected volatile int _validationQueryTimeout;
    protected Collection _connectionInitSqls;
    protected volatile ObjectPool _pool;
    protected volatile KeyedObjectPoolFactory _stmtPoolFactory;
    protected Boolean _defaultReadOnly;
    protected boolean _defaultAutoCommit;
    protected int _defaultTransactionIsolation;
    protected String _defaultCatalog;
    protected AbandonedConfig _config;
    static final int UNKNOWN_TRANSACTIONISOLATION = -1;
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final Collection connectionInitSqls, final boolean defaultReadOnly, final boolean defaultAutoCommit) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final boolean defaultReadOnly, final boolean defaultAutoCommit) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final Collection connectionInitSqls, final boolean defaultReadOnly, final boolean defaultAutoCommit) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final Collection connectionInitSqls, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final Collection connectionInitSqls, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        (this._pool = pool).setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
        this._defaultCatalog = defaultCatalog;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final Boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._defaultReadOnly = defaultReadOnly;
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
        this._defaultCatalog = defaultCatalog;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final Collection connectionInitSqls, final Boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = defaultReadOnly;
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
        this._defaultCatalog = defaultCatalog;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final Boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._defaultReadOnly = defaultReadOnly;
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
        this._defaultCatalog = defaultCatalog;
    }
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectPool pool, final KeyedObjectPoolFactory stmtPoolFactory, final String validationQuery, final int validationQueryTimeout, final Collection connectionInitSqls, final Boolean defaultReadOnly, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final String defaultCatalog, final AbandonedConfig config) {
        this._connFactory = null;
        this._validationQuery = null;
        this._validationQueryTimeout = -1;
        this._connectionInitSqls = null;
        this._pool = null;
        this._stmtPoolFactory = null;
        this._defaultReadOnly = null;
        this._defaultAutoCommit = true;
        this._defaultTransactionIsolation = -1;
        this._config = null;
        this._connFactory = connFactory;
        this._pool = pool;
        this._config = config;
        this._pool.setFactory(this);
        this._stmtPoolFactory = stmtPoolFactory;
        this._validationQuery = validationQuery;
        this._validationQueryTimeout = validationQueryTimeout;
        this._connectionInitSqls = connectionInitSqls;
        this._defaultReadOnly = defaultReadOnly;
        this._defaultAutoCommit = defaultAutoCommit;
        this._defaultTransactionIsolation = defaultTransactionIsolation;
        this._defaultCatalog = defaultCatalog;
    }
    
    public void setConnectionFactory(final ConnectionFactory connFactory) {
        this._connFactory = connFactory;
    }
    
    public void setValidationQuery(final String validationQuery) {
        this._validationQuery = validationQuery;
    }
    
    public void setValidationQueryTimeout(final int timeout) {
        this._validationQueryTimeout = timeout;
    }
    
    public synchronized void setConnectionInitSql(final Collection connectionInitSqls) {
        this._connectionInitSqls = connectionInitSqls;
    }
    
    public synchronized void setPool(final ObjectPool pool) {
        if (null != this._pool && pool != this._pool) {
            try {
                this._pool.close();
            }
            catch (Exception ex) {}
        }
        this._pool = pool;
    }
    
    public synchronized ObjectPool getPool() {
        return this._pool;
    }
    
    public void setStatementPoolFactory(final KeyedObjectPoolFactory stmtPoolFactory) {
        this._stmtPoolFactory = stmtPoolFactory;
    }
    
    public void setDefaultReadOnly(final boolean defaultReadOnly) {
        this._defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setDefaultAutoCommit(final boolean defaultAutoCommit) {
        this._defaultAutoCommit = defaultAutoCommit;
    }
    
    public void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        this._defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public void setDefaultCatalog(final String defaultCatalog) {
        this._defaultCatalog = defaultCatalog;
    }
    
    @Override
    public Object makeObject() throws Exception {
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
        return new PoolableConnection(conn, this._pool, this._config);
    }
    
    protected void initializeConnection(final Connection conn) throws SQLException {
        final Collection sqls = this._connectionInitSqls;
        if (conn.isClosed()) {
            throw new SQLException("initializeConnection: connection closed");
        }
        if (null != sqls) {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                for (final Object o : sqls) {
                    if (o == null) {
                        throw new NullPointerException("null connectionInitSqls element");
                    }
                    final String sql = o.toString();
                    stmt.execute(sql);
                }
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (Exception ex) {}
                }
            }
        }
    }
    
    @Override
    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof PoolableConnection) {
            ((PoolableConnection)obj).reallyClose();
        }
    }
    
    @Override
    public boolean validateObject(final Object obj) {
        if (obj instanceof Connection) {
            try {
                this.validateConnection((Connection)obj);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    public void validateConnection(final Connection conn) throws SQLException {
        final String query = this._validationQuery;
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }
        if (null != query) {
            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = conn.createStatement();
                if (this._validationQueryTimeout > 0) {
                    stmt.setQueryTimeout(this._validationQueryTimeout);
                }
                rset = stmt.executeQuery(query);
                if (!rset.next()) {
                    throw new SQLException("validationQuery didn't return a row");
                }
            }
            finally {
                if (rset != null) {
                    try {
                        rset.close();
                    }
                    catch (Exception ex) {}
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (Exception ex2) {}
                }
            }
        }
    }
    
    @Override
    public void passivateObject(final Object obj) throws Exception {
        if (obj instanceof Connection) {
            final Connection conn = (Connection)obj;
            if (!conn.getAutoCommit() && !conn.isReadOnly()) {
                conn.rollback();
            }
            conn.clearWarnings();
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }
        }
        if (obj instanceof DelegatingConnection) {
            ((DelegatingConnection)obj).passivate();
        }
    }
    
    @Override
    public void activateObject(final Object obj) throws Exception {
        if (obj instanceof DelegatingConnection) {
            ((DelegatingConnection)obj).activate();
        }
        if (obj instanceof Connection) {
            final Connection conn = (Connection)obj;
            if (conn.getAutoCommit() != this._defaultAutoCommit) {
                conn.setAutoCommit(this._defaultAutoCommit);
            }
            if (this._defaultTransactionIsolation != -1 && conn.getTransactionIsolation() != this._defaultTransactionIsolation) {
                conn.setTransactionIsolation(this._defaultTransactionIsolation);
            }
            if (this._defaultReadOnly != null && conn.isReadOnly() != this._defaultReadOnly) {
                conn.setReadOnly(this._defaultReadOnly);
            }
            if (this._defaultCatalog != null && !this._defaultCatalog.equals(conn.getCatalog())) {
                conn.setCatalog(this._defaultCatalog);
            }
        }
    }
}
