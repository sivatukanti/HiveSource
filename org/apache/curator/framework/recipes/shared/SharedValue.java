// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.framework.api.Watchable;
import org.apache.curator.utils.ThreadUtils;
import com.google.common.base.Function;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.curator.framework.api.Pathable;
import java.io.IOException;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.Arrays;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.framework.api.CuratorWatcher;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.ListenerContainer;
import org.slf4j.Logger;
import java.io.Closeable;

public class SharedValue implements Closeable, SharedValueReader
{
    private static final int UNINITIALIZED_VERSION = -1;
    private final Logger log;
    private final ListenerContainer<SharedValueListener> listeners;
    private final CuratorFramework client;
    private final String path;
    private final byte[] seedValue;
    private final AtomicReference<State> state;
    private final AtomicReference<VersionedValue<byte[]>> currentValue;
    private final CuratorWatcher watcher;
    private final ConnectionStateListener connectionStateListener;
    private final BackgroundCallback upadateAndNotifyListenerCallback;
    
    public SharedValue(final CuratorFramework client, final String path, final byte[] seedValue) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.listeners = new ListenerContainer<SharedValueListener>();
        this.state = new AtomicReference<State>(State.LATENT);
        this.watcher = new CuratorWatcher() {
            @Override
            public void process(final WatchedEvent event) throws Exception {
                if (SharedValue.this.state.get() == State.STARTED && event.getType() != Watcher.Event.EventType.None) {
                    SharedValue.this.readValueAndNotifyListenersInBackground();
                }
            }
        };
        this.connectionStateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                SharedValue.this.notifyListenerOfStateChanged(newState);
            }
        };
        this.upadateAndNotifyListenerCallback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    SharedValue.this.updateValue(event.getStat().getVersion(), event.getData());
                    SharedValue.this.notifyListeners();
                }
            }
        };
        this.client = client;
        this.path = PathUtils.validatePath(path);
        this.seedValue = Arrays.copyOf(seedValue, seedValue.length);
        this.currentValue = new AtomicReference<VersionedValue<byte[]>>(new VersionedValue<byte[]>(-1, Arrays.copyOf(seedValue, seedValue.length)));
    }
    
    @Override
    public byte[] getValue() {
        final VersionedValue<byte[]> localCopy = this.currentValue.get();
        return Arrays.copyOf(localCopy.getValue(), localCopy.getValue().length);
    }
    
    @Override
    public VersionedValue<byte[]> getVersionedValue() {
        final VersionedValue<byte[]> localCopy = this.currentValue.get();
        return new VersionedValue<byte[]>(localCopy.getVersion(), Arrays.copyOf(localCopy.getValue(), localCopy.getValue().length));
    }
    
    public void setValue(final byte[] newValue) throws Exception {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"not started");
        final Stat result = this.client.setData().forPath(this.path, newValue);
        this.updateValue(result.getVersion(), Arrays.copyOf(newValue, newValue.length));
    }
    
    @Deprecated
    public boolean trySetValue(final byte[] newValue) throws Exception {
        return this.trySetValue(this.currentValue.get(), newValue);
    }
    
    public boolean trySetValue(final VersionedValue<byte[]> previous, final byte[] newValue) throws Exception {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"not started");
        final VersionedValue<byte[]> current = this.currentValue.get();
        if (previous.getVersion() != current.getVersion() || !Arrays.equals(previous.getValue(), current.getValue())) {
            return false;
        }
        try {
            final Stat result = this.client.setData().withVersion(previous.getVersion()).forPath(this.path, newValue);
            this.updateValue(result.getVersion(), Arrays.copyOf(newValue, newValue.length));
            return true;
        }
        catch (KeeperException.BadVersionException ex) {
            this.readValue();
            return false;
        }
    }
    
    private void updateValue(final int version, final byte[] bytes) {
        while (true) {
            final VersionedValue<byte[]> current = this.currentValue.get();
            if (current.getVersion() >= version) {
                return;
            }
            if (this.currentValue.compareAndSet(current, new VersionedValue<byte[]>(version, bytes))) {
                return;
            }
        }
    }
    
    @Override
    public ListenerContainer<SharedValueListener> getListenable() {
        return this.listeners;
    }
    
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.client.getConnectionStateListenable().addListener(this.connectionStateListener);
        try {
            this.client.create().creatingParentContainersIfNeeded().forPath(this.path, this.seedValue);
        }
        catch (KeeperException.NodeExistsException ex) {}
        this.readValue();
    }
    
    @Override
    public void close() throws IOException {
        this.client.getConnectionStateListenable().removeListener(this.connectionStateListener);
        this.state.set(State.CLOSED);
        this.listeners.clear();
    }
    
    private void readValue() throws Exception {
        final Stat localStat = new Stat();
        final byte[] bytes = this.client.getData().storingStatIn(localStat).usingWatcher(this.watcher).forPath(this.path);
        this.updateValue(localStat.getVersion(), bytes);
    }
    
    private void readValueAndNotifyListenersInBackground() throws Exception {
        ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(this.watcher).inBackground(this.upadateAndNotifyListenerCallback)).forPath(this.path);
    }
    
    private void notifyListeners() {
        final byte[] localValue = this.getValue();
        this.listeners.forEach(new Function<SharedValueListener, Void>() {
            @Override
            public Void apply(final SharedValueListener listener) {
                try {
                    listener.valueHasChanged(SharedValue.this, localValue);
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    SharedValue.this.log.error("From SharedValue listener", e);
                }
                return null;
            }
        });
    }
    
    private void notifyListenerOfStateChanged(final ConnectionState newState) {
        this.listeners.forEach(new Function<SharedValueListener, Void>() {
            @Override
            public Void apply(final SharedValueListener listener) {
                listener.stateChanged(SharedValue.this.client, newState);
                return null;
            }
        });
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
