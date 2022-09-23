// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.barriers;

import org.apache.curator.framework.api.Watchable;
import org.apache.curator.framework.api.BackgroundPathable;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.CuratorFramework;

public class DistributedBarrier
{
    private final CuratorFramework client;
    private final String barrierPath;
    private final Watcher watcher;
    
    public DistributedBarrier(final CuratorFramework client, final String barrierPath) {
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                DistributedBarrier.this.notifyFromWatcher();
            }
        };
        this.client = client;
        this.barrierPath = PathUtils.validatePath(barrierPath);
    }
    
    public synchronized void setBarrier() throws Exception {
        try {
            this.client.create().creatingParentContainersIfNeeded().forPath(this.barrierPath);
        }
        catch (KeeperException.NodeExistsException ex) {}
    }
    
    public synchronized void removeBarrier() throws Exception {
        try {
            this.client.delete().forPath(this.barrierPath);
        }
        catch (KeeperException.NoNodeException ex) {}
    }
    
    public synchronized void waitOnBarrier() throws Exception {
        this.waitOnBarrier(-1L, null);
    }
    
    public synchronized boolean waitOnBarrier(final long maxWait, final TimeUnit unit) throws Exception {
        final long startMs = System.currentTimeMillis();
        final boolean hasMaxWait = unit != null;
        final long maxWaitMs = hasMaxWait ? TimeUnit.MILLISECONDS.convert(maxWait, unit) : Long.MAX_VALUE;
        boolean result;
        while (true) {
            result = (((Watchable<BackgroundPathable>)this.client.checkExists()).usingWatcher(this.watcher).forPath(this.barrierPath) == null);
            if (result) {
                break;
            }
            if (hasMaxWait) {
                final long elapsed = System.currentTimeMillis() - startMs;
                final long thisWaitMs = maxWaitMs - elapsed;
                if (thisWaitMs <= 0L) {
                    break;
                }
                this.wait(thisWaitMs);
            }
            else {
                this.wait();
            }
        }
        return result;
    }
    
    private synchronized void notifyFromWatcher() {
        this.notifyAll();
    }
}
