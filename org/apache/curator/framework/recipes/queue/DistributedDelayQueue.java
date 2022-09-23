// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import java.io.Closeable;

public class DistributedDelayQueue<T> implements Closeable, QueueBase<T>
{
    private final DistributedQueue<T> queue;
    private static final String SEPARATOR = "|";
    
    DistributedDelayQueue(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath, final ThreadFactory threadFactory, final Executor executor, final int minItemsBeforeRefresh, final String lockPath, final int maxItems, final boolean putInBackground, final int finalFlushMs) {
        Preconditions.checkArgument(minItemsBeforeRefresh >= 0, (Object)"minItemsBeforeRefresh cannot be negative");
        this.queue = new DistributedQueue<T>(client, consumer, serializer, queuePath, threadFactory, executor, minItemsBeforeRefresh, true, lockPath, maxItems, putInBackground, finalFlushMs) {
            @Override
            protected long getDelay(final String itemNode) {
                return this.getDelay(itemNode, System.currentTimeMillis());
            }
            
            private long getDelay(final String itemNode, final long sortTime) {
                final long epoch = getEpoch(itemNode);
                return epoch - sortTime;
            }
            
            @Override
            protected void sortChildren(final List<String> children) {
                final long sortTime = System.currentTimeMillis();
                Collections.sort(children, new Comparator<String>() {
                    @Override
                    public int compare(final String o1, final String o2) {
                        final long diff = DistributedDelayQueue$1.this.getDelay(o1, sortTime) - DistributedDelayQueue$1.this.getDelay(o2, sortTime);
                        return (diff < 0L) ? -1 : ((diff > 0L) ? 1 : 0);
                    }
                });
            }
        };
    }
    
    @Override
    public void start() throws Exception {
        this.queue.start();
    }
    
    @Override
    public void close() throws IOException {
        this.queue.close();
    }
    
    public void put(final T item, final long delayUntilEpoch) throws Exception {
        this.put(item, delayUntilEpoch, 0, null);
    }
    
    public boolean put(final T item, final long delayUntilEpoch, final int maxWait, final TimeUnit unit) throws Exception {
        Preconditions.checkArgument(delayUntilEpoch > 0L, (Object)"delayUntilEpoch cannot be negative");
        this.queue.checkState();
        return this.queue.internalPut(item, null, this.queue.makeItemPath() + epochToString(delayUntilEpoch), maxWait, unit);
    }
    
    public void putMulti(final MultiItem<T> items, final long delayUntilEpoch) throws Exception {
        this.putMulti(items, delayUntilEpoch, 0, null);
    }
    
    public boolean putMulti(final MultiItem<T> items, final long delayUntilEpoch, final int maxWait, final TimeUnit unit) throws Exception {
        Preconditions.checkArgument(delayUntilEpoch > 0L, (Object)"delayUntilEpoch cannot be negative");
        this.queue.checkState();
        return this.queue.internalPut(null, items, this.queue.makeItemPath() + epochToString(delayUntilEpoch), maxWait, unit);
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
    static String epochToString(final long epoch) {
        return "|" + String.format("%08X", epoch) + "|";
    }
    
    private static long getEpoch(final String itemNode) {
        final int index2 = itemNode.lastIndexOf("|");
        final int index3 = (index2 > 0) ? itemNode.lastIndexOf("|", index2 - 1) : -1;
        if (index3 > 0 && index2 > index3 + 1) {
            try {
                final String epochStr = itemNode.substring(index3 + 1, index2);
                return Long.parseLong(epochStr, 16);
            }
            catch (NumberFormatException ex) {}
        }
        return 0L;
    }
}
