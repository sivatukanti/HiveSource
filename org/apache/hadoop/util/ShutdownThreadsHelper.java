// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;

public class ShutdownThreadsHelper
{
    private static final Logger LOG;
    @VisibleForTesting
    static final int SHUTDOWN_WAIT_MS = 3000;
    
    public static boolean shutdownThread(final Thread thread) {
        return shutdownThread(thread, 3000L);
    }
    
    public static boolean shutdownThread(final Thread thread, final long timeoutInMilliSeconds) {
        if (thread == null) {
            return true;
        }
        try {
            thread.interrupt();
            thread.join(timeoutInMilliSeconds);
            return true;
        }
        catch (InterruptedException ie) {
            ShutdownThreadsHelper.LOG.warn("Interrupted while shutting down thread - " + thread.getName());
            return false;
        }
    }
    
    public static boolean shutdownExecutorService(final ExecutorService service) throws InterruptedException {
        return shutdownExecutorService(service, 3000L);
    }
    
    public static boolean shutdownExecutorService(final ExecutorService service, final long timeoutInMs) throws InterruptedException {
        if (service == null) {
            return true;
        }
        service.shutdown();
        if (!service.awaitTermination(timeoutInMs, TimeUnit.MILLISECONDS)) {
            service.shutdownNow();
            return service.awaitTermination(timeoutInMs, TimeUnit.MILLISECONDS);
        }
        return true;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ShutdownThreadsHelper.class);
    }
}
