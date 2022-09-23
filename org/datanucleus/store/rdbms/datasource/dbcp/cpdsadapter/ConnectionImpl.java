// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.cpdsadapter;

import org.datanucleus.store.rdbms.datasource.dbcp.DelegatingPreparedStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.DelegatingConnection;

class ConnectionImpl extends DelegatingConnection
{
    private final boolean accessToUnderlyingConnectionAllowed;
    private final PooledConnectionImpl pooledConnection;
    
    ConnectionImpl(final PooledConnectionImpl pooledConnection, final Connection connection, final boolean accessToUnderlyingConnectionAllowed) {
        super(connection);
        this.pooledConnection = pooledConnection;
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }
    
    @Override
    public void close() throws SQLException {
        if (!this._closed) {
            this._closed = true;
            this.passivate();
            this.pooledConnection.notifyListeners();
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, autoGeneratedKeys));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, columnIndexes));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, columnNames));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    @Override
    public Connection getDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return this.getDelegateInternal();
        }
        return null;
    }
    
    @Override
    public Connection getInnermostDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return super.getInnermostDelegateInternal();
        }
        return null;
    }
}
