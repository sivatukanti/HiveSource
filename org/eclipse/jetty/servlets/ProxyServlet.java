// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import javax.servlet.UnavailableException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.MalformedURLException;
import org.eclipse.jetty.util.IO;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import org.eclipse.jetty.http.HttpURI;
import java.io.InputStream;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.continuation.ContinuationSupport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.util.HostMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import java.util.HashSet;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.Servlet;

public class ProxyServlet implements Servlet
{
    protected Logger _log;
    protected HttpClient _client;
    protected String _hostHeader;
    protected HashSet<String> _DontProxyHeaders;
    protected ServletConfig _config;
    protected ServletContext _context;
    protected HostMap<PathMap> _white;
    protected HostMap<PathMap> _black;
    
    public ProxyServlet() {
        (this._DontProxyHeaders = new HashSet<String>()).add("proxy-connection");
        this._DontProxyHeaders.add("connection");
        this._DontProxyHeaders.add("keep-alive");
        this._DontProxyHeaders.add("transfer-encoding");
        this._DontProxyHeaders.add("te");
        this._DontProxyHeaders.add("trailer");
        this._DontProxyHeaders.add("proxy-authorization");
        this._DontProxyHeaders.add("proxy-authenticate");
        this._DontProxyHeaders.add("upgrade");
        this._white = new HostMap<PathMap>();
        this._black = new HostMap<PathMap>();
    }
    
