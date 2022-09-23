// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.Executor;

public class UnterminatableExecutor implements Executor
{
    private final Executor executor;
    
    public UnterminatableExecutor(final Executor executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        this.executor = executor;
    }
    
    public void execute(final Runnable command) {
        this.executor.execute(command);
    }
}
