// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.datasources;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import javax.sql.PooledConnection;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.NoSuchElementException;
import org.apache.commons.dbcp.SQLNestedException;
import java.sql.Connection;
import java.io.OutputStream;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import java.io.Serializable;
import javax.naming.Referenceable;
import javax.sql.DataSource;

public abstract class InstanceKeyDataSource implements DataSource, Referenceable, Serializable
{
    private static final long serialVersionUID = -4243533936955098795L;
    private static final String GET_CONNECTION_CALLED = "A Connection was already requested from this source, further initialization is not allowed.";
    private static final String BAD_TRANSACTION_ISOLATION = "The requested TransactionIsolation level is invalid.";
    protected static final int UNKNOWN_TRANSACTIONISOLATION = -1;
    private volatile boolean getConnectionCalled;
    private ConnectionPoolDataSource dataSource;
    private String dataSourceName;
    private boolean defaultAutoCommit;
    private int defaultTransactionIsolation;
    private boolean defaultReadOnly;
    private String description;
    Properties jndiEnvironment;
    private int loginTimeout;
    private PrintWriter logWriter;
    private boolean _testOnBorrow;
    private boolean _testOnReturn;
    private int _timeBetweenEvictionRunsMillis;
    private int _numTestsPerEvictionRun;
    private int _minEvictableIdleTimeMillis;
    private boolean _testWhileIdle;
    private String validationQuery;
    private boolean rollbackAfterValidation;
    private boolean testPositionSet;
    protected String instanceKey;
    
    public InstanceKeyDataSource() {
        this.getConnectionCalled = false;
        this.dataSource = null;
        this.dataSourceName = null;
        this.defaultAutoCommit = false;
        this.defaultTransactionIsolation = -1;
        this.defaultReadOnly = false;
        this.description = null;
        this.jndiEnvironment = null;
        this.loginTimeout = 0;
        this.logWriter = null;
        this._testOnBorrow = false;
        this._testOnReturn = false;
        this._timeBetweenEvictionRunsMillis = (int)Math.min(2147483647L, -1L);
        this._numTestsPerEvictionRun = 3;
        this._minEvictableIdleTimeMillis = (int)Math.min(2147483647L, 1800000L);
        this._testWhileIdle = false;
        this.validationQuery = null;
        this.rollbackAfterValidation = false;
        this.testPositionSet = false;
        this.instanceKey = null;
        this.defaultAutoCommit = true;
    }
    
