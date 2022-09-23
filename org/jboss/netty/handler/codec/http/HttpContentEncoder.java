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
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import java.util.Queue;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelHandler;

public abstract class HttpContentEncoder extends SimpleChannelHandler implements LifeCycleAwareChannelHandler
{
    private final Queue<String> acceptEncodingQueue;
    private volatile EncoderEmbedder<ChannelBuffer> encoder;
    private volatile boolean offerred;
    
    protected HttpContentEncoder() {
        this.acceptEncodingQueue = new ConcurrentLinkedQueue<String>();
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        if (!(msg instanceof HttpMessage)) {
            ctx.sendUpstream(e);
            return;
        }
        final HttpMessage m = (HttpMessage)msg;
        String acceptedEncoding = m.headers().get("Accept-Encoding");
        if (acceptedEncoding == null) {
            acceptedEncoding = "identity";
        }
        final boolean offered = this.acceptEncodingQueue.offer(acceptedEncoding);
        assert offered;
        ctx.sendUpstream(e);
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        if (msg instanceof HttpResponse && ((HttpResponse)msg).getStatus().getCode() == 100) {
            ctx.sendDownstream(e);
        }
        else if (msg instanceof HttpMessage) {
            final HttpMessage m = (HttpMessage)msg;
            this.finishEncode();
            final String acceptEncoding = this.acceptEncodingQueue.poll();
            if (acceptEncoding == null) {
                throw new IllegalStateException("cannot send more responses than requests");
            }
            final String contentEncoding = m.headers().get("Content-Encoding");
            if (contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding)) {
                ctx.sendDownstream(e);
            }
            else {
                final boolean hasContent = m.isChunked() || m.getContent().readable();
                if (hasContent && (this.encoder = this.newContentEncoder(m, acceptEncoding)) != null) {
                    m.headers().set("Content-Encoding", this.getTargetContentEncoding(acceptEncoding));
                    if (m.isChunked()) {
                        m.headers().remove("Content-Length");
                    }
                    else {
                        ChannelBuffer content = m.getContent();
                        content = ChannelBuffers.wrappedBuffer(this.encode(content), this.finishEncode());
                        m.setContent(content);
                        if (m.headers().contains("Content-Length")) {
                            m.headers().set("Content-Length", Integer.toString(content.readableBytes()));
                        }
                    }
                }
                ctx.sendDownstream(e);
            }
        }
        else if (msg instanceof HttpChunk) {
            final HttpChunk c = (HttpChunk)msg;
            ChannelBuffer content2 = c.getContent();
            if (this.encoder != null) {
                if (!c.isLast()) {
                    content2 = this.encode(content2);
                    if (content2.readable()) {
                        c.setContent(content2);
                        ctx.sendDownstream(e);
                    }
                }
                else {
                    final ChannelBuffer lastProduct = this.finishEncode();
                    if (lastProduct.readable()) {
                        Channels.write(ctx, Channels.succeededFuture(e.getChannel()), new DefaultHttpChunk(lastProduct), e.getRemoteAddress());
                    }
                    ctx.sendDownstream(e);
                }
            }
            else {
                ctx.sendDownstream(e);
            }
        }
        else {
            ctx.sendDownstream(e);
        }
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.finishEncode();
        super.channelClosed(ctx, e);
    }
    
    protected abstract EncoderEmbedder<ChannelBuffer> newContentEncoder(final HttpMessage p0, final String p1) throws Exception;
    
    protected abstract String getTargetContentEncoding(final String p0) throws Exception;
    
    private ChannelBuffer encode(final ChannelBuffer buf) {
        this.offerred = true;
        this.encoder.offer(buf);
        return ChannelBuffers.wrappedBuffer((ChannelBuffer[])this.encoder.pollAll(new ChannelBuffer[this.encoder.size()]));
    }
    
    private ChannelBuffer finishEncode() {
        if (this.encoder == null) {
            this.offerred = false;
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (!this.offerred) {
            this.offerred = false;
            this.encoder.offer(ChannelBuffers.EMPTY_BUFFER);
        }
        ChannelBuffer result;
        if (this.encoder.finish()) {
            result = ChannelBuffers.wrappedBuffer((ChannelBuffer[])this.encoder.pollAll(new ChannelBuffer[this.encoder.size()]));
        }
        else {
            result = ChannelBuffers.EMPTY_BUFFER;
        }
        this.encoder = null;
        return result;
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
        this.finishEncode();
    }
}
