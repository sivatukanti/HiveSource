// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.io.InputStream;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

public class ObjectDecoder extends LengthFieldBasedFrameDecoder
{
    private final ClassResolver classResolver;
    
    public ObjectDecoder(final ClassResolver classResolver) {
        this(1048576, classResolver);
    }
    
    public ObjectDecoder(final int maxObjectSize, final ClassResolver classResolver) {
        super(maxObjectSize, 0, 4, 0, 4);
        if (classResolver == null) {
            throw new NullPointerException("classResolver");
        }
        this.classResolver = classResolver;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        final ChannelBuffer frame = (ChannelBuffer)super.decode(ctx, channel, buffer);
        if (frame == null) {
            return null;
        }
        return new CompactObjectInputStream(new ChannelBufferInputStream(frame), this.classResolver).readObject();
    }
    
    @Override
    protected ChannelBuffer extractFrame(final ChannelBuffer buffer, final int index, final int length) {
        return buffer.slice(index, length);
    }
}
