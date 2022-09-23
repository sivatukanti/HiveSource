// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class SocksMessageEncoder extends OneToOneEncoder
{
    private static final int DEFAULT_ENCODER_BUFFER_SIZE = 1024;
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        ChannelBuffer buffer = null;
        if (msg instanceof SocksMessage) {
            buffer = ChannelBuffers.buffer(1024);
            ((SocksMessage)msg).encodeAsByteBuf(buffer);
        }
        return buffer;
    }
}
