// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.Watcher;

public interface Watchable<T>
{
    T watched();
    
    T usingWatcher(final Watcher p0);
    
    T usingWatcher(final CuratorWatcher p0);
}
