// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpMessageDecoder;

public abstract class RtspMessageDecoder extends HttpMessageDecoder
{
    private final DecoderEmbedder<HttpMessage> aggregator;
    
    protected RtspMessageDecoder() {
        this(4096, 8192, 8192);
    }
    
    protected RtspMessageDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2);
        this.aggregator = new DecoderEmbedder<HttpMessage>(new ChannelUpstreamHandler[] { new HttpChunkAggregator(maxContentLength) });
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        final Object o = super.decode(ctx, channel, buffer, state);
        if (o != null && this.aggregator.offer(o)) {
            return this.aggregator.poll();
        }
        return null;
    }
    
    @Override
    protected boolean isContentAlwaysEmpty(final HttpMessage msg) {
        final boolean empty = super.isContentAlwaysEmpty(msg);
        return empty || !msg.headers().contains("Content-Length") || empty;
    }
}
