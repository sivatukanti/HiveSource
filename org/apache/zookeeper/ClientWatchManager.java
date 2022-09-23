// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.Set;

public interface ClientWatchManager
{
    Set<Watcher> materialize(final Watcher.Event.KeeperState p0, final Watcher.Event.EventType p1, final String p2);
}
