// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.security;

import org.eclipse.jetty.util.log.Log;
import java.sql.DatabaseMetaData;
import javax.naming.NameNotFoundException;
import org.eclipse.jetty.plus.jndi.NamingEntryUtil;
import javax.naming.InitialContext;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import java.util.ArrayList;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.server.Server;
import javax.sql.DataSource;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.security.MappedLoginService;

public class DataSourceLoginService extends MappedLoginService
{
    private static final Logger LOG;
    private String _jndiName;
    private DataSource _datasource;
    private Server _server;
    private String _userTableName;
    private String _userTableKey;
    private String _userTableUserField;
    private String _userTablePasswordField;
    private String _roleTableName;
    private String _roleTableKey;
    private String _roleTableRoleField;
    private String _userRoleTableName;
    private String _userRoleTableUserKey;
    private String _userRoleTableRoleKey;
    private int _cacheMs;
    private String _userSql;
    private String _roleSql;
    private boolean _createTables;
    
    public DataSourceLoginService() {
        this._jndiName = "javax.sql.DataSource/default";
        this._userTableName = "users";
        this._userTableKey = "id";
        this._userTableUserField = "username";
        this._userTablePasswordField = "pwd";
        this._roleTableName = "roles";
        this._roleTableKey = "id";
        this._roleTableRoleField = "role";
        this._userRoleTableName = "user_roles";
        this._userRoleTableUserKey = "user_id";
        this._userRoleTableRoleKey = "role_id";
        this._cacheMs = 30000;
        this._createTables = false;
    }
    
    public DataSourceLoginService(final String name) {
        this._jndiName = "javax.sql.DataSource/default";
        this._userTableName = "users";
        this._userTableKey = "id";
        this._userTableUserField = "username";
        this._userTablePasswordField = "pwd";
        this._roleTableName = "roles";
        this._roleTableKey = "id";
        this._roleTableRoleField = "role";
        this._userRoleTableName = "user_roles";
        this._userRoleTableUserKey = "user_id";
        this._userRoleTableRoleKey = "role_id";
        this._cacheMs = 30000;
        this._createTables = false;
        this.setName(name);
    }
    
    public DataSourceLoginService(final String name, final IdentityService identityService) {
        this._jndiName = "javax.sql.DataSource/default";
        this._userTableName = "users";
        this._userTableKey = "id";
        this._userTableUserField = "username";
        this._userTablePasswordField = "pwd";
        this._roleTableName = "roles";
        this._roleTableKey = "id";
        this._roleTableRoleField = "role";
        this._userRoleTableName = "user_roles";
        this._userRoleTableUserKey = "user_id";
        this._userRoleTableRoleKey = "role_id";
        this._cacheMs = 30000;
        this._createTables = false;
        this.setName(name);
        this.setIdentityService(identityService);
    }
    
    public void setJndiName(final String jndi) {
        this._jndiName = jndi;
    }
    
    public String getJndiName() {
        return this._jndiName;
    }
    
    public void setServer(final Server server) {
        this._server = server;
    }
    
    public Server getServer() {
        return this._server;
    }
    
    public void setCreateTables(final boolean createTables) {
        this._createTables = createTables;
    }
    
    public boolean getCreateTables() {
        return this._createTables;
    }
    
    public void setUserTableName(final String name) {
        this._userTableName = name;
    }
    
    public String getUserTableName() {
        return this._userTableName;
    }
    
    public String getUserTableKey() {
        return this._userTableKey;
    }
    
    public void setUserTableKey(final String tableKey) {
        this._userTableKey = tableKey;
    }
    
    public String getUserTableUserField() {
        return this._userTableUserField;
    }
    
    public void setUserTableUserField(final String tableUserField) {
        this._userTableUserField = tableUserField;
    }
    
    public String getUserTablePasswordField() {
        return this._userTablePasswordField;
    }
    
    public void setUserTablePasswordField(final String tablePasswordField) {
        this._userTablePasswordField = tablePasswordField;
    }
    
    public String getRoleTableName() {
        return this._roleTableName;
    }
    
    public void setRoleTableName(final String tableName) {
        this._roleTableName = tableName;
    }
    
    public String getRoleTableKey() {
        return this._roleTableKey;
    }
    
    public void setRoleTableKey(final String tableKey) {
        this._roleTableKey = tableKey;
    }
    
    public String getRoleTableRoleField() {
        return this._roleTableRoleField;
    }
    
    public void setRoleTableRoleField(final String tableRoleField) {
        this._roleTableRoleField = tableRoleField;
    }
    
    public String getUserRoleTableName() {
        return this._userRoleTableName;
    }
    
    public void setUserRoleTableName(final String roleTableName) {
        this._userRoleTableName = roleTableName;
    }
    
    public String getUserRoleTableUserKey() {
        return this._userRoleTableUserKey;
    }
    
    public void setUserRoleTableUserKey(final String roleTableUserKey) {
        this._userRoleTableUserKey = roleTableUserKey;
    }
    
    public String getUserRoleTableRoleKey() {
        return this._userRoleTableRoleKey;
    }
    
    public void setUserRoleTableRoleKey(final String roleTableRoleKey) {
        this._userRoleTableRoleKey = roleTableRoleKey;
    }
    
    public void setCacheMs(final int ms) {
        this._cacheMs = ms;
    }
    
    public int getCacheMs() {
        return this._cacheMs;
    }
    
    @Override
    protected void loadUsers() {
    }
    
