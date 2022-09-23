// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Response;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.MultiPartOutputStream;
import org.eclipse.jetty.server.InclusiveByteRange;
import javax.servlet.AsyncContext;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.BufferUtil;
import java.io.Writer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.GzipHttpContent;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.DateParser;
import org.eclipse.jetty.server.Request;
import java.net.MalformedURLException;
import org.eclipse.jetty.http.PathMap;
import java.util.Iterator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.io.IOException;
import org.eclipse.jetty.util.URIUtil;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.eclipse.jetty.server.ResourceContentFactory;
import org.eclipse.jetty.http.HttpHeader;
import javax.servlet.UnavailableException;
import java.util.List;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.server.ResourceCache;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletContext;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.ResourceFactory;
import javax.servlet.http.HttpServlet;

public class DefaultServlet extends HttpServlet implements ResourceFactory
{
    private static final Logger LOG;
    private static final long serialVersionUID = 4930458713846881193L;
    private static final PreEncodedHttpField ACCEPT_RANGES;
    private ServletContext _servletContext;
    private ContextHandler _contextHandler;
    private boolean _acceptRanges;
    private boolean _dirAllowed;
    private boolean _welcomeServlets;
    private boolean _welcomeExactServlets;
    private boolean _redirectWelcome;
    private boolean _gzip;
    private boolean _pathInfoOnly;
    private boolean _etags;
    private Resource _resourceBase;
    private ResourceCache _cache;
    private HttpContent.Factory _contentFactory;
    private MimeTypes _mimeTypes;
    private String[] _welcomes;
    private Resource _stylesheet;
    private boolean _useFileMappedBuffer;
    private HttpField _cacheControl;
    private String _relativeResourceBase;
    private ServletHandler _servletHandler;
    private ServletHolder _defaultHolder;
    private List<String> _gzipEquivalentFileExtensions;
    
    public DefaultServlet() {
        this._acceptRanges = true;
        this._dirAllowed = true;
        this._welcomeServlets = false;
        this._welcomeExactServlets = false;
        this._redirectWelcome = false;
        this._gzip = false;
        this._pathInfoOnly = false;
        this._etags = false;
        this._useFileMappedBuffer = false;
    }
    
