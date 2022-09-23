// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.IOException;

public class ByteArrayEndPoint implements EndPoint
{
    byte[] _inBytes;
    ByteArrayBuffer _in;
    ByteArrayBuffer _out;
    boolean _closed;
    boolean _nonBlocking;
    boolean _growOutput;
    
    public ByteArrayEndPoint() {
    }
    
    public boolean isNonBlocking() {
        return this._nonBlocking;
    }
    
    public void setNonBlocking(final boolean nonBlocking) {
        this._nonBlocking = nonBlocking;
    }
    
    public ByteArrayEndPoint(final byte[] input, final int outputSize) {
        this._inBytes = input;
        this._in = new ByteArrayBuffer(input);
        this._out = new ByteArrayBuffer(outputSize);
    }
    
    public ByteArrayBuffer getIn() {
        return this._in;
    }
    
    public void setIn(final ByteArrayBuffer in) {
        this._in = in;
    }
    
    public ByteArrayBuffer getOut() {
        return this._out;
    }
    
    public void setOut(final ByteArrayBuffer out) {
        this._out = out;
    }
    
    public boolean isOpen() {
        return !this._closed;
    }
    
    public boolean isBlocking() {
        return !this._nonBlocking;
    }
    
    public boolean blockReadable(final long millisecs) {
        return true;
    }
    
    public boolean blockWritable(final long millisecs) {
        return true;
    }
    
    public void shutdownOutput() throws IOException {
    }
    
    public void close() throws IOException {
        this._closed = true;
    }
    
    public int fill(final Buffer buffer) throws IOException {
        if (this._closed) {
            throw new IOException("CLOSED");
        }
        if (this._in == null) {
            return -1;
        }
        if (this._in.length() <= 0) {
            return this._nonBlocking ? 0 : -1;
        }
        final int len = buffer.put(this._in);
        this._in.skip(len);
        return len;
    }
    
    public int flush(final Buffer buffer) throws IOException {
        if (this._closed) {
            throw new IOException("CLOSED");
        }
        if (this._growOutput && buffer.length() > this._out.space()) {
            this._out.compact();
            if (buffer.length() > this._out.space()) {
                final ByteArrayBuffer n = new ByteArrayBuffer(this._out.putIndex() + buffer.length());
                n.put(this._out.peek(0, this._out.putIndex()));
                if (this._out.getIndex() > 0) {
                    n.mark();
                    n.setGetIndex(this._out.getIndex());
                }
                this._out = n;
            }
        }
        final int len = this._out.put(buffer);
        buffer.skip(len);
        return len;
    }
    
    public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
        if (this._closed) {
            throw new IOException("CLOSED");
        }
        int flushed = 0;
        if (header != null && header.length() > 0) {
            flushed = this.flush(header);
        }
        if (header == null || header.length() == 0) {
            if (buffer != null && buffer.length() > 0) {
                flushed += this.flush(buffer);
            }
            if ((buffer == null || buffer.length() == 0) && trailer != null && trailer.length() > 0) {
                flushed += this.flush(trailer);
            }
        }
        return flushed;
    }
    
    public void reset() {
        this._closed = false;
        this._in.clear();
        this._out.clear();
        if (this._inBytes != null) {
            this._in.setPutIndex(this._inBytes.length);
        }
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
        return this._inBytes;
    }
    
    public void flush() throws IOException {
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
    
    public boolean isGrowOutput() {
        return this._growOutput;
    }
    
    public void setGrowOutput(final boolean growOutput) {
        this._growOutput = growOutput;
    }
}
