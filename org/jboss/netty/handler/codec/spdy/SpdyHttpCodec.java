// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class SpdyHttpCodec implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    private final SpdyHttpDecoder decoder;
    private final SpdyHttpEncoder encoder;
    
    public SpdyHttpCodec(final SpdyVersion version, final int maxContentLength) {
        this.decoder = new SpdyHttpDecoder(version, maxContentLength);
        this.encoder = new SpdyHttpEncoder(version);
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.decoder.handleUpstream(ctx, e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.encoder.handleDownstream(ctx, e);
    }
}
