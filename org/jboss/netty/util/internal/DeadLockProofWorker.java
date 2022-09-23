// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.Executor;

public final class DeadLockProofWorker
{
    public static final ThreadLocal<Executor> PARENT;
    
    public static void start(final Executor parent, final Runnable runnable) {
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        parent.execute(new Runnable() {
            public void run() {
                DeadLockProofWorker.PARENT.set(parent);
                try {
                    runnable.run();
                }
                finally {
                    DeadLockProofWorker.PARENT.remove();
                }
            }
        });
    }
    
    private DeadLockProofWorker() {
    }
    
    static {
        PARENT = new ThreadLocal<Executor>();
    }
}
