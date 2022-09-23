// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.zookeeper.WatchedEvent;

class NamespaceWatchedEvent extends WatchedEvent
{
    NamespaceWatchedEvent(final CuratorFrameworkImpl client, final WatchedEvent event) {
        super(event.getType(), event.getState(), client.unfixForNamespace(event.getPath()));
    }
}
