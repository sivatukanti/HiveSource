// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

class ZooKeeperServerListenerImpl implements ZooKeeperServerListener
{
    private static final Logger LOG;
    private final ZooKeeperServer zkServer;
    
    ZooKeeperServerListenerImpl(final ZooKeeperServer zkServer) {
        this.zkServer = zkServer;
    }
    
    @Override
    public void notifyStopping(final String threadName, final int exitCode) {
        ZooKeeperServerListenerImpl.LOG.info("Thread {} exits, error code {}", threadName, exitCode);
        this.zkServer.setState(ZooKeeperServer.State.ERROR);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperServerListenerImpl.class);
    }
}
