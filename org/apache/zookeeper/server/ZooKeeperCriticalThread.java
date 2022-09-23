// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ZooKeeperCriticalThread extends ZooKeeperThread
{
    private static final Logger LOG;
    private final ZooKeeperServerListener listener;
    
    public ZooKeeperCriticalThread(final String threadName, final ZooKeeperServerListener listener) {
        super(threadName);
        this.listener = listener;
    }
    
    @Override
    protected void handleException(final String threadName, final Throwable e) {
        ZooKeeperCriticalThread.LOG.error("Severe unrecoverable error, from thread : {}", threadName, e);
        this.listener.notifyStopping(threadName, 1);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperCriticalThread.class);
    }
}
