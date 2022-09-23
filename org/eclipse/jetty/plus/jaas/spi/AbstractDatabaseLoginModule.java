// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import org.eclipse.jetty.util.log.Log;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.eclipse.jetty.util.security.Credential;
import java.util.ArrayList;
import java.sql.Connection;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractDatabaseLoginModule extends AbstractLoginModule
{
    private static final Logger LOG;
    private String userQuery;
    private String rolesQuery;
    private String dbUserTable;
    private String dbUserTableUserField;
    private String dbUserTableCredentialField;
    private String dbUserRoleTable;
    private String dbUserRoleTableUserField;
    private String dbUserRoleTableRoleField;
    
    public abstract Connection getConnection() throws Exception;
    
    @Override
    public UserInfo getUserInfo(final String userName) throws Exception {
        Connection connection = null;
        try {
            connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(this.userQuery);
            statement.setString(1, userName);
            ResultSet results = statement.executeQuery();
            String dbCredential = null;
            if (results.next()) {
                dbCredential = results.getString(1);
            }
            results.close();
            statement.close();
            statement = connection.prepareStatement(this.rolesQuery);
            statement.setString(1, userName);
            results = statement.executeQuery();
            final List<String> roles = new ArrayList<String>();
            while (results.next()) {
                final String roleName = results.getString(1);
                roles.add(roleName);
            }
            results.close();
            statement.close();
            return (dbCredential == null) ? null : new UserInfo(userName, Credential.getCredential(dbCredential), roles);
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.dbUserTable = (String)options.get("userTable");
        this.dbUserTableUserField = (String)options.get("userField");
        this.dbUserTableCredentialField = (String)options.get("credentialField");
        this.userQuery = "select " + this.dbUserTableCredentialField + " from " + this.dbUserTable + " where " + this.dbUserTableUserField + "=?";
        this.dbUserRoleTable = (String)options.get("userRoleTable");
        this.dbUserRoleTableUserField = (String)options.get("userRoleUserField");
        this.dbUserRoleTableRoleField = (String)options.get("userRoleRoleField");
        this.rolesQuery = "select " + this.dbUserRoleTableRoleField + " from " + this.dbUserRoleTable + " where " + this.dbUserRoleTableUserField + "=?";
        if (AbstractDatabaseLoginModule.LOG.isDebugEnabled()) {
            AbstractDatabaseLoginModule.LOG.debug("userQuery = " + this.userQuery, new Object[0]);
        }
        if (AbstractDatabaseLoginModule.LOG.isDebugEnabled()) {
            AbstractDatabaseLoginModule.LOG.debug("rolesQuery = " + this.rolesQuery, new Object[0]);
        }
    }
    
    static {
        LOG = Log.getLogger(AbstractDatabaseLoginModule.class);
    }
}
