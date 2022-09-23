// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import java.sql.Connection;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import javax.sql.DataSource;

public class DataSourceLoginModule extends AbstractDatabaseLoginModule
{
    private String dbJNDIName;
    private DataSource dataSource;
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        try {
            super.initialize(subject, callbackHandler, sharedState, options);
            this.dbJNDIName = (String)options.get("dbJNDIName");
            final InitialContext ic = new InitialContext();
            this.dataSource = (DataSource)ic.lookup("java:comp/env/" + this.dbJNDIName);
        }
        catch (NamingException e) {
            throw new IllegalStateException(e.toString());
        }
    }
    
    @Override
    public Connection getConnection() throws Exception {
        return this.dataSource.getConnection();
    }
}
