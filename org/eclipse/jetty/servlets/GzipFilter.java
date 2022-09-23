// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import org.eclipse.jetty.util.log.Log;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.http.gzip.GzipResponseWrapper;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.StringTokenizer;
import java.util.HashSet;
import javax.servlet.FilterConfig;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;

public class GzipFilter extends UserAgentFilter
{
    private static final Logger LOG;
    protected Set<String> _mimeTypes;
    protected int _bufferSize;
    protected int _minGzipSize;
    protected Set<String> _excluded;
    
    public GzipFilter() {
        this._bufferSize = 8192;
        this._minGzipSize = 256;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        String tmp = filterConfig.getInitParameter("bufferSize");
        if (tmp != null) {
            this._bufferSize = Integer.parseInt(tmp);
        }
        tmp = filterConfig.getInitParameter("minGzipSize");
        if (tmp != null) {
            this._minGzipSize = Integer.parseInt(tmp);
        }
        tmp = filterConfig.getInitParameter("mimeTypes");
        if (tmp != null) {
            this._mimeTypes = new HashSet<String>();
            final StringTokenizer tok = new StringTokenizer(tmp, ",", false);
            while (tok.hasMoreTokens()) {
                this._mimeTypes.add(tok.nextToken());
            }
        }
        tmp = filterConfig.getInitParameter("excludedAgents");
        if (tmp != null) {
            this._excluded = new HashSet<String>();
            final StringTokenizer tok = new StringTokenizer(tmp, ",", false);
            while (tok.hasMoreTokens()) {
                this._excluded.add(tok.nextToken());
            }
        }
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final String ae = request.getHeader("accept-encoding");
        if (ae != null && ae.indexOf("gzip") >= 0 && !response.containsHeader("Content-Encoding") && !"HEAD".equalsIgnoreCase(request.getMethod())) {
            if (this._excluded != null) {
                final String ua = this.getUserAgent(request);
                if (this._excluded.contains(ua)) {
                    super.doFilter(request, response, chain);
                    return;
                }
            }
            final GzipResponseWrapper wrappedResponse = this.newGzipResponseWrapper(request, response);
            boolean exceptional = true;
            try {
                super.doFilter(request, wrappedResponse, chain);
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
                                GzipFilter.LOG.warn(e);
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
            super.doFilter(request, response, chain);
        }
    }
    
    protected GzipResponseWrapper newGzipResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        return new GzipResponseWrapper(request, response) {
            {
                this.setMimeTypes(GzipFilter.this._mimeTypes);
                this.setBufferSize(GzipFilter.this._bufferSize);
                this.setMinGzipSize(GzipFilter.this._minGzipSize);
            }
            
            @Override
            protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
                return GzipFilter.this.newWriter(out, encoding);
            }
        };
    }
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        return (encoding == null) ? new PrintWriter(out) : new PrintWriter(new OutputStreamWriter(out, encoding));
    }
    
    static {
        LOG = Log.getLogger(GzipFilter.class);
    }
}
