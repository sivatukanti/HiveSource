// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet.jetty;

import org.mortbay.jetty.HttpConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.mortbay.io.UncheckedPrintWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import org.mortbay.servlet.GzipFilter;

public class IncludableGzipFilter extends GzipFilter
{
    boolean _uncheckedPrintWriter;
    
    public IncludableGzipFilter() {
        this._uncheckedPrintWriter = false;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        final String tmp = filterConfig.getInitParameter("uncheckedPrintWriter");
        if (tmp != null) {
            this._uncheckedPrintWriter = Boolean.valueOf(tmp);
        }
    }
    
    protected GZIPResponseWrapper newGZIPResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        return new IncludableResponseWrapper(request, response);
    }
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        if (this._uncheckedPrintWriter) {
            return (encoding == null) ? new UncheckedPrintWriter(out) : new UncheckedPrintWriter(new OutputStreamWriter(out, encoding));
        }
        return super.newWriter(out, encoding);
    }
    
    public class IncludableResponseWrapper extends GZIPResponseWrapper
    {
        public IncludableResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
            super(request, response);
        }
        
        protected GzipStream newGzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            return new IncludableGzipStream(request, response, contentLength, bufferSize, minGzipSize);
        }
    }
    
    public class IncludableGzipStream extends GzipStream
    {
        public IncludableGzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            super(request, response, contentLength, bufferSize, minGzipSize);
        }
        
        protected boolean setContentEncodingGzip() {
            final HttpConnection connection = HttpConnection.getCurrentConnection();
            connection.getResponseFields().put("Content-Encoding", "gzip");
            return true;
        }
    }
}
