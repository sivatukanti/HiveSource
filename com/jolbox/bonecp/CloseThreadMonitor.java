// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CloseThreadMonitor implements Runnable
{
    private ConnectionHandle connectionHandle;
    private String stackTrace;
    private Thread threadToMonitor;
    private long closeConnectionWatchTimeout;
    private static final Logger logger;
    
    public CloseThreadMonitor(final Thread threadToMonitor, final ConnectionHandle connectionHandle, final String stackTrace, final long closeConnectionWatchTimeout) {
        this.connectionHandle = connectionHandle;
        this.stackTrace = stackTrace;
        this.threadToMonitor = threadToMonitor;
        this.closeConnectionWatchTimeout = closeConnectionWatchTimeout;
    }
    
    public void run() {
        try {
            this.connectionHandle.setThreadWatch(Thread.currentThread());
            this.threadToMonitor.join(this.closeConnectionWatchTimeout);
            if (!this.connectionHandle.isClosed() && this.threadToMonitor.equals(this.connectionHandle.getThreadUsingConnection())) {
                CloseThreadMonitor.logger.error(this.stackTrace);
            }
        }
        catch (Exception e) {
            if (this.connectionHandle != null) {
                this.connectionHandle.setThreadWatch(null);
            }
        }
    }
    
    static {
        logger = LoggerFactory.getLogger(CloseThreadMonitor.class);
    }
}
