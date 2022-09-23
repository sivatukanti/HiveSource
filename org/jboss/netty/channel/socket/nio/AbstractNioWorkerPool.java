// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.socket.Worker;
import org.jboss.netty.util.internal.ExecutorUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.util.ExternalResourceReleasable;

public abstract class AbstractNioWorkerPool<E extends AbstractNioWorker> implements WorkerPool<E>, ExternalResourceReleasable
{
    private static final int INITIALIZATION_TIMEOUT = 10;
    private static final InternalLogger logger;
    private final AbstractNioWorker[] workers;
    private final AtomicInteger workerIndex;
    private final Executor workerExecutor;
    private final AtomicBoolean initialized;
    
    AbstractNioWorkerPool(final Executor workerExecutor, final int workerCount) {
        this(workerExecutor, workerCount, true);
    }
    
    AbstractNioWorkerPool(final Executor workerExecutor, final int workerCount, final boolean autoInit) {
        this.workerIndex = new AtomicInteger();
        this.initialized = new AtomicBoolean(false);
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount (" + workerCount + ") " + "must be a positive integer.");
        }
        this.workers = new AbstractNioWorker[workerCount];
        this.workerExecutor = workerExecutor;
        if (autoInit) {
            this.init();
        }
    }
    
    protected void init() {
        if (!this.initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("initialized already");
        }
        for (int i = 0; i < this.workers.length; ++i) {
            this.workers[i] = this.newWorker(this.workerExecutor);
        }
        this.waitForWorkerThreads();
    }
    
    private void waitForWorkerThreads() {
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10L);
        boolean warn = false;
        for (final AbstractNioSelector worker : this.workers) {
            final long waitTime = deadline - System.nanoTime();
            try {
                if (waitTime <= 0L) {
                    if (worker.thread == null) {
                        warn = true;
                        break;
                    }
                }
                else if (!worker.startupLatch.await(waitTime, TimeUnit.NANOSECONDS)) {
                    warn = true;
                    break;
                }
            }
            catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (warn) {
            AbstractNioWorkerPool.logger.warn("Failed to get all worker threads ready within 10 second(s). Make sure to specify the executor which has more threads than the requested workerCount. If unsure, use Executors.newCachedThreadPool().");
        }
    }
    
    protected abstract E newWorker(final Executor p0);
    
    public E nextWorker() {
        return (E)this.workers[Math.abs(this.workerIndex.getAndIncrement() % this.workers.length)];
    }
    
    public void rebuildSelectors() {
        for (final AbstractNioWorker worker : this.workers) {
            worker.rebuildSelector();
        }
    }
    
    public void releaseExternalResources() {
        this.shutdown();
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }
    
    public void shutdown() {
        for (final AbstractNioWorker worker : this.workers) {
            worker.shutdown();
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(AbstractNioWorkerPool.class);
    }
}
