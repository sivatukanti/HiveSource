// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class CompatibleMarshallingDecoder extends ReplayingDecoder<VoidEnum>
{
    protected final UnmarshallerProvider provider;
    protected final int maxObjectSize;
    private boolean discardingTooLongFrame;
    
    public CompatibleMarshallingDecoder(final UnmarshallerProvider provider, final int maxObjectSize) {
        this.provider = provider;
        this.maxObjectSize = maxObjectSize;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final VoidEnum state) throws Exception {
        if (this.discardingTooLongFrame) {
            buffer.skipBytes(this.actualReadableBytes());
            this.checkpoint();
            return null;
        }
        final Unmarshaller unmarshaller = this.provider.getUnmarshaller(ctx);
        ByteInput input = (ByteInput)new ChannelBufferByteInput(buffer);
        if (this.maxObjectSize != Integer.MAX_VALUE) {
            input = (ByteInput)new LimitingByteInput(input, this.maxObjectSize);
        }
        try {
            unmarshaller.start(input);
            final Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            return obj;
        }
        catch (LimitingByteInput.TooBigObjectException e) {
            this.discardingTooLongFrame = true;
            throw new TooLongFrameException();
        }
        finally {
            unmarshaller.close();
        }
    }
    
    @Override
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final VoidEnum state) throws Exception {
        switch (buffer.readableBytes()) {
            case 0: {
                return null;
            }
            case 1: {
                if (buffer.getByte(buffer.readerIndex()) == 121) {
                    buffer.skipBytes(1);
                    return null;
                }
                break;
            }
        }
        final Object decoded = this.decode(ctx, channel, buffer, state);
        return decoded;
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof TooLongFrameException) {
            e.getChannel().close();
        }
        else {
            super.exceptionCaught(ctx, e);
        }
    }
}
