// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.util.CharsetUtil;
import java.util.List;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class HttpChunkAggregator extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler
{
    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private static final ChannelBuffer CONTINUE;
    private final int maxContentLength;
    private HttpMessage currentMessage;
    private boolean tooLongFrameFound;
    private ChannelHandlerContext ctx;
    private int maxCumulationBufferComponents;
    
    public HttpChunkAggregator(final int maxContentLength) {
        this.maxCumulationBufferComponents = 1024;
        if (maxContentLength <= 0) {
            throw new IllegalArgumentException("maxContentLength must be a positive integer: " + maxContentLength);
        }
        this.maxContentLength = maxContentLength;
    }
    
    public final int getMaxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }
    
    public final void setMaxCumulationBufferComponents(final int maxCumulationBufferComponents) {
        if (maxCumulationBufferComponents < 2) {
            throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
        }
        if (this.ctx == null) {
            this.maxCumulationBufferComponents = maxCumulationBufferComponents;
            return;
        }
        throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        final HttpMessage currentMessage = this.currentMessage;
        if (msg instanceof HttpMessage) {
            final HttpMessage m = (HttpMessage)msg;
            this.tooLongFrameFound = false;
            if (HttpHeaders.is100ContinueExpected(m)) {
                Channels.write(ctx, Channels.succeededFuture(ctx.getChannel()), HttpChunkAggregator.CONTINUE.duplicate());
            }
            if (m.isChunked()) {
                HttpCodecUtil.removeTransferEncodingChunked(m);
                m.setChunked(false);
                this.currentMessage = m;
            }
            else {
                this.currentMessage = null;
                ctx.sendUpstream(e);
            }
        }
        else if (msg instanceof HttpChunk) {
            if (currentMessage == null) {
                throw new IllegalStateException("received " + HttpChunk.class.getSimpleName() + " without " + HttpMessage.class.getSimpleName());
            }
            final HttpChunk chunk = (HttpChunk)msg;
            if (this.tooLongFrameFound) {
                if (chunk.isLast()) {
                    this.currentMessage = null;
                }
                return;
            }
            final ChannelBuffer content = currentMessage.getContent();
            if (content.readableBytes() > this.maxContentLength - chunk.getContent().readableBytes()) {
                this.tooLongFrameFound = true;
                throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
            }
            this.appendToCumulation(chunk.getContent());
            if (chunk.isLast()) {
                this.currentMessage = null;
                if (chunk instanceof HttpChunkTrailer) {
                    final HttpChunkTrailer trailer = (HttpChunkTrailer)chunk;
                    for (final Map.Entry<String, String> header : trailer.trailingHeaders()) {
                        currentMessage.headers().set(header.getKey(), header.getValue());
                    }
                }
                currentMessage.headers().set("Content-Length", String.valueOf(content.readableBytes()));
                Channels.fireMessageReceived(ctx, currentMessage, e.getRemoteAddress());
            }
        }
        else {
            ctx.sendUpstream(e);
        }
    }
    
    protected void appendToCumulation(final ChannelBuffer input) {
        final ChannelBuffer cumulation = this.currentMessage.getContent();
        if (cumulation instanceof CompositeChannelBuffer) {
            final CompositeChannelBuffer composite = (CompositeChannelBuffer)cumulation;
            if (composite.numComponents() >= this.maxCumulationBufferComponents) {
                this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(composite.copy(), input));
            }
            else {
                final List<ChannelBuffer> decomposed = composite.decompose(0, composite.readableBytes());
                final ChannelBuffer[] buffers = decomposed.toArray(new ChannelBuffer[decomposed.size() + 1]);
                buffers[buffers.length - 1] = input;
                this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(buffers));
            }
        }
        else {
            this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(cumulation, input));
        }
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    static {
        CONTINUE = ChannelBuffers.copiedBuffer("HTTP/1.1 100 Continue\r\n\r\n", CharsetUtil.US_ASCII);
    }
}
