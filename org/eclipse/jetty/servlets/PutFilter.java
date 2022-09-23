// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponseWrapper;
import java.net.URISyntaxException;
import java.io.OutputStream;
import java.io.InputStream;
import org.eclipse.jetty.util.IO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.FilterConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.io.File;
import javax.servlet.ServletContext;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import javax.servlet.Filter;

public class PutFilter implements Filter
{
    public static final String __PUT = "PUT";
    public static final String __DELETE = "DELETE";
    public static final String __MOVE = "MOVE";
    public static final String __OPTIONS = "OPTIONS";
    Set<String> _operations;
    private ConcurrentMap<String, String> _hidden;
    private ServletContext _context;
    private String _baseURI;
    private boolean _delAllowed;
    private boolean _putAtomic;
    private File _tmpdir;
    
    public PutFilter() {
        this._operations = new HashSet<String>();
        this._hidden = new ConcurrentHashMap<String, String>();
    }
    
    public void init(final FilterConfig config) throws ServletException {
        this._context = config.getServletContext();
        this._tmpdir = (File)this._context.getAttribute("javax.servlet.context.tempdir");
        if (this._context.getRealPath("/") == null) {
            throw new UnavailableException("Packed war");
        }
        final String b = config.getInitParameter("baseURI");
        if (b != null) {
            this._baseURI = b;
        }
        else {
            final File base = new File(this._context.getRealPath("/"));
            this._baseURI = base.toURI().toString();
        }
        this._delAllowed = this.getInitBoolean(config, "delAllowed");
        this._putAtomic = this.getInitBoolean(config, "putAtomic");
        this._operations.add("OPTIONS");
        this._operations.add("PUT");
        if (this._delAllowed) {
            this._operations.add("DELETE");
            this._operations.add("MOVE");
        }
    }
    
    private boolean getInitBoolean(final FilterConfig config, final String name) {
        final String value = config.getInitParameter(name);
        return value != null && value.length() > 0 && (value.startsWith("t") || value.startsWith("T") || value.startsWith("y") || value.startsWith("Y") || value.startsWith("1"));
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final String servletPath = request.getServletPath();
        final String pathInfo = request.getPathInfo();
        final String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        final String resource = URIUtil.addPaths(this._baseURI, pathInContext);
        final String method = request.getMethod();
        final boolean op = this._operations.contains(method);
        if (op) {
            File file = null;
            try {
                if (method.equals("OPTIONS")) {
                    this.handleOptions(chain, request, response);
                }
                else {
                    file = new File(new URI(resource));
                    final boolean exists = file.exists();
                    if (exists && !this.passConditionalHeaders(request, response, file)) {
                        return;
                    }
                    if (method.equals("PUT")) {
                        this.handlePut(request, response, pathInContext, file);
                    }
                    else if (method.equals("DELETE")) {
                        this.handleDelete(request, response, pathInContext, file);
                    }
                    else {
                        if (!method.equals("MOVE")) {
                            throw new IllegalStateException();
                        }
                        this.handleMove(request, response, pathInContext, file);
                    }
                }
            }
            catch (Exception e) {
                this._context.log(e.toString(), e);
                response.sendError(500);
            }
            return;
        }
        if (this.isHidden(pathInContext)) {
            response.sendError(404);
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
    private boolean isHidden(final String pathInContext) {
        return this._hidden.containsKey(pathInContext);
    }
    
    public void destroy() {
    }
    
    public void handlePut(final HttpServletRequest request, final HttpServletResponse response, final String pathInContext, final File file) throws ServletException, IOException {
        final boolean exists = file.exists();
        if (pathInContext.endsWith("/")) {
            if (!exists) {
                if (!file.mkdirs()) {
                    response.sendError(403);
                }
                else {
                    response.setStatus(201);
                    response.flushBuffer();
                }
            }
            else {
                response.setStatus(200);
                response.flushBuffer();
            }
        }
        else {
            boolean ok = false;
            try {
                this._hidden.put(pathInContext, pathInContext);
                final File parent = file.getParentFile();
                parent.mkdirs();
                final int toRead = request.getContentLength();
                final InputStream in = request.getInputStream();
                if (this._putAtomic) {
                    final File tmp = File.createTempFile(file.getName(), null, this._tmpdir);
                    final OutputStream out = new FileOutputStream(tmp, false);
                    if (toRead >= 0) {
                        IO.copy(in, out, toRead);
                    }
                    else {
                        IO.copy(in, out);
                    }
                    out.close();
                    if (!tmp.renameTo(file)) {
                        throw new IOException("rename from " + tmp + " to " + file + " failed");
                    }
                }
                else {
                    final OutputStream out2 = new FileOutputStream(file, false);
                    if (toRead >= 0) {
                        IO.copy(in, out2, toRead);
                    }
                    else {
                        IO.copy(in, out2);
                    }
                    out2.close();
                }
                response.setStatus(exists ? 200 : 201);
                response.flushBuffer();
                ok = true;
            }
            catch (Exception ex) {
                this._context.log(ex.toString(), ex);
                response.sendError(403);
            }
            finally {
                if (!ok) {
                    try {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    catch (Exception e) {
                        this._context.log(e.toString(), e);
                    }
                }
                this._hidden.remove(pathInContext);
            }
        }
    }
    
    public void handleDelete(final HttpServletRequest request, final HttpServletResponse response, final String pathInContext, final File file) throws ServletException, IOException {
        try {
            if (file.delete()) {
                response.setStatus(204);
                response.flushBuffer();
            }
            else {
                response.sendError(403);
            }
        }
        catch (SecurityException sex) {
            this._context.log(sex.toString(), sex);
            response.sendError(403);
        }
    }
    
    public void handleMove(final HttpServletRequest request, final HttpServletResponse response, final String pathInContext, final File file) throws ServletException, IOException, URISyntaxException {
        final String newPath = URIUtil.canonicalPath(request.getHeader("new-uri"));
        if (newPath == null) {
            response.sendError(400);
            return;
        }
        final String contextPath = request.getContextPath();
        if (contextPath != null && !newPath.startsWith(contextPath)) {
            response.sendError(405);
            return;
        }
        String newInfo = newPath;
        if (contextPath != null) {
            newInfo = newInfo.substring(contextPath.length());
        }
        final String new_resource = URIUtil.addPaths(this._baseURI, newInfo);
        final File new_file = new File(new URI(new_resource));
        file.renameTo(new_file);
        response.setStatus(204);
        response.flushBuffer();
    }
    
    public void handleOptions(final FilterChain chain, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        chain.doFilter(request, new HttpServletResponseWrapper(response) {
            @Override
            public void setHeader(final String name, String value) {
                if ("Allow".equalsIgnoreCase(name)) {
                    final Set<String> options = new HashSet<String>();
                    options.addAll(Arrays.asList(value.split(" *, *")));
                    options.addAll(PutFilter.this._operations);
                    value = null;
                    for (final String o : options) {
                        value = ((value == null) ? o : (value + ", " + o));
                    }
                }
                super.setHeader(name, value);
            }
        });
    }
    
    protected boolean passConditionalHeaders(final HttpServletRequest request, final HttpServletResponse response, final File file) throws IOException {
        long date = 0L;
        if ((date = request.getDateHeader("if-unmodified-since")) > 0L && file.lastModified() / 1000L > date / 1000L) {
            response.sendError(412);
            return false;
        }
        if ((date = request.getDateHeader("if-modified-since")) > 0L && file.lastModified() / 1000L <= date / 1000L) {
            response.reset();
            response.setStatus(304);
            response.flushBuffer();
            return false;
        }
        return true;
    }
}
