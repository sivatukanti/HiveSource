// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.BufferUtil;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.concurrent.ConcurrentMap;

public class MappedByteBufferPool implements ByteBufferPool
{
    private final ConcurrentMap<Integer, Bucket> directBuffers;
    private final ConcurrentMap<Integer, Bucket> heapBuffers;
    private final int _factor;
    private final int _maxQueue;
    private final Function<Integer, Bucket> _newBucket;
    
    public MappedByteBufferPool() {
        this(-1);
    }
    
    public MappedByteBufferPool(final int factor) {
        this(factor, -1, null);
    }
    
    public MappedByteBufferPool(final int factor, final int maxQueue) {
        this(factor, maxQueue, null);
    }
    
    public MappedByteBufferPool(final int factor, final int maxQueue, final Function<Integer, Bucket> newBucket) {
        this.directBuffers = new ConcurrentHashMap<Integer, Bucket>();
        this.heapBuffers = new ConcurrentHashMap<Integer, Bucket>();
        this._factor = ((factor <= 0) ? 1024 : factor);
        this._maxQueue = maxQueue;
        this._newBucket = (Function<Integer, Bucket>)((newBucket != null) ? newBucket : (i -> new Bucket(this, i * this._factor, this._maxQueue)));
    }
    
    @Override
    public ByteBuffer acquire(final int size, final boolean direct) {
        final int b = this.bucketFor(size);
        final ConcurrentMap<Integer, Bucket> buffers = this.bucketsFor(direct);
        final Bucket bucket = buffers.get(b);
        if (bucket == null) {
            return this.newByteBuffer(b * this._factor, direct);
        }
        return bucket.acquire(direct);
    }
    
    @Override
    public void release(final ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        assert buffer.capacity() % this._factor == 0;
        final int b = this.bucketFor(buffer.capacity());
        final ConcurrentMap<Integer, Bucket> buckets = this.bucketsFor(buffer.isDirect());
        final Bucket bucket = buckets.computeIfAbsent(b, this._newBucket);
        bucket.release(buffer);
    }
    
    public void clear() {
        this.directBuffers.values().forEach(Bucket::clear);
        this.directBuffers.clear();
        this.heapBuffers.values().forEach(Bucket::clear);
        this.heapBuffers.clear();
    }
    
    private int bucketFor(final int size) {
        int bucket = size / this._factor;
        if (size % this._factor > 0) {
            ++bucket;
        }
        return bucket;
    }
    
    ConcurrentMap<Integer, Bucket> bucketsFor(final boolean direct) {
        return direct ? this.directBuffers : this.heapBuffers;
    }
    
    public static class Tagged extends MappedByteBufferPool
    {
        private final AtomicInteger tag;
        
        public Tagged() {
            this.tag = new AtomicInteger();
        }
        
        @Override
        public ByteBuffer newByteBuffer(final int capacity, final boolean direct) {
            final ByteBuffer buffer = super.newByteBuffer(capacity + 4, direct);
            buffer.limit(buffer.capacity());
            buffer.putInt(this.tag.incrementAndGet());
            final ByteBuffer slice = buffer.slice();
            BufferUtil.clear(slice);
            return slice;
        }
    }
}
