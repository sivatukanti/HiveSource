// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.api.Versionable;
import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.ACLPathAndBytesable;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import java.util.Iterator;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.Semaphore;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.api.ErrorListenerPathAndBytesable;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.BackgroundCallback;
import com.google.common.base.Function;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import java.io.IOException;
import java.io.Closeable;
import org.apache.curator.utils.CloseableUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import org.apache.zookeeper.KeeperException;
import java.util.concurrent.Executors;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class DistributedQueue<T> implements QueueBase<T>
{
    private final Logger log;
    private final CuratorFramework client;
    private final QueueSerializer<T> serializer;
    private final String queuePath;
    private final Executor executor;
    private final ExecutorService service;
    private final AtomicReference<State> state;
    private final QueueConsumer<T> consumer;
    private final int minItemsBeforeRefresh;
    private final boolean refreshOnWatch;
    private final boolean isProducerOnly;
    private final String lockPath;
    private final AtomicReference<ErrorMode> errorMode;
    private final ListenerContainer<QueuePutListener<T>> putListenerContainer;
    private final AtomicInteger lastChildCount;
    private final int maxItems;
    private final int finalFlushMs;
    private final boolean putInBackground;
    private final ChildrenCache childrenCache;
    private final AtomicInteger putCount;
    private static final String QUEUE_ITEM_NAME = "queue-";
    
    DistributedQueue(final CuratorFramework client, final QueueConsumer<T> consumer, final QueueSerializer<T> serializer, final String queuePath, final ThreadFactory threadFactory, final Executor executor, final int minItemsBeforeRefresh, final boolean refreshOnWatch, final String lockPath, final int maxItems, final boolean putInBackground, final int finalFlushMs) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.state = new AtomicReference<State>(State.LATENT);
        this.errorMode = new AtomicReference<ErrorMode>(ErrorMode.REQUEUE);
        this.putListenerContainer = new ListenerContainer<QueuePutListener<T>>();
        this.lastChildCount = new AtomicInteger(0);
        this.putCount = new AtomicInteger(0);
        Preconditions.checkNotNull(client, (Object)"client cannot be null");
        Preconditions.checkNotNull(serializer, (Object)"serializer cannot be null");
        Preconditions.checkNotNull(threadFactory, (Object)"threadFactory cannot be null");
        Preconditions.checkNotNull(executor, (Object)"executor cannot be null");
        Preconditions.checkArgument(maxItems > 0, (Object)"maxItems must be a positive number");
        this.isProducerOnly = (consumer == null);
        this.lockPath = ((lockPath == null) ? null : PathUtils.validatePath(lockPath));
        this.putInBackground = putInBackground;
        this.consumer = consumer;
        this.minItemsBeforeRefresh = minItemsBeforeRefresh;
        this.refreshOnWatch = refreshOnWatch;
        this.client = client;
        this.serializer = serializer;
        this.queuePath = PathUtils.validatePath(queuePath);
        this.executor = executor;
        this.maxItems = maxItems;
        this.finalFlushMs = finalFlushMs;
        this.service = Executors.newFixedThreadPool(2, threadFactory);
        this.childrenCache = new ChildrenCache(client, queuePath);
        if (maxItems != Integer.MAX_VALUE && putInBackground) {
            this.log.warn("Bounded queues should set putInBackground(false) in the builder. Putting in the background will result in spotty maxItem consistency.");
        }
    }
    
    @Override
    public void start() throws Exception {
        if (!this.state.compareAndSet(State.LATENT, State.STARTED)) {
            throw new IllegalStateException();
        }
        try {
            this.client.create().creatingParentContainersIfNeeded().forPath(this.queuePath);
        }
        catch (KeeperException.NodeExistsException ex) {}
        if (this.lockPath != null) {
            try {
                this.client.create().creatingParentContainersIfNeeded().forPath(this.lockPath);
            }
            catch (KeeperException.NodeExistsException ex2) {}
        }
        if (!this.isProducerOnly || this.maxItems != Integer.MAX_VALUE) {
            this.childrenCache.start();
        }
        if (!this.isProducerOnly) {
            this.service.submit((Callable<Object>)new Callable<Object>() {
                @Override
                public Object call() {
                    DistributedQueue.this.runLoop();
                    return null;
                }
            });
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.STOPPED)) {
            if (this.finalFlushMs > 0) {
                try {
                    this.flushPuts(this.finalFlushMs, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            CloseableUtils.closeQuietly(this.childrenCache);
            this.putListenerContainer.clear();
            this.service.shutdownNow();
        }
    }
    
    @Override
    public ListenerContainer<QueuePutListener<T>> getPutListenerContainer() {
        return this.putListenerContainer;
    }
    
    @Override
    public void setErrorMode(final ErrorMode newErrorMode) {
        Preconditions.checkNotNull(this.lockPath, (Object)"lockPath cannot be null");
        if (newErrorMode == ErrorMode.REQUEUE) {
            this.log.warn("ErrorMode.REQUEUE requires ZooKeeper version 3.4.x+ - make sure you are not using a prior version");
        }
        this.errorMode.set(newErrorMode);
    }
    
    @Override
    public boolean flushPuts(final long waitTime, final TimeUnit timeUnit) throws InterruptedException {
        long msWaitRemaining = TimeUnit.MILLISECONDS.convert(waitTime, timeUnit);
        synchronized (this.putCount) {
            while (this.putCount.get() > 0) {
                if (msWaitRemaining <= 0L) {
                    return false;
                }
                final long startMs = System.currentTimeMillis();
                this.putCount.wait(msWaitRemaining);
                final long elapsedMs = System.currentTimeMillis() - startMs;
                msWaitRemaining -= elapsedMs;
            }
        }
        return true;
    }
    
    public void put(final T item) throws Exception {
        this.put(item, 0, null);
    }
    
    public boolean put(final T item, final int maxWait, final TimeUnit unit) throws Exception {
        this.checkState();
        final String path = this.makeItemPath();
        return this.internalPut(item, null, path, maxWait, unit);
    }
    
    public void putMulti(final MultiItem<T> items) throws Exception {
        this.putMulti(items, 0, null);
    }
    
    public boolean putMulti(final MultiItem<T> items, final int maxWait, final TimeUnit unit) throws Exception {
        this.checkState();
        final String path = this.makeItemPath();
        return this.internalPut(null, items, path, maxWait, unit);
    }
    
    @Override
    public int getLastMessageCount() {
        return this.lastChildCount.get();
    }
    
    boolean internalPut(final T item, MultiItem<T> multiItem, final String path, final int maxWait, final TimeUnit unit) throws Exception {
        if (!this.blockIfMaxed(maxWait, unit)) {
            return false;
        }
        final MultiItem<T> givenMultiItem = multiItem;
        if (item != null) {
            final AtomicReference<T> ref = new AtomicReference<T>(item);
            multiItem = new MultiItem<T>() {
                @Override
                public T nextItem() throws Exception {
                    return ref.getAndSet(null);
                }
            };
        }
        this.putCount.incrementAndGet();
        final byte[] bytes = ItemSerializer.serialize(multiItem, this.serializer);
        if (this.putInBackground) {
            this.doPutInBackground(item, path, givenMultiItem, bytes);
        }
        else {
            this.doPutInForeground(item, path, givenMultiItem, bytes);
        }
        return true;
    }
    
    private void doPutInForeground(final T item, final String path, final MultiItem<T> givenMultiItem, final byte[] bytes) throws Exception {
        ((CreateModable<ACLBackgroundPathAndBytesable>)this.client.create()).withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, bytes);
        synchronized (this.putCount) {
            this.putCount.decrementAndGet();
            this.putCount.notifyAll();
        }
        this.putListenerContainer.forEach(new Function<QueuePutListener<T>, Void>() {
            @Override
            public Void apply(final QueuePutListener<T> listener) {
                if (item != null) {
                    listener.putCompleted(item);
                }
                else {
                    listener.putMultiCompleted(givenMultiItem);
                }
                return null;
            }
        });
    }
    
    private void doPutInBackground(final T item, final String path, final MultiItem<T> givenMultiItem, final byte[] bytes) throws Exception {
        final BackgroundCallback callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() != KeeperException.Code.OK.intValue()) {
                    return;
                }
                if (event.getType() == CuratorEventType.CREATE) {
                    synchronized (DistributedQueue.this.putCount) {
                        DistributedQueue.this.putCount.decrementAndGet();
                        DistributedQueue.this.putCount.notifyAll();
                    }
                }
                DistributedQueue.this.putListenerContainer.forEach(new Function<QueuePutListener<T>, Void>() {
                    @Override
                    public Void apply(final QueuePutListener<T> listener) {
                        if (item != null) {
                            listener.putCompleted(item);
                        }
                        else {
                            listener.putMultiCompleted(givenMultiItem);
                        }
                        return null;
                    }
                });
            }
        };
        this.internalCreateNode(path, bytes, callback);
    }
    
    @VisibleForTesting
    void internalCreateNode(final String path, final byte[] bytes, final BackgroundCallback callback) throws Exception {
        ((ErrorListenerPathAndBytesable)((CreateModable<ACLBackgroundPathAndBytesable>)this.client.create()).withMode(CreateMode.PERSISTENT_SEQUENTIAL).inBackground(callback)).forPath(path, bytes);
    }
    
    void checkState() throws Exception {
        if (this.state.get() != State.STARTED) {
            throw new IllegalStateException();
        }
    }
    
    String makeItemPath() {
        return ZKPaths.makePath(this.queuePath, "queue-");
    }
    
    @VisibleForTesting
    ChildrenCache getCache() {
        return this.childrenCache;
    }
    
    protected void sortChildren(final List<String> children) {
        Collections.sort(children);
    }
    
    protected List<String> getChildren() throws Exception {
        return this.client.getChildren().forPath(this.queuePath);
    }
    
    protected long getDelay(final String itemNode) {
        return 0L;
    }
    
    protected boolean tryRemove(final String itemNode) throws Exception {
        final boolean isUsingLockSafety = this.lockPath != null;
        if (isUsingLockSafety) {
            return this.processWithLockSafety(itemNode, ProcessType.REMOVE);
        }
        return this.processNormally(itemNode, ProcessType.REMOVE);
    }
    
    private boolean blockIfMaxed(final int maxWait, final TimeUnit unit) throws Exception {
        ChildrenCache.Data data = this.childrenCache.getData();
        while (data.children.size() >= this.maxItems) {
            final long previousVersion = data.version;
            data = this.childrenCache.blockingNextGetData(data.version, maxWait, unit);
            if (data.version == previousVersion) {
                return false;
            }
        }
        return true;
    }
    
    private void runLoop() {
        long currentVersion = -1L;
        long maxWaitMs = -1L;
        try {
            while (this.state.get() == State.STARTED) {
                try {
                    final ChildrenCache.Data data = (maxWaitMs > 0L) ? this.childrenCache.blockingNextGetData(currentVersion, maxWaitMs, TimeUnit.MILLISECONDS) : this.childrenCache.blockingNextGetData(currentVersion);
                    currentVersion = data.version;
                    final List<String> children = (List<String>)Lists.newArrayList((Iterable<?>)data.children);
                    this.sortChildren(children);
                    if (children.size() <= 0) {
                        continue;
                    }
                    maxWaitMs = this.getDelay(children.get(0));
                    if (maxWaitMs > 0L) {
                        continue;
                    }
                    this.processChildren(children, currentVersion);
                }
                catch (InterruptedException ex) {}
            }
        }
        catch (Exception e) {
            this.log.error("Exception caught in background handler", e);
        }
    }
    
    private void processChildren(final List<String> children, final long currentVersion) throws Exception {
        final Semaphore processedLatch = new Semaphore(0);
        final boolean isUsingLockSafety = this.lockPath != null;
        int min = this.minItemsBeforeRefresh;
        for (final String itemNode : children) {
            if (Thread.currentThread().isInterrupted()) {
                processedLatch.release(children.size());
                break;
            }
            if (!itemNode.startsWith("queue-")) {
                this.log.warn("Foreign node in queue path: " + itemNode);
                processedLatch.release();
            }
            else {
                if (min-- <= 0 && this.refreshOnWatch && currentVersion != this.childrenCache.getData().version) {
                    processedLatch.release(children.size());
                    break;
                }
                if (this.getDelay(itemNode) > 0L) {
                    processedLatch.release();
                }
                else {
                    this.executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isUsingLockSafety) {
                                    DistributedQueue.this.processWithLockSafety(itemNode, ProcessType.NORMAL);
                                }
                                else {
                                    DistributedQueue.this.processNormally(itemNode, ProcessType.NORMAL);
                                }
                            }
                            catch (Exception e) {
                                ThreadUtils.checkInterrupted(e);
                                DistributedQueue.this.log.error("Error processing message at " + itemNode, e);
                            }
                            finally {
                                processedLatch.release();
                            }
                        }
                    });
                }
            }
        }
        processedLatch.acquire(children.size());
    }
    
    private ProcessMessageBytesCode processMessageBytes(final String itemNode, final byte[] bytes) throws Exception {
        ProcessMessageBytesCode resultCode = ProcessMessageBytesCode.NORMAL;
        MultiItem<T> items;
        try {
            items = ItemSerializer.deserialize(bytes, this.serializer);
        }
        catch (Throwable e) {
            ThreadUtils.checkInterrupted(e);
            this.log.error("Corrupted queue item: " + itemNode, e);
            return resultCode;
        }
        while (true) {
            final T item = items.nextItem();
            if (item == null) {
                break;
            }
            try {
                this.consumer.consumeMessage(item);
            }
            catch (Throwable e2) {
                ThreadUtils.checkInterrupted(e2);
                this.log.error("Exception processing queue item: " + itemNode, e2);
                if (this.errorMode.get() == ErrorMode.REQUEUE) {
                    resultCode = ProcessMessageBytesCode.REQUEUE;
                    break;
                }
                continue;
            }
        }
        return resultCode;
    }
    
    private boolean processNormally(final String itemNode, final ProcessType type) throws Exception {
        try {
            final String itemPath = ZKPaths.makePath(this.queuePath, itemNode);
            final Stat stat = new Stat();
            byte[] bytes = null;
            if (type == ProcessType.NORMAL) {
                bytes = this.client.getData().storingStatIn(stat).forPath(itemPath);
            }
            if (this.client.getState() == CuratorFrameworkState.STARTED) {
                ((Versionable<BackgroundPathable>)this.client.delete()).withVersion(stat.getVersion()).forPath(itemPath);
            }
            if (type == ProcessType.NORMAL) {
                this.processMessageBytes(itemNode, bytes);
            }
            return true;
        }
        catch (KeeperException.NodeExistsException ex) {}
        catch (KeeperException.NoNodeException ex2) {}
        catch (KeeperException.BadVersionException ex3) {}
        return false;
    }
    
    @VisibleForTesting
    protected boolean processWithLockSafety(final String itemNode, final ProcessType type) throws Exception {
        final String lockNodePath = ZKPaths.makePath(this.lockPath, itemNode);
        boolean lockCreated = false;
        try {
            ((CreateModable<ACLBackgroundPathAndBytesable>)this.client.create()).withMode(CreateMode.EPHEMERAL).forPath(lockNodePath);
            lockCreated = true;
            final String itemPath = ZKPaths.makePath(this.queuePath, itemNode);
            boolean requeue = false;
            byte[] bytes = null;
            if (type == ProcessType.NORMAL) {
                bytes = this.client.getData().forPath(itemPath);
                requeue = (this.processMessageBytes(itemNode, bytes) == ProcessMessageBytesCode.REQUEUE);
            }
            if (requeue) {
                this.client.inTransaction().delete().forPath(itemPath).and().create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(this.makeRequeueItemPath(itemPath), bytes).and().commit();
            }
            else {
                this.client.delete().forPath(itemPath);
            }
            return true;
        }
        catch (KeeperException.NodeExistsException ex) {}
        catch (KeeperException.NoNodeException ex2) {}
        catch (KeeperException.BadVersionException ex3) {}
        finally {
            if (lockCreated) {
                this.client.delete().guaranteed().forPath(lockNodePath);
            }
        }
        return false;
    }
    
    protected String makeRequeueItemPath(final String itemPath) {
        return this.makeItemPath();
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        STOPPED;
    }
    
    @VisibleForTesting
    protected enum ProcessType
    {
        NORMAL, 
        REMOVE;
    }
    
    private enum ProcessMessageBytesCode
    {
        NORMAL, 
        REQUEUE;
    }
}
