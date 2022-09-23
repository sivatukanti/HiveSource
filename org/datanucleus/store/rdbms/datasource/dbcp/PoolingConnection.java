// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.sql.CallableStatement;
import java.util.NoSuchElementException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import java.sql.Connection;

public class PoolingConnection extends DelegatingConnection implements Connection, KeyedPoolableObjectFactory
{
    protected KeyedObjectPool _pstmtPool;
    private static final byte STATEMENT_PREPAREDSTMT = 0;
    private static final byte STATEMENT_CALLABLESTMT = 1;
    
    public PoolingConnection(final Connection c) {
        super(c);
        this._pstmtPool = null;
    }
    
    public PoolingConnection(final Connection c, final KeyedObjectPool pool) {
        super(c);
        this._pstmtPool = null;
        this._pstmtPool = pool;
    }
    
    @Override
    public synchronized void close() throws SQLException {
        if (null != this._pstmtPool) {
            final KeyedObjectPool oldpool = this._pstmtPool;
            this._pstmtPool = null;
            try {
                oldpool.close();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (SQLException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw (SQLException)new SQLException("Cannot close connection").initCause(e3);
            }
        }
        this.getInnermostDelegate().close();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        if (null == this._pstmtPool) {
            throw new SQLException("Statement pool is null - closed or invalid PoolingConnection.");
        }
        try {
            return (PreparedStatement)this._pstmtPool.borrowObject(this.createKey(sql));
        }
        catch (NoSuchElementException e) {
            throw (SQLException)new SQLException("MaxOpenPreparedStatements limit reached").initCause(e);
        }
        catch (RuntimeException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e3);
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        if (null == this._pstmtPool) {
            throw new SQLException("Statement pool is null - closed or invalid PoolingConnection.");
        }
        try {
            return (PreparedStatement)this._pstmtPool.borrowObject(this.createKey(sql, resultSetType, resultSetConcurrency));
        }
        catch (NoSuchElementException e) {
            throw (SQLException)new SQLException("MaxOpenPreparedStatements limit reached").initCause(e);
        }
        catch (RuntimeException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw (SQLException)new SQLException("Borrow prepareStatement from pool failed").initCause(e3);
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        try {
            return (CallableStatement)this._pstmtPool.borrowObject(this.createKey(sql, (byte)1));
        }
        catch (NoSuchElementException e) {
            throw new SQLNestedException("MaxOpenCallableStatements limit reached", e);
        }
        catch (RuntimeException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new SQLNestedException("Borrow callableStatement from pool failed", e3);
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        try {
            return (CallableStatement)this._pstmtPool.borrowObject(this.createKey(sql, resultSetType, resultSetConcurrency, (byte)1));
        }
        catch (NoSuchElementException e) {
            throw new SQLNestedException("MaxOpenCallableStatements limit reached", e);
        }
        catch (RuntimeException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new SQLNestedException("Borrow callableStatement from pool failed", e3);
        }
    }
    
    protected Object createKey(final String sql, final int resultSetType, final int resultSetConcurrency) {
        String catalog = null;
        try {
            catalog = this.getCatalog();
        }
        catch (SQLException ex) {}
        return new PStmtKey(this.normalizeSQL(sql), catalog, resultSetType, resultSetConcurrency);
    }
    
    protected Object createKey(final String sql, final int resultSetType, final int resultSetConcurrency, final byte stmtType) {
        String catalog = null;
        try {
            catalog = this.getCatalog();
        }
        catch (SQLException ex) {}
        return new PStmtKey(this.normalizeSQL(sql), catalog, resultSetType, resultSetConcurrency, stmtType);
    }
    
    protected Object createKey(final String sql) {
        String catalog = null;
        try {
            catalog = this.getCatalog();
        }
        catch (SQLException ex) {}
        return new PStmtKey(this.normalizeSQL(sql), catalog);
    }
    
    protected Object createKey(final String sql, final byte stmtType) {
        String catalog = null;
        try {
            catalog = this.getCatalog();
        }
        catch (SQLException ex) {}
        return new PStmtKey(this.normalizeSQL(sql), catalog, stmtType);
    }
    
    protected String normalizeSQL(final String sql) {
        return sql.trim();
    }
    
    @Override
    public Object makeObject(final Object obj) throws Exception {
        if (null == obj || !(obj instanceof PStmtKey)) {
            throw new IllegalArgumentException("Prepared statement key is null or invalid.");
        }
        final PStmtKey key = (PStmtKey)obj;
        if (null == key._resultSetType && null == key._resultSetConcurrency) {
            if (key._stmtType == 0) {
                return new PoolablePreparedStatement(this.getDelegate().prepareStatement(key._sql), key, this._pstmtPool, this);
            }
            return new PoolableCallableStatement(this.getDelegate().prepareCall(key._sql), key, this._pstmtPool, this);
        }
        else {
            if (key._stmtType == 0) {
                return new PoolablePreparedStatement(this.getDelegate().prepareStatement(key._sql, key._resultSetType, key._resultSetConcurrency), key, this._pstmtPool, this);
            }
            return new PoolableCallableStatement(this.getDelegate().prepareCall(key._sql, key._resultSetType, key._resultSetConcurrency), key, this._pstmtPool, this);
        }
    }
    
    @Override
    public void destroyObject(final Object key, final Object obj) throws Exception {
        if (obj instanceof DelegatingPreparedStatement) {
            ((DelegatingPreparedStatement)obj).getInnermostDelegate().close();
        }
        else {
            ((PreparedStatement)obj).close();
        }
    }
    
    @Override
    public boolean validateObject(final Object key, final Object obj) {
        return true;
    }
    
    @Override
    public void activateObject(final Object key, final Object obj) throws Exception {
        ((DelegatingPreparedStatement)obj).activate();
    }
    
    @Override
    public void passivateObject(final Object key, final Object obj) throws Exception {
        ((PreparedStatement)obj).clearParameters();
        ((DelegatingPreparedStatement)obj).passivate();
    }
    
    @Override
    public String toString() {
        if (this._pstmtPool != null) {
            return "PoolingConnection: " + this._pstmtPool.toString();
        }
        return "PoolingConnection: null";
    }
    
    static class PStmtKey
    {
        protected String _sql;
        protected Integer _resultSetType;
        protected Integer _resultSetConcurrency;
        protected String _catalog;
        protected byte _stmtType;
        
        PStmtKey(final String sql) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
        }
        
        PStmtKey(final String sql, final String catalog) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
            this._catalog = catalog;
        }
        
        PStmtKey(final String sql, final String catalog, final byte stmtType) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
            this._catalog = catalog;
            this._stmtType = stmtType;
        }
        
        PStmtKey(final String sql, final int resultSetType, final int resultSetConcurrency) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
            this._resultSetType = new Integer(resultSetType);
            this._resultSetConcurrency = new Integer(resultSetConcurrency);
        }
        
