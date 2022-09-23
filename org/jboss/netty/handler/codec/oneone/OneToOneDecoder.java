// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.oneone;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public abstract class OneToOneDecoder implements ChannelUpstreamHandler
{
    protected OneToOneDecoder() {
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendUpstream(evt);
            return;
        }
        final MessageEvent e = (MessageEvent)evt;
        final Object originalMessage = e.getMessage();
        final Object decodedMessage = this.decode(ctx, e.getChannel(), originalMessage);
        if (originalMessage == decodedMessage) {
            ctx.sendUpstream(evt);
        }
        else if (decodedMessage != null) {
            Channels.fireMessageReceived(ctx, decodedMessage, e.getRemoteAddress());
        }
    }
    
    protected abstract Object decode(final ChannelHandlerContext p0, final Channel p1, final Object p2) throws Exception;
}
