// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;

public class PooledBuffers extends AbstractBuffers
{
    private final Queue<Buffer> _headers;
    private final Queue<Buffer> _buffers;
    private final Queue<Buffer> _others;
    private final AtomicInteger _size;
    private final int _maxSize;
    private final boolean _otherHeaders;
    private final boolean _otherBuffers;
    
    public PooledBuffers(final Buffers.Type headerType, final int headerSize, final Buffers.Type bufferType, final int bufferSize, final Buffers.Type otherType, final int maxSize) {
        super(headerType, headerSize, bufferType, bufferSize, otherType);
        this._size = new AtomicInteger();
        this._headers = new ConcurrentLinkedQueue<Buffer>();
        this._buffers = new ConcurrentLinkedQueue<Buffer>();
        this._others = new ConcurrentLinkedQueue<Buffer>();
        this._otherHeaders = (headerType == otherType);
        this._otherBuffers = (bufferType == otherType);
        this._maxSize = maxSize;
    }
    
    public Buffer getHeader() {
        Buffer buffer = this._headers.poll();
        if (buffer == null) {
            buffer = this.newHeader();
        }
        else {
            this._size.decrementAndGet();
        }
        return buffer;
    }
    
    public Buffer getBuffer() {
        Buffer buffer = this._buffers.poll();
        if (buffer == null) {
            buffer = this.newBuffer();
        }
        else {
            this._size.decrementAndGet();
        }
        return buffer;
    }
    
    public Buffer getBuffer(final int size) {
        if (this._otherHeaders && size == this.getHeaderSize()) {
            return this.getHeader();
        }
        if (this._otherBuffers && size == this.getBufferSize()) {
            return this.getBuffer();
        }
        Buffer buffer;
        for (buffer = this._others.poll(); buffer != null && buffer.capacity() != size; buffer = this._others.poll()) {
            this._size.decrementAndGet();
        }
        if (buffer == null) {
            buffer = this.newBuffer(size);
        }
        else {
            this._size.decrementAndGet();
        }
        return buffer;
    }
    
    public void returnBuffer(final Buffer buffer) {
        buffer.clear();
        if (buffer.isVolatile() || buffer.isImmutable()) {
            return;
        }
        if (this._size.incrementAndGet() > this._maxSize) {
            this._size.decrementAndGet();
        }
        else if (this.isHeader(buffer)) {
            this._headers.add(buffer);
        }
        else if (this.isBuffer(buffer)) {
            this._buffers.add(buffer);
        }
        else {
            this._others.add(buffer);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s [%d/%d@%d,%d/%d@%d,%d/%d@-]", this.getClass().getSimpleName(), this._headers.size(), this._maxSize, this._headerSize, this._buffers.size(), this._maxSize, this._bufferSize, this._others.size(), this._maxSize);
    }
}
