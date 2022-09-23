// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import java.io.Closeable;

public class DistributedPriorityQueue<T> implements Closeable, QueueBase<T>
{
    private final DistributedQueue<T> queue;
    
    DistributedPriorityQueue(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath, final ThreadFactory threadFactory, final Executor executor, final int minItemsBeforeRefresh, final String lockPath, final int maxItems, final boolean putInBackground, final int finalFlushMs) {
        Preconditions.checkArgument(minItemsBeforeRefresh >= 0, (Object)"minItemsBeforeRefresh cannot be negative");
        this.queue = new DistributedQueue<T>(client, consumer, serializer, queuePath, threadFactory, executor, minItemsBeforeRefresh, true, lockPath, maxItems, putInBackground, finalFlushMs);
    }
    
    @Override
    public void start() throws Exception {
        this.queue.start();
    }
    
    @Override
    public void close() throws IOException {
        this.queue.close();
    }
    
    public void put(final T item, final int priority) throws Exception {
        this.put(item, priority, 0, null);
    }
    
    public boolean put(final T item, final int priority, final int maxWait, final TimeUnit unit) throws Exception {
        this.queue.checkState();
        final String priorityHex = priorityToString(priority);
        return this.queue.internalPut(item, null, this.queue.makeItemPath() + priorityHex, maxWait, unit);
    }
    
    public void putMulti(final MultiItem<T> items, final int priority) throws Exception {
        this.putMulti(items, priority, 0, null);
    }
    
    public boolean putMulti(final MultiItem<T> items, final int priority, final int maxWait, final TimeUnit unit) throws Exception {
        this.queue.checkState();
        final String priorityHex = priorityToString(priority);
        return this.queue.internalPut(null, items, this.queue.makeItemPath() + priorityHex, maxWait, unit);
    }
    
    @Override
    public void setErrorMode(final ErrorMode newErrorMode) {
        this.queue.setErrorMode(newErrorMode);
    }
    
    @Override
    public boolean flushPuts(final long waitTime, final TimeUnit timeUnit) throws InterruptedException {
        return this.queue.flushPuts(waitTime, timeUnit);
    }
    
    @Override
    public ListenerContainer<QueuePutListener<T>> getPutListenerContainer() {
        return this.queue.getPutListenerContainer();
    }
    
    @Override
    public int getLastMessageCount() {
        return this.queue.getLastMessageCount();
    }
    
    @VisibleForTesting
    ChildrenCache getCache() {
        return this.queue.getCache();
    }
    
    @VisibleForTesting
    static String priorityToString(final int priority) {
        final long l = (long)priority & 0xFFFFFFFFL;
        return String.format("%s%08X", (priority >= 0) ? "1" : "0", l);
    }
}
