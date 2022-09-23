// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.handler.codec.PrematureChannelClosureException;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class HttpClientCodec implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    final Queue<HttpMethod> queue;
    volatile boolean done;
    private final HttpRequestEncoder encoder;
    private final HttpResponseDecoder decoder;
    private final AtomicLong requestResponseCounter;
    private final boolean failOnMissingResponse;
    
    public HttpClientCodec() {
        this(4096, 8192, 8192, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse) {
        this.queue = new ConcurrentLinkedQueue<HttpMethod>();
        this.encoder = new Encoder();
        this.requestResponseCounter = new AtomicLong(0L);
        this.decoder = new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        this.failOnMissingResponse = failOnMissingResponse;
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.decoder.handleUpstream(ctx, e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.encoder.handleDownstream(ctx, e);
    }
    
    private final class Encoder extends HttpRequestEncoder
    {
        Encoder() {
        }
        
        @Override
        protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
            if (msg instanceof HttpRequest && !HttpClientCodec.this.done) {
                HttpClientCodec.this.queue.offer(((HttpRequest)msg).getMethod());
            }
            final Object obj = super.encode(ctx, channel, msg);
            if (HttpClientCodec.this.failOnMissingResponse) {
                if (msg instanceof HttpRequest && !((HttpRequest)msg).isChunked()) {
                    HttpClientCodec.this.requestResponseCounter.incrementAndGet();
                }
                else if (msg instanceof HttpChunk && ((HttpChunk)msg).isLast()) {
                    HttpClientCodec.this.requestResponseCounter.incrementAndGet();
                }
            }
            return obj;
        }
    }
    
    private final class Decoder extends HttpResponseDecoder
    {
        Decoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        }
        
        @Override
        protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
            if (!HttpClientCodec.this.done) {
                final Object msg = super.decode(ctx, channel, buffer, state);
                if (HttpClientCodec.this.failOnMissingResponse) {
                    this.decrement(msg);
                }
                return msg;
            }
            final int readable = this.actualReadableBytes();
            if (readable == 0) {
                return null;
            }
            return buffer.readBytes(readable);
        }
        
        private void decrement(final Object msg) {
            if (msg == null) {
                return;
            }
            if (msg instanceof HttpMessage && !((HttpMessage)msg).isChunked()) {
                HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }
            else if (msg instanceof HttpChunk && ((HttpChunk)msg).isLast()) {
                HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }
            else if (msg instanceof Object[]) {
                HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }
        }
        
        @Override
        protected boolean isContentAlwaysEmpty(final HttpMessage msg) {
            final int statusCode = ((HttpResponse)msg).getStatus().getCode();
            if (statusCode == 100) {
                return true;
            }
            final HttpMethod method = HttpClientCodec.this.queue.poll();
            final char firstChar = method.getName().charAt(0);
            switch (firstChar) {
                case 'H': {
                    if (HttpMethod.HEAD.equals(method)) {
                        return true;
                    }
                    break;
                }
                case 'C': {
                    if (statusCode == 200 && HttpMethod.CONNECT.equals(method)) {
                        HttpClientCodec.this.done = true;
                        HttpClientCodec.this.queue.clear();
                        return true;
                    }
                    break;
                }
            }
            return super.isContentAlwaysEmpty(msg);
        }
        
        @Override
        public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            super.channelClosed(ctx, e);
            if (HttpClientCodec.this.failOnMissingResponse) {
                final long missingResponses = HttpClientCodec.this.requestResponseCounter.get();
                if (missingResponses > 0L) {
                    throw new PrematureChannelClosureException("Channel closed but still missing " + missingResponses + " response(s)");
                }
            }
        }
    }
}
