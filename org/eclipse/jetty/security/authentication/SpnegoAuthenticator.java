// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import java.io.IOException;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.http.HttpHeader;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.util.log.Logger;

public class SpnegoAuthenticator extends LoginAuthenticator
{
    private static final Logger LOG;
    private String _authMethod;
    
    public SpnegoAuthenticator() {
        this._authMethod = "SPNEGO";
    }
    
    public SpnegoAuthenticator(final String authMethod) {
        this._authMethod = "SPNEGO";
        this._authMethod = authMethod;
    }
    
    @Override
    public String getAuthMethod() {
        return this._authMethod;
    }
    
    @Override
    public Authentication validateRequest(final ServletRequest request, final ServletResponse response, final boolean mandatory) throws ServerAuthException {
        final HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse res = (HttpServletResponse)response;
        final String header = req.getHeader(HttpHeader.AUTHORIZATION.asString());
        if (!mandatory) {
            return new DeferredAuthentication(this);
        }
        if (header == null) {
            try {
                if (DeferredAuthentication.isDeferred(res)) {
                    return Authentication.UNAUTHENTICATED;
                }
                SpnegoAuthenticator.LOG.debug("SpengoAuthenticator: sending challenge", new Object[0]);
                res.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), HttpHeader.NEGOTIATE.asString());
                res.sendError(401);
                return Authentication.SEND_CONTINUE;
            }
            catch (IOException ioe) {
                throw new ServerAuthException(ioe);
            }
        }
        if (header != null && header.startsWith(HttpHeader.NEGOTIATE.asString())) {
            final String spnegoToken = header.substring(10);
            final UserIdentity user = this.login(null, spnegoToken, request);
            if (user != null) {
                return new UserAuthentication(this.getAuthMethod(), user);
            }
        }
        return Authentication.UNAUTHENTICATED;
    }
    
    @Override
    public boolean secureResponse(final ServletRequest request, final ServletResponse response, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        return true;
    }
    
    static {
        LOG = Log.getLogger(SpnegoAuthenticator.class);
    }
}
