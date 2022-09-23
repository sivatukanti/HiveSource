// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;

class Watching
{
    private final Watcher watcher;
    private final boolean watched;
    
    Watching(final boolean watched) {
        this.watcher = null;
        this.watched = watched;
    }
    
    Watching(final CuratorFrameworkImpl client, final Watcher watcher) {
        this.watcher = ((watcher != null) ? client.getNamespaceWatcherMap().getNamespaceWatcher(watcher) : null);
        this.watched = false;
    }
    
    Watching(final CuratorFrameworkImpl client, final CuratorWatcher watcher) {
        this.watcher = ((watcher != null) ? client.getNamespaceWatcherMap().getNamespaceWatcher(watcher) : null);
        this.watched = false;
    }
    
    Watching() {
        this.watcher = null;
        this.watched = false;
    }
    
    Watcher getWatcher() {
        return this.watcher;
    }
    
    boolean isWatched() {
        return this.watched;
    }
}
