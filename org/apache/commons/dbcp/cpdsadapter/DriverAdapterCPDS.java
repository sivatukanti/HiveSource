// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.cpdsadapter;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import org.apache.commons.pool.KeyedObjectPool;
import java.sql.DriverManager;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import java.sql.SQLException;
import javax.sql.PooledConnection;
import java.util.Properties;
import java.io.PrintWriter;
import javax.naming.spi.ObjectFactory;
import java.io.Serializable;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;

public class DriverAdapterCPDS implements ConnectionPoolDataSource, Referenceable, Serializable, ObjectFactory
{
    private static final long serialVersionUID = -4820523787212147844L;
    private static final String GET_CONNECTION_CALLED = "A PooledConnection was already requested from this source, further initialization is not allowed.";
    private String description;
    private String password;
    private String url;
    private String user;
    private String driver;
    private int loginTimeout;
    private transient PrintWriter logWriter;
    private boolean poolPreparedStatements;
    private int maxActive;
    private int maxIdle;
    private int _timeBetweenEvictionRunsMillis;
    private int _numTestsPerEvictionRun;
    private int _minEvictableIdleTimeMillis;
    private int _maxPreparedStatements;
    private volatile boolean getConnectionCalled;
    private Properties connectionProperties;
    private boolean accessToUnderlyingConnectionAllowed;
    
