// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.bio;

import org.mortbay.io.Buffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.mortbay.io.EndPoint;

public class StreamEndPoint implements EndPoint
{
    InputStream _in;
    OutputStream _out;
    
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
    
    public int fill(final Buffer buffer) throws IOException {
        if (this._in == null) {
            return 0;
        }
        final int space = buffer.space();
        if (space > 0) {
            final int len = buffer.readFrom(this._in, space);
            return len;
        }
        if (buffer.hasContent()) {
            return 0;
        }
        throw new IOException("FULL");
    }
    
    public int flush(final Buffer buffer) throws IOException {
        if (this._out == null) {
            return -1;
        }
        final int length = buffer.length();
        if (length > 0) {
            buffer.writeTo(this._out);
        }
        buffer.clear();
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
        this._out.flush();
    }
    
    public boolean isBufferingInput() {
        return false;
    }
    
    public boolean isBufferingOutput() {
        return false;
    }
    
    public boolean isBufferred() {
        return false;
    }
}
