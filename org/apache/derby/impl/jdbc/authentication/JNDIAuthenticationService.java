// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.authentication.UserAuthenticator;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;

public class JNDIAuthenticationService extends AuthenticationServiceBase
{
    private String authenticationProvider;
    
    public boolean canSupport(final Properties properties) {
        if (!this.requireAuthentication(properties)) {
            return false;
        }
        this.authenticationProvider = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.provider");
        return this.authenticationProvider != null && StringUtil.SQLEqualsIgnoreCase(this.authenticationProvider, "LDAP");
    }
    
    @Override
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        this.setAuthenticationService(new LDAPAuthenticationSchemeImpl(this, properties));
    }
}
