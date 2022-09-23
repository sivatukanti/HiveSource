// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.BlockingQueue;
import java.nio.ByteBuffer;

public class SslBufferPool
{
    private static final int MAX_PACKET_SIZE_ALIGNED = 18816;
    private static final int DEFAULT_POOL_SIZE = 19267584;
    private final ByteBuffer preallocated;
    private final BlockingQueue<ByteBuffer> pool;
    private final int maxBufferCount;
    private final boolean allocateDirect;
    private final AtomicInteger numAllocations;
    
    public SslBufferPool() {
        this(19267584);
    }
    
    public SslBufferPool(final boolean preallocate, final boolean allocateDirect) {
        this(19267584, preallocate, allocateDirect);
    }
    
    public SslBufferPool(final int maxPoolSize) {
        this(maxPoolSize, false, false);
    }
    
    public SslBufferPool(final int maxPoolSize, final boolean preallocate, final boolean allocateDirect) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("maxPoolSize: " + maxPoolSize);
        }
        int maxBufferCount = maxPoolSize / 18816;
        if (maxPoolSize % 18816 != 0) {
            ++maxBufferCount;
        }
        this.maxBufferCount = maxBufferCount;
        this.allocateDirect = allocateDirect;
        this.pool = new ArrayBlockingQueue<ByteBuffer>(maxBufferCount);
        if (preallocate) {
            this.preallocated = this.allocate(maxBufferCount * 18816);
            this.numAllocations = null;
            for (int i = 0; i < maxBufferCount; ++i) {
                final int pos = i * 18816;
                this.preallocated.clear().position(pos).limit(pos + 18816);
                this.pool.add(this.preallocated.slice());
            }
        }
        else {
            this.preallocated = null;
            this.numAllocations = new AtomicInteger();
        }
    }
    
    public int getMaxPoolSize() {
        return this.maxBufferCount * 18816;
    }
    
    public int getUnacquiredPoolSize() {
        return this.pool.size() * 18816;
    }
    
    public ByteBuffer acquireBuffer() {
        ByteBuffer buf;
        if (this.preallocated != null || this.numAllocations.get() >= this.maxBufferCount) {
            boolean interrupted = false;
            while (true) {
                try {
                    buf = this.pool.take();
                }
                catch (InterruptedException ignore) {
                    interrupted = true;
                    continue;
                }
                break;
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        else {
            buf = this.pool.poll();
            if (buf == null) {
                this.numAllocations.incrementAndGet();
                buf = this.allocate(18713);
            }
        }
        buf.clear();
        return buf;
    }
    
    public void releaseBuffer(final ByteBuffer buffer) {
        this.pool.offer(buffer);
    }
    
    private ByteBuffer allocate(final int capacity) {
        if (this.allocateDirect) {
            return ByteBuffer.allocateDirect(capacity);
        }
        return ByteBuffer.allocate(capacity);
    }
}
