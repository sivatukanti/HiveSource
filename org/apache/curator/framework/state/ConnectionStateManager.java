// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.state;

import com.google.common.base.Function;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import java.io.Closeable;

public class ConnectionStateManager implements Closeable
{
    private static final int QUEUE_SIZE;
    private final Logger log;
    private final BlockingQueue<ConnectionState> eventQueue;
    private final CuratorFramework client;
    private final ListenerContainer<ConnectionStateListener> listeners;
    private final AtomicBoolean initialConnectMessageSent;
    private final ExecutorService service;
    private final AtomicReference<State> state;
    private ConnectionState currentConnectionState;
    
    public ConnectionStateManager(final CuratorFramework client, ThreadFactory threadFactory) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.eventQueue = new ArrayBlockingQueue<ConnectionState>(ConnectionStateManager.QUEUE_SIZE);
        this.listeners = new ListenerContainer<ConnectionStateListener>();
        this.initialConnectMessageSent = new AtomicBoolean(false);
        this.state = new AtomicReference<State>(State.LATENT);
        this.client = client;
        if (threadFactory == null) {
            threadFactory = ThreadUtils.newThreadFactory("ConnectionStateManager");
        }
        this.service = Executors.newSingleThreadExecutor(threadFactory);
    }
    
    public void start() {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.service.submit((Callable<Object>)new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ConnectionStateManager.this.processEvents();
                return null;
            }
        });
    }
    
    @Override
    public void close() {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            this.service.shutdownNow();
            this.listeners.clear();
        }
    }
    
    public ListenerContainer<ConnectionStateListener> getListenable() {
        return this.listeners;
    }
    
    public synchronized boolean setToSuspended() {
        if (this.state.get() != State.STARTED) {
            return false;
        }
        if (this.currentConnectionState == ConnectionState.LOST || this.currentConnectionState == ConnectionState.SUSPENDED) {
            return false;
        }
        this.currentConnectionState = ConnectionState.SUSPENDED;
        this.postState(ConnectionState.SUSPENDED);
        return true;
    }
    
    public synchronized boolean addStateChange(final ConnectionState newConnectionState) {
        if (this.state.get() != State.STARTED) {
            return false;
        }
        final ConnectionState previousState = this.currentConnectionState;
        if (previousState == newConnectionState) {
            return false;
        }
        this.currentConnectionState = newConnectionState;
        ConnectionState localState = newConnectionState;
        final boolean isNegativeMessage = newConnectionState == ConnectionState.LOST || newConnectionState == ConnectionState.SUSPENDED || newConnectionState == ConnectionState.READ_ONLY;
        if (!isNegativeMessage && this.initialConnectMessageSent.compareAndSet(false, true)) {
            localState = ConnectionState.CONNECTED;
        }
        this.postState(localState);
        return true;
    }
    
    public synchronized boolean blockUntilConnected(final int maxWaitTime, final TimeUnit units) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        final boolean hasMaxWait = units != null;
        final long maxWaitTimeMs = hasMaxWait ? TimeUnit.MILLISECONDS.convert(maxWaitTime, units) : 0L;
        while (!this.isConnected()) {
            if (hasMaxWait) {
                final long waitTime = maxWaitTimeMs - (System.currentTimeMillis() - startTime);
                if (waitTime <= 0L) {
                    return this.isConnected();
                }
                this.wait(waitTime);
            }
            else {
                this.wait();
            }
        }
        return this.isConnected();
    }
    
    public synchronized boolean isConnected() {
        return this.currentConnectionState != null && this.currentConnectionState.isConnected();
    }
    
    private void postState(final ConnectionState state) {
        this.log.info("State change: " + state);
        this.notifyAll();
        while (!this.eventQueue.offer(state)) {
            this.eventQueue.poll();
            this.log.warn("ConnectionStateManager queue full - dropping events to make room");
        }
    }
    
    private void processEvents() {
        while (this.state.get() == State.STARTED) {
            try {
                final ConnectionState newState = this.eventQueue.take();
                if (this.listeners.size() == 0) {
                    this.log.warn("There are no ConnectionStateListeners registered.");
                }
                this.listeners.forEach(new Function<ConnectionStateListener, Void>() {
                    @Override
                    public Void apply(final ConnectionStateListener listener) {
                        listener.stateChanged(ConnectionStateManager.this.client, newState);
                        return null;
                    }
                });
            }
            catch (InterruptedException ex) {}
        }
    }
    
    static {
        int size = 25;
        final String property = System.getProperty("ConnectionStateManagerSize", null);
        if (property != null) {
            try {
                size = Integer.parseInt(property);
            }
            catch (NumberFormatException ex) {}
        }
        QUEUE_SIZE = size;
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
