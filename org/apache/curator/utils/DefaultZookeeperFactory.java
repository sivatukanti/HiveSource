// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;

public class DefaultZookeeperFactory implements ZookeeperFactory
{
    @Override
    public ZooKeeper newZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher, final boolean canBeReadOnly) throws Exception {
        return new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly);
    }
}
