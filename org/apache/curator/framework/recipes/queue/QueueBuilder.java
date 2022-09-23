// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.shaded.com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.TimeUnit;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.CuratorFramework;

public class QueueBuilder<T>
{
    private final CuratorFramework client;
    private final QueueConsumer<T> consumer;
    private final QueueSerializer<T> serializer;
    private final String queuePath;
    private ThreadFactory factory;
    private Executor executor;
    private String lockPath;
    private int maxItems;
    private boolean putInBackground;
    private int finalFlushMs;
    static final ThreadFactory defaultThreadFactory;
    static final int NOT_SET = Integer.MAX_VALUE;
    
    public static <T> QueueBuilder<T> builder(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath) {
        return new QueueBuilder<T>(client, consumer, serializer, queuePath);
    }
    
    public DistributedQueue<T> buildQueue() {
        return new DistributedQueue<T>(this.client, this.consumer, this.serializer, this.queuePath, this.factory, this.executor, Integer.MAX_VALUE, false, this.lockPath, this.maxItems, this.putInBackground, this.finalFlushMs);
    }
    
    public DistributedIdQueue<T> buildIdQueue() {
        return new DistributedIdQueue<T>(this.client, this.consumer, this.serializer, this.queuePath, this.factory, this.executor, Integer.MAX_VALUE, false, this.lockPath, this.maxItems, this.putInBackground, this.finalFlushMs);
    }
    
    public DistributedPriorityQueue<T> buildPriorityQueue(final int minItemsBeforeRefresh) {
        return new DistributedPriorityQueue<T>(this.client, this.consumer, this.serializer, this.queuePath, this.factory, this.executor, minItemsBeforeRefresh, this.lockPath, this.maxItems, this.putInBackground, this.finalFlushMs);
    }
    
    public DistributedDelayQueue<T> buildDelayQueue() {
        return new DistributedDelayQueue<T>(this.client, this.consumer, this.serializer, this.queuePath, this.factory, this.executor, Integer.MAX_VALUE, this.lockPath, this.maxItems, this.putInBackground, this.finalFlushMs);
    }
    
    public QueueBuilder<T> threadFactory(final ThreadFactory factory) {
        Preconditions.checkNotNull(factory, (Object)"factory cannot be null");
        this.factory = factory;
        return this;
    }
    
    public QueueBuilder<T> executor(final Executor executor) {
        Preconditions.checkNotNull(executor, (Object)"executor cannot be null");
        this.executor = executor;
        return this;
    }
    
    public QueueBuilder<T> lockPath(final String path) {
        this.lockPath = PathUtils.validatePath(path);
        return this;
    }
    
    public QueueBuilder<T> maxItems(final int maxItems) {
        this.maxItems = maxItems;
        this.putInBackground = false;
        return this;
    }
    
    public QueueBuilder<T> putInBackground(final boolean putInBackground) {
        this.putInBackground = putInBackground;
        return this;
    }
    
    public QueueBuilder<T> finalFlushTime(final int time, final TimeUnit unit) {
        this.finalFlushMs = (int)unit.toMillis(time);
        return this;
    }
    
    private QueueBuilder(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath) {
        this.maxItems = Integer.MAX_VALUE;
        this.putInBackground = true;
        this.finalFlushMs = 5000;
        this.client = client;
        this.consumer = consumer;
        this.serializer = serializer;
        this.queuePath = PathUtils.validatePath(queuePath);
        this.factory = QueueBuilder.defaultThreadFactory;
        this.executor = MoreExecutors.sameThreadExecutor();
    }
    
    static {
        defaultThreadFactory = ThreadUtils.newThreadFactory("QueueBuilder");
    }
}
