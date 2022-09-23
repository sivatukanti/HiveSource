// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.base64;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

@ChannelHandler.Sharable
public class Base64Decoder extends OneToOneDecoder
{
    private final Base64Dialect dialect;
    
    public Base64Decoder() {
        this(Base64Dialect.STANDARD);
    }
    
    public Base64Decoder(final Base64Dialect dialect) {
        if (dialect == null) {
            throw new NullPointerException("dialect");
        }
        this.dialect = dialect;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, Object msg) throws Exception {
        if (msg instanceof String) {
            msg = ChannelBuffers.copiedBuffer((CharSequence)msg, CharsetUtil.US_ASCII);
        }
        else if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        final ChannelBuffer src = (ChannelBuffer)msg;
        return Base64.decode(src, src.readerIndex(), src.readableBytes(), this.dialect);
    }
}
