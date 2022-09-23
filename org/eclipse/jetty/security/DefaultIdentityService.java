// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.UserIdentity;

public class DefaultIdentityService implements IdentityService
{
    @Override
    public Object associate(final UserIdentity user) {
        return null;
    }
    
    @Override
    public void disassociate(final Object previous) {
    }
    
    @Override
    public Object setRunAs(final UserIdentity user, final RunAsToken token) {
        return token;
    }
    
    @Override
    public void unsetRunAs(final Object lastToken) {
    }
    
    @Override
    public RunAsToken newRunAsToken(final String runAsName) {
        return new RoleRunAsToken(runAsName);
    }
    
    @Override
    public UserIdentity getSystemUserIdentity() {
        return null;
    }
    
    @Override
    public UserIdentity newUserIdentity(final Subject subject, final Principal userPrincipal, final String[] roles) {
        return new DefaultUserIdentity(subject, userPrincipal, roles);
    }
}
