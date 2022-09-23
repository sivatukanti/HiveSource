// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import org.mortbay.io.nio.IndirectNIOBuffer;
import org.mortbay.io.nio.DirectNIOBuffer;
import org.mortbay.jetty.HttpHeaderValues;
import org.mortbay.jetty.HttpFields;
import org.mortbay.util.TypeUtil;
import org.mortbay.jetty.HttpHeaders;
import java.io.InputStream;
import java.util.List;
import java.io.OutputStream;
import org.mortbay.util.IO;
import org.mortbay.util.MultiPartOutputStream;
import org.mortbay.jetty.InclusiveByteRange;
import org.mortbay.jetty.Response;
import java.io.Writer;
import org.mortbay.io.WriterOutputStream;
import org.mortbay.io.Buffer;
import java.net.MalformedURLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.HttpContent;
import java.util.Enumeration;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.mortbay.jetty.nio.NIOConnector;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.mortbay.log.Log;
import javax.servlet.UnavailableException;
import org.mortbay.resource.FileResource;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.ResourceCache;
import org.mortbay.resource.Resource;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.resource.ResourceFactory;
import javax.servlet.http.HttpServlet;

public class DefaultServlet extends HttpServlet implements ResourceFactory
{
    private ContextHandler.SContext _context;
    private boolean _acceptRanges;
    private boolean _dirAllowed;
    private boolean _welcomeServlets;
    private boolean _redirectWelcome;
    private boolean _gzip;
    private Resource _resourceBase;
    private NIOResourceCache _nioCache;
    private ResourceCache _bioCache;
    private MimeTypes _mimeTypes;
    private String[] _welcomes;
    private boolean _aliases;
    private boolean _useFileMappedBuffer;
    ByteArrayBuffer _cacheControl;
    private ServletHandler _servletHandler;
    private ServletHolder _defaultHolder;
    
    public DefaultServlet() {
        this._acceptRanges = true;
        this._dirAllowed = true;
        this._welcomeServlets = false;
        this._redirectWelcome = false;
        this._gzip = true;
        this._aliases = false;
        this._useFileMappedBuffer = false;
    }
    
