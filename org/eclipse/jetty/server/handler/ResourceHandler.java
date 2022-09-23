// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Response;
import javax.servlet.ServletException;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.resource.PathResource;
import javax.servlet.AsyncContext;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.server.HttpOutput;
import java.io.Writer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class ResourceHandler extends HandlerWrapper implements ResourceFactory
{
    private static final Logger LOG;
    ContextHandler _context;
    Resource _baseResource;
    Resource _defaultStylesheet;
    Resource _stylesheet;
    String[] _welcomeFiles;
    MimeTypes _mimeTypes;
    String _cacheControl;
    boolean _directory;
    boolean _gzip;
    boolean _etags;
    int _minMemoryMappedContentLength;
    int _minAsyncContentLength;
    
    public ResourceHandler() {
        this._welcomeFiles = new String[] { "index.html" };
        this._minMemoryMappedContentLength = 0;
        this._minAsyncContentLength = 16384;
    }
    
    public MimeTypes getMimeTypes() {
        return this._mimeTypes;
    }
    
    public void setMimeTypes(final MimeTypes mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    public boolean isDirectoriesListed() {
        return this._directory;
    }
    
    public void setDirectoriesListed(final boolean directory) {
        this._directory = directory;
    }
    
    public int getMinMemoryMappedContentLength() {
        return this._minMemoryMappedContentLength;
    }
    
    public void setMinMemoryMappedContentLength(final int minMemoryMappedFileSize) {
        this._minMemoryMappedContentLength = minMemoryMappedFileSize;
    }
    
    public int getMinAsyncContentLength() {
        return this._minAsyncContentLength;
    }
    
    public void setMinAsyncContentLength(final int minAsyncContentLength) {
        this._minAsyncContentLength = minAsyncContentLength;
    }
    
    public boolean isEtags() {
        return this._etags;
    }
    
    public void setEtags(final boolean etags) {
        this._etags = etags;
    }
    
    public void doStart() throws Exception {
        final ContextHandler.Context scontext = ContextHandler.getCurrentContext();
        this._context = ((scontext == null) ? null : scontext.getContextHandler());
        this._mimeTypes = ((this._context == null) ? new MimeTypes() : this._context.getMimeTypes());
        super.doStart();
    }
    
    public Resource getBaseResource() {
        if (this._baseResource == null) {
            return null;
        }
        return this._baseResource;
    }
    
    public String getResourceBase() {
        if (this._baseResource == null) {
            return null;
        }
        return this._baseResource.toString();
    }
    
    public void setBaseResource(final Resource base) {
        this._baseResource = base;
    }
    
    public void setResourceBase(final String resourceBase) {
        try {
            this.setBaseResource(Resource.newResource(resourceBase));
        }
        catch (Exception e) {
            ResourceHandler.LOG.warn(e.toString(), new Object[0]);
            ResourceHandler.LOG.debug(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }
    
    public Resource getStylesheet() {
        if (this._stylesheet != null) {
            return this._stylesheet;
        }
        if (this._defaultStylesheet == null) {
            this._defaultStylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
        }
        return this._defaultStylesheet;
    }
    
    public void setStylesheet(final String stylesheet) {
        try {
            this._stylesheet = Resource.newResource(stylesheet);
            if (!this._stylesheet.exists()) {
                ResourceHandler.LOG.warn("unable to find custom stylesheet: " + stylesheet, new Object[0]);
                this._stylesheet = null;
            }
        }
        catch (Exception e) {
            ResourceHandler.LOG.warn(e.toString(), new Object[0]);
            ResourceHandler.LOG.debug(e);
            throw new IllegalArgumentException(stylesheet);
        }
    }
    
    public String getCacheControl() {
        return this._cacheControl;
    }
    
    public void setCacheControl(final String cacheControl) {
        this._cacheControl = cacheControl;
    }
    
    @Override
    public Resource getResource(String path) {
        if (ResourceHandler.LOG.isDebugEnabled()) {
            ResourceHandler.LOG.debug("{} getResource({})", (this._context == null) ? this._baseResource : this._context, this._baseResource, path);
        }
        if (path == null || !path.startsWith("/")) {
            return null;
        }
        try {
            final Resource base = this._baseResource;
            if (base == null) {
                if (this._context == null) {
                    return null;
                }
                return this._context.getResource(path);
            }
            else {
                path = URIUtil.canonicalPath(path);
                final Resource r = base.addPath(path);
                if (r != null && r.isAlias() && (this._context == null || !this._context.checkAlias(path, r))) {
                    if (ResourceHandler.LOG.isDebugEnabled()) {
                        ResourceHandler.LOG.debug("resource={} alias={}", r, r.getAlias());
                    }
                    return null;
                }
                return r;
            }
        }
        catch (Exception e) {
            ResourceHandler.LOG.debug(e);
            return null;
        }
    }
    
    protected Resource getResource(final HttpServletRequest request) throws MalformedURLException {
        final Boolean included = request.getAttribute("javax.servlet.include.request_uri") != null;
        String servletPath;
        String pathInfo;
        if (included != null && included) {
            servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            if (servletPath == null && pathInfo == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        }
        else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }
        final String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        return this.getResource(pathInContext);
    }
    
    public String[] getWelcomeFiles() {
        return this._welcomeFiles;
    }
    
    public void setWelcomeFiles(final String[] welcomeFiles) {
        this._welcomeFiles = welcomeFiles;
    }
    
    protected Resource getWelcome(final Resource directory) throws MalformedURLException, IOException {
        for (int i = 0; i < this._welcomeFiles.length; ++i) {
            final Resource welcome = directory.addPath(this._welcomeFiles[i]);
            if (welcome.exists() && !welcome.isDirectory()) {
                return welcome;
            }
        }
        return null;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled()) {
            return;
        }
        boolean skipContentBody = false;
        if (!HttpMethod.GET.is(request.getMethod())) {
            if (!HttpMethod.HEAD.is(request.getMethod())) {
                super.handle(target, baseRequest, request, response);
                return;
            }
            skipContentBody = true;
        }
        Resource resource = this.getResource(request);
        if (ResourceHandler.LOG.isDebugEnabled()) {
            if (resource == null) {
                ResourceHandler.LOG.debug("resource=null", new Object[0]);
            }
            else {
                ResourceHandler.LOG.debug("resource={} alias={} exists={}", resource, resource.getAlias(), resource.exists());
            }
        }
        if (resource == null || !resource.exists()) {
            if (!target.endsWith("/jetty-dir.css")) {
                super.handle(target, baseRequest, request, response);
                return;
            }
            resource = this.getStylesheet();
            if (resource == null) {
                return;
            }
            response.setContentType("text/css");
        }
        baseRequest.setHandled(true);
        if (resource.isDirectory()) {
            final String pathInfo = request.getPathInfo();
            final boolean endsWithSlash = ((pathInfo == null) ? request.getServletPath() : pathInfo).endsWith("/");
            if (!endsWithSlash) {
                response.sendRedirect(response.encodeRedirectURL(request.getRequestURI() + "/"));
                return;
            }
            final Resource welcome = this.getWelcome(resource);
            if (welcome == null || !welcome.exists()) {
                this.doDirectory(request, response, resource);
                baseRequest.setHandled(true);
                return;
            }
            resource = welcome;
        }
        final long last_modified = resource.lastModified();
        String etag = null;
        if (this._etags) {
            final String ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
            etag = resource.getWeakETag();
            if (ifnm != null && resource != null && ifnm.equals(etag)) {
                response.setStatus(304);
                baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);
                return;
            }
        }
        if (last_modified > 0L) {
            final long if_modified = request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
            if (if_modified > 0L && last_modified / 1000L <= if_modified / 1000L) {
                response.setStatus(304);
                return;
            }
        }
        String mime = this._mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null) {
            mime = this._mimeTypes.getMimeByExtension(request.getPathInfo());
        }
        this.doResponseHeaders(response, resource, mime);
        if (this._etags) {
            baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);
        }
        if (last_modified > 0L) {
            response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(), last_modified);
        }
        if (skipContentBody) {
            return;
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        }
        catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }
        if (!(out instanceof HttpOutput)) {
            resource.writeTo(out, 0L, resource.length());
        }
        else {
            final int min_async_size = (this._minAsyncContentLength == 0) ? response.getBufferSize() : this._minAsyncContentLength;
            if (request.isAsyncSupported() && min_async_size > 0 && resource.length() >= min_async_size) {
                final AsyncContext async = request.startAsync();
                async.setTimeout(0L);
                final Callback callback = new Callback() {
                    @Override
                    public void succeeded() {
                        async.complete();
                    }
                    
                    @Override
                    public void failed(final Throwable x) {
                        ResourceHandler.LOG.warn(x.toString(), new Object[0]);
                        ResourceHandler.LOG.debug(x);
                        async.complete();
                    }
                };
                if (this._minMemoryMappedContentLength >= 0 && resource.length() > this._minMemoryMappedContentLength && resource.length() < 2147483647L && resource instanceof PathResource) {
                    final ByteBuffer buffer = BufferUtil.toMappedBuffer(resource.getFile());
                    ((HttpOutput)out).sendContent(buffer, callback);
                }
                else {
                    final ReadableByteChannel channel = resource.getReadableByteChannel();
                    if (channel != null) {
                        ((HttpOutput)out).sendContent(channel, callback);
                    }
                    else {
                        ((HttpOutput)out).sendContent(resource.getInputStream(), callback);
                    }
                }
            }
            else if (this._minMemoryMappedContentLength > 0 && resource.length() > this._minMemoryMappedContentLength && resource instanceof PathResource) {
                final ByteBuffer buffer2 = BufferUtil.toMappedBuffer(resource.getFile());
                ((HttpOutput)out).sendContent(buffer2);
            }
            else {
                final ReadableByteChannel channel2 = resource.getReadableByteChannel();
                if (channel2 != null) {
                    ((HttpOutput)out).sendContent(channel2);
                }
                else {
                    ((HttpOutput)out).sendContent(resource.getInputStream());
                }
            }
        }
    }
    
    protected void doDirectory(final HttpServletRequest request, final HttpServletResponse response, final Resource resource) throws IOException {
        if (this._directory) {
            final String listing = resource.getListHTML(request.getRequestURI(), request.getPathInfo().lastIndexOf("/") > 0);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(listing);
        }
        else {
            response.sendError(403);
        }
    }
    
    protected void doResponseHeaders(final HttpServletResponse response, final Resource resource, final String mimeType) {
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        final long length = resource.length();
        if (response instanceof Response) {
            final HttpFields fields = ((Response)response).getHttpFields();
            if (length > 0L) {
                ((Response)response).setLongContentLength(length);
            }
            if (this._cacheControl != null) {
                fields.put(HttpHeader.CACHE_CONTROL, this._cacheControl);
            }
        }
        else {
            if (length > 2147483647L) {
                response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Long.toString(length));
            }
            else if (length > 0L) {
                response.setContentLength((int)length);
            }
            if (this._cacheControl != null) {
                response.setHeader(HttpHeader.CACHE_CONTROL.asString(), this._cacheControl);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ResourceHandler.class);
    }
}
