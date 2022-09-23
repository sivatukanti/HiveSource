// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.util.concurrent.CountDownLatch;

class ZooKeeperServerShutdownHandler
{
    private final CountDownLatch shutdownLatch;
    
    ZooKeeperServerShutdownHandler(final CountDownLatch shutdownLatch) {
        this.shutdownLatch = shutdownLatch;
    }
    
    void handle(final ZooKeeperServer.State state) {
        if (state == ZooKeeperServer.State.ERROR || state == ZooKeeperServer.State.SHUTDOWN) {
            this.shutdownLatch.countDown();
        }
    }
}
