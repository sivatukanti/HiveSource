// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.barriers;

import org.apache.curator.framework.api.Watchable;
import org.apache.zookeeper.data.Stat;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.shaded.com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathable;
import java.util.concurrent.TimeUnit;
import org.apache.curator.utils.ZKPaths;
import java.util.UUID;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.CuratorFramework;

public class DistributedDoubleBarrier
{
    private final CuratorFramework client;
    private final String barrierPath;
    private final int memberQty;
    private final String ourPath;
    private final String readyPath;
    private final AtomicBoolean hasBeenNotified;
    private final AtomicBoolean connectionLost;
    private final Watcher watcher;
    private static final String READY_NODE = "ready";
    
    public DistributedDoubleBarrier(final CuratorFramework client, final String barrierPath, final int memberQty) {
        this.hasBeenNotified = new AtomicBoolean(false);
        this.connectionLost = new AtomicBoolean(false);
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                DistributedDoubleBarrier.this.connectionLost.set(event.getState() != Event.KeeperState.SyncConnected);
                DistributedDoubleBarrier.this.notifyFromWatcher();
            }
        };
        Preconditions.checkState(memberQty > 0, (Object)"memberQty cannot be 0");
        this.client = client;
        this.barrierPath = PathUtils.validatePath(barrierPath);
        this.memberQty = memberQty;
        this.ourPath = ZKPaths.makePath(barrierPath, UUID.randomUUID().toString());
        this.readyPath = ZKPaths.makePath(barrierPath, "ready");
    }
    
    public void enter() throws Exception {
        this.enter(-1L, null);
    }
    
    public boolean enter(final long maxWait, final TimeUnit unit) throws Exception {
        final long startMs = System.currentTimeMillis();
        final boolean hasMaxWait = unit != null;
        final long maxWaitMs = hasMaxWait ? TimeUnit.MILLISECONDS.convert(maxWait, unit) : Long.MAX_VALUE;
        final boolean readyPathExists = ((Watchable<BackgroundPathable>)this.client.checkExists()).usingWatcher(this.watcher).forPath(this.readyPath) != null;
        this.client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(this.ourPath);
        final boolean result = readyPathExists || this.internalEnter(startMs, hasMaxWait, maxWaitMs);
        if (this.connectionLost.get()) {
            throw new KeeperException.ConnectionLossException();
        }
        return result;
    }
    
    public synchronized void leave() throws Exception {
        this.leave(-1L, null);
    }
    
    public synchronized boolean leave(final long maxWait, final TimeUnit unit) throws Exception {
        final long startMs = System.currentTimeMillis();
        final boolean hasMaxWait = unit != null;
        final long maxWaitMs = hasMaxWait ? TimeUnit.MILLISECONDS.convert(maxWait, unit) : Long.MAX_VALUE;
        return this.internalLeave(startMs, hasMaxWait, maxWaitMs);
    }
    
    @VisibleForTesting
    protected List<String> getChildrenForEntering() throws Exception {
        return this.client.getChildren().forPath(this.barrierPath);
    }
    
    private List<String> filterAndSortChildren(final List<String> children) {
        final Iterable<String> filtered = Iterables.filter(children, new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
                return !name.equals("ready");
            }
        });
        final ArrayList<String> filteredList = Lists.newArrayList((Iterable<? extends String>)filtered);
        Collections.sort(filteredList);
        return filteredList;
    }
    
    private boolean internalLeave(final long startMs, final boolean hasMaxWait, final long maxWaitMs) throws Exception {
        final String ourPathName = ZKPaths.getNodeFromPath(this.ourPath);
        boolean ourNodeShouldExist = true;
        boolean result = true;
        while (!this.connectionLost.get()) {
            List<String> children;
            try {
                children = this.client.getChildren().forPath(this.barrierPath);
            }
            catch (KeeperException.NoNodeException dummy) {
                children = (List<String>)Lists.newArrayList();
            }
            children = this.filterAndSortChildren(children);
            if (children != null) {
                if (children.size() != 0) {
                    final int ourIndex = children.indexOf(ourPathName);
                    if (ourIndex < 0 && ourNodeShouldExist) {
                        if (!this.connectionLost.get()) {
                            throw new IllegalStateException(String.format("Our path (%s) is missing", ourPathName));
                        }
                    }
                    else {
                        if (children.size() != 1) {
                            final boolean IsLowestNode = ourIndex == 0;
                            Stat stat;
                            if (IsLowestNode) {
                                final String highestNodePath = ZKPaths.makePath(this.barrierPath, children.get(children.size() - 1));
                                stat = this.client.checkExists().usingWatcher(this.watcher).forPath(highestNodePath);
                            }
                            else {
                                final String lowestNodePath = ZKPaths.makePath(this.barrierPath, children.get(0));
                                stat = this.client.checkExists().usingWatcher(this.watcher).forPath(lowestNodePath);
                                this.checkDeleteOurPath(ourNodeShouldExist);
                                ourNodeShouldExist = false;
                            }
                            if (stat == null) {
                                continue;
                            }
                            if (hasMaxWait) {
                                final long elapsed = System.currentTimeMillis() - startMs;
                                final long thisWaitMs = maxWaitMs - elapsed;
                                if (thisWaitMs <= 0L) {
                                    result = false;
                                }
                                else {
                                    this.wait(thisWaitMs);
                                }
                            }
                            else {
                                this.wait();
                            }
                            continue;
                        }
                        if (ourNodeShouldExist && !children.get(0).equals(ourPathName)) {
                            throw new IllegalStateException(String.format("Last path (%s) is not ours (%s)", children.get(0), ourPathName));
                        }
                        this.checkDeleteOurPath(ourNodeShouldExist);
                    }
                }
            }
            try {
                this.client.delete().forPath(this.readyPath);
            }
            catch (KeeperException.NoNodeException ex) {}
            return result;
        }
        throw new KeeperException.ConnectionLossException();
    }
    
    private void checkDeleteOurPath(final boolean shouldExist) throws Exception {
        if (shouldExist) {
            this.client.delete().forPath(this.ourPath);
        }
    }
    
    private synchronized boolean internalEnter(final long startMs, final boolean hasMaxWait, final long maxWaitMs) throws Exception {
        boolean result = true;
        final List<String> children = this.getChildrenForEntering();
        final int count = (children != null) ? children.size() : 0;
        if (count >= this.memberQty) {
            try {
                this.client.create().forPath(this.readyPath);
            }
            catch (KeeperException.NodeExistsException ex) {}
        }
        else if (hasMaxWait && !this.hasBeenNotified.get()) {
            final long elapsed = System.currentTimeMillis() - startMs;
            final long thisWaitMs = maxWaitMs - elapsed;
            if (thisWaitMs <= 0L) {
                result = false;
            }
            else {
                this.wait(thisWaitMs);
            }
            if (!this.hasBeenNotified.get()) {
                result = false;
            }
        }
        else {
            this.wait();
        }
        return result;
    }
    
    private synchronized void notifyFromWatcher() {
        this.hasBeenNotified.set(true);
        this.notifyAll();
    }
}
