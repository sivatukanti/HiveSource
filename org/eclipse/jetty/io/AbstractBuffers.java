// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.io.nio.IndirectNIOBuffer;
import org.eclipse.jetty.io.nio.DirectNIOBuffer;

public abstract class AbstractBuffers implements Buffers
{
    protected final Type _headerType;
    protected final int _headerSize;
    protected final Type _bufferType;
    protected final int _bufferSize;
    protected final Type _otherType;
    
    public AbstractBuffers(final Type headerType, final int headerSize, final Type bufferType, final int bufferSize, final Type otherType) {
        this._headerType = headerType;
        this._headerSize = headerSize;
        this._bufferType = bufferType;
        this._bufferSize = bufferSize;
        this._otherType = otherType;
    }
    
    public int getBufferSize() {
        return this._bufferSize;
    }
    
    public int getHeaderSize() {
        return this._headerSize;
    }
    
    protected final Buffer newHeader() {
        switch (this._headerType) {
            case BYTE_ARRAY: {
                return new ByteArrayBuffer(this._headerSize);
            }
            case DIRECT: {
                return new DirectNIOBuffer(this._headerSize);
            }
            case INDIRECT: {
                return new IndirectNIOBuffer(this._headerSize);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    protected final Buffer newBuffer() {
        switch (this._bufferType) {
            case BYTE_ARRAY: {
                return new ByteArrayBuffer(this._bufferSize);
            }
            case DIRECT: {
                return new DirectNIOBuffer(this._bufferSize);
            }
            case INDIRECT: {
                return new IndirectNIOBuffer(this._bufferSize);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    protected final Buffer newBuffer(final int size) {
        switch (this._otherType) {
            case BYTE_ARRAY: {
                return new ByteArrayBuffer(size);
            }
            case DIRECT: {
                return new DirectNIOBuffer(size);
            }
            case INDIRECT: {
                return new IndirectNIOBuffer(size);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public final boolean isHeader(final Buffer buffer) {
        if (buffer.capacity() == this._headerSize) {
            switch (this._headerType) {
                case BYTE_ARRAY: {
                    return buffer instanceof ByteArrayBuffer && !(buffer instanceof IndirectNIOBuffer);
                }
                case DIRECT: {
                    return buffer instanceof DirectNIOBuffer;
                }
                case INDIRECT: {
                    return buffer instanceof IndirectNIOBuffer;
                }
            }
        }
        return false;
    }
    
    public final boolean isBuffer(final Buffer buffer) {
        if (buffer.capacity() == this._bufferSize) {
            switch (this._bufferType) {
                case BYTE_ARRAY: {
                    return buffer instanceof ByteArrayBuffer && !(buffer instanceof IndirectNIOBuffer);
                }
                case DIRECT: {
                    return buffer instanceof DirectNIOBuffer;
                }
                case INDIRECT: {
                    return buffer instanceof IndirectNIOBuffer;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s [%d,%d]", this.getClass().getSimpleName(), this._headerSize, this._bufferSize);
    }
}
