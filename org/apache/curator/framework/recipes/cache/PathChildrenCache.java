// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import org.apache.curator.framework.api.Watchable;
import com.google.common.base.Predicate;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.GetDataWatchBackgroundStatable;
import com.google.common.base.Function;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.BackgroundCallback;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Iterator;
import org.apache.curator.utils.ZKPaths;
import java.util.List;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.Exchanger;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.EnsureContainers;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentMap;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

public class PathChildrenCache implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final String path;
    private final CloseableExecutorService executorService;
    private final boolean cacheData;
    private final boolean dataIsCompressed;
    private final ListenerContainer<PathChildrenCacheListener> listeners;
    private final ConcurrentMap<String, ChildData> currentData;
    private final AtomicReference<Map<String, ChildData>> initialSet;
    private final Set<Operation> operationsQuantizer;
    private final AtomicReference<State> state;
    private final EnsureContainers ensureContainers;
    private static final ChildData NULL_CHILD_DATA;
    private static final boolean USE_EXISTS;
    private volatile Watcher childrenWatcher;
    private volatile Watcher dataWatcher;
    @VisibleForTesting
    volatile Exchanger<Object> rebuildTestExchanger;
    private volatile ConnectionStateListener connectionStateListener;
    private static final ThreadFactory defaultThreadFactory;
    
    @Deprecated
    public PathChildrenCache(final CuratorFramework client, final String path, final PathChildrenCacheMode mode) {
        this(client, path, mode != PathChildrenCacheMode.CACHE_PATHS_ONLY, false, new CloseableExecutorService(Executors.newSingleThreadExecutor(PathChildrenCache.defaultThreadFactory), true));
    }
    
    @Deprecated
    public PathChildrenCache(final CuratorFramework client, final String path, final PathChildrenCacheMode mode, final ThreadFactory threadFactory) {
        this(client, path, mode != PathChildrenCacheMode.CACHE_PATHS_ONLY, false, new CloseableExecutorService(Executors.newSingleThreadExecutor(threadFactory), true));
    }
    
    public PathChildrenCache(final CuratorFramework client, final String path, final boolean cacheData) {
        this(client, path, cacheData, false, new CloseableExecutorService(Executors.newSingleThreadExecutor(PathChildrenCache.defaultThreadFactory), true));
    }
    
    public PathChildrenCache(final CuratorFramework client, final String path, final boolean cacheData, final ThreadFactory threadFactory) {
        this(client, path, cacheData, false, new CloseableExecutorService(Executors.newSingleThreadExecutor(threadFactory), true));
    }
    
    public PathChildrenCache(final CuratorFramework client, final String path, final boolean cacheData, final boolean dataIsCompressed, final ThreadFactory threadFactory) {
        this(client, path, cacheData, dataIsCompressed, new CloseableExecutorService(Executors.newSingleThreadExecutor(threadFactory), true));
    }
    
    public PathChildrenCache(final CuratorFramework client, final String path, final boolean cacheData, final boolean dataIsCompressed, final ExecutorService executorService) {
        this(client, path, cacheData, dataIsCompressed, new CloseableExecutorService(executorService));
    }
    
    public PathChildrenCache(final CuratorFramework client, final String path, final boolean cacheData, final boolean dataIsCompressed, final CloseableExecutorService executorService) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.listeners = new ListenerContainer<PathChildrenCacheListener>();
        this.currentData = Maps.newConcurrentMap();
        this.initialSet = new AtomicReference<Map<String, ChildData>>();
        this.operationsQuantizer = Sets.newSetFromMap((Map<Operation, Boolean>)Maps.newConcurrentMap());
        this.state = new AtomicReference<State>(State.LATENT);
        this.childrenWatcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                PathChildrenCache.this.offerOperation(new RefreshOperation(PathChildrenCache.this, RefreshMode.STANDARD));
            }
        };
        this.dataWatcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                try {
                    if (event.getType() == Event.EventType.NodeDeleted) {
                        PathChildrenCache.this.remove(event.getPath());
                    }
                    else if (event.getType() == Event.EventType.NodeDataChanged) {
                        PathChildrenCache.this.offerOperation(new GetDataOperation(PathChildrenCache.this, event.getPath()));
                    }
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    PathChildrenCache.this.handleException(e);
                }
            }
        };
        this.connectionStateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                PathChildrenCache.this.handleStateChange(newState);
            }
        };
        this.client = client;
        this.path = PathUtils.validatePath(path);
        this.cacheData = cacheData;
        this.dataIsCompressed = dataIsCompressed;
        this.executorService = executorService;
        this.ensureContainers = new EnsureContainers(client, path);
    }
    
    public void start() throws Exception {
        this.start(StartMode.NORMAL);
    }
    
    @Deprecated
    public void start(final boolean buildInitial) throws Exception {
        this.start(buildInitial ? StartMode.BUILD_INITIAL_CACHE : StartMode.NORMAL);
    }
    
    public void start(StartMode mode) throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"already started");
        mode = Preconditions.checkNotNull(mode, (Object)"mode cannot be null");
        this.client.getConnectionStateListenable().addListener(this.connectionStateListener);
        switch (mode) {
            case NORMAL: {
                this.offerOperation(new RefreshOperation(this, RefreshMode.STANDARD));
                break;
            }
            case BUILD_INITIAL_CACHE: {
                this.rebuild();
                break;
            }
            case POST_INITIALIZED_EVENT: {
                this.initialSet.set((Map<String, ChildData>)Maps.newConcurrentMap());
                this.offerOperation(new RefreshOperation(this, RefreshMode.POST_INITIALIZED));
                break;
            }
        }
    }
    
    public void rebuild() throws Exception {
        Preconditions.checkState(!this.executorService.isShutdown(), (Object)"cache has been closed");
        this.ensurePath();
        this.clear();
        final List<String> children = this.client.getChildren().forPath(this.path);
        for (final String child : children) {
            final String fullPath = ZKPaths.makePath(this.path, child);
            this.internalRebuildNode(fullPath);
            if (this.rebuildTestExchanger != null) {
                this.rebuildTestExchanger.exchange(new Object());
            }
        }
        this.offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
    }
    
    public void rebuildNode(final String fullPath) throws Exception {
        Preconditions.checkArgument(ZKPaths.getPathAndNode(fullPath).getPath().equals(this.path), (Object)("Node is not part of this cache: " + fullPath));
        Preconditions.checkState(!this.executorService.isShutdown(), (Object)"cache has been closed");
        this.ensurePath();
        this.internalRebuildNode(fullPath);
        this.offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            this.client.getConnectionStateListenable().removeListener(this.connectionStateListener);
            this.listeners.clear();
            this.executorService.close();
            this.client.clearWatcherReferences(this.childrenWatcher);
            this.client.clearWatcherReferences(this.dataWatcher);
            this.connectionStateListener = null;
            this.childrenWatcher = null;
            this.dataWatcher = null;
        }
    }
    
    public ListenerContainer<PathChildrenCacheListener> getListenable() {
        return this.listeners;
    }
    
    public List<ChildData> getCurrentData() {
        return (List<ChildData>)ImmutableList.copyOf((Collection<?>)Sets.newTreeSet((Iterable<? extends Comparable>)this.currentData.values()));
    }
    
    public ChildData getCurrentData(final String fullPath) {
        return this.currentData.get(fullPath);
    }
    
    public void clearDataBytes(final String fullPath) {
        this.clearDataBytes(fullPath, -1);
    }
    
    public boolean clearDataBytes(final String fullPath, final int ifVersion) {
        final ChildData data = this.currentData.get(fullPath);
        if (data != null && (ifVersion < 0 || ifVersion == data.getStat().getVersion())) {
            if (data.getData() != null) {
                this.currentData.replace(fullPath, data, new ChildData(data.getPath(), data.getStat(), null));
            }
            return true;
        }
        return false;
    }
    
    public void clearAndRefresh() throws Exception {
        this.currentData.clear();
        this.offerOperation(new RefreshOperation(this, RefreshMode.STANDARD));
    }
    
    public void clear() {
        this.currentData.clear();
    }
    
    void refresh(final RefreshMode mode) throws Exception {
        this.ensurePath();
        final BackgroundCallback callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (PathChildrenCache.this.state.get().equals(State.CLOSED)) {
                    return;
                }
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    PathChildrenCache.this.processChildren(event.getChildren(), mode);
                }
            }
        };
        ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getChildren()).usingWatcher(this.childrenWatcher).inBackground(callback)).forPath(this.path);
    }
    
    void callListeners(final PathChildrenCacheEvent event) {
        this.listeners.forEach(new Function<PathChildrenCacheListener, Void>() {
            @Override
            public Void apply(final PathChildrenCacheListener listener) {
                try {
                    listener.childEvent(PathChildrenCache.this.client, event);
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    PathChildrenCache.this.handleException(e);
                }
                return null;
            }
        });
    }
    
    void getDataAndStat(final String fullPath) throws Exception {
        final BackgroundCallback callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                PathChildrenCache.this.applyNewData(fullPath, event.getResultCode(), event.getStat(), (byte[])(PathChildrenCache.this.cacheData ? event.getData() : null));
            }
        };
        if (PathChildrenCache.USE_EXISTS && !this.cacheData) {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.checkExists()).usingWatcher(this.dataWatcher).inBackground(callback)).forPath(fullPath);
        }
        else if (this.dataIsCompressed && this.cacheData) {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData().decompressed()).usingWatcher(this.dataWatcher).inBackground(callback)).forPath(fullPath);
        }
        else {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(this.dataWatcher).inBackground(callback)).forPath(fullPath);
        }
    }
    
    protected void handleException(final Throwable e) {
        this.log.error("", e);
    }
    
    protected void ensurePath() throws Exception {
        this.ensureContainers.ensure();
    }
    
    @VisibleForTesting
    protected void remove(final String fullPath) {
        final ChildData data = this.currentData.remove(fullPath);
        if (data != null) {
            this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_REMOVED, data)));
        }
        final Map<String, ChildData> localInitialSet = this.initialSet.get();
        if (localInitialSet != null) {
            localInitialSet.remove(fullPath);
            this.maybeOfferInitializedEvent(localInitialSet);
        }
    }
    
    private void internalRebuildNode(final String fullPath) throws Exception {
        if (this.cacheData) {
            try {
                final Stat stat = new Stat();
                final byte[] bytes = this.dataIsCompressed ? this.client.getData().decompressed().storingStatIn(stat).forPath(fullPath) : this.client.getData().storingStatIn(stat).forPath(fullPath);
                this.currentData.put(fullPath, new ChildData(fullPath, stat, bytes));
            }
            catch (KeeperException.NoNodeException ignore) {
                this.currentData.remove(fullPath);
            }
        }
        else {
            final Stat stat = this.client.checkExists().forPath(fullPath);
            if (stat != null) {
                this.currentData.put(fullPath, new ChildData(fullPath, stat, null));
            }
            else {
                this.currentData.remove(fullPath);
            }
        }
    }
    
    private void handleStateChange(final ConnectionState newState) {
        switch (newState) {
            case SUSPENDED: {
                this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED, null)));
                break;
            }
            case LOST: {
                this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_LOST, null)));
                break;
            }
            case CONNECTED:
            case RECONNECTED: {
                try {
                    this.offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
                    this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED, null)));
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.handleException(e);
                }
                break;
            }
        }
    }
    
    private void processChildren(final List<String> children, final RefreshMode mode) throws Exception {
        final Set<String> removedNodes = (Set<String>)Sets.newHashSet((Iterable<?>)this.currentData.keySet());
        for (final String child : children) {
            removedNodes.remove(ZKPaths.makePath(this.path, child));
        }
        for (final String fullPath : removedNodes) {
            this.remove(fullPath);
        }
        for (final String name : children) {
            final String fullPath2 = ZKPaths.makePath(this.path, name);
            if (mode == RefreshMode.FORCE_GET_DATA_AND_STAT || !this.currentData.containsKey(fullPath2)) {
                this.getDataAndStat(fullPath2);
            }
            this.updateInitialSet(name, PathChildrenCache.NULL_CHILD_DATA);
        }
        this.maybeOfferInitializedEvent(this.initialSet.get());
    }
    
    private void applyNewData(final String fullPath, final int resultCode, final Stat stat, final byte[] bytes) {
        if (resultCode == KeeperException.Code.OK.intValue()) {
            final ChildData data = new ChildData(fullPath, stat, bytes);
            final ChildData previousData = this.currentData.put(fullPath, data);
            if (previousData == null) {
                this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_ADDED, data)));
            }
            else if (previousData.getStat().getVersion() != stat.getVersion()) {
                this.offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_UPDATED, data)));
            }
            this.updateInitialSet(ZKPaths.getNodeFromPath(fullPath), data);
        }
    }
    
    private void updateInitialSet(final String name, final ChildData data) {
        final Map<String, ChildData> localInitialSet = this.initialSet.get();
        if (localInitialSet != null) {
            localInitialSet.put(name, data);
            this.maybeOfferInitializedEvent(localInitialSet);
        }
    }
    
    private void maybeOfferInitializedEvent(final Map<String, ChildData> localInitialSet) {
        if (!this.hasUninitialized(localInitialSet) && this.initialSet.getAndSet(null) != null) {
            final List<ChildData> children = (List<ChildData>)ImmutableList.copyOf((Collection<?>)localInitialSet.values());
            final PathChildrenCacheEvent event = new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.INITIALIZED, null) {
                @Override
                public List<ChildData> getInitialData() {
                    return children;
                }
            };
            this.offerOperation(new EventOperation(this, event));
        }
    }
    
    private boolean hasUninitialized(final Map<String, ChildData> localInitialSet) {
        if (localInitialSet == null) {
            return false;
        }
        final Map<String, ChildData> uninitializedChildren = Maps.filterValues(localInitialSet, new Predicate<ChildData>() {
            @Override
            public boolean apply(final ChildData input) {
                return input == PathChildrenCache.NULL_CHILD_DATA;
            }
        });
        return uninitializedChildren.size() != 0;
    }
    
    void offerOperation(final Operation operation) {
        if (this.operationsQuantizer.add(operation)) {
            this.submitToExecutor(new Runnable() {
                @Override
                public void run() {
                    try {
                        PathChildrenCache.this.operationsQuantizer.remove(operation);
                        operation.invoke();
                    }
                    catch (InterruptedException e) {
                        if (PathChildrenCache.this.state.get() != State.CLOSED) {
                            PathChildrenCache.this.handleException(e);
                        }
                        Thread.currentThread().interrupt();
                    }
                    catch (Exception e2) {
                        ThreadUtils.checkInterrupted(e2);
                        PathChildrenCache.this.handleException(e2);
                    }
                }
            });
        }
    }
    
    private synchronized void submitToExecutor(final Runnable command) {
        if (this.state.get() == State.STARTED) {
            this.executorService.submit(command);
        }
    }
    
    static {
        NULL_CHILD_DATA = new ChildData("/", null, null);
        USE_EXISTS = Boolean.getBoolean("curator-path-children-cache-use-exists");
        defaultThreadFactory = ThreadUtils.newThreadFactory("PathChildrenCache");
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
    
    public enum StartMode
    {
        NORMAL, 
        BUILD_INITIAL_CACHE, 
        POST_INITIALIZED_EVENT;
    }
    
    enum RefreshMode
    {
        STANDARD, 
        FORCE_GET_DATA_AND_STAT, 
        POST_INITIALIZED;
    }
}
