// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.protobuf;

import org.jboss.netty.buffer.ChannelBuffers;
import java.io.OutputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import com.google.protobuf.CodedOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class ProtobufVarint32LengthFieldPrepender extends OneToOneEncoder
{
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        final ChannelBuffer body = (ChannelBuffer)msg;
        final int length = body.readableBytes();
        final ChannelBuffer header = channel.getConfig().getBufferFactory().getBuffer(body.order(), CodedOutputStream.computeRawVarint32Size(length));
        final CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(new ChannelBufferOutputStream(header));
        codedOutputStream.writeRawVarint32(length);
        codedOutputStream.flush();
        return ChannelBuffers.wrappedBuffer(header, body);
    }
}
