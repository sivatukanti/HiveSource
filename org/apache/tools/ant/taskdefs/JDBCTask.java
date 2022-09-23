// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.sql.Driver;
import java.util.Iterator;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.sql.Connection;
import org.apache.tools.ant.types.Reference;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.AntClassLoader;
import java.util.Hashtable;
import org.apache.tools.ant.Task;

public abstract class JDBCTask extends Task
{
    private static final int HASH_TABLE_SIZE = 3;
    private static Hashtable<String, AntClassLoader> LOADER_MAP;
    private boolean caching;
    private Path classpath;
    private AntClassLoader loader;
    private boolean autocommit;
    private String driver;
    private String url;
    private String userId;
    private String password;
    private String rdbms;
    private String version;
    private boolean failOnConnectionError;
    private List<Property> connectionProperties;
    
    public JDBCTask() {
        this.caching = true;
        this.autocommit = false;
        this.driver = null;
        this.url = null;
        this.userId = null;
        this.password = null;
        this.rdbms = null;
        this.version = null;
        this.failOnConnectionError = true;
        this.connectionProperties = new ArrayList<Property>();
    }
    
    public void setClasspath(final Path classpath) {
        this.classpath = classpath;
    }
    
    public void setCaching(final boolean enable) {
        this.caching = enable;
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setDriver(final String driver) {
        this.driver = driver.trim();
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setAutocommit(final boolean autocommit) {
        this.autocommit = autocommit;
    }
    
    public void setRdbms(final String rdbms) {
        this.rdbms = rdbms;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void setFailOnConnectionError(final boolean b) {
        this.failOnConnectionError = b;
    }
    
    protected boolean isValidRdbms(final Connection conn) {
        if (this.rdbms == null && this.version == null) {
            return true;
        }
        try {
            final DatabaseMetaData dmd = conn.getMetaData();
            if (this.rdbms != null) {
                final String theVendor = dmd.getDatabaseProductName().toLowerCase();
                this.log("RDBMS = " + theVendor, 3);
                if (theVendor == null || theVendor.indexOf(this.rdbms) < 0) {
                    this.log("Not the required RDBMS: " + this.rdbms, 3);
                    return false;
                }
            }
            if (this.version != null) {
                final String theVersion = dmd.getDatabaseProductVersion().toLowerCase(Locale.ENGLISH);
                this.log("Version = " + theVersion, 3);
                if (theVersion == null || (!theVersion.startsWith(this.version) && theVersion.indexOf(" " + this.version) < 0)) {
                    this.log("Not the required version: \"" + this.version + "\"", 3);
                    return false;
                }
            }
        }
        catch (SQLException e) {
            this.log("Failed to obtain required RDBMS information", 0);
            return false;
        }
        return true;
    }
    
    protected static Hashtable<String, AntClassLoader> getLoaderMap() {
        return JDBCTask.LOADER_MAP;
    }
    
    protected AntClassLoader getLoader() {
        return this.loader;
    }
    
    public void addConnectionProperty(final Property var) {
        this.connectionProperties.add(var);
    }
    
    protected Connection getConnection() throws BuildException {
        if (this.userId == null) {
            throw new BuildException("UserId attribute must be set!", this.getLocation());
        }
        if (this.password == null) {
            throw new BuildException("Password attribute must be set!", this.getLocation());
        }
        if (this.url == null) {
            throw new BuildException("Url attribute must be set!", this.getLocation());
        }
        try {
            this.log("connecting to " + this.getUrl(), 3);
            final Properties info = new Properties();
            info.put("user", this.getUserId());
            info.put("password", this.getPassword());
            for (final Property p : this.connectionProperties) {
                final String name = p.getName();
                final String value = p.getValue();
                if (name == null || value == null) {
                    this.log("Only name/value pairs are supported as connection properties.", 1);
                }
                else {
                    this.log("Setting connection property " + name + " to " + value, 3);
                    info.put(name, value);
                }
            }
            final Connection conn = this.getDriver().connect(this.getUrl(), info);
            if (conn == null) {
                throw new SQLException("No suitable Driver for " + this.url);
            }
            conn.setAutoCommit(this.autocommit);
            return conn;
        }
        catch (SQLException e) {
            if (!this.failOnConnectionError) {
                this.log("Failed to connect: " + e.getMessage(), 1);
                return null;
            }
            throw new BuildException(e, this.getLocation());
        }
    }
    
    private Driver getDriver() throws BuildException {
        if (this.driver == null) {
            throw new BuildException("Driver attribute must be set!", this.getLocation());
        }
        Driver driverInstance = null;
        try {
            Class<?> dc;
            if (this.classpath != null) {
                synchronized (JDBCTask.LOADER_MAP) {
                    if (this.caching) {
                        this.loader = JDBCTask.LOADER_MAP.get(this.driver);
                    }
                    if (this.loader == null) {
                        this.log("Loading " + this.driver + " using AntClassLoader with classpath " + this.classpath, 3);
                        this.loader = this.getProject().createClassLoader(this.classpath);
                        if (this.caching) {
                            JDBCTask.LOADER_MAP.put(this.driver, this.loader);
                        }
                    }
                    else {
                        this.log("Loading " + this.driver + " using a cached AntClassLoader.", 3);
                    }
                }
                dc = this.loader.loadClass(this.driver);
            }
            else {
                this.log("Loading " + this.driver + " using system loader.", 3);
                dc = Class.forName(this.driver);
            }
            driverInstance = (Driver)dc.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("Class Not Found: JDBC driver " + this.driver + " could not be loaded", e, this.getLocation());
        }
        catch (IllegalAccessException e2) {
            throw new BuildException("Illegal Access: JDBC driver " + this.driver + " could not be loaded", e2, this.getLocation());
        }
        catch (InstantiationException e3) {
            throw new BuildException("Instantiation Exception: JDBC driver " + this.driver + " could not be loaded", e3, this.getLocation());
        }
        return driverInstance;
    }
    
    public void isCaching(final boolean value) {
        this.caching = value;
    }
    
    public Path getClasspath() {
        return this.classpath;
    }
    
    public boolean isAutocommit() {
        return this.autocommit;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserid(final String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getRdbms() {
        return this.rdbms;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    static {
        JDBCTask.LOADER_MAP = new Hashtable<String, AntClassLoader>(3);
    }
}
