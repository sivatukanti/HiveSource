// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.curator;

import java.util.Iterator;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.utils.ZKPaths;
import java.util.List;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.PathUtils;
import java.io.IOException;
import org.apache.curator.utils.CloseableUtils;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.curator.utils.CloseableScheduledExecutorService;
import java.util.Collection;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ChildReaper implements Closeable
{
    private final Logger log;
    private final Reaper reaper;
    private final AtomicReference<State> state;
    private final CuratorFramework client;
    private final Collection<String> paths;
    private final Reaper.Mode mode;
    private final CloseableScheduledExecutorService executor;
    private final int reapingThresholdMs;
    private volatile Future<?> task;
    static final int DEFAULT_REAPING_THRESHOLD_MS;
    
    public static <E> Set<E> newConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<E, Boolean>());
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode) {
        this(client, path, mode, newExecutorService(), ChildReaper.DEFAULT_REAPING_THRESHOLD_MS, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final int reapingThresholdMs) {
        this(client, path, mode, newExecutorService(), reapingThresholdMs, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final ScheduledExecutorService executor, final int reapingThresholdMs) {
        this(client, path, mode, executor, reapingThresholdMs, null);
    }
    
    public ChildReaper(final CuratorFramework client, final String path, final Reaper.Mode mode, final ScheduledExecutorService executor, final int reapingThresholdMs, final String leaderPath) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.state = new AtomicReference<State>(State.LATENT);
        this.paths = (Collection<String>)newConcurrentHashSet();
        this.client = client;
        this.mode = mode;
        this.executor = new CloseableScheduledExecutorService(executor);
        this.reapingThresholdMs = reapingThresholdMs;
        this.reaper = new Reaper(client, executor, reapingThresholdMs, leaderPath);
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
        this.reaper.start();
    }
    
    @Override
    public void close() throws IOException {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            CloseableUtils.closeQuietly(this.reaper);
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
    
    private static ScheduledExecutorService newExecutorService() {
        return ThreadUtils.newFixedThreadScheduledPool(2, "ChildReaper");
    }
    
    private void doWork() {
        for (final String path : this.paths) {
            try {
                final List<String> children = this.client.getChildren().forPath(path);
                for (final String name : children) {
                    final String thisPath = ZKPaths.makePath(path, name);
                    final Stat stat = this.client.checkExists().forPath(thisPath);
                    if (stat != null && stat.getNumChildren() == 0) {
                        this.reaper.addPath(thisPath, this.mode);
                    }
                }
            }
            catch (Exception e) {
                this.log.error("Could not get children for path: " + path, e);
            }
        }
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
}
