// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

class ZooKeeperTestable implements Testable
{
    private static final Logger LOG;
    private final ZooKeeper zooKeeper;
    private final ClientCnxn clientCnxn;
    
    ZooKeeperTestable(final ZooKeeper zooKeeper, final ClientCnxn clientCnxn) {
        this.zooKeeper = zooKeeper;
        this.clientCnxn = clientCnxn;
    }
    
    @Override
    public void injectSessionExpiration() {
        ZooKeeperTestable.LOG.info("injectSessionExpiration() called");
        this.clientCnxn.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Expired, null));
        this.clientCnxn.eventThread.queueEventOfDeath();
        this.clientCnxn.sendThread.getClientCnxnSocket().wakeupCnxn();
        this.clientCnxn.state = ZooKeeper.States.CLOSED;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperTestable.class);
    }
}
