// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.gzip;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletOutputStream;

public class GzipStream extends ServletOutputStream
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
    protected boolean _doNotGzip;
    
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
        if (this._response.isCommitted()) {
            throw new IllegalStateException("Committed");
        }
        this._closed = false;
        this._out = null;
        this._bOut = null;
        if (this._gzOut != null) {
            this._response.setHeader("Content-Encoding", null);
        }
        this._gzOut = null;
        this._doNotGzip = false;
    }
    
    public void setContentLength(final long length) {
        this._contentLength = length;
        if (this._doNotGzip && length >= 0L) {
            if (this._contentLength < 2147483647L) {
                this._response.setContentLength((int)this._contentLength);
            }
            else {
                this._response.setHeader("Content-Length", Long.toString(this._contentLength));
            }
        }
    }
    
    @Override
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
    
    @Override
    public void close() throws IOException {
        if (this._closed) {
            return;
        }
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
    
    @Override
    public void write(final int b) throws IOException {
        this.checkOut(1);
        this._out.write(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.checkOut(b.length);
        this._out.write(b);
    }
    
    @Override
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
            this._doNotGzip = true;
            this._out = this._response.getOutputStream();
            this.setContentLength(this._contentLength);
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
    
    protected PrintWriter newWriter(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        return (encoding == null) ? new PrintWriter(out) : new PrintWriter(new OutputStreamWriter(out, encoding));
    }
}
