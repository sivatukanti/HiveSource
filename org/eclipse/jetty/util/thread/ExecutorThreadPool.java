// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ExecutorThreadPool extends AbstractLifeCycle implements ThreadPool, LifeCycle
{
    private static final Logger LOG;
    private final ExecutorService _executor;
    
    public ExecutorThreadPool(final ExecutorService executor) {
        this._executor = executor;
    }
    
    public ExecutorThreadPool() {
        this(new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    }
    
    public ExecutorThreadPool(final int queueSize) {
        this((queueSize < 0) ? new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()) : ((queueSize == 0) ? new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()) : new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize))));
    }
    
    public ExecutorThreadPool(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS);
    }
    
    public ExecutorThreadPool(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
    }
    
    public ExecutorThreadPool(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        this(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue));
    }
    
    @Override
    public void execute(final Runnable job) {
        this._executor.execute(job);
    }
    
    public boolean dispatch(final Runnable job) {
        try {
            this._executor.execute(job);
            return true;
        }
        catch (RejectedExecutionException e) {
            ExecutorThreadPool.LOG.warn(e);
            return false;
        }
    }
    
    @Override
    public int getIdleThreads() {
        if (this._executor instanceof ThreadPoolExecutor) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
            return tpe.getPoolSize() - tpe.getActiveCount();
        }
        return -1;
    }
    
    @Override
    public int getThreads() {
        if (this._executor instanceof ThreadPoolExecutor) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
            return tpe.getPoolSize();
        }
        return -1;
    }
    
    @Override
    public boolean isLowOnThreads() {
        if (this._executor instanceof ThreadPoolExecutor) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
            return tpe.getPoolSize() == tpe.getMaximumPoolSize() && tpe.getQueue().size() >= tpe.getPoolSize() - tpe.getActiveCount();
        }
        return false;
    }
    
    @Override
    public void join() throws InterruptedException {
        this._executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this._executor.shutdownNow();
    }
    
    static {
        LOG = Log.getLogger(ExecutorThreadPool.class);
    }
}
