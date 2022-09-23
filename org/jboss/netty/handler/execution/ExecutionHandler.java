// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

@ChannelHandler.Sharable
public class ExecutionHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler, ExternalResourceReleasable
{
    private final Executor executor;
    private final boolean handleDownstream;
    private final boolean handleUpstream;
    
    public ExecutionHandler(final Executor executor) {
        this(executor, false, true);
    }
    
    public ExecutionHandler(final Executor executor, final boolean handleDownstream, final boolean handleUpstream) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        if (!handleDownstream && !handleUpstream) {
            throw new IllegalArgumentException("You must handle at least handle one event type");
        }
        this.executor = executor;
        this.handleDownstream = handleDownstream;
        this.handleUpstream = handleUpstream;
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public void releaseExternalResources() {
        final Executor executor = this.getExecutor();
        if (executor instanceof ExecutorService) {
            ((ExecutorService)executor).shutdown();
        }
        if (executor instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)executor).releaseExternalResources();
        }
    }
    
    public void handleUpstream(final ChannelHandlerContext context, final ChannelEvent e) throws Exception {
        if (this.handleUpstream) {
            this.executor.execute(new ChannelUpstreamEventRunnable(context, e, this.executor));
        }
        else {
            context.sendUpstream(e);
        }
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (!this.handleReadSuspend(ctx, e)) {
            if (this.handleDownstream) {
                this.executor.execute(new ChannelDownstreamEventRunnable(ctx, e, this.executor));
            }
            else {
                ctx.sendDownstream(e);
            }
        }
    }
    
    protected boolean handleReadSuspend(final ChannelHandlerContext ctx, final ChannelEvent e) {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent cse = (ChannelStateEvent)e;
            if (cse.getState() == ChannelState.INTEREST_OPS && ((int)cse.getValue() & 0x1) != 0x0) {
                final boolean readSuspended = ctx.getAttachment() != null;
                if (readSuspended) {
                    e.getFuture().setSuccess();
                    return true;
                }
            }
        }
        return false;
    }
}
