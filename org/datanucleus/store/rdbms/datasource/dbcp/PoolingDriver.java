// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.util.Map;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.sql.DriverPropertyInfo;
import java.util.NoSuchElementException;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.datanucleus.store.rdbms.datasource.dbcp.jocl.JOCLContentHandler;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import java.util.HashMap;
import java.sql.Driver;

public class PoolingDriver implements Driver
{
    protected static final HashMap _pools;
    private static boolean accessToUnderlyingConnectionAllowed;
    protected static final String URL_PREFIX = "jdbc:apache:commons:dbcp:";
    protected static final int URL_PREFIX_LEN;
    protected static final int MAJOR_VERSION = 1;
    protected static final int MINOR_VERSION = 0;
    
    public static synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return PoolingDriver.accessToUnderlyingConnectionAllowed;
    }
    
    public static synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        PoolingDriver.accessToUnderlyingConnectionAllowed = allow;
    }
    
    @Deprecated
    public synchronized ObjectPool getPool(final String name) {
        try {
            return this.getConnectionPool(name);
        }
        catch (Exception e) {
            throw new DbcpException(e);
        }
    }
    
    public synchronized ObjectPool getConnectionPool(final String name) throws SQLException {
        ObjectPool pool = PoolingDriver._pools.get(name);
        if (null == pool) {
            InputStream in = this.getClass().getResourceAsStream(String.valueOf(name) + ".jocl");
            if (in == null) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.valueOf(name) + ".jocl");
            }
            if (null == in) {
                throw new SQLException("Configuration file not found");
            }
            JOCLContentHandler jocl = null;
            try {
                jocl = JOCLContentHandler.parse(in);
            }
            catch (SAXException e) {
                throw (SQLException)new SQLException("Could not parse configuration file").initCause(e);
            }
            catch (IOException e2) {
                throw (SQLException)new SQLException("Could not load configuration file").initCause(e2);
            }
            if (jocl.getType(0).equals(String.class)) {
                pool = this.getPool((String)jocl.getValue(0));
                if (null != pool) {
                    this.registerPool(name, pool);
                }
            }
            else {
                pool = ((PoolableConnectionFactory)jocl.getValue(0)).getPool();
                if (null != pool) {
                    this.registerPool(name, pool);
                }
            }
        }
        return pool;
    }
    
    public synchronized void registerPool(final String name, final ObjectPool pool) {
        PoolingDriver._pools.put(name, pool);
    }
    
    public synchronized void closePool(final String name) throws SQLException {
        final ObjectPool pool = PoolingDriver._pools.get(name);
        if (pool != null) {
            PoolingDriver._pools.remove(name);
            try {
                pool.close();
            }
            catch (Exception e) {
                throw (SQLException)new SQLException("Error closing pool " + name).initCause(e);
            }
        }
    }
    
    public synchronized String[] getPoolNames() {
        final Set names = PoolingDriver._pools.keySet();
        return names.toArray(new String[names.size()]);
    }
    
    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        try {
            return url.startsWith("jdbc:apache:commons:dbcp:");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (this.acceptsURL(url)) {
            final ObjectPool pool = this.getConnectionPool(url.substring(PoolingDriver.URL_PREFIX_LEN));
            if (null == pool) {
                throw new SQLException("No pool found for " + url + ".");
            }
            try {
                Connection conn = (Connection)pool.borrowObject();
                if (conn != null) {
                    conn = new PoolGuardConnectionWrapper(pool, conn);
                }
                return conn;
            }
            catch (SQLException e) {
                throw e;
            }
            catch (NoSuchElementException e2) {
                throw (SQLException)new SQLException("Cannot get a connection, pool error: " + e2.getMessage()).initCause(e2);
            }
            catch (RuntimeException e3) {
                throw e3;
            }
            catch (Exception e4) {
                throw (SQLException)new SQLException("Cannot get a connection, general error: " + e4.getMessage()).initCause(e4);
            }
        }
        return null;
    }
    
    public void invalidateConnection(final Connection conn) throws SQLException {
        if (conn instanceof PoolGuardConnectionWrapper) {
            final PoolGuardConnectionWrapper pgconn = (PoolGuardConnectionWrapper)conn;
            final ObjectPool pool = pgconn.pool;
            final Connection delegate = pgconn.delegate;
            try {
                pool.invalidateObject(delegate);
            }
            catch (Exception ex) {}
            pgconn.delegate = null;
            return;
        }
        throw new SQLException("Invalid connection class");
    }
    
    @Override
    public int getMajorVersion() {
        return 1;
    }
    
    @Override
    public int getMinorVersion() {
        return 0;
    }
    
    @Override
    public boolean jdbcCompliant() {
        return true;
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) {
        return new DriverPropertyInfo[0];
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }
    
    static {
        try {
            DriverManager.registerDriver(new PoolingDriver());
        }
        catch (Exception ex) {}
        _pools = new HashMap();
        PoolingDriver.accessToUnderlyingConnectionAllowed = false;
        URL_PREFIX_LEN = "jdbc:apache:commons:dbcp:".length();
    }
    
    private static class PoolGuardConnectionWrapper extends DelegatingConnection
    {
        private final ObjectPool pool;
        private Connection delegate;
        
        PoolGuardConnectionWrapper(final ObjectPool pool, final Connection delegate) {
            super(delegate);
            this.pool = pool;
            this.delegate = delegate;
        }
        
        @Override
        protected void checkOpen() throws SQLException {
            if (this.delegate == null) {
                throw new SQLException("Connection is closed.");
            }
        }
        
        @Override
        public void close() throws SQLException {
            if (this.delegate != null) {
                this.delegate.close();
                super.setDelegate(this.delegate = null);
            }
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return this.delegate == null || this.delegate.isClosed();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            this.checkOpen();
            this.delegate.clearWarnings();
        }
        
        @Override
        public void commit() throws SQLException {
            this.checkOpen();
            this.delegate.commit();
        }
        
        @Override
        public Statement createStatement() throws SQLException {
            this.checkOpen();
            return new DelegatingStatement(this, this.delegate.createStatement());
        }
        
        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
            this.checkOpen();
            return new DelegatingStatement(this, this.delegate.createStatement(resultSetType, resultSetConcurrency));
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this.delegate != null && this.delegate.equals(obj);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            this.checkOpen();
            return this.delegate.getAutoCommit();
        }
        
        @Override
        public String getCatalog() throws SQLException {
            this.checkOpen();
            return this.delegate.getCatalog();
        }
        
        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            this.checkOpen();
            return this.delegate.getMetaData();
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            this.checkOpen();
            return this.delegate.getTransactionIsolation();
        }
        
        @Override
        public Map getTypeMap() throws SQLException {
            this.checkOpen();
            return this.delegate.getTypeMap();
        }
        
        @Override
        public SQLWarning getWarnings() throws SQLException {
            this.checkOpen();
            return this.delegate.getWarnings();
        }
        
        @Override
        public int hashCode() {
            if (this.delegate == null) {
                return 0;
            }
            return this.delegate.hashCode();
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            this.checkOpen();
            return this.delegate.isReadOnly();
        }
        
        @Override
        public String nativeSQL(final String sql) throws SQLException {
            this.checkOpen();
            return this.delegate.nativeSQL(sql);
        }
        
        @Override
        public CallableStatement prepareCall(final String sql) throws SQLException {
            this.checkOpen();
            return new DelegatingCallableStatement(this, this.delegate.prepareCall(sql));
        }
        
        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
            this.checkOpen();
            return new DelegatingCallableStatement(this, this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
        
        @Override
        public void rollback() throws SQLException {
            this.checkOpen();
            this.delegate.rollback();
        }
        
        @Override
        public void setAutoCommit(final boolean autoCommit) throws SQLException {
            this.checkOpen();
            this.delegate.setAutoCommit(autoCommit);
        }
        
        @Override
        public void setCatalog(final String catalog) throws SQLException {
            this.checkOpen();
            this.delegate.setCatalog(catalog);
        }
        
        @Override
        public void setReadOnly(final boolean readOnly) throws SQLException {
            this.checkOpen();
            this.delegate.setReadOnly(readOnly);
        }
        
        @Override
        public void setTransactionIsolation(final int level) throws SQLException {
            this.checkOpen();
            this.delegate.setTransactionIsolation(level);
        }
        
        @Override
        public void setTypeMap(final Map map) throws SQLException {
            this.checkOpen();
            this.delegate.setTypeMap(map);
        }
        
        @Override
        public String toString() {
            if (this.delegate == null) {
                return "NULL";
            }
            return this.delegate.toString();
        }
        
        @Override
        public int getHoldability() throws SQLException {
            this.checkOpen();
            return this.delegate.getHoldability();
        }
        
        @Override
        public void setHoldability(final int holdability) throws SQLException {
            this.checkOpen();
            this.delegate.setHoldability(holdability);
        }
        
        @Override
        public Savepoint setSavepoint() throws SQLException {
            this.checkOpen();
            return this.delegate.setSavepoint();
        }
        
        @Override
        public Savepoint setSavepoint(final String name) throws SQLException {
            this.checkOpen();
            return this.delegate.setSavepoint(name);
        }
        
        @Override
        public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
            this.checkOpen();
            this.delegate.releaseSavepoint(savepoint);
        }
        
        @Override
        public void rollback(final Savepoint savepoint) throws SQLException {
            this.checkOpen();
            this.delegate.rollback(savepoint);
        }
        
        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            this.checkOpen();
            return new DelegatingStatement(this, this.delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        
        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            this.checkOpen();
            return new DelegatingCallableStatement(this, this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql, autoGeneratedKeys));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql, columnIndexes));
        }
        
        @Override
        public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
            this.checkOpen();
            return new DelegatingPreparedStatement(this, this.delegate.prepareStatement(sql, columnNames));
        }
        
        @Override
        public Connection getDelegate() {
            if (PoolingDriver.isAccessToUnderlyingConnectionAllowed()) {
                return super.getDelegate();
            }
            return null;
        }
        
        @Override
        public Connection getInnermostDelegate() {
            if (PoolingDriver.isAccessToUnderlyingConnectionAllowed()) {
                return super.getInnermostDelegate();
            }
            return null;
        }
    }
}