    protected void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException("A Connection was already requested from this source, further initialization is not allowed.");
        }
    }
    
    public abstract void close() throws Exception;
    
    protected abstract PooledConnectionManager getConnectionManager(final UserPassKey p0);
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("InstanceKeyDataSource is not a wrapper.");
    }
    
    public ConnectionPoolDataSource getConnectionPoolDataSource() {
        return this.dataSource;
    }
    
    public void setConnectionPoolDataSource(final ConnectionPoolDataSource v) {
        this.assertInitializationAllowed();
        if (this.dataSourceName != null) {
            throw new IllegalStateException("Cannot set the DataSource, if JNDI is used.");
        }
        if (this.dataSource != null) {
            throw new IllegalStateException("The CPDS has already been set. It cannot be altered.");
        }
        this.dataSource = v;
        this.instanceKey = InstanceKeyObjectFactory.registerNewInstance(this);
    }
    
    public String getDataSourceName() {
        return this.dataSourceName;
    }
    
    public void setDataSourceName(final String v) {
        this.assertInitializationAllowed();
        if (this.dataSource != null) {
            throw new IllegalStateException("Cannot set the JNDI name for the DataSource, if already set using setConnectionPoolDataSource.");
        }
        if (this.dataSourceName != null) {
            throw new IllegalStateException("The DataSourceName has already been set. It cannot be altered.");
        }
        this.dataSourceName = v;
        this.instanceKey = InstanceKeyObjectFactory.registerNewInstance(this);
    }
    
    public boolean isDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    public void setDefaultAutoCommit(final boolean v) {
        this.assertInitializationAllowed();
        this.defaultAutoCommit = v;
    }
    
    public boolean isDefaultReadOnly() {
        return this.defaultReadOnly;
    }
    
    public void setDefaultReadOnly(final boolean v) {
        this.assertInitializationAllowed();
        this.defaultReadOnly = v;
    }
    
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public void setDefaultTransactionIsolation(final int v) {
        this.assertInitializationAllowed();
        switch (v) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 8: {
                this.defaultTransactionIsolation = v;
            }
            default: {
                throw new IllegalArgumentException("The requested TransactionIsolation level is invalid.");
            }
        }
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String v) {
        this.description = v;
    }
    
    public String getJndiEnvironment(final String key) {
        String value = null;
        if (this.jndiEnvironment != null) {
            value = this.jndiEnvironment.getProperty(key);
        }
        return value;
    }
    
    public void setJndiEnvironment(final String key, final String value) {
        if (this.jndiEnvironment == null) {
            this.jndiEnvironment = new Properties();
        }
        this.jndiEnvironment.setProperty(key, value);
    }
    
    @Override
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    @Override
    public void setLoginTimeout(final int v) {
        this.loginTimeout = v;
    }
    
    @Override
    public PrintWriter getLogWriter() {
        if (this.logWriter == null) {
            this.logWriter = new PrintWriter(System.out);
        }
        return this.logWriter;
    }
    
    @Override
    public void setLogWriter(final PrintWriter v) {
        this.logWriter = v;
    }
    
    public final boolean isTestOnBorrow() {
        return this.getTestOnBorrow();
    }
    
    public boolean getTestOnBorrow() {
        return this._testOnBorrow;
    }
    
    public void setTestOnBorrow(final boolean testOnBorrow) {
        this.assertInitializationAllowed();
        this._testOnBorrow = testOnBorrow;
        this.testPositionSet = true;
    }
    
    public final boolean isTestOnReturn() {
        return this.getTestOnReturn();
    }
    
    public boolean getTestOnReturn() {
        return this._testOnReturn;
    }
    
    public void setTestOnReturn(final boolean testOnReturn) {
        this.assertInitializationAllowed();
        this._testOnReturn = testOnReturn;
        this.testPositionSet = true;
    }
    
    public int getTimeBetweenEvictionRunsMillis() {
        return this._timeBetweenEvictionRunsMillis;
    }
    
    public void setTimeBetweenEvictionRunsMillis(final int timeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
    
    public int getNumTestsPerEvictionRun() {
        return this._numTestsPerEvictionRun;
    }
    
    public void setNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
    }
    
    public int getMinEvictableIdleTimeMillis() {
        return this._minEvictableIdleTimeMillis;
    }
    
    public void setMinEvictableIdleTimeMillis(final int minEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    
    public final boolean isTestWhileIdle() {
        return this.getTestWhileIdle();
    }
    
    public boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }
    
    public void setTestWhileIdle(final boolean testWhileIdle) {
        this.assertInitializationAllowed();
        this._testWhileIdle = testWhileIdle;
        this.testPositionSet = true;
    }
    
    public String getValidationQuery() {
        return this.validationQuery;
    }
    
    public void setValidationQuery(final String validationQuery) {
        this.assertInitializationAllowed();
        this.validationQuery = validationQuery;
        if (!this.testPositionSet) {
            this.setTestOnBorrow(true);
        }
    }
    
    public boolean isRollbackAfterValidation() {
        return this.rollbackAfterValidation;
    }
    
    public void setRollbackAfterValidation(final boolean rollbackAfterValidation) {
        this.assertInitializationAllowed();
        this.rollbackAfterValidation = rollbackAfterValidation;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection(null, null);
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        if (this.instanceKey == null) {
            throw new SQLException("Must set the ConnectionPoolDataSource through setDataSourceName or setConnectionPoolDataSource before calling getConnection.");
        }
        this.getConnectionCalled = true;
        PooledConnectionAndInfo info = null;
        try {
            info = this.getPooledConnectionAndInfo(username, password);
        }
        catch (NoSuchElementException e) {
            this.closeDueToException(info);
            throw new SQLNestedException("Cannot borrow connection from pool", e);
        }
        catch (RuntimeException e2) {
            this.closeDueToException(info);
            throw e2;
        }
        catch (SQLException e3) {
            this.closeDueToException(info);
            throw e3;
        }
        catch (Exception e4) {
            this.closeDueToException(info);
            throw new SQLNestedException("Cannot borrow connection from pool", e4);
        }
        Label_0338: {
            if (null == password) {
                if (null == info.getPassword()) {
                    break Label_0338;
                }
            }
            else if (password.equals(info.getPassword())) {
                break Label_0338;
            }
            try {
                this.testCPDS(username, password);
            }
            catch (SQLException ex2) {
                this.closeDueToException(info);
                throw new SQLException("Given password did not match password used to create the PooledConnection.");
            }
            catch (NamingException ne) {
                throw (SQLException)new SQLException("NamingException encountered connecting to database").initCause(ne);
            }
            final UserPassKey upkey = info.getUserPassKey();
            final PooledConnectionManager manager = this.getConnectionManager(upkey);
            manager.invalidate(info.getPooledConnection());
            manager.setPassword(upkey.getPassword());
            info = null;
            for (int i = 0; i < 10; ++i) {
                try {
                    info = this.getPooledConnectionAndInfo(username, password);
                }
                catch (NoSuchElementException e5) {
                    this.closeDueToException(info);
                    throw new SQLNestedException("Cannot borrow connection from pool", e5);
                }
                catch (RuntimeException e6) {
                    this.closeDueToException(info);
                    throw e6;
                }
                catch (SQLException e7) {
                    this.closeDueToException(info);
                    throw e7;
                }
                catch (Exception e8) {
                    this.closeDueToException(info);
                    throw new SQLNestedException("Cannot borrow connection from pool", e8);
                }
                if (info != null && password.equals(info.getPassword())) {
                    break;
                }
                if (info != null) {
                    manager.invalidate(info.getPooledConnection());
                }
                info = null;
            }
            if (info == null) {
                throw new SQLException("Cannot borrow connection from pool - password change failure.");
            }
        }
        final Connection con = info.getPooledConnection().getConnection();
        try {
            this.setupDefaults(con, username);
            con.clearWarnings();
            return con;
        }
        catch (SQLException ex) {
            try {
                con.close();
            }
            catch (Exception exc) {
                this.getLogWriter().println("ignoring exception during close: " + exc);
            }
            throw ex;
        }
    }
    
    protected abstract PooledConnectionAndInfo getPooledConnectionAndInfo(final String p0, final String p1) throws SQLException;
    
    protected abstract void setupDefaults(final Connection p0, final String p1) throws SQLException;
    
    private void closeDueToException(final PooledConnectionAndInfo info) {
        if (info != null) {
            try {
                info.getPooledConnection().getConnection().close();
            }
            catch (Exception e) {
                this.getLogWriter().println("[ERROR] Could not return connection to pool during exception handling. " + e.getMessage());
            }
        }
    }
    
    protected ConnectionPoolDataSource testCPDS(final String username, final String password) throws NamingException, SQLException {
        ConnectionPoolDataSource cpds = this.dataSource;
        if (cpds == null) {
            Context ctx = null;
            if (this.jndiEnvironment == null) {
                ctx = new InitialContext();
            }
            else {
                ctx = new InitialContext(this.jndiEnvironment);
            }
            final Object ds = ctx.lookup(this.dataSourceName);
            if (!(ds instanceof ConnectionPoolDataSource)) {
                throw new SQLException("Illegal configuration: DataSource " + this.dataSourceName + " (" + ds.getClass().getName() + ")" + " doesn't implement javax.sql.ConnectionPoolDataSource");
            }
            cpds = (ConnectionPoolDataSource)ds;
        }
        PooledConnection conn = null;
        try {
            if (username != null) {
                conn = cpds.getPooledConnection(username, password);
            }
            else {
                conn = cpds.getPooledConnection();
            }
            if (conn == null) {
                throw new SQLException("Cannot connect using the supplied username/password");
            }
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex) {}
            }
        }
        return cpds;
    }
    
    protected byte whenExhaustedAction(final int maxActive, final int maxWait) {
        byte whenExhausted = 1;
        if (maxActive <= 0) {
            whenExhausted = 2;
        }
        else if (maxWait == 0) {
            whenExhausted = 0;
        }
        return whenExhausted;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final String className = this.getClass().getName();
        final String factoryName = className + "Factory";
        final Reference ref = new Reference(className, factoryName, null);
        ref.add(new StringRefAddr("instanceKey", this.instanceKey));
        return ref;
    }
}
