// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.logging.Log;

public final class AuthChallengeProcessor
{
    private static final Log LOG;
    private HttpParams params;
    
    public AuthChallengeProcessor(final HttpParams params) {
        this.params = null;
        if (params == null) {
            throw new IllegalArgumentException("Parameter collection may not be null");
        }
        this.params = params;
    }
    
    public AuthScheme selectAuthScheme(final Map challenges) throws AuthChallengeException {
        if (challenges == null) {
            throw new IllegalArgumentException("Challenge map may not be null");
        }
        Collection authPrefs = (Collection)this.params.getParameter("http.auth.scheme-priority");
        if (authPrefs == null || authPrefs.isEmpty()) {
            authPrefs = AuthPolicy.getDefaultAuthPrefs();
        }
        if (AuthChallengeProcessor.LOG.isDebugEnabled()) {
            AuthChallengeProcessor.LOG.debug("Supported authentication schemes in the order of preference: " + authPrefs);
        }
        AuthScheme authscheme = null;
        String challenge = null;
        for (final String id : authPrefs) {
            challenge = challenges.get(id.toLowerCase());
            if (challenge != null) {
                if (AuthChallengeProcessor.LOG.isInfoEnabled()) {
                    AuthChallengeProcessor.LOG.info(id + " authentication scheme selected");
                }
                try {
                    authscheme = AuthPolicy.getAuthScheme(id);
                    break;
                }
                catch (IllegalStateException e) {
                    throw new AuthChallengeException(e.getMessage());
                }
            }
            if (AuthChallengeProcessor.LOG.isDebugEnabled()) {
                AuthChallengeProcessor.LOG.debug("Challenge for " + id + " authentication scheme not available");
            }
        }
        if (authscheme == null) {
            throw new AuthChallengeException("Unable to respond to any of these challenges: " + challenges);
        }
        return authscheme;
    }
    
    public AuthScheme processChallenge(final AuthState state, final Map challenges) throws MalformedChallengeException, AuthenticationException {
        if (state == null) {
            throw new IllegalArgumentException("Authentication state may not be null");
        }
        if (challenges == null) {
            throw new IllegalArgumentException("Challenge map may not be null");
        }
        if (state.isPreemptive() || state.getAuthScheme() == null) {
            state.setAuthScheme(this.selectAuthScheme(challenges));
        }
        final AuthScheme authscheme = state.getAuthScheme();
        final String id = authscheme.getSchemeName();
        if (AuthChallengeProcessor.LOG.isDebugEnabled()) {
            AuthChallengeProcessor.LOG.debug("Using authentication scheme: " + id);
        }
        final String challenge = challenges.get(id.toLowerCase());
        if (challenge == null) {
            throw new AuthenticationException(id + " authorization challenge expected, but not found");
        }
        authscheme.processChallenge(challenge);
        AuthChallengeProcessor.LOG.debug("Authorization challenge processed");
        return authscheme;
    }
    
    static {
        LOG = LogFactory.getLog(AuthChallengeProcessor.class);
    }
}
