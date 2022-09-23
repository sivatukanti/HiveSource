// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.util.zip.GZIPOutputStream;
import org.mortbay.util.ByteArrayOutputStream2;
import javax.servlet.ServletOutputStream;
import org.mortbay.util.StringUtil;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;
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

public class GzipFilter extends UserAgentFilter
{
    protected Set _mimeTypes;
    protected int _bufferSize;
    protected int _minGzipSize;
    protected Set _excluded;
    
    public GzipFilter() {
        this._bufferSize = 8192;
        this._minGzipSize = 0;
    }
    
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
            this._mimeTypes = new HashSet();
            final StringTokenizer tok = new StringTokenizer(tmp, ",", false);
            while (tok.hasMoreTokens()) {
                this._mimeTypes.add(tok.nextToken());
            }
        }
        tmp = filterConfig.getInitParameter("excludedAgents");
        if (tmp != null) {
            this._excluded = new HashSet();
            final StringTokenizer tok = new StringTokenizer(tmp, ",", false);
            while (tok.hasMoreTokens()) {
                this._excluded.add(tok.nextToken());
            }
        }
    }
    
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final String ae = request.getHeader("accept-encoding");
        final Boolean gzip = (Boolean)request.getAttribute("GzipFilter");
        if (ae != null && ae.indexOf("gzip") >= 0 && !response.containsHeader("Content-Encoding") && (gzip == null || gzip) && !"HEAD".equalsIgnoreCase(request.getMethod())) {
            if (this._excluded != null) {
                final String ua = this.getUserAgent(request);
                if (this._excluded.contains(ua)) {
                    super.doFilter(request, response, chain);
                    return;
                }
            }
            final GZIPResponseWrapper wrappedResponse = this.newGZIPResponseWrapper(request, response);
            boolean exceptional = true;
            try {
                super.doFilter(request, wrappedResponse, chain);
                exceptional = false;
            }
            catch (RuntimeException e) {
                request.setAttribute("GzipFilter", Boolean.FALSE);
                if (!response.isCommitted()) {
                    response.reset();
                }
                throw e;
            }
            finally {
                if (exceptional && !response.isCommitted()) {
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
    
    protected GZIPResponseWrapper newGZIPResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        return new GZIPResponseWrapper(request, response);
    }
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        return (encoding == null) ? new PrintWriter(out) : new PrintWriter(new OutputStreamWriter(out, encoding));
    }
    
    public class GZIPResponseWrapper extends HttpServletResponseWrapper
    {
        HttpServletRequest _request;
        boolean _noGzip;
        PrintWriter _writer;
        GzipStream _gzStream;
        long _contentLength;
        
        public GZIPResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
            super(response);
            this._contentLength = -1L;
            this._request = request;
        }
        
        public void setContentType(String ct) {
            super.setContentType(ct);
            if (ct != null) {
                final int colon = ct.indexOf(";");
                if (colon > 0) {
                    ct = ct.substring(0, colon);
                }
            }
            if ((this._gzStream == null || this._gzStream._out == null) && ((GzipFilter.this._mimeTypes == null && "application/gzip".equalsIgnoreCase(ct)) || (GzipFilter.this._mimeTypes != null && (ct == null || !GzipFilter.this._mimeTypes.contains(StringUtil.asciiToLowerCase(ct)))))) {
                this.noGzip();
            }
        }
        
        public void setStatus(final int sc, final String sm) {
            super.setStatus(sc, sm);
            if (sc < 200 || sc >= 300) {
                this.noGzip();
            }
        }
        
        public void setStatus(final int sc) {
            super.setStatus(sc);
            if (sc < 200 || sc >= 300) {
                this.noGzip();
            }
        }
        
        public void setContentLength(final int length) {
            this._contentLength = length;
            if (this._gzStream != null) {
                this._gzStream.setContentLength(length);
            }
        }
        
        public void addHeader(final String name, final String value) {
            if ("content-length".equalsIgnoreCase(name)) {
                this._contentLength = Long.parseLong(value);
                if (this._gzStream != null) {
                    this._gzStream.setContentLength(this._contentLength);
                }
            }
            else if ("content-type".equalsIgnoreCase(name)) {
                this.setContentType(value);
            }
            else if ("content-encoding".equalsIgnoreCase(name)) {
                super.addHeader(name, value);
                if (!this.isCommitted()) {
                    this.noGzip();
                }
            }
            else {
                super.addHeader(name, value);
            }
        }
        
        public void setHeader(final String name, final String value) {
            if ("content-length".equalsIgnoreCase(name)) {
                this._contentLength = Long.parseLong(value);
                if (this._gzStream != null) {
                    this._gzStream.setContentLength(this._contentLength);
                }
            }
            else if ("content-type".equalsIgnoreCase(name)) {
                this.setContentType(value);
            }
            else if ("content-encoding".equalsIgnoreCase(name)) {
                super.setHeader(name, value);
                if (!this.isCommitted()) {
                    this.noGzip();
                }
            }
            else {
                super.setHeader(name, value);
            }
        }
        
        public void setIntHeader(final String name, final int value) {
            if ("content-length".equalsIgnoreCase(name)) {
                this._contentLength = value;
                if (this._gzStream != null) {
                    this._gzStream.setContentLength(this._contentLength);
                }
            }
            else {
                super.setIntHeader(name, value);
            }
        }
        
        public void flushBuffer() throws IOException {
            if (this._writer != null) {
                this._writer.flush();
            }
            if (this._gzStream != null) {
                this._gzStream.finish();
            }
            else {
                this.getResponse().flushBuffer();
            }
        }
        
        public void reset() {
            super.reset();
            if (this._gzStream != null) {
                this._gzStream.resetBuffer();
            }
            this._writer = null;
            this._gzStream = null;
            this._noGzip = false;
            this._contentLength = -1L;
        }
        
        public void resetBuffer() {
            super.resetBuffer();
            if (this._gzStream != null) {
                this._gzStream.resetBuffer();
            }
            this._writer = null;
            this._gzStream = null;
        }
        
        public void sendError(final int sc, final String msg) throws IOException {
            this.resetBuffer();
            super.sendError(sc, msg);
        }
        
        public void sendError(final int sc) throws IOException {
            this.resetBuffer();
            super.sendError(sc);
        }
        
        public void sendRedirect(final String location) throws IOException {
            this.resetBuffer();
            super.sendRedirect(location);
        }
        
        public ServletOutputStream getOutputStream() throws IOException {
            if (this._gzStream == null) {
                if (this.getResponse().isCommitted() || this._noGzip) {
                    return this.getResponse().getOutputStream();
                }
                this._gzStream = this.newGzipStream(this._request, (HttpServletResponse)this.getResponse(), this._contentLength, GzipFilter.this._bufferSize, GzipFilter.this._minGzipSize);
            }
            else if (this._writer != null) {
                throw new IllegalStateException("getWriter() called");
            }
            return this._gzStream;
        }
        
        public PrintWriter getWriter() throws IOException {
            if (this._writer == null) {
                if (this._gzStream != null) {
                    throw new IllegalStateException("getOutputStream() called");
                }
                if (this.getResponse().isCommitted() || this._noGzip) {
                    return this.getResponse().getWriter();
                }
                this._gzStream = this.newGzipStream(this._request, (HttpServletResponse)this.getResponse(), this._contentLength, GzipFilter.this._bufferSize, GzipFilter.this._minGzipSize);
                this._writer = GzipFilter.this.newWriter(this._gzStream, this.getCharacterEncoding());
            }
            return this._writer;
        }
        
        void noGzip() {
            this._noGzip = true;
            if (this._gzStream != null) {
                try {
                    this._gzStream.doNotGzip();
                }
                catch (IOException e) {
                    throw new IllegalStateException();
                }
            }
        }
        
        void finish() throws IOException {
            if (this._writer != null && !this._gzStream._closed) {
                this._writer.flush();
            }
            if (this._gzStream != null) {
                this._gzStream.finish();
            }
        }
        
        protected GzipStream newGzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            return new GzipStream(request, response, contentLength, bufferSize, minGzipSize);
        }
    }
    
    public static class GzipStream extends ServletOutputStream
    {
        protected HttpServletRequest _request;
        protected HttpServletResponse _response;
        protected OutputStream _out;
        protected ByteArrayOutputStream2 _bOut;
        protected GZIPOutputStream _gzOut;
        protected boolean _closed;
        protected int _bufferSize;
        protected int _minGzipSize;
        protected long _contentLength;
        
        public GzipStream(final HttpServletRequest request, final HttpServletResponse response, final long contentLength, final int bufferSize, final int minGzipSize) throws IOException {
            this._request = request;
            this._response = response;
            this._contentLength = contentLength;
            this._bufferSize = bufferSize;
            this._minGzipSize = minGzipSize;
            if (minGzipSize == 0) {
                this.doGzip();
            }
        }
        
        public void resetBuffer() {
            this._closed = false;
            this._out = null;
            this._bOut = null;
            if (this._gzOut != null && !this._response.isCommitted()) {
                this._response.setHeader("Content-Encoding", null);
            }
            this._gzOut = null;
        }
        
        public void setContentLength(final long length) {
            this._contentLength = length;
        }
        
        public void flush() throws IOException {
            if (this._out == null || this._bOut != null) {
                if (this._contentLength > 0L && this._contentLength < this._minGzipSize) {
                    this.doNotGzip();
                }
                else {
                    this.doGzip();
                }
            }
            this._out.flush();
        }
        
        public void close() throws IOException {
            if (this._request.getAttribute("javax.servlet.include.request_uri") != null) {
                this.flush();
            }
            else {
                if (this._bOut != null) {
                    if (this._contentLength < 0L) {
                        this._contentLength = this._bOut.getCount();
                    }
                    if (this._contentLength < this._minGzipSize) {
                        this.doNotGzip();
                    }
                    else {
                        this.doGzip();
                    }
                }
                else if (this._out == null) {
                    this.doNotGzip();
                }
                if (this._gzOut != null) {
                    this._gzOut.close();
                }
                else {
                    this._out.close();
                }
                this._closed = true;
            }
        }
        
        public void finish() throws IOException {
            if (!this._closed) {
                if (this._out == null || this._bOut != null) {
                    if (this._contentLength > 0L && this._contentLength < this._minGzipSize) {
                        this.doNotGzip();
                    }
                    else {
                        this.doGzip();
                    }
                }
                if (this._gzOut != null && !this._closed) {
                    this._closed = true;
                    this._gzOut.close();
                }
            }
        }
        
        public void write(final int b) throws IOException {
            this.checkOut(1);
            this._out.write(b);
        }
        
        public void write(final byte[] b) throws IOException {
            this.checkOut(b.length);
            this._out.write(b);
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.checkOut(len);
            this._out.write(b, off, len);
        }
        
        protected boolean setContentEncodingGzip() {
            this._response.setHeader("Content-Encoding", "gzip");
            return this._response.containsHeader("Content-Encoding");
        }
        
        public void doGzip() throws IOException {
            if (this._gzOut == null) {
                if (this._response.isCommitted()) {
                    throw new IllegalStateException();
                }
                if (this.setContentEncodingGzip()) {
                    final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(this._response.getOutputStream(), this._bufferSize);
                    this._gzOut = gzipOutputStream;
                    this._out = gzipOutputStream;
                    if (this._bOut != null) {
                        this._out.write(this._bOut.getBuf(), 0, this._bOut.getCount());
                        this._bOut = null;
                    }
                }
                else {
                    this.doNotGzip();
                }
            }
        }
        
        public void doNotGzip() throws IOException {
            if (this._gzOut != null) {
                throw new IllegalStateException();
            }
            if (this._out == null || this._bOut != null) {
                this._out = this._response.getOutputStream();
                if (this._contentLength >= 0L) {
                    if (this._contentLength < 2147483647L) {
                        this._response.setContentLength((int)this._contentLength);
                    }
                    else {
                        this._response.setHeader("Content-Length", Long.toString(this._contentLength));
                    }
                }
                if (this._bOut != null) {
                    this._out.write(this._bOut.getBuf(), 0, this._bOut.getCount());
                }
                this._bOut = null;
            }
        }
        
        private void checkOut(final int length) throws IOException {
            if (this._closed) {
                throw new IOException("CLOSED");
            }
            if (this._out == null) {
                if (this._response.isCommitted() || (this._contentLength >= 0L && this._contentLength < this._minGzipSize)) {
                    this.doNotGzip();
                }
                else if (length > this._minGzipSize) {
                    this.doGzip();
                }
                else {
                    final ByteArrayOutputStream2 byteArrayOutputStream2 = new ByteArrayOutputStream2(this._bufferSize);
                    this._bOut = byteArrayOutputStream2;
                    this._out = byteArrayOutputStream2;
                }
            }
            else if (this._bOut != null) {
                if (this._response.isCommitted() || (this._contentLength >= 0L && this._contentLength < this._minGzipSize)) {
                    this.doNotGzip();
                }
                else if (length >= this._bOut.getBuf().length - this._bOut.getCount()) {
                    this.doGzip();
                }
            }
        }
    }
}
