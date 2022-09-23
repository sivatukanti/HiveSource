// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.base64;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class Base64Encoder extends OneToOneEncoder
{
    private final boolean breakLines;
    private final Base64Dialect dialect;
    
    public Base64Encoder() {
        this(true);
    }
    
    public Base64Encoder(final boolean breakLines) {
        this(breakLines, Base64Dialect.STANDARD);
    }
    
    public Base64Encoder(final boolean breakLines, final Base64Dialect dialect) {
        if (dialect == null) {
            throw new NullPointerException("dialect");
        }
        this.breakLines = breakLines;
        this.dialect = dialect;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        final ChannelBuffer src = (ChannelBuffer)msg;
        return Base64.encode(src, src.readerIndex(), src.readableBytes(), this.breakLines, this.dialect);
    }
}
