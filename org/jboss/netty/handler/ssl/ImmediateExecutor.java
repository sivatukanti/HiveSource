// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import java.util.concurrent.Executor;

public final class ImmediateExecutor implements Executor
{
    public static final ImmediateExecutor INSTANCE;
    
    public void execute(final Runnable command) {
        command.run();
    }
    
    private ImmediateExecutor() {
    }
    
    static {
        INSTANCE = new ImmediateExecutor();
    }
}
