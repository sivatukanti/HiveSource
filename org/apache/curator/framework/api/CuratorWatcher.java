// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.WatchedEvent;

public interface CuratorWatcher
{
    void process(final WatchedEvent p0) throws Exception;
}
