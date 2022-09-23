// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public class ThreadLocalBuffers extends AbstractBuffers
{
    private final ThreadLocal<ThreadBuffers> _buffers;
    
    public ThreadLocalBuffers(final Buffers.Type headerType, final int headerSize, final Buffers.Type bufferType, final int bufferSize, final Buffers.Type otherType) {
        super(headerType, headerSize, bufferType, bufferSize, otherType);
        this._buffers = new ThreadLocal<ThreadBuffers>() {
            @Override
            protected ThreadBuffers initialValue() {
                return new ThreadBuffers();
            }
        };
    }
    
    public Buffer getBuffer() {
        final ThreadBuffers buffers = this._buffers.get();
        if (buffers._buffer != null) {
            final Buffer b = buffers._buffer;
            buffers._buffer = null;
            return b;
        }
        if (buffers._other != null && this.isBuffer(buffers._other)) {
            final Buffer b = buffers._other;
            buffers._other = null;
            return b;
        }
        return this.newBuffer();
    }
    
    public Buffer getHeader() {
        final ThreadBuffers buffers = this._buffers.get();
        if (buffers._header != null) {
            final Buffer b = buffers._header;
            buffers._header = null;
            return b;
        }
        if (buffers._other != null && this.isHeader(buffers._other)) {
            final Buffer b = buffers._other;
            buffers._other = null;
            return b;
        }
        return this.newHeader();
    }
    
    public Buffer getBuffer(final int size) {
        final ThreadBuffers buffers = this._buffers.get();
        if (buffers._other != null && buffers._other.capacity() == size) {
            final Buffer b = buffers._other;
            buffers._other = null;
            return b;
        }
        return this.newBuffer(size);
    }
    
    public void returnBuffer(final Buffer buffer) {
        buffer.clear();
        if (buffer.isVolatile() || buffer.isImmutable()) {
            return;
        }
        final ThreadBuffers buffers = this._buffers.get();
        if (buffers._header == null && this.isHeader(buffer)) {
            buffers._header = buffer;
        }
        else if (buffers._buffer == null && this.isBuffer(buffer)) {
            buffers._buffer = buffer;
        }
        else {
            buffers._other = buffer;
        }
    }
    
    @Override
    public String toString() {
        return "{{" + this.getHeaderSize() + "," + this.getBufferSize() + "}}";
    }
    
    protected static class ThreadBuffers
    {
        Buffer _buffer;
        Buffer _header;
        Buffer _other;
    }
}
