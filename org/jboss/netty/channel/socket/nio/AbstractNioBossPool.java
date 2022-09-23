// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.ExecutorUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.util.ExternalResourceReleasable;

public abstract class AbstractNioBossPool<E extends Boss> implements BossPool<E>, ExternalResourceReleasable
{
    private static final int INITIALIZATION_TIMEOUT = 10;
    private static final InternalLogger logger;
    private final Boss[] bosses;
    private final AtomicInteger bossIndex;
    private final Executor bossExecutor;
    private final AtomicBoolean initialized;
    
    AbstractNioBossPool(final Executor bossExecutor, final int bossCount) {
        this(bossExecutor, bossCount, true);
    }
    
    AbstractNioBossPool(final Executor bossExecutor, final int bossCount, final boolean autoInit) {
        this.bossIndex = new AtomicInteger();
        this.initialized = new AtomicBoolean(false);
        if (bossExecutor == null) {
            throw new NullPointerException("bossExecutor");
        }
        if (bossCount <= 0) {
            throw new IllegalArgumentException("bossCount (" + bossCount + ") " + "must be a positive integer.");
        }
        this.bosses = new Boss[bossCount];
        this.bossExecutor = bossExecutor;
        if (autoInit) {
            this.init();
        }
    }
    
    protected void init() {
        if (!this.initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("initialized already");
        }
        for (int i = 0; i < this.bosses.length; ++i) {
            this.bosses[i] = this.newBoss(this.bossExecutor);
        }
        this.waitForBossThreads();
    }
    
    private void waitForBossThreads() {
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10L);
        boolean warn = false;
        for (final Boss boss : this.bosses) {
            if (boss instanceof AbstractNioSelector) {
                final AbstractNioSelector selector = (AbstractNioSelector)boss;
                final long waitTime = deadline - System.nanoTime();
                try {
                    if (waitTime <= 0L) {
                        if (selector.thread == null) {
                            warn = true;
                            break;
                        }
                    }
                    else if (!selector.startupLatch.await(waitTime, TimeUnit.NANOSECONDS)) {
                        warn = true;
                        break;
                    }
                }
                catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        if (warn) {
            AbstractNioBossPool.logger.warn("Failed to get all boss threads ready within 10 second(s). Make sure to specify the executor which has more threads than the requested bossCount. If unsure, use Executors.newCachedThreadPool().");
        }
    }
    
    protected abstract E newBoss(final Executor p0);
    
    public E nextBoss() {
        return (E)this.bosses[Math.abs(this.bossIndex.getAndIncrement() % this.bosses.length)];
    }
    
    public void rebuildSelectors() {
        for (final Boss boss : this.bosses) {
            boss.rebuildSelector();
        }
    }
    
    public void releaseExternalResources() {
        this.shutdown();
        ExecutorUtil.shutdownNow(this.bossExecutor);
    }
    
    public void shutdown() {
        for (final Boss boss : this.bosses) {
            boss.shutdown();
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(AbstractNioBossPool.class);
    }
}
