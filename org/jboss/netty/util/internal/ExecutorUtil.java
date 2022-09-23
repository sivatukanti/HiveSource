// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;

public final class ExecutorUtil
{
    public static void shutdownNow(final Executor executor) {
        if (executor instanceof ExecutorService) {
            final ExecutorService es = (ExecutorService)executor;
            try {
                es.shutdownNow();
            }
            catch (SecurityException ex) {
                try {
                    es.shutdown();
                }
                catch (SecurityException ex2) {}
                catch (NullPointerException ex3) {}
            }
            catch (NullPointerException ex4) {}
        }
    }
    
    public static boolean isShutdown(final Executor executor) {
        return executor instanceof ExecutorService && ((ExecutorService)executor).isShutdown();
    }
    
    public static void terminate(final Executor... executors) {
        terminate(DeadLockProofWorker.PARENT, executors);
    }
    
    public static void terminate(final ThreadLocal<Executor> deadLockChecker, final Executor... executors) {
        if (executors == null) {
            throw new NullPointerException("executors");
        }
        final Executor[] executorsCopy = new Executor[executors.length];
        for (int i = 0; i < executors.length; ++i) {
            if (executors[i] == null) {
                throw new NullPointerException("executors[" + i + ']');
            }
            executorsCopy[i] = executors[i];
        }
        final Executor currentParent = deadLockChecker.get();
        if (currentParent != null) {
            for (final Executor e : executorsCopy) {
                if (e == currentParent) {
                    throw new IllegalStateException("An Executor cannot be shut down from the thread acquired from itself.  Please make sure you are not calling releaseExternalResources() from an I/O worker thread.");
                }
            }
        }
        boolean interrupted = false;
        for (final Executor e2 : executorsCopy) {
            if (e2 instanceof ExecutorService) {
                final ExecutorService es = (ExecutorService)e2;
                while (true) {
                    shutdownNow(es);
                    try {
                        if (es.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
                            break;
                        }
                        continue;
                    }
                    catch (InterruptedException ex) {
                        interrupted = true;
                    }
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
    
    private ExecutorUtil() {
    }
}
