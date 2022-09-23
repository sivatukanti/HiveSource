// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.cpdsadapter;

import java.util.Arrays;
import org.datanucleus.store.rdbms.datasource.dbcp.DelegatingPreparedStatement;
import java.sql.PreparedStatement;
import javax.sql.ConnectionEvent;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.datasource.dbcp.SQLNestedException;
import javax.sql.StatementEventListener;
import javax.sql.ConnectionEventListener;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import java.util.Vector;
import org.datanucleus.store.rdbms.datasource.dbcp.DelegatingConnection;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import javax.sql.PooledConnection;

class PooledConnectionImpl implements PooledConnection, KeyedPoolableObjectFactory
{
    private static final String CLOSED = "Attempted to use PooledConnection after closed() was called.";
    private Connection connection;
    private final DelegatingConnection delegatingConnection;
    private Connection logicalConnection;
    private final Vector eventListeners;
    private final Vector statementEventListeners;
    boolean isClosed;
    protected KeyedObjectPool pstmtPool;
    private boolean accessToUnderlyingConnectionAllowed;
    
    PooledConnectionImpl(final Connection connection, final KeyedObjectPool pool) {
        this.connection = null;
        this.logicalConnection = null;
        this.statementEventListeners = new Vector();
        this.pstmtPool = null;
        this.accessToUnderlyingConnectionAllowed = false;
        this.connection = connection;
        if (connection instanceof DelegatingConnection) {
            this.delegatingConnection = (DelegatingConnection)connection;
        }
        else {
            this.delegatingConnection = new DelegatingConnection(connection);
        }
        this.eventListeners = new Vector();
        this.isClosed = false;
        if (pool != null) {
            (this.pstmtPool = pool).setFactory(this);
        }
    }
    
    @Override
    public void addConnectionEventListener(final ConnectionEventListener listener) {
        if (!this.eventListeners.contains(listener)) {
            this.eventListeners.add(listener);
        }
    }
    
    @Override
    public void addStatementEventListener(final StatementEventListener listener) {
        if (!this.statementEventListeners.contains(listener)) {
            this.statementEventListeners.add(listener);
        }
    }
    
    @Override
    public void close() throws SQLException {
        this.assertOpen();
        this.isClosed = true;
        try {
            if (this.pstmtPool != null) {
                try {
                    this.pstmtPool.close();
                }
                finally {
                    this.pstmtPool = null;
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Cannot close connection (return to pool failed)", e2);
        }
        finally {
            try {
                this.connection.close();
            }
            finally {
                this.connection = null;
            }
        }
    }
    
    private void assertOpen() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Attempted to use PooledConnection after closed() was called.");
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        this.assertOpen();
        if (this.logicalConnection != null && !this.logicalConnection.isClosed()) {
            throw new SQLException("PooledConnection was reused, withoutits previous Connection being closed.");
        }
        return this.logicalConnection = new ConnectionImpl(this, this.connection, this.isAccessToUnderlyingConnectionAllowed());
    }
    
    @Override
    public void removeConnectionEventListener(final ConnectionEventListener listener) {
        this.eventListeners.remove(listener);
    }
    
    @Override
    public void removeStatementEventListener(final StatementEventListener listener) {
        this.statementEventListeners.remove(listener);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.connection.close();
        }
        catch (Exception ex) {}
        if (this.logicalConnection != null && !this.logicalConnection.isClosed()) {
            throw new SQLException("PooledConnection was gc'ed, withoutits last Connection being closed.");
        }
    }
    
    void notifyListeners() {
        final ConnectionEvent event = new ConnectionEvent(this);
        final Object[] listeners = this.eventListeners.toArray();
        for (int i = 0; i < listeners.length; ++i) {
            ((ConnectionEventListener)listeners[i]).connectionClosed(event);
        }
    }
    
