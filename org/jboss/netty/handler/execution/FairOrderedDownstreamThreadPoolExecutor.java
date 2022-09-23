// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class FairOrderedDownstreamThreadPoolExecutor extends FairOrderedMemoryAwareThreadPoolExecutor
{
    public FairOrderedDownstreamThreadPoolExecutor(final int corePoolSize) {
        super(corePoolSize, 0L, 0L);
    }
    
    public FairOrderedDownstreamThreadPoolExecutor(final int corePoolSize, final long keepAliveTime, final TimeUnit unit) {
        super(corePoolSize, 0L, 0L, keepAliveTime, unit);
    }
    
    public FairOrderedDownstreamThreadPoolExecutor(final int corePoolSize, final long keepAliveTime, final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, 0L, 0L, keepAliveTime, unit, threadFactory);
    }
    
    @Override
    public ObjectSizeEstimator getObjectSizeEstimator() {
        return null;
    }
    
    @Override
    public void setObjectSizeEstimator(final ObjectSizeEstimator objectSizeEstimator) {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }
    
    @Override
    public long getMaxChannelMemorySize() {
        return 0L;
    }
    
    @Override
    public void setMaxChannelMemorySize(final long maxChannelMemorySize) {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }
    
    @Override
    public long getMaxTotalMemorySize() {
        return 0L;
    }
    
    @Override
    protected boolean shouldCount(final Runnable task) {
        return false;
    }
    
    @Override
    public void execute(final Runnable command) {
        if (command instanceof ChannelUpstreamEventRunnable) {
            throw new RejectedExecutionException("command must be enclosed with an downstream event.");
        }
        this.doExecute(command);
    }
    
    @Override
    protected void doExecute(final Runnable task) {
        if (task instanceof ChannelEventRunnable) {
            final ChannelEventRunnable eventRunnable = (ChannelEventRunnable)task;
            final ChannelEvent event = eventRunnable.getEvent();
            final EventTask newEventTask = new EventTask(eventRunnable);
            final Object key = this.getKey(event);
            final EventTask previousEventTask = this.map.put(key, newEventTask);
            if (previousEventTask != null) {
                if (this.compareAndSetNext(previousEventTask, null, newEventTask)) {
                    return;
                }
            }
            else {
                event.getChannel().getCloseFuture().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture future) throws Exception {
                        FairOrderedDownstreamThreadPoolExecutor.this.removeKey(key);
                    }
                });
            }
            this.doUnorderedExecute(newEventTask);
        }
        else {
            this.doUnorderedExecute(task);
        }
    }
}
