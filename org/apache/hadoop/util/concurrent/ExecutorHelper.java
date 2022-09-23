// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.concurrent;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;

public final class ExecutorHelper
{
    private static final Logger LOG;
    
    static void logThrowableFromAfterExecute(final Runnable r, Throwable t) {
        if (ExecutorHelper.LOG.isDebugEnabled()) {
            ExecutorHelper.LOG.debug("afterExecute in thread: " + Thread.currentThread().getName() + ", runnable type: " + r.getClass().getName());
        }
        if (t == null && r instanceof Future && ((Future)r).isDone()) {
            try {
                ((Future)r).get();
            }
            catch (ExecutionException ee) {
                ExecutorHelper.LOG.warn("Execution exception when running task in " + Thread.currentThread().getName());
                t = ee.getCause();
            }
            catch (InterruptedException ie) {
                ExecutorHelper.LOG.warn("Thread (" + Thread.currentThread() + ") interrupted: ", ie);
                Thread.currentThread().interrupt();
            }
            catch (Throwable throwable) {
                t = throwable;
            }
        }
        if (t != null) {
            ExecutorHelper.LOG.warn("Caught exception in thread " + Thread.currentThread().getName() + ": ", t);
        }
    }
    
    private ExecutorHelper() {
    }
    
    static {
        LOG = LoggerFactory.getLogger(ExecutorHelper.class);
    }
}
