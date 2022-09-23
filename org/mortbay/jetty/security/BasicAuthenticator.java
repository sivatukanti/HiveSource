// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.io.IOException;
import org.mortbay.util.StringUtil;
import org.mortbay.log.Log;
import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;

public class BasicAuthenticator implements Authenticator
{
    public Principal authenticate(final UserRealm realm, final String pathInContext, final Request request, final Response response) throws IOException {
        Principal user = null;
        String credentials = request.getHeader("Authorization");
        if (credentials != null) {
            try {
                if (Log.isDebugEnabled()) {
                    Log.debug("Credentials: " + credentials);
                }
                credentials = credentials.substring(credentials.indexOf(32) + 1);
                credentials = B64Code.decode(credentials, StringUtil.__ISO_8859_1);
                final int i = credentials.indexOf(58);
                final String username = credentials.substring(0, i);
                final String password = credentials.substring(i + 1);
                user = realm.authenticate(username, password, request);
                if (user == null) {
                    Log.warn("AUTH FAILURE: user {}", StringUtil.printable(username));
                }
                else {
                    request.setAuthType("BASIC");
                    request.setUserPrincipal(user);
                }
            }
            catch (Exception e) {
                Log.warn("AUTH FAILURE: " + e.toString());
                Log.ignore(e);
            }
        }
        if (user == null && response != null) {
            this.sendChallenge(realm, response);
        }
        return user;
    }
    
    public String getAuthMethod() {
        return "BASIC";
    }
    
    public void sendChallenge(final UserRealm realm, final Response response) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm.getName() + '\"');
        response.sendError(401);
    }
}
