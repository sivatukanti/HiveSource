// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.eclipse.jetty.util.security.Credential;
import java.util.ArrayList;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.ServletRequest;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.InputStream;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Properties;
import java.io.IOException;
import java.sql.Connection;
import org.eclipse.jetty.util.log.Logger;

public class JDBCLoginService extends MappedLoginService
{
    private static final Logger LOG;
    protected String _config;
    protected String _jdbcDriver;
    protected String _url;
    protected String _userName;
    protected String _password;
    protected String _userTableKey;
    protected String _userTablePasswordField;
    protected String _roleTableRoleField;
    protected int _cacheTime;
    protected long _lastHashPurge;
    protected Connection _con;
    protected String _userSql;
    protected String _roleSql;
    
    public JDBCLoginService() throws IOException {
    }
    
    public JDBCLoginService(final String name) throws IOException {
        this.setName(name);
    }
    
    public JDBCLoginService(final String name, final String config) throws IOException {
        this.setName(name);
        this.setConfig(config);
    }
    
    public JDBCLoginService(final String name, final IdentityService identityService, final String config) throws IOException {
        this.setName(name);
        this.setIdentityService(identityService);
        this.setConfig(config);
    }
    
    @Override
    protected void doStart() throws Exception {
        final Properties properties = new Properties();
        final Resource resource = Resource.newResource(this._config);
        final InputStream in = resource.getInputStream();
        Throwable x0 = null;
        try {
            properties.load(in);
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (in != null) {
                $closeResource(x0, in);
            }
        }
        this._jdbcDriver = properties.getProperty("jdbcdriver");
        this._url = properties.getProperty("url");
        this._userName = properties.getProperty("username");
        this._password = properties.getProperty("password");
        final String _userTable = properties.getProperty("usertable");
        this._userTableKey = properties.getProperty("usertablekey");
        final String _userTableUserField = properties.getProperty("usertableuserfield");
        this._userTablePasswordField = properties.getProperty("usertablepasswordfield");
        final String _roleTable = properties.getProperty("roletable");
        final String _roleTableKey = properties.getProperty("roletablekey");
        this._roleTableRoleField = properties.getProperty("roletablerolefield");
        final String _userRoleTable = properties.getProperty("userroletable");
        final String _userRoleTableUserKey = properties.getProperty("userroletableuserkey");
        final String _userRoleTableRoleKey = properties.getProperty("userroletablerolekey");
        this._cacheTime = new Integer(properties.getProperty("cachetime"));
        if (this._jdbcDriver == null || this._jdbcDriver.equals("") || this._url == null || this._url.equals("") || this._userName == null || this._userName.equals("") || this._password == null || this._cacheTime < 0) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " has not been properly configured", new Object[0]);
        }
        this._cacheTime *= 1000;
        this._lastHashPurge = 0L;
        this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + _userTable + " where " + _userTableUserField + " = ?";
        this._roleSql = "select r." + this._roleTableRoleField + " from " + _roleTable + " r, " + _userRoleTable + " u where u." + _userRoleTableUserKey + " = ? and r." + _roleTableKey + " = u." + _userRoleTableRoleKey;
        Loader.loadClass(this.getClass(), this._jdbcDriver).newInstance();
        super.doStart();
    }
    
    public String getConfig() {
        return this._config;
    }
    
    public void setConfig(final String config) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._config = config;
    }
    
    public void connectDatabase() {
        try {
            Class.forName(this._jdbcDriver);
            this._con = DriverManager.getConnection(this._url, this._userName, this._password);
        }
        catch (SQLException e) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " could not connect to database; will try later", e);
        }
        catch (ClassNotFoundException e2) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " could not connect to database; will try later", e2);
        }
    }
    
    @Override
    public UserIdentity login(final String username, final Object credentials, final ServletRequest request) {
        final long now = System.currentTimeMillis();
        if (now - this._lastHashPurge > this._cacheTime || this._cacheTime == 0) {
            this._users.clear();
            this._lastHashPurge = now;
            this.closeConnection();
        }
        return super.login(username, credentials, request);
    }
    
    @Override
    protected void loadUsers() {
    }
    
    @Deprecated
    @Override
    protected UserIdentity loadUser(final String username) {
        try {
            if (null == this._con) {
                this.connectDatabase();
            }
            if (null == this._con) {
                throw new SQLException("Can't connect to database");
            }
            final PreparedStatement stat1 = this._con.prepareStatement(this._userSql);
            Throwable x0 = null;
            try {
                stat1.setObject(1, username);
                final ResultSet rs1 = stat1.executeQuery();
                Throwable x2 = null;
                try {
                    if (rs1.next()) {
                        final int key = rs1.getInt(this._userTableKey);
                        final String credentials = rs1.getString(this._userTablePasswordField);
                        final List<String> roles = new ArrayList<String>();
                        final PreparedStatement stat2 = this._con.prepareStatement(this._roleSql);
                        Throwable x3 = null;
                        try {
                            stat2.setInt(1, key);
                            final ResultSet rs2 = stat2.executeQuery();
                            Throwable x4 = null;
                            try {
                                while (rs2.next()) {
                                    roles.add(rs2.getString(this._roleTableRoleField));
                                }
                            }
                            catch (Throwable t) {
                                x4 = t;
                                throw t;
                            }
                            finally {
                                if (rs2 != null) {
                                    $closeResource(x4, rs2);
                                }
                            }
                        }
                        catch (Throwable t2) {
                            x3 = t2;
                            throw t2;
                        }
                        finally {
                            if (stat2 != null) {
                                $closeResource(x3, stat2);
                            }
                        }
                        return this.putUser(username, Credential.getCredential(credentials), roles.toArray(new String[roles.size()]));
                    }
                }
                catch (Throwable t3) {
                    x2 = t3;
                    throw t3;
                }
                finally {
                    if (rs1 != null) {
                        $closeResource(x2, rs1);
                    }
                }
            }
            catch (Throwable t4) {
                x0 = t4;
                throw t4;
            }
            finally {
                if (stat1 != null) {
                    $closeResource(x0, stat1);
                }
            }
        }
        catch (SQLException e) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " could not load user information from database", e);
            this.closeConnection();
        }
        return null;
    }
    
    public KnownUser loadUserInfo(final String username) {
        try {
            if (null == this._con) {
                this.connectDatabase();
            }
            if (null == this._con) {
                throw new SQLException("Can't connect to database");
            }
            final PreparedStatement stat1 = this._con.prepareStatement(this._userSql);
            Throwable x0 = null;
            try {
                stat1.setObject(1, username);
                final ResultSet rs1 = stat1.executeQuery();
                Throwable x2 = null;
                try {
                    if (rs1.next()) {
                        final int key = rs1.getInt(this._userTableKey);
                        final String credentials = rs1.getString(this._userTablePasswordField);
                        return new JDBCKnownUser(username, Credential.getCredential(credentials), key);
                    }
                }
                catch (Throwable t) {
                    x2 = t;
                    throw t;
                }
                finally {
                    if (rs1 != null) {
                        $closeResource(x2, rs1);
                    }
                }
            }
            catch (Throwable t2) {
                x0 = t2;
                throw t2;
            }
            finally {
                if (stat1 != null) {
                    $closeResource(x0, stat1);
                }
            }
        }
        catch (SQLException e) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " could not load user information from database", e);
            this.closeConnection();
        }
        return null;
    }
    
    public String[] loadRoleInfo(final KnownUser user) {
        final JDBCKnownUser jdbcUser = (JDBCKnownUser)user;
        try {
            if (null == this._con) {
                this.connectDatabase();
            }
            if (null == this._con) {
                throw new SQLException("Can't connect to database");
            }
            final List<String> roles = new ArrayList<String>();
            final PreparedStatement stat2 = this._con.prepareStatement(this._roleSql);
            Throwable x0 = null;
            try {
                stat2.setInt(1, jdbcUser.getUserKey());
                final ResultSet rs2 = stat2.executeQuery();
                Throwable x2 = null;
                try {
                    while (rs2.next()) {
                        roles.add(rs2.getString(this._roleTableRoleField));
                    }
                    return roles.toArray(new String[roles.size()]);
                }
                catch (Throwable t) {
                    x2 = t;
                    throw t;
                }
                finally {
                    if (rs2 != null) {
                        $closeResource(x2, rs2);
                    }
                }
            }
            catch (Throwable t2) {
                x0 = t2;
                throw t2;
            }
            finally {
                if (stat2 != null) {
                    $closeResource(x0, stat2);
                }
            }
        }
        catch (SQLException e) {
            JDBCLoginService.LOG.warn("UserRealm " + this.getName() + " could not load user information from database", e);
            this.closeConnection();
            return null;
        }
    }
    
    private void closeConnection() {
        if (this._con != null) {
            if (JDBCLoginService.LOG.isDebugEnabled()) {
                JDBCLoginService.LOG.debug("Closing db connection for JDBCUserRealm", new Object[0]);
            }
            try {
                this._con.close();
            }
            catch (Exception e) {
                JDBCLoginService.LOG.ignore(e);
            }
        }
        this._con = null;
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(JDBCLoginService.class);
    }
    
    public class JDBCKnownUser extends KnownUser
    {
        int _userKey;
        
        public JDBCKnownUser(final String name, final Credential credential, final int key) {
            super(name, credential);
            this._userKey = key;
        }
        
        public int getUserKey() {
            return this._userKey;
        }
    }
}
