// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import java.util.Collections;
import java.util.Comparator;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.listen.ListenerContainer;
import java.io.IOException;
import java.util.List;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class DistributedIdQueue<T> implements QueueBase<T>
{
    private final Logger log;
    private final DistributedQueue<T> queue;
    private static final char SEPARATOR = '|';
    
    DistributedIdQueue(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath, final ThreadFactory threadFactory, final Executor executor, final int minItemsBeforeRefresh, final boolean refreshOnWatch, final String lockPath, final int maxItems, final boolean putInBackground, final int finalFlushMs) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.queue = new DistributedQueue<T>(client, consumer, serializer, queuePath, threadFactory, executor, minItemsBeforeRefresh, refreshOnWatch, lockPath, maxItems, putInBackground, finalFlushMs) {
            @Override
            protected void sortChildren(final List<String> children) {
                DistributedIdQueue.this.internalSortChildren(children);
            }
            
            @Override
            protected String makeRequeueItemPath(final String itemPath) {
                return DistributedIdQueue.this.makeIdPath(DistributedIdQueue.this.parseId(itemPath).id);
            }
        };
        if (this.queue.makeItemPath().contains(Character.toString('|'))) {
            throw new IllegalStateException("DistributedQueue can't use |");
        }
    }
    
    @Override
    public void start() throws Exception {
        this.queue.start();
    }
    
    @Override
    public void close() throws IOException {
        this.queue.close();
    }
    
    @Override
    public ListenerContainer<QueuePutListener<T>> getPutListenerContainer() {
        return this.queue.getPutListenerContainer();
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
    public int getLastMessageCount() {
        return this.queue.getLastMessageCount();
    }
    
    public void put(final T item, final String itemId) throws Exception {
        this.put(item, itemId, 0, null);
    }
    
    public boolean put(final T item, final String itemId, final int maxWait, final TimeUnit unit) throws Exception {
        Preconditions.checkArgument(this.isValidId(itemId), (Object)("Invalid id: " + itemId));
        this.queue.checkState();
        return this.queue.internalPut(item, null, this.makeIdPath(itemId), maxWait, unit);
    }
    
    public int remove(String id) throws Exception {
        id = Preconditions.checkNotNull(id, (Object)"id cannot be null");
        this.queue.checkState();
        int count = 0;
        for (final String name : this.queue.getChildren()) {
            if (this.parseId(name).id.equals(id) && this.queue.tryRemove(name)) {
                ++count;
            }
        }
        return count;
    }
    
    @VisibleForTesting
    boolean debugIsQueued(final String id) throws Exception {
        for (final String name : this.queue.getChildren()) {
            if (this.parseId(name).id.equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    private String makeIdPath(final String itemId) {
        return this.queue.makeItemPath() + '|' + fixId(itemId) + '|';
    }
    
    private void internalSortChildren(final List<String> children) {
        Collections.sort(children, new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return DistributedIdQueue.this.parseId(o1).cleaned.compareTo(DistributedIdQueue.this.parseId(o2).cleaned);
            }
        });
    }
    
    private boolean isValidId(final String id) {
        return id != null && id.length() > 0;
    }
    
    private static String fixId(final String id) {
        final String fixed = id.replace('/', '_');
        return fixed.replace('|', '_');
    }
    
    private Parts parseId(final String name) {
        final int firstIndex = name.indexOf(124);
        final int secondIndex = name.indexOf(124, firstIndex + 1);
        if (firstIndex < 0 || secondIndex < 0) {
            this.log.error("Bad node in queue: " + name);
            return new Parts(name, name);
        }
        return new Parts(name.substring(firstIndex + 1, secondIndex), name.substring(0, firstIndex) + name.substring(secondIndex + 1));
    }
    
    private static class Parts
    {
        final String id;
        final String cleaned;
        
        private Parts(final String id, final String cleaned) {
            this.id = id;
            this.cleaned = cleaned;
        }
    }
}
