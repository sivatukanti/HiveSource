// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.util.Set;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public interface Authenticator
{
    void setConfiguration(final AuthConfiguration p0);
    
    String getAuthMethod();
    
    void prepareRequest(final ServletRequest p0);
    
    Authentication validateRequest(final ServletRequest p0, final ServletResponse p1, final boolean p2) throws ServerAuthException;
    
    boolean secureResponse(final ServletRequest p0, final ServletResponse p1, final boolean p2, final Authentication.User p3) throws ServerAuthException;
    
    public interface Factory
    {
        Authenticator getAuthenticator(final Server p0, final ServletContext p1, final AuthConfiguration p2, final IdentityService p3, final LoginService p4);
    }
    
    public interface AuthConfiguration
    {
        String getAuthMethod();
        
        String getRealmName();
        
        String getInitParameter(final String p0);
        
        Set<String> getInitParameterNames();
        
        LoginService getLoginService();
        
        IdentityService getIdentityService();
        
        boolean isSessionRenewedOnAuthentication();
    }
}
