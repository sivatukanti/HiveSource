// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import org.apache.curator.framework.api.Watchable;
import java.util.List;
import java.util.Collections;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import java.util.ArrayList;
import org.apache.curator.framework.api.GetDataWatchBackgroundStatable;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.Watcher;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.data.Stat;
import com.google.common.base.Function;
import java.util.Iterator;
import org.apache.curator.shaded.com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.framework.state.ConnectionState;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.state.ConnectionStateListener;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.ExecutorService;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.slf4j.Logger;
import java.io.Closeable;

public class TreeCache implements Closeable
{
    private static final Logger LOG;
    private final boolean createParentNodes;
    private final TreeCacheSelector selector;
    private static final AtomicReferenceFieldUpdater<TreeNode, NodeState> nodeStateUpdater;
    private static final AtomicReferenceFieldUpdater<TreeNode, ChildData> childDataUpdater;
    private static final AtomicReferenceFieldUpdater<TreeNode, ConcurrentMap> childrenUpdater;
    private final AtomicLong outstandingOps;
    private final AtomicBoolean isInitialized;
    private final TreeNode root;
    private final CuratorFramework client;
    private final ExecutorService executorService;
    private final boolean cacheData;
    private final boolean dataIsCompressed;
    private final int maxDepth;
    private final ListenerContainer<TreeCacheListener> listeners;
    private final ListenerContainer<UnhandledErrorListener> errorListeners;
    private final AtomicReference<TreeState> treeState;
    private final ConnectionStateListener connectionStateListener;
    static final ThreadFactory defaultThreadFactory;
    
    public static Builder newBuilder(final CuratorFramework client, final String path) {
        return new Builder(client, path);
    }
    
    public TreeCache(final CuratorFramework client, final String path) {
        this(client, path, true, false, Integer.MAX_VALUE, Executors.newSingleThreadExecutor(TreeCache.defaultThreadFactory), false, new DefaultTreeCacheSelector());
    }
    
