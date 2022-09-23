// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.gzip;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import org.eclipse.jetty.util.StringUtil;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

public class GzipResponseWrapper extends HttpServletResponseWrapper
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_MIN_GZIP_SIZE = 256;
    private HttpServletRequest _request;
    private Set<String> _mimeTypes;
    private int _bufferSize;
    private int _minGzipSize;
    private PrintWriter _writer;
    private GzipStream _gzStream;
    private long _contentLength;
    private boolean _noGzip;
    
    public GzipResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
        super(response);
        this._bufferSize = 8192;
        this._minGzipSize = 256;
        this._contentLength = -1L;
        this._request = request;
    }
    
    public void setMimeTypes(final Set<String> mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    @Override
    public void setBufferSize(final int bufferSize) {
        this._bufferSize = bufferSize;
    }
    
    public void setMinGzipSize(final int minGzipSize) {
        this._minGzipSize = minGzipSize;
    }
    
    @Override
    public void setContentType(String ct) {
        super.setContentType(ct);
        if (ct != null) {
            final int colon = ct.indexOf(";");
            if (colon > 0) {
                ct = ct.substring(0, colon);
            }
        }
        if ((this._gzStream == null || this._gzStream._out == null) && ((this._mimeTypes == null && "application/gzip".equalsIgnoreCase(ct)) || (this._mimeTypes != null && (ct == null || !this._mimeTypes.contains(StringUtil.asciiToLowerCase(ct)))))) {
            this.noGzip();
        }
    }
    
    @Override
    public void setStatus(final int sc, final String sm) {
        super.setStatus(sc, sm);
        if (sc < 200 || sc == 204 || sc == 205 || sc >= 300) {
            this.noGzip();
        }
    }
    
    @Override
    public void setStatus(final int sc) {
        super.setStatus(sc);
        if (sc < 200 || sc == 204 || sc == 205 || sc >= 300) {
            this.noGzip();
        }
    }
    
    @Override
    public void setContentLength(final int length) {
        this.setContentLength((long)length);
    }
    
    protected void setContentLength(final long length) {
        this._contentLength = length;
        if (this._gzStream != null) {
            this._gzStream.setContentLength(length);
        }
        else if (this._noGzip && this._contentLength >= 0L) {
            final HttpServletResponse response = (HttpServletResponse)this.getResponse();
            if (this._contentLength < 2147483647L) {
                response.setContentLength((int)this._contentLength);
            }
            else {
                response.setHeader("Content-Length", Long.toString(this._contentLength));
            }
        }
    }
    
    @Override
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
    
    @Override
    public void setHeader(final String name, final String value) {
        if ("content-length".equalsIgnoreCase(name)) {
            this.setContentLength(Long.parseLong(value));
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
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
    public void resetBuffer() {
        super.resetBuffer();
        if (this._gzStream != null) {
            this._gzStream.resetBuffer();
        }
        this._writer = null;
        this._gzStream = null;
    }
    
    @Override
    public void sendError(final int sc, final String msg) throws IOException {
        this.resetBuffer();
        super.sendError(sc, msg);
    }
    
    @Override
    public void sendError(final int sc) throws IOException {
        this.resetBuffer();
        super.sendError(sc);
    }
    
    @Override
    public void sendRedirect(final String location) throws IOException {
        this.resetBuffer();
        super.sendRedirect(location);
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this._gzStream == null) {
            if (this.getResponse().isCommitted() || this._noGzip) {
                this.setContentLength(this._contentLength);
                return this.getResponse().getOutputStream();
            }
            this._gzStream = this.newGzipStream(this._request, (HttpServletResponse)this.getResponse(), this._contentLength, this._bufferSize, this._minGzipSize);
        }
        else if (this._writer != null) {
            throw new IllegalStateException("getWriter() called");
        }
        return this._gzStream;
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        if (this._writer == null) {
            if (this._gzStream != null) {
                throw new IllegalStateException("getOutputStream() called");
            }
            if (this.getResponse().isCommitted() || this._noGzip) {
                this.setContentLength(this._contentLength);
                return this.getResponse().getWriter();
            }
            this._gzStream = this.newGzipStream(this._request, (HttpServletResponse)this.getResponse(), this._contentLength, this._bufferSize, this._minGzipSize);
            this._writer = this.newWriter(this._gzStream, this.getCharacterEncoding());
        }
        return this._writer;
    }
    
    public void noGzip() {
        this._noGzip = true;
        if (this._gzStream != null) {
            try {
                this._gzStream.doNotGzip();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    public void finish() throws IOException {
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
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        return (encoding == null) ? new PrintWriter(out) : new PrintWriter(new OutputStreamWriter(out, encoding));
    }
}
