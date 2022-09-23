// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import org.jboss.netty.buffer.ChannelBuffer;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class ObjectEncoder extends OneToOneEncoder
{
    private static final byte[] LENGTH_PLACEHOLDER;
    private final int estimatedLength;
    
    public ObjectEncoder() {
        this(512);
    }
    
    public ObjectEncoder(final int estimatedLength) {
        if (estimatedLength < 0) {
            throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
        }
        this.estimatedLength = estimatedLength;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        final ChannelBufferOutputStream bout = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer(this.estimatedLength, ctx.getChannel().getConfig().getBufferFactory()));
        bout.write(ObjectEncoder.LENGTH_PLACEHOLDER);
        final ObjectOutputStream oout = new CompactObjectOutputStream(bout);
        oout.writeObject(msg);
        oout.flush();
        oout.close();
        final ChannelBuffer encoded = bout.buffer();
        encoded.setInt(0, encoded.writerIndex() - 4);
        return encoded;
    }
    
    static {
        LENGTH_PLACEHOLDER = new byte[4];
    }
}