    public void init(final ServletConfig config) throws ServletException {
        this._config = config;
        this._context = config.getServletContext();
        this._hostHeader = config.getInitParameter("HostHeader");
        try {
            this._log = this.createLogger(config);
            this._client = this.createHttpClient(config);
            if (this._context != null) {
                this._context.setAttribute(config.getServletName() + ".ThreadPool", this._client.getThreadPool());
                this._context.setAttribute(config.getServletName() + ".HttpClient", this._client);
            }
            final String white = config.getInitParameter("whiteList");
            if (white != null) {
                this.parseList(white, this._white);
            }
            final String black = config.getInitParameter("blackList");
            if (black != null) {
                this.parseList(black, this._black);
            }
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    public void destroy() {
        try {
            this._client.stop();
        }
        catch (Exception x) {
            this._log.debug(x);
        }
    }
    
    protected Logger createLogger(final ServletConfig config) {
        return Log.getLogger("org.eclipse.jetty.servlets." + config.getServletName());
    }
    
    protected HttpClient createHttpClient(final ServletConfig config) throws Exception {
        final HttpClient client = new HttpClient();
        client.setConnectorType(2);
        String t = config.getInitParameter("maxThreads");
        if (t != null) {
            client.setThreadPool(new QueuedThreadPool(Integer.parseInt(t)));
        }
        else {
            client.setThreadPool(new QueuedThreadPool());
        }
        ((QueuedThreadPool)client.getThreadPool()).setName(config.getServletName());
        t = config.getInitParameter("maxConnections");
        if (t != null) {
            client.setMaxConnectionsPerAddress(Integer.parseInt(t));
        }
        t = config.getInitParameter("timeout");
        if (t != null) {
            client.setTimeout(Long.parseLong(t));
        }
        t = config.getInitParameter("idleTimeout");
        if (t != null) {
            client.setIdleTimeout(Long.parseLong(t));
        }
        t = config.getInitParameter("requestHeaderSize");
        if (t != null) {
            client.setRequestHeaderSize(Integer.parseInt(t));
        }
        t = config.getInitParameter("requestBufferSize");
        if (t != null) {
            client.setRequestBufferSize(Integer.parseInt(t));
        }
        t = config.getInitParameter("responseHeaderSize");
        if (t != null) {
            client.setResponseHeaderSize(Integer.parseInt(t));
        }
        t = config.getInitParameter("responseBufferSize");
        if (t != null) {
            client.setResponseBufferSize(Integer.parseInt(t));
        }
        client.start();
        return client;
    }
    
    private void parseList(final String list, final HostMap<PathMap> hostMap) {
        if (list != null && list.length() > 0) {
            final StringTokenizer entries = new StringTokenizer(list, ",");
            while (entries.hasMoreTokens()) {
                final String entry = entries.nextToken();
                final int idx = entry.indexOf(47);
                String host = (idx > 0) ? entry.substring(0, idx) : entry;
                final String path = (idx > 0) ? entry.substring(idx) : "/*";
                host = host.trim();
                PathMap pathMap = hostMap.get(host);
                if (pathMap == null) {
                    pathMap = new PathMap(true);
                    hostMap.put(host, pathMap);
                }
                if (path != null) {
                    pathMap.put((Object)path, path);
                }
            }
        }
    }
    
    public boolean validateDestination(final String host, final String path) {
        if (this._white.size() > 0) {
            boolean match = false;
            final Object whiteObj = this._white.getLazyMatches(host);
            if (whiteObj != null) {
                final List whiteList = (whiteObj instanceof List) ? ((List)whiteObj) : Collections.singletonList(whiteObj);
                for (final Object entry : whiteList) {
                    final PathMap pathMap = ((Map.Entry)entry).getValue();
                    if (match = (pathMap != null && (pathMap.size() == 0 || pathMap.match(path) != null))) {
                        break;
                    }
                }
            }
            if (!match) {
                return false;
            }
        }
        if (this._black.size() > 0) {
            final Object blackObj = this._black.getLazyMatches(host);
            if (blackObj != null) {
                final List blackList = (blackObj instanceof List) ? ((List)blackObj) : Collections.singletonList(blackObj);
                for (final Object entry2 : blackList) {
                    final PathMap pathMap2 = ((Map.Entry)entry2).getValue();
                    if (pathMap2 != null && (pathMap2.size() == 0 || pathMap2.match(path) != null)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public ServletConfig getServletConfig() {
        return this._config;
    }
    
    public String getHostHeader() {
        return this._hostHeader;
    }
    
    public void setHostHeader(final String hostHeader) {
        this._hostHeader = hostHeader;
    }
    
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        final int debug = this._log.isDebugEnabled() ? req.hashCode() : 0;
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
            this.handleConnect(request, response);
        }
        else {
            final InputStream in = request.getInputStream();
            final OutputStream out = response.getOutputStream();
            final Continuation continuation = ContinuationSupport.getContinuation(request);
            if (!continuation.isInitial()) {
                response.sendError(504);
            }
            else {
                String uri = request.getRequestURI();
                if (request.getQueryString() != null) {
                    uri = uri + "?" + request.getQueryString();
                }
                final HttpURI url = this.proxyHttpURI(request.getScheme(), request.getServerName(), request.getServerPort(), uri);
                if (debug != 0) {
                    this._log.debug(debug + " proxy " + uri + "-->" + url, new Object[0]);
                }
                if (url == null) {
                    response.sendError(403);
                    return;
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
                            ProxyServlet.this._log.debug(debug + " complete", new Object[0]);
                        }
                        continuation.complete();
                    }
                    
                    @Override
                    protected void onResponseContent(final Buffer content) throws IOException {
                        if (debug != 0) {
                            ProxyServlet.this._log.debug(debug + " content" + content.length(), new Object[0]);
                        }
                        content.writeTo(out);
                    }
                    
                    @Override
                    protected void onResponseHeaderComplete() throws IOException {
                    }
                    
                    @Override
                    protected void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
                        if (debug != 0) {
                            ProxyServlet.this._log.debug(debug + " " + version + " " + status + " " + reason, new Object[0]);
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
                        if (!ProxyServlet.this._DontProxyHeaders.contains(s) || (HttpHeaders.CONNECTION_BUFFER.equals(name) && HttpHeaderValues.CLOSE_BUFFER.equals(value))) {
                            if (debug != 0) {
                                ProxyServlet.this._log.debug(debug + " " + name + ": " + value, new Object[0]);
                            }
                            response.addHeader(name.toString(), value.toString());
                        }
                        else if (debug != 0) {
                            ProxyServlet.this._log.debug(debug + " " + name + "! " + value, new Object[0]);
                        }
                    }
                    
                    @Override
                    protected void onConnectionFailed(final Throwable ex) {
                        ProxyServlet.this.handleOnConnectionFailed(ex, request, response);
                        if (!continuation.isInitial()) {
                            continuation.complete();
                        }
                    }
                    
                    @Override
                    protected void onException(final Throwable ex) {
                        if (ex instanceof EofException) {
                            ProxyServlet.this._log.ignore(ex);
                            return;
                        }
                        ProxyServlet.this.handleOnException(ex, request, response);
                        if (!continuation.isInitial()) {
                            continuation.complete();
                        }
                    }
                    
                    @Override
                    protected void onExpire() {
                        ProxyServlet.this.handleOnExpire(request, response);
                        continuation.complete();
                    }
                };
                exchange.setScheme("https".equals(request.getScheme()) ? HttpSchemes.HTTPS_BUFFER : HttpSchemes.HTTP_BUFFER);
                exchange.setMethod(request.getMethod());
                exchange.setURL(url.toString());
                exchange.setVersion(request.getProtocol());
                if (debug != 0) {
                    this._log.debug(debug + " " + request.getMethod() + " " + url + " " + request.getProtocol(), new Object[0]);
                }
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
                                this._log.debug(debug + " " + hdr + ": " + val, new Object[0]);
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
                if (hasContent) {
                    exchange.setRequestContentSource(in);
                }
                this.customizeExchange(exchange, request);
                final long ctimeout = (this._client.getTimeout() > exchange.getTimeout()) ? this._client.getTimeout() : exchange.getTimeout();
                if (ctimeout == 0L) {
                    continuation.setTimeout(0L);
                }
                else {
                    continuation.setTimeout(ctimeout + 1000L);
                }
                this.customizeContinuation(continuation);
                continuation.suspend(response);
                this._client.send(exchange);
            }
        }
    }
    
    public void handleConnect(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String uri = request.getRequestURI();
        String port = "";
        String host = "";
        final int c = uri.indexOf(58);
        if (c >= 0) {
            port = uri.substring(c + 1);
            host = uri.substring(0, c);
            if (host.indexOf(47) > 0) {
                host = host.substring(host.indexOf(47) + 1);
            }
        }
        final InetSocketAddress inetAddress = new InetSocketAddress(host, Integer.parseInt(port));
        final InputStream in = request.getInputStream();
        final OutputStream out = response.getOutputStream();
        final Socket socket = new Socket(inetAddress.getAddress(), inetAddress.getPort());
        response.setStatus(200);
        response.setHeader("Connection", "close");
        response.flushBuffer();
        IO.copyThread(socket.getInputStream(), out);
        IO.copy(in, socket.getOutputStream());
    }
    
    protected HttpURI proxyHttpURI(final String scheme, final String serverName, final int serverPort, final String uri) throws MalformedURLException {
        if (!this.validateDestination(serverName, uri)) {
            return null;
        }
        return new HttpURI(scheme + "://" + serverName + ":" + serverPort + uri);
    }
    
    public String getServletInfo() {
        return "Proxy Servlet";
    }
    
    protected void customizeExchange(final HttpExchange exchange, final HttpServletRequest request) {
    }
    
    protected void customizeContinuation(final Continuation continuation) {
    }
    
    protected void handleOnConnectionFailed(final Throwable ex, final HttpServletRequest request, final HttpServletResponse response) {
        this.handleOnException(ex, request, response);
    }
    
    protected void handleOnException(final Throwable ex, final HttpServletRequest request, final HttpServletResponse response) {
        if (ex instanceof IOException) {
            this._log.warn(ex.toString(), new Object[0]);
            this._log.debug(ex);
        }
        else {
            this._log.warn(ex);
        }
        if (!response.isCommitted()) {
            response.setStatus(500);
        }
    }
    
    protected void handleOnExpire(final HttpServletRequest request, final HttpServletResponse response) {
        if (!response.isCommitted()) {
            response.setStatus(504);
        }
    }
    
    public static class Transparent extends ProxyServlet
    {
        String _prefix;
        String _proxyTo;
        
        public Transparent() {
        }
        
        public Transparent(final String prefix, final String host, final int port) {
            this(prefix, "http", host, port, null);
        }
        
        public Transparent(final String prefix, final String schema, final String host, final int port, final String path) {
            try {
                if (prefix != null) {
                    this._prefix = new URI(prefix).normalize().toString();
                }
                this._proxyTo = new URI(schema, null, host, port, path, null, null).normalize().toString();
            }
            catch (URISyntaxException ex) {
                this._log.debug("Invalid URI syntax", ex);
            }
        }
        
        @Override
        public void init(final ServletConfig config) throws ServletException {
            super.init(config);
            final String prefix = config.getInitParameter("Prefix");
            this._prefix = ((prefix == null) ? this._prefix : prefix);
            final String contextPath = this._context.getContextPath();
            this._prefix = ((this._prefix == null) ? contextPath : (contextPath + this._prefix));
            final String proxyTo = config.getInitParameter("ProxyTo");
            this._proxyTo = ((proxyTo == null) ? this._proxyTo : proxyTo);
            if (this._proxyTo == null) {
                throw new UnavailableException("ProxyTo parameter is requred.");
            }
            if (!this._prefix.startsWith("/")) {
                throw new UnavailableException("Prefix parameter must start with a '/'.");
            }
            this._log.info(config.getServletName() + " @ " + this._prefix + " to " + this._proxyTo, new Object[0]);
        }
        
        @Override
        protected HttpURI proxyHttpURI(final String scheme, final String serverName, final int serverPort, final String uri) throws MalformedURLException {
            try {
                if (!uri.startsWith(this._prefix)) {
                    return null;
                }
                final URI dstUri = new URI(this._proxyTo + uri.substring(this._prefix.length())).normalize();
                if (!this.validateDestination(dstUri.getHost(), dstUri.getPath())) {
                    return null;
                }
                return new HttpURI(dstUri.toString());
            }
            catch (URISyntaxException ex) {
                throw new MalformedURLException(ex.getMessage());
            }
        }
    }
}
