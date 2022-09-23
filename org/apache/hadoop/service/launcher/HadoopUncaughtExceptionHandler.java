// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.ShutdownHookManager;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class HadoopUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final Logger LOG;
    private final Thread.UncaughtExceptionHandler delegate;
    
    public HadoopUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler delegate) {
        this.delegate = delegate;
    }
    
    public HadoopUncaughtExceptionHandler() {
        this(null);
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable exception) {
        if (ShutdownHookManager.get().isShutdownInProgress()) {
            HadoopUncaughtExceptionHandler.LOG.error("Thread {} threw an error during shutdown: {}.", thread.toString(), exception, exception);
        }
        else if (exception instanceof Error) {
            try {
                HadoopUncaughtExceptionHandler.LOG.error("Thread {} threw an error: {}. Shutting down", thread.toString(), exception, exception);
            }
            catch (Throwable t) {}
            if (exception instanceof OutOfMemoryError) {
                try {
                    System.err.println("Halting due to Out Of Memory Error...");
                }
                catch (Throwable t2) {}
                ExitUtil.haltOnOutOfMemory((OutOfMemoryError)exception);
            }
            else {
                final ExitUtil.ExitException ee = ServiceLauncher.convertToExitException(exception);
                ExitUtil.terminate(ee.status, ee);
            }
        }
        else {
            HadoopUncaughtExceptionHandler.LOG.error("Thread {} threw an exception: {}", thread.toString(), exception, exception);
            if (this.delegate != null) {
                this.delegate.uncaughtException(thread, exception);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(HadoopUncaughtExceptionHandler.class);
    }
}
