// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.nio.ByteBuffer;

public class ArrayByteBufferPool implements ByteBufferPool
{
    private final int _min;
    private final int _maxQueue;
    private final Bucket[] _direct;
    private final Bucket[] _indirect;
    private final int _inc;
    
    public ArrayByteBufferPool() {
        this(-1, -1, -1, -1);
    }
    
    public ArrayByteBufferPool(final int minSize, final int increment, final int maxSize) {
        this(minSize, increment, maxSize, -1);
    }
    
    public ArrayByteBufferPool(int minSize, int increment, int maxSize, final int maxQueue) {
        if (minSize <= 0) {
            minSize = 0;
        }
        if (increment <= 0) {
            increment = 1024;
        }
        if (maxSize <= 0) {
            maxSize = 65536;
        }
        if (minSize >= increment) {
            throw new IllegalArgumentException("minSize >= increment");
        }
        if (maxSize % increment != 0 || increment >= maxSize) {
            throw new IllegalArgumentException("increment must be a divisor of maxSize");
        }
        this._min = minSize;
        this._inc = increment;
        this._direct = new Bucket[maxSize / increment];
        this._indirect = new Bucket[maxSize / increment];
        this._maxQueue = maxQueue;
        int size = 0;
        for (int i = 0; i < this._direct.length; ++i) {
            size += this._inc;
            this._direct[i] = new Bucket(this, size, this._maxQueue);
            this._indirect[i] = new Bucket(this, size, this._maxQueue);
        }
    }
    
    @Override
    public ByteBuffer acquire(final int size, final boolean direct) {
        final Bucket bucket = this.bucketFor(size, direct);
        if (bucket == null) {
            return this.newByteBuffer(size, direct);
        }
        return bucket.acquire(direct);
    }
    
    @Override
    public void release(final ByteBuffer buffer) {
        if (buffer != null) {
            final Bucket bucket = this.bucketFor(buffer.capacity(), buffer.isDirect());
            if (bucket != null) {
                bucket.release(buffer);
            }
        }
    }
    
    public void clear() {
        for (int i = 0; i < this._direct.length; ++i) {
            this._direct[i].clear();
            this._indirect[i].clear();
        }
    }
    
    private Bucket bucketFor(final int size, final boolean direct) {
        if (size <= this._min) {
            return null;
        }
        final int b = (size - 1) / this._inc;
        if (b >= this._direct.length) {
            return null;
        }
        final Bucket bucket = direct ? this._direct[b] : this._indirect[b];
        return bucket;
    }
    
    Bucket[] bucketsFor(final boolean direct) {
        return direct ? this._direct : this._indirect;
    }
}
