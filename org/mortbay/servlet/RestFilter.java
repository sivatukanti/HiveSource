// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.io.OutputStream;
import java.io.InputStream;
import org.mortbay.util.IO;
import java.io.FileOutputStream;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.mortbay.util.URIUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.UnavailableException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class RestFilter implements Filter
{
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_DELETE = "DELETE";
    private FilterConfig filterConfig;
    private long _maxPutSize;
    
    public void init(final FilterConfig filterConfig) throws UnavailableException {
        this.filterConfig = filterConfig;
        final String tmp = filterConfig.getInitParameter("maxPutSize");
        if (tmp != null) {
            this._maxPutSize = Long.parseLong(tmp);
        }
    }
    
    private File locateFile(final HttpServletRequest request) {
        return new File(this.filterConfig.getServletContext().getRealPath(URIUtil.addPaths(request.getServletPath(), request.getPathInfo())));
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (httpRequest.getMethod().equals("GET")) {
            chain.doFilter(httpRequest, httpResponse);
        }
        else if (httpRequest.getMethod().equals("PUT")) {
            this.doPut(httpRequest, httpResponse);
        }
        else if (httpRequest.getMethod().equals("DELETE")) {
            this.doDelete(httpRequest, httpResponse);
        }
        else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }
    
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final File file = this.locateFile(request);
        if (file.exists()) {
            final boolean success = file.delete();
            if (!success) {
                response.sendError(403);
                return;
            }
        }
        final FileOutputStream out = new FileOutputStream(file);
        try {
            if (this._maxPutSize > 0L) {
                final int length = request.getContentLength();
                if (length > this._maxPutSize) {
                    response.sendError(403);
                    return;
                }
                IO.copy(request.getInputStream(), out, this._maxPutSize);
            }
            else {
                IO.copy(request.getInputStream(), out);
            }
        }
        finally {
            out.close();
        }
        response.setStatus(204);
    }
    
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final File file = this.locateFile(request);
        if (!file.exists()) {
            response.sendError(404);
            return;
        }
        final boolean success = IO.delete(file);
        if (success) {
            response.setStatus(204);
        }
        else {
            response.sendError(500);
        }
    }
    
    public void destroy() {
    }
}
