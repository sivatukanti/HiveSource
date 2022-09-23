// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.util.Map;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.pool.ObjectPool;
import java.io.PrintWriter;
import javax.sql.DataSource;

public class PoolingDataSource implements DataSource
{
    private boolean accessToUnderlyingConnectionAllowed;
    protected PrintWriter _logWriter;
    protected ObjectPool _pool;
    
    public PoolingDataSource() {
        this(null);
    }
    
    public PoolingDataSource(final ObjectPool pool) {
        this.accessToUnderlyingConnectionAllowed = false;
        this._logWriter = null;
        this._pool = null;
        this._pool = pool;
    }
    
    public void setPool(final ObjectPool pool) throws IllegalStateException, NullPointerException {
        if (null != this._pool) {
            throw new IllegalStateException("Pool already set");
        }
        if (null == pool) {
            throw new NullPointerException("Pool must not be null.");
        }
        this._pool = pool;
    }
    
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("PoolingDataSource is not a wrapper.");
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = (Connection)this._pool.borrowObject();
            if (conn != null) {
                conn = new PoolGuardConnectionWrapper(conn);
            }
            return conn;
        }
        catch (SQLException e) {
            throw e;
        }
        catch (NoSuchElementException e2) {
            throw new SQLNestedException("Cannot get a connection, pool error " + e2.getMessage(), e2);
        }
        catch (RuntimeException e3) {
            throw e3;
        }
        catch (Exception e4) {
            throw new SQLNestedException("Cannot get a connection, general error", e4);
        }
    }
    
    @Override
    public Connection getConnection(final String uname, final String passwd) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return this._logWriter;
    }
    
    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
        this._logWriter = out;
    }
    
    private class PoolGuardConnectionWrapper extends DelegatingConnection
    {
        private Connection delegate;
        
        PoolGuardConnectionWrapper(final Connection delegate) {
            super(delegate);
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
        public boolean innermostDelegateEquals(final Connection c) {
            final Connection innerCon = super.getInnermostDelegate();
            if (innerCon == null) {
                return c == null;
            }
            return innerCon.equals(c);
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
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            final Connection conn = super.getInnermostDelegate();
            if (conn == null) {
                return false;
            }
            if (obj instanceof DelegatingConnection) {
                final DelegatingConnection c = (DelegatingConnection)obj;
                return c.innermostDelegateEquals(conn);
            }
            return conn.equals(obj);
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
            if (PoolingDataSource.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getDelegate();
            }
            return null;
        }
        
        @Override
        public Connection getInnermostDelegate() {
            if (PoolingDataSource.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getInnermostDelegate();
            }
            return null;
        }
    }
}
