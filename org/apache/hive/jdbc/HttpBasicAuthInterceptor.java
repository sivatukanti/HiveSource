// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.http.Header;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.impl.auth.BasicScheme;
import java.util.Map;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.auth.UsernamePasswordCredentials;

public class HttpBasicAuthInterceptor extends HttpRequestInterceptorBase
{
    UsernamePasswordCredentials credentials;
    AuthSchemeBase authScheme;
    
    public HttpBasicAuthInterceptor(final String username, final String password, final CookieStore cookieStore, final String cn, final boolean isSSL, final Map<String, String> additionalHeaders) {
        super(cookieStore, cn, isSSL, additionalHeaders);
        this.authScheme = new BasicScheme();
        if (username != null) {
            this.credentials = new UsernamePasswordCredentials(username, password);
        }
    }
    
    @Override
    protected void addHttpAuthHeader(final HttpRequest httpRequest, final HttpContext httpContext) throws Exception {
        final Header basicAuthHeader = this.authScheme.authenticate(this.credentials, httpRequest, httpContext);
        httpRequest.addHeader(basicAuthHeader);
    }
}
