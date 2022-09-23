// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Iterator;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import org.jboss.netty.handler.codec.http.HttpMessage;
import java.util.Map;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class SpdyHttpDecoder extends OneToOneDecoder
{
    private final int spdyVersion;
    private final int maxContentLength;
    private final Map<Integer, HttpMessage> messageMap;
    
    public SpdyHttpDecoder(final SpdyVersion spdyVersion, final int maxContentLength) {
        this(spdyVersion, maxContentLength, new HashMap<Integer, HttpMessage>());
    }
    
    protected SpdyHttpDecoder(final SpdyVersion spdyVersion, final int maxContentLength, final Map<Integer, HttpMessage> messageMap) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        if (maxContentLength <= 0) {
            throw new IllegalArgumentException("maxContentLength must be a positive integer: " + maxContentLength);
        }
        this.spdyVersion = spdyVersion.getVersion();
        this.maxContentLength = maxContentLength;
        this.messageMap = messageMap;
    }
    
    protected HttpMessage putMessage(final int streamId, final HttpMessage message) {
        return this.messageMap.put(streamId, message);
    }
    
    protected HttpMessage getMessage(final int streamId) {
        return this.messageMap.get(streamId);
    }
    
    protected HttpMessage removeMessage(final int streamId) {
        return this.messageMap.remove(streamId);
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (msg instanceof SpdySynStreamFrame) {
            final SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            final int streamId = spdySynStreamFrame.getStreamId();
            if (SpdyCodecUtil.isServerId(streamId)) {
                final int associatedToStreamId = spdySynStreamFrame.getAssociatedToStreamId();
                if (associatedToStreamId == 0) {
                    final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INVALID_STREAM);
                    Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame);
                    return null;
                }
                if (spdySynStreamFrame.isLast()) {
                    final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                    Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame);
                    return null;
                }
                if (spdySynStreamFrame.isTruncated()) {
                    final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                    Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame);
                    return null;
                }
                try {
                    final HttpRequest httpRequest = createHttpRequest(this.spdyVersion, spdySynStreamFrame);
                    SpdyHttpHeaders.setStreamId(httpRequest, streamId);
                    SpdyHttpHeaders.setAssociatedToStreamId(httpRequest, associatedToStreamId);
                    SpdyHttpHeaders.setPriority(httpRequest, spdySynStreamFrame.getPriority());
                    return httpRequest;
                }
                catch (Exception e2) {
                    final SpdyRstStreamFrame spdyRstStreamFrame2 = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                    Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame2);
                    return null;
                }
            }
            if (spdySynStreamFrame.isTruncated()) {
                final SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                spdySynReplyFrame.setLast(true);
                SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame, HttpVersion.HTTP_1_0);
                Channels.write(ctx, Channels.future(channel), spdySynReplyFrame);
                return null;
            }
            try {
                final HttpRequest httpRequest2 = createHttpRequest(this.spdyVersion, spdySynStreamFrame);
                SpdyHttpHeaders.setStreamId(httpRequest2, streamId);
                if (spdySynStreamFrame.isLast()) {
                    return httpRequest2;
                }
                this.putMessage(streamId, httpRequest2);
            }
            catch (Exception e3) {
                final SpdySynReplyFrame spdySynReplyFrame2 = new DefaultSpdySynReplyFrame(streamId);
                spdySynReplyFrame2.setLast(true);
                SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame2, HttpResponseStatus.BAD_REQUEST);
                SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame2, HttpVersion.HTTP_1_0);
                Channels.write(ctx, Channels.future(channel), spdySynReplyFrame2);
            }
        }
        else if (msg instanceof SpdySynReplyFrame) {
            final SpdySynReplyFrame spdySynReplyFrame3 = (SpdySynReplyFrame)msg;
            final int streamId = spdySynReplyFrame3.getStreamId();
            if (spdySynReplyFrame3.isTruncated()) {
                final SpdyRstStreamFrame spdyRstStreamFrame3 = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame3);
                return null;
            }
            try {
                final HttpResponse httpResponse = createHttpResponse(this.spdyVersion, spdySynReplyFrame3);
                SpdyHttpHeaders.setStreamId(httpResponse, streamId);
                if (spdySynReplyFrame3.isLast()) {
                    HttpHeaders.setContentLength(httpResponse, 0L);
                    return httpResponse;
                }
                this.putMessage(streamId, httpResponse);
            }
            catch (Exception e3) {
                final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame);
            }
        }
        else if (msg instanceof SpdyHeadersFrame) {
            final SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
            final int streamId = spdyHeadersFrame.getStreamId();
            HttpMessage httpMessage = this.getMessage(streamId);
            if (httpMessage == null) {
                if (SpdyCodecUtil.isServerId(streamId)) {
                    if (spdyHeadersFrame.isTruncated()) {
                        final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                        Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame);
                        return null;
                    }
                    try {
                        httpMessage = createHttpResponse(this.spdyVersion, spdyHeadersFrame);
                        SpdyHttpHeaders.setStreamId(httpMessage, streamId);
                        if (spdyHeadersFrame.isLast()) {
                            HttpHeaders.setContentLength(httpMessage, 0L);
                            return httpMessage;
                        }
                        this.putMessage(streamId, httpMessage);
                    }
                    catch (Exception e2) {
                        final SpdyRstStreamFrame spdyRstStreamFrame2 = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                        Channels.write(ctx, Channels.future(channel), spdyRstStreamFrame2);
                        return null;
                    }
                }
                return null;
            }
            if (!spdyHeadersFrame.isTruncated()) {
                for (final Map.Entry<String, String> e : spdyHeadersFrame.headers()) {
                    httpMessage.headers().add(e.getKey(), e.getValue());
                }
            }
            if (spdyHeadersFrame.isLast()) {
                HttpHeaders.setContentLength(httpMessage, httpMessage.getContent().readableBytes());
                this.removeMessage(streamId);
                return httpMessage;
            }
        }
        else if (msg instanceof SpdyDataFrame) {
            final SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            final int streamId = spdyDataFrame.getStreamId();
            final HttpMessage httpMessage = this.getMessage(streamId);
            if (httpMessage == null) {
                return null;
            }
            ChannelBuffer content = httpMessage.getContent();
            if (content.readableBytes() > this.maxContentLength - spdyDataFrame.getData().readableBytes()) {
                this.removeMessage(streamId);
                throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
            }
            if (content == ChannelBuffers.EMPTY_BUFFER) {
                content = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
                content.writeBytes(spdyDataFrame.getData());
                httpMessage.setContent(content);
            }
            else {
                content.writeBytes(spdyDataFrame.getData());
            }
            if (spdyDataFrame.isLast()) {
                HttpHeaders.setContentLength(httpMessage, content.readableBytes());
                this.removeMessage(streamId);
                return httpMessage;
            }
        }
        else if (msg instanceof SpdyRstStreamFrame) {
            final SpdyRstStreamFrame spdyRstStreamFrame4 = (SpdyRstStreamFrame)msg;
            final int streamId = spdyRstStreamFrame4.getStreamId();
            this.removeMessage(streamId);
        }
        return null;
    }
    
    private static HttpRequest createHttpRequest(final int spdyVersion, final SpdyHeadersFrame requestFrame) throws Exception {
        final HttpMethod method = SpdyHeaders.getMethod(spdyVersion, requestFrame);
        final String url = SpdyHeaders.getUrl(spdyVersion, requestFrame);
        final HttpVersion httpVersion = SpdyHeaders.getVersion(spdyVersion, requestFrame);
        SpdyHeaders.removeMethod(spdyVersion, requestFrame);
        SpdyHeaders.removeUrl(spdyVersion, requestFrame);
        SpdyHeaders.removeVersion(spdyVersion, requestFrame);
        final HttpRequest httpRequest = new DefaultHttpRequest(httpVersion, method, url);
        SpdyHeaders.removeScheme(spdyVersion, requestFrame);
        final String host = SpdyHeaders.getHost(requestFrame);
        SpdyHeaders.removeHost(requestFrame);
        HttpHeaders.setHost(httpRequest, host);
        for (final Map.Entry<String, String> e : requestFrame.headers()) {
            httpRequest.headers().add(e.getKey(), e.getValue());
        }
        HttpHeaders.setKeepAlive(httpRequest, true);
        httpRequest.headers().remove("Transfer-Encoding");
        return httpRequest;
    }
    
    private static HttpResponse createHttpResponse(final int spdyVersion, final SpdyHeadersFrame responseFrame) throws Exception {
        final HttpResponseStatus status = SpdyHeaders.getStatus(spdyVersion, responseFrame);
        final HttpVersion version = SpdyHeaders.getVersion(spdyVersion, responseFrame);
        SpdyHeaders.removeStatus(spdyVersion, responseFrame);
        SpdyHeaders.removeVersion(spdyVersion, responseFrame);
        final HttpResponse httpResponse = new DefaultHttpResponse(version, status);
        for (final Map.Entry<String, String> e : responseFrame.headers()) {
            httpResponse.headers().add(e.getKey(), e.getValue());
        }
        HttpHeaders.setKeepAlive(httpResponse, true);
        httpResponse.headers().remove("Transfer-Encoding");
        httpResponse.headers().remove("Trailer");
        return httpResponse;
    }
}
