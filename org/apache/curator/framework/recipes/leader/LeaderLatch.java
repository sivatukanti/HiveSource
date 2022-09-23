// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

import org.apache.curator.framework.api.Backgroundable;
import org.apache.curator.framework.api.Watchable;
import org.apache.curator.framework.recipes.locks.StandardLockInternalsDriver;
import com.google.common.base.Function;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import java.util.List;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.ErrorListenerPathAndBytesable;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.recipes.locks.LockInternals;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.io.EOFException;
import java.util.concurrent.Executor;
import org.apache.curator.utils.ThreadUtils;
import java.io.IOException;
import org.apache.curator.framework.recipes.AfterConnectionEstablished;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.LoggerFactory;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.recipes.locks.LockInternalsSorter;
import org.apache.curator.framework.state.ConnectionStateListener;
import java.util.concurrent.Future;
import org.apache.curator.framework.listen.ListenerContainer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

public class LeaderLatch implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final String latchPath;
    private final String id;
    private final AtomicReference<State> state;
    private final AtomicBoolean hasLeadership;
    private final AtomicReference<String> ourPath;
    private final ListenerContainer<LeaderLatchListener> listeners;
    private final CloseMode closeMode;
    private final AtomicReference<Future<?>> startTask;
    private final ConnectionStateListener listener;
    private static final String LOCK_NAME = "latch-";
    private static final LockInternalsSorter sorter;
    @VisibleForTesting
    volatile CountDownLatch debugResetWaitLatch;
    
    public LeaderLatch(final CuratorFramework client, final String latchPath) {
        this(client, latchPath, "", CloseMode.SILENT);
    }
    
    public LeaderLatch(final CuratorFramework client, final String latchPath, final String id) {
        this(client, latchPath, id, CloseMode.SILENT);
    }
    
    public LeaderLatch(final CuratorFramework client, final String latchPath, final String id, final CloseMode closeMode) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.state = new AtomicReference<State>(State.LATENT);
        this.hasLeadership = new AtomicBoolean(false);
        this.ourPath = new AtomicReference<String>();
        this.listeners = new ListenerContainer<LeaderLatchListener>();
        this.startTask = new AtomicReference<Future<?>>();
        this.listener = new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                LeaderLatch.this.handleStateChange(newState);
            }
        };
        this.debugResetWaitLatch = null;
        this.client = Preconditions.checkNotNull(client, (Object)"client cannot be null");
        this.latchPath = PathUtils.validatePath(latchPath);
        this.id = Preconditions.checkNotNull(id, (Object)"id cannot be null");
        this.closeMode = Preconditions.checkNotNull(closeMode, (Object)"closeMode cannot be null");
    }
    
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.startTask.set(AfterConnectionEstablished.execute(this.client, new Runnable() {
            @Override
            public void run() {
                try {
                    LeaderLatch.this.internalStart();
                }
                finally {
                    LeaderLatch.this.startTask.set(null);
                }
            }
        }));
    }
    
    @Override
    public void close() throws IOException {
        this.close(this.closeMode);
    }
    
    public synchronized void close(final CloseMode closeMode) throws IOException {
        Preconditions.checkState(this.state.compareAndSet(State.STARTED, State.CLOSED), (Object)"Already closed or has not been started");
        Preconditions.checkNotNull(closeMode, (Object)"closeMode cannot be null");
        this.cancelStartTask();
        try {
            this.setNode(null);
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            throw new IOException(e);
        }
        finally {
            this.client.getConnectionStateListenable().removeListener(this.listener);
            switch (closeMode) {
                case NOTIFY_LEADER: {
                    this.setLeadership(false);
                    this.listeners.clear();
                    break;
                }
                default: {
                    this.listeners.clear();
                    this.setLeadership(false);
                    break;
                }
            }
        }
    }
    
    @VisibleForTesting
    protected boolean cancelStartTask() {
        final Future<?> localStartTask = this.startTask.getAndSet(null);
        if (localStartTask != null) {
            localStartTask.cancel(true);
            return true;
        }
        return false;
    }
    
    public void addListener(final LeaderLatchListener listener) {
        this.listeners.addListener(listener);
    }
    
    public void addListener(final LeaderLatchListener listener, final Executor executor) {
        this.listeners.addListener(listener, executor);
    }
    
    public void removeListener(final LeaderLatchListener listener) {
        this.listeners.removeListener(listener);
    }
    
    public void await() throws InterruptedException, EOFException {
        synchronized (this) {
            while (this.state.get() == State.STARTED && !this.hasLeadership.get()) {
                this.wait();
            }
        }
        if (this.state.get() != State.STARTED) {
            throw new EOFException();
        }
    }
    
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        long waitNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
        synchronized (this) {
            while (waitNanos > 0L && this.state.get() == State.STARTED && !this.hasLeadership.get()) {
                final long startNanos = System.nanoTime();
                TimeUnit.NANOSECONDS.timedWait(this, waitNanos);
                final long elapsed = System.nanoTime() - startNanos;
                waitNanos -= elapsed;
            }
        }
        return this.hasLeadership();
    }
    
    public String getId() {
        return this.id;
    }
    
    public State getState() {
        return this.state.get();
    }
    
    public Collection<Participant> getParticipants() throws Exception {
        final Collection<String> participantNodes = LockInternals.getParticipantNodes(this.client, this.latchPath, "latch-", LeaderLatch.sorter);
        return LeaderSelector.getParticipants(this.client, participantNodes);
    }
    
    public Participant getLeader() throws Exception {
        final Collection<String> participantNodes = LockInternals.getParticipantNodes(this.client, this.latchPath, "latch-", LeaderLatch.sorter);
        return LeaderSelector.getLeader(this.client, participantNodes);
    }
    
    public boolean hasLeadership() {
        return this.state.get() == State.STARTED && this.hasLeadership.get();
    }
    
    @VisibleForTesting
    void reset() throws Exception {
        this.setLeadership(false);
        this.setNode(null);
        final BackgroundCallback callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (LeaderLatch.this.debugResetWaitLatch != null) {
                    LeaderLatch.this.debugResetWaitLatch.await();
                    LeaderLatch.this.debugResetWaitLatch = null;
                }
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    LeaderLatch.this.setNode(event.getName());
                    if (LeaderLatch.this.state.get() == State.CLOSED) {
                        LeaderLatch.this.setNode(null);
                    }
                    else {
                        LeaderLatch.this.getChildren();
                    }
                }
                else {
                    LeaderLatch.this.log.error("getChildren() failed. rc = " + event.getResultCode());
                }
            }
        };
        ((ErrorListenerPathAndBytesable)this.client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).inBackground(callback)).forPath(ZKPaths.makePath(this.latchPath, "latch-"), LeaderSelector.getIdBytes(this.id));
    }
    
    private synchronized void internalStart() {
        if (this.state.get() == State.STARTED) {
            this.client.getConnectionStateListenable().addListener(this.listener);
            try {
                this.reset();
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                this.log.error("An error occurred checking resetting leadership.", e);
            }
        }
    }
    
    private void checkLeadership(final List<String> children) throws Exception {
        final String localOurPath = this.ourPath.get();
        final List<String> sortedChildren = LockInternals.getSortedChildren("latch-", LeaderLatch.sorter, children);
        final int ourIndex = (localOurPath != null) ? sortedChildren.indexOf(ZKPaths.getNodeFromPath(localOurPath)) : -1;
        if (ourIndex < 0) {
            this.log.error("Can't find our node. Resetting. Index: " + ourIndex);
            this.reset();
        }
        else if (ourIndex == 0) {
            this.setLeadership(true);
        }
        else {
            final String watchPath = sortedChildren.get(ourIndex - 1);
            final Watcher watcher = new Watcher() {
                @Override
                public void process(final WatchedEvent event) {
                    if (LeaderLatch.this.state.get() == State.STARTED && event.getType() == Event.EventType.NodeDeleted && localOurPath != null) {
                        try {
                            LeaderLatch.this.getChildren();
                        }
                        catch (Exception ex) {
                            ThreadUtils.checkInterrupted(ex);
                            LeaderLatch.this.log.error("An error occurred checking the leadership.", ex);
                        }
                    }
                }
            };
            final BackgroundCallback callback = new BackgroundCallback() {
                @Override
                public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                    if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
                        LeaderLatch.this.reset();
                    }
                }
            };
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(watcher).inBackground(callback)).forPath(ZKPaths.makePath(this.latchPath, watchPath));
        }
    }
    
    private void getChildren() throws Exception {
        final BackgroundCallback callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    LeaderLatch.this.checkLeadership(event.getChildren());
                }
            }
        };
        ((Backgroundable<ErrorListenerPathable>)this.client.getChildren()).inBackground(callback).forPath(ZKPaths.makePath(this.latchPath, null));
    }
    
    private void handleStateChange(final ConnectionState newState) {
        switch (newState) {
            case RECONNECTED: {
                try {
                    this.reset();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.log.error("Could not reset leader latch", e);
                    this.setLeadership(false);
                }
                break;
            }
            case SUSPENDED:
            case LOST: {
                this.setLeadership(false);
                break;
            }
        }
    }
    
    private synchronized void setLeadership(final boolean newValue) {
        final boolean oldValue = this.hasLeadership.getAndSet(newValue);
        if (oldValue && !newValue) {
            this.listeners.forEach(new Function<LeaderLatchListener, Void>() {
                @Override
                public Void apply(final LeaderLatchListener listener) {
                    listener.notLeader();
                    return null;
                }
            });
        }
        else if (!oldValue && newValue) {
            this.listeners.forEach(new Function<LeaderLatchListener, Void>() {
                @Override
                public Void apply(final LeaderLatchListener input) {
                    input.isLeader();
                    return null;
                }
            });
        }
        this.notifyAll();
    }
    
    private void setNode(final String newValue) throws Exception {
        final String oldPath = this.ourPath.getAndSet(newValue);
        if (oldPath != null) {
            ((Backgroundable<ErrorListenerPathable>)this.client.delete().guaranteed()).inBackground().forPath(oldPath);
        }
    }
    
    static {
        sorter = new LockInternalsSorter() {
            @Override
            public String fixForSorting(final String str, final String lockName) {
                return StandardLockInternalsDriver.standardFixForSorting(str, lockName);
            }
        };
    }
    
    public enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
    
    public enum CloseMode
    {
        SILENT, 
        NOTIFY_LEADER;
    }
}
