// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ZooKeeperThread extends Thread
{
    private static final Logger LOG;
    private UncaughtExceptionHandler uncaughtExceptionalHandler;
    
    public ZooKeeperThread(final Runnable thread, final String threadName) {
        super(thread, threadName);
        this.setUncaughtExceptionHandler(this.uncaughtExceptionalHandler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                ZooKeeperThread.this.handleException(t.getName(), e);
            }
        });
    }
    
    public ZooKeeperThread(final String threadName) {
        super(threadName);
        this.setUncaughtExceptionHandler(this.uncaughtExceptionalHandler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                ZooKeeperThread.this.handleException(t.getName(), e);
            }
        });
    }
    
    protected void handleException(final String thName, final Throwable e) {
        ZooKeeperThread.LOG.warn("Exception occurred from thread {}", thName, e);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperThread.class);
    }
}
