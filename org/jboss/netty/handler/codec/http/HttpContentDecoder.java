// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public abstract class HttpContentDecoder extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler
{
    private DecoderEmbedder<ChannelBuffer> decoder;
    
    protected HttpContentDecoder() {
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        if (msg instanceof HttpResponse && ((HttpResponse)msg).getStatus().getCode() == 100) {
            ctx.sendUpstream(e);
        }
        else if (msg instanceof HttpMessage) {
            final HttpMessage m = (HttpMessage)msg;
            this.finishDecode();
            String contentEncoding = m.headers().get("Content-Encoding");
            if (contentEncoding != null) {
                contentEncoding = contentEncoding.trim();
            }
            else {
                contentEncoding = "identity";
            }
            final boolean hasContent = m.isChunked() || m.getContent().readable();
            if (hasContent && (this.decoder = this.newContentDecoder(contentEncoding)) != null) {
                final String targetContentEncoding = this.getTargetContentEncoding(contentEncoding);
                if ("identity".equals(targetContentEncoding)) {
                    m.headers().remove("Content-Encoding");
                }
                else {
                    m.headers().set("Content-Encoding", targetContentEncoding);
                }
                if (!m.isChunked()) {
                    ChannelBuffer content = m.getContent();
                    content = ChannelBuffers.wrappedBuffer(this.decode(content), this.finishDecode());
                    m.setContent(content);
                    if (m.headers().contains("Content-Length")) {
                        m.headers().set("Content-Length", Integer.toString(content.readableBytes()));
                    }
                }
            }
            ctx.sendUpstream(e);
        }
        else if (msg instanceof HttpChunk) {
            final HttpChunk c = (HttpChunk)msg;
            ChannelBuffer content2 = c.getContent();
            if (this.decoder != null) {
                if (!c.isLast()) {
                    content2 = this.decode(content2);
                    if (content2.readable()) {
                        c.setContent(content2);
                        ctx.sendUpstream(e);
                    }
                }
                else {
                    final ChannelBuffer lastProduct = this.finishDecode();
                    if (lastProduct.readable()) {
                        Channels.fireMessageReceived(ctx, new DefaultHttpChunk(lastProduct), e.getRemoteAddress());
                    }
                    ctx.sendUpstream(e);
                }
            }
            else {
                ctx.sendUpstream(e);
            }
        }
        else {
            ctx.sendUpstream(e);
        }
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.finishDecode();
        super.channelClosed(ctx, e);
    }
    
    protected abstract DecoderEmbedder<ChannelBuffer> newContentDecoder(final String p0) throws Exception;
    
    protected String getTargetContentEncoding(final String contentEncoding) throws Exception {
        return "identity";
    }
    
    private ChannelBuffer decode(final ChannelBuffer buf) {
        this.decoder.offer(buf);
        return ChannelBuffers.wrappedBuffer((ChannelBuffer[])this.decoder.pollAll(new ChannelBuffer[this.decoder.size()]));
    }
    
    private ChannelBuffer finishDecode() {
        if (this.decoder == null) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        ChannelBuffer result;
        if (this.decoder.finish()) {
            result = ChannelBuffers.wrappedBuffer((ChannelBuffer[])this.decoder.pollAll(new ChannelBuffer[this.decoder.size()]));
        }
        else {
            result = ChannelBuffers.EMPTY_BUFFER;
        }
        this.decoder = null;
        return result;
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
        this.finishDecode();
    }
}
