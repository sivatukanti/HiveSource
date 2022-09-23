// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.jetty.HttpFields;
import org.mortbay.util.TypeUtil;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Response;
import javax.servlet.ServletException;
import java.io.OutputStream;
import org.mortbay.io.Buffer;
import java.io.Writer;
import org.mortbay.io.WriterOutputStream;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.util.URIUtil;
import java.net.MalformedURLException;
import org.mortbay.log.Log;
import org.mortbay.resource.FileResource;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.resource.Resource;

public class ResourceHandler extends AbstractHandler
{
    ContextHandler _context;
    Resource _baseResource;
    String[] _welcomeFiles;
    MimeTypes _mimeTypes;
    ByteArrayBuffer _cacheControl;
    boolean _aliases;
    
    public ResourceHandler() {
        this._welcomeFiles = new String[] { "index.html" };
        this._mimeTypes = new MimeTypes();
    }
    
    public MimeTypes getMimeTypes() {
        return this._mimeTypes;
    }
    
    public void setMimeTypes(final MimeTypes mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    public boolean isAliases() {
        return this._aliases;
    }
    
    public void setAliases(final boolean aliases) {
        this._aliases = aliases;
    }
    
    public void doStart() throws Exception {
        final ContextHandler.SContext scontext = ContextHandler.getCurrentContext();
        this._context = ((scontext == null) ? null : scontext.getContextHandler());
        if (!this._aliases && !FileResource.getCheckAliases()) {
            throw new IllegalStateException("Alias checking disabled");
        }
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
            Log.warn(e.toString());
            Log.debug(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }
    
    public String getCacheControl() {
        return this._cacheControl.toString();
    }
    
    public void setCacheControl(final String cacheControl) {
        this._cacheControl = ((cacheControl == null) ? null : new ByteArrayBuffer(cacheControl));
    }
    
    public Resource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }
        Resource base = this._baseResource;
        if (base == null) {
            if (this._context == null) {
                return null;
            }
            base = this._context.getBaseResource();
            if (base == null) {
                return null;
            }
        }
        try {
            path = URIUtil.canonicalPath(path);
            final Resource resource = base.addPath(path);
            return resource;
        }
        catch (Exception e) {
            Log.ignore(e);
            return null;
        }
    }
    
    protected Resource getResource(final HttpServletRequest request) throws MalformedURLException {
        final String path_info = request.getPathInfo();
        if (path_info == null) {
            return null;
        }
        return this.getResource(path_info);
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
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        if (base_request.isHandled()) {
            return;
        }
        boolean skipContentBody = false;
        if (!"GET".equals(request.getMethod())) {
            if (!"HEAD".equals(request.getMethod())) {
                return;
            }
            skipContentBody = true;
        }
        Resource resource = this.getResource(request);
        if (resource == null || !resource.exists()) {
            return;
        }
        if (!this._aliases && resource.getAlias() != null) {
            Log.info(resource + " aliased to " + resource.getAlias());
            return;
        }
        base_request.setHandled(true);
        if (resource.isDirectory()) {
            if (!request.getPathInfo().endsWith("/")) {
                response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), "/")));
                return;
            }
            resource = this.getWelcome(resource);
            if (resource == null || !resource.exists() || resource.isDirectory()) {
                response.sendError(403);
                return;
            }
        }
        final long last_modified = resource.lastModified();
        if (last_modified > 0L) {
            final long if_modified = request.getDateHeader("If-Modified-Since");
            if (if_modified > 0L && last_modified / 1000L <= if_modified / 1000L) {
                response.setStatus(304);
                return;
            }
        }
        Buffer mime = this._mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null) {
            mime = this._mimeTypes.getMimeByExtension(request.getPathInfo());
        }
        this.doResponseHeaders(response, resource, (mime != null) ? mime.toString() : null);
        response.setDateHeader("Last-Modified", last_modified);
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
        if (out instanceof HttpConnection.Output) {
            ((HttpConnection.Output)out).sendContent(resource.getInputStream());
        }
        else {
            resource.writeTo(out, 0L, resource.length());
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
                fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);
            }
            if (this._cacheControl != null) {
                fields.put(HttpHeaders.CACHE_CONTROL_BUFFER, this._cacheControl);
            }
        }
        else {
            if (length > 0L) {
                response.setHeader("Content-Length", TypeUtil.toString(length));
            }
            if (this._cacheControl != null) {
                response.setHeader("Cache-Control", this._cacheControl.toString());
            }
        }
    }
}