    PreparedStatement prepareStatement(final String sql) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql, resultSetType, resultSetConcurrency));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql, autoGeneratedKeys);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql, autoGeneratedKeys));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql, columnIndexes);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql, columnIndexes));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        if (this.pstmtPool == null) {
            return this.connection.prepareStatement(sql, columnNames);
        }
        try {
            return (PreparedStatement)this.pstmtPool.borrowObject(this.createKey(sql, columnNames));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Borrow prepareStatement from pool failed", e2);
        }
    }
    
    protected Object createKey(final String sql, final int autoGeneratedKeys) {
        return new PStmtKey(this.normalizeSQL(sql), autoGeneratedKeys);
    }
    
    protected Object createKey(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
        return new PStmtKey(this.normalizeSQL(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    protected Object createKey(final String sql, final int[] columnIndexes) {
        return new PStmtKey(this.normalizeSQL(sql), columnIndexes);
    }
    
    protected Object createKey(final String sql, final String[] columnNames) {
        return new PStmtKey(this.normalizeSQL(sql), columnNames);
    }
    
    protected Object createKey(final String sql, final int resultSetType, final int resultSetConcurrency) {
        return new PStmtKey(this.normalizeSQL(sql), resultSetType, resultSetConcurrency);
    }
    
    protected Object createKey(final String sql) {
        return new PStmtKey(this.normalizeSQL(sql));
    }
    
    protected String normalizeSQL(final String sql) {
        return sql.trim();
    }
    
    @Override
    public Object makeObject(final Object obj) throws Exception {
        if (null == obj || !(obj instanceof PStmtKey)) {
            throw new IllegalArgumentException();
        }
        final PStmtKey key = (PStmtKey)obj;
        if (null != key._resultSetType || null != key._resultSetConcurrency) {
            return new PoolablePreparedStatementStub(this.connection.prepareStatement(key._sql, key._resultSetType, key._resultSetConcurrency), key, this.pstmtPool, this.delegatingConnection);
        }
        if (null == key._autoGeneratedKeys) {
            return new PoolablePreparedStatementStub(this.connection.prepareStatement(key._sql), key, this.pstmtPool, this.delegatingConnection);
        }
        return new PoolablePreparedStatementStub(this.connection.prepareStatement(key._sql, key._autoGeneratedKeys), key, this.pstmtPool, this.delegatingConnection);
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
        ((PoolablePreparedStatementStub)obj).activate();
    }
    
    @Override
    public void passivateObject(final Object key, final Object obj) throws Exception {
        ((PreparedStatement)obj).clearParameters();
        ((PoolablePreparedStatementStub)obj).passivate();
    }
    
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    static class PStmtKey
    {
        protected String _sql;
        protected Integer _resultSetType;
        protected Integer _resultSetConcurrency;
        protected Integer _autoGeneratedKeys;
        protected Integer _resultSetHoldability;
        protected int[] _columnIndexes;
        protected String[] _columnNames;
        
        PStmtKey(final String sql) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
        }
        
        PStmtKey(final String sql, final int resultSetType, final int resultSetConcurrency) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
            this._resultSetType = new Integer(resultSetType);
            this._resultSetConcurrency = new Integer(resultSetConcurrency);
        }
        
        PStmtKey(final String sql, final int autoGeneratedKeys) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
            this._autoGeneratedKeys = new Integer(autoGeneratedKeys);
        }
        
        PStmtKey(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
            this._resultSetType = new Integer(resultSetType);
            this._resultSetConcurrency = new Integer(resultSetConcurrency);
            this._resultSetHoldability = new Integer(resultSetHoldability);
        }
        
        PStmtKey(final String sql, final int[] columnIndexes) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
            this._columnIndexes = columnIndexes;
        }
        
        PStmtKey(final String sql, final String[] columnNames) {
            this._sql = null;
            this._resultSetType = null;
            this._resultSetConcurrency = null;
            this._autoGeneratedKeys = null;
            this._resultSetHoldability = null;
            this._columnIndexes = null;
            this._columnNames = null;
            this._sql = sql;
            this._columnNames = columnNames;
        }
        
        @Override
        public boolean equals(final Object that) {
            try {
                final PStmtKey key = (PStmtKey)that;
                return ((null == this._sql && null == key._sql) || this._sql.equals(key._sql)) && ((null == this._resultSetType && null == key._resultSetType) || this._resultSetType.equals(key._resultSetType)) && ((null == this._resultSetConcurrency && null == key._resultSetConcurrency) || this._resultSetConcurrency.equals(key._resultSetConcurrency)) && ((null == this._autoGeneratedKeys && null == key._autoGeneratedKeys) || this._autoGeneratedKeys.equals(key._autoGeneratedKeys)) && ((null == this._resultSetHoldability && null == key._resultSetHoldability) || this._resultSetHoldability.equals(key._resultSetHoldability)) && ((null == this._columnIndexes && null == key._columnIndexes) || Arrays.equals(this._columnIndexes, key._columnIndexes)) && ((null == this._columnNames && null == key._columnNames) || Arrays.equals(this._columnNames, key._columnNames));
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
            return (null == this._sql) ? 0 : this._sql.hashCode();
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("PStmtKey: sql=");
            buf.append(this._sql);
            buf.append(", resultSetType=");
            buf.append(this._resultSetType);
            buf.append(", resultSetConcurrency=");
            buf.append(this._resultSetConcurrency);
            buf.append(", autoGeneratedKeys=");
            buf.append(this._autoGeneratedKeys);
            buf.append(", resultSetHoldability=");
            buf.append(this._resultSetHoldability);
            buf.append(", columnIndexes=");
            this.arrayToString(buf, this._columnIndexes);
            buf.append(", columnNames=");
            this.arrayToString(buf, this._columnNames);
            return buf.toString();
        }
        
        private void arrayToString(final StringBuffer sb, final int[] array) {
            if (array == null) {
                sb.append("null");
                return;
            }
            sb.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(array[i]);
            }
            sb.append(']');
        }
        
        private void arrayToString(final StringBuffer sb, final String[] array) {
            if (array == null) {
                sb.append("null");
                return;
            }
            sb.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(array[i]);
            }
            sb.append(']');
        }
    }
}
