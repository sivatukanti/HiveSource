// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import java.io.Closeable;
import org.apache.zookeeper.Watcher;

class NamespaceWatcher implements Watcher, Closeable
{
    private volatile CuratorFrameworkImpl client;
    private volatile Watcher actualWatcher;
    private volatile CuratorWatcher curatorWatcher;
    
    NamespaceWatcher(final CuratorFrameworkImpl client, final Watcher actualWatcher) {
        this.client = client;
        this.actualWatcher = actualWatcher;
        this.curatorWatcher = null;
    }
    
    NamespaceWatcher(final CuratorFrameworkImpl client, final CuratorWatcher curatorWatcher) {
        this.client = client;
        this.actualWatcher = null;
        this.curatorWatcher = curatorWatcher;
    }
    
    @Override
    public void close() {
        this.client = null;
        this.actualWatcher = null;
        this.curatorWatcher = null;
    }
    
    @Override
    public void process(final WatchedEvent event) {
        if (this.client != null) {
            if (this.actualWatcher != null) {
                this.actualWatcher.process(new NamespaceWatchedEvent(this.client, event));
            }
            else if (this.curatorWatcher != null) {
                try {
                    this.curatorWatcher.process(new NamespaceWatchedEvent(this.client, event));
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    this.client.logError("Watcher exception", e);
                }
            }
        }
    }
}
