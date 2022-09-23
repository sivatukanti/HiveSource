// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class HiveDataSource implements DataSource
{
    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection("", "");
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        try {
            return new HiveConnection("", null);
        }
        catch (Exception ex) {
            throw new SQLException("Error in getting HiveConnection", ex);
        }
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Method not supported");
    }
    
    @Override
    public void setLogWriter(final PrintWriter arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setLoginTimeout(final int arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
}
