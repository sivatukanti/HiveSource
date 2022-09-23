// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.logging.InternalLogger;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker
{
    private static final InternalLogger logger;
    
    public WebSocketServerHandshaker00(final String webSocketURL, final String subprotocols) {
        this(webSocketURL, subprotocols, Long.MAX_VALUE);
    }
    
    public WebSocketServerHandshaker00(final String webSocketURL, final String subprotocols, final long maxFramePayloadLength) {
        super(WebSocketVersion.V00, webSocketURL, subprotocols, maxFramePayloadLength);
    }
    
    @Override
    public ChannelFuture handshake(final Channel channel, final HttpRequest req) {
        if (WebSocketServerHandshaker00.logger.isDebugEnabled()) {
            WebSocketServerHandshaker00.logger.debug(String.format("Channel %s WS Version 00 server handshake", channel.getId()));
        }
        if (!"Upgrade".equalsIgnoreCase(req.headers().get("Connection")) || !"WebSocket".equalsIgnoreCase(req.headers().get("Upgrade"))) {
            throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
        }
        final boolean isHixie76 = req.headers().contains("Sec-WebSocket-Key1") && req.headers().contains("Sec-WebSocket-Key2");
        final HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));
        res.headers().add("Upgrade", "WebSocket");
        res.headers().add("Connection", "Upgrade");
        if (isHixie76) {
            res.headers().add("Sec-WebSocket-Origin", req.headers().get("Origin"));
            res.headers().add("Sec-WebSocket-Location", this.getWebSocketUrl());
            final String subprotocols = req.headers().get("Sec-WebSocket-Protocol");
            if (subprotocols != null) {
                final String selectedSubprotocol = this.selectSubprotocol(subprotocols);
                if (selectedSubprotocol == null) {
                    throw new WebSocketHandshakeException("Requested subprotocol(s) not supported: " + subprotocols);
                }
                res.headers().add("Sec-WebSocket-Protocol", selectedSubprotocol);
                this.setSelectedSubprotocol(selectedSubprotocol);
            }
            final String key1 = req.headers().get("Sec-WebSocket-Key1");
            final String key2 = req.headers().get("Sec-WebSocket-Key2");
            final int a = (int)(Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1.replaceAll("[^ ]", "").length());
            final int b = (int)(Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2.replaceAll("[^ ]", "").length());
            final long c = req.getContent().readLong();
            final ChannelBuffer input = ChannelBuffers.buffer(16);
            input.writeInt(a);
            input.writeInt(b);
            input.writeLong(c);
            res.setContent(WebSocketUtil.md5(input));
        }
        else {
            res.headers().add("WebSocket-Origin", req.headers().get("Origin"));
            res.headers().add("WebSocket-Location", this.getWebSocketUrl());
            final String protocol = req.headers().get("WebSocket-Protocol");
            if (protocol != null) {
                res.headers().add("WebSocket-Protocol", this.selectSubprotocol(protocol));
            }
        }
        return this.writeHandshakeResponse(channel, res, new WebSocket00FrameEncoder(), new WebSocket00FrameDecoder(this.getMaxFramePayloadLength()));
    }
    
    @Override
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame) {
        return channel.write(frame);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker00.class);
    }
}
