// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;

public class ClientCertAuthenticator implements Authenticator
{
    private int _maxHandShakeSeconds;
    
    public ClientCertAuthenticator() {
        this._maxHandShakeSeconds = 60;
    }
    
    public int getMaxHandShakeSeconds() {
        return this._maxHandShakeSeconds;
    }
    
    public void setMaxHandShakeSeconds(final int maxHandShakeSeconds) {
        this._maxHandShakeSeconds = maxHandShakeSeconds;
    }
    
    public Principal authenticate(final UserRealm realm, final String pathInContext, final Request request, final Response response) throws IOException {
        final X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs == null || certs.length == 0 || certs[0] == null) {
            if (response != null) {
                response.sendError(403, "A client certificate is required for accessing this web application but the server's listener is not configured for mutual authentication (or the client did not provide a certificate).");
            }
            return null;
        }
        Principal principal = certs[0].getSubjectDN();
        if (principal == null) {
            principal = certs[0].getIssuerDN();
        }
        final String username = (principal == null) ? "clientcert" : principal.getName();
        final Principal user = realm.authenticate(username, certs, request);
        if (user == null) {
            if (response != null) {
                response.sendError(403, "The provided client certificate does not correspond to a trusted user.");
            }
            return null;
        }
        request.setAuthType("CLIENT_CERT");
        request.setUserPrincipal(user);
        return user;
    }
    
    public String getAuthMethod() {
        return "CLIENT_CERT";
    }
}
