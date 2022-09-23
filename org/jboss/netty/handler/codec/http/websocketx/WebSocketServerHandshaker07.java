// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.logging.InternalLogger;

public class WebSocketServerHandshaker07 extends WebSocketServerHandshaker
{
    private static final InternalLogger logger;
    public static final String WEBSOCKET_07_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private final boolean allowExtensions;
    
    public WebSocketServerHandshaker07(final String webSocketURL, final String subprotocols, final boolean allowExtensions, final long maxFramePayloadLength) {
        super(WebSocketVersion.V07, webSocketURL, subprotocols, maxFramePayloadLength);
        this.allowExtensions = allowExtensions;
    }
    
    @Override
    public ChannelFuture handshake(final Channel channel, final HttpRequest req) {
        if (WebSocketServerHandshaker07.logger.isDebugEnabled()) {
            WebSocketServerHandshaker07.logger.debug(String.format("Channel %s WS Version 7 server handshake", channel.getId()));
        }
        final HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS);
        final String key = req.headers().get("Sec-WebSocket-Key");
        if (key == null) {
            throw new WebSocketHandshakeException("not a WebSocket request: missing key");
        }
        final String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        final ChannelBuffer sha1 = WebSocketUtil.sha1(ChannelBuffers.copiedBuffer(acceptSeed, CharsetUtil.US_ASCII));
        final String accept = WebSocketUtil.base64(sha1);
        if (WebSocketServerHandshaker07.logger.isDebugEnabled()) {
            WebSocketServerHandshaker07.logger.debug(String.format("WS Version 7 Server Handshake key: %s. Response: %s.", key, accept));
        }
        res.setStatus(HttpResponseStatus.SWITCHING_PROTOCOLS);
        res.headers().add("Upgrade", "WebSocket".toLowerCase());
        res.headers().add("Connection", "Upgrade");
        res.headers().add("Sec-WebSocket-Accept", accept);
        final String subprotocols = req.headers().get("Sec-WebSocket-Protocol");
        if (subprotocols != null) {
            final String selectedSubprotocol = this.selectSubprotocol(subprotocols);
            if (selectedSubprotocol == null) {
                throw new WebSocketHandshakeException("Requested subprotocol(s) not supported: " + subprotocols);
            }
            res.headers().add("Sec-WebSocket-Protocol", selectedSubprotocol);
            this.setSelectedSubprotocol(selectedSubprotocol);
        }
        return this.writeHandshakeResponse(channel, res, new WebSocket07FrameEncoder(false), new WebSocket07FrameDecoder(true, this.allowExtensions, this.getMaxFramePayloadLength()));
    }
    
    @Override
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame) {
        final ChannelFuture future = channel.write(frame);
        future.addListener(ChannelFutureListener.CLOSE);
        return future;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker07.class);
    }
}