    public DriverAdapterCPDS() {
        this.logWriter = null;
        this.maxActive = 10;
        this.maxIdle = 10;
        this._timeBetweenEvictionRunsMillis = -1;
        this._numTestsPerEvictionRun = -1;
        this._minEvictableIdleTimeMillis = -1;
        this._maxPreparedStatements = -1;
        this.getConnectionCalled = false;
        this.connectionProperties = null;
        this.accessToUnderlyingConnectionAllowed = false;
    }
    
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection(this.getUser(), this.getPassword());
    }
    
    @Override
    public PooledConnection getPooledConnection(final String username, final String pass) throws SQLException {
        this.getConnectionCalled = true;
        KeyedObjectPool stmtPool = null;
        if (this.isPoolPreparedStatements()) {
            if (this.getMaxPreparedStatements() <= 0) {
                stmtPool = new GenericKeyedObjectPool(null, this.getMaxActive(), (byte)2, 0L, this.getMaxIdle(), false, false, this.getTimeBetweenEvictionRunsMillis(), this.getNumTestsPerEvictionRun(), this.getMinEvictableIdleTimeMillis(), false);
            }
            else {
                stmtPool = new GenericKeyedObjectPool(null, this.getMaxActive(), (byte)2, 0L, this.getMaxIdle(), this.getMaxPreparedStatements(), false, false, -1L, 0, 0L, false);
            }
        }
        try {
            PooledConnectionImpl pci = null;
            if (this.connectionProperties != null) {
                this.connectionProperties.put("user", username);
                this.connectionProperties.put("password", pass);
                pci = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties), stmtPool);
            }
            else {
                pci = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), username, pass), stmtPool);
            }
            pci.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
            return pci;
        }
        catch (ClassCircularityError e) {
            PooledConnectionImpl pci2 = null;
            if (this.connectionProperties != null) {
                pci2 = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties), stmtPool);
            }
            else {
                pci2 = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), username, pass), stmtPool);
            }
            pci2.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
            return pci2;
        }
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final String factory = this.getClass().getName();
        final Reference ref = new Reference(this.getClass().getName(), factory, null);
        ref.add(new StringRefAddr("description", this.getDescription()));
        ref.add(new StringRefAddr("driver", this.getDriver()));
        ref.add(new StringRefAddr("loginTimeout", String.valueOf(this.getLoginTimeout())));
        ref.add(new StringRefAddr("password", this.getPassword()));
        ref.add(new StringRefAddr("user", this.getUser()));
        ref.add(new StringRefAddr("url", this.getUrl()));
        ref.add(new StringRefAddr("poolPreparedStatements", String.valueOf(this.isPoolPreparedStatements())));
        ref.add(new StringRefAddr("maxActive", String.valueOf(this.getMaxActive())));
        ref.add(new StringRefAddr("maxIdle", String.valueOf(this.getMaxIdle())));
        ref.add(new StringRefAddr("timeBetweenEvictionRunsMillis", String.valueOf(this.getTimeBetweenEvictionRunsMillis())));
        ref.add(new StringRefAddr("numTestsPerEvictionRun", String.valueOf(this.getNumTestsPerEvictionRun())));
        ref.add(new StringRefAddr("minEvictableIdleTimeMillis", String.valueOf(this.getMinEvictableIdleTimeMillis())));
        ref.add(new StringRefAddr("maxPreparedStatements", String.valueOf(this.getMaxPreparedStatements())));
        return ref;
    }
    
    @Override
    public Object getObjectInstance(final Object refObj, final Name name, final Context context, final Hashtable env) throws Exception {
        DriverAdapterCPDS cpds = null;
        if (refObj instanceof Reference) {
            final Reference ref = (Reference)refObj;
            if (ref.getClassName().equals(this.getClass().getName())) {
                RefAddr ra = ref.get("description");
                if (ra != null && ra.getContent() != null) {
                    this.setDescription(ra.getContent().toString());
                }
                ra = ref.get("driver");
                if (ra != null && ra.getContent() != null) {
                    this.setDriver(ra.getContent().toString());
                }
                ra = ref.get("url");
                if (ra != null && ra.getContent() != null) {
                    this.setUrl(ra.getContent().toString());
                }
                ra = ref.get("user");
                if (ra != null && ra.getContent() != null) {
                    this.setUser(ra.getContent().toString());
                }
                ra = ref.get("password");
                if (ra != null && ra.getContent() != null) {
                    this.setPassword(ra.getContent().toString());
                }
                ra = ref.get("poolPreparedStatements");
                if (ra != null && ra.getContent() != null) {
                    this.setPoolPreparedStatements(Boolean.valueOf(ra.getContent().toString()));
                }
                ra = ref.get("maxActive");
                if (ra != null && ra.getContent() != null) {
                    this.setMaxActive(Integer.parseInt(ra.getContent().toString()));
                }
                ra = ref.get("maxIdle");
                if (ra != null && ra.getContent() != null) {
                    this.setMaxIdle(Integer.parseInt(ra.getContent().toString()));
                }
                ra = ref.get("timeBetweenEvictionRunsMillis");
                if (ra != null && ra.getContent() != null) {
                    this.setTimeBetweenEvictionRunsMillis(Integer.parseInt(ra.getContent().toString()));
                }
                ra = ref.get("numTestsPerEvictionRun");
                if (ra != null && ra.getContent() != null) {
                    this.setNumTestsPerEvictionRun(Integer.parseInt(ra.getContent().toString()));
                }
                ra = ref.get("minEvictableIdleTimeMillis");
                if (ra != null && ra.getContent() != null) {
                    this.setMinEvictableIdleTimeMillis(Integer.parseInt(ra.getContent().toString()));
                }
                ra = ref.get("maxPreparedStatements");
                if (ra != null && ra.getContent() != null) {
                    this.setMaxPreparedStatements(Integer.parseInt(ra.getContent().toString()));
                }
                cpds = this;
            }
        }
        return cpds;
    }
    
    private void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException("A PooledConnection was already requested from this source, further initialization is not allowed.");
        }
    }
    
    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }
    
    public void setConnectionProperties(final Properties props) {
        this.assertInitializationAllowed();
        this.connectionProperties = props;
        if (this.connectionProperties.containsKey("user")) {
            this.setUser(this.connectionProperties.getProperty("user"));
        }
        if (this.connectionProperties.containsKey("password")) {
            this.setPassword(this.connectionProperties.getProperty("password"));
        }
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String v) {
        this.description = v;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String v) {
        this.assertInitializationAllowed();
        this.password = v;
        if (this.connectionProperties != null) {
            this.connectionProperties.setProperty("password", v);
        }
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String v) {
        this.assertInitializationAllowed();
        this.url = v;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String v) {
        this.assertInitializationAllowed();
        this.user = v;
        if (this.connectionProperties != null) {
            this.connectionProperties.setProperty("user", v);
        }
    }
    
    public String getDriver() {
        return this.driver;
    }
    
    public void setDriver(final String v) throws ClassNotFoundException {
        this.assertInitializationAllowed();
        Class.forName(this.driver = v);
    }
    
    @Override
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
        this.loginTimeout = seconds;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
        this.logWriter = out;
    }
    
    public boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }
    
    public void setPoolPreparedStatements(final boolean v) {
        this.assertInitializationAllowed();
        this.poolPreparedStatements = v;
    }
    
    public int getMaxActive() {
        return this.maxActive;
    }
    
    public void setMaxActive(final int maxActive) {
        this.assertInitializationAllowed();
        this.maxActive = maxActive;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.assertInitializationAllowed();
        this.maxIdle = maxIdle;
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
    
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    public int getMaxPreparedStatements() {
        return this._maxPreparedStatements;
    }
    
    public void setMaxPreparedStatements(final int maxPreparedStatements) {
        this._maxPreparedStatements = maxPreparedStatements;
    }
    
    static {
        DriverManager.getDrivers();
    }
}
