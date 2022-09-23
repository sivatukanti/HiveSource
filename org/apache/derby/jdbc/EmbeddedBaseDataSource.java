// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import org.apache.derby.impl.jdbc.Util;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Locale;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.io.Serializable;

public abstract class EmbeddedBaseDataSource implements Serializable, EmbeddedDataSourceInterface
{
    private static final long serialVersionUID = 1872877359127597176L;
    protected String description;
    protected String dataSourceName;
    protected String databaseName;
    protected String connectionAttributes;
    protected String createDatabase;
    protected String shutdownDatabase;
    protected boolean attributesAsPassword;
    private String shortDatabaseName;
    private String password;
    private String user;
    protected int loginTimeout;
    private transient PrintWriter printer;
    protected transient String jdbcurl;
    protected transient InternalDriver driver;
    
    public EmbeddedBaseDataSource() {
        this.update();
    }
    
    public final synchronized void setDatabaseName(final String s) {
        this.databaseName = s;
        if (s != null && s.indexOf(";") >= 0) {
            this.shortDatabaseName = s.split(";")[0];
        }
        else {
            this.shortDatabaseName = s;
        }
        this.update();
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    protected String getShortDatabaseName() {
        return this.shortDatabaseName;
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
    
    protected void update() {
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
    
    InternalDriver findDriver() throws SQLException {
        final String jdbcurl = this.jdbcurl;
        synchronized (this) {
            if (this.driver == null || !this.driver.acceptsURL(jdbcurl)) {
                new EmbeddedDriver();
                final Driver driver = DriverManager.getDriver(jdbcurl);
                if (driver instanceof AutoloadedDriver) {
                    this.driver = (InternalDriver)AutoloadedDriver.getDriverModule();
                }
                else {
                    this.driver = (InternalDriver)driver;
                }
            }
        }
        return this.driver;
    }
    
    public final void setConnectionAttributes(final String connectionAttributes) {
        this.connectionAttributes = connectionAttributes;
        this.update();
    }
    
    public final String getConnectionAttributes() {
        return this.connectionAttributes;
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
    
    public final void setAttributesAsPassword(final boolean attributesAsPassword) {
        this.attributesAsPassword = attributesAsPassword;
        this.update();
    }
    
    public final boolean getAttributesAsPassword() {
        return this.attributesAsPassword;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof EmbeddedDataSource) {
            final EmbeddedDataSource embeddedDataSource = (EmbeddedDataSource)o;
            boolean b = true;
            if (this.databaseName != null) {
                if (!this.databaseName.equals(embeddedDataSource.databaseName)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.databaseName != null) {
                b = false;
            }
            if (this.dataSourceName != null) {
                if (!this.dataSourceName.equals(embeddedDataSource.dataSourceName)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.dataSourceName != null) {
                b = false;
            }
            if (this.description != null) {
                if (!this.description.equals(embeddedDataSource.description)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.description != null) {
                b = false;
            }
            if (this.createDatabase != null) {
                if (!this.createDatabase.equals(embeddedDataSource.createDatabase)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.createDatabase != null) {
                b = false;
            }
            if (this.shutdownDatabase != null) {
                if (!this.shutdownDatabase.equals(embeddedDataSource.shutdownDatabase)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.shutdownDatabase != null) {
                b = false;
            }
            if (this.connectionAttributes != null) {
                if (!this.connectionAttributes.equals(embeddedDataSource.connectionAttributes)) {
                    b = false;
                }
            }
            else if (embeddedDataSource.connectionAttributes != null) {
                b = false;
            }
            if (this.loginTimeout != embeddedDataSource.loginTimeout) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public Connection getConnection() throws SQLException {
        return this.getConnection(this.getUser(), this.getPassword(), false);
    }
    
    public Connection getConnection(final String s, final String s2) throws SQLException {
        return this.getConnection(s, s2, true);
    }
    
    final Connection getConnection(final String value, final String s, final boolean b) throws SQLException {
        final Properties properties = new Properties();
        if (value != null) {
            properties.put("user", value);
        }
        if ((!b || !this.attributesAsPassword) && s != null) {
            properties.put("password", s);
        }
        if (this.createDatabase != null) {
            properties.put("create", "true");
        }
        if (this.shutdownDatabase != null) {
            properties.put("shutdown", "true");
        }
        String str = this.jdbcurl;
        if (this.attributesAsPassword && b && s != null) {
            final StringBuffer sb = new StringBuffer(str.length() + s.length() + 1);
            sb.append(str);
            sb.append(';');
            sb.append(s);
            str = sb.toString();
        }
        final Connection connect = this.findDriver().connect(str, properties, this.loginTimeout);
        if (connect == null) {
            throw Util.generateCsSQLException("XCY00.S", "databaseName", this.getDatabaseName());
        }
        return connect;
    }
    
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return clazz.isInstance(this);
    }
    
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw Util.generateCsSQLException("XJ128.S", clazz);
        }
    }
    
    protected static ResourceAdapter setupResourceAdapter(final EmbeddedXADataSourceInterface embeddedXADataSourceInterface, ResourceAdapter resourceAdapter, final String s, final String s2, final boolean b) throws SQLException {
        synchronized (embeddedXADataSourceInterface) {
            if (resourceAdapter == null || !resourceAdapter.isActive()) {
                resourceAdapter = null;
                final String shortDatabaseName = ((EmbeddedBaseDataSource)embeddedXADataSourceInterface).getShortDatabaseName();
                if (shortDatabaseName != null) {
                    Database database = null;
                    if (Monitor.getMonitor() != null) {
                        database = (Database)Monitor.findService("org.apache.derby.database.Database", shortDatabaseName);
                    }
                    if (database == null) {
                        if (b) {
                            embeddedXADataSourceInterface.getConnection(s, s2).close();
                        }
                        else {
                            embeddedXADataSourceInterface.getConnection().close();
                        }
                        database = (Database)Monitor.findService("org.apache.derby.database.Database", shortDatabaseName);
                    }
                    if (database != null) {
                        resourceAdapter = (ResourceAdapter)database.getResourceAdapter();
                    }
                }
                if (resourceAdapter == null) {
                    throw new SQLException(MessageService.getTextMessage("I024"), "08006", 45000);
                }
                if (((EmbeddedBaseDataSource)embeddedXADataSourceInterface).findDriver() == null) {
                    throw new SQLException(MessageService.getTextMessage("I025"), "08006", 45000);
                }
            }
        }
        return resourceAdapter;
    }
}