    public void init() throws UnavailableException {
        final ServletContext config = this.getServletContext();
        this._context = (ContextHandler.SContext)config;
        this._mimeTypes = this._context.getContextHandler().getMimeTypes();
        this._welcomes = this._context.getContextHandler().getWelcomeFiles();
        if (this._welcomes == null) {
            this._welcomes = new String[] { "index.jsp", "index.html" };
        }
        this._acceptRanges = this.getInitBoolean("acceptRanges", this._acceptRanges);
        this._dirAllowed = this.getInitBoolean("dirAllowed", this._dirAllowed);
        this._welcomeServlets = this.getInitBoolean("welcomeServlets", this._welcomeServlets);
        this._redirectWelcome = this.getInitBoolean("redirectWelcome", this._redirectWelcome);
        this._gzip = this.getInitBoolean("gzip", this._gzip);
        this._aliases = this.getInitBoolean("aliases", this._aliases);
        if (!this._aliases && !FileResource.getCheckAliases()) {
            throw new IllegalStateException("Alias checking disabled");
        }
        if (this._aliases) {
            config.log("Aliases are enabled");
        }
        this._useFileMappedBuffer = this.getInitBoolean("useFileMappedBuffer", this._useFileMappedBuffer);
        final String rrb = this.getInitParameter("relativeResourceBase");
        if (rrb != null) {
            try {
                final Resource root = this._context.getContextHandler().getResource("/");
                if (root == null) {
                    throw new UnavailableException("No base resourceBase for relativeResourceBase in" + this._context.getContextPath());
                }
                this._resourceBase = root.addPath(rrb);
            }
            catch (Exception e) {
                Log.warn("EXCEPTION ", e);
                throw new UnavailableException(e.toString());
            }
        }
        final String rb = this.getInitParameter("resourceBase");
        if (rrb != null && rb != null) {
            throw new UnavailableException("resourceBase & relativeResourceBase");
        }
        if (rb != null) {
            try {
                this._resourceBase = Resource.newResource(rb);
            }
            catch (Exception e2) {
                Log.warn("EXCEPTION ", e2);
                throw new UnavailableException(e2.toString());
            }
        }
        final String t = this.getInitParameter("cacheControl");
        if (t != null) {
            this._cacheControl = new ByteArrayBuffer(t);
        }
        try {
            if (this._resourceBase == null) {
                this._resourceBase = this._context.getContextHandler().getResource("/");
            }
            final String cache_type = this.getInitParameter("cacheType");
            final int max_cache_size = this.getInitInt("maxCacheSize", -2);
            final int max_cached_file_size = this.getInitInt("maxCachedFileSize", -2);
            final int max_cached_files = this.getInitInt("maxCachedFiles", -2);
            if ((cache_type == null || "nio".equals(cache_type) || "both".equals(cache_type)) && (max_cache_size == -2 || max_cache_size > 0)) {
                this._nioCache = new NIOResourceCache(this._mimeTypes);
                if (max_cache_size > 0) {
                    this._nioCache.setMaxCacheSize(max_cache_size);
                }
                if (max_cached_file_size >= -1) {
                    this._nioCache.setMaxCachedFileSize(max_cached_file_size);
                }
                if (max_cached_files >= -1) {
                    this._nioCache.setMaxCachedFiles(max_cached_files);
                }
                this._nioCache.start();
            }
            if (("bio".equals(cache_type) || "both".equals(cache_type)) && (max_cache_size == -2 || max_cache_size > 0)) {
                this._bioCache = new ResourceCache(this._mimeTypes);
                if (max_cache_size > 0) {
                    this._bioCache.setMaxCacheSize(max_cache_size);
                }
                if (max_cached_file_size >= -1) {
                    this._bioCache.setMaxCachedFileSize(max_cached_file_size);
                }
                if (max_cached_files >= -1) {
                    this._bioCache.setMaxCachedFiles(max_cached_files);
                }
                this._bioCache.start();
            }
            if (this._nioCache == null) {
                this._bioCache = null;
            }
        }
        catch (Exception e3) {
            Log.warn("EXCEPTION ", e3);
            throw new UnavailableException(e3.toString());
        }
        this._servletHandler = (ServletHandler)this._context.getContextHandler().getChildHandlerByClass(ServletHandler.class);
        final ServletHolder[] holders = this._servletHandler.getServlets();
        int i = holders.length;
        while (i-- > 0) {
            if (holders[i].getServletInstance() == this) {
                this._defaultHolder = holders[i];
            }
        }
        if (Log.isDebugEnabled()) {
            Log.debug("resource base = " + this._resourceBase);
        }
    }
    
    public String getInitParameter(final String name) {
        String value = this.getServletContext().getInitParameter("org.mortbay.jetty.servlet.Default." + name);
        if (value == null) {
            value = super.getInitParameter(name);
        }
        return value;
    }
    
    private boolean getInitBoolean(final String name, final boolean dft) {
        final String value = this.getInitParameter(name);
        if (value == null || value.length() == 0) {
            return dft;
        }
        return value.startsWith("t") || value.startsWith("T") || value.startsWith("y") || value.startsWith("Y") || value.startsWith("1");
    }
    
    private int getInitInt(final String name, final int dft) {
        String value = this.getInitParameter(name);
        if (value == null) {
            value = this.getInitParameter(name);
        }
        if (value != null && value.length() > 0) {
            return Integer.parseInt(value);
        }
        return dft;
    }
    
