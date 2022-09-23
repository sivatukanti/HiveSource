// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.bio;

import java.net.SocketTimeoutException;
import org.eclipse.jetty.io.Buffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.eclipse.jetty.io.EndPoint;

public class StreamEndPoint implements EndPoint
{
    InputStream _in;
    OutputStream _out;
    int _maxIdleTime;
    boolean _ishut;
    boolean _oshut;
    
    public StreamEndPoint(final InputStream in, final OutputStream out) {
        this._in = in;
        this._out = out;
    }
    
    public boolean isBlocking() {
        return true;
    }
    
    public boolean blockReadable(final long millisecs) throws IOException {
        return true;
    }
    
    public boolean blockWritable(final long millisecs) throws IOException {
        return true;
    }
    
    public boolean isOpen() {
        return this._in != null;
    }
    
    public final boolean isClosed() {
        return !this.isOpen();
    }
    
    public void shutdownOutput() throws IOException {
        this._oshut = true;
        if (this._ishut && this._out != null) {
            this._out.close();
        }
    }
    
    public boolean isInputShutdown() {
        return this._ishut;
    }
    
    public void shutdownInput() throws IOException {
        this._ishut = true;
        if (this._oshut && this._in != null) {
            this._in.close();
        }
    }
    
    public boolean isOutputShutdown() {
        return this._oshut;
    }
    
    public void close() throws IOException {
        if (this._in != null) {
            this._in.close();
        }
        this._in = null;
        if (this._out != null) {
            this._out.close();
        }
        this._out = null;
    }
    
    protected void idleExpired() throws IOException {
        if (this._in != null) {
            this._in.close();
        }
    }
    
    public int fill(final Buffer buffer) throws IOException {
        if (this._ishut) {
            return -1;
        }
        if (this._in == null) {
            return 0;
        }
        final int space = buffer.space();
        if (space <= 0) {
            if (buffer.hasContent()) {
                return 0;
            }
            throw new IOException("FULL");
        }
        else {
            try {
                final int filled = buffer.readFrom(this._in, space);
                if (filled < 0) {
                    this.shutdownInput();
                }
                return filled;
            }
            catch (SocketTimeoutException e) {
                this.idleExpired();
                return -1;
            }
        }
    }
    
    public int flush(final Buffer buffer) throws IOException {
        if (this._oshut) {
            return -1;
        }
        if (this._out == null) {
            return 0;
        }
        final int length = buffer.length();
        if (length > 0) {
            buffer.writeTo(this._out);
        }
        if (!buffer.isImmutable()) {
            buffer.clear();
        }
        return length;
    }
    
    public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
        int len = 0;
        if (header != null) {
            final int tw = header.length();
            if (tw > 0) {
                final int f = this.flush(header);
                if ((len = f) < tw) {
                    return len;
                }
            }
        }
        if (buffer != null) {
            final int tw = buffer.length();
            if (tw > 0) {
                final int f = this.flush(buffer);
                if (f < 0) {
                    return (len > 0) ? len : f;
                }
                len += f;
                if (f < tw) {
                    return len;
                }
            }
        }
        if (trailer != null) {
            final int tw = trailer.length();
            if (tw > 0) {
                final int f = this.flush(trailer);
                if (f < 0) {
                    return (len > 0) ? len : f;
                }
                len += f;
            }
        }
        return len;
    }
    
    public String getLocalAddr() {
        return null;
    }
    
    public String getLocalHost() {
        return null;
    }
    
    public int getLocalPort() {
        return 0;
    }
    
    public String getRemoteAddr() {
        return null;
    }
    
    public String getRemoteHost() {
        return null;
    }
    
    public int getRemotePort() {
        return 0;
    }
    
    public Object getTransport() {
        return null;
    }
    
    public InputStream getInputStream() {
        return this._in;
    }
    
    public void setInputStream(final InputStream in) {
        this._in = in;
    }
    
    public OutputStream getOutputStream() {
        return this._out;
    }
    
    public void setOutputStream(final OutputStream out) {
        this._out = out;
    }
    
    public void flush() throws IOException {
        if (this._out != null) {
            this._out.flush();
        }
    }
    
    public int getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public void setMaxIdleTime(final int timeMs) throws IOException {
        this._maxIdleTime = timeMs;
    }
}
