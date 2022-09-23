// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory, Thread.UncaughtExceptionHandler
{
    private boolean daemon;
    private String threadName;
    private static final Logger logger;
    
    public CustomThreadFactory(final String threadName, final boolean daemon) {
        this.threadName = threadName;
        this.daemon = daemon;
    }
    
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(r, this.threadName);
        t.setDaemon(this.daemon);
        t.setUncaughtExceptionHandler(this);
        return t;
    }
    
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        CustomThreadFactory.logger.error("Uncaught Exception in thread " + thread.getName(), throwable);
    }
    
    static {
        logger = LoggerFactory.getLogger(CustomThreadFactory.class);
    }
}
