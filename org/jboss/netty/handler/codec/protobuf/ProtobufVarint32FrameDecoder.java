// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.protobuf;

import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import com.google.protobuf.CodedInputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class ProtobufVarint32FrameDecoder extends FrameDecoder
{
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        buffer.markReaderIndex();
        final byte[] buf = new byte[5];
        int i = 0;
        while (i < buf.length) {
            if (!buffer.readable()) {
                buffer.resetReaderIndex();
                return null;
            }
            buf[i] = buffer.readByte();
            if (buf[i] >= 0) {
                final int length = CodedInputStream.newInstance(buf, 0, i + 1).readRawVarint32();
                if (length < 0) {
                    throw new CorruptedFrameException("negative length: " + length);
                }
                if (buffer.readableBytes() < length) {
                    buffer.resetReaderIndex();
                    return null;
                }
                return buffer.readBytes(length);
            }
            else {
                ++i;
            }
        }
        throw new CorruptedFrameException("length wider than 32-bit");
    }
}
