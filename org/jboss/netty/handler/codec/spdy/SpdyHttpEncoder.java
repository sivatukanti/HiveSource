// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DownstreamMessageEvent;
import java.util.Iterator;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import java.util.Map;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;

public class SpdyHttpEncoder implements ChannelDownstreamHandler
{
    private final int spdyVersion;
    private volatile int currentStreamId;
    
    public SpdyHttpEncoder(final SpdyVersion spdyVersion) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.spdyVersion = spdyVersion.getVersion();
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }
        final MessageEvent e = (MessageEvent)evt;
        final Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            final HttpRequest httpRequest = (HttpRequest)msg;
            final SpdySynStreamFrame spdySynStreamFrame = this.createSynStreamFrame(httpRequest);
            this.currentStreamId = spdySynStreamFrame.getStreamId();
            final ChannelFuture future = this.getMessageFuture(ctx, e, this.currentStreamId, httpRequest);
            Channels.write(ctx, future, spdySynStreamFrame, e.getRemoteAddress());
        }
        else if (msg instanceof HttpResponse) {
            final HttpResponse httpResponse = (HttpResponse)msg;
            if (httpResponse.headers().contains("X-SPDY-Associated-To-Stream-ID")) {
                final SpdySynStreamFrame spdySynStreamFrame = this.createSynStreamFrame(httpResponse);
                this.currentStreamId = spdySynStreamFrame.getStreamId();
                final ChannelFuture future = this.getMessageFuture(ctx, e, this.currentStreamId, httpResponse);
                Channels.write(ctx, future, spdySynStreamFrame, e.getRemoteAddress());
            }
            else {
                final SpdySynReplyFrame spdySynReplyFrame = this.createSynReplyFrame(httpResponse);
                this.currentStreamId = spdySynReplyFrame.getStreamId();
                final ChannelFuture future = this.getMessageFuture(ctx, e, this.currentStreamId, httpResponse);
                Channels.write(ctx, future, spdySynReplyFrame, e.getRemoteAddress());
            }
        }
        else if (msg instanceof HttpChunk) {
            final HttpChunk chunk = (HttpChunk)msg;
            this.writeChunk(ctx, e.getFuture(), this.currentStreamId, chunk, e.getRemoteAddress());
        }
        else {
            ctx.sendDownstream(evt);
        }
    }
    
    protected void writeChunk(final ChannelHandlerContext ctx, final ChannelFuture future, final int streamId, final HttpChunk chunk, final SocketAddress remoteAddress) {
        if (chunk.isLast()) {
            if (chunk instanceof HttpChunkTrailer) {
                final HttpChunkTrailer trailer = (HttpChunkTrailer)chunk;
                final HttpHeaders trailers = trailer.trailingHeaders();
                if (trailers.isEmpty()) {
                    final SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(streamId);
                    spdyDataFrame.setLast(true);
                    Channels.write(ctx, future, spdyDataFrame, remoteAddress);
                }
                else {
                    final SpdyHeadersFrame spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId);
                    spdyHeadersFrame.setLast(true);
                    for (final Map.Entry<String, String> entry : trailers) {
                        spdyHeadersFrame.headers().add(entry.getKey(), entry.getValue());
                    }
                    Channels.write(ctx, future, spdyHeadersFrame, remoteAddress);
                }
            }
            else {
                final SpdyDataFrame spdyDataFrame2 = new DefaultSpdyDataFrame(streamId);
                spdyDataFrame2.setLast(true);
                Channels.write(ctx, future, spdyDataFrame2, remoteAddress);
            }
        }
        else {
            final SpdyDataFrame[] spdyDataFrames = this.createSpdyDataFrames(streamId, chunk.getContent());
            final ChannelFuture dataFuture = getDataFuture(ctx, future, spdyDataFrames, remoteAddress);
            dataFuture.setSuccess();
        }
    }
    
    private ChannelFuture getMessageFuture(final ChannelHandlerContext ctx, final MessageEvent e, final int streamId, final HttpMessage httpMessage) {
        if (!httpMessage.getContent().readable()) {
            return e.getFuture();
        }
        final SpdyDataFrame[] spdyDataFrames = this.createSpdyDataFrames(streamId, httpMessage.getContent());
        if (spdyDataFrames.length > 0) {
            spdyDataFrames[spdyDataFrames.length - 1].setLast(true);
        }
        return getDataFuture(ctx, e.getFuture(), spdyDataFrames, e.getRemoteAddress());
    }
    
    private static ChannelFuture getDataFuture(final ChannelHandlerContext ctx, ChannelFuture future, final SpdyDataFrame[] spdyDataFrames, final SocketAddress remoteAddress) {
        ChannelFuture dataFuture = future;
        int i = spdyDataFrames.length;
        while (--i >= 0) {
            future = Channels.future(ctx.getChannel());
            future.addListener(new SpdyFrameWriter(ctx, new DownstreamMessageEvent(ctx.getChannel(), dataFuture, spdyDataFrames[i], remoteAddress)));
            dataFuture = future;
        }
        return dataFuture;
    }
    
    private SpdySynStreamFrame createSynStreamFrame(final HttpMessage httpMessage) throws Exception {
        final boolean chunked = httpMessage.isChunked();
        final int streamId = SpdyHttpHeaders.getStreamId(httpMessage);
        final int associatedToStreamId = SpdyHttpHeaders.getAssociatedToStreamId(httpMessage);
        final byte priority = SpdyHttpHeaders.getPriority(httpMessage);
        final String URL = SpdyHttpHeaders.getUrl(httpMessage);
        String scheme = SpdyHttpHeaders.getScheme(httpMessage);
        SpdyHttpHeaders.removeStreamId(httpMessage);
        SpdyHttpHeaders.removeAssociatedToStreamId(httpMessage);
        SpdyHttpHeaders.removePriority(httpMessage);
        SpdyHttpHeaders.removeUrl(httpMessage);
        SpdyHttpHeaders.removeScheme(httpMessage);
        httpMessage.headers().remove("Connection");
        httpMessage.headers().remove("Keep-Alive");
        httpMessage.headers().remove("Proxy-Connection");
        httpMessage.headers().remove("Transfer-Encoding");
        final SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority);
        spdySynStreamFrame.setLast(!chunked && !httpMessage.getContent().readable());
        if (httpMessage instanceof HttpRequest) {
            final HttpRequest httpRequest = (HttpRequest)httpMessage;
            SpdyHeaders.setMethod(this.spdyVersion, spdySynStreamFrame, httpRequest.getMethod());
            SpdyHeaders.setUrl(this.spdyVersion, spdySynStreamFrame, httpRequest.getUri());
            SpdyHeaders.setVersion(this.spdyVersion, spdySynStreamFrame, httpMessage.getProtocolVersion());
        }
        if (httpMessage instanceof HttpResponse) {
            final HttpResponse httpResponse = (HttpResponse)httpMessage;
            SpdyHeaders.setStatus(this.spdyVersion, spdySynStreamFrame, httpResponse.getStatus());
            SpdyHeaders.setUrl(this.spdyVersion, spdySynStreamFrame, URL);
            SpdyHeaders.setVersion(this.spdyVersion, spdySynStreamFrame, httpMessage.getProtocolVersion());
            spdySynStreamFrame.setUnidirectional(true);
        }
        final String host = HttpHeaders.getHost(httpMessage);
        httpMessage.headers().remove("Host");
        SpdyHeaders.setHost(spdySynStreamFrame, host);
        if (scheme == null) {
            scheme = "https";
        }
        SpdyHeaders.setScheme(this.spdyVersion, spdySynStreamFrame, scheme);
        for (final Map.Entry<String, String> entry : httpMessage.headers()) {
            spdySynStreamFrame.headers().add(entry.getKey(), entry.getValue());
        }
        return spdySynStreamFrame;
    }
    
    private SpdySynReplyFrame createSynReplyFrame(final HttpResponse httpResponse) throws Exception {
        final boolean chunked = httpResponse.isChunked();
        final int streamId = SpdyHttpHeaders.getStreamId(httpResponse);
        SpdyHttpHeaders.removeStreamId(httpResponse);
        httpResponse.headers().remove("Connection");
        httpResponse.headers().remove("Keep-Alive");
        httpResponse.headers().remove("Proxy-Connection");
        httpResponse.headers().remove("Transfer-Encoding");
        final SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
        spdySynReplyFrame.setLast(!chunked && !httpResponse.getContent().readable());
        SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame, httpResponse.getStatus());
        SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame, httpResponse.getProtocolVersion());
        for (final Map.Entry<String, String> entry : httpResponse.headers()) {
            spdySynReplyFrame.headers().add(entry.getKey(), entry.getValue());
        }
        return spdySynReplyFrame;
    }
    
    private SpdyDataFrame[] createSpdyDataFrames(final int streamId, final ChannelBuffer content) {
        final int readableBytes = content.readableBytes();
        int count = readableBytes / 16777215;
        if (readableBytes % 16777215 > 0) {
            ++count;
        }
        final SpdyDataFrame[] spdyDataFrames = new SpdyDataFrame[count];
        for (int i = 0; i < count; ++i) {
            final SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(streamId);
            final int dataSize = Math.min(content.readableBytes(), 16777215);
            spdyDataFrame.setData(content.readSlice(dataSize));
            spdyDataFrames[i] = spdyDataFrame;
        }
        return spdyDataFrames;
    }
    
    private static class SpdyFrameWriter implements ChannelFutureListener
    {
        private final ChannelHandlerContext ctx;
        private final MessageEvent e;
        
        SpdyFrameWriter(final ChannelHandlerContext ctx, final MessageEvent e) {
            this.ctx = ctx;
            this.e = e;
        }
        
        public void operationComplete(final ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                this.ctx.sendDownstream(this.e);
            }
            else if (future.isCancelled()) {
                this.e.getFuture().cancel();
            }
            else {
                this.e.getFuture().setFailure(future.getCause());
            }
        }
    }
}