    public Resource getResource(final String pathInContext) {
        if (this._resourceBase == null) {
            return null;
        }
        Resource r = null;
        try {
            r = this._resourceBase.addPath(pathInContext);
            if (!this._aliases && r.getAlias() != null) {
                if (r.exists()) {
                    Log.warn("Aliased resource: " + r + "==" + r.getAlias());
                }
                return null;
            }
            if (Log.isDebugEnabled()) {
                Log.debug("RESOURCE=" + r);
            }
        }
        catch (IOException e) {
            Log.ignore(e);
        }
        return r;
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String servletPath = null;
        String pathInfo = null;
        Enumeration reqRanges = null;
        Boolean included = (Boolean)request.getAttribute("org.mortbay.jetty.included");
        if (included != null && included) {
            servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            if (servletPath == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        }
        else {
            included = Boolean.FALSE;
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
            reqRanges = request.getHeaders("Range");
            if (reqRanges != null && !reqRanges.hasMoreElements()) {
                reqRanges = null;
            }
        }
        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        final boolean endsWithSlash = pathInContext.endsWith("/");
        String pathInContextGz = null;
        boolean gzip = false;
        if (!included && this._gzip && reqRanges == null && !endsWithSlash) {
            final String accept = request.getHeader("Accept-Encoding");
            if (accept != null && accept.indexOf("gzip") >= 0) {
                gzip = true;
            }
        }
        Resource resource = null;
        HttpContent content = null;
        final Connector connector = HttpConnection.getCurrentConnection().getConnector();
        final ResourceCache cache = (connector instanceof NIOConnector) ? this._nioCache : this._bioCache;
        try {
            if (gzip) {
                pathInContextGz = pathInContext + ".gz";
                resource = this.getResource(pathInContextGz);
                if (resource == null || !resource.exists() || resource.isDirectory()) {
                    gzip = false;
                    pathInContextGz = null;
                }
                else if (cache != null) {
                    content = cache.lookup(pathInContextGz, resource);
                    if (content != null) {
                        resource = content.getResource();
                    }
                }
                if (resource == null || !resource.exists() || resource.isDirectory()) {
                    gzip = false;
                    pathInContextGz = null;
                }
            }
            if (!gzip) {
                if (cache == null) {
                    resource = this.getResource(pathInContext);
                }
                else {
                    content = cache.lookup(pathInContext, this);
                    if (content != null) {
                        resource = content.getResource();
                    }
                    else {
                        resource = this.getResource(pathInContext);
                    }
                }
            }
            if (Log.isDebugEnabled()) {
                Log.debug("resource=" + resource + ((content != null) ? " content" : ""));
            }
            if (resource == null || !resource.exists()) {
                response.sendError(404);
            }
            else if (!resource.isDirectory()) {
                if (endsWithSlash && this._aliases && pathInContext.length() > 1) {
                    final String q = request.getQueryString();
                    pathInContext = pathInContext.substring(0, pathInContext.length() - 1);
                    if (q != null && q.length() != 0) {
                        pathInContext = pathInContext + "?" + q;
                    }
                    response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(this._context.getContextPath(), pathInContext)));
                }
                else {
                    if (content == null) {
                        content = new UnCachedContent(resource);
                    }
                    if (included || this.passConditionalHeaders(request, response, resource, content)) {
                        if (gzip) {
                            response.setHeader("Content-Encoding", "gzip");
                            final String mt = this._context.getMimeType(pathInContext);
                            if (mt != null) {
                                response.setContentType(mt);
                            }
                        }
                        this.sendData(request, response, included, resource, content, reqRanges);
                    }
                }
            }
            else {
                String welcome = null;
                if (!endsWithSlash || (pathInContext.length() == 1 && request.getAttribute("org.mortbay.jetty.nullPathInfo") != null)) {
                    final StringBuffer buf = request.getRequestURL();
                    final int param = buf.lastIndexOf(";");
                    if (param < 0) {
                        buf.append('/');
                    }
                    else {
                        buf.insert(param, '/');
                    }
                    final String q2 = request.getQueryString();
                    if (q2 != null && q2.length() != 0) {
                        buf.append('?');
                        buf.append(q2);
                    }
                    response.setContentLength(0);
                    response.sendRedirect(response.encodeRedirectURL(buf.toString()));
                }
                else if (null != (welcome = this.getWelcomeFile(pathInContext))) {
                    if (this._redirectWelcome) {
                        response.setContentLength(0);
                        final String q3 = request.getQueryString();
                        if (q3 != null && q3.length() != 0) {
                            response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(this._context.getContextPath(), welcome) + "?" + q3));
                        }
                        else {
                            response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(this._context.getContextPath(), welcome)));
                        }
                    }
                    else {
                        final RequestDispatcher dispatcher = request.getRequestDispatcher(welcome);
                        if (dispatcher != null) {
                            if (included) {
                                dispatcher.include(request, response);
                            }
                            else {
                                request.setAttribute("org.mortbay.jetty.welcome", welcome);
                                dispatcher.forward(request, response);
                            }
                        }
                    }
                }
                else {
                    content = new UnCachedContent(resource);
                    if (included || this.passConditionalHeaders(request, response, resource, content)) {
                        this.sendDirectory(request, response, resource, pathInContext.length() > 1);
                    }
                }
            }
        }
        catch (IllegalArgumentException e) {
            Log.warn("EXCEPTION ", e);
            if (!response.isCommitted()) {
                response.sendError(500, e.getMessage());
            }
        }
        finally {
            if (content != null) {
                content.release();
            }
            else if (resource != null) {
                resource.release();
            }
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
    
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405);
    }
    
    private String getWelcomeFile(final String pathInContext) throws MalformedURLException, IOException {
        if (this._welcomes == null) {
            return null;
        }
        String welcome_servlet = null;
        for (int i = 0; i < this._welcomes.length; ++i) {
            final String welcome_in_context = URIUtil.addPaths(pathInContext, this._welcomes[i]);
            final Resource welcome = this.getResource(welcome_in_context);
            if (welcome != null && welcome.exists()) {
                return this._welcomes[i];
            }
            if (this._welcomeServlets && welcome_servlet == null) {
                final Map.Entry entry = this._servletHandler.getHolderEntry(welcome_in_context);
                if (entry != null && entry.getValue() != this._defaultHolder) {
                    welcome_servlet = welcome_in_context;
                }
            }
        }
        return welcome_servlet;
    }
    
    protected boolean passConditionalHeaders(final HttpServletRequest request, final HttpServletResponse response, final Resource resource, final HttpContent content) throws IOException {
        try {
            if (!request.getMethod().equals("HEAD")) {
                final String ifms = request.getHeader("If-Modified-Since");
                if (ifms != null) {
                    if (content != null) {
                        final Buffer mdlm = content.getLastModified();
                        if (mdlm != null && ifms.equals(mdlm.toString())) {
                            response.reset();
                            response.setStatus(304);
                            response.flushBuffer();
                            return false;
                        }
                    }
                    final long ifmsl = request.getDateHeader("If-Modified-Since");
                    if (ifmsl != -1L && resource.lastModified() / 1000L <= ifmsl / 1000L) {
                        response.reset();
                        response.setStatus(304);
                        response.flushBuffer();
                        return false;
                    }
                }
                final long date = request.getDateHeader("If-Unmodified-Since");
                if (date != -1L && resource.lastModified() / 1000L > date / 1000L) {
                    response.sendError(412);
                    return false;
                }
            }
        }
        catch (IllegalArgumentException iae) {
            if (!response.isCommitted()) {
                response.sendError(400, iae.getMessage());
            }
            throw iae;
        }
        return true;
    }
    
    protected void sendDirectory(final HttpServletRequest request, final HttpServletResponse response, final Resource resource, final boolean parent) throws IOException {
        if (!this._dirAllowed) {
            response.sendError(403);
            return;
        }
        byte[] data = null;
        final String base = URIUtil.addPaths(request.getRequestURI(), "/");
        final String dir = resource.getListHTML(base, parent);
        if (dir == null) {
            response.sendError(403, "No directory");
            return;
        }
        data = dir.getBytes("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
    
    protected void sendData(final HttpServletRequest request, final HttpServletResponse response, final boolean include, final Resource resource, final HttpContent content, final Enumeration reqRanges) throws IOException {
        final long content_length = (content == null) ? resource.length() : content.getContentLength();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        }
        catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }
        if (reqRanges == null || !reqRanges.hasMoreElements() || content_length < 0L) {
            if (include) {
                resource.writeTo(out, 0L, content_length);
            }
            else if (out instanceof HttpConnection.Output) {
                if (response instanceof Response) {
                    this.writeOptionHeaders(((Response)response).getHttpFields());
                    ((HttpConnection.Output)out).sendContent(content);
                }
                else if (content.getBuffer() != null) {
                    this.writeHeaders(response, content, content_length);
                    ((HttpConnection.Output)out).sendContent(content.getBuffer());
                }
                else {
                    this.writeHeaders(response, content, content_length);
                    resource.writeTo(out, 0L, content_length);
                }
            }
            else {
                this.writeHeaders(response, content, content_length);
                resource.writeTo(out, 0L, content_length);
            }
        }
        else {
            final List ranges = InclusiveByteRange.satisfiableRanges(reqRanges, content_length);
            if (ranges == null || ranges.size() == 0) {
                this.writeHeaders(response, content, content_length);
                response.setStatus(416);
                response.setHeader("Content-Range", InclusiveByteRange.to416HeaderRangeString(content_length));
                resource.writeTo(out, 0L, content_length);
                return;
            }
            if (ranges.size() == 1) {
                final InclusiveByteRange singleSatisfiableRange = ranges.get(0);
                final long singleLength = singleSatisfiableRange.getSize(content_length);
                this.writeHeaders(response, content, singleLength);
                response.setStatus(206);
                response.setHeader("Content-Range", singleSatisfiableRange.toHeaderRangeString(content_length));
                resource.writeTo(out, singleSatisfiableRange.getFirst(content_length), singleLength);
                return;
            }
            this.writeHeaders(response, content, -1L);
            final String mimetype = content.getContentType().toString();
            final MultiPartOutputStream multi = new MultiPartOutputStream(out);
            response.setStatus(206);
            String ctp;
            if (request.getHeader("Request-Range") != null) {
                ctp = "multipart/x-byteranges; boundary=";
            }
            else {
                ctp = "multipart/byteranges; boundary=";
            }
            response.setContentType(ctp + multi.getBoundary());
            InputStream in = resource.getInputStream();
            long pos = 0L;
            int length = 0;
            final String[] header = new String[ranges.size()];
            for (int i = 0; i < ranges.size(); ++i) {
                final InclusiveByteRange ibr = ranges.get(i);
                header[i] = ibr.toHeaderRangeString(content_length);
                length += (int)(((i > 0) ? 2 : 0) + 2 + multi.getBoundary().length() + 2 + "Content-Type".length() + 2 + mimetype.length() + 2 + "Content-Range".length() + 2 + header[i].length() + 2 + 2 + (ibr.getLast(content_length) - ibr.getFirst(content_length)) + 1L);
            }
            length += 4 + multi.getBoundary().length() + 2 + 2;
            response.setContentLength(length);
            for (int i = 0; i < ranges.size(); ++i) {
                final InclusiveByteRange ibr = ranges.get(i);
                multi.startPart(mimetype, new String[] { "Content-Range: " + header[i] });
                final long start = ibr.getFirst(content_length);
                final long size = ibr.getSize(content_length);
                if (in != null) {
                    if (start < pos) {
                        in.close();
                        in = resource.getInputStream();
                        pos = 0L;
                    }
                    if (pos < start) {
                        in.skip(start - pos);
                        pos = start;
                    }
                    IO.copy(in, multi, size);
                    pos += size;
                }
                else {
                    resource.writeTo(multi, start, size);
                }
            }
            if (in != null) {
                in.close();
            }
            multi.close();
        }
    }
    
    protected void writeHeaders(final HttpServletResponse response, final HttpContent content, final long count) throws IOException {
        if (content.getContentType() != null && response.getContentType() == null) {
            response.setContentType(content.getContentType().toString());
        }
        if (response instanceof Response) {
            final Response r = (Response)response;
            final HttpFields fields = r.getHttpFields();
            if (content.getLastModified() != null) {
                fields.put(HttpHeaders.LAST_MODIFIED_BUFFER, content.getLastModified(), content.getResource().lastModified());
            }
            else if (content.getResource() != null) {
                final long lml = content.getResource().lastModified();
                if (lml != -1L) {
                    fields.putDateField(HttpHeaders.LAST_MODIFIED_BUFFER, lml);
                }
            }
            if (count != -1L) {
                r.setLongContentLength(count);
            }
            this.writeOptionHeaders(fields);
        }
        else {
            final long lml2 = content.getResource().lastModified();
            if (lml2 >= 0L) {
                response.setDateHeader("Last-Modified", lml2);
            }
            if (count != -1L) {
                if (count < 2147483647L) {
                    response.setContentLength((int)count);
                }
                else {
                    response.setHeader("Content-Length", TypeUtil.toString(count));
                }
            }
            this.writeOptionHeaders(response);
        }
    }
    
    protected void writeOptionHeaders(final HttpFields fields) throws IOException {
        if (this._acceptRanges) {
            fields.put(HttpHeaders.ACCEPT_RANGES_BUFFER, HttpHeaderValues.BYTES_BUFFER);
        }
        if (this._cacheControl != null) {
            fields.put(HttpHeaders.CACHE_CONTROL_BUFFER, this._cacheControl);
        }
    }
    
    protected void writeOptionHeaders(final HttpServletResponse response) throws IOException {
        if (this._acceptRanges) {
            response.setHeader("Accept-Ranges", "bytes");
        }
        if (this._cacheControl != null) {
            response.setHeader("Cache-Control", this._cacheControl.toString());
        }
    }
    
    public void destroy() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        org/mortbay/jetty/servlet/DefaultServlet._nioCache:Lorg/mortbay/jetty/servlet/DefaultServlet$NIOResourceCache;
        //     4: ifnull          14
        //     7: aload_0         /* this */
        //     8: getfield        org/mortbay/jetty/servlet/DefaultServlet._nioCache:Lorg/mortbay/jetty/servlet/DefaultServlet$NIOResourceCache;
        //    11: invokevirtual   org/mortbay/jetty/servlet/DefaultServlet$NIOResourceCache.stop:()V
        //    14: jsr             39
        //    17: goto            93
        //    20: astore_1        /* e */
        //    21: ldc             "EXCEPTION "
        //    23: aload_1         /* e */
        //    24: invokestatic    org/mortbay/log/Log.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    27: jsr             39
        //    30: goto            93
        //    33: astore_2       
        //    34: jsr             39
        //    37: aload_2        
        //    38: athrow         
        //    39: astore_3       
        //    40: aload_0         /* this */
        //    41: getfield        org/mortbay/jetty/servlet/DefaultServlet._bioCache:Lorg/mortbay/jetty/ResourceCache;
        //    44: ifnull          54
        //    47: aload_0         /* this */
        //    48: getfield        org/mortbay/jetty/servlet/DefaultServlet._bioCache:Lorg/mortbay/jetty/ResourceCache;
        //    51: invokevirtual   org/mortbay/jetty/ResourceCache.stop:()V
        //    54: jsr             83
        //    57: goto            91
        //    60: astore          e
        //    62: ldc             "EXCEPTION "
        //    64: aload           e
        //    66: invokestatic    org/mortbay/log/Log.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    69: jsr             83
        //    72: goto            91
        //    75: astore          5
        //    77: jsr             83
        //    80: aload           5
        //    82: athrow         
        //    83: astore          6
        //    85: aload_0         /* this */
        //    86: invokespecial   javax/servlet/http/HttpServlet.destroy:()V
        //    89: ret             6
        //    91: ret             3
        //    93: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      14     20     33     Ljava/lang/Exception;
        //  0      17     33     39     Any
        //  20     30     33     39     Any
        //  33     37     33     39     Any
        //  40     54     60     75     Ljava/lang/Exception;
        //  40     57     75     83     Any
        //  60     72     75     83     Any
        //  75     80     75     83     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Inconsistent stack size at #0074 (coming from #0071).
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2183)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private class UnCachedContent implements HttpContent
    {
        Resource _resource;
        
        UnCachedContent(final Resource resource) {
            this._resource = resource;
        }
        
        public Buffer getContentType() {
            return DefaultServlet.this._mimeTypes.getMimeByExtension(this._resource.toString());
        }
        
        public Buffer getLastModified() {
            return null;
        }
        
        public Buffer getBuffer() {
            return null;
        }
        
        public long getContentLength() {
            return this._resource.length();
        }
        
        public InputStream getInputStream() throws IOException {
            return this._resource.getInputStream();
        }
        
        public Resource getResource() {
            return this._resource;
        }
        
        public void release() {
            this._resource.release();
            this._resource = null;
        }
    }
    
    class NIOResourceCache extends ResourceCache
    {
        public NIOResourceCache(final MimeTypes mimeTypes) {
            super(mimeTypes);
        }
        
        protected void fill(final Content content) throws IOException {
            Buffer buffer = null;
            final Resource resource = content.getResource();
            final long length = resource.length();
            if (DefaultServlet.this._useFileMappedBuffer && resource.getFile() != null) {
                buffer = new DirectNIOBuffer(resource.getFile());
            }
            else {
                final InputStream is = resource.getInputStream();
                try {
                    final Connector connector = HttpConnection.getCurrentConnection().getConnector();
                    buffer = (((NIOConnector)connector).getUseDirectBuffers() ? new DirectNIOBuffer((int)length) : new IndirectNIOBuffer((int)length));
                }
                catch (OutOfMemoryError e) {
                    Log.warn(e.toString());
                    Log.debug(e);
                    buffer = new IndirectNIOBuffer((int)length);
                }
                buffer.readFrom(is, (int)length);
                is.close();
            }
            content.setBuffer(buffer);
        }
    }
}
