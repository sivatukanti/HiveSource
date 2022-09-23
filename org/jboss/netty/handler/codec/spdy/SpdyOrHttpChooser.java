// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import javax.net.ssl.SSLEngine;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public abstract class SpdyOrHttpChooser implements ChannelUpstreamHandler
{
    private final int maxSpdyContentLength;
    private final int maxHttpContentLength;
    
    protected SpdyOrHttpChooser(final int maxSpdyContentLength, final int maxHttpContentLength) {
        this.maxSpdyContentLength = maxSpdyContentLength;
        this.maxHttpContentLength = maxHttpContentLength;
    }
    
    protected abstract SelectedProtocol getProtocol(final SSLEngine p0);
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        final SslHandler handler = ctx.getPipeline().get(SslHandler.class);
        if (handler == null) {
            throw new IllegalStateException("SslHandler is needed for SPDY");
        }
        final ChannelPipeline pipeline = ctx.getPipeline();
        final SelectedProtocol protocol = this.getProtocol(handler.getEngine());
        switch (protocol) {
            case None: {
                return;
            }
            case SpdyVersion3_1: {
                this.addSpdyHandlers(ctx, SpdyVersion.SPDY_3_1);
                break;
            }
            case HttpVersion1_0:
            case HttpVersion1_1: {
                this.addHttpHandlers(ctx);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown SelectedProtocol");
            }
        }
        pipeline.remove(this);
        ctx.sendUpstream(e);
    }
    
    protected void addSpdyHandlers(final ChannelHandlerContext ctx, final SpdyVersion version) {
        final ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.addLast("spdyFrameCodec", new SpdyFrameCodec(version));
        pipeline.addLast("spdySessionHandler", new SpdySessionHandler(version, true));
        pipeline.addLast("spdyHttpEncoder", new SpdyHttpEncoder(version));
        pipeline.addLast("spdyHttpDecoder", new SpdyHttpDecoder(version, this.maxSpdyContentLength));
        pipeline.addLast("spdyStreamIdHandler", new SpdyHttpResponseStreamIdHandler());
        pipeline.addLast("httpRequestHandler", this.createHttpRequestHandlerForSpdy());
    }
    
    protected void addHttpHandlers(final ChannelHandlerContext ctx) {
        final ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.addLast("httpRequestDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpResponseEncoder", new HttpResponseEncoder());
        pipeline.addLast("httpChunkAggregator", new HttpChunkAggregator(this.maxHttpContentLength));
        pipeline.addLast("httpRequestHandler", this.createHttpRequestHandlerForHttp());
    }
    
    protected abstract ChannelUpstreamHandler createHttpRequestHandlerForHttp();
    
    protected ChannelUpstreamHandler createHttpRequestHandlerForSpdy() {
        return this.createHttpRequestHandlerForHttp();
    }
    
    public enum SelectedProtocol
    {
        SpdyVersion3_1, 
        HttpVersion1_1, 
        HttpVersion1_0, 
        None;
    }
}
