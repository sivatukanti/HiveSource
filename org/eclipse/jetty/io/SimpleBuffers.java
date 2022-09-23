// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public class SimpleBuffers implements Buffers
{
    final Buffer _header;
    final Buffer _buffer;
    boolean _headerOut;
    boolean _bufferOut;
    
    public SimpleBuffers(final Buffer header, final Buffer buffer) {
        this._header = header;
        this._buffer = buffer;
    }
    
    public Buffer getBuffer() {
        synchronized (this) {
            if (this._buffer != null && !this._bufferOut) {
                this._bufferOut = true;
                return this._buffer;
            }
            if (this._buffer != null && this._header != null && this._header.capacity() == this._buffer.capacity() && !this._headerOut) {
                this._headerOut = true;
                return this._header;
            }
            if (this._buffer != null) {
                return new ByteArrayBuffer(this._buffer.capacity());
            }
            return new ByteArrayBuffer(4096);
        }
    }
    
    public Buffer getHeader() {
        synchronized (this) {
            if (this._header != null && !this._headerOut) {
                this._headerOut = true;
                return this._header;
            }
            if (this._buffer != null && this._header != null && this._header.capacity() == this._buffer.capacity() && !this._bufferOut) {
                this._bufferOut = true;
                return this._buffer;
            }
            if (this._header != null) {
                return new ByteArrayBuffer(this._header.capacity());
            }
            return new ByteArrayBuffer(4096);
        }
    }
    
    public Buffer getBuffer(final int size) {
        synchronized (this) {
            if (this._header != null && this._header.capacity() == size) {
                return this.getHeader();
            }
            if (this._buffer != null && this._buffer.capacity() == size) {
                return this.getBuffer();
            }
            return null;
        }
    }
    
    public void returnBuffer(final Buffer buffer) {
        synchronized (this) {
            buffer.clear();
            if (buffer == this._header) {
                this._headerOut = false;
            }
            if (buffer == this._buffer) {
                this._bufferOut = false;
            }
        }
    }
}
