// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ScheduledExecutorScheduler extends AbstractLifeCycle implements Scheduler, Dumpable
{
    private final String name;
    private final boolean daemon;
    private final ClassLoader classloader;
    private final ThreadGroup threadGroup;
    private volatile ScheduledThreadPoolExecutor scheduler;
    private volatile Thread thread;
    
    public ScheduledExecutorScheduler() {
        this(null, false);
    }
    
    public ScheduledExecutorScheduler(final String name, final boolean daemon) {
        this(name, daemon, Thread.currentThread().getContextClassLoader());
    }
    
    public ScheduledExecutorScheduler(final String name, final boolean daemon, final ClassLoader threadFactoryClassLoader) {
        this(name, daemon, threadFactoryClassLoader, null);
    }
    
    public ScheduledExecutorScheduler(final String name, final boolean daemon, final ClassLoader threadFactoryClassLoader, final ThreadGroup threadGroup) {
        this.name = ((name == null) ? ("Scheduler-" + this.hashCode()) : name);
        this.daemon = daemon;
        this.classloader = ((threadFactoryClassLoader == null) ? Thread.currentThread().getContextClassLoader() : threadFactoryClassLoader);
        this.threadGroup = threadGroup;
    }
    
    @Override
    protected void doStart() throws Exception {
        (this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread thread = ScheduledExecutorScheduler.this.thread = new Thread(ScheduledExecutorScheduler.this.threadGroup, r, ScheduledExecutorScheduler.this.name);
                thread.setDaemon(ScheduledExecutorScheduler.this.daemon);
                thread.setContextClassLoader(ScheduledExecutorScheduler.this.classloader);
                return thread;
            }
        })).setRemoveOnCancelPolicy(true);
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this.scheduler.shutdownNow();
        super.doStop();
        this.scheduler = null;
    }
    
    @Override
    public Task schedule(final Runnable task, final long delay, final TimeUnit unit) {
        final ScheduledThreadPoolExecutor s = this.scheduler;
        if (s == null) {
            return new Task() {
                @Override
                public boolean cancel() {
                    return false;
                }
            };
        }
        final ScheduledFuture<?> result = s.schedule(task, delay, unit);
        return new ScheduledFutureTask(result);
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        ContainerLifeCycle.dumpObject(out, this);
        final Thread thread = this.thread;
        if (thread != null) {
            final List<StackTraceElement> frames = Arrays.asList(thread.getStackTrace());
            ContainerLifeCycle.dump(out, indent, frames);
        }
    }
    
    private static class ScheduledFutureTask implements Task
    {
        private final ScheduledFuture<?> scheduledFuture;
        
        ScheduledFutureTask(final ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
        
        @Override
        public boolean cancel() {
            return this.scheduledFuture.cancel(false);
        }
    }
}