    TreeCache(final CuratorFramework client, final String path, final boolean cacheData, final boolean dataIsCompressed, final int maxDepth, final ExecutorService executorService, final boolean createParentNodes, final TreeCacheSelector selector) {
        this.outstandingOps = new AtomicLong(0L);
        this.isInitialized = new AtomicBoolean(false);
        this.listeners = new ListenerContainer<TreeCacheListener>();
        this.errorListeners = new ListenerContainer<UnhandledErrorListener>();
        this.treeState = new AtomicReference<TreeState>(TreeState.LATENT);
        this.connectionStateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                TreeCache.this.handleStateChange(newState);
            }
        };
        this.createParentNodes = createParentNodes;
        this.selector = Preconditions.checkNotNull(selector, (Object)"selector cannot be null");
        this.root = new TreeNode(PathUtils.validatePath(path), null);
        this.client = Preconditions.checkNotNull(client, (Object)"client cannot be null");
        this.cacheData = cacheData;
        this.dataIsCompressed = dataIsCompressed;
        this.maxDepth = maxDepth;
        this.executorService = Preconditions.checkNotNull(executorService, (Object)"executorService cannot be null");
    }
    
    public TreeCache start() throws Exception {
        Preconditions.checkState(this.treeState.compareAndSet(TreeState.LATENT, TreeState.STARTED), (Object)"already started");
        if (this.createParentNodes) {
            this.client.createContainers(this.root.path);
        }
        this.client.getConnectionStateListenable().addListener(this.connectionStateListener);
        if (this.client.getZookeeperClient().isConnected()) {
            this.root.wasCreated();
        }
        return this;
    }
    
    @Override
    public void close() {
        if (this.treeState.compareAndSet(TreeState.STARTED, TreeState.CLOSED)) {
            this.client.getConnectionStateListenable().removeListener(this.connectionStateListener);
            this.listeners.clear();
            this.executorService.shutdown();
            try {
                this.root.wasDeleted();
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                this.handleException(e);
            }
        }
    }
    
    public Listenable<TreeCacheListener> getListenable() {
        return this.listeners;
    }
    
    @VisibleForTesting
    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
        return this.errorListeners;
    }
    
    private TreeNode find(final String findPath) {
        PathUtils.validatePath(findPath);
        final LinkedList<String> rootElements = new LinkedList<String>(ZKPaths.split(this.root.path));
        final LinkedList<String> findElements = new LinkedList<String>(ZKPaths.split(findPath));
        while (!rootElements.isEmpty()) {
            if (findElements.isEmpty()) {
                return null;
            }
            final String nextRoot = rootElements.removeFirst();
            final String nextFind = findElements.removeFirst();
            if (!nextFind.equals(nextRoot)) {
                return null;
            }
        }
        TreeNode current = this.root;
        while (!findElements.isEmpty()) {
            final String nextFind = findElements.removeFirst();
            final ConcurrentMap<String, TreeNode> map = current.children;
            if (map == null) {
                return null;
            }
            current = map.get(nextFind);
            if (current == null) {
                return null;
            }
        }
        return current;
    }
    
    public Map<String, ChildData> getCurrentChildren(final String fullPath) {
        final TreeNode node = this.find(fullPath);
        if (node == null || node.nodeState != NodeState.LIVE) {
            return null;
        }
        final ConcurrentMap<String, TreeNode> map = node.children;
        Map<String, ChildData> result;
        if (map == null) {
            result = (Map<String, ChildData>)ImmutableMap.of();
        }
        else {
            final ImmutableMap.Builder<String, ChildData> builder = ImmutableMap.builder();
            for (final Map.Entry<String, TreeNode> entry : map.entrySet()) {
                final TreeNode childNode = entry.getValue();
                final ChildData childData = childNode.childData;
                if (childData != null && childNode.nodeState == NodeState.LIVE) {
                    builder.put(entry.getKey(), childData);
                }
            }
            result = builder.build();
        }
        return (node.nodeState == NodeState.LIVE) ? result : null;
    }
    
    public ChildData getCurrentData(final String fullPath) {
        final TreeNode node = this.find(fullPath);
        if (node == null || node.nodeState != NodeState.LIVE) {
            return null;
        }
        final ChildData result = node.childData;
        return (node.nodeState == NodeState.LIVE) ? result : null;
    }
    
    private void callListeners(final TreeCacheEvent event) {
        this.listeners.forEach(new Function<TreeCacheListener, Void>() {
            @Override
            public Void apply(final TreeCacheListener listener) {
                try {
                    listener.childEvent(TreeCache.this.client, event);
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    TreeCache.this.handleException(e);
                }
                return null;
            }
        });
    }
    
    private void handleException(final Throwable e) {
        if (this.errorListeners.size() == 0) {
            TreeCache.LOG.error("", e);
        }
        else {
            this.errorListeners.forEach(new Function<UnhandledErrorListener, Void>() {
                @Override
                public Void apply(final UnhandledErrorListener listener) {
                    try {
                        listener.unhandledError("", e);
                    }
                    catch (Exception e) {
                        ThreadUtils.checkInterrupted(e);
                        TreeCache.LOG.error("Exception handling exception", e);
                    }
                    return null;
                }
            });
        }
    }
    
    private void handleStateChange(final ConnectionState newState) {
        switch (newState) {
            case SUSPENDED: {
                this.publishEvent(TreeCacheEvent.Type.CONNECTION_SUSPENDED);
                break;
            }
            case LOST: {
                this.isInitialized.set(false);
                this.publishEvent(TreeCacheEvent.Type.CONNECTION_LOST);
                break;
            }
            case CONNECTED: {
                try {
                    this.root.wasCreated();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.handleException(e);
                }
                break;
            }
            case RECONNECTED: {
                try {
                    this.root.wasReconnected();
                    this.publishEvent(TreeCacheEvent.Type.CONNECTION_RECONNECTED);
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.handleException(e);
                }
                break;
            }
        }
    }
    
    private void publishEvent(final TreeCacheEvent.Type type) {
        this.publishEvent(new TreeCacheEvent(type, null));
    }
    
    private void publishEvent(final TreeCacheEvent.Type type, final String path) {
        this.publishEvent(new TreeCacheEvent(type, new ChildData(path, null, null)));
    }
    
    private void publishEvent(final TreeCacheEvent.Type type, final ChildData data) {
        this.publishEvent(new TreeCacheEvent(type, data));
    }
    
    private void publishEvent(final TreeCacheEvent event) {
        if (this.treeState.get() != TreeState.CLOSED) {
            TreeCache.LOG.debug("publishEvent: {}", event);
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TreeCache.this.callListeners(event);
                    }
                    catch (Exception e) {
                        ThreadUtils.checkInterrupted(e);
                        TreeCache.this.handleException(e);
                    }
                }
            });
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TreeCache.class);
        nodeStateUpdater = AtomicReferenceFieldUpdater.newUpdater(TreeNode.class, NodeState.class, "nodeState");
        childDataUpdater = AtomicReferenceFieldUpdater.newUpdater(TreeNode.class, ChildData.class, "childData");
        childrenUpdater = AtomicReferenceFieldUpdater.newUpdater(TreeNode.class, ConcurrentMap.class, "children");
        defaultThreadFactory = ThreadUtils.newThreadFactory("TreeCache");
    }
    
    public static final class Builder
    {
        private final CuratorFramework client;
        private final String path;
        private boolean cacheData;
        private boolean dataIsCompressed;
        private ExecutorService executorService;
        private int maxDepth;
        private boolean createParentNodes;
        private TreeCacheSelector selector;
        
        private Builder(final CuratorFramework client, final String path) {
            this.cacheData = true;
            this.dataIsCompressed = false;
            this.executorService = null;
            this.maxDepth = Integer.MAX_VALUE;
            this.createParentNodes = false;
            this.selector = new DefaultTreeCacheSelector();
            this.client = Preconditions.checkNotNull(client);
            this.path = PathUtils.validatePath(path);
        }
        
        public TreeCache build() {
            ExecutorService executor = this.executorService;
            if (executor == null) {
                executor = Executors.newSingleThreadExecutor(TreeCache.defaultThreadFactory);
            }
            return new TreeCache(this.client, this.path, this.cacheData, this.dataIsCompressed, this.maxDepth, executor, this.createParentNodes, this.selector);
        }
        
        public Builder setCacheData(final boolean cacheData) {
            this.cacheData = cacheData;
            return this;
        }
        
        public Builder setDataIsCompressed(final boolean dataIsCompressed) {
            this.dataIsCompressed = dataIsCompressed;
            return this;
        }
        
        public Builder setExecutor(final ThreadFactory threadFactory) {
            return this.setExecutor(Executors.newSingleThreadExecutor(threadFactory));
        }
        
        public Builder setExecutor(final ExecutorService executorService) {
            this.executorService = Preconditions.checkNotNull(executorService);
            return this;
        }
        
        public Builder setMaxDepth(final int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }
        
        public Builder setCreateParentNodes(final boolean createParentNodes) {
            this.createParentNodes = createParentNodes;
            return this;
        }
        
        public Builder setSelector(final TreeCacheSelector selector) {
            this.selector = selector;
            return this;
        }
    }
    
    private enum NodeState
    {
        PENDING, 
        LIVE, 
        DEAD;
    }
    
    private final class TreeNode implements Watcher, BackgroundCallback
    {
        volatile NodeState nodeState;
        volatile ChildData childData;
        final TreeNode parent;
        final String path;
        volatile ConcurrentMap<String, TreeNode> children;
        final int depth;
        
        TreeNode(final String path, final TreeNode parent) {
            this.nodeState = NodeState.PENDING;
            this.path = path;
            this.parent = parent;
            this.depth = ((parent == null) ? 0 : (parent.depth + 1));
        }
        
        private void refresh() throws Exception {
            if (this.depth < TreeCache.this.maxDepth && TreeCache.this.selector.traverseChildren(this.path)) {
                TreeCache.this.outstandingOps.addAndGet(2L);
                this.doRefreshData();
                this.doRefreshChildren();
            }
            else {
                this.refreshData();
            }
        }
        
        private void refreshChildren() throws Exception {
            if (this.depth < TreeCache.this.maxDepth && TreeCache.this.selector.traverseChildren(this.path)) {
                TreeCache.this.outstandingOps.incrementAndGet();
                this.doRefreshChildren();
            }
        }
        
        private void refreshData() throws Exception {
            TreeCache.this.outstandingOps.incrementAndGet();
            this.doRefreshData();
        }
        
        private void doRefreshChildren() throws Exception {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)TreeCache.this.client.getChildren()).usingWatcher(this).inBackground(this)).forPath(this.path);
        }
        
        private void doRefreshData() throws Exception {
            if (TreeCache.this.dataIsCompressed) {
                ((ErrorListenerPathable)((Watchable<BackgroundPathable>)TreeCache.this.client.getData().decompressed()).usingWatcher(this).inBackground(this)).forPath(this.path);
            }
            else {
                ((ErrorListenerPathable)((Watchable<BackgroundPathable>)TreeCache.this.client.getData()).usingWatcher(this).inBackground(this)).forPath(this.path);
            }
        }
        
        void wasReconnected() throws Exception {
            this.refresh();
            final ConcurrentMap<String, TreeNode> childMap = this.children;
            if (childMap != null) {
                for (final TreeNode child : childMap.values()) {
                    child.wasReconnected();
                }
            }
        }
        
        void wasCreated() throws Exception {
            this.refresh();
        }
        
        void wasDeleted() throws Exception {
            final ChildData oldChildData = TreeCache.childDataUpdater.getAndSet(this, null);
            TreeCache.this.client.clearWatcherReferences(this);
            final ConcurrentMap<String, TreeNode> childMap = TreeCache.childrenUpdater.getAndSet(this, null);
            if (childMap != null) {
                final ArrayList<TreeNode> childCopy = new ArrayList<TreeNode>(childMap.values());
                childMap.clear();
                for (final TreeNode child : childCopy) {
                    child.wasDeleted();
                }
            }
            if (TreeCache.this.treeState.get() == TreeState.CLOSED) {
                return;
            }
            final NodeState oldState = TreeCache.nodeStateUpdater.getAndSet(this, NodeState.DEAD);
            if (oldState == NodeState.LIVE) {
                TreeCache.this.publishEvent(TreeCacheEvent.Type.NODE_REMOVED, oldChildData);
            }
            if (this.parent == null) {
                ((ErrorListenerPathable)((Watchable<BackgroundPathable>)TreeCache.this.client.checkExists()).usingWatcher(this).inBackground(this)).forPath(this.path);
            }
            else {
                final ConcurrentMap<String, TreeNode> parentChildMap = this.parent.children;
                if (parentChildMap != null) {
                    parentChildMap.remove(ZKPaths.getNodeFromPath(this.path), this);
                }
            }
        }
        
        @Override
        public void process(final WatchedEvent event) {
            TreeCache.LOG.debug("process: {}", event);
            try {
                switch (event.getType()) {
                    case NodeCreated: {
                        Preconditions.checkState(this.parent == null, (Object)"unexpected NodeCreated on non-root node");
                        this.wasCreated();
                        break;
                    }
                    case NodeChildrenChanged: {
                        this.refreshChildren();
                        break;
                    }
                    case NodeDataChanged: {
                        this.refreshData();
                        break;
                    }
                    case NodeDeleted: {
                        this.wasDeleted();
                        break;
                    }
                }
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                TreeCache.this.handleException(e);
            }
        }
        
        @Override
        public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
            TreeCache.LOG.debug("processResult: {}", event);
            final Stat newStat = event.getStat();
            switch (event.getType()) {
                case EXISTS: {
                    Preconditions.checkState(this.parent == null, (Object)"unexpected EXISTS on non-root node");
                    if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                        TreeCache.nodeStateUpdater.compareAndSet(this, NodeState.DEAD, NodeState.PENDING);
                        this.wasCreated();
                        break;
                    }
                    break;
                }
                case CHILDREN: {
                    if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                        final ChildData oldChildData = this.childData;
                        if (oldChildData != null && oldChildData.getStat().getMzxid() == newStat.getMzxid()) {
                            TreeCache.childDataUpdater.compareAndSet(this, oldChildData, new ChildData(oldChildData.getPath(), newStat, oldChildData.getData()));
                        }
                        if (event.getChildren().isEmpty()) {
                            break;
                        }
                        ConcurrentMap<String, TreeNode> childMap = this.children;
                        if (childMap == null) {
                            childMap = Maps.newConcurrentMap();
                            if (!TreeCache.childrenUpdater.compareAndSet(this, null, childMap)) {
                                childMap = this.children;
                            }
                        }
                        final List<String> newChildren = new ArrayList<String>();
                        for (final String child : event.getChildren()) {
                            if (!childMap.containsKey(child) && TreeCache.this.selector.acceptChild(ZKPaths.makePath(this.path, child))) {
                                newChildren.add(child);
                            }
                        }
                        Collections.sort(newChildren);
                        for (final String child : newChildren) {
                            final String fullPath = ZKPaths.makePath(this.path, child);
                            final TreeNode node = new TreeNode(fullPath, this);
                            if (childMap.putIfAbsent(child, node) == null) {
                                node.wasCreated();
                            }
                        }
                        break;
                    }
                    else {
                        if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
                            this.wasDeleted();
                            break;
                        }
                        break;
                    }
                    break;
                }
                case GET_DATA: {
                    if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                        final ChildData toPublish = new ChildData(event.getPath(), newStat, event.getData());
                        ChildData oldChildData2;
                        if (TreeCache.this.cacheData) {
                            oldChildData2 = TreeCache.childDataUpdater.getAndSet(this, toPublish);
                        }
                        else {
                            oldChildData2 = TreeCache.childDataUpdater.getAndSet(this, new ChildData(event.getPath(), newStat, null));
                        }
                        boolean added;
                        if (this.parent == null) {
                            added = (TreeCache.nodeStateUpdater.getAndSet(this, NodeState.LIVE) != NodeState.LIVE);
                        }
                        else {
                            added = TreeCache.nodeStateUpdater.compareAndSet(this, NodeState.PENDING, NodeState.LIVE);
                            if (!added && this.nodeState != NodeState.LIVE) {
                                return;
                            }
                        }
                        if (added) {
                            TreeCache.this.publishEvent(TreeCacheEvent.Type.NODE_ADDED, toPublish);
                        }
                        else if (oldChildData2 == null || oldChildData2.getStat().getMzxid() != newStat.getMzxid()) {
                            TreeCache.this.publishEvent(TreeCacheEvent.Type.NODE_UPDATED, toPublish);
                        }
                        break;
                    }
                    if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
                        this.wasDeleted();
                        break;
                    }
                    break;
                }
                default: {
                    TreeCache.LOG.info(String.format("Unknown event %s", event));
                    TreeCache.this.outstandingOps.decrementAndGet();
                    return;
                }
            }
            if (TreeCache.this.outstandingOps.decrementAndGet() == 0L && TreeCache.this.isInitialized.compareAndSet(false, true)) {
                TreeCache.this.publishEvent(TreeCacheEvent.Type.INITIALIZED);
            }
        }
    }
    
    private enum TreeState
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
