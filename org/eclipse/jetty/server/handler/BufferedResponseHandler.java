// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.ByteBuffer;
import java.util.Queue;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.Iterator;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.pathmap.PathSpecSet;
import org.eclipse.jetty.util.IncludeExclude;
import org.eclipse.jetty.util.log.Logger;

public class BufferedResponseHandler extends HandlerWrapper
{
    static final Logger LOG;
    private final IncludeExclude<String> _methods;
    private final IncludeExclude<String> _paths;
    private final IncludeExclude<String> _mimeTypes;
    
    public BufferedResponseHandler() {
        this._methods = new IncludeExclude<String>();
        this._paths = new IncludeExclude<String>((Class<SET>)PathSpecSet.class);
        this._mimeTypes = new IncludeExclude<String>();
        this._methods.include(HttpMethod.GET.asString());
        for (final String type : MimeTypes.getKnownMimeTypes()) {
            if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
                this._mimeTypes.exclude(type);
            }
        }
        BufferedResponseHandler.LOG.debug("{} mime types {}", this, this._mimeTypes);
    }
    
    public IncludeExclude<String> getMethodIncludeExclude() {
        return this._methods;
    }
    
    public IncludeExclude<String> getPathIncludeExclude() {
        return this._paths;
    }
    
    public IncludeExclude<String> getMimeIncludeExclude() {
        return this._mimeTypes;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final ServletContext context = baseRequest.getServletContext();
        final String path = (context == null) ? baseRequest.getRequestURI() : URIUtil.addPaths(baseRequest.getServletPath(), baseRequest.getPathInfo());
        BufferedResponseHandler.LOG.debug("{} handle {} in {}", this, baseRequest, context);
        final HttpOutput out = baseRequest.getResponse().getHttpOutput();
        for (HttpOutput.Interceptor interceptor = out.getInterceptor(); interceptor != null; interceptor = interceptor.getNextInterceptor()) {
            if (interceptor instanceof BufferedInterceptor) {
                BufferedResponseHandler.LOG.debug("{} already intercepting {}", this, request);
                this._handler.handle(target, baseRequest, request, response);
                return;
            }
        }
        if (!this._methods.matches(baseRequest.getMethod())) {
            BufferedResponseHandler.LOG.debug("{} excluded by method {}", this, request);
            this._handler.handle(target, baseRequest, request, response);
            return;
        }
        if (!this.isPathBufferable(path)) {
            BufferedResponseHandler.LOG.debug("{} excluded by path {}", this, request);
            this._handler.handle(target, baseRequest, request, response);
            return;
        }
        String mimeType = (context == null) ? MimeTypes.getDefaultMimeByExtension(path) : context.getMimeType(path);
        if (mimeType != null) {
            mimeType = MimeTypes.getContentTypeWithoutCharset(mimeType);
            if (!this.isMimeTypeBufferable(mimeType)) {
                BufferedResponseHandler.LOG.debug("{} excluded by path suffix mime type {}", this, request);
                this._handler.handle(target, baseRequest, request, response);
                return;
            }
        }
        out.setInterceptor(new BufferedInterceptor(baseRequest.getHttpChannel(), out.getInterceptor()));
        if (this._handler != null) {
            this._handler.handle(target, baseRequest, request, response);
        }
    }
    
    protected boolean isMimeTypeBufferable(final String mimetype) {
        return this._mimeTypes.matches(mimetype);
    }
    
    protected boolean isPathBufferable(final String requestURI) {
        return requestURI == null || this._paths.matches(requestURI);
    }
    
    static {
        LOG = Log.getLogger(BufferedResponseHandler.class);
    }
    
    private class BufferedInterceptor implements HttpOutput.Interceptor
    {
        final HttpOutput.Interceptor _next;
        final HttpChannel _channel;
        final Queue<ByteBuffer> _buffers;
        Boolean _aggregating;
        ByteBuffer _aggregate;
        
        public BufferedInterceptor(final HttpChannel httpChannel, final HttpOutput.Interceptor interceptor) {
            this._buffers = new ConcurrentLinkedQueue<ByteBuffer>();
            this._next = interceptor;
            this._channel = httpChannel;
        }
        
        @Override
        public void resetBuffer() {
            this._buffers.clear();
            this._aggregating = null;
            this._aggregate = null;
        }
        
        @Override
        public void write(final ByteBuffer content, final boolean last, final Callback callback) {
            if (BufferedResponseHandler.LOG.isDebugEnabled()) {
                BufferedResponseHandler.LOG.debug("{} write last={} {}", this, last, BufferUtil.toDetailString(content));
            }
            if (this._aggregating == null) {
                final Response response = this._channel.getResponse();
                final int sc = response.getStatus();
                if (sc > 0 && (sc < 200 || sc == 204 || sc == 205 || sc >= 300)) {
                    this._aggregating = Boolean.FALSE;
                }
                else {
                    String ct = response.getContentType();
                    if (ct == null) {
                        this._aggregating = Boolean.TRUE;
                    }
                    else {
                        ct = MimeTypes.getContentTypeWithoutCharset(ct);
                        this._aggregating = BufferedResponseHandler.this.isMimeTypeBufferable(StringUtil.asciiToLowerCase(ct));
                    }
                }
            }
            if (!this._aggregating) {
                this.getNextInterceptor().write(content, last, callback);
                return;
            }
            if (last) {
                if (BufferUtil.length(content) > 0) {
                    this._buffers.add(content);
                }
                if (BufferedResponseHandler.LOG.isDebugEnabled()) {
                    BufferedResponseHandler.LOG.debug("{} committing {}", this, this._buffers.size());
                }
                this.commit(this._buffers, callback);
            }
            else {
                if (BufferedResponseHandler.LOG.isDebugEnabled()) {
                    BufferedResponseHandler.LOG.debug("{} aggregating", this);
                }
                while (BufferUtil.hasContent(content)) {
                    if (BufferUtil.space(this._aggregate) == 0) {
                        final int size = Math.max(this._channel.getHttpConfiguration().getOutputBufferSize(), BufferUtil.length(content));
                        this._aggregate = BufferUtil.allocate(size);
                        this._buffers.add(this._aggregate);
                    }
                    BufferUtil.append(this._aggregate, content);
                }
                callback.succeeded();
            }
        }
        
        @Override
        public HttpOutput.Interceptor getNextInterceptor() {
            return this._next;
        }
        
        @Override
        public boolean isOptimizedForDirectBuffers() {
            return false;
        }
        
        protected void commit(final Queue<ByteBuffer> buffers, final Callback callback) {
            if (this._buffers.size() == 0) {
                this.getNextInterceptor().write(BufferUtil.EMPTY_BUFFER, true, callback);
            }
            else if (this._buffers.size() == 1) {
                this.getNextInterceptor().write(this._buffers.remove(), true, callback);
            }
            else {
                final IteratingCallback icb = new IteratingCallback() {
                    @Override
                    protected Action process() throws Exception {
                        final ByteBuffer buffer = BufferedInterceptor.this._buffers.poll();
                        if (buffer == null) {
                            return Action.SUCCEEDED;
                        }
                        BufferedInterceptor.this.getNextInterceptor().write(buffer, BufferedInterceptor.this._buffers.isEmpty(), this);
                        return Action.SCHEDULED;
                    }
                    
                    @Override
                    protected void onCompleteSuccess() {
                        callback.succeeded();
                    }
                    
                    @Override
                    protected void onCompleteFailure(final Throwable cause) {
                        callback.failed(cause);
                    }
                };
                icb.iterate();
            }
        }
    }
}
