// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import org.apache.curator.framework.api.Watchable;
import com.google.common.base.Function;
import org.apache.curator.shaded.com.google.common.base.Objects;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.GetDataWatchBackgroundStatable;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import java.io.IOException;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.LoggerFactory;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.Exchanger;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.state.ConnectionStateListener;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

public class NodeCache implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final String path;
    private final boolean dataIsCompressed;
    private final AtomicReference<ChildData> data;
    private final AtomicReference<State> state;
    private final ListenerContainer<NodeCacheListener> listeners;
    private final AtomicBoolean isConnected;
    private ConnectionStateListener connectionStateListener;
    private Watcher watcher;
    private final BackgroundCallback backgroundCallback;
    @VisibleForTesting
    volatile Exchanger<Object> rebuildTestExchanger;
    
    public NodeCache(final CuratorFramework client, final String path) {
        this(client, path, false);
    }
    
    public NodeCache(final CuratorFramework client, final String path, final boolean dataIsCompressed) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.data = new AtomicReference<ChildData>(null);
        this.state = new AtomicReference<State>(State.LATENT);
        this.listeners = new ListenerContainer<NodeCacheListener>();
        this.isConnected = new AtomicBoolean(true);
        this.connectionStateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                    if (NodeCache.this.isConnected.compareAndSet(false, true)) {
                        try {
                            NodeCache.this.reset();
                        }
                        catch (Exception e) {
                            ThreadUtils.checkInterrupted(e);
                            NodeCache.this.log.error("Trying to reset after reconnection", e);
                        }
                    }
                }
                else {
                    NodeCache.this.isConnected.set(false);
                }
            }
        };
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                try {
                    NodeCache.this.reset();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    NodeCache.this.handleException(e);
                }
            }
        };
        this.backgroundCallback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                NodeCache.this.processBackgroundResult(event);
            }
        };
        this.client = client;
        this.path = PathUtils.validatePath(path);
        this.dataIsCompressed = dataIsCompressed;
    }
    
    public void start() throws Exception {
        this.start(false);
    }
    
    public void start(final boolean buildInitial) throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.client.getConnectionStateListenable().addListener(this.connectionStateListener);
        if (buildInitial) {
            this.client.checkExists().creatingParentContainersIfNeeded().forPath(this.path);
            this.internalRebuild();
        }
        this.reset();
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            this.listeners.clear();
            this.client.clearWatcherReferences(this.watcher);
            this.client.getConnectionStateListenable().removeListener(this.connectionStateListener);
            this.connectionStateListener = null;
            this.watcher = null;
        }
    }
    
    public ListenerContainer<NodeCacheListener> getListenable() {
        Preconditions.checkState(this.state.get() != State.CLOSED, (Object)"Closed");
        return this.listeners;
    }
    
    public void rebuild() throws Exception {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"Not started");
        this.internalRebuild();
        this.reset();
    }
    
    public ChildData getCurrentData() {
        return this.data.get();
    }
    
    private void reset() throws Exception {
        if (this.state.get() == State.STARTED && this.isConnected.get()) {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.checkExists().creatingParentContainersIfNeeded()).usingWatcher(this.watcher).inBackground(this.backgroundCallback)).forPath(this.path);
        }
    }
    
    private void internalRebuild() throws Exception {
        try {
            final Stat stat = new Stat();
            final byte[] bytes = this.dataIsCompressed ? this.client.getData().decompressed().storingStatIn(stat).forPath(this.path) : this.client.getData().storingStatIn(stat).forPath(this.path);
            this.data.set(new ChildData(this.path, stat, bytes));
        }
        catch (KeeperException.NoNodeException e) {
            this.data.set(null);
        }
    }
    
    private void processBackgroundResult(final CuratorEvent event) throws Exception {
        switch (event.getType()) {
            case GET_DATA: {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    final ChildData childData = new ChildData(this.path, event.getStat(), event.getData());
                    this.setNewData(childData);
                    break;
                }
                break;
            }
            case EXISTS: {
                if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
                    this.setNewData(null);
                    break;
                }
                if (event.getResultCode() != KeeperException.Code.OK.intValue()) {
                    break;
                }
                if (this.dataIsCompressed) {
                    ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData().decompressed()).usingWatcher(this.watcher).inBackground(this.backgroundCallback)).forPath(this.path);
                    break;
                }
                ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(this.watcher).inBackground(this.backgroundCallback)).forPath(this.path);
                break;
            }
        }
    }
    
    private void setNewData(final ChildData newData) throws InterruptedException {
        final ChildData previousData = this.data.getAndSet(newData);
        if (!Objects.equal(previousData, newData)) {
            this.listeners.forEach(new Function<NodeCacheListener, Void>() {
                @Override
                public Void apply(final NodeCacheListener listener) {
                    try {
                        listener.nodeChanged();
                    }
                    catch (Exception e) {
                        ThreadUtils.checkInterrupted(e);
                        NodeCache.this.log.error("Calling listener", e);
                    }
                    return null;
                }
            });
            if (this.rebuildTestExchanger != null) {
                try {
                    this.rebuildTestExchanger.exchange(new Object());
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    protected void handleException(final Throwable e) {
        this.log.error("", e);
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
