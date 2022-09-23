// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPoolFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericKeyedObjectPoolFactory;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericObjectPool;
import java.util.List;
import javax.sql.DataSource;

public class BasicDataSource implements DataSource
{
    protected volatile boolean defaultAutoCommit;
    protected transient Boolean defaultReadOnly;
    protected volatile int defaultTransactionIsolation;
    protected volatile String defaultCatalog;
    protected String driverClassName;
    protected ClassLoader driverClassLoader;
    protected int maxActive;
    protected int maxIdle;
    protected int minIdle;
    protected int initialSize;
    protected long maxWait;
    protected boolean poolPreparedStatements;
    protected int maxOpenPreparedStatements;
    protected boolean testOnBorrow;
    protected boolean testOnReturn;
    protected long timeBetweenEvictionRunsMillis;
    protected int numTestsPerEvictionRun;
    protected long minEvictableIdleTimeMillis;
    protected boolean testWhileIdle;
    protected volatile String password;
    protected String url;
    protected String username;
    protected volatile String validationQuery;
    protected volatile int validationQueryTimeout;
    protected volatile List connectionInitSqls;
    private boolean accessToUnderlyingConnectionAllowed;
    private volatile boolean restartNeeded;
    protected volatile GenericObjectPool connectionPool;
    protected Properties connectionProperties;
    protected volatile DataSource dataSource;
    protected PrintWriter logWriter;
    private AbandonedConfig abandonedConfig;
    protected boolean closed;
    
    public BasicDataSource() {
        this.defaultAutoCommit = true;
        this.defaultReadOnly = null;
        this.defaultTransactionIsolation = -1;
        this.defaultCatalog = null;
        this.driverClassName = null;
        this.driverClassLoader = null;
        this.maxActive = 8;
        this.maxIdle = 8;
        this.minIdle = 0;
        this.initialSize = 0;
        this.maxWait = -1L;
        this.poolPreparedStatements = false;
        this.maxOpenPreparedStatements = -1;
        this.testOnBorrow = true;
        this.testOnReturn = false;
        this.timeBetweenEvictionRunsMillis = -1L;
        this.numTestsPerEvictionRun = 3;
        this.minEvictableIdleTimeMillis = 1800000L;
        this.testWhileIdle = false;
        this.password = null;
        this.url = null;
        this.username = null;
        this.validationQuery = null;
        this.validationQueryTimeout = -1;
        this.accessToUnderlyingConnectionAllowed = false;
        this.restartNeeded = false;
        this.connectionPool = null;
        this.connectionProperties = new Properties();
        this.dataSource = null;
        this.logWriter = new PrintWriter(System.out);
    }
    
    public boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    public void setDefaultAutoCommit(final boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        this.restartNeeded = true;
    }
    
    public boolean getDefaultReadOnly() {
        final Boolean val = this.defaultReadOnly;
        return val != null && val;
    }
    
