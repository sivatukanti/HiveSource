// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.BufferUtil;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.LeakDetector;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public class LeakTrackingByteBufferPool extends ContainerLifeCycle implements ByteBufferPool
{
    private static final Logger LOG;
    private final LeakDetector<ByteBuffer> leakDetector;
    private static final boolean NOISY;
    private final ByteBufferPool delegate;
    private final AtomicLong leakedReleases;
    private final AtomicLong leakedAcquires;
    private final AtomicLong leaked;
    
    public LeakTrackingByteBufferPool(final ByteBufferPool delegate) {
        this.leakDetector = new LeakDetector<ByteBuffer>() {
            @Override
            public String id(final ByteBuffer resource) {
                return BufferUtil.toIDString(resource);
            }
            
            @Override
            protected void leaked(final LeakInfo leakInfo) {
                LeakTrackingByteBufferPool.this.leaked.incrementAndGet();
                LeakTrackingByteBufferPool.this.leaked(leakInfo);
            }
        };
        this.leakedReleases = new AtomicLong(0L);
        this.leakedAcquires = new AtomicLong(0L);
        this.leaked = new AtomicLong(0L);
        this.delegate = delegate;
        this.addBean(this.leakDetector);
        this.addBean(delegate);
    }
    
    @Override
    public ByteBuffer acquire(final int size, final boolean direct) {
        final ByteBuffer buffer = this.delegate.acquire(size, direct);
        final boolean leaked = this.leakDetector.acquired(buffer);
        if (LeakTrackingByteBufferPool.NOISY || !leaked) {
            this.leakedAcquires.incrementAndGet();
            LeakTrackingByteBufferPool.LOG.info(String.format("ByteBuffer acquire %s leaked.acquired=%s", this.leakDetector.id(buffer), leaked ? "normal" : "LEAK"), new Throwable("LeakStack.Acquire"));
        }
        return buffer;
    }
    
    @Override
    public void release(final ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        final boolean leaked = this.leakDetector.released(buffer);
        if (LeakTrackingByteBufferPool.NOISY || !leaked) {
            this.leakedReleases.incrementAndGet();
            LeakTrackingByteBufferPool.LOG.info(String.format("ByteBuffer release %s leaked.released=%s", this.leakDetector.id(buffer), leaked ? "normal" : "LEAK"), new Throwable("LeakStack.Release"));
        }
        this.delegate.release(buffer);
    }
    
    public void clearTracking() {
        this.leakedAcquires.set(0L);
        this.leakedReleases.set(0L);
    }
    
    public long getLeakedAcquires() {
        return this.leakedAcquires.get();
    }
    
    public long getLeakedReleases() {
        return this.leakedReleases.get();
    }
    
    public long getLeakedResources() {
        return this.leaked.get();
    }
    
    protected void leaked(final LeakDetector.LeakInfo leakInfo) {
        LeakTrackingByteBufferPool.LOG.warn("ByteBuffer " + leakInfo.getResourceDescription() + " leaked at:", leakInfo.getStackFrames());
    }
    
    static {
        LOG = Log.getLogger(LeakTrackingByteBufferPool.class);
        NOISY = Boolean.getBoolean(LeakTrackingByteBufferPool.class.getName() + ".NOISY");
    }
}
