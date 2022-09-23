// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class YarnUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final Log LOG;
    
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        if (ShutdownHookManager.get().isShutdownInProgress()) {
            YarnUncaughtExceptionHandler.LOG.error("Thread " + t + " threw an Throwable, but we are shutting " + "down, so ignoring this", e);
        }
        else if (e instanceof Error) {
            try {
                YarnUncaughtExceptionHandler.LOG.fatal("Thread " + t + " threw an Error.  Shutting down now...", e);
            }
            catch (Throwable t2) {}
            if (e instanceof OutOfMemoryError) {
                try {
                    System.err.println("Halting due to Out Of Memory Error...");
                }
                catch (Throwable t3) {}
                ExitUtil.halt(-1);
            }
            else {
                ExitUtil.terminate(-1);
            }
        }
        else {
            YarnUncaughtExceptionHandler.LOG.error("Thread " + t + " threw an Exception.", e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(YarnUncaughtExceptionHandler.class);
    }
}
