// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.api.Watchable;
import java.util.Iterator;
import java.util.Collections;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.BackgroundPathable;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.utils.ZKPaths;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import org.apache.zookeeper.Watcher;
import org.apache.curator.utils.PathUtils;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.EnsureContainers;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class SimpleDistributedQueue
{
    private final Logger log;
    private final CuratorFramework client;
    private final String path;
    private final EnsureContainers ensureContainers;
    private final String PREFIX = "qn-";
    
    public SimpleDistributedQueue(final CuratorFramework client, final String path) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.client = client;
        this.path = PathUtils.validatePath(path);
        this.ensureContainers = new EnsureContainers(client, path);
    }
    
    public byte[] element() throws Exception {
        final byte[] bytes = this.internalElement(false, null);
        if (bytes == null) {
            throw new NoSuchElementException();
        }
        return bytes;
    }
    
    public byte[] remove() throws Exception {
        final byte[] bytes = this.internalElement(true, null);
        if (bytes == null) {
            throw new NoSuchElementException();
        }
        return bytes;
    }
    
    public byte[] take() throws Exception {
        return this.internalPoll(0L, null);
    }
    
    public boolean offer(final byte[] data) throws Exception {
        final String thisPath = ZKPaths.makePath(this.path, "qn-");
        this.client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(thisPath, data);
        return true;
    }
    
    public byte[] peek() throws Exception {
        try {
            return this.element();
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }
    
    public byte[] poll(final long timeout, final TimeUnit unit) throws Exception {
        return this.internalPoll(timeout, unit);
    }
    
    public byte[] poll() throws Exception {
        try {
            return this.remove();
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }
    
    protected void ensurePath() throws Exception {
        this.ensureContainers.ensure();
    }
    
    private byte[] internalPoll(final long timeout, final TimeUnit unit) throws Exception {
        this.ensurePath();
        final long startMs = System.currentTimeMillis();
        final boolean hasTimeout = unit != null;
        final long maxWaitMs = hasTimeout ? TimeUnit.MILLISECONDS.convert(timeout, unit) : Long.MAX_VALUE;
        while (true) {
            final CountDownLatch latch = new CountDownLatch(1);
            final Watcher watcher = new Watcher() {
                @Override
                public void process(final WatchedEvent event) {
                    latch.countDown();
                }
            };
            final byte[] bytes = this.internalElement(true, watcher);
            if (bytes != null) {
                return bytes;
            }
            if (hasTimeout) {
                final long elapsedMs = System.currentTimeMillis() - startMs;
                final long thisWaitMs = maxWaitMs - elapsedMs;
                if (thisWaitMs <= 0L) {
                    return null;
                }
                latch.await(thisWaitMs, TimeUnit.MILLISECONDS);
            }
            else {
                latch.await();
            }
        }
    }
    
    private byte[] internalElement(final boolean removeIt, final Watcher watcher) throws Exception {
        this.ensurePath();
        List<String> nodes;
        try {
            nodes = ((watcher != null) ? ((Watchable<BackgroundPathable<List>>)this.client.getChildren()).usingWatcher(watcher).forPath(this.path) : this.client.getChildren().forPath(this.path));
        }
        catch (KeeperException.NoNodeException dummy) {
            return null;
        }
        Collections.sort(nodes);
        for (final String node : nodes) {
            if (node.startsWith("qn-")) {
                final String thisPath = ZKPaths.makePath(this.path, node);
                try {
                    final byte[] bytes = this.client.getData().forPath(thisPath);
                    if (removeIt) {
                        this.client.delete().forPath(thisPath);
                    }
                    return bytes;
                }
                catch (KeeperException.NoNodeException ex) {
                    continue;
                }
                break;
            }
            this.log.warn("Foreign node in queue path: " + node);
        }
        return null;
    }
}
