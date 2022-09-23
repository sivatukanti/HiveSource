// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.security.authentication.ClientCertAuthenticator;
import org.eclipse.jetty.security.authentication.SpnegoAuthenticator;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.Server;

public class DefaultAuthenticatorFactory implements Authenticator.Factory
{
    LoginService _loginService;
    
    @Override
    public Authenticator getAuthenticator(final Server server, final ServletContext context, final Authenticator.AuthConfiguration configuration, final IdentityService identityService, final LoginService loginService) {
        final String auth = configuration.getAuthMethod();
        Authenticator authenticator = null;
        if (auth == null || "BASIC".equalsIgnoreCase(auth)) {
            authenticator = new BasicAuthenticator();
        }
        else if ("DIGEST".equalsIgnoreCase(auth)) {
            authenticator = new DigestAuthenticator();
        }
        else if ("FORM".equalsIgnoreCase(auth)) {
            authenticator = new FormAuthenticator();
        }
        else if ("SPNEGO".equalsIgnoreCase(auth)) {
            authenticator = new SpnegoAuthenticator();
        }
        else if ("NEGOTIATE".equalsIgnoreCase(auth)) {
            authenticator = new SpnegoAuthenticator("NEGOTIATE");
        }
        if ("CLIENT_CERT".equalsIgnoreCase(auth) || "CLIENT-CERT".equalsIgnoreCase(auth)) {
            authenticator = new ClientCertAuthenticator();
        }
        return authenticator;
    }
    
    public LoginService getLoginService() {
        return this._loginService;
    }
    
    public void setLoginService(final LoginService loginService) {
        this._loginService = loginService;
    }
}
