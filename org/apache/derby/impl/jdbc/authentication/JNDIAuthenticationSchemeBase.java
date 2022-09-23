// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.authentication.UserAuthenticator;

public abstract class JNDIAuthenticationSchemeBase implements UserAuthenticator
{
    protected final JNDIAuthenticationService authenticationService;
    protected String providerURL;
    private AccessFactory store;
    protected Properties initDirContextEnv;
    
    public JNDIAuthenticationSchemeBase(final JNDIAuthenticationService authenticationService, final Properties initDirContextEnv) {
        this.authenticationService = authenticationService;
        this.setInitDirContextEnv(initDirContextEnv);
        this.setJNDIProviderProperties();
    }
    
    protected abstract void setJNDIProviderProperties();
    
    private void setInitDirContextEnv(final Properties properties) {
        this.initDirContextEnv = new Properties();
        if (properties != null) {
            final Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                final String s = (String)propertyNames.nextElement();
                if (s.startsWith("java.naming.")) {
                    this.initDirContextEnv.put(s, properties.getProperty(s));
                }
            }
        }
    }
    
    protected static final SQLException getLoginSQLException(final Exception ex) {
        return new SQLException(MessageService.getTextMessage("08004", ex), "08004", 40000);
    }
}