    @Override
    public void init() throws UnavailableException {
        this._servletContext = this.getServletContext();
        this._contextHandler = this.initContextHandler(this._servletContext);
        this._mimeTypes = this._contextHandler.getMimeTypes();
        this._welcomes = this._contextHandler.getWelcomeFiles();
        if (this._welcomes == null) {
            this._welcomes = new String[] { "index.html", "index.jsp" };
        }
        this._acceptRanges = this.getInitBoolean("acceptRanges", this._acceptRanges);
        this._dirAllowed = this.getInitBoolean("dirAllowed", this._dirAllowed);
        this._redirectWelcome = this.getInitBoolean("redirectWelcome", this._redirectWelcome);
        this._gzip = this.getInitBoolean("gzip", this._gzip);
        this._pathInfoOnly = this.getInitBoolean("pathInfoOnly", this._pathInfoOnly);
        if ("exact".equals(this.getInitParameter("welcomeServlets"))) {
            this._welcomeExactServlets = true;
            this._welcomeServlets = false;
        }
        else {
            this._welcomeServlets = this.getInitBoolean("welcomeServlets", this._welcomeServlets);
        }
        this._useFileMappedBuffer = this.getInitBoolean("useFileMappedBuffer", this._useFileMappedBuffer);
        this._relativeResourceBase = this.getInitParameter("relativeResourceBase");
        final String rb = this.getInitParameter("resourceBase");
        if (rb != null) {
            if (this._relativeResourceBase != null) {
                throw new UnavailableException("resourceBase & relativeResourceBase");
            }
            try {
                this._resourceBase = this._contextHandler.newResource(rb);
            }
            catch (Exception e) {
                DefaultServlet.LOG.warn("EXCEPTION ", e);
                throw new UnavailableException(e.toString());
            }
        }
        final String css = this.getInitParameter("stylesheet");
        try {
            if (css != null) {
                this._stylesheet = Resource.newResource(css);
                if (!this._stylesheet.exists()) {
                    DefaultServlet.LOG.warn("!" + css, new Object[0]);
                    this._stylesheet = null;
                }
            }
            if (this._stylesheet == null) {
                this._stylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
            }
        }
        catch (Exception e2) {
            DefaultServlet.LOG.warn(e2.toString(), new Object[0]);
            DefaultServlet.LOG.debug(e2);
        }
        final String cc = this.getInitParameter("cacheControl");
        if (cc != null) {
            this._cacheControl = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cc);
        }
        final String resourceCache = this.getInitParameter("resourceCache");
        final int max_cache_size = this.getInitInt("maxCacheSize", -2);
        final int max_cached_file_size = this.getInitInt("maxCachedFileSize", -2);
        final int max_cached_files = this.getInitInt("maxCachedFiles", -2);
        if (resourceCache != null) {
            if (max_cache_size != -1 || max_cached_file_size != -2 || max_cached_files != -2) {
                DefaultServlet.LOG.debug("ignoring resource cache configuration, using resourceCache attribute", new Object[0]);
            }
            if (this._relativeResourceBase != null || this._resourceBase != null) {
                throw new UnavailableException("resourceCache specified with resource bases");
            }
            this._cache = (ResourceCache)this._servletContext.getAttribute(resourceCache);
            if (DefaultServlet.LOG.isDebugEnabled()) {
                DefaultServlet.LOG.debug("Cache {}={}", resourceCache, this._contentFactory);
            }
        }
        this._etags = this.getInitBoolean("etags", this._etags);
        try {
            if (this._cache == null && (max_cached_files != -2 || max_cache_size != -2 || max_cached_file_size != -2)) {
                this._cache = new ResourceCache(null, this, this._mimeTypes, this._useFileMappedBuffer, this._etags, this._gzip);
                if (max_cache_size >= 0) {
                    this._cache.setMaxCacheSize(max_cache_size);
                }
                if (max_cached_file_size >= -1) {
                    this._cache.setMaxCachedFileSize(max_cached_file_size);
                }
                if (max_cached_files >= -1) {
                    this._cache.setMaxCachedFiles(max_cached_files);
                }
                this._servletContext.setAttribute((resourceCache == null) ? "resourceCache" : resourceCache, this._cache);
            }
        }
        catch (Exception e3) {
            DefaultServlet.LOG.warn("EXCEPTION ", e3);
            throw new UnavailableException(e3.toString());
        }
        if (this._cache != null) {
            this._contentFactory = this._cache;
        }
        else {
            this._contentFactory = new ResourceContentFactory(this, this._mimeTypes, this._gzip);
            if (resourceCache != null) {
                this._servletContext.setAttribute(resourceCache, this._contentFactory);
            }
        }
        this._gzipEquivalentFileExtensions = new ArrayList<String>();
        final String otherGzipExtensions = this.getInitParameter("otherGzipFileExtensions");
        if (otherGzipExtensions != null) {
            final StringTokenizer tok = new StringTokenizer(otherGzipExtensions, ",", false);
            while (tok.hasMoreTokens()) {
                final String s = tok.nextToken().trim();
                this._gzipEquivalentFileExtensions.add((s.charAt(0) == '.') ? s : ("." + s));
            }
        }
        else {
            this._gzipEquivalentFileExtensions.add(".svgz");
        }
        this._servletHandler = this._contextHandler.getChildHandlerByClass(ServletHandler.class);
        for (final ServletHolder h : this._servletHandler.getServlets()) {
            if (h.getServletInstance() == this) {
                this._defaultHolder = h;
            }
        }
        if (DefaultServlet.LOG.isDebugEnabled()) {
            DefaultServlet.LOG.debug("resource base = " + this._resourceBase, new Object[0]);
        }
    }
    
    protected ContextHandler initContextHandler(final ServletContext servletContext) {
        final ContextHandler.Context scontext = ContextHandler.getCurrentContext();
        if (scontext != null) {
            return ContextHandler.getCurrentContext().getContextHandler();
        }
        if (servletContext instanceof ContextHandler.Context) {
            return ((ContextHandler.Context)servletContext).getContextHandler();
        }
        throw new IllegalArgumentException("The servletContext " + servletContext + " " + servletContext.getClass().getName() + " is not " + ContextHandler.Context.class.getName());
    }
    
    @Override
    public String getInitParameter(final String name) {
        String value = this.getServletContext().getInitParameter("org.eclipse.jetty.servlet.Default." + name);
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
    
    @Override
    public Resource getResource(String pathInContext) {
        Resource r = null;
        if (this._relativeResourceBase != null) {
            pathInContext = URIUtil.addPaths(this._relativeResourceBase, pathInContext);
        }
        try {
            if (this._resourceBase != null) {
                r = this._resourceBase.addPath(pathInContext);
                if (!this._contextHandler.checkAlias(pathInContext, r)) {
                    r = null;
                }
            }
            else if (this._servletContext instanceof ContextHandler.Context) {
                r = this._contextHandler.getResource(pathInContext);
            }
            else {
                final URL u = this._servletContext.getResource(pathInContext);
                r = this._contextHandler.newResource(u);
            }
            if (DefaultServlet.LOG.isDebugEnabled()) {
                DefaultServlet.LOG.debug("Resource " + pathInContext + "=" + r, new Object[0]);
            }
        }
        catch (IOException e) {
            DefaultServlet.LOG.ignore(e);
        }
        catch (Throwable t) {
            throw (InvalidPathException)new InvalidPathException(pathInContext, "Invalid PathInContext").initCause(t);
        }
        if ((r == null || !r.exists()) && pathInContext.endsWith("/jetty-dir.css")) {
            r = this._stylesheet;
        }
        return r;
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String servletPath = null;
        String pathInfo = null;
        Enumeration<String> reqRanges = null;
        final boolean included = request.getAttribute("javax.servlet.include.request_uri") != null;
        if (included) {
            servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            if (servletPath == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        }
        else {
            servletPath = (this._pathInfoOnly ? "/" : request.getServletPath());
            pathInfo = request.getPathInfo();
            reqRanges = request.getHeaders(HttpHeader.RANGE.asString());
            if (!this.hasDefinedRange(reqRanges)) {
                reqRanges = null;
            }
        }
        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        final boolean endsWithSlash = ((pathInfo == null) ? request.getServletPath() : pathInfo).endsWith("/");
        final boolean gzippable = this._gzip && !endsWithSlash && !included && reqRanges == null;
        HttpContent content = null;
        boolean release_content = true;
        try {
            content = this._contentFactory.getContent(pathInContext, response.getBufferSize());
            if (DefaultServlet.LOG.isDebugEnabled()) {
                DefaultServlet.LOG.info("content={}", content);
            }
            if (content == null || !content.getResource().exists()) {
                if (included) {
                    throw new FileNotFoundException("!" + pathInContext);
                }
                response.sendError(404);
            }
            else {
                if (content.getResource().isDirectory()) {
                    this.sendWelcome(content, pathInContext, endsWithSlash, included, request, response);
                    return;
                }
                if (endsWithSlash && pathInContext.length() > 1) {
                    final String q = request.getQueryString();
                    pathInContext = pathInContext.substring(0, pathInContext.length() - 1);
                    String uri = URIUtil.addPaths(this._servletContext.getContextPath(), pathInContext);
                    if (q != null && q.length() != 0) {
                        uri = uri + "?" + q;
                    }
                    response.sendRedirect(response.encodeRedirectURL(uri));
                    return;
                }
                if (!included && !this.passConditionalHeaders(request, response, content)) {
                    return;
                }
                final HttpContent gzip_content = gzippable ? content.getGzipContent() : null;
                if (gzip_content != null) {
                    response.addHeader(HttpHeader.VARY.asString(), HttpHeader.ACCEPT_ENCODING.asString());
                    final String accept = request.getHeader(HttpHeader.ACCEPT_ENCODING.asString());
                    if (accept != null && accept.indexOf("gzip") >= 0) {
                        if (DefaultServlet.LOG.isDebugEnabled()) {
                            DefaultServlet.LOG.debug("gzip={}", gzip_content);
                        }
                        content = gzip_content;
                    }
                }
                if (this.isGzippedContent(pathInContext)) {
                    response.setHeader(HttpHeader.CONTENT_ENCODING.asString(), "gzip");
                }
                release_content = this.sendData(request, response, included, content, reqRanges);
            }
        }
        catch (IllegalArgumentException e) {
            DefaultServlet.LOG.warn("EXCEPTION ", e);
            if (!response.isCommitted()) {
                response.sendError(500, e.getMessage());
            }
        }
        finally {
            if (release_content && content != null) {
                content.release();
            }
        }
    }
    
    protected void sendWelcome(final HttpContent content, final String pathInContext, final boolean endsWithSlash, final boolean included, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (!endsWithSlash || (pathInContext.length() == 1 && request.getAttribute("org.eclipse.jetty.server.nullPathInfo") != null)) {
            final StringBuffer buf = request.getRequestURL();
            synchronized (buf) {
                final int param = buf.lastIndexOf(";");
                if (param < 0) {
                    buf.append('/');
                }
                else {
                    buf.insert(param, '/');
                }
                final String q = request.getQueryString();
                if (q != null && q.length() != 0) {
                    buf.append('?');
                    buf.append(q);
                }
                response.setContentLength(0);
                response.sendRedirect(response.encodeRedirectURL(buf.toString()));
            }
            return;
        }
        final String welcome = this.getWelcomeFile(pathInContext);
        if (welcome != null) {
            if (DefaultServlet.LOG.isDebugEnabled()) {
                DefaultServlet.LOG.debug("welcome={}", welcome);
            }
            if (this._redirectWelcome) {
                response.setContentLength(0);
                String uri = URIUtil.encodePath(URIUtil.addPaths(this._servletContext.getContextPath(), welcome));
                final String q2 = request.getQueryString();
                if (q2 != null && !q2.isEmpty()) {
                    uri = uri + "?" + q2;
                }
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
            else {
                final RequestDispatcher dispatcher = this._servletContext.getRequestDispatcher(welcome);
                if (dispatcher != null) {
                    if (included) {
                        dispatcher.include(request, response);
                    }
                    else {
                        request.setAttribute("org.eclipse.jetty.server.welcome", welcome);
                        dispatcher.forward(request, response);
                    }
                }
            }
            return;
        }
        if (included || this.passConditionalHeaders(request, response, content)) {
            this.sendDirectory(request, response, content.getResource(), pathInContext);
        }
    }
    
    protected boolean isGzippedContent(final String path) {
        if (path == null) {
            return false;
        }
        for (final String suffix : this._gzipEquivalentFileExtensions) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasDefinedRange(final Enumeration<String> reqRanges) {
        return reqRanges != null && reqRanges.hasMoreElements();
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
    
    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405);
    }
    
    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Allow", "GET,HEAD,POST,OPTIONS");
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
                return welcome_in_context;
            }
            if ((this._welcomeServlets || this._welcomeExactServlets) && welcome_servlet == null) {
                final PathMap.MappedEntry<?> entry = this._servletHandler.getHolderEntry(welcome_in_context);
                if (entry != null && entry.getValue() != this._defaultHolder && (this._welcomeServlets || (this._welcomeExactServlets && entry.getKey().equals(welcome_in_context)))) {
                    welcome_servlet = welcome_in_context;
                }
            }
        }
        return welcome_servlet;
    }
    
    protected boolean passConditionalHeaders(final HttpServletRequest request, final HttpServletResponse response, final HttpContent content) throws IOException {
        try {
            String ifm = null;
            String ifnm = null;
            String ifms = null;
            long ifums = -1L;
            if (request instanceof Request) {
                final HttpFields fields = ((Request)request).getHttpFields();
                int i = fields.size();
                while (i-- > 0) {
                    final HttpField field = fields.getField(i);
                    if (field.getHeader() != null) {
                        switch (field.getHeader()) {
                            case IF_MATCH: {
                                ifm = field.getValue();
                                continue;
                            }
                            case IF_NONE_MATCH: {
                                ifnm = field.getValue();
                                continue;
                            }
                            case IF_MODIFIED_SINCE: {
                                ifms = field.getValue();
                                continue;
                            }
                            case IF_UNMODIFIED_SINCE: {
                                ifums = DateParser.parseDate(field.getValue());
                                continue;
                            }
                        }
                    }
                }
            }
            else {
                ifm = request.getHeader(HttpHeader.IF_MATCH.asString());
                ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
                ifms = request.getHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
                ifums = request.getDateHeader(HttpHeader.IF_UNMODIFIED_SINCE.asString());
            }
            if (!HttpMethod.HEAD.is(request.getMethod())) {
                if (this._etags) {
                    final String etag = content.getETagValue();
                    if (ifm != null) {
                        boolean match = false;
                        if (etag != null) {
                            final QuotedCSV quoted = new QuotedCSV(true, new String[] { ifm });
                            for (final String tag : quoted) {
                                if (etag.equals(tag) || (tag.endsWith("--gzip\"") && etag.equals(GzipHttpContent.removeGzipFromETag(tag)))) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (!match) {
                            response.setStatus(412);
                            return false;
                        }
                    }
                    if (ifnm != null && etag != null) {
                        if (etag.equals(ifnm) || (ifnm.endsWith("--gzip\"") && ifnm.indexOf(44) < 0 && etag.equals(GzipHttpContent.removeGzipFromETag(ifnm)))) {
                            response.setStatus(304);
                            response.setHeader(HttpHeader.ETAG.asString(), ifnm);
                            return false;
                        }
                        final QuotedCSV quoted2 = new QuotedCSV(true, new String[] { ifnm });
                        for (final String tag2 : quoted2) {
                            if (etag.equals(tag2) || (tag2.endsWith("--gzip\"") && etag.equals(GzipHttpContent.removeGzipFromETag(tag2)))) {
                                response.setStatus(304);
                                response.setHeader(HttpHeader.ETAG.asString(), tag2);
                                return false;
                            }
                        }
                        return true;
                    }
                }
                if (ifms != null) {
                    final String mdlm = content.getLastModifiedValue();
                    if (mdlm != null && ifms.equals(mdlm)) {
                        response.setStatus(304);
                        if (this._etags) {
                            response.setHeader(HttpHeader.ETAG.asString(), content.getETagValue());
                        }
                        response.flushBuffer();
                        return false;
                    }
                    final long ifmsl = request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
                    if (ifmsl != -1L && content.getResource().lastModified() / 1000L <= ifmsl / 1000L) {
                        response.setStatus(304);
                        if (this._etags) {
                            response.setHeader(HttpHeader.ETAG.asString(), content.getETagValue());
                        }
                        response.flushBuffer();
                        return false;
                    }
                }
                if (ifums != -1L && content.getResource().lastModified() / 1000L > ifums / 1000L) {
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
    
    protected void sendDirectory(final HttpServletRequest request, final HttpServletResponse response, Resource resource, final String pathInContext) throws IOException {
        if (!this._dirAllowed) {
            response.sendError(403);
            return;
        }
        byte[] data = null;
        final String base = URIUtil.addPaths(request.getRequestURI(), "/");
        if (this._resourceBase != null) {
            if (this._resourceBase instanceof ResourceCollection) {
                resource = this._resourceBase.addPath(pathInContext);
            }
        }
        else if (this._contextHandler.getBaseResource() instanceof ResourceCollection) {
            resource = this._contextHandler.getBaseResource().addPath(pathInContext);
        }
        final String dir = resource.getListHTML(base, pathInContext.length() > 1);
        if (dir == null) {
            response.sendError(403, "No directory");
            return;
        }
        data = dir.getBytes("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
    
    protected boolean sendData(final HttpServletRequest request, final HttpServletResponse response, final boolean include, final HttpContent content, final Enumeration<String> reqRanges) throws IOException {
        final long content_length = content.getContentLengthValue();
        OutputStream out = null;
        boolean written;
        try {
            out = response.getOutputStream();
            written = (!(out instanceof HttpOutput) || ((HttpOutput)out).isWritten());
        }
        catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
            written = true;
        }
        if (DefaultServlet.LOG.isDebugEnabled()) {
            DefaultServlet.LOG.debug(String.format("sendData content=%s out=%s async=%b", content, out, request.isAsyncSupported()), new Object[0]);
        }
        if (reqRanges == null || !reqRanges.hasMoreElements() || content_length < 0L) {
            if (include) {
                content.getResource().writeTo(out, 0L, content_length);
            }
            else if (written || !(out instanceof HttpOutput)) {
                this.putHeaders(response, content, written ? -1L : 0L);
                final ByteBuffer buffer = content.getIndirectBuffer();
                if (buffer != null) {
                    BufferUtil.writeTo(buffer, out);
                }
                else {
                    content.getResource().writeTo(out, 0L, content_length);
                }
            }
            else {
                this.putHeaders(response, content, 0L);
                if (request.isAsyncSupported()) {
                    final AsyncContext context = request.startAsync();
                    context.setTimeout(0L);
                    ((HttpOutput)out).sendContent(content, new Callback() {
                        @Override
                        public void succeeded() {
                            context.complete();
                            content.release();
                        }
                        
                        @Override
                        public void failed(final Throwable x) {
                            if (x instanceof IOException) {
                                DefaultServlet.LOG.debug(x);
                            }
                            else {
                                DefaultServlet.LOG.warn(x);
                            }
                            context.complete();
                            content.release();
                        }
                        
                        @Override
                        public String toString() {
                            return String.format("DefaultServlet@%x$CB", DefaultServlet.this.hashCode());
                        }
                    });
                    return false;
                }
                ((HttpOutput)out).sendContent(content);
            }
        }
        else {
            final List<InclusiveByteRange> ranges = InclusiveByteRange.satisfiableRanges(reqRanges, content_length);
            if (ranges == null || ranges.size() == 0) {
                this.putHeaders(response, content, 0L);
                response.setStatus(416);
                response.setHeader(HttpHeader.CONTENT_RANGE.asString(), InclusiveByteRange.to416HeaderRangeString(content_length));
                content.getResource().writeTo(out, 0L, content_length);
                return true;
            }
            if (ranges.size() == 1) {
                final InclusiveByteRange singleSatisfiableRange = ranges.get(0);
                final long singleLength = singleSatisfiableRange.getSize(content_length);
                this.putHeaders(response, content, singleLength);
                response.setStatus(206);
                if (!response.containsHeader(HttpHeader.DATE.asString())) {
                    response.addDateHeader(HttpHeader.DATE.asString(), System.currentTimeMillis());
                }
                response.setHeader(HttpHeader.CONTENT_RANGE.asString(), singleSatisfiableRange.toHeaderRangeString(content_length));
                content.getResource().writeTo(out, singleSatisfiableRange.getFirst(content_length), singleLength);
                return true;
            }
            this.putHeaders(response, content, -1L);
            final String mimetype = (content == null) ? null : content.getContentTypeValue();
            if (mimetype == null) {
                DefaultServlet.LOG.warn("Unknown mimetype for " + request.getRequestURI(), new Object[0]);
            }
            final MultiPartOutputStream multi = new MultiPartOutputStream(out);
            response.setStatus(206);
            if (!response.containsHeader(HttpHeader.DATE.asString())) {
                response.addDateHeader(HttpHeader.DATE.asString(), System.currentTimeMillis());
            }
            String ctp;
            if (request.getHeader(HttpHeader.REQUEST_RANGE.asString()) != null) {
                ctp = "multipart/x-byteranges; boundary=";
            }
            else {
                ctp = "multipart/byteranges; boundary=";
            }
            response.setContentType(ctp + multi.getBoundary());
            InputStream in = content.getResource().getInputStream();
            long pos = 0L;
            int length = 0;
            final String[] header = new String[ranges.size()];
            for (int i = 0; i < ranges.size(); ++i) {
                final InclusiveByteRange ibr = ranges.get(i);
                header[i] = ibr.toHeaderRangeString(content_length);
                length += (int)(((i > 0) ? 2 : 0) + 2 + multi.getBoundary().length() + 2 + ((mimetype == null) ? 0 : (HttpHeader.CONTENT_TYPE.asString().length() + 2 + mimetype.length())) + 2 + HttpHeader.CONTENT_RANGE.asString().length() + 2 + header[i].length() + 2 + 2 + (ibr.getLast(content_length) - ibr.getFirst(content_length)) + 1L);
            }
            length += 4 + multi.getBoundary().length() + 2 + 2;
            response.setContentLength(length);
            for (int i = 0; i < ranges.size(); ++i) {
                final InclusiveByteRange ibr = ranges.get(i);
                multi.startPart(mimetype, new String[] { HttpHeader.CONTENT_RANGE + ": " + header[i] });
                final long start = ibr.getFirst(content_length);
                final long size = ibr.getSize(content_length);
                if (in != null) {
                    if (start < pos) {
                        in.close();
                        in = content.getResource().getInputStream();
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
                    content.getResource().writeTo(multi, start, size);
                }
            }
            if (in != null) {
                in.close();
            }
            multi.close();
        }
        return true;
    }
    
    protected void putHeaders(final HttpServletResponse response, final HttpContent content, final long contentLength) {
        if (response instanceof Response) {
            final Response r = (Response)response;
            r.putHeaders(content, contentLength, this._etags);
            final HttpFields f = r.getHttpFields();
            if (this._acceptRanges) {
                f.put(DefaultServlet.ACCEPT_RANGES);
            }
            if (this._cacheControl != null) {
                f.put(this._cacheControl);
            }
        }
        else {
            Response.putHeaders(response, content, contentLength, this._etags);
            if (this._acceptRanges) {
                response.setHeader(DefaultServlet.ACCEPT_RANGES.getName(), DefaultServlet.ACCEPT_RANGES.getValue());
            }
            if (this._cacheControl != null) {
                response.setHeader(this._cacheControl.getName(), this._cacheControl.getValue());
            }
        }
    }
    
    @Override
    public void destroy() {
        if (this._cache != null) {
            this._cache.flushCache();
        }
        super.destroy();
    }
    
    static {
        LOG = Log.getLogger(DefaultServlet.class);
        ACCEPT_RANGES = new PreEncodedHttpField(HttpHeader.ACCEPT_RANGES, "bytes");
    }
}