        PStmtKey(final String sql, final String catalog, final int resultSetType, final int resultSetConcurrency) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
            this._catalog = catalog;
            this._resultSetType = new Integer(resultSetType);
            this._resultSetConcurrency = new Integer(resultSetConcurrency);
        }
        
        PStmtKey(final String sql, final String catalog, final int resultSetType, final int resultSetConcurrency, final byte stmtType) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._catalog = null;
            this._stmtType = 0;
            this._sql = sql;
            this._catalog = catalog;
            this._resultSetType = new Integer(resultSetType);
            this._resultSetConcurrency = new Integer(resultSetConcurrency);
            this._stmtType = stmtType;
        }
        
        @Override
        public boolean equals(final Object that) {
            try {
                final PStmtKey key = (PStmtKey)that;
                return ((null == this._sql && null == key._sql) || this._sql.equals(key._sql)) && ((null == this._catalog && null == key._catalog) || this._catalog.equals(key._catalog)) && ((null == this._resultSetType && null == key._resultSetType) || this._resultSetType.equals(key._resultSetType)) && ((null == this._resultSetConcurrency && null == key._resultSetConcurrency) || this._resultSetConcurrency.equals(key._resultSetConcurrency)) && this._stmtType == key._stmtType;
            }
            catch (ClassCastException e) {
                return false;
            }
            catch (NullPointerException e2) {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            if (this._catalog == null) {
                return (null == this._sql) ? 0 : this._sql.hashCode();
            }
            return (null == this._sql) ? this._catalog.hashCode() : (this._catalog + this._sql).hashCode();
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("PStmtKey: sql=");
            buf.append(this._sql);
            buf.append(", catalog=");
            buf.append(this._catalog);
            buf.append(", resultSetType=");
            buf.append(this._resultSetType);
            buf.append(", resultSetConcurrency=");
            buf.append(this._resultSetConcurrency);
            buf.append(", statmentType=");
            buf.append(this._stmtType);
            return buf.toString();
        }
    }
}
