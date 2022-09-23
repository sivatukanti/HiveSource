// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

import org.apache.curator.framework.state.ConnectionState;
import java.util.concurrent.TimeUnit;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.KeeperException;
import java.util.Iterator;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.io.UnsupportedEncodingException;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

public class LeaderSelector implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final LeaderSelectorListener listener;
    private final CloseableExecutorService executorService;
    private final InterProcessMutex mutex;
    private final AtomicReference<State> state;
    private final AtomicBoolean autoRequeue;
    private final AtomicReference<Future<?>> ourTask;
    private volatile boolean hasLeadership;
    private volatile String id;
    @VisibleForTesting
    volatile CountDownLatch debugLeadershipLatch;
    volatile CountDownLatch debugLeadershipWaitLatch;
    private boolean isQueued;
    private static final ThreadFactory defaultThreadFactory;
    
    public LeaderSelector(final CuratorFramework client, final String leaderPath, final LeaderSelectorListener listener) {
        this(client, leaderPath, new CloseableExecutorService(Executors.newSingleThreadExecutor(LeaderSelector.defaultThreadFactory), true), listener);
    }
    
    @Deprecated
    public LeaderSelector(final CuratorFramework client, final String leaderPath, final ThreadFactory threadFactory, final Executor executor, final LeaderSelectorListener listener) {
        this(client, leaderPath, new CloseableExecutorService(wrapExecutor(executor), true), listener);
    }
    
    public LeaderSelector(final CuratorFramework client, final String leaderPath, final ExecutorService executorService, final LeaderSelectorListener listener) {
        this(client, leaderPath, new CloseableExecutorService(executorService), listener);
    }
    
    public LeaderSelector(final CuratorFramework client, final String leaderPath, final CloseableExecutorService executorService, final LeaderSelectorListener listener) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.state = new AtomicReference<State>(State.LATENT);
        this.autoRequeue = new AtomicBoolean(false);
        this.ourTask = new AtomicReference<Future<?>>(null);
        this.id = "";
        this.debugLeadershipLatch = null;
        this.debugLeadershipWaitLatch = null;
        this.isQueued = false;
        Preconditions.checkNotNull(client, (Object)"client cannot be null");
        PathUtils.validatePath(leaderPath);
        Preconditions.checkNotNull(listener, (Object)"listener cannot be null");
        this.client = client;
        this.listener = new WrappedListener(this, listener);
        this.hasLeadership = false;
        this.executorService = executorService;
        this.mutex = new InterProcessMutex(client, leaderPath) {
            @Override
            protected byte[] getLockNodeBytes() {
                return (byte[])((LeaderSelector.this.id.length() > 0) ? LeaderSelector.getIdBytes(LeaderSelector.this.id) : null);
            }
        };
    }
    
    static byte[] getIdBytes(final String id) {
        try {
            return id.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
    
    public void autoRequeue() {
        this.autoRequeue.set(true);
    }
    
    public void setId(final String id) {
        Preconditions.checkNotNull(id, (Object)"id cannot be null");
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void start() {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        Preconditions.checkState(!this.executorService.isShutdown(), (Object)"Already started");
        Preconditions.checkState(!this.hasLeadership, (Object)"Already has leadership");
        this.client.getConnectionStateListenable().addListener(this.listener);
        this.requeue();
    }
    
    public boolean requeue() {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"close() has already been called");
        return this.internalRequeue();
    }
    
    private synchronized boolean internalRequeue() {
        if (!this.isQueued && this.state.get() == State.STARTED) {
            this.isQueued = true;
            final Future<Void> task = this.executorService.submit((Callable<Void>)new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        LeaderSelector.this.doWorkLoop();
                    }
                    finally {
                        LeaderSelector.this.clearIsQueued();
                        if (LeaderSelector.this.autoRequeue.get()) {
                            LeaderSelector.this.internalRequeue();
                        }
                    }
                    return null;
                }
            });
            this.ourTask.set(task);
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized void close() {
        Preconditions.checkState(this.state.compareAndSet(State.STARTED, State.CLOSED), (Object)"Already closed or has not been started");
        this.client.getConnectionStateListenable().removeListener(this.listener);
        this.executorService.close();
        this.ourTask.set(null);
    }
    
    public Collection<Participant> getParticipants() throws Exception {
        final Collection<String> participantNodes = this.mutex.getParticipantNodes();
        return getParticipants(this.client, participantNodes);
    }
    
    static Collection<Participant> getParticipants(final CuratorFramework client, final Collection<String> participantNodes) throws Exception {
        final ImmutableList.Builder<Participant> builder = ImmutableList.builder();
        boolean isLeader = true;
        for (final String path : participantNodes) {
            final Participant participant = participantForPath(client, path, isLeader);
            if (participant != null) {
                builder.add(participant);
                isLeader = false;
            }
        }
        return builder.build();
    }
    
    public Participant getLeader() throws Exception {
        final Collection<String> participantNodes = this.mutex.getParticipantNodes();
        return getLeader(this.client, participantNodes);
    }
    
    static Participant getLeader(final CuratorFramework client, final Collection<String> participantNodes) throws Exception {
        Participant result = null;
        if (participantNodes.size() > 0) {
            final Iterator<String> iter = participantNodes.iterator();
            while (iter.hasNext()) {
                result = participantForPath(client, iter.next(), true);
                if (result != null) {
                    break;
                }
            }
        }
        if (result == null) {
            result = new Participant();
        }
        return result;
    }
    
    public boolean hasLeadership() {
        return this.hasLeadership;
    }
    
    public synchronized void interruptLeadership() {
        final Future<?> task = this.ourTask.get();
        if (task != null) {
            task.cancel(true);
        }
    }
    
    private static Participant participantForPath(final CuratorFramework client, final String path, final boolean markAsLeader) throws Exception {
        try {
            final byte[] bytes = client.getData().forPath(path);
            final String thisId = new String(bytes, "UTF-8");
            return new Participant(thisId, markAsLeader);
        }
        catch (KeeperException.NoNodeException e) {
            return null;
        }
    }
    
    @VisibleForTesting
    void doWork() throws Exception {
        this.hasLeadership = false;
        try {
            this.mutex.acquire();
            this.hasLeadership = true;
            try {
                if (this.debugLeadershipLatch != null) {
                    this.debugLeadershipLatch.countDown();
                }
                if (this.debugLeadershipWaitLatch != null) {
                    this.debugLeadershipWaitLatch.await();
                }
                this.listener.takeLeadership(this.client);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
            catch (Throwable e2) {
                ThreadUtils.checkInterrupted(e2);
            }
            finally {
                this.clearIsQueued();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
        finally {
            if (this.hasLeadership) {
                this.hasLeadership = false;
                try {
                    this.mutex.release();
                }
                catch (Exception e3) {
                    ThreadUtils.checkInterrupted(e3);
                    this.log.error("The leader threw an exception", e3);
                }
            }
        }
    }
    
    private void doWorkLoop() throws Exception {
        KeeperException exception = null;
        try {
            this.doWork();
        }
        catch (KeeperException.ConnectionLossException e) {
            exception = e;
        }
        catch (KeeperException.SessionExpiredException e2) {
            exception = e2;
        }
        catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        if (exception != null && !this.autoRequeue.get()) {
            throw exception;
        }
    }
    
    private synchronized void clearIsQueued() {
        this.isQueued = false;
    }
    
    private static ExecutorService wrapExecutor(final Executor executor) {
        return new AbstractExecutorService() {
            private volatile boolean isShutdown = false;
            private volatile boolean isTerminated = false;
            
            @Override
            public void shutdown() {
                this.isShutdown = true;
            }
            
            @Override
            public List<Runnable> shutdownNow() {
                return (List<Runnable>)Lists.newArrayList();
            }
            
            @Override
            public boolean isShutdown() {
                return this.isShutdown;
            }
            
            @Override
            public boolean isTerminated() {
                return this.isTerminated;
            }
            
            @Override
            public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void execute(final Runnable command) {
                try {
                    executor.execute(command);
                }
                finally {
                    this.isShutdown = true;
                    this.isTerminated = true;
                }
            }
        };
    }
    
    static {
        defaultThreadFactory = ThreadUtils.newThreadFactory("LeaderSelector");
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
    
    private static class WrappedListener implements LeaderSelectorListener
    {
        private final LeaderSelector leaderSelector;
        private final LeaderSelectorListener listener;
        
        public WrappedListener(final LeaderSelector leaderSelector, final LeaderSelectorListener listener) {
            this.leaderSelector = leaderSelector;
            this.listener = listener;
        }
        
        @Override
        public void takeLeadership(final CuratorFramework client) throws Exception {
            this.listener.takeLeadership(client);
        }
        
        @Override
        public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
            try {
                this.listener.stateChanged(client, newState);
            }
            catch (CancelLeadershipException dummy) {
                this.leaderSelector.interruptLeadership();
            }
        }
    }
}
