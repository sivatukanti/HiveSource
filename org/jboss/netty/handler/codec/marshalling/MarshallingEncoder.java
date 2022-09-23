// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.ByteOutput;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class MarshallingEncoder extends OneToOneEncoder
{
    private static final byte[] LENGTH_PLACEHOLDER;
    private final MarshallerProvider provider;
    private final int estimatedLength;
    
    public MarshallingEncoder(final MarshallerProvider provider) {
        this(provider, 512);
    }
    
    public MarshallingEncoder(final MarshallerProvider provider, final int estimatedLength) {
        if (estimatedLength < 0) {
            throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
        }
        this.estimatedLength = estimatedLength;
        this.provider = provider;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        final Marshaller marshaller = this.provider.getMarshaller(ctx);
        final ChannelBufferByteOutput output = new ChannelBufferByteOutput(ctx.getChannel().getConfig().getBufferFactory(), this.estimatedLength);
        output.getBuffer().writeBytes(MarshallingEncoder.LENGTH_PLACEHOLDER);
        marshaller.start((ByteOutput)output);
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
        final ChannelBuffer encoded = output.getBuffer();
        encoded.setInt(0, encoded.writerIndex() - 4);
        return encoded;
    }
    
    static {
        LENGTH_PLACEHOLDER = new byte[4];
    }
}
