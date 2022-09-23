// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpMessageEncoder;

@ChannelHandler.Sharable
public abstract class RtspMessageEncoder extends HttpMessageEncoder
{
    protected RtspMessageEncoder() {
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof HttpMessage)) {
            return msg;
        }
        return super.encode(ctx, channel, msg);
    }
}
