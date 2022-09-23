// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.authentication.UserAuthenticator;

public final class NoneAuthenticationServiceImpl extends AuthenticationServiceBase implements UserAuthenticator
{
    public boolean canSupport(final Properties properties) {
        return !this.requireAuthentication(properties);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        this.setAuthenticationService(this);
    }
    
    public boolean authenticateUser(final String s, final String s2, final String s3, final Properties properties) {
        return true;
    }
}
