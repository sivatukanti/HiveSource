// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class LengthFieldPrepender extends OneToOneEncoder
{
    private final int lengthFieldLength;
    private final boolean lengthIncludesLengthFieldLength;
    
    public LengthFieldPrepender(final int lengthFieldLength) {
        this(lengthFieldLength, false);
    }
    
    public LengthFieldPrepender(final int lengthFieldLength, final boolean lengthIncludesLengthFieldLength) {
        if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 3 && lengthFieldLength != 4 && lengthFieldLength != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + lengthFieldLength);
        }
        this.lengthFieldLength = lengthFieldLength;
        this.lengthIncludesLengthFieldLength = lengthIncludesLengthFieldLength;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        final ChannelBuffer body = (ChannelBuffer)msg;
        final ChannelBuffer header = channel.getConfig().getBufferFactory().getBuffer(body.order(), this.lengthFieldLength);
        final int length = this.lengthIncludesLengthFieldLength ? (body.readableBytes() + this.lengthFieldLength) : body.readableBytes();
        switch (this.lengthFieldLength) {
            case 1: {
                if (length >= 256) {
                    throw new IllegalArgumentException("length does not fit into a byte: " + length);
                }
                header.writeByte((byte)length);
                break;
            }
            case 2: {
                if (length >= 65536) {
                    throw new IllegalArgumentException("length does not fit into a short integer: " + length);
                }
                header.writeShort((short)length);
                break;
            }
            case 3: {
                if (length >= 16777216) {
                    throw new IllegalArgumentException("length does not fit into a medium integer: " + length);
                }
                header.writeMedium(length);
                break;
            }
            case 4: {
                header.writeInt(length);
                break;
            }
            case 8: {
                header.writeLong(length);
                break;
            }
            default: {
                throw new Error("should not reach here");
            }
        }
        return ChannelBuffers.wrappedBuffer(header, body);
    }
}
