// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.KeeperException;
import java.util.Iterator;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.WatchedEvent;
import java.io.IOException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.drivers.EventTrace;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.curator.utils.ZookeeperFactory;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import org.apache.curator.drivers.TracerDriver;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.ensemble.EnsembleProvider;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import java.io.Closeable;
import org.apache.zookeeper.Watcher;

class ConnectionState implements Watcher, Closeable
{
    private static final int MAX_BACKGROUND_EXCEPTIONS = 10;
    private static final boolean LOG_EVENTS;
    private static final Logger log;
    private final HandleHolder zooKeeper;
    private final AtomicBoolean isConnected;
    private final EnsembleProvider ensembleProvider;
    private final int sessionTimeoutMs;
    private final int connectionTimeoutMs;
    private final AtomicReference<TracerDriver> tracer;
    private final Queue<Exception> backgroundExceptions;
    private final Queue<Watcher> parentWatchers;
    private final AtomicLong instanceIndex;
    private volatile long connectionStartMs;
    @VisibleForTesting
    volatile boolean debugWaitOnExpiredEvent;
    
    ConnectionState(final ZookeeperFactory zookeeperFactory, final EnsembleProvider ensembleProvider, final int sessionTimeoutMs, final int connectionTimeoutMs, final Watcher parentWatcher, final AtomicReference<TracerDriver> tracer, final boolean canBeReadOnly) {
        this.isConnected = new AtomicBoolean(false);
        this.backgroundExceptions = new ConcurrentLinkedQueue<Exception>();
        this.parentWatchers = new ConcurrentLinkedQueue<Watcher>();
        this.instanceIndex = new AtomicLong();
        this.connectionStartMs = 0L;
        this.debugWaitOnExpiredEvent = false;
        this.ensembleProvider = ensembleProvider;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.tracer = tracer;
        if (parentWatcher != null) {
            this.parentWatchers.offer(parentWatcher);
        }
        this.zooKeeper = new HandleHolder(zookeeperFactory, this, ensembleProvider, sessionTimeoutMs, canBeReadOnly);
    }
    
    ZooKeeper getZooKeeper() throws Exception {
        if (SessionFailRetryLoop.sessionForThreadHasFailed()) {
            throw new SessionFailRetryLoop.SessionFailedException();
        }
        final Exception exception = this.backgroundExceptions.poll();
        if (exception != null) {
            new EventTrace("background-exceptions", this.tracer.get()).commit();
            throw exception;
        }
        final boolean localIsConnected = this.isConnected.get();
        if (!localIsConnected) {
            this.checkTimeouts();
        }
        return this.zooKeeper.getZooKeeper();
    }
    
    boolean isConnected() {
        return this.isConnected.get();
    }
    
    void start() throws Exception {
        ConnectionState.log.debug("Starting");
        this.ensembleProvider.start();
        this.reset();
    }
    
    @Override
    public void close() throws IOException {
        ConnectionState.log.debug("Closing");
        CloseableUtils.closeQuietly(this.ensembleProvider);
        try {
            this.zooKeeper.closeAndClear();
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            throw new IOException(e);
        }
        finally {
            this.isConnected.set(false);
        }
    }
    
    void addParentWatcher(final Watcher watcher) {
        this.parentWatchers.offer(watcher);
    }
    
    void removeParentWatcher(final Watcher watcher) {
        this.parentWatchers.remove(watcher);
    }
    
    long getInstanceIndex() {
        return this.instanceIndex.get();
    }
    
    @Override
    public void process(final WatchedEvent event) {
        if (ConnectionState.LOG_EVENTS) {
            ConnectionState.log.debug("ConnectState watcher: " + event);
        }
        final boolean eventTypeNone = event.getType() == Event.EventType.None;
        if (eventTypeNone) {
            final boolean wasConnected = this.isConnected.get();
            final boolean newIsConnected = this.checkState(event.getState(), wasConnected);
            if (newIsConnected != wasConnected) {
                this.isConnected.set(newIsConnected);
                this.connectionStartMs = System.currentTimeMillis();
            }
        }
        if (this.debugWaitOnExpiredEvent && event.getState() == Event.KeeperState.Expired) {
            this.waitOnExpiredEvent();
        }
        for (final Watcher parentWatcher : this.parentWatchers) {
            final OperationTrace trace = new OperationTrace("connection-state-parent-process", this.tracer.get(), this.getSessionId());
            parentWatcher.process(event);
            trace.commit();
        }
        if (eventTypeNone) {
            this.handleState(event.getState());
        }
    }
    
