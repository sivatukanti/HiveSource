// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import javax.servlet.http.Cookie;
import org.mortbay.jetty.webapp.WebAppContext;
import java.security.Principal;
import org.mortbay.log.Log;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;
import java.security.SecureRandom;
import java.util.Random;
import java.util.HashMap;

public class HashSSORealm implements SSORealm
{
    public static final String SSO_COOKIE_NAME = "SSO_ID";
    private HashMap _ssoId2Principal;
    private HashMap _ssoUsername2Id;
    private HashMap _ssoPrincipal2Credential;
    private transient Random _random;
    
    public HashSSORealm() {
        this._ssoId2Principal = new HashMap();
        this._ssoUsername2Id = new HashMap();
        this._ssoPrincipal2Credential = new HashMap();
        this._random = new SecureRandom();
    }
    
    public Credential getSingleSignOn(final Request request, final Response response) {
        String ssoID = null;
        final Cookie[] cookies = request.getCookies();
        for (int i = 0; cookies != null && i < cookies.length; ++i) {
            if (cookies[i].getName().equals("SSO_ID")) {
                ssoID = cookies[i].getValue();
                break;
            }
        }
        if (Log.isDebugEnabled()) {
            Log.debug("get ssoID=" + ssoID);
        }
        Principal principal = null;
        Credential credential = null;
        synchronized (this._ssoId2Principal) {
            principal = this._ssoId2Principal.get(ssoID);
            credential = this._ssoPrincipal2Credential.get(principal);
        }
        if (Log.isDebugEnabled()) {
            Log.debug("SSO principal=" + principal);
        }
        if (principal != null && credential != null) {
            final UserRealm realm = ((WebAppContext)request.getContext().getContextHandler()).getSecurityHandler().getUserRealm();
            final Principal authPrincipal = realm.authenticate(principal.getName(), credential, request);
            if (authPrincipal != null) {
                request.setUserPrincipal(authPrincipal);
                return credential;
            }
            synchronized (this._ssoId2Principal) {
                this._ssoId2Principal.remove(ssoID);
                this._ssoPrincipal2Credential.remove(principal);
                this._ssoUsername2Id.remove(principal.getName());
            }
        }
        return null;
    }
    
    public void setSingleSignOn(final Request request, final Response response, final Principal principal, final Credential credential) {
        String ssoID = null;
        synchronized (this._ssoId2Principal) {
            do {
                ssoID = Long.toString(Math.abs(this._random.nextLong()), 30 + (int)(System.currentTimeMillis() % 7L));
            } while (this._ssoId2Principal.containsKey(ssoID));
            if (Log.isDebugEnabled()) {
                Log.debug("set ssoID=" + ssoID);
            }
            this._ssoId2Principal.put(ssoID, principal);
            this._ssoPrincipal2Credential.put(principal, credential);
            this._ssoUsername2Id.put(principal.getName(), ssoID);
        }
        final Cookie cookie = new Cookie("SSO_ID", ssoID);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
    public void clearSingleSignOn(final String username) {
        synchronized (this._ssoId2Principal) {
            final Object ssoID = this._ssoUsername2Id.remove(username);
            final Object principal = this._ssoId2Principal.remove(ssoID);
            this._ssoPrincipal2Credential.remove(principal);
        }
    }
}
