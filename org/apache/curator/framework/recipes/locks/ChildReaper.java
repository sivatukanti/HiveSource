// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.utils.ZKPaths;
import java.util.List;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.PathUtils;
import java.io.IOException;
import org.apache.curator.utils.CloseableUtils;
import java.util.concurrent.TimeUnit;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.utils.CloseableScheduledExecutorService;
import java.util.Iterator;
import java.util.Collection;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import java.io.Closeable;

@Deprecated
public class ChildReaper implements Closeable
{
    private final Logger log;
    private final Reaper reaper;
    private final AtomicReference<State> state;
    private final CuratorFramework client;
    private final Collection<String> paths;
    private volatile Iterator<String> pathIterator;
    private final Reaper.Mode mode;
    private final CloseableScheduledExecutorService executor;
    private final int reapingThresholdMs;
    private final LeaderLatch leaderLatch;
    private final Set<String> lockSchema;
    private final AtomicInteger maxChildren;
    private volatile Future<?> task;
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode) {
        this(client, path, mode, newExecutorService(), Reaper.DEFAULT_REAPING_THRESHOLD_MS, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final int reapingThresholdMs) {
        this(client, path, mode, newExecutorService(), reapingThresholdMs, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final ScheduledExecutorService executor, final int reapingThresholdMs) {
        this(client, path, mode, executor, reapingThresholdMs, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final ScheduledExecutorService executor, final int reapingThresholdMs, final String leaderPath) {
        this(client, path, mode, executor, reapingThresholdMs, leaderPath, Collections.emptySet());
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final ScheduledExecutorService executor, final int reapingThresholdMs, final String leaderPath, final Set<String> lockSchema) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.state = new AtomicReference<State>(State.LATENT);
        this.paths = (Collection<String>)Sets.newConcurrentHashSet();
        this.pathIterator = null;
        this.maxChildren = new AtomicInteger(-1);
        this.client = client;
        this.mode = mode;
        this.executor = new CloseableScheduledExecutorService(executor);
        this.reapingThresholdMs = reapingThresholdMs;
        if (leaderPath != null) {
            this.leaderLatch = new LeaderLatch(client, leaderPath);
        }
        else {
            this.leaderLatch = null;
        }
        this.reaper = new Reaper(client, executor, reapingThresholdMs, this.leaderLatch);
        this.lockSchema = lockSchema;
        this.addPath(path);
    }
    
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.task = this.executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                ChildReaper.this.doWork();
            }
        }, this.reapingThresholdMs, this.reapingThresholdMs, TimeUnit.MILLISECONDS);
        if (this.leaderLatch != null) {
            this.leaderLatch.start();
        }
        this.reaper.start();
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            CloseableUtils.closeQuietly(this.reaper);
            if (this.leaderLatch != null) {
                CloseableUtils.closeQuietly(this.leaderLatch);
            }
            this.task.cancel(true);
        }
    }
    
    public ChildReaper addPath(final String path) {
        this.paths.add(PathUtils.validatePath(path));
        return this;
    }
    
    public boolean removePath(final String path) {
        return this.paths.remove(PathUtils.validatePath(path));
    }
    
    public void setMaxChildren(final int maxChildren) {
        this.maxChildren.set(maxChildren);
    }
    
    public static ScheduledExecutorService newExecutorService() {
        return ThreadUtils.newFixedThreadScheduledPool(2, "ChildReaper");
    }
    
    @VisibleForTesting
    protected void warnMaxChildren(final String path, final Stat stat) {
        this.log.warn(String.format("Skipping %s as it has too many children: %d", path, stat.getNumChildren()));
    }
    
    private void doWork() {
        if (this.shouldDoWork()) {
            if (this.pathIterator == null || !this.pathIterator.hasNext()) {
                this.pathIterator = this.paths.iterator();
            }
            while (this.pathIterator.hasNext()) {
                final String path = this.pathIterator.next();
                try {
                    final int maxChildren = this.maxChildren.get();
                    if (maxChildren > 0) {
                        final Stat stat = this.client.checkExists().forPath(path);
                        if (stat != null && stat.getNumChildren() > maxChildren) {
                            this.warnMaxChildren(path, stat);
                            continue;
                        }
                    }
                    final List<String> children = this.client.getChildren().forPath(path);
                    this.log.info(String.format("Found %d children for %s", children.size(), path));
                    for (final String name : children) {
                        final String childPath = ZKPaths.makePath(path, name);
                        this.addPathToReaperIfEmpty(childPath);
                        for (final String subNode : this.lockSchema) {
                            this.addPathToReaperIfEmpty(ZKPaths.makePath(childPath, subNode));
                        }
                    }
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.log.error("Could not get children for path: " + path, e);
                }
            }
        }
    }
    
    private void addPathToReaperIfEmpty(final String path) throws Exception {
        final Stat stat = this.client.checkExists().forPath(path);
        if (stat != null && stat.getNumChildren() == 0) {
            this.log.info("Adding " + path);
            this.reaper.addPath(path, this.mode);
        }
    }
    
    private boolean shouldDoWork() {
        return this.leaderLatch == null || this.leaderLatch.hasLeadership();
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
