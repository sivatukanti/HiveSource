// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.io.OutputStream;
import org.apache.derby.iapi.jdbc.JDBCBoot;
import org.apache.derby.impl.jdbc.Util;
import java.util.Properties;
import java.sql.Connection;
import java.util.Locale;
import java.sql.SQLException;
import java.io.PrintWriter;
import javax.sql.DataSource;

public final class EmbeddedSimpleDataSource implements DataSource
{
    private String password;
    private String user;
    private String databaseName;
    private String dataSourceName;
    private String description;
    private String createDatabase;
    private String shutdownDatabase;
    private String connectionAttributes;
    private transient PrintWriter printer;
    private transient int loginTimeout;
    private transient InternalDriver driver;
    private transient String jdbcurl;
    
    public EmbeddedSimpleDataSource() {
        this.update();
    }
    
    public int getLoginTimeout() throws SQLException {
        return this.loginTimeout;
    }
    
    public void setLoginTimeout(final int loginTimeout) throws SQLException {
        this.loginTimeout = loginTimeout;
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return this.printer;
    }
    
    public void setLogWriter(final PrintWriter printer) throws SQLException {
        this.printer = printer;
    }
    
    public final synchronized void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        this.update();
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public final void setDataSourceName(final String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public final String getDataSourceName() {
        return this.dataSourceName;
    }
    
    public final void setDescription(final String description) {
        this.description = description;
    }
    
    public final String getDescription() {
        return this.description;
    }
    
    public final void setUser(final String user) {
        this.user = user;
    }
    
    public final String getUser() {
        return this.user;
    }
    
    public final void setPassword(final String password) {
        this.password = password;
    }
    
    public final String getPassword() {
        return this.password;
    }
    
    public final void setCreateDatabase(final String createDatabase) {
        if (createDatabase != null && createDatabase.toLowerCase(Locale.ENGLISH).equals("create")) {
            this.createDatabase = createDatabase;
        }
        else {
            this.createDatabase = null;
        }
    }
    
    public final String getCreateDatabase() {
        return this.createDatabase;
    }
    
    public final void setShutdownDatabase(final String shutdownDatabase) {
        if (shutdownDatabase != null && shutdownDatabase.equalsIgnoreCase("shutdown")) {
            this.shutdownDatabase = shutdownDatabase;
        }
        else {
            this.shutdownDatabase = null;
        }
    }
    
    public final String getShutdownDatabase() {
        return this.shutdownDatabase;
    }
    
    public final void setConnectionAttributes(final String connectionAttributes) {
        this.connectionAttributes = connectionAttributes;
        this.update();
    }
    
    public final String getConnectionAttributes() {
        return this.connectionAttributes;
    }
    
    public final Connection getConnection() throws SQLException {
        return this.getConnection(this.getUser(), this.getPassword());
    }
    
    public final Connection getConnection(final String value, final String value2) throws SQLException {
        final Properties properties = new Properties();
        if (value != null) {
            properties.put("user", value);
        }
        if (value2 != null) {
            properties.put("password", value2);
        }
        if (this.createDatabase != null) {
            properties.put("create", "true");
        }
        if (this.shutdownDatabase != null) {
            properties.put("shutdown", "true");
        }
        final Connection connect = this.findDriver().connect(this.jdbcurl, properties, this.loginTimeout);
        if (connect == null) {
            throw Util.generateCsSQLException("XCY00.S", "databaseName", this.getDatabaseName());
        }
        return connect;
    }
    
    private InternalDriver findDriver() throws SQLException {
        final String jdbcurl = this.jdbcurl;
        if (this.driver == null || !this.driver.acceptsURL(jdbcurl)) {
            synchronized (this) {
                if (this.driver == null || !this.driver.acceptsURL(jdbcurl)) {
                    new JDBCBoot().boot("jdbc:derby:", new PrintWriter(System.err, true));
                    this.driver = InternalDriver.activeDriver();
                    if (this.driver == null) {
                        throw Util.generateCsSQLException("08006.C.8");
                    }
                }
            }
        }
        return this.driver;
    }
    
    private void update() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append("jdbc:derby:");
        String str = this.getDatabaseName();
        if (str != null) {
            str = str.trim();
        }
        if (str == null || str.length() == 0) {
            str = " ";
        }
        sb.append(str);
        final String connectionAttributes = this.getConnectionAttributes();
        if (connectionAttributes != null && connectionAttributes.trim().length() != 0) {
            sb.append(';');
            sb.append(this.connectionAttributes);
        }
        this.jdbcurl = sb.toString();
    }
}
