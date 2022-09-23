// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.nodes;

import org.apache.curator.framework.api.Watchable;
import org.apache.curator.framework.api.Backgroundable;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.api.BackgroundPathable;
import java.io.IOException;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.api.ErrorListenerPathAndBytesable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import java.util.Arrays;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.BackgroundCallback;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.io.Closeable;

public class PersistentNode implements Closeable
{
    private final AtomicReference<CountDownLatch> initialCreateLatch;
    private final Logger log;
    private final CuratorFramework client;
    private final AtomicReference<String> nodePath;
    private final String basePath;
    private final CreateMode mode;
    private final AtomicReference<byte[]> data;
    private final AtomicReference<State> state;
    private final AtomicBoolean authFailure;
    private final BackgroundCallback backgroundCallback;
    private final boolean useProtection;
    private final AtomicReference<CreateModable<ACLBackgroundPathAndBytesable<String>>> createMethod;
    private final CuratorWatcher watcher;
    private final BackgroundCallback checkExistsCallback;
    private final BackgroundCallback setDataCallback;
    private final ConnectionStateListener connectionStateListener;
    
    public PersistentNode(final CuratorFramework client, final CreateMode mode, final boolean useProtection, final String basePath, final byte[] initData) {
        this.initialCreateLatch = new AtomicReference<CountDownLatch>(new CountDownLatch(1));
        this.log = LoggerFactory.getLogger(this.getClass());
        this.nodePath = new AtomicReference<String>(null);
        this.data = new AtomicReference<byte[]>();
        this.state = new AtomicReference<State>(State.LATENT);
        this.authFailure = new AtomicBoolean(false);
        this.createMethod = new AtomicReference<CreateModable<ACLBackgroundPathAndBytesable<String>>>(null);
        this.watcher = new CuratorWatcher() {
            @Override
            public void process(final WatchedEvent event) throws Exception {
                if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                    PersistentNode.this.createNode();
                }
                else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    PersistentNode.this.watchNode();
                }
            }
        };
        this.checkExistsCallback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
                    PersistentNode.this.createNode();
                }
                else {
                    final boolean isEphemeral = event.getStat().getEphemeralOwner() != 0L;
                    if (isEphemeral != PersistentNode.this.mode.isEphemeral()) {
                        PersistentNode.this.log.warn("Existing node ephemeral state doesn't match requested state. Maybe the node was created outside of PersistentNode? " + PersistentNode.this.basePath);
                    }
                }
            }
        };
        this.setDataCallback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    PersistentNode.this.initialisationComplete();
                }
            }
        };
        this.connectionStateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                if (newState == ConnectionState.RECONNECTED) {
                    PersistentNode.this.createNode();
                }
            }
        };
        this.useProtection = useProtection;
        this.client = Preconditions.checkNotNull(client, (Object)"client cannot be null");
        this.basePath = PathUtils.validatePath(basePath);
        this.mode = Preconditions.checkNotNull(mode, (Object)"mode cannot be null");
        final byte[] data = Preconditions.checkNotNull(initData, (Object)"data cannot be null");
        this.backgroundCallback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (PersistentNode.this.state.get() == State.STARTED) {
                    PersistentNode.this.processBackgroundCallback(event);
                }
                else {
                    PersistentNode.this.processBackgroundCallbackClosedState(event);
                }
            }
        };
        this.data.set(Arrays.copyOf(data, data.length));
    }
    
    private void processBackgroundCallbackClosedState(final CuratorEvent event) {
        String path = null;
        if (event.getResultCode() == KeeperException.Code.NODEEXISTS.intValue()) {
            path = event.getPath();
        }
        else if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
            path = event.getName();
        }
        if (path != null) {
            try {
                ((Backgroundable<ErrorListenerPathable>)this.client.delete().guaranteed()).inBackground().forPath(path);
            }
            catch (Exception e) {
                this.log.error("Could not delete node after close", e);
            }
        }
    }
    
    private void processBackgroundCallback(final CuratorEvent event) throws Exception {
        String path = null;
        boolean nodeExists = false;
        if (event.getResultCode() == KeeperException.Code.NODEEXISTS.intValue()) {
            path = event.getPath();
            nodeExists = true;
        }
        else if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
            path = event.getName();
        }
        else if (event.getResultCode() == KeeperException.Code.NOAUTH.intValue()) {
            this.log.warn("Client does not have authorisation to write node at path {}", event.getPath());
            this.authFailure.set(true);
            return;
        }
        if (path != null) {
            this.authFailure.set(false);
            this.nodePath.set(path);
            this.watchNode();
            if (nodeExists) {
                ((Backgroundable<ErrorListenerPathAndBytesable>)this.client.setData()).inBackground(this.setDataCallback).forPath(this.getActualPath(), this.getData());
            }
            else {
                this.initialisationComplete();
            }
        }
        else {
            this.createNode();
        }
    }
    
    private void initialisationComplete() {
        final CountDownLatch localLatch = this.initialCreateLatch.getAndSet(null);
        if (localLatch != null) {
            localLatch.countDown();
        }
    }
    
    public void start() {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Already started");
        this.client.getConnectionStateListenable().addListener(this.connectionStateListener);
        this.createNode();
    }
    
    public boolean waitForInitialCreate(final long timeout, final TimeUnit unit) throws InterruptedException {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"Not started");
        final CountDownLatch localLatch = this.initialCreateLatch.get();
        return localLatch == null || localLatch.await(timeout, unit);
    }
    
    @Override
    public void close() throws IOException {
        if (!this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            return;
        }
        this.client.getConnectionStateListenable().removeListener(this.connectionStateListener);
        try {
            this.deleteNode();
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            throw new IOException(e);
        }
    }
    
    public String getActualPath() {
        return this.nodePath.get();
    }
    
    public void setData(byte[] data) throws Exception {
        data = Preconditions.checkNotNull(data, (Object)"data cannot be null");
        Preconditions.checkState(this.nodePath.get() != null, (Object)"initial create has not been processed. Call waitForInitialCreate() to ensure.");
        this.data.set(Arrays.copyOf(data, data.length));
        if (this.isActive()) {
            ((Backgroundable<ErrorListenerPathAndBytesable>)this.client.setData()).inBackground().forPath(this.getActualPath(), this.getData());
        }
    }
    
    public byte[] getData() {
        return this.data.get();
    }
    
    private void deleteNode() throws Exception {
        final String localNodePath = this.nodePath.getAndSet(null);
        if (localNodePath != null) {
            try {
                this.client.delete().guaranteed().forPath(localNodePath);
            }
            catch (KeeperException.NoNodeException ex) {}
        }
    }
    
    private void createNode() {
        if (!this.isActive()) {
            return;
        }
        try {
            final String existingPath = this.nodePath.get();
            final String createPath = (existingPath != null && !this.useProtection) ? existingPath : this.basePath;
            CreateModable<ACLBackgroundPathAndBytesable<String>> localCreateMethod = this.createMethod.get();
            if (localCreateMethod == null) {
                final CreateModable<ACLBackgroundPathAndBytesable<String>> tempCreateMethod = (CreateModable<ACLBackgroundPathAndBytesable<String>>)(this.useProtection ? this.client.create().creatingParentContainersIfNeeded().withProtection() : this.client.create().creatingParentContainersIfNeeded());
                if (this.createMethod.compareAndSet(null, tempCreateMethod)) {
                    localCreateMethod = tempCreateMethod;
                }
            }
            localCreateMethod.withMode(this.getCreateMode(existingPath != null)).inBackground(this.backgroundCallback).forPath(createPath, this.data.get());
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            throw new RuntimeException("Creating node. BasePath: " + this.basePath, e);
        }
    }
    
    private CreateMode getCreateMode(final boolean pathIsSet) {
        if (pathIsSet) {
            switch (this.mode) {
                case EPHEMERAL_SEQUENTIAL: {
                    return CreateMode.EPHEMERAL;
                }
                case PERSISTENT_SEQUENTIAL: {
                    return CreateMode.PERSISTENT;
                }
            }
        }
        return this.mode;
    }
    
    private void watchNode() throws Exception {
        if (!this.isActive()) {
            return;
        }
        final String localNodePath = this.nodePath.get();
        if (localNodePath != null) {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.checkExists()).usingWatcher(this.watcher).inBackground(this.checkExistsCallback)).forPath(localNodePath);
        }
    }
    
    private boolean isActive() {
        return this.state.get() == State.STARTED;
    }
    
    @VisibleForTesting
    boolean isAuthFailure() {
        return this.authFailure.get();
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
