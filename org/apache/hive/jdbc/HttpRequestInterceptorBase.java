// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.io.IOException;
import java.util.Iterator;
import org.apache.http.HttpException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import java.util.Map;
import org.apache.http.client.CookieStore;
import org.apache.http.HttpRequestInterceptor;

public abstract class HttpRequestInterceptorBase implements HttpRequestInterceptor
{
    CookieStore cookieStore;
    boolean isCookieEnabled;
    String cookieName;
    boolean isSSL;
    Map<String, String> additionalHeaders;
    
    protected abstract void addHttpAuthHeader(final HttpRequest p0, final HttpContext p1) throws Exception;
    
    public HttpRequestInterceptorBase(final CookieStore cs, final String cn, final boolean isSSL, final Map<String, String> additionalHeaders) {
        this.cookieStore = cs;
        this.isCookieEnabled = (cs != null);
        this.cookieName = cn;
        this.isSSL = isSSL;
        this.additionalHeaders = additionalHeaders;
    }
    
    @Override
    public void process(final HttpRequest httpRequest, final HttpContext httpContext) throws HttpException, IOException {
        try {
            if (this.isCookieEnabled) {
                httpContext.setAttribute("http.cookie-store", this.cookieStore);
            }
            if (!this.isCookieEnabled || (httpContext.getAttribute("hive.server2.retryserver") == null && (this.cookieStore == null || (this.cookieStore != null && Utils.needToSendCredentials(this.cookieStore, this.cookieName, this.isSSL)))) || (httpContext.getAttribute("hive.server2.retryserver") != null && httpContext.getAttribute("hive.server2.retryserver").equals("true"))) {
                this.addHttpAuthHeader(httpRequest, httpContext);
            }
            if (this.isCookieEnabled) {
                httpContext.setAttribute("hive.server2.retryserver", "false");
            }
            if (this.additionalHeaders != null) {
                for (final Map.Entry<String, String> entry : this.additionalHeaders.entrySet()) {
                    httpRequest.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }
}
