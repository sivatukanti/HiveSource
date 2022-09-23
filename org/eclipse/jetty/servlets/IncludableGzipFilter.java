// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import java.io.IOException;
import org.eclipse.jetty.http.gzip.GzipStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.eclipse.jetty.io.UncheckedPrintWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import org.eclipse.jetty.http.gzip.GzipResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;

public class IncludableGzipFilter extends GzipFilter
{
    boolean _uncheckedPrintWriter;
    
    public IncludableGzipFilter() {
        this._uncheckedPrintWriter = false;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        final String tmp = filterConfig.getInitParameter("uncheckedPrintWriter");
        if (tmp != null) {
            this._uncheckedPrintWriter = Boolean.valueOf(tmp);
        }
    }
    
    @Override
    protected GzipResponseWrapper newGzipResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        return new IncludableResponseWrapper(request, response);
    }
    
    @Override
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        if (this._uncheckedPrintWriter) {
            return (encoding == null) ? new UncheckedPrintWriter(out) : new UncheckedPrintWriter(new OutputStreamWriter(out, encoding));
        }
        return super.newWriter(out, encoding);
    }
    
    public class IncludableResponseWrapper extends GzipResponseWrapper
    {
        public IncludableResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
            super(request, response);
            super.setMimeTypes(IncludableGzipFilter.this._mimeTypes);
            super.setBufferSize(IncludableGzipFilter.this._bufferSize);
            super.setMinGzipSize(IncludableGzipFilter.this._minGzipSize);
        }
        
        @Override
        protected GzipStream newGzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            return new IncludableGzipStream(request, response, contentLength, bufferSize, minGzipSize);
        }
        
        @Override
        protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
            return IncludableGzipFilter.this.newWriter(out, encoding);
        }
    }
    
    public class IncludableGzipStream extends GzipStream
    {
        public IncludableGzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            super(request, response, contentLength, bufferSize, minGzipSize);
        }
        
        @Override
        protected boolean setContentEncodingGzip() {
            if (this._request.getAttribute("javax.servlet.include.request_uri") != null) {
                this._response.setHeader("org.eclipse.jetty.server.include.Content-Encoding", "gzip");
            }
            else {
                this._response.setHeader("Content-Encoding", "gzip");
            }
            return this._response.containsHeader("Content-Encoding");
        }
    }
}
