// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.OutputStream;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.http.gzip.GzipResponseWrapper;
import org.eclipse.jetty.continuation.ContinuationListener;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.continuation.ContinuationSupport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;

public class GzipHandler extends HandlerWrapper
{
    private static final Logger LOG;
    protected Set<String> _mimeTypes;
    protected Set<String> _excluded;
    protected int _bufferSize;
    protected int _minGzipSize;
    
    public GzipHandler() {
        this._bufferSize = 8192;
        this._minGzipSize = 256;
    }
    
    public Set<String> getMimeTypes() {
        return this._mimeTypes;
    }
    
    public void setMimeTypes(final Set<String> mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    public void setMimeTypes(final String mimeTypes) {
        if (mimeTypes != null) {
            this._mimeTypes = new HashSet<String>();
            final StringTokenizer tok = new StringTokenizer(mimeTypes, ",", false);
            while (tok.hasMoreTokens()) {
                this._mimeTypes.add(tok.nextToken());
            }
        }
    }
    
    public Set<String> getExcluded() {
        return this._excluded;
    }
    
    public void setExcluded(final Set<String> excluded) {
        this._excluded = excluded;
    }
    
    public void setExcluded(final String excluded) {
        if (excluded != null) {
            this._excluded = new HashSet<String>();
            final StringTokenizer tok = new StringTokenizer(excluded, ",", false);
            while (tok.hasMoreTokens()) {
                this._excluded.add(tok.nextToken());
            }
        }
    }
    
    public int getBufferSize() {
        return this._bufferSize;
    }
    
    public void setBufferSize(final int bufferSize) {
        this._bufferSize = bufferSize;
    }
    
    public int getMinGzipSize() {
        return this._minGzipSize;
    }
    
    public void setMinGzipSize(final int minGzipSize) {
        this._minGzipSize = minGzipSize;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this._handler != null && this.isStarted()) {
            final String ae = request.getHeader("accept-encoding");
            if (ae != null && ae.indexOf("gzip") >= 0 && !response.containsHeader("Content-Encoding") && !"HEAD".equalsIgnoreCase(request.getMethod())) {
                if (this._excluded != null) {
                    final String ua = request.getHeader("User-Agent");
                    if (this._excluded.contains(ua)) {
                        this._handler.handle(target, baseRequest, request, response);
                        return;
                    }
                }
                final GzipResponseWrapper wrappedResponse = this.newGzipResponseWrapper(request, response);
                boolean exceptional = true;
                try {
                    this._handler.handle(target, baseRequest, request, wrappedResponse);
                    exceptional = false;
                }
                finally {
                    final Continuation continuation = ContinuationSupport.getContinuation(request);
                    if (continuation.isSuspended() && continuation.isResponseWrapped()) {
                        continuation.addContinuationListener(new ContinuationListener() {
                            public void onComplete(final Continuation continuation) {
                                try {
                                    wrappedResponse.finish();
                                }
                                catch (IOException e) {
                                    GzipHandler.LOG.warn(e);
                                }
                            }
                            
                            public void onTimeout(final Continuation continuation) {
                            }
                        });
                    }
                    else if (exceptional && !response.isCommitted()) {
                        wrappedResponse.resetBuffer();
                        wrappedResponse.noGzip();
                    }
                    else {
                        wrappedResponse.finish();
                    }
                }
            }
            else {
                this._handler.handle(target, baseRequest, request, response);
            }
        }
    }
    
    protected GzipResponseWrapper newGzipResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        return new GzipResponseWrapper(request, response) {
            {
                super.setMimeTypes(GzipHandler.this._mimeTypes);
                super.setBufferSize(GzipHandler.this._bufferSize);
                super.setMinGzipSize(GzipHandler.this._minGzipSize);
            }
            
            @Override
            protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
                return GzipHandler.this.newWriter(out, encoding);
            }
        };
    }
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        return (encoding == null) ? new PrintWriter(out) : new PrintWriter(new OutputStreamWriter(out, encoding));
    }
    
    static {
        LOG = Log.getLogger(GzipHandler.class);
    }
}
