// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.authentication.UserAuthenticator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;

public class SpecificAuthenticationServiceImpl extends AuthenticationServiceBase
{
    private String specificAuthenticationScheme;
    
    public boolean canSupport(final Properties properties) {
        if (!this.requireAuthentication(properties)) {
            return false;
        }
        if (PropertyUtil.nativeAuthenticationEnabled(properties)) {
            return false;
        }
        this.specificAuthenticationScheme = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.provider");
        return this.specificAuthenticationScheme != null && this.specificAuthenticationScheme.length() != 0 && !StringUtil.SQLEqualsIgnoreCase(this.specificAuthenticationScheme, "BUILTIN") && !this.specificAuthenticationScheme.equalsIgnoreCase("LDAP");
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        ClassNotFoundException ex;
        try {
            final Class<?> forName = Class.forName(this.specificAuthenticationScheme);
            if (!UserAuthenticator.class.isAssignableFrom(forName)) {
                throw StandardException.newException("XBM0L.D", this.specificAuthenticationScheme, "org.apache.derby.authentication.UserAuthenticator");
            }
            this.setAuthenticationService((UserAuthenticator)forName.newInstance());
            return;
        }
        catch (ClassNotFoundException ex2) {
            ex = ex2;
        }
        catch (InstantiationException ex3) {
            ex = (ClassNotFoundException)ex3;
        }
        catch (IllegalAccessException ex4) {
            ex = (ClassNotFoundException)ex4;
        }
        throw StandardException.newException("XBM0M.D", this.specificAuthenticationScheme, ex.getClass().getName() + ": " + ex.getMessage());
    }
}
