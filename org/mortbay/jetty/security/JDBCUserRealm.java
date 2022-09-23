// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.mortbay.jetty.Request;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.security.Principal;
import org.mortbay.log.Log;
import java.util.Properties;
import java.io.IOException;
import org.mortbay.util.Loader;
import java.sql.Connection;

public class JDBCUserRealm extends HashUserRealm implements UserRealm
{
    private String _jdbcDriver;
    private String _url;
    private String _userName;
    private String _password;
    private String _userTable;
    private String _userTableKey;
    private String _userTableUserField;
    private String _userTablePasswordField;
    private String _roleTable;
    private String _roleTableKey;
    private String _roleTableRoleField;
    private String _userRoleTable;
    private String _userRoleTableUserKey;
    private String _userRoleTableRoleKey;
    private int _cacheTime;
    private long _lastHashPurge;
    private Connection _con;
    private String _userSql;
    private String _roleSql;
    
    public JDBCUserRealm() {
    }
    
    public JDBCUserRealm(final String name) {
        super(name);
    }
    
    public JDBCUserRealm(final String name, final String config) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super(name);
        this.setConfig(config);
        Loader.loadClass(this.getClass(), this._jdbcDriver).newInstance();
    }
    
    protected void loadConfig() throws IOException {
        final Properties properties = new Properties();
        properties.load(this.getConfigResource().getInputStream());
        this._jdbcDriver = properties.getProperty("jdbcdriver");
        this._url = properties.getProperty("url");
        this._userName = properties.getProperty("username");
        this._password = properties.getProperty("password");
        this._userTable = properties.getProperty("usertable");
        this._userTableKey = properties.getProperty("usertablekey");
        this._userTableUserField = properties.getProperty("usertableuserfield");
        this._userTablePasswordField = properties.getProperty("usertablepasswordfield");
        this._roleTable = properties.getProperty("roletable");
        this._roleTableKey = properties.getProperty("roletablekey");
        this._roleTableRoleField = properties.getProperty("roletablerolefield");
        this._userRoleTable = properties.getProperty("userroletable");
        this._userRoleTableUserKey = properties.getProperty("userroletableuserkey");
        this._userRoleTableRoleKey = properties.getProperty("userroletablerolekey");
        final String cachetime = properties.getProperty("cachetime");
        this._cacheTime = ((cachetime != null) ? new Integer(cachetime) : 30);
        if ((this._jdbcDriver == null || this._jdbcDriver.equals("") || this._url == null || this._url.equals("") || this._userName == null || this._userName.equals("") || this._password == null || this._cacheTime < 0) && Log.isDebugEnabled()) {
            Log.debug("UserRealm " + this.getName() + " has not been properly configured");
        }
        this._cacheTime *= 1000;
        this._lastHashPurge = 0L;
        this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + this._userTable + " where " + this._userTableUserField + " = ?";
        this._roleSql = "select r." + this._roleTableRoleField + " from " + this._roleTable + " r, " + this._userRoleTable + " u where u." + this._userRoleTableUserKey + " = ?" + " and r." + this._roleTableKey + " = u." + this._userRoleTableRoleKey;
    }
    
    public void logout(final Principal user) {
    }
    
    public void connectDatabase() {
        try {
            Class.forName(this._jdbcDriver);
            this._con = DriverManager.getConnection(this._url, this._userName, this._password);
        }
        catch (SQLException e) {
            Log.warn("UserRealm " + this.getName() + " could not connect to database; will try later", e);
        }
        catch (ClassNotFoundException e2) {
            Log.warn("UserRealm " + this.getName() + " could not connect to database; will try later", e2);
        }
    }
    
    public Principal authenticate(final String username, final Object credentials, final Request request) {
        synchronized (this) {
            final long now = System.currentTimeMillis();
            if (now - this._lastHashPurge > this._cacheTime || this._cacheTime == 0) {
                this._users.clear();
                this._roles.clear();
                this._lastHashPurge = now;
                this.closeConnection();
            }
            Principal user = super.getPrincipal(username);
            if (user == null) {
                this.loadUser(username);
                user = super.getPrincipal(username);
            }
        }
        return super.authenticate(username, credentials, request);
    }
    
    public synchronized boolean isUserInRole(final Principal user, final String roleName) {
        if (super.getPrincipal(user.getName()) == null) {
            this.loadUser(user.getName());
        }
        return super.isUserInRole(user, roleName);
    }
    
    private void loadUser(final String username) {
        try {
            if (null == this._con) {
                this.connectDatabase();
            }
            if (null == this._con) {
                throw new SQLException("Can't connect to database");
            }
            PreparedStatement stat = this._con.prepareStatement(this._userSql);
            stat.setObject(1, username);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                final int key = rs.getInt(this._userTableKey);
                this.put(username, rs.getString(this._userTablePasswordField));
                stat.close();
                stat = this._con.prepareStatement(this._roleSql);
                stat.setInt(1, key);
                rs = stat.executeQuery();
                while (rs.next()) {
                    this.addUserToRole(username, rs.getString(this._roleTableRoleField));
                }
                stat.close();
            }
        }
        catch (SQLException e) {
            Log.warn("UserRealm " + this.getName() + " could not load user information from database", e);
            this.closeConnection();
        }
    }
    
    private void closeConnection() {
        if (this._con != null) {
            if (Log.isDebugEnabled()) {
                Log.debug("Closing db connection for JDBCUserRealm");
            }
            try {
                this._con.close();
            }
            catch (Exception e) {
                Log.ignore(e);
            }
        }
        this._con = null;
    }
}