    @Override
    protected UserIdentity loadUser(final String userName) {
        Connection connection = null;
        try {
            this.initDb();
            connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(this._userSql);
            statement.setObject(1, userName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                final int key = rs.getInt(this._userTableKey);
                final String credentials = rs.getString(this._userTablePasswordField);
                statement.close();
                statement = connection.prepareStatement(this._roleSql);
                statement.setInt(1, key);
                rs = statement.executeQuery();
                final List<String> roles = new ArrayList<String>();
                while (rs.next()) {
                    roles.add(rs.getString(this._roleTableRoleField));
                }
                statement.close();
                return this.putUser(userName, new Password(credentials), roles.toArray(new String[roles.size()]));
            }
            return null;
        }
        catch (NamingException e) {
            DataSourceLoginService.LOG.warn("No datasource for " + this._jndiName, e);
        }
        catch (SQLException e2) {
            DataSourceLoginService.LOG.warn("Problem loading user info for " + userName, e2);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException x) {
                    DataSourceLoginService.LOG.warn("Problem closing connection", x);
                    connection = null;
                }
                finally {
                    connection = null;
                }
            }
        }
        return null;
    }
    
    public void initDb() throws NamingException, SQLException {
        if (this._datasource != null) {
            return;
        }
        final InitialContext ic = new InitialContext();
        if (this._server != null) {
            try {
                this._datasource = (DataSource)NamingEntryUtil.lookup(this._server, this._jndiName);
            }
            catch (NameNotFoundException ex) {}
        }
        if (this._datasource == null) {
            this._datasource = (DataSource)NamingEntryUtil.lookup(null, this._jndiName);
        }
        this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + this._userTableName + " where " + this._userTableUserField + " = ?";
        this._roleSql = "select r." + this._roleTableRoleField + " from " + this._roleTableName + " r, " + this._userRoleTableName + " u where u." + this._userRoleTableUserKey + " = ?" + " and r." + this._roleTableKey + " = u." + this._userRoleTableRoleKey;
        this.prepareTables();
    }
    
    private void prepareTables() throws NamingException, SQLException {
        Connection connection = null;
        boolean autocommit = true;
        if (this._createTables) {
            try {
                connection = this.getConnection();
                autocommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                final DatabaseMetaData metaData = connection.getMetaData();
                String tableName = metaData.storesLowerCaseIdentifiers() ? this._userTableName.toLowerCase() : (metaData.storesUpperCaseIdentifiers() ? this._userTableName.toUpperCase() : this._userTableName);
                ResultSet result = metaData.getTables(null, null, tableName, null);
                if (!result.next()) {
                    connection.createStatement().executeUpdate("create table " + this._userTableName + "(" + this._userTableKey + " integer," + this._userTableUserField + " varchar(100) not null unique," + this._userTablePasswordField + " varchar(20) not null, primary key(" + this._userTableKey + "))");
                    if (DataSourceLoginService.LOG.isDebugEnabled()) {
                        DataSourceLoginService.LOG.debug("Created table " + this._userTableName, new Object[0]);
                    }
                }
                result.close();
                tableName = (metaData.storesLowerCaseIdentifiers() ? this._roleTableName.toLowerCase() : (metaData.storesUpperCaseIdentifiers() ? this._roleTableName.toUpperCase() : this._roleTableName));
                result = metaData.getTables(null, null, tableName, null);
                if (!result.next()) {
                    final String str = "create table " + this._roleTableName + " (" + this._roleTableKey + " integer, " + this._roleTableRoleField + " varchar(100) not null unique, primary key(" + this._roleTableKey + "))";
                    connection.createStatement().executeUpdate(str);
                    if (DataSourceLoginService.LOG.isDebugEnabled()) {
                        DataSourceLoginService.LOG.debug("Created table " + this._roleTableName, new Object[0]);
                    }
                }
                result.close();
                tableName = (metaData.storesLowerCaseIdentifiers() ? this._userRoleTableName.toLowerCase() : (metaData.storesUpperCaseIdentifiers() ? this._userRoleTableName.toUpperCase() : this._userRoleTableName));
                result = metaData.getTables(null, null, tableName, null);
                if (!result.next()) {
                    connection.createStatement().executeUpdate("create table " + this._userRoleTableName + " (" + this._userRoleTableUserKey + " integer, " + this._userRoleTableRoleKey + " integer, " + "primary key (" + this._userRoleTableUserKey + ", " + this._userRoleTableRoleKey + "))");
                    connection.createStatement().executeUpdate("create index indx_user_role on " + this._userRoleTableName + "(" + this._userRoleTableUserKey + ")");
                    if (DataSourceLoginService.LOG.isDebugEnabled()) {
                        DataSourceLoginService.LOG.debug("Created table " + this._userRoleTableName + " and index", new Object[0]);
                    }
                }
                result.close();
                connection.commit();
            }
            finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(autocommit);
                        connection.close();
                    }
                    catch (SQLException e) {
                        if (DataSourceLoginService.LOG.isDebugEnabled()) {
                            DataSourceLoginService.LOG.debug("Prepare tables", e);
                        }
                        connection = null;
                    }
                    finally {
                        connection = null;
                    }
                }
            }
        }
        else if (DataSourceLoginService.LOG.isDebugEnabled()) {
            DataSourceLoginService.LOG.debug("createTables false", new Object[0]);
        }
    }
    
    private Connection getConnection() throws NamingException, SQLException {
        this.initDb();
        return this._datasource.getConnection();
    }
    
    static {
        LOG = Log.getLogger(DataSourceLoginService.class);
    }
}
