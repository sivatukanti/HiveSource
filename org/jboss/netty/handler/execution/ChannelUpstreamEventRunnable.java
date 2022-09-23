// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import java.util.concurrent.Executor;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;

public class ChannelUpstreamEventRunnable extends ChannelEventRunnable
{
    public ChannelUpstreamEventRunnable(final ChannelHandlerContext ctx, final ChannelEvent e, final Executor executor) {
        super(ctx, e, executor);
    }
    
    @Override
    protected void doRun() {
        this.ctx.sendUpstream(this.e);
    }
}
