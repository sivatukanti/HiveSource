// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler.gzip;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.HttpOutput;
import java.io.File;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.StringUtil;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.pathmap.PathSpecSet;
import org.eclipse.jetty.util.RegexSet;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.IncludeExclude;
import java.util.zip.Deflater;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public class GzipHandler extends HandlerWrapper implements GzipFactory
{
    private static final Logger LOG;
    public static final String GZIP = "gzip";
    public static final String DEFLATE = "deflate";
    public static final int DEFAULT_MIN_GZIP_SIZE = 16;
    private int _minGzipSize;
    private int _compressionLevel;
    private boolean _checkGzExists;
    private boolean _syncFlush;
    private EnumSet<DispatcherType> _dispatchers;
    private final ThreadLocal<Deflater> _deflater;
    private final IncludeExclude<String> _agentPatterns;
    private final IncludeExclude<String> _methods;
    private final IncludeExclude<String> _paths;
    private final IncludeExclude<String> _mimeTypes;
    private HttpField _vary;
    
    public GzipHandler() {
        this._minGzipSize = 16;
        this._compressionLevel = -1;
        this._checkGzExists = true;
        this._syncFlush = false;
        this._dispatchers = EnumSet.of(DispatcherType.REQUEST);
        this._deflater = new ThreadLocal<Deflater>();
        this._agentPatterns = new IncludeExclude<String>((Class<SET>)RegexSet.class);
        this._methods = new IncludeExclude<String>();
        this._paths = new IncludeExclude<String>((Class<SET>)PathSpecSet.class);
        this._mimeTypes = new IncludeExclude<String>();
        this._methods.include(HttpMethod.GET.asString());
        for (final String type : MimeTypes.getKnownMimeTypes()) {
            if ("image/svg+xml".equals(type)) {
                this._paths.exclude("*.svgz");
            }
            else {
                if (!type.startsWith("image/") && !type.startsWith("audio/") && !type.startsWith("video/")) {
                    continue;
                }
                this._mimeTypes.exclude(type);
            }
        }
        this._mimeTypes.exclude("application/compress");
        this._mimeTypes.exclude("application/zip");
        this._mimeTypes.exclude("application/gzip");
        this._mimeTypes.exclude("application/bzip2");
        this._mimeTypes.exclude("application/x-rar-compressed");
        GzipHandler.LOG.debug("{} mime types {}", this, this._mimeTypes);
        this._agentPatterns.exclude(".*MSIE 6.0.*");
    }
    
    public void addExcludedAgentPatterns(final String... patterns) {
        this._agentPatterns.exclude(patterns);
    }
    
    public void addExcludedMethods(final String... methods) {
        for (final String m : methods) {
            this._methods.exclude(m);
        }
    }
    
    public EnumSet<DispatcherType> getDispatcherTypes() {
        return this._dispatchers;
    }
    
    public void setDispatcherTypes(final EnumSet<DispatcherType> dispatchers) {
        this._dispatchers = dispatchers;
    }
    
    public void setDispatcherTypes(final DispatcherType... dispatchers) {
        this._dispatchers = EnumSet.copyOf(Arrays.asList(dispatchers));
    }
    
    public void addExcludedMimeTypes(final String... types) {
        for (final String t : types) {
            this._mimeTypes.exclude(StringUtil.csvSplit(t));
        }
    }
    
    public void addExcludedPaths(final String... pathspecs) {
        for (final String p : pathspecs) {
            this._paths.exclude(StringUtil.csvSplit(p));
        }
    }
    
    public void addIncludedAgentPatterns(final String... patterns) {
        this._agentPatterns.include(patterns);
    }
    
    public void addIncludedMethods(final String... methods) {
        for (final String m : methods) {
            this._methods.include(m);
        }
    }
    
    public boolean isSyncFlush() {
        return this._syncFlush;
    }
    
    public void setSyncFlush(final boolean syncFlush) {
        this._syncFlush = syncFlush;
    }
    
    public void addIncludedMimeTypes(final String... types) {
        for (final String t : types) {
            this._mimeTypes.include(StringUtil.csvSplit(t));
        }
    }
    
    public void addIncludedPaths(final String... pathspecs) {
        for (final String p : pathspecs) {
            this._paths.include(StringUtil.csvSplit(p));
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this._vary = ((this._agentPatterns.size() > 0) ? GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING_USER_AGENT : GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING);
        super.doStart();
    }
    
    public boolean getCheckGzExists() {
        return this._checkGzExists;
    }
    
    public int getCompressionLevel() {
        return this._compressionLevel;
    }
    
    @Override
    public Deflater getDeflater(final Request request, final long content_length) {
        final String ua = request.getHttpFields().get(HttpHeader.USER_AGENT);
        if (ua != null && !this.isAgentGzipable(ua)) {
            GzipHandler.LOG.debug("{} excluded user agent {}", this, request);
            return null;
        }
        if (content_length >= 0L && content_length < this._minGzipSize) {
            GzipHandler.LOG.debug("{} excluded minGzipSize {}", this, request);
            return null;
        }
        final HttpField accept = request.getHttpFields().getField(HttpHeader.ACCEPT_ENCODING);
        if (accept == null) {
            GzipHandler.LOG.debug("{} excluded !accept {}", this, request);
            return null;
        }
        final boolean gzip = accept.contains("gzip");
        if (!gzip) {
            GzipHandler.LOG.debug("{} excluded not gzip accept {}", this, request);
            return null;
        }
        Deflater df = this._deflater.get();
        if (df == null) {
            df = new Deflater(this._compressionLevel, true);
        }
        else {
            this._deflater.set(null);
        }
        return df;
    }
    
    public String[] getExcludedAgentPatterns() {
        final Set<String> excluded = this._agentPatterns.getExcluded();
        return excluded.toArray(new String[excluded.size()]);
    }
    
    public String[] getExcludedMethods() {
        final Set<String> excluded = this._methods.getExcluded();
        return excluded.toArray(new String[excluded.size()]);
    }
    
    public String[] getExcludedMimeTypes() {
        final Set<String> excluded = this._mimeTypes.getExcluded();
        return excluded.toArray(new String[excluded.size()]);
    }
    
    public String[] getExcludedPaths() {
        final Set<String> excluded = this._paths.getExcluded();
        return excluded.toArray(new String[excluded.size()]);
    }
    
    public String[] getIncludedAgentPatterns() {
        final Set<String> includes = this._agentPatterns.getIncluded();
        return includes.toArray(new String[includes.size()]);
    }
    
    public String[] getIncludedMethods() {
        final Set<String> includes = this._methods.getIncluded();
        return includes.toArray(new String[includes.size()]);
    }
    
    public String[] getIncludedMimeTypes() {
        final Set<String> includes = this._mimeTypes.getIncluded();
        return includes.toArray(new String[includes.size()]);
    }
    
    public String[] getIncludedPaths() {
        final Set<String> includes = this._paths.getIncluded();
        return includes.toArray(new String[includes.size()]);
    }
    
    @Deprecated
    public String[] getMethods() {
        return this.getIncludedMethods();
    }
    
    public int getMinGzipSize() {
        return this._minGzipSize;
    }
    
    protected HttpField getVaryField() {
        return this._vary;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final ServletContext context = baseRequest.getServletContext();
        final String path = (context == null) ? baseRequest.getRequestURI() : URIUtil.addPaths(baseRequest.getServletPath(), baseRequest.getPathInfo());
        GzipHandler.LOG.debug("{} handle {} in {}", this, baseRequest, context);
        if (!this._dispatchers.contains(baseRequest.getDispatcherType())) {
            GzipHandler.LOG.debug("{} excluded by dispatcherType {}", this, baseRequest.getDispatcherType());
            this._handler.handle(target, baseRequest, request, response);
            return;
        }
        final HttpOutput out = baseRequest.getResponse().getHttpOutput();
        for (HttpOutput.Interceptor interceptor = out.getInterceptor(); interceptor != null; interceptor = interceptor.getNextInterceptor()) {
            if (interceptor instanceof GzipHttpOutputInterceptor) {
                GzipHandler.LOG.debug("{} already intercepting {}", this, request);
                this._handler.handle(target, baseRequest, request, response);
                return;
            }
        }
        if (!this._methods.matches(baseRequest.getMethod())) {
            GzipHandler.LOG.debug("{} excluded by method {}", this, request);
            this._handler.handle(target, baseRequest, request, response);
            return;
        }
        if (!this.isPathGzipable(path)) {
            GzipHandler.LOG.debug("{} excluded by path {}", this, request);
            this._handler.handle(target, baseRequest, request, response);
            return;
        }
        String mimeType = (context == null) ? MimeTypes.getDefaultMimeByExtension(path) : context.getMimeType(path);
        if (mimeType != null) {
            mimeType = MimeTypes.getContentTypeWithoutCharset(mimeType);
            if (!this.isMimeTypeGzipable(mimeType)) {
                GzipHandler.LOG.debug("{} excluded by path suffix mime type {}", this, request);
                this._handler.handle(target, baseRequest, request, response);
                return;
            }
        }
        if (this._checkGzExists && context != null) {
            final String realpath = request.getServletContext().getRealPath(path);
            if (realpath != null) {
                final File gz = new File(realpath + ".gz");
                if (gz.exists()) {
                    GzipHandler.LOG.debug("{} gzip exists {}", this, request);
                    this._handler.handle(target, baseRequest, request, response);
                    return;
                }
            }
        }
        String etag = baseRequest.getHttpFields().get(HttpHeader.IF_NONE_MATCH);
        if (etag != null) {
            int i = etag.indexOf("--gzip\"");
            if (i > 0) {
                baseRequest.setAttribute("o.e.j.s.h.gzip.GzipHandler.etag", etag);
                while (i >= 0) {
                    etag = etag.substring(0, i) + etag.substring(i + "--gzip".length());
                    i = etag.indexOf("--gzip\"", i);
                }
                baseRequest.getHttpFields().put(new HttpField(HttpHeader.IF_NONE_MATCH, etag));
            }
        }
        final HttpOutput.Interceptor orig_interceptor = out.getInterceptor();
        try {
            out.setInterceptor(new GzipHttpOutputInterceptor(this, this.getVaryField(), baseRequest.getHttpChannel(), orig_interceptor, this.isSyncFlush()));
            if (this._handler != null) {
                this._handler.handle(target, baseRequest, request, response);
            }
        }
        finally {
            if (!baseRequest.isHandled() && !baseRequest.isAsyncStarted()) {
                out.setInterceptor(orig_interceptor);
            }
        }
    }
    
    protected boolean isAgentGzipable(final String ua) {
        return ua != null && this._agentPatterns.matches(ua);
    }
    
    @Override
    public boolean isMimeTypeGzipable(final String mimetype) {
        return this._mimeTypes.matches(mimetype);
    }
    
    protected boolean isPathGzipable(final String requestURI) {
        return requestURI == null || this._paths.matches(requestURI);
    }
    
    @Override
    public void recycle(final Deflater deflater) {
        if (this._deflater.get() == null) {
            deflater.reset();
            this._deflater.set(deflater);
        }
        else {
            deflater.end();
        }
    }
    
    public void setCheckGzExists(final boolean checkGzExists) {
        this._checkGzExists = checkGzExists;
    }
    
    public void setCompressionLevel(final int compressionLevel) {
        this._compressionLevel = compressionLevel;
    }
    
    public void setExcludedAgentPatterns(final String... patterns) {
        this._agentPatterns.getExcluded().clear();
        this.addExcludedAgentPatterns(patterns);
    }
    
    public void setExcludedMethods(final String... method) {
        this._methods.getExcluded().clear();
        this._methods.exclude(method);
    }
    
    public void setExcludedMimeTypes(final String... types) {
        this._mimeTypes.getExcluded().clear();
        this._mimeTypes.exclude(types);
    }
    
    public void setExcludedPaths(final String... pathspecs) {
        this._paths.getExcluded().clear();
        this._paths.exclude(pathspecs);
    }
    
    public void setIncludedAgentPatterns(final String... patterns) {
        this._agentPatterns.getIncluded().clear();
        this.addIncludedAgentPatterns(patterns);
    }
    
    public void setIncludedMethods(final String... methods) {
        this._methods.getIncluded().clear();
        this._methods.include(methods);
    }
    
    public void setIncludedMimeTypes(final String... types) {
        this._mimeTypes.getIncluded().clear();
        this._mimeTypes.include(types);
    }
    
    public void setIncludedPaths(final String... pathspecs) {
        this._paths.getIncluded().clear();
        this._paths.include(pathspecs);
    }
    
    public void setMinGzipSize(final int minGzipSize) {
        this._minGzipSize = minGzipSize;
    }
    
    static {
        LOG = Log.getLogger(GzipHandler.class);
    }
}