    private void waitOnExpiredEvent() {
        ConnectionState.log.debug("Waiting on Expired event for testing");
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException ex) {}
        ConnectionState.log.debug("Continue processing");
    }
    
    EnsembleProvider getEnsembleProvider() {
        return this.ensembleProvider;
    }
    
    private synchronized void checkTimeouts() throws Exception {
        final int minTimeout = Math.min(this.sessionTimeoutMs, this.connectionTimeoutMs);
        final long elapsed = System.currentTimeMillis() - this.connectionStartMs;
        if (elapsed >= minTimeout) {
            if (this.zooKeeper.hasNewConnectionString()) {
                this.handleNewConnectionString();
            }
            else {
                final int maxTimeout = Math.max(this.sessionTimeoutMs, this.connectionTimeoutMs);
                if (elapsed <= maxTimeout) {
                    final KeeperException.ConnectionLossException connectionLossException = new CuratorConnectionLossException();
                    if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                        ConnectionState.log.error(String.format("Connection timed out for connection string (%s) and timeout (%d) / elapsed (%d)", this.zooKeeper.getConnectionString(), this.connectionTimeoutMs, elapsed), connectionLossException);
                    }
                    new EventTrace("connections-timed-out", this.tracer.get(), this.getSessionId()).commit();
                    throw connectionLossException;
                }
                if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                    ConnectionState.log.warn(String.format("Connection attempt unsuccessful after %d (greater than max timeout of %d). Resetting connection and trying again with a new connection.", elapsed, maxTimeout));
                }
                this.reset();
            }
        }
    }
    
    public long getSessionId() {
        long sessionId = 0L;
        try {
            final ZooKeeper zk = this.zooKeeper.getZooKeeper();
            if (zk != null) {
                sessionId = zk.getSessionId();
            }
        }
        catch (Exception ex) {}
        return sessionId;
    }
    
    private synchronized void reset() throws Exception {
        ConnectionState.log.debug("reset");
        this.instanceIndex.incrementAndGet();
        this.isConnected.set(false);
        this.connectionStartMs = System.currentTimeMillis();
        this.zooKeeper.closeAndReset();
        this.zooKeeper.getZooKeeper();
    }
    
    private boolean checkState(final Event.KeeperState state, final boolean wasConnected) {
        boolean isConnected = wasConnected;
        while (true) {
            switch (state) {
                default: {
                    isConnected = false;
                }
                case SaslAuthenticated: {
                    if (state != Event.KeeperState.Expired) {
                        new EventTrace(state.toString(), this.tracer.get(), this.getSessionId()).commit();
                    }
                    return isConnected;
                }
                case SyncConnected:
                case ConnectedReadOnly: {
                    isConnected = true;
                    continue;
                }
                case AuthFailed: {
                    isConnected = false;
                    ConnectionState.log.error("Authentication failed");
                    continue;
                }
            }
            break;
        }
    }
    
    private void handleState(final Event.KeeperState state) {
        if (state == Event.KeeperState.Expired) {
            this.handleExpiredSession();
        }
        else if (this.zooKeeper.hasNewConnectionString()) {
            this.handleNewConnectionString();
        }
    }
    
    private void handleNewConnectionString() {
        ConnectionState.log.info("Connection string changed");
        new EventTrace("connection-string-changed", this.tracer.get(), this.getSessionId()).commit();
        try {
            this.reset();
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.queueBackgroundException(e);
        }
    }
    
    private void handleExpiredSession() {
        ConnectionState.log.warn("Session expired event received");
        new EventTrace("session-expired", this.tracer.get(), this.getSessionId()).commit();
        try {
            this.reset();
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.queueBackgroundException(e);
        }
    }
    
    private void queueBackgroundException(final Exception e) {
        while (this.backgroundExceptions.size() >= 10) {
            this.backgroundExceptions.poll();
        }
        this.backgroundExceptions.offer(e);
    }
    
    static {
        LOG_EVENTS = Boolean.getBoolean("curator-log-events");
        log = LoggerFactory.getLogger(ConnectionState.class);
    }
}
