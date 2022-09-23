// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.util.Iterator;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.io.IOException;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;
import org.apache.curator.utils.CloseableScheduledExecutorService;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

@Deprecated
public class Reaper implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final CloseableScheduledExecutorService executor;
    private final int reapingThresholdMs;
    private final Map<String, PathHolder> activePaths;
    private final AtomicReference<State> state;
    private final LeaderLatch leaderLatch;
    private final AtomicBoolean reapingIsActive;
    private final boolean ownsLeaderLatch;
    static final int DEFAULT_REAPING_THRESHOLD_MS;
    @VisibleForTesting
    static final int EMPTY_COUNT_THRESHOLD = 3;
    
    public Reaper(final CuratorFramework client) {
        this(client, newExecutorService(), Reaper.DEFAULT_REAPING_THRESHOLD_MS, (String)null);
    }
    
    public Reaper(final CuratorFramework client, final int reapingThresholdMs) {
        this(client, newExecutorService(), reapingThresholdMs, (String)null);
    }
    
    public Reaper(final CuratorFramework client, final ScheduledExecutorService executor, final int reapingThresholdMs) {
        this(client, executor, reapingThresholdMs, (String)null);
    }
    
    public Reaper(final CuratorFramework client, final ScheduledExecutorService executor, final int reapingThresholdMs, final String leaderPath) {
        this(client, executor, reapingThresholdMs, makeLeaderLatchIfPathNotNull(client, leaderPath), true);
    }
    
    public Reaper(final CuratorFramework client, final ScheduledExecutorService executor, final int reapingThresholdMs, final LeaderLatch leaderLatch) {
        this(client, executor, reapingThresholdMs, leaderLatch, false);
    }
    
    private Reaper(final CuratorFramework client, final ScheduledExecutorService executor, final int reapingThresholdMs, final LeaderLatch leaderLatch, final boolean ownsLeaderLatch) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.activePaths = (Map<String, PathHolder>)Maps.newConcurrentMap();
        this.state = new AtomicReference<State>(State.LATENT);
        this.reapingIsActive = new AtomicBoolean(true);
        this.client = client;
        this.executor = new CloseableScheduledExecutorService(executor);
        this.reapingThresholdMs = reapingThresholdMs / 3;
        this.leaderLatch = leaderLatch;
        if (leaderLatch != null) {
            this.addListenerToLeaderLatch(leaderLatch);
        }
        this.ownsLeaderLatch = ownsLeaderLatch;
    }
    
    public void addPath(final String path) {
        this.addPath(path, Mode.REAP_INDEFINITELY);
    }
    
    public void addPath(final String path, final Mode mode) {
        final PathHolder pathHolder = new PathHolder(path, mode, 0);
        this.activePaths.put(path, pathHolder);
        this.schedule(pathHolder, this.reapingThresholdMs);
    }
    
    public boolean removePath(final String path) {
        return this.activePaths.remove(path) != null;
    }
    
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        if (this.leaderLatch != null && this.ownsLeaderLatch) {
            this.leaderLatch.start();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            this.executor.close();
            if (this.leaderLatch != null && this.ownsLeaderLatch) {
                this.leaderLatch.close();
            }
        }
    }
    
    @VisibleForTesting
    protected Future<?> schedule(final PathHolder pathHolder, final int reapingThresholdMs) {
        if (this.reapingIsActive.get()) {
            return this.executor.schedule(pathHolder, reapingThresholdMs, TimeUnit.MILLISECONDS);
        }
        return null;
    }
    
    @VisibleForTesting
    protected void reap(final PathHolder holder) {
        if (!this.activePaths.containsKey(holder.path)) {
            return;
        }
        boolean addBack = true;
        int newEmptyCount = 0;
        try {
            final Stat stat = this.client.checkExists().forPath(holder.path);
            if (stat != null) {
                if (stat.getNumChildren() == 0) {
                    if (holder.emptyCount + 1 >= 3) {
                        try {
                            this.client.delete().forPath(holder.path);
                            this.log.info("Reaping path: " + holder.path);
                            if (holder.mode == Mode.REAP_UNTIL_DELETE || holder.mode == Mode.REAP_UNTIL_GONE) {
                                addBack = false;
                            }
                        }
                        catch (KeeperException.NoNodeException ignore) {
                            if (holder.mode == Mode.REAP_UNTIL_GONE) {
                                addBack = false;
                            }
                        }
                        catch (KeeperException.NotEmptyException ex) {}
                    }
                    else {
                        newEmptyCount = holder.emptyCount + 1;
                    }
                }
            }
            else if (holder.mode == Mode.REAP_UNTIL_GONE) {
                addBack = false;
            }
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.log.error("Trying to reap: " + holder.path, e);
        }
        if (!addBack) {
            this.activePaths.remove(holder.path);
        }
        else if (!Thread.currentThread().isInterrupted() && this.state.get() == State.STARTED && this.activePaths.containsKey(holder.path)) {
            this.activePaths.put(holder.path, holder);
            this.schedule(new PathHolder(holder.path, holder.mode, newEmptyCount), this.reapingThresholdMs);
        }
    }
    
    public static ScheduledExecutorService newExecutorService() {
        return ThreadUtils.newSingleThreadScheduledExecutor("Reaper");
    }
    
    private void addListenerToLeaderLatch(final LeaderLatch leaderLatch) {
        final LeaderLatchListener listener = new LeaderLatchListener() {
            @Override
            public void isLeader() {
                Reaper.this.reapingIsActive.set(true);
                for (final PathHolder holder : Reaper.this.activePaths.values()) {
                    Reaper.this.schedule(holder, Reaper.this.reapingThresholdMs);
                }
            }
            
            @Override
            public void notLeader() {
                Reaper.this.reapingIsActive.set(false);
            }
        };
        leaderLatch.addListener(listener);
        this.reapingIsActive.set(leaderLatch.hasLeadership());
    }
    
    private static LeaderLatch makeLeaderLatchIfPathNotNull(final CuratorFramework client, final String leaderPath) {
        if (leaderPath == null) {
            return null;
        }
        return new LeaderLatch(client, leaderPath);
    }
    
    static {
        DEFAULT_REAPING_THRESHOLD_MS = (int)TimeUnit.MILLISECONDS.convert(5L, TimeUnit.MINUTES);
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
    
    @VisibleForTesting
    class PathHolder implements Runnable
    {
        final String path;
        final Mode mode;
        final int emptyCount;
        
        @Override
        public void run() {
            Reaper.this.reap(this);
        }
        
        private PathHolder(final String path, final Mode mode, final int emptyCount) {
            this.path = path;
            this.mode = mode;
            this.emptyCount = emptyCount;
        }
    }
    
    public enum Mode
    {
        REAP_INDEFINITELY, 
        REAP_UNTIL_DELETE, 
        REAP_UNTIL_GONE;
    }
}
