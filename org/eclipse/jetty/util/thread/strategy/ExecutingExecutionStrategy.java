// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread.strategy;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.RejectedExecutionException;
import java.io.Closeable;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ExecutionStrategy;

public abstract class ExecutingExecutionStrategy implements ExecutionStrategy
{
    private static final Logger LOG;
    private final Executor _executor;
    
    protected ExecutingExecutionStrategy(final Executor executor) {
        this._executor = executor;
    }
    
    protected boolean execute(final Runnable task) {
        try {
            this._executor.execute(task);
            return true;
        }
        catch (RejectedExecutionException e) {
            ExecutingExecutionStrategy.LOG.debug(e);
            ExecutingExecutionStrategy.LOG.warn("Rejected execution of {}", task);
            try {
                if (task instanceof Closeable) {
                    ((Closeable)task).close();
                }
            }
            catch (Exception x) {
                e.addSuppressed(x);
                ExecutingExecutionStrategy.LOG.warn(e);
            }
            return false;
        }
    }
    
    static {
        LOG = Log.getLogger(ExecutingExecutionStrategy.class);
    }
}
