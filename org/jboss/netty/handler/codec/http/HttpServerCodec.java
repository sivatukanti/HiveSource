// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class HttpServerCodec implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    private final HttpRequestDecoder decoder;
    private final HttpResponseEncoder encoder;
    
    public HttpServerCodec() {
        this(4096, 8192, 8192);
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        this.encoder = new HttpResponseEncoder();
        this.decoder = new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.decoder.handleUpstream(ctx, e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.encoder.handleDownstream(ctx, e);
    }
}
