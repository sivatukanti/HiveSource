// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.concurrent;

import org.slf4j.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public final class HadoopExecutors
{
    public static ExecutorService newCachedThreadPool(final ThreadFactory threadFactory) {
        return new HadoopThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
    }
    
    public static ExecutorService newFixedThreadPool(final int nThreads, final ThreadFactory threadFactory) {
        return new HadoopThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
    }
    
    public static ExecutorService newFixedThreadPool(final int nThreads) {
        return new HadoopThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    
    public static ExecutorService newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }
    
    public static ExecutorService newSingleThreadExecutor(final ThreadFactory threadFactory) {
        return Executors.newSingleThreadExecutor(threadFactory);
    }
    
    public static ScheduledExecutorService newScheduledThreadPool(final int corePoolSize) {
        return new HadoopScheduledThreadPoolExecutor(corePoolSize);
    }
    
    public static ScheduledExecutorService newScheduledThreadPool(final int corePoolSize, final ThreadFactory threadFactory) {
        return new HadoopScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(final ThreadFactory threadFactory) {
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }
    
    public static void shutdown(final ExecutorService executorService, final Logger logger, final long timeout, final TimeUnit unit) {
        try {
            if (executorService != null) {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(timeout, unit)) {
                        executorService.shutdownNow();
                    }
                    if (!executorService.awaitTermination(timeout, unit)) {
                        logger.error("Unable to shutdown properly.");
                    }
                }
                catch (InterruptedException e) {
                    logger.error("Error attempting to shutdown.", e);
                    executorService.shutdownNow();
                }
            }
        }
        catch (Exception e2) {
            logger.error("Error during shutdown: ", e2);
            throw e2;
        }
    }
    
    private HadoopExecutors() {
    }
}
