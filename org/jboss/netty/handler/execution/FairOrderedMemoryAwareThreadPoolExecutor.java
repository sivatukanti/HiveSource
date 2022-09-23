// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FairOrderedMemoryAwareThreadPoolExecutor extends MemoryAwareThreadPoolExecutor
{
    private final EventTask end;
    private final AtomicReferenceFieldUpdater<EventTask, EventTask> fieldUpdater;
    protected final ConcurrentMap<Object, EventTask> map;
    
    public FairOrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize);
        this.end = new EventTask(null);
        this.fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(EventTask.class, EventTask.class, "next");
        this.map = this.newMap();
    }
    
    public FairOrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit);
        this.end = new EventTask(null);
        this.fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(EventTask.class, EventTask.class, "next");
        this.map = this.newMap();
    }
    
    public FairOrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, threadFactory);
        this.end = new EventTask(null);
        this.fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(EventTask.class, EventTask.class, "next");
        this.map = this.newMap();
    }
    
    public FairOrderedMemoryAwareThreadPoolExecutor(final int corePoolSize, final long maxChannelMemorySize, final long maxTotalMemorySize, final long keepAliveTime, final TimeUnit unit, final ObjectSizeEstimator objectSizeEstimator, final ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, objectSizeEstimator, threadFactory);
        this.end = new EventTask(null);
        this.fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(EventTask.class, EventTask.class, "next");
        this.map = this.newMap();
    }
    
    protected ConcurrentMap<Object, EventTask> newMap() {
        return new ConcurrentIdentityWeakKeyHashMap<Object, EventTask>();
    }
    
    @Override
    protected void doExecute(final Runnable task) {
        if (task instanceof ChannelEventRunnable) {
            final ChannelEventRunnable eventRunnable = (ChannelEventRunnable)task;
            final EventTask newEventTask = new EventTask(eventRunnable);
            final Object key = this.getKey(eventRunnable.getEvent());
            final EventTask previousEventTask = this.map.put(key, newEventTask);
            this.removeIfClosed(eventRunnable, key);
            if (previousEventTask != null && this.compareAndSetNext(previousEventTask, null, newEventTask)) {
                return;
            }
            this.doUnorderedExecute(newEventTask);
        }
        else {
            this.doUnorderedExecute(task);
        }
    }
    
    private void removeIfClosed(final ChannelEventRunnable eventRunnable, final Object key) {
        final ChannelEvent event = eventRunnable.getEvent();
        if (event instanceof ChannelStateEvent) {
            final ChannelStateEvent se = (ChannelStateEvent)event;
            if (se.getState() == ChannelState.OPEN && !event.getChannel().isOpen()) {
                this.removeKey(key);
            }
        }
    }
    
    protected boolean removeKey(final Object key) {
        return this.map.remove(key) != null;
    }
    
    protected Object getKey(final ChannelEvent e) {
        return e.getChannel();
    }
    
    @Override
    protected boolean shouldCount(final Runnable task) {
        return !(task instanceof EventTask) && super.shouldCount(task);
    }
    
    protected final boolean compareAndSetNext(final EventTask eventTask, final EventTask expect, final EventTask update) {
        return this.fieldUpdater.compareAndSet(eventTask, expect, update);
    }
    
    protected final class EventTask implements Runnable
    {
        volatile EventTask next;
        private final ChannelEventRunnable runnable;
        
        EventTask(final ChannelEventRunnable runnable) {
            this.runnable = runnable;
        }
        
        public void run() {
            try {
                this.runnable.run();
            }
            finally {
                if (!FairOrderedMemoryAwareThreadPoolExecutor.this.compareAndSetNext(this, null, FairOrderedMemoryAwareThreadPoolExecutor.this.end)) {
                    FairOrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this.next);
                }
            }
        }
    }
}
