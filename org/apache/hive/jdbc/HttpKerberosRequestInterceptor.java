// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.http.HttpException;
import org.apache.hive.service.auth.HttpAuthUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import java.util.Map;
import org.apache.http.client.CookieStore;
import java.util.concurrent.locks.ReentrantLock;

public class HttpKerberosRequestInterceptor extends HttpRequestInterceptorBase
{
    String principal;
    String host;
    String serverHttpUrl;
    boolean assumeSubject;
    private static ReentrantLock kerberosLock;
    
    public HttpKerberosRequestInterceptor(final String principal, final String host, final String serverHttpUrl, final boolean assumeSubject, final CookieStore cs, final String cn, final boolean isSSL, final Map<String, String> additionalHeaders) {
        super(cs, cn, isSSL, additionalHeaders);
        this.principal = principal;
        this.host = host;
        this.serverHttpUrl = serverHttpUrl;
        this.assumeSubject = assumeSubject;
    }
    
    @Override
    protected void addHttpAuthHeader(final HttpRequest httpRequest, final HttpContext httpContext) throws Exception {
        try {
            HttpKerberosRequestInterceptor.kerberosLock.lock();
            final String kerberosAuthHeader = HttpAuthUtils.getKerberosServiceTicket(this.principal, this.host, this.serverHttpUrl, this.assumeSubject);
            httpRequest.addHeader("Authorization: Negotiate ", kerberosAuthHeader);
        }
        catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
        finally {
            HttpKerberosRequestInterceptor.kerberosLock.unlock();
        }
    }
    
    static {
        HttpKerberosRequestInterceptor.kerberosLock = new ReentrantLock(true);
    }
}
