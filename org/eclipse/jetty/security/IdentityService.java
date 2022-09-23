// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.UserIdentity;

public interface IdentityService
{
    public static final String[] NO_ROLES = new String[0];
    
    Object associate(final UserIdentity p0);
    
    void disassociate(final Object p0);
    
    Object setRunAs(final UserIdentity p0, final RunAsToken p1);
    
    void unsetRunAs(final Object p0);
    
    UserIdentity newUserIdentity(final Subject p0, final Principal p1, final String[] p2);
    
    RunAsToken newRunAsToken(final String p0);
    
    UserIdentity getSystemUserIdentity();
}
