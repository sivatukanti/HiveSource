// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.ServletRequest;

public interface LoginService
{
    String getName();
    
    UserIdentity login(final String p0, final Object p1, final ServletRequest p2);
    
    boolean validate(final UserIdentity p0);
    
    IdentityService getIdentityService();
    
    void setIdentityService(final IdentityService p0);
    
    void logout(final UserIdentity p0);
}
