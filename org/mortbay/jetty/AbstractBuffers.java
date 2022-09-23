// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.io.Buffer;
import org.mortbay.io.Buffers;
import org.mortbay.component.AbstractLifeCycle;

public abstract class AbstractBuffers extends AbstractLifeCycle implements Buffers
{
    private int _headerBufferSize;
    private int _requestBufferSize;
    private int _responseBufferSize;
    private static final int __HEADER = 0;
    private static final int __REQUEST = 1;
    private static final int __RESPONSE = 2;
    private static final int __OTHER = 3;
    private final int[] _pool;
    private final ThreadLocal _buffers;
    
    public AbstractBuffers() {
        this._headerBufferSize = 4096;
        this._requestBufferSize = 8192;
        this._responseBufferSize = 24576;
        this._pool = new int[] { 2, 1, 1, 2 };
        this._buffers = new ThreadLocal() {
            protected Object initialValue() {
                return new ThreadBuffers(AbstractBuffers.this._pool[0], AbstractBuffers.this._pool[1], AbstractBuffers.this._pool[2], AbstractBuffers.this._pool[3]);
            }
        };
    }
    
    public Buffer getBuffer(final int size) {
        final int set = (size == this._headerBufferSize) ? 0 : ((size == this._responseBufferSize) ? 2 : ((size == this._requestBufferSize) ? 1 : 3));
        final ThreadBuffers thread_buffers = this._buffers.get();
        final Buffer[] buffers = thread_buffers._buffers[set];
        for (int i = 0; i < buffers.length; ++i) {
            final Buffer b = buffers[i];
            if (b != null && b.capacity() == size) {
                buffers[i] = null;
                return b;
            }
        }
        return this.newBuffer(size);
    }
    
    public void returnBuffer(final Buffer buffer) {
        buffer.clear();
        if (buffer.isVolatile() || buffer.isImmutable()) {
            return;
        }
        final int size = buffer.capacity();
        final int set = (size == this._headerBufferSize) ? 0 : ((size == this._responseBufferSize) ? 2 : ((size == this._requestBufferSize) ? 1 : 3));
        final ThreadBuffers thread_buffers = this._buffers.get();
        final Buffer[] buffers = thread_buffers._buffers[set];
        for (int i = 0; i < buffers.length; ++i) {
            if (buffers[i] == null) {
                buffers[i] = buffer;
                return;
            }
        }
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        if (this._headerBufferSize == this._requestBufferSize && this._headerBufferSize == this._responseBufferSize) {
            final int[] pool = this._pool;
            final int n = 0;
            pool[n] += this._pool[1] + this._pool[2];
            this._pool[1] = 0;
            this._pool[2] = 0;
        }
        else if (this._headerBufferSize == this._requestBufferSize) {
            final int[] pool2 = this._pool;
            final int n2 = 0;
            pool2[n2] += this._pool[1];
            this._pool[1] = 0;
        }
        else if (this._headerBufferSize == this._responseBufferSize) {
            final int[] pool3 = this._pool;
            final int n3 = 0;
            pool3[n3] += this._pool[2];
            this._pool[2] = 0;
        }
        else if (this._requestBufferSize == this._responseBufferSize) {
            final int[] pool4 = this._pool;
            final int n4 = 2;
            pool4[n4] += this._pool[1];
            this._pool[1] = 0;
        }
    }
    
    public int getHeaderBufferSize() {
        return this._headerBufferSize;
    }
    
    public void setHeaderBufferSize(final int headerBufferSize) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this._headerBufferSize = headerBufferSize;
    }
    
    public int getRequestBufferSize() {
        return this._requestBufferSize;
    }
    
    public void setRequestBufferSize(final int requestBufferSize) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this._requestBufferSize = requestBufferSize;
    }
    
    public int getResponseBufferSize() {
        return this._responseBufferSize;
    }
    
    public void setResponseBufferSize(final int responseBufferSize) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this._responseBufferSize = responseBufferSize;
    }
    
    protected abstract Buffer newBuffer(final int p0);
    
    public String toString() {
        return "{{" + this._headerBufferSize + "," + this._requestBufferSize + "," + this._responseBufferSize + "}}";
    }
    
    protected static class ThreadBuffers
    {
        final Buffer[][] _buffers;
        
        ThreadBuffers(final int headers, final int requests, final int responses, final int others) {
            (this._buffers = new Buffer[4][])[0] = new Buffer[headers];
            this._buffers[1] = new Buffer[requests];
            this._buffers[2] = new Buffer[responses];
            this._buffers[3] = new Buffer[others];
        }
    }
}
