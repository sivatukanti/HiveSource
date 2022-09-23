// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import javax.servlet.UnavailableException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStream;
import org.mortbay.util.IO;
import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import java.util.HashSet;
import javax.servlet.Servlet;

public class ProxyServlet implements Servlet
{
    protected HashSet _DontProxyHeaders;
    protected ServletConfig _config;
    protected ServletContext _context;
    
    public ProxyServlet() {
        (this._DontProxyHeaders = new HashSet()).add("proxy-connection");
        this._DontProxyHeaders.add("connection");
        this._DontProxyHeaders.add("keep-alive");
        this._DontProxyHeaders.add("transfer-encoding");
        this._DontProxyHeaders.add("te");
        this._DontProxyHeaders.add("trailer");
        this._DontProxyHeaders.add("proxy-authorization");
        this._DontProxyHeaders.add("proxy-authenticate");
        this._DontProxyHeaders.add("upgrade");
    }
    
    public void init(final ServletConfig config) throws ServletException {
        this._config = config;
        this._context = config.getServletContext();
    }
    
    public ServletConfig getServletConfig() {
        return this._config;
    }
    
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
            this.handleConnect(request, response);
        }
        else {
            String uri = request.getRequestURI();
            if (request.getQueryString() != null) {
                uri = uri + "?" + request.getQueryString();
            }
            final URL url = this.proxyHttpURL(request.getScheme(), request.getServerName(), request.getServerPort(), uri);
            final URLConnection connection = url.openConnection();
            connection.setAllowUserInteraction(false);
            HttpURLConnection http = null;
            if (connection instanceof HttpURLConnection) {
                http = (HttpURLConnection)connection;
                http.setRequestMethod(request.getMethod());
                http.setInstanceFollowRedirects(false);
            }
            String connectionHdr = request.getHeader("Connection");
            if (connectionHdr != null) {
                connectionHdr = connectionHdr.toLowerCase();
                if (connectionHdr.equals("keep-alive") || connectionHdr.equals("close")) {
                    connectionHdr = null;
                }
            }
            boolean xForwardedFor = false;
            boolean hasContent = false;
            final Enumeration enm = request.getHeaderNames();
            while (enm.hasMoreElements()) {
                final String hdr = enm.nextElement();
                final String lhdr = hdr.toLowerCase();
                if (this._DontProxyHeaders.contains(lhdr)) {
                    continue;
                }
                if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0) {
                    continue;
                }
                if ("content-type".equals(lhdr)) {
                    hasContent = true;
                }
                final Enumeration vals = request.getHeaders(hdr);
                while (vals.hasMoreElements()) {
                    final String val = vals.nextElement();
                    if (val != null) {
                        connection.addRequestProperty(hdr, val);
                        xForwardedFor |= "X-Forwarded-For".equalsIgnoreCase(hdr);
                    }
                }
            }
            connection.setRequestProperty("Via", "1.1 (jetty)");
            if (!xForwardedFor) {
                connection.addRequestProperty("X-Forwarded-For", request.getRemoteAddr());
                connection.addRequestProperty("X-Forwarded-Proto", request.getScheme());
                connection.addRequestProperty("X-Forwarded-Host", request.getServerName());
                connection.addRequestProperty("X-Forwarded-Server", request.getLocalName());
            }
            final String cache_control = request.getHeader("Cache-Control");
            if (cache_control != null && (cache_control.indexOf("no-cache") >= 0 || cache_control.indexOf("no-store") >= 0)) {
                connection.setUseCaches(false);
            }
            try {
                connection.setDoInput(true);
                final InputStream in = request.getInputStream();
                if (hasContent) {
                    connection.setDoOutput(true);
                    IO.copy(in, connection.getOutputStream());
                }
                connection.connect();
            }
            catch (Exception e) {
                this._context.log("proxy", e);
            }
            InputStream proxy_in = null;
            int code = 500;
            if (http != null) {
                proxy_in = http.getErrorStream();
                code = http.getResponseCode();
                response.setStatus(code, http.getResponseMessage());
            }
            if (proxy_in == null) {
                try {
                    proxy_in = connection.getInputStream();
                }
                catch (Exception e2) {
                    this._context.log("stream", e2);
                    proxy_in = http.getErrorStream();
                }
            }
            response.setHeader("Date", null);
            response.setHeader("Server", null);
            int h = 0;
            for (String hdr2 = connection.getHeaderFieldKey(h), val2 = connection.getHeaderField(h); hdr2 != null || val2 != null; hdr2 = connection.getHeaderFieldKey(h), val2 = connection.getHeaderField(h)) {
                final String lhdr2 = (hdr2 != null) ? hdr2.toLowerCase() : null;
                if (hdr2 != null && val2 != null && !this._DontProxyHeaders.contains(lhdr2)) {
                    response.addHeader(hdr2, val2);
                }
                ++h;
            }
            response.addHeader("Via", "1.1 (jetty)");
            if (proxy_in != null) {
                IO.copy(proxy_in, response.getOutputStream());
            }
        }
    }
    
    protected URL proxyHttpURL(final String scheme, final String serverName, final int serverPort, final String uri) throws MalformedURLException {
        return new URL(scheme, serverName, serverPort, uri);
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
    
    public String getServletInfo() {
        return "Proxy Servlet";
    }
    
    public void destroy() {
    }
    
    public static class Transparent extends ProxyServlet
    {
        String _prefix;
        String _proxyTo;
        
        public Transparent() {
        }
        
        public Transparent(final String prefix, final String server, final int port) {
            this._prefix = prefix;
            this._proxyTo = "http://" + server + ":" + port;
        }
        
        public void init(final ServletConfig config) throws ServletException {
            if (config.getInitParameter("ProxyTo") != null) {
                this._proxyTo = config.getInitParameter("ProxyTo");
            }
            if (config.getInitParameter("Prefix") != null) {
                this._prefix = config.getInitParameter("Prefix");
            }
            if (this._proxyTo == null) {
                throw new UnavailableException("No ProxyTo");
            }
            super.init(config);
            this._context.log("Transparent ProxyServlet @ " + ((this._prefix == null) ? "-" : this._prefix) + " to " + this._proxyTo);
        }
        
        protected URL proxyHttpURL(final String scheme, final String serverName, final int serverPort, final String uri) throws MalformedURLException {
            if (this._prefix != null && !uri.startsWith(this._prefix)) {
                return null;
            }
            if (this._prefix != null) {
                return new URL(this._proxyTo + uri.substring(this._prefix.length()));
            }
            return new URL(this._proxyTo + uri);
        }
    }
}
