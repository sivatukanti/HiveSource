// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.util.log.Log;
import java.util.Enumeration;
import org.eclipse.jetty.http.PathMap;
import java.io.InputStream;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import java.io.OutputStream;
import org.eclipse.jetty.client.HttpExchange;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import java.util.HashSet;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.log.Logger;

public class ProxyRule extends PatternRule
{
    private static final Logger _log;
    private HttpClient _client;
    private String _hostHeader;
    private String _proxyTo;
    private int _connectorType;
    private String _maxThreads;
    private String _maxConnections;
    private String _timeout;
    private String _idleTimeout;
    private String _requestHeaderSize;
    private String _requestBufferSize;
    private String _responseHeaderSize;
    private String _responseBufferSize;
    private HashSet<String> _DontProxyHeaders;
    
    public ProxyRule() {
        this._connectorType = 2;
        (this._DontProxyHeaders = new HashSet<String>()).add("proxy-connection");
        this._DontProxyHeaders.add("connection");
        this._DontProxyHeaders.add("keep-alive");
        this._DontProxyHeaders.add("transfer-encoding");
        this._DontProxyHeaders.add("te");
        this._DontProxyHeaders.add("trailer");
        this._DontProxyHeaders.add("proxy-authorization");
        this._DontProxyHeaders.add("proxy-authenticate");
        this._DontProxyHeaders.add("upgrade");
        this._handling = true;
        this._terminating = true;
    }
    
    private void initializeClient() throws Exception {
        (this._client = new HttpClient()).setConnectorType(this._connectorType);
        if (this._maxThreads != null) {
            this._client.setThreadPool(new QueuedThreadPool(Integer.parseInt(this._maxThreads)));
        }
        else {
            this._client.setThreadPool(new QueuedThreadPool());
        }
        if (this._maxConnections != null) {
            this._client.setMaxConnectionsPerAddress(Integer.parseInt(this._maxConnections));
        }
        if (this._timeout != null) {
            this._client.setTimeout(Long.parseLong(this._timeout));
        }
        if (this._idleTimeout != null) {
            this._client.setIdleTimeout(Long.parseLong(this._idleTimeout));
        }
        if (this._requestBufferSize != null) {
            this._client.setRequestBufferSize(Integer.parseInt(this._requestBufferSize));
        }
        if (this._requestHeaderSize != null) {
            this._client.setRequestHeaderSize(Integer.parseInt(this._requestHeaderSize));
        }
        if (this._responseBufferSize != null) {
            this._client.setResponseBufferSize(Integer.parseInt(this._responseBufferSize));
        }
        if (this._responseHeaderSize != null) {
            this._client.setResponseHeaderSize(Integer.parseInt(this._responseHeaderSize));
        }
        this._client.start();
    }
    
    private HttpURI proxyHttpURI(final String uri) throws MalformedURLException {
        return new HttpURI(this._proxyTo + uri);
    }
    
    @Override
    protected String apply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        synchronized (this) {
            if (this._client == null) {
                try {
                    this.initializeClient();
                }
                catch (Exception e) {
                    throw new IOException("Unable to proxy: " + e.getMessage());
                }
            }
        }
        final int debug = ProxyRule._log.isDebugEnabled() ? request.hashCode() : 0;
        final InputStream in = request.getInputStream();
        final OutputStream out = response.getOutputStream();
        final HttpURI url = this.createUrl(request, debug);
        if (url == null) {
            response.sendError(403);
            return target;
        }
        final HttpExchange exchange = new HttpExchange() {
            @Override
            protected void onRequestCommitted() throws IOException {
            }
            
            @Override
            protected void onRequestComplete() throws IOException {
            }
            
            @Override
            protected void onResponseComplete() throws IOException {
                if (debug != 0) {
                    ProxyRule._log.debug(debug + " complete", new Object[0]);
                }
            }
            
            @Override
            protected void onResponseContent(final Buffer content) throws IOException {
                if (debug != 0) {
                    ProxyRule._log.debug(debug + " content" + content.length(), new Object[0]);
                }
                content.writeTo(out);
            }
            
            @Override
            protected void onResponseHeaderComplete() throws IOException {
            }
            
            @Override
            protected void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
                if (debug != 0) {
                    ProxyRule._log.debug(debug + " " + version + " " + status + " " + reason, new Object[0]);
                }
                if (reason != null && reason.length() > 0) {
                    response.setStatus(status, reason.toString());
                }
                else {
                    response.setStatus(status);
                }
            }
            
