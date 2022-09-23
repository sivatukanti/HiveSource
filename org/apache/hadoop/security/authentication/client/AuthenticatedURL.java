// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.net.CookieHandler;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.HttpCookie;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;

public class AuthenticatedURL
{
    private static final Logger LOG;
    public static final String AUTH_COOKIE = "hadoop.auth";
    private static Class<? extends Authenticator> DEFAULT_AUTHENTICATOR;
    private Authenticator authenticator;
    private ConnectionConfigurator connConfigurator;
    
    public static void setDefaultAuthenticator(final Class<? extends Authenticator> authenticator) {
        AuthenticatedURL.DEFAULT_AUTHENTICATOR = authenticator;
    }
    
    public static Class<? extends Authenticator> getDefaultAuthenticator() {
        return AuthenticatedURL.DEFAULT_AUTHENTICATOR;
    }
    
    public AuthenticatedURL() {
        this(null);
    }
    
    public AuthenticatedURL(final Authenticator authenticator) {
        this(authenticator, null);
    }
    
    public AuthenticatedURL(final Authenticator authenticator, final ConnectionConfigurator connConfigurator) {
        try {
            this.authenticator = ((authenticator != null) ? authenticator : AuthenticatedURL.DEFAULT_AUTHENTICATOR.newInstance());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.connConfigurator = connConfigurator;
        this.authenticator.setConnectionConfigurator(connConfigurator);
    }
    
    protected Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    public HttpURLConnection openConnection(final URL url, final Token token) throws IOException, AuthenticationException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be NULL");
        }
        if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
            throw new IllegalArgumentException("url must be for a HTTP or HTTPS resource");
        }
        if (token == null) {
            throw new IllegalArgumentException("token cannot be NULL");
        }
        this.authenticator.authenticate(url, token);
        return token.openConnection(url, this.connConfigurator);
    }
    
    public static void injectToken(final HttpURLConnection conn, final Token token) {
        final HttpCookie authCookie = token.cookieHandler.getAuthCookie();
        if (authCookie != null) {
            conn.addRequestProperty("Cookie", authCookie.toString());
        }
    }
    
    public static void extractToken(final HttpURLConnection conn, final Token token) throws IOException, AuthenticationException {
        final int respCode = conn.getResponseCode();
        if (respCode == 200 || respCode == 201 || respCode == 202) {
            token.cookieHandler.put(null, conn.getHeaderFields());
            return;
        }
        if (respCode == 404) {
            AuthenticatedURL.LOG.trace("Setting token value to null ({}), resp={}", token, respCode);
            token.set(null);
            throw new FileNotFoundException(conn.getURL().toString());
        }
        AuthenticatedURL.LOG.trace("Setting token value to null ({}), resp={}", token, respCode);
        token.set(null);
        throw new AuthenticationException("Authentication failed, URL: " + conn.getURL() + ", status: " + conn.getResponseCode() + ", message: " + conn.getResponseMessage());
    }
    
    static {
        LOG = LoggerFactory.getLogger(AuthenticatedURL.class);
        AuthenticatedURL.DEFAULT_AUTHENTICATOR = KerberosAuthenticator.class;
    }
    
    private static class AuthCookieHandler extends CookieHandler
    {
        private HttpCookie authCookie;
        private Map<String, List<String>> cookieHeaders;
        
        private AuthCookieHandler() {
            this.cookieHeaders = Collections.emptyMap();
        }
        
        @Override
        public synchronized Map<String, List<String>> get(final URI uri, final Map<String, List<String>> requestHeaders) throws IOException {
            this.getAuthCookie();
            return this.cookieHeaders;
        }
        
        @Override
        public void put(final URI uri, final Map<String, List<String>> responseHeaders) {
            final List<String> headers = responseHeaders.get("Set-Cookie");
            if (headers != null) {
                for (final String header : headers) {
                    List<HttpCookie> cookies;
                    try {
                        cookies = HttpCookie.parse(header);
                    }
                    catch (IllegalArgumentException iae) {
                        AuthenticatedURL.LOG.debug("Cannot parse cookie header: " + header, iae);
                        continue;
                    }
                    for (final HttpCookie cookie : cookies) {
                        if ("hadoop.auth".equals(cookie.getName())) {
                            this.setAuthCookie(cookie);
                        }
                    }
                }
            }
        }
        
        private synchronized HttpCookie getAuthCookie() {
            if (this.authCookie != null && this.authCookie.hasExpired()) {
                this.setAuthCookie(null);
            }
            return this.authCookie;
        }
        
        private synchronized void setAuthCookie(final HttpCookie cookie) {
            final HttpCookie oldCookie = this.authCookie;
            this.authCookie = null;
            this.cookieHeaders = Collections.emptyMap();
            boolean valid = cookie != null && !cookie.getValue().isEmpty() && !cookie.hasExpired();
            if (valid) {
                final long maxAge = cookie.getMaxAge();
                if (maxAge != -1L) {
                    cookie.setMaxAge(maxAge * 9L / 10L);
                    valid = !cookie.hasExpired();
                }
            }
            if (valid) {
                if (cookie.getVersion() == 0) {
                    String value = cookie.getValue();
                    if (!value.startsWith("\"")) {
                        value = "\"" + value + "\"";
                        cookie.setValue(value);
                    }
                }
                this.authCookie = cookie;
                (this.cookieHeaders = new HashMap<String, List<String>>()).put("Cookie", Arrays.asList(cookie.toString()));
            }
            AuthenticatedURL.LOG.trace("Setting token value to {} ({})", this.authCookie, oldCookie);
        }
        
        private void setAuthCookieValue(final String value) {
            HttpCookie c = null;
            if (value != null) {
                c = new HttpCookie("hadoop.auth", value);
            }
            this.setAuthCookie(c);
        }
    }
    
    public static class Token
    {
        private final AuthCookieHandler cookieHandler;
        
        public Token() {
            this.cookieHandler = new AuthCookieHandler();
        }
        
        public Token(final String tokenStr) {
            this.cookieHandler = new AuthCookieHandler();
            if (tokenStr == null) {
                throw new IllegalArgumentException("tokenStr cannot be null");
            }
            this.set(tokenStr);
        }
        
        public boolean isSet() {
            return this.cookieHandler.getAuthCookie() != null;
        }
        
        void set(final String tokenStr) {
            this.cookieHandler.setAuthCookieValue(tokenStr);
        }
        
        HttpURLConnection openConnection(final URL url, final ConnectionConfigurator connConfigurator) throws IOException {
            HttpURLConnection conn;
            synchronized (CookieHandler.class) {
                final CookieHandler current = CookieHandler.getDefault();
                CookieHandler.setDefault(this.cookieHandler);
                try {
                    conn = (HttpURLConnection)url.openConnection();
                }
                finally {
                    CookieHandler.setDefault(current);
                }
            }
            if (connConfigurator != null) {
                connConfigurator.configure(conn);
            }
            return conn;
        }
        
        @Override
        public String toString() {
            String value = "";
            final HttpCookie authCookie = this.cookieHandler.getAuthCookie();
            if (authCookie != null) {
                value = authCookie.getValue();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
            }
            return value;
        }
    }
}
