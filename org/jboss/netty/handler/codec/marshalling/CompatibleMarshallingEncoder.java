// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.ByteOutput;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class CompatibleMarshallingEncoder extends OneToOneEncoder
{
    private final MarshallerProvider provider;
    
    public CompatibleMarshallingEncoder(final MarshallerProvider provider) {
        this.provider = provider;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        final Marshaller marshaller = this.provider.getMarshaller(ctx);
        final ChannelBufferByteOutput output = new ChannelBufferByteOutput(ctx.getChannel().getConfig().getBufferFactory(), 256);
        marshaller.start((ByteOutput)output);
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
        return output.getBuffer();
    }
}
