// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.Driver;
import java.util.Map;
import java.sql.SQLException;
import org.datanucleus.util.StringUtils;
import java.sql.Connection;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Properties;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;
import javax.sql.DataSource;

public class DriverManagerDataSource implements DataSource
{
    private static final Localiser LOCALISER;
    private final String driverName;
    private final String url;
    private final ClassLoaderResolver clr;
    private final String userName;
    private final String password;
    private final Properties props;
    
    public DriverManagerDataSource(final String driverName, final String url, final String userName, final String password, final ClassLoaderResolver clr, final Properties props) {
        this.driverName = driverName;
        this.url = url;
        this.clr = clr;
        this.userName = userName;
        this.password = password;
        this.props = props;
        if (driverName != null) {
            try {
                clr.classForName(driverName).newInstance();
            }
            catch (Exception e) {
                try {
                    Class.forName(driverName).newInstance();
                }
                catch (Exception e2) {
                    throw new NucleusUserException(DriverManagerDataSource.LOCALISER.msg("047006", driverName), e).setFatal();
                }
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (StringUtils.isWhitespace(this.driverName)) {
            throw new NucleusUserException(DriverManagerDataSource.LOCALISER.msg("047007"));
        }
        return this.getConnection(this.userName, this.password);
    }
    
    @Override
    public Connection getConnection(final String userName, final String password) throws SQLException {
        try {
            final Properties info = new Properties();
            if (userName != null) {
                info.put("user", this.userName);
            }
            if (password != null) {
                info.put("password", this.password);
            }
            if (this.props != null) {
                info.putAll(this.props);
            }
            return this.clr.classForName(this.driverName).newInstance().connect(this.url, info);
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e2) {
            try {
                return DriverManager.getConnection(this.url, this.userName, this.password);
            }
            catch (Exception e3) {
                throw new NucleusUserException(DriverManagerDataSource.LOCALISER.msg("047006", this.driverName), e2).setFatal();
            }
        }
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
        DriverManager.setLogWriter(out);
    }
    
    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DriverManagerDataSource)) {
            return false;
        }
        final DriverManagerDataSource dmds = (DriverManagerDataSource)obj;
        if (this.driverName == null) {
            if (dmds.driverName != null) {
                return false;
            }
        }
        else if (!this.driverName.equals(dmds.driverName)) {
            return false;
        }
        if (this.url == null) {
            if (dmds.url != null) {
                return false;
            }
        }
        else if (!this.url.equals(dmds.url)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return ((this.driverName == null) ? 0 : this.driverName.hashCode()) ^ ((this.url == null) ? 0 : this.url.hashCode());
    }
    
    @Override
    public Object unwrap(final Class iface) throws SQLException {
        if (!DataSource.class.equals(iface)) {
            throw new SQLException("DataSource of type [" + this.getClass().getName() + "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName() + "]");
        }
        return this;
    }
    
    @Override
    public boolean isWrapperFor(final Class iface) throws SQLException {
        return DataSource.class.equals(iface);
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
