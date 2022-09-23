// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import java.util.concurrent.ExecutorService;
import org.jboss.netty.util.ExternalResourceReleasable;
import java.util.concurrent.Executor;

public class ChainedExecutor implements Executor, ExternalResourceReleasable
{
    private final Executor cur;
    private final Executor next;
    private final ChannelEventRunnableFilter filter;
    
    public ChainedExecutor(final ChannelEventRunnableFilter filter, final Executor cur, final Executor next) {
        if (filter == null) {
            throw new NullPointerException("filter");
        }
        if (cur == null) {
            throw new NullPointerException("cur");
        }
        if (next == null) {
            throw new NullPointerException("next");
        }
        this.filter = filter;
        this.cur = cur;
        this.next = next;
    }
    
    public void execute(final Runnable command) {
        assert command instanceof ChannelEventRunnable;
        if (this.filter.filter((ChannelEventRunnable)command)) {
            this.cur.execute(command);
        }
        else {
            this.next.execute(command);
        }
    }
    
    public void releaseExternalResources() {
        if (this.cur instanceof ExecutorService) {
            ((ExecutorService)this.cur).shutdown();
        }
        if (this.next instanceof ExecutorService) {
            ((ExecutorService)this.next).shutdown();
        }
        releaseExternal(this.cur);
        releaseExternal(this.next);
    }
    
    private static void releaseExternal(final Executor executor) {
        if (executor instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)executor).releaseExternalResources();
        }
    }
}
