// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.Properties;
import java.sql.SQLClientInfoException;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Savepoint;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ClientInfoStatus;
import java.util.Map;
import java.sql.Connection;

public class DelegatingConnection extends AbandonedTrace implements Connection
{
    private static final Map<String, ClientInfoStatus> EMPTY_FAILED_PROPERTIES;
    protected Connection _conn;
    protected boolean _closed;
    
    public DelegatingConnection(final Connection c) {
        this._conn = null;
        this._closed = false;
        this._conn = c;
    }
    
    public DelegatingConnection(final Connection c, final AbandonedConfig config) {
        super(config);
        this._conn = null;
        this._closed = false;
        this._conn = c;
    }
    
    @Override
    public String toString() {
        String s = null;
        final Connection c = this.getInnermostDelegateInternal();
        if (c != null) {
            try {
                if (c.isClosed()) {
                    s = "connection is closed";
                }
                else {
                    final DatabaseMetaData meta = c.getMetaData();
                    if (meta != null) {
                        final StringBuffer sb = new StringBuffer();
                        sb.append(meta.getURL());
                        sb.append(", UserName=");
                        sb.append(meta.getUserName());
                        sb.append(", ");
                        sb.append(meta.getDriverName());
                        s = sb.toString();
                    }
                }
            }
            catch (SQLException ex) {}
        }
        if (s == null) {
            s = super.toString();
        }
        return s;
    }
    
    public Connection getDelegate() {
        return this.getDelegateInternal();
    }
    
    protected Connection getDelegateInternal() {
        return this._conn;
    }
    
    public boolean innermostDelegateEquals(final Connection c) {
        final Connection innerCon = this.getInnermostDelegateInternal();
        if (innerCon == null) {
            return c == null;
        }
        return innerCon.equals(c);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final Connection delegate = this.getInnermostDelegateInternal();
        if (delegate == null) {
            return false;
        }
        if (obj instanceof DelegatingConnection) {
            final DelegatingConnection c = (DelegatingConnection)obj;
            return c.innermostDelegateEquals(delegate);
        }
        return delegate.equals(obj);
    }
    
    @Override
    public int hashCode() {
        final Object obj = this.getInnermostDelegateInternal();
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
    
    public Connection getInnermostDelegate() {
        return this.getInnermostDelegateInternal();
    }
    
    protected final Connection getInnermostDelegateInternal() {
        Connection c = this._conn;
        while (c != null && c instanceof DelegatingConnection) {
            c = ((DelegatingConnection)c).getDelegateInternal();
            if (this == c) {
                return null;
            }
        }
        return c;
    }
    
    public void setDelegate(final Connection c) {
        this._conn = c;
    }
    
    @Override
    public void close() throws SQLException {
        this.passivate();
        this._conn.close();
    }
    
    protected void handleException(final SQLException e) throws SQLException {
        throw e;
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingStatement(this, this._conn.createStatement());
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingStatement(this, this._conn.createStatement(resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql));
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
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this._conn.prepareCall(sql));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this._conn.prepareCall(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.checkOpen();
        try {
            this._conn.clearWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void commit() throws SQLException {
        this.checkOpen();
        try {
            this._conn.commit();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getAutoCommit();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public String getCatalog() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getCatalog();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingDatabaseMetaData(this, this._conn.getMetaData());
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getTransactionIsolation();
        }
        catch (SQLException e) {
            this.handleException(e);
            return -1;
        }
    }
    
    @Override
    public Map getTypeMap() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getTypeMap();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.isReadOnly();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.nativeSQL(sql);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        this.checkOpen();
        try {
            this._conn.rollback();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setCatalog(catalog);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setReadOnly(readOnly);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setTransactionIsolation(level);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTypeMap(final Map map) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setTypeMap(map);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this._closed || this._conn.isClosed();
    }
    
    protected void checkOpen() throws SQLException {
        if (!this._closed) {
            return;
        }
        if (null != this._conn) {
            String label = "";
            try {
                label = this._conn.toString();
            }
            catch (Exception ex) {}
            throw new SQLException("Connection " + label + " is closed.");
        }
        throw new SQLException("Connection is null.");
    }
    
    protected void activate() {
        this._closed = false;
        this.setLastUsed();
        if (this._conn instanceof DelegatingConnection) {
            ((DelegatingConnection)this._conn).activate();
        }
    }
    
    protected void passivate() throws SQLException {
        try {
            final List traces = this.getTrace();
            if (traces != null) {
                for (final Object trace : traces) {
                    if (trace instanceof Statement) {
                        ((Statement)trace).close();
                    }
                    else {
                        if (!(trace instanceof ResultSet)) {
                            continue;
                        }
                        ((ResultSet)trace).close();
                    }
                }
                this.clearTrace();
            }
            this.setLastUsed(0L);
            if (this._conn instanceof DelegatingConnection) {
                ((DelegatingConnection)this._conn).passivate();
            }
        }
        finally {
            this._closed = true;
        }
    }
    
    @Override
    public int getHoldability() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getHoldability();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this.checkOpen();
        try {
            this._conn.setHoldability(holdability);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.setSavepoint();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.setSavepoint(name);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this._conn.rollback(savepoint);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this._conn.releaseSavepoint(savepoint);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingStatement(this, this._conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
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
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this._conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
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
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql, autoGeneratedKeys));
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
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql, columnIndexes));
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
            return new DelegatingPreparedStatement(this, this._conn.prepareStatement(sql, columnNames));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || this._conn.isWrapperFor(iface);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this._conn.getClass())) {
            return iface.cast(this._conn);
        }
        return this._conn.unwrap(iface);
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createArrayOf(typeName, elements);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createBlob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob createClob() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createClob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createNClob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createSQLXML();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.createStruct(typeName, attributes);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.isValid(timeout);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this._conn.setClientInfo(name, value);
        }
        catch (SQLClientInfoException e) {
            throw e;
        }
        catch (SQLException e2) {
            throw new SQLClientInfoException("Connection is closed.", DelegatingConnection.EMPTY_FAILED_PROPERTIES, e2);
        }
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this._conn.setClientInfo(properties);
        }
        catch (SQLClientInfoException e) {
            throw e;
        }
        catch (SQLException e2) {
            throw new SQLClientInfoException("Connection is closed.", DelegatingConnection.EMPTY_FAILED_PROPERTIES, e2);
        }
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getClientInfo();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        this.checkOpen();
        try {
            return this._conn.getClientInfo(name);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
    }
    
    @Override
    public String getSchema() throws SQLException {
        return null;
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
    
    static {
        EMPTY_FAILED_PROPERTIES = Collections.emptyMap();
    }
}
