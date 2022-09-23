// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.protobuf;

import com.google.protobuf.MessageLite;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class ProtobufEncoder extends OneToOneEncoder
{
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (msg instanceof MessageLite) {
            final byte[] array = ((MessageLite)msg).toByteArray();
            return ctx.getChannel().getConfig().getBufferFactory().getBuffer(array, 0, array.length);
        }
        if (msg instanceof MessageLite.Builder) {
            final byte[] array = ((MessageLite.Builder)msg).build().toByteArray();
            return ctx.getChannel().getConfig().getBufferFactory().getBuffer(array, 0, array.length);
        }
        return msg;
    }
}
