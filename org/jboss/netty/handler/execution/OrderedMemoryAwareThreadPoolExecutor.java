// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import java.util.Set;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentMap;

public class OrderedMemoryAwareThreadPoolExecutor extends MemoryAwareThreadPoolExecutor
{
    protected final ConcurrentMap<Object, Executor> childExecutors;
    
    public OrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize);
        this.childExecutors = this.newChildExecutorMap();
    }
    
    public OrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit);
        this.childExecutors = this.newChildExecutorMap();
    }
    
    public OrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, threadFactory);
        this.childExecutors = this.newChildExecutorMap();
    }
    
    public OrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ObjectSizeEstimator objectSizeEstimator, final ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, objectSizeEstimator, threadFactory);
        this.childExecutors = this.newChildExecutorMap();
    }
    
    protected ConcurrentMap<Object, Executor> newChildExecutorMap() {
        return new ConcurrentIdentityWeakKeyHashMap<Object, Executor>();
    }
    
    protected Object getChildExecutorKey(final ChannelEvent e) {
        return e.getChannel();
    }
    
    protected Set<Object> getChildExecutorKeySet() {
        return this.childExecutors.keySet();
    }
    
    protected boolean removeChildExecutor(final Object key) {
        return this.childExecutors.remove(key) != null;
    }
    
    @Override
    protected void doExecute(final Runnable task) {
        if (!(task instanceof ChannelEventRunnable)) {
            this.doUnorderedExecute(task);
        }
        else {
            final ChannelEventRunnable r = (ChannelEventRunnable)task;
            this.getChildExecutor(r.getEvent()).execute(task);
        }
    }
    
    protected Executor getChildExecutor(final ChannelEvent e) {
        final Object key = this.getChildExecutorKey(e);
        Executor executor = this.childExecutors.get(key);
        if (executor == null) {
            executor = new ChildExecutor();
            final Executor oldExecutor = this.childExecutors.putIfAbsent(key, executor);
            if (oldExecutor != null) {
                executor = oldExecutor;
            }
        }
        if (e instanceof ChannelStateEvent) {
            final Channel channel = e.getChannel();
            final ChannelStateEvent se = (ChannelStateEvent)e;
            if (se.getState() == ChannelState.OPEN && !channel.isOpen()) {
                this.removeChildExecutor(key);
            }
        }
        return executor;
    }
    
    @Override
    protected boolean shouldCount(final Runnable task) {
        return !(task instanceof ChildExecutor) && super.shouldCount(task);
    }
    
    void onAfterExecute(final Runnable r, final Throwable t) {
        this.afterExecute(r, t);
    }
    
    protected final class ChildExecutor implements Executor, Runnable
    {
        private final Queue<Runnable> tasks;
        private final AtomicBoolean isRunning;
        
        protected ChildExecutor() {
            this.tasks = new ConcurrentLinkedQueue<Runnable>();
            this.isRunning = new AtomicBoolean();
        }
        
        public void execute(final Runnable command) {
            this.tasks.add(command);
            if (!this.isRunning.get()) {
                OrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this);
            }
        }
        
        public void run() {
            if (this.isRunning.compareAndSet(false, true)) {
                final boolean acquired = true;
                try {
                    final Thread thread = Thread.currentThread();
                    while (true) {
                        final Runnable task = this.tasks.poll();
                        if (task == null) {
                            break;
                        }
                        boolean ran = false;
                        OrderedMemoryAwareThreadPoolExecutor.this.beforeExecute(thread, task);
                        try {
                            task.run();
                            ran = true;
                            OrderedMemoryAwareThreadPoolExecutor.this.onAfterExecute(task, null);
                        }
                        catch (RuntimeException e) {
                            if (!ran) {
                                OrderedMemoryAwareThreadPoolExecutor.this.onAfterExecute(task, e);
                            }
                            throw e;
                        }
                    }
                }
                finally {
                    this.isRunning.set(false);
                }
                if (acquired && !this.isRunning.get() && this.tasks.peek() != null) {
                    OrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this);
                }
            }
        }
    }
}
