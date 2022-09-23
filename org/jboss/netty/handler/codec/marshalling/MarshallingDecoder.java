// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

public class MarshallingDecoder extends LengthFieldBasedFrameDecoder
{
    private final UnmarshallerProvider provider;
    
    public MarshallingDecoder(final UnmarshallerProvider provider) {
        this(provider, 1048576);
    }
    
    public MarshallingDecoder(final UnmarshallerProvider provider, final int maxObjectSize) {
        super(maxObjectSize, 0, 4, 0, 4);
        this.provider = provider;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        final ChannelBuffer frame = (ChannelBuffer)super.decode(ctx, channel, buffer);
        if (frame == null) {
            return null;
        }
        final Unmarshaller unmarshaller = this.provider.getUnmarshaller(ctx);
        final ByteInput input = (ByteInput)new ChannelBufferByteInput(frame);
        try {
            unmarshaller.start(input);
            final Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            return obj;
        }
        finally {
            unmarshaller.close();
        }
    }
    
    @Override
    protected ChannelBuffer extractFrame(final ChannelBuffer buffer, final int index, final int length) {
        return buffer.slice(index, length);
    }
}
