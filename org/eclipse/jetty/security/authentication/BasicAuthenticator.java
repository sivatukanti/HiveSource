// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.server.UserIdentity;
import java.io.IOException;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.util.B64Code;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.http.HttpHeader;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public class BasicAuthenticator extends LoginAuthenticator
{
    @Override
    public String getAuthMethod() {
        return "BASIC";
    }
    
    @Override
    public Authentication validateRequest(final ServletRequest req, final ServletResponse res, final boolean mandatory) throws ServerAuthException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());
        try {
            if (!mandatory) {
                return new DeferredAuthentication(this);
            }
            if (credentials != null) {
                final int space = credentials.indexOf(32);
                if (space > 0) {
                    final String method = credentials.substring(0, space);
                    if ("basic".equalsIgnoreCase(method)) {
                        credentials = credentials.substring(space + 1);
                        credentials = B64Code.decode(credentials, StandardCharsets.ISO_8859_1);
                        final int i = credentials.indexOf(58);
                        if (i > 0) {
                            final String username = credentials.substring(0, i);
                            final String password = credentials.substring(i + 1);
                            final UserIdentity user = this.login(username, password, request);
                            if (user != null) {
                                return new UserAuthentication(this.getAuthMethod(), user);
                            }
                        }
                    }
                }
            }
            if (DeferredAuthentication.isDeferred(response)) {
                return Authentication.UNAUTHENTICATED;
            }
            response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), "basic realm=\"" + this._loginService.getName() + '\"');
            response.sendError(401);
            return Authentication.SEND_CONTINUE;
        }
        catch (IOException e) {
            throw new ServerAuthException(e);
        }
    }
    
    @Override
    public boolean secureResponse(final ServletRequest req, final ServletResponse res, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        return true;
    }
}
