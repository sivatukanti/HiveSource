// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.Loader;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.sql.DriverManager;
import java.sql.Connection;
import org.eclipse.jetty.util.log.Logger;

public class JDBCLoginModule extends AbstractDatabaseLoginModule
{
    private static final Logger LOG;
    private String dbDriver;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    
    @Override
    public Connection getConnection() throws Exception {
        if (this.dbDriver == null || this.dbUrl == null) {
            throw new IllegalStateException("Database connection information not configured");
        }
        if (JDBCLoginModule.LOG.isDebugEnabled()) {
            JDBCLoginModule.LOG.debug("Connecting using dbDriver=" + this.dbDriver + "+ dbUserName=" + this.dbUserName + ", dbPassword=" + this.dbUrl, new Object[0]);
        }
        return DriverManager.getConnection(this.dbUrl, this.dbUserName, this.dbPassword);
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        try {
            super.initialize(subject, callbackHandler, sharedState, options);
            this.dbDriver = (String)options.get("dbDriver");
            this.dbUrl = (String)options.get("dbUrl");
            this.dbUserName = (String)options.get("dbUserName");
            this.dbPassword = (String)options.get("dbPassword");
            if (this.dbUserName == null) {
                this.dbUserName = "";
            }
            if (this.dbPassword == null) {
                this.dbPassword = "";
            }
            if (this.dbDriver != null) {
                Loader.loadClass(this.getClass(), this.dbDriver).newInstance();
            }
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.toString());
        }
        catch (InstantiationException e2) {
            throw new IllegalStateException(e2.toString());
        }
        catch (IllegalAccessException e3) {
            throw new IllegalStateException(e3.toString());
        }
    }
    
    static {
        LOG = Log.getLogger(JDBCLoginModule.class);
    }
}
