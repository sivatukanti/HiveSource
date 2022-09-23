// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.security.Permission;
import java.sql.SQLPermission;
import java.util.concurrent.Executor;
import java.util.Map;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLClientInfoException;
import org.apache.derby.iapi.jdbc.FailedProperties40;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.jdbc.InternalDriver;
import org.apache.derby.iapi.jdbc.EngineConnection40;

public class EmbedConnection40 extends EmbedConnection implements EngineConnection40
{
    public EmbedConnection40(final EmbedConnection embedConnection) {
        super(embedConnection);
    }
    
    public EmbedConnection40(final InternalDriver internalDriver, final String s, final Properties properties) throws SQLException {
        super(internalDriver, s, properties);
    }
    
    @Override
    public Array createArrayOf(final String s, final Object[] array) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public Struct createStruct(final String s, final Object[] array) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public boolean isValid(final int value) throws SQLException {
        if (value < 0) {
            throw Util.generateCsSQLException("XJ081.S", new Integer(value), "timeout", "java.sql.Connection.isValid");
        }
        return !this.isClosed();
    }
    
    @Override
    public void setClientInfo(final String s, final String s2) throws SQLClientInfoException {
        final Properties properties = FailedProperties40.makeProperties(s, s2);
        try {
            this.checkIfClosed();
        }
        catch (SQLException ex) {
            throw new SQLClientInfoException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), new FailedProperties40(properties).getProperties());
        }
        if (s == null && s2 == null) {
            return;
        }
        this.setClientInfo(properties);
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        final FailedProperties40 failedProperties40 = new FailedProperties40(properties);
        try {
            this.checkIfClosed();
        }
        catch (SQLException ex) {
            throw new SQLClientInfoException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), failedProperties40.getProperties());
        }
        if (properties == null || properties.isEmpty()) {
            return;
        }
        final StandardException exception = StandardException.newException("XCY02.S", failedProperties40.getFirstKey(), failedProperties40.getFirstValue());
        throw new SQLClientInfoException(exception.getMessage(), exception.getSQLState(), exception.getErrorCode(), failedProperties40.getProperties());
    }
    
    @Override
    public String getClientInfo(final String s) throws SQLException {
        this.checkIfClosed();
        return null;
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkIfClosed();
        return new Properties();
    }
    
    @Override
    public final Map<String, Class<?>> getTypeMap() throws SQLException {
        return (Map<String, Class<?>>)super.getTypeMap();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        this.checkIfClosed();
        return clazz.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        this.checkIfClosed();
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw EmbedConnection.newSQLException("XJ128.S", clazz);
        }
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        if (this.isClosed()) {
            return;
        }
        if (executor == null) {
            throw EmbedConnection.newSQLException("XCZ02.S", "executor", "null");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SQLPermission("callAbort"));
        }
        this.beginAborting();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EmbedConnection40.this.rollback();
                    EmbedConnection40.this.close(EmbedConnection.exceptionClose);
                }
                catch (SQLException ex) {
                    Util.logSQLException(ex);
                }
            }
        });
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int n) throws SQLException {
        throw Util.notImplemented();
    }
}
