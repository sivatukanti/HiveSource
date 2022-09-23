// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.oneone;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;

public abstract class OneToOneEncoder implements ChannelDownstreamHandler
{
    protected OneToOneEncoder() {
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }
        final MessageEvent e = (MessageEvent)evt;
        if (!this.doEncode(ctx, e)) {
            ctx.sendDownstream(e);
        }
    }
    
    protected boolean doEncode(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object originalMessage = e.getMessage();
        final Object encodedMessage = this.encode(ctx, e.getChannel(), originalMessage);
        if (originalMessage == encodedMessage) {
            return false;
        }
        if (encodedMessage != null) {
            Channels.write(ctx, e.getFuture(), encodedMessage, e.getRemoteAddress());
        }
        return true;
    }
    
    protected abstract Object encode(final ChannelHandlerContext p0, final Channel p1, final Object p2) throws Exception;
}
