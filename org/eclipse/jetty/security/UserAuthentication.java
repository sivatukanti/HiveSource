// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.UserIdentity;

public class UserAuthentication extends AbstractUserAuthentication
{
    public UserAuthentication(final String method, final UserIdentity userIdentity) {
        super(method, userIdentity);
    }
    
    @Override
    public String toString() {
        return "{User," + this.getAuthMethod() + "," + this._userIdentity + "}";
    }
    
    @Override
    public void logout() {
        final SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
        if (security != null) {
            security.logout(this);
        }
    }
}
