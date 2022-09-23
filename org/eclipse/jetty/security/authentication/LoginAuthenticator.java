// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.session.AbstractSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.security.Authenticator;

public abstract class LoginAuthenticator implements Authenticator
{
    private static final Logger LOG;
    protected LoginService _loginService;
    protected IdentityService _identityService;
    private boolean _renewSession;
    
    protected LoginAuthenticator() {
    }
    
    @Override
    public void prepareRequest(final ServletRequest request) {
    }
    
    public UserIdentity login(final String username, final Object password, final ServletRequest request) {
        final UserIdentity user = this._loginService.login(username, password, request);
        if (user != null) {
            this.renewSession((HttpServletRequest)request, (request instanceof Request) ? ((Request)request).getResponse() : null);
            return user;
        }
        return null;
    }
    
    @Override
    public void setConfiguration(final AuthConfiguration configuration) {
        this._loginService = configuration.getLoginService();
        if (this._loginService == null) {
            throw new IllegalStateException("No LoginService for " + this + " in " + configuration);
        }
        this._identityService = configuration.getIdentityService();
        if (this._identityService == null) {
            throw new IllegalStateException("No IdentityService for " + this + " in " + configuration);
        }
        this._renewSession = configuration.isSessionRenewedOnAuthentication();
    }
    
    public LoginService getLoginService() {
        return this._loginService;
    }
    
    protected HttpSession renewSession(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession httpSession = request.getSession(false);
        if (this._renewSession && httpSession != null) {
            synchronized (httpSession) {
                if (httpSession.getAttribute("org.eclipse.jetty.security.sessionCreatedSecure") != Boolean.TRUE) {
                    if (httpSession instanceof AbstractSession) {
                        final AbstractSession abstractSession = (AbstractSession)httpSession;
                        final String oldId = abstractSession.getId();
                        abstractSession.renewId(request);
                        abstractSession.setAttribute("org.eclipse.jetty.security.sessionCreatedSecure", Boolean.TRUE);
                        if (abstractSession.isIdChanged() && response != null && response instanceof Response) {
                            ((Response)response).addCookie(abstractSession.getSessionManager().getSessionCookie(abstractSession, request.getContextPath(), request.isSecure()));
                        }
                        LoginAuthenticator.LOG.debug("renew {}->{}", oldId, abstractSession.getId());
                    }
                    else {
                        LoginAuthenticator.LOG.warn("Unable to renew session " + httpSession, new Object[0]);
                    }
                    return httpSession;
                }
            }
        }
        return httpSession;
    }
    
    static {
        LOG = Log.getLogger(LoginAuthenticator.class);
    }
}