    public void setDefaultReadOnly(final boolean defaultReadOnly) {
        this.defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
        this.restartNeeded = true;
    }
    
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        this.restartNeeded = true;
    }
    
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }
    
    public void setDefaultCatalog(final String defaultCatalog) {
        if (defaultCatalog != null && defaultCatalog.trim().length() > 0) {
            this.defaultCatalog = defaultCatalog;
        }
        else {
            this.defaultCatalog = null;
        }
        this.restartNeeded = true;
    }
    
    public synchronized String getDriverClassName() {
        return this.driverClassName;
    }
    
    public synchronized void setDriverClassName(final String driverClassName) {
        if (driverClassName != null && driverClassName.trim().length() > 0) {
            this.driverClassName = driverClassName;
        }
        else {
            this.driverClassName = null;
        }
        this.restartNeeded = true;
    }
    
    public synchronized ClassLoader getDriverClassLoader() {
        return this.driverClassLoader;
    }
    
    public synchronized void setDriverClassLoader(final ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
        this.restartNeeded = true;
    }
    
    public synchronized int getMaxActive() {
        return this.maxActive;
    }
    
    public synchronized void setMaxActive(final int maxActive) {
        this.maxActive = maxActive;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxActive(maxActive);
        }
    }
    
    public synchronized int getMaxIdle() {
        return this.maxIdle;
    }
    
    public synchronized void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxIdle(maxIdle);
        }
    }
    
    public synchronized int getMinIdle() {
        return this.minIdle;
    }
    
    public synchronized void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setMinIdle(minIdle);
        }
    }
    
    public synchronized int getInitialSize() {
        return this.initialSize;
    }
    
    public synchronized void setInitialSize(final int initialSize) {
        this.initialSize = initialSize;
        this.restartNeeded = true;
    }
    
    public synchronized long getMaxWait() {
        return this.maxWait;
    }
    
    public synchronized void setMaxWait(final long maxWait) {
        this.maxWait = maxWait;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxWait(maxWait);
        }
    }
    
    public synchronized boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }
    
    public synchronized void setPoolPreparedStatements(final boolean poolingStatements) {
        this.poolPreparedStatements = poolingStatements;
        this.restartNeeded = true;
    }
    
    public synchronized int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }
    
    public synchronized void setMaxOpenPreparedStatements(final int maxOpenStatements) {
        this.maxOpenPreparedStatements = maxOpenStatements;
        this.restartNeeded = true;
    }
    
    public synchronized boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }
    
    public synchronized void setTestOnBorrow(final boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        if (this.connectionPool != null) {
            this.connectionPool.setTestOnBorrow(testOnBorrow);
        }
    }
    
    public synchronized boolean getTestOnReturn() {
        return this.testOnReturn;
    }
    
    public synchronized void setTestOnReturn(final boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        if (this.connectionPool != null) {
            this.connectionPool.setTestOnReturn(testOnReturn);
        }
    }
    
    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }
    
    public synchronized void setTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
    }
    
    public synchronized int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }
    
    public synchronized void setNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        if (this.connectionPool != null) {
            this.connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
    }
    
    public synchronized long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }
    
    public synchronized void setMinEvictableIdleTimeMillis(final long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
    }
    
    public synchronized boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }
    
    public synchronized void setTestWhileIdle(final boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setTestWhileIdle(testWhileIdle);
        }
    }
    
    public synchronized int getNumActive() {
        if (this.connectionPool != null) {
            return this.connectionPool.getNumActive();
        }
        return 0;
    }
    
    public synchronized int getNumIdle() {
        if (this.connectionPool != null) {
            return this.connectionPool.getNumIdle();
        }
        return 0;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
        this.restartNeeded = true;
    }
    
    public synchronized String getUrl() {
        return this.url;
    }
    
    public synchronized void setUrl(final String url) {
        this.url = url;
        this.restartNeeded = true;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
        this.restartNeeded = true;
    }
    
    public String getValidationQuery() {
        return this.validationQuery;
    }
    
    public void setValidationQuery(final String validationQuery) {
        if (validationQuery != null && validationQuery.trim().length() > 0) {
            this.validationQuery = validationQuery;
        }
        else {
            this.validationQuery = null;
        }
        this.restartNeeded = true;
    }
    
    public int getValidationQueryTimeout() {
        return this.validationQueryTimeout;
    }
    
    public void setValidationQueryTimeout(final int timeout) {
        this.validationQueryTimeout = timeout;
        this.restartNeeded = true;
    }
    
    public Collection getConnectionInitSqls() {
        final Collection result = this.connectionInitSqls;
        if (result == null) {
            return Collections.EMPTY_LIST;
        }
        return result;
    }
    
    public void setConnectionInitSqls(final Collection connectionInitSqls) {
        if (connectionInitSqls != null && connectionInitSqls.size() > 0) {
            ArrayList newVal = null;
            for (final Object o : connectionInitSqls) {
                if (o != null) {
                    final String s = o.toString();
                    if (s.trim().length() <= 0) {
                        continue;
                    }
                    if (newVal == null) {
                        newVal = new ArrayList();
                    }
                    newVal.add(s);
                }
            }
            this.connectionInitSqls = newVal;
        }
        else {
            this.connectionInitSqls = null;
        }
        this.restartNeeded = true;
    }
    
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
        this.restartNeeded = true;
    }
    
    private boolean isRestartNeeded() {
        return this.restartNeeded;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.createDataSource().getConnection();
    }
    
    @Override
    public Connection getConnection(final String user, final String pass) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.createDataSource().getLogWriter();
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    @Override
    public void setLogWriter(final PrintWriter logWriter) throws SQLException {
        this.createDataSource().setLogWriter(logWriter);
        this.logWriter = logWriter;
    }
    
    public boolean getRemoveAbandoned() {
        return this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandoned();
    }
    
    public void setRemoveAbandoned(final boolean removeAbandoned) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setRemoveAbandoned(removeAbandoned);
        this.restartNeeded = true;
    }
    
    public int getRemoveAbandonedTimeout() {
        if (this.abandonedConfig != null) {
            return this.abandonedConfig.getRemoveAbandonedTimeout();
        }
        return 300;
    }
    
    public void setRemoveAbandonedTimeout(final int removeAbandonedTimeout) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        this.restartNeeded = true;
    }
    
    public boolean getLogAbandoned() {
        return this.abandonedConfig != null && this.abandonedConfig.getLogAbandoned();
    }
    
    public void setLogAbandoned(final boolean logAbandoned) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setLogAbandoned(logAbandoned);
        this.restartNeeded = true;
    }
    
    public void addConnectionProperty(final String name, final String value) {
        this.connectionProperties.put(name, value);
        this.restartNeeded = true;
    }
    
    public void removeConnectionProperty(final String name) {
        this.connectionProperties.remove(name);
        this.restartNeeded = true;
    }
    
    public void setConnectionProperties(final String connectionProperties) {
        if (connectionProperties == null) {
            throw new NullPointerException("connectionProperties is null");
        }
        final String[] entries = connectionProperties.split(";");
        final Properties properties = new Properties();
        for (int i = 0; i < entries.length; ++i) {
            final String entry = entries[i];
            if (entry.length() > 0) {
                final int index = entry.indexOf(61);
                if (index > 0) {
                    final String name = entry.substring(0, index);
                    final String value = entry.substring(index + 1);
                    properties.setProperty(name, value);
                }
                else {
                    properties.setProperty(entry, "");
                }
            }
        }
        this.connectionProperties = properties;
        this.restartNeeded = true;
    }
    
    public synchronized void close() throws SQLException {
        this.closed = true;
        final GenericObjectPool oldpool = this.connectionPool;
        this.connectionPool = null;
        this.dataSource = null;
        try {
            if (oldpool != null) {
                oldpool.close();
            }
        }
        catch (SQLException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new SQLNestedException("Cannot close connection pool", e3);
        }
    }
    
    public synchronized boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("BasicDataSource is not a wrapper.");
    }
    
    protected synchronized DataSource createDataSource() throws SQLException {
        if (this.closed) {
            throw new SQLException("Data source is closed");
        }
        if (this.dataSource != null) {
            return this.dataSource;
        }
        final ConnectionFactory driverConnectionFactory = this.createConnectionFactory();
        this.createConnectionPool();
        GenericKeyedObjectPoolFactory statementPoolFactory = null;
        if (this.isPoolPreparedStatements()) {
            statementPoolFactory = new GenericKeyedObjectPoolFactory(null, -1, (byte)0, 0L, 1, this.maxOpenPreparedStatements);
        }
        this.createPoolableConnectionFactory(driverConnectionFactory, statementPoolFactory, this.abandonedConfig);
        this.createDataSourceInstance();
        try {
            for (int i = 0; i < this.initialSize; ++i) {
                this.connectionPool.addObject();
            }
        }
        catch (Exception e) {
            throw new SQLNestedException("Error preloading the connection pool", e);
        }
        return this.dataSource;
    }
    
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        Class driverFromCCL = null;
        if (this.driverClassName != null) {
            try {
                try {
                    if (this.driverClassLoader == null) {
                        Class.forName(this.driverClassName);
                    }
                    else {
                        Class.forName(this.driverClassName, true, this.driverClassLoader);
                    }
                }
                catch (ClassNotFoundException cnfe) {
                    driverFromCCL = Thread.currentThread().getContextClassLoader().loadClass(this.driverClassName);
                }
            }
            catch (Throwable t) {
                final String message = "Cannot load JDBC driver class '" + this.driverClassName + "'";
                this.logWriter.println(message);
                t.printStackTrace(this.logWriter);
                throw new SQLNestedException(message, t);
            }
        }
        Driver driver = null;
        try {
            if (driverFromCCL == null) {
                driver = DriverManager.getDriver(this.url);
            }
            else {
                driver = driverFromCCL.newInstance();
                if (!driver.acceptsURL(this.url)) {
                    throw new SQLException("No suitable driver", "08001");
                }
            }
        }
        catch (Throwable t2) {
            final String message2 = "Cannot create JDBC driver of class '" + ((this.driverClassName != null) ? this.driverClassName : "") + "' for connect URL '" + this.url + "'";
            this.logWriter.println(message2);
            t2.printStackTrace(this.logWriter);
            throw new SQLNestedException(message2, t2);
        }
        if (this.validationQuery == null) {
            this.setTestOnBorrow(false);
            this.setTestOnReturn(false);
            this.setTestWhileIdle(false);
        }
        final String user = this.username;
        if (user != null) {
            this.connectionProperties.put("user", user);
        }
        else {
            this.log("DBCP DataSource configured without a 'username'");
        }
        final String pwd = this.password;
        if (pwd != null) {
            this.connectionProperties.put("password", pwd);
        }
        else {
            this.log("DBCP DataSource configured without a 'password'");
        }
        final ConnectionFactory driverConnectionFactory = new DriverConnectionFactory(driver, this.url, this.connectionProperties);
        return driverConnectionFactory;
    }
    
    protected void createConnectionPool() {
        GenericObjectPool gop;
        if (this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandoned()) {
            gop = new AbandonedObjectPool(null, this.abandonedConfig);
        }
        else {
            gop = new GenericObjectPool();
        }
        gop.setMaxActive(this.maxActive);
        gop.setMaxIdle(this.maxIdle);
        gop.setMinIdle(this.minIdle);
        gop.setMaxWait(this.maxWait);
        gop.setTestOnBorrow(this.testOnBorrow);
        gop.setTestOnReturn(this.testOnReturn);
        gop.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        gop.setNumTestsPerEvictionRun(this.numTestsPerEvictionRun);
        gop.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        gop.setTestWhileIdle(this.testWhileIdle);
        this.connectionPool = gop;
    }
    
    protected void createDataSourceInstance() throws SQLException {
        final PoolingDataSource pds = new PoolingDataSource(this.connectionPool);
        pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        pds.setLogWriter(this.logWriter);
        this.dataSource = pds;
    }
    
    protected void createPoolableConnectionFactory(final ConnectionFactory driverConnectionFactory, final KeyedObjectPoolFactory statementPoolFactory, final AbandonedConfig configuration) throws SQLException {
        PoolableConnectionFactory connectionFactory = null;
        try {
            connectionFactory = new PoolableConnectionFactory(driverConnectionFactory, this.connectionPool, statementPoolFactory, this.validationQuery, this.validationQueryTimeout, this.connectionInitSqls, this.defaultReadOnly, this.defaultAutoCommit, this.defaultTransactionIsolation, this.defaultCatalog, configuration);
            validateConnectionFactory(connectionFactory);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLNestedException("Cannot create PoolableConnectionFactory (" + e2.getMessage() + ")", e2);
        }
    }
    
    protected static void validateConnectionFactory(final PoolableConnectionFactory connectionFactory) throws Exception {
        Connection conn = null;
        try {
            conn = (Connection)connectionFactory.makeObject();
            connectionFactory.activateObject(conn);
            connectionFactory.validateConnection(conn);
            connectionFactory.passivateObject(conn);
        }
        finally {
            connectionFactory.destroyObject(conn);
        }
    }
    
    private void restart() {
        try {
            this.close();
        }
        catch (SQLException e) {
            this.log("Could not restart DataSource, cause: " + e.getMessage());
        }
    }
    
    protected void log(final String message) {
        if (this.logWriter != null) {
            this.logWriter.println(message);
        }
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }
    
    static {
        DriverManager.getDrivers();
    }
}
