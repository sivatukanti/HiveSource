// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.concurrent.Executor;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.util.Properties;
import java.sql.Struct;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.Map;
import java.sql.Statement;
import java.sql.Savepoint;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.datanucleus.store.NucleusConnection;
import java.sql.Connection;

public class JDOConnectionJDBCImpl extends JDOConnectionImpl implements Connection
{
    private final Connection conn;
    private boolean isAvailable;
    
    public JDOConnectionJDBCImpl(final NucleusConnection nconn) {
        super(nconn);
        this.isAvailable = true;
        this.conn = (Connection)nconn.getNativeConnection();
    }
    
    public boolean isAvailable() {
        return this.nucConn.isAvailable();
    }
    
    public int getHoldability() throws SQLException {
        this.assertAvailable();
        return this.conn.getHoldability();
    }
    
    public int getTransactionIsolation() throws SQLException {
        this.assertAvailable();
        return this.conn.getTransactionIsolation();
    }
    
    public void clearWarnings() throws SQLException {
        this.assertAvailable();
        this.conn.clearWarnings();
    }
    
    public void commit() throws SQLException {
        super.throwExceptionUnsupportedOperation("commit");
    }
    
    public void rollback() throws SQLException {
        super.throwExceptionUnsupportedOperation("rollback");
    }
    
    public boolean getAutoCommit() throws SQLException {
        this.assertAvailable();
        return this.conn.getAutoCommit();
    }
    
    public boolean isClosed() throws SQLException {
        return !this.nucConn.isAvailable() || this.conn.isClosed();
    }
    
    public boolean isReadOnly() throws SQLException {
        this.assertAvailable();
        return this.conn.isReadOnly();
    }
    
    public void setHoldability(final int holdability) throws SQLException {
        super.throwExceptionUnsupportedOperation("setHoldability");
    }
    
    public void setTransactionIsolation(final int level) throws SQLException {
        super.throwExceptionUnsupportedOperation("setTransactionIsolation");
    }
    
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        super.throwExceptionUnsupportedOperation("setAutoCommit");
    }
    
    public void setReadOnly(final boolean readOnly) throws SQLException {
        super.throwExceptionUnsupportedOperation("setReadOnly");
    }
    
    public String getCatalog() throws SQLException {
        this.assertAvailable();
        return this.conn.getCatalog();
    }
    
    public void setCatalog(final String catalog) throws SQLException {
        super.throwExceptionUnsupportedOperation("setCatalog");
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        super.throwExceptionUnsupportedOperation("getMetaData");
        return null;
    }
    
    public SQLWarning getWarnings() throws SQLException {
        this.assertAvailable();
        return this.conn.getWarnings();
    }
    
    public Savepoint setSavepoint() throws SQLException {
        super.throwExceptionUnsupportedOperation("setSavepoint");
        return null;
    }
    
    public void releaseSavepoint(final Savepoint pt) throws SQLException {
        super.throwExceptionUnsupportedOperation("releaseSavepoint");
    }
    
    public void rollback(final Savepoint pt) throws SQLException {
        super.throwExceptionUnsupportedOperation("rollback");
    }
    
    public Statement createStatement() throws SQLException {
        this.assertAvailable();
        return this.conn.createStatement();
    }
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.assertAvailable();
        return this.conn.createStatement(resultSetType, resultSetConcurrency);
    }
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.assertAvailable();
        return this.conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    public Map getTypeMap() throws SQLException {
        this.assertAvailable();
        return this.conn.getTypeMap();
    }
    
    public void setTypeMap(final Map map) throws SQLException {
        super.throwExceptionUnsupportedOperation("setTypeMap");
    }
    
    public String nativeSQL(final String sql) throws SQLException {
        this.assertAvailable();
        return this.conn.nativeSQL(sql);
    }
    
    public CallableStatement prepareCall(final String sql) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareCall(sql);
    }
    
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    public CallableStatement prepareCall(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareCall(arg0, arg1, arg2, arg3);
    }
    
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(sql);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int arg1) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(arg0, arg1);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(arg0, arg1, arg2, arg3);
    }
    
    public PreparedStatement prepareStatement(final String arg0, final int[] arg1) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(arg0, arg1);
    }
    
    public Savepoint setSavepoint(final String arg0) throws SQLException {
        super.throwExceptionUnsupportedOperation("setSavepoint");
        return null;
    }
    
    public PreparedStatement prepareStatement(final String arg0, final String[] arg1) throws SQLException {
        this.assertAvailable();
        return this.conn.prepareStatement(arg0, arg1);
    }
    
    public void assertAvailable() {
        if (!this.isAvailable) {
            this.throwExceptionNotAvailable();
        }
    }
    
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return this.conn.createArrayOf(typeName, elements);
    }
    
    public Blob createBlob() throws SQLException {
        return this.conn.createBlob();
    }
    
    public Clob createClob() throws SQLException {
        return this.conn.createClob();
    }
    
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return this.conn.createStruct(typeName, attributes);
    }
    
    public Properties getClientInfo() throws SQLException {
        return this.conn.getClientInfo();
    }
    
    public String getClientInfo(final String name) throws SQLException {
        return this.conn.getClientInfo(name);
    }
    
    public boolean isValid(final int timeout) throws SQLException {
        return this.conn.isValid(timeout);
    }
    
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        try {
            this.conn.setClientInfo(properties);
        }
        catch (Exception ex) {}
    }
    
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        try {
            this.conn.setClientInfo(name, value);
        }
        catch (Exception ex) {}
    }
    
    public NClob createNClob() throws SQLException {
        return this.conn.createNClob();
    }
    
    public SQLXML createSQLXML() throws SQLException {
        return this.conn.createSQLXML();
    }
    
    public boolean isWrapperFor(final Class iface) throws SQLException {
        return Connection.class.equals(iface);
    }
    
    public Object unwrap(final Class iface) throws SQLException {
        if (!Connection.class.equals(iface)) {
            throw new SQLException("Connection of type [" + this.getClass().getName() + "] can only be unwrapped as [java.sql.Connection], not as [" + iface.getName() + "]");
        }
        return this;
    }
    
    public void setSchema(final String schema) throws SQLException {
    }
    
    public String getSchema() throws SQLException {
        return null;
    }
    
    public void abort(final Executor executor) throws SQLException {
    }
    
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    }
    
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
}
