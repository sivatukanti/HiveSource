// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class AsyncDiskService
{
    public static final Logger LOG;
    private static final int CORE_THREADS_PER_VOLUME = 1;
    private static final int MAXIMUM_THREADS_PER_VOLUME = 4;
    private static final long THREADS_KEEP_ALIVE_SECONDS = 60L;
    private final ThreadGroup threadGroup;
    private ThreadFactory threadFactory;
    private HashMap<String, ThreadPoolExecutor> executors;
    
    public AsyncDiskService(final String[] volumes) {
        this.threadGroup = new ThreadGroup("async disk service");
        this.executors = new HashMap<String, ThreadPoolExecutor>();
        this.threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(AsyncDiskService.this.threadGroup, r);
            }
        };
        for (int v = 0; v < volumes.length; ++v) {
            final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), this.threadFactory);
            executor.allowCoreThreadTimeOut(true);
            this.executors.put(volumes[v], executor);
        }
    }
    
    public synchronized void execute(final String root, final Runnable task) {
        final ThreadPoolExecutor executor = this.executors.get(root);
        if (executor == null) {
            throw new RuntimeException("Cannot find root " + root + " for execution of task " + task);
        }
        executor.execute(task);
    }
    
    public synchronized void shutdown() {
        AsyncDiskService.LOG.info("Shutting down all AsyncDiskService threads...");
        for (final Map.Entry<String, ThreadPoolExecutor> e : this.executors.entrySet()) {
            e.getValue().shutdown();
        }
    }
    
    public synchronized boolean awaitTermination(final long milliseconds) throws InterruptedException {
        final long end = Time.now() + milliseconds;
        for (final Map.Entry<String, ThreadPoolExecutor> e : this.executors.entrySet()) {
            final ThreadPoolExecutor executor = e.getValue();
            if (!executor.awaitTermination(Math.max(end - Time.now(), 0L), TimeUnit.MILLISECONDS)) {
                AsyncDiskService.LOG.warn("AsyncDiskService awaitTermination timeout.");
                return false;
            }
        }
        AsyncDiskService.LOG.info("All AsyncDiskService threads are terminated.");
        return true;
    }
    
    public synchronized List<Runnable> shutdownNow() {
        AsyncDiskService.LOG.info("Shutting down all AsyncDiskService threads immediately...");
        final List<Runnable> list = new ArrayList<Runnable>();
        for (final Map.Entry<String, ThreadPoolExecutor> e : this.executors.entrySet()) {
            list.addAll(e.getValue().shutdownNow());
        }
        return list;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AsyncDiskService.class);
    }
}
