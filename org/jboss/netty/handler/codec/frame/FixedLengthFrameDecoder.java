// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class FixedLengthFrameDecoder extends FrameDecoder
{
    private final int frameLength;
    private final boolean allocateFullBuffer;
    
    public FixedLengthFrameDecoder(final int frameLength) {
        this(frameLength, false);
    }
    
    public FixedLengthFrameDecoder(final int frameLength, final boolean allocateFullBuffer) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
        this.allocateFullBuffer = allocateFullBuffer;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < this.frameLength) {
            return null;
        }
        final ChannelBuffer frame = this.extractFrame(buffer, buffer.readerIndex(), this.frameLength);
        buffer.skipBytes(this.frameLength);
        return frame;
    }
    
    @Override
    protected ChannelBuffer newCumulationBuffer(final ChannelHandlerContext ctx, final int minimumCapacity) {
        final ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
        if (this.allocateFullBuffer) {
            return factory.getBuffer(this.frameLength);
        }
        return super.newCumulationBuffer(ctx, minimumCapacity);
    }
}