            @Override
            protected void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
                final String s = name.toString().toLowerCase();
                if (!ProxyRule.this._DontProxyHeaders.contains(s) || (HttpHeaders.CONNECTION_BUFFER.equals(name) && HttpHeaderValues.CLOSE_BUFFER.equals(value))) {
                    if (debug != 0) {
                        ProxyRule._log.debug(debug + " " + name + ": " + value, new Object[0]);
                    }
                    response.addHeader(name.toString(), value.toString());
                }
                else if (debug != 0) {
                    ProxyRule._log.debug(debug + " " + name + "! " + value, new Object[0]);
                }
            }
            
            @Override
            protected void onConnectionFailed(final Throwable ex) {
                ProxyRule._log.warn(ex.toString(), new Object[0]);
                ProxyRule._log.debug(ex);
                if (!response.isCommitted()) {
                    response.setStatus(500);
                }
            }
            
            @Override
            protected void onException(final Throwable ex) {
                if (ex instanceof EofException) {
                    ProxyRule._log.ignore(ex);
                    return;
                }
                ProxyRule._log.warn(ex.toString(), new Object[0]);
                ProxyRule._log.debug(ex);
                if (!response.isCommitted()) {
                    response.setStatus(500);
                }
            }
            
            @Override
            protected void onExpire() {
                if (!response.isCommitted()) {
                    response.setStatus(504);
                }
            }
        };
        exchange.setMethod(request.getMethod());
        exchange.setURL(url.toString());
        exchange.setVersion(request.getProtocol());
        if (debug != 0) {
            ProxyRule._log.debug("{} {} {} {}", debug, request.getMethod(), url, request.getProtocol());
        }
        final boolean hasContent = this.createHeaders(request, debug, exchange);
        if (hasContent) {
            exchange.setRequestContentSource(in);
        }
        final long ctimeout = (this._client.getTimeout() > exchange.getTimeout()) ? this._client.getTimeout() : exchange.getTimeout();
        exchange.setTimeout(ctimeout);
        this._client.send(exchange);
        try {
            exchange.waitForDone();
        }
        catch (InterruptedException e2) {
            ProxyRule._log.info("Exception while waiting for response on proxied request", e2);
        }
        return target;
    }
    
    private HttpURI createUrl(final HttpServletRequest request, final int debug) throws MalformedURLException {
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) {
            uri = uri + "?" + request.getQueryString();
        }
        uri = PathMap.pathInfo(this._pattern, uri);
        if (uri == null) {
            uri = "/";
        }
        final HttpURI url = this.proxyHttpURI(uri);
        if (debug != 0) {
            ProxyRule._log.debug(debug + " proxy " + uri + "-->" + url, new Object[0]);
        }
        return url;
    }
    
    private boolean createHeaders(final HttpServletRequest request, final int debug, final HttpExchange exchange) {
        String connectionHdr = request.getHeader("Connection");
        if (connectionHdr != null) {
            connectionHdr = connectionHdr.toLowerCase();
            if (connectionHdr.indexOf("keep-alive") < 0 && connectionHdr.indexOf("close") < 0) {
                connectionHdr = null;
            }
        }
        if (this._hostHeader != null) {
            exchange.setRequestHeader("Host", this._hostHeader);
        }
        boolean xForwardedFor = false;
        boolean hasContent = false;
        long contentLength = -1L;
        final Enumeration<?> enm = request.getHeaderNames();
        while (enm.hasMoreElements()) {
            final String hdr = (String)enm.nextElement();
            final String lhdr = hdr.toLowerCase();
            if (this._DontProxyHeaders.contains(lhdr)) {
                continue;
            }
            if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0) {
                continue;
            }
            if (this._hostHeader != null && "host".equals(lhdr)) {
                continue;
            }
            if ("content-type".equals(lhdr)) {
                hasContent = true;
            }
            else if ("content-length".equals(lhdr)) {
                contentLength = request.getContentLength();
                exchange.setRequestHeader("Content-Length", Long.toString(contentLength));
                if (contentLength > 0L) {
                    hasContent = true;
                }
            }
            else if ("x-forwarded-for".equals(lhdr)) {
                xForwardedFor = true;
            }
            final Enumeration<?> vals = request.getHeaders(hdr);
            while (vals.hasMoreElements()) {
                final String val = (String)vals.nextElement();
                if (val != null) {
                    if (debug != 0) {
                        ProxyRule._log.debug("{} {} {}", debug, hdr, val);
                    }
                    exchange.setRequestHeader(hdr, val);
                }
            }
        }
        exchange.setRequestHeader("Via", "1.1 (jetty)");
        if (!xForwardedFor) {
            exchange.addRequestHeader("X-Forwarded-For", request.getRemoteAddr());
            exchange.addRequestHeader("X-Forwarded-Proto", request.getScheme());
            exchange.addRequestHeader("X-Forwarded-Host", request.getServerName());
            exchange.addRequestHeader("X-Forwarded-Server", request.getLocalName());
        }
        return hasContent;
    }
    
    public void setProxyTo(final String proxyTo) {
        this._proxyTo = proxyTo;
    }
    
    public void setMaxThreads(final String maxThreads) {
        this._maxThreads = maxThreads;
    }
    
    public void setMaxConnections(final String maxConnections) {
        this._maxConnections = maxConnections;
    }
    
    public void setTimeout(final String timeout) {
        this._timeout = timeout;
    }
    
    public void setIdleTimeout(final String idleTimeout) {
        this._idleTimeout = idleTimeout;
    }
    
    public void setRequestHeaderSize(final String requestHeaderSize) {
        this._requestHeaderSize = requestHeaderSize;
    }
    
    public void setRequestBufferSize(final String requestBufferSize) {
        this._requestBufferSize = requestBufferSize;
    }
    
    public void setResponseHeaderSize(final String responseHeaderSize) {
        this._responseHeaderSize = responseHeaderSize;
    }
    
    public void setResponseBufferSize(final String responseBufferSize) {
        this._responseBufferSize = responseBufferSize;
    }
    
    public void addDontProxyHeaders(final String dontProxyHeader) {
        this._DontProxyHeaders.add(dontProxyHeader);
    }
    
    public void setConnectorType(final int connectorType) {
        this._connectorType = connectorType;
    }
    
    public String getHostHeader() {
        return this._hostHeader;
    }
    
    public void setHostHeader(final String hostHeader) {
        this._hostHeader = hostHeader;
    }
    
    static {
        _log = Log.getLogger(ProxyRule.class);
    }
}
