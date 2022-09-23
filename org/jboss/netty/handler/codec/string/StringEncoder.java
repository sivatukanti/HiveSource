// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.string;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.nio.charset.Charset;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class StringEncoder extends OneToOneEncoder
{
    private final Charset charset;
    
    public StringEncoder() {
        this(Charset.defaultCharset());
    }
    
    public StringEncoder(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (msg instanceof String) {
            return ChannelBuffers.copiedBuffer(ctx.getChannel().getConfig().getBufferFactory().getDefaultOrder(), (CharSequence)msg, this.charset);
        }
        return msg;
    }
}
