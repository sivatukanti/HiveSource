// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.netty.channel.ChannelEvent;
import java.util.Iterator;
import java.util.Set;
import org.jboss.netty.channel.Channels;
import java.util.HashSet;
import java.io.IOException;
import java.util.List;
import java.lang.reflect.Method;
import org.jboss.netty.util.internal.ConcurrentIdentityHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.DefaultObjectSizeEstimator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.util.internal.SharedResourceMisuseDetector;
import org.jboss.netty.logging.InternalLogger;
import java.util.concurrent.ThreadPoolExecutor;

public class MemoryAwareThreadPoolExecutor extends ThreadPoolExecutor
{
    private static final InternalLogger logger;
    private static final SharedResourceMisuseDetector misuseDetector;
    private volatile Settings settings;
    private final ConcurrentMap<Channel, AtomicLong> channelCounters;
    private final Limiter totalLimiter;
    private volatile boolean notifyOnShutdown;
    
    public MemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, 30L, TimeUnit.SECONDS);
    }
    
    public MemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, Executors.defaultThreadFactory());
    }
    
    public MemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ThreadFactory threadFactory) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, new DefaultObjectSizeEstimator(), threadFactory);
    }
    
    public MemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ObjectSizeEstimator objectSizeEstimator, final ThreadFactory threadFactory) {
        super(corePoolSize, corePoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(), threadFactory, new NewThreadRunsPolicy());
        this.channelCounters = new ConcurrentIdentityHashMap<Channel, AtomicLong>();
        if (objectSizeEstimator == null) {
            throw new NullPointerException("objectSizeEstimator");
        }
        if (maxChannelMemorySize < 0L) {
            throw new IllegalArgumentException("maxChannelMemorySize: " + maxChannelMemorySize);
        }
        if (maxTotalMemorySize < 0L) {
            throw new IllegalArgumentException("maxTotalMemorySize: " + maxTotalMemorySize);
        }
        try {
            final Method m = this.getClass().getMethod("allowCoreThreadTimeOut", Boolean.TYPE);
            m.invoke(this, Boolean.TRUE);
        }
        catch (Throwable t) {
            MemoryAwareThreadPoolExecutor.logger.debug("ThreadPoolExecutor.allowCoreThreadTimeOut() is not supported in this platform.");
        }
        this.settings = new Settings(objectSizeEstimator, maxChannelMemorySize);
        if (maxTotalMemorySize == 0L) {
            this.totalLimiter = null;
        }
        else {
            this.totalLimiter = new Limiter(maxTotalMemorySize);
        }
        MemoryAwareThreadPoolExecutor.misuseDetector.increase();
    }
    
    @Override
    protected void terminated() {
        super.terminated();
        MemoryAwareThreadPoolExecutor.misuseDetector.decrease();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        return this.shutdownNow(this.notifyOnShutdown);
    }
    
    public List<Runnable> shutdownNow(final boolean notify) {
        if (!notify) {
            return super.shutdownNow();
        }
        Throwable cause = null;
        Set<Channel> channels = null;
        final List<Runnable> tasks = super.shutdownNow();
        for (final Runnable task : tasks) {
            if (task instanceof ChannelEventRunnable) {
                if (cause == null) {
                    cause = new IOException("Unable to process queued event");
                }
                final ChannelEvent event = ((ChannelEventRunnable)task).getEvent();
                event.getFuture().setFailure(cause);
                if (channels == null) {
                    channels = new HashSet<Channel>();
                }
                channels.add(event.getChannel());
            }
        }
        if (channels != null) {
            for (final Channel channel : channels) {
                Channels.fireExceptionCaughtLater(channel, cause);
            }
        }
        return tasks;
    }
    
    public ObjectSizeEstimator getObjectSizeEstimator() {
        return this.settings.objectSizeEstimator;
    }
    
    public void setObjectSizeEstimator(final ObjectSizeEstimator objectSizeEstimator) {
        if (objectSizeEstimator == null) {
            throw new NullPointerException("objectSizeEstimator");
        }
        this.settings = new Settings(objectSizeEstimator, this.settings.maxChannelMemorySize);
    }
    
    public long getMaxChannelMemorySize() {
        return this.settings.maxChannelMemorySize;
    }
    
    public void setMaxChannelMemorySize(final long maxChannelMemorySize) {
        if (maxChannelMemorySize < 0L) {
            throw new IllegalArgumentException("maxChannelMemorySize: " + maxChannelMemorySize);
        }
        if (this.getTaskCount() > 0L) {
            throw new IllegalStateException("can't be changed after a task is executed");
        }
        this.settings = new Settings(this.settings.objectSizeEstimator, maxChannelMemorySize);
    }
    
    public long getMaxTotalMemorySize() {
        if (this.totalLimiter == null) {
            return 0L;
        }
        return this.totalLimiter.limit;
    }
    
    public void setNotifyChannelFuturesOnShutdown(final boolean notifyOnShutdown) {
        this.notifyOnShutdown = notifyOnShutdown;
    }
    
    public boolean getNotifyChannelFuturesOnShutdown() {
        return this.notifyOnShutdown;
    }
    
    @Override
    public void execute(Runnable command) {
        if (command instanceof ChannelDownstreamEventRunnable) {
            throw new RejectedExecutionException("command must be enclosed with an upstream event.");
        }
        if (!(command instanceof ChannelEventRunnable)) {
            command = new MemoryAwareRunnable(command);
        }
        this.increaseCounter(command);
        this.doExecute(command);
    }
    
    protected void doExecute(final Runnable task) {
        this.doUnorderedExecute(task);
    }
    
    protected final void doUnorderedExecute(final Runnable task) {
        super.execute(task);
    }
    
    @Override
    public boolean remove(final Runnable task) {
        final boolean removed = super.remove(task);
        if (removed) {
            this.decreaseCounter(task);
        }
        return removed;
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        this.decreaseCounter(r);
    }
    
    protected void increaseCounter(final Runnable task) {
        if (!this.shouldCount(task)) {
            return;
        }
        final Settings settings = this.settings;
        final long maxChannelMemorySize = settings.maxChannelMemorySize;
        final int increment = settings.objectSizeEstimator.estimateSize(task);
        if (task instanceof ChannelEventRunnable) {
            final ChannelEventRunnable eventTask = (ChannelEventRunnable)task;
            eventTask.estimatedSize = increment;
            final Channel channel = eventTask.getEvent().getChannel();
            final long channelCounter = this.getChannelCounter(channel).addAndGet(increment);
            if (maxChannelMemorySize != 0L && channelCounter >= maxChannelMemorySize && channel.isOpen() && channel.isReadable()) {
                final ChannelHandlerContext ctx = eventTask.getContext();
                if (ctx.getHandler() instanceof ExecutionHandler) {
                    ctx.setAttachment(Boolean.TRUE);
                }
                channel.setReadable(false);
            }
        }
        else {
            ((MemoryAwareRunnable)task).estimatedSize = increment;
        }
        if (this.totalLimiter != null) {
            this.totalLimiter.increase(increment);
        }
    }
    
    protected void decreaseCounter(final Runnable task) {
        if (!this.shouldCount(task)) {
            return;
        }
        final Settings settings = this.settings;
        final long maxChannelMemorySize = settings.maxChannelMemorySize;
        int increment;
        if (task instanceof ChannelEventRunnable) {
            increment = ((ChannelEventRunnable)task).estimatedSize;
        }
        else {
            increment = ((MemoryAwareRunnable)task).estimatedSize;
        }
        if (this.totalLimiter != null) {
            this.totalLimiter.decrease(increment);
        }
        if (task instanceof ChannelEventRunnable) {
            final ChannelEventRunnable eventTask = (ChannelEventRunnable)task;
            final Channel channel = eventTask.getEvent().getChannel();
            final long channelCounter = this.getChannelCounter(channel).addAndGet(-increment);
            if (maxChannelMemorySize != 0L && channelCounter < maxChannelMemorySize && channel.isOpen() && !channel.isReadable()) {
                final ChannelHandlerContext ctx = eventTask.getContext();
                if (ctx.getHandler() instanceof ExecutionHandler) {
                    if (ctx.getAttachment() != null) {
                        ctx.setAttachment(null);
                        channel.setReadable(true);
                    }
                }
                else {
                    channel.setReadable(true);
                }
            }
        }
    }
    
    private AtomicLong getChannelCounter(final Channel channel) {
        AtomicLong counter = this.channelCounters.get(channel);
        if (counter == null) {
            counter = new AtomicLong();
            final AtomicLong oldCounter = this.channelCounters.putIfAbsent(channel, counter);
            if (oldCounter != null) {
                counter = oldCounter;
            }
        }
        if (!channel.isOpen()) {
            this.channelCounters.remove(channel);
        }
        return counter;
    }
    
    protected boolean shouldCount(final Runnable task) {
        if (task instanceof ChannelUpstreamEventRunnable) {
            final ChannelUpstreamEventRunnable r = (ChannelUpstreamEventRunnable)task;
            final ChannelEvent e = r.getEvent();
            if (e instanceof WriteCompletionEvent) {
                return false;
            }
            if (e instanceof ChannelStateEvent && ((ChannelStateEvent)e).getState() == ChannelState.INTEREST_OPS) {
                return false;
            }
        }
        return true;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(MemoryAwareThreadPoolExecutor.class);
        misuseDetector = new SharedResourceMisuseDetector(MemoryAwareThreadPoolExecutor.class);
    }
    
    private static final class Settings
    {
        final ObjectSizeEstimator objectSizeEstimator;
        final long maxChannelMemorySize;
        
        Settings(final ObjectSizeEstimator objectSizeEstimator, final long maxChannelMemorySize) {
            this.objectSizeEstimator = objectSizeEstimator;
            this.maxChannelMemorySize = maxChannelMemorySize;
        }
    }
    
    private static final class NewThreadRunsPolicy implements RejectedExecutionHandler
    {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
            try {
                final Thread t = new Thread(r, "Temporary task executor");
                t.start();
            }
            catch (Throwable e) {
                throw new RejectedExecutionException("Failed to start a new thread", e);
            }
        }
    }
    
    private static final class MemoryAwareRunnable implements Runnable
    {
        final Runnable task;
        int estimatedSize;
        
        MemoryAwareRunnable(final Runnable task) {
            this.task = task;
        }
        
        public void run() {
            this.task.run();
        }
    }
    
    private static class Limiter
    {
        final long limit;
        private long counter;
        private int waiters;
        
        Limiter(final long limit) {
            this.limit = limit;
        }
        
        synchronized void increase(final long amount) {
            while (this.counter >= this.limit) {
                ++this.waiters;
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    --this.waiters;
                }
            }
            this.counter += amount;
        }
        
        synchronized void decrease(final long amount) {
            this.counter -= amount;
            if (this.counter < this.limit && this.waiters > 0) {
                this.notifyAll();
            }
        }
    }
}
