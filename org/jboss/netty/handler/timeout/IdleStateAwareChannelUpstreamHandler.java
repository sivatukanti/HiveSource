// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class IdleStateAwareChannelUpstreamHandler extends SimpleChannelUpstreamHandler
{
    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            this.channelIdle(ctx, (IdleStateEvent)e);
        }
        else {
            super.handleUpstream(ctx, e);
        }
    }
    
    public void channelIdle(final ChannelHandlerContext ctx, final IdleStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
}
