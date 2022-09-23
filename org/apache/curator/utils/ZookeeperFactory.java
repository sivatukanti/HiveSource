// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;

public interface ZookeeperFactory
{
    ZooKeeper newZooKeeper(final String p0, final int p1, final Watcher p2, final boolean p3) throws Exception;
}
