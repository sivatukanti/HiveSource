// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.concurrent;

import org.slf4j.LoggerFactory;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class HadoopScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor
{
    private static final Logger LOG;
    
    public HadoopScheduledThreadPoolExecutor(final int corePoolSize) {
        super(corePoolSize);
    }
    
    public HadoopScheduledThreadPoolExecutor(final int corePoolSize, final ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }
    
    public HadoopScheduledThreadPoolExecutor(final int corePoolSize, final RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }
    
    public HadoopScheduledThreadPoolExecutor(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        if (HadoopScheduledThreadPoolExecutor.LOG.isDebugEnabled()) {
            HadoopScheduledThreadPoolExecutor.LOG.debug("beforeExecute in thread: " + Thread.currentThread().getName() + ", runnable type: " + r.getClass().getName());
        }
    }
    
    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        super.afterExecute(r, t);
        ExecutorHelper.logThrowableFromAfterExecute(r, t);
    }
    
    static {
        LOG = LoggerFactory.getLogger(HadoopScheduledThreadPoolExecutor.class);
    }
}
