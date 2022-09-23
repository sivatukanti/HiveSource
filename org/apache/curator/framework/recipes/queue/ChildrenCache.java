// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.api.Backgroundable;
import org.apache.curator.framework.api.Watchable;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ErrorListenerPathable;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import java.util.List;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorWatcher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import java.io.Closeable;

class ChildrenCache implements Closeable
{
    private final CuratorFramework client;
    private final String path;
    private final AtomicReference<Data> children;
    private final AtomicBoolean isClosed;
    private final CuratorWatcher watcher;
    private final BackgroundCallback callback;
    
    ChildrenCache(final CuratorFramework client, final String path) {
        this.children = new AtomicReference<Data>(new Data((List)Lists.newArrayList(), 0L));
        this.isClosed = new AtomicBoolean(false);
        this.watcher = new CuratorWatcher() {
            @Override
            public void process(final WatchedEvent event) throws Exception {
                if (!ChildrenCache.this.isClosed.get()) {
                    ChildrenCache.this.sync(true);
                }
            }
        };
        this.callback = new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    ChildrenCache.this.setNewChildren(event.getChildren());
                }
            }
        };
        this.client = client;
        this.path = PathUtils.validatePath(path);
    }
    
    void start() throws Exception {
        this.sync(true);
    }
    
    @Override
    public void close() throws IOException {
        this.isClosed.set(true);
        this.notifyFromCallback();
    }
    
    Data getData() {
        return this.children.get();
    }
    
    Data blockingNextGetData(final long startVersion) throws InterruptedException {
        return this.blockingNextGetData(startVersion, 0L, null);
    }
    
    synchronized Data blockingNextGetData(final long startVersion, final long maxWait, final TimeUnit unit) throws InterruptedException {
        final long startMs = System.currentTimeMillis();
        final boolean hasMaxWait = unit != null;
        final long maxWaitMs = hasMaxWait ? unit.toMillis(maxWait) : -1L;
        while (startVersion == this.children.get().version) {
            if (hasMaxWait) {
                final long elapsedMs = System.currentTimeMillis() - startMs;
                final long thisWaitMs = maxWaitMs - elapsedMs;
                if (thisWaitMs <= 0L) {
                    break;
                }
                this.wait(thisWaitMs);
            }
            else {
                this.wait();
            }
        }
        return this.children.get();
    }
    
    private synchronized void notifyFromCallback() {
        this.notifyAll();
    }
    
    private synchronized void sync(final boolean watched) throws Exception {
        if (watched) {
            ((ErrorListenerPathable)((Watchable<BackgroundPathable>)this.client.getChildren()).usingWatcher(this.watcher).inBackground(this.callback)).forPath(this.path);
        }
        else {
            ((Backgroundable<ErrorListenerPathable>)this.client.getChildren()).inBackground(this.callback).forPath(this.path);
        }
    }
    
    private synchronized void setNewChildren(final List<String> newChildren) {
        if (newChildren != null) {
            final Data currentData = this.children.get();
            this.children.set(new Data((List)newChildren, currentData.version + 1L));
            this.notifyFromCallback();
        }
    }
    
    static class Data
    {
        final List<String> children;
        final long version;
        
        private Data(final List<String> children, final long version) {
            this.children = (List<String>)ImmutableList.copyOf((Collection<?>)children);
            this.version = version;
        }
    }
}
