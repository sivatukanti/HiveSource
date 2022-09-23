// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;

public interface ByteBufferPool
{
    ByteBuffer acquire(final int p0, final boolean p1);
    
    void release(final ByteBuffer p0);
    
    default ByteBuffer newByteBuffer(final int capacity, final boolean direct) {
        return direct ? BufferUtil.allocateDirect(capacity) : BufferUtil.allocate(capacity);
    }
    
    public static class Lease
    {
        private final ByteBufferPool byteBufferPool;
        private final List<ByteBuffer> buffers;
        private final List<Boolean> recycles;
        
        public Lease(final ByteBufferPool byteBufferPool) {
            this.byteBufferPool = byteBufferPool;
            this.buffers = new ArrayList<ByteBuffer>();
            this.recycles = new ArrayList<Boolean>();
        }
        
        public ByteBuffer acquire(final int capacity, final boolean direct) {
            final ByteBuffer buffer = this.byteBufferPool.acquire(capacity, direct);
            BufferUtil.clearToFill(buffer);
            return buffer;
        }
        
        public void append(final ByteBuffer buffer, final boolean recycle) {
            this.buffers.add(buffer);
            this.recycles.add(recycle);
        }
        
        public void insert(final int index, final ByteBuffer buffer, final boolean recycle) {
            this.buffers.add(index, buffer);
            this.recycles.add(index, recycle);
        }
        
        public List<ByteBuffer> getByteBuffers() {
            return this.buffers;
        }
        
        public long getTotalLength() {
            long length = 0L;
            for (int i = 0; i < this.buffers.size(); ++i) {
                length += this.buffers.get(i).remaining();
            }
            return length;
        }
        
        public int getSize() {
            return this.buffers.size();
        }
        
        public void recycle() {
            for (int i = 0; i < this.buffers.size(); ++i) {
                final ByteBuffer buffer = this.buffers.get(i);
                if (this.recycles.get(i)) {
                    this.byteBufferPool.release(buffer);
                }
            }
            this.buffers.clear();
            this.recycles.clear();
        }
    }
    
    public static class Bucket
    {
        private final Deque<ByteBuffer> _queue;
        private final ByteBufferPool _pool;
        private final int _capacity;
        private final AtomicInteger _space;
        
        public Bucket(final ByteBufferPool pool, final int bufferSize, final int maxSize) {
            this._queue = new ConcurrentLinkedDeque<ByteBuffer>();
            this._pool = pool;
            this._capacity = bufferSize;
            this._space = ((maxSize > 0) ? new AtomicInteger(maxSize) : null);
        }
        
        public ByteBuffer acquire(final boolean direct) {
            final ByteBuffer buffer = this.queuePoll();
            if (buffer == null) {
                return this._pool.newByteBuffer(this._capacity, direct);
            }
            if (this._space != null) {
                this._space.incrementAndGet();
            }
            return buffer;
        }
        
        public void release(final ByteBuffer buffer) {
            BufferUtil.clear(buffer);
            if (this._space == null) {
                this.queueOffer(buffer);
            }
            else if (this._space.decrementAndGet() >= 0) {
                this.queueOffer(buffer);
            }
            else {
                this._space.incrementAndGet();
            }
        }
        
        public void clear() {
            if (this._space == null) {
                this.queueClear();
            }
            else {
                int s = this._space.getAndSet(0);
                while (s-- > 0) {
                    if (this.queuePoll() == null) {
                        this._space.incrementAndGet();
                    }
                }
            }
        }
        
        private void queueOffer(final ByteBuffer buffer) {
            this._queue.offerFirst(buffer);
        }
        
        private ByteBuffer queuePoll() {
            return this._queue.poll();
        }
        
        private void queueClear() {
            this._queue.clear();
        }
        
        boolean isEmpty() {
            return this._queue.isEmpty();
        }
        
        int size() {
            return this._queue.size();
        }
        
        @Override
        public String toString() {
            return String.format("Bucket@%x{%d/%d}", this.hashCode(), this.size(), this._capacity);
        }
    }
}
