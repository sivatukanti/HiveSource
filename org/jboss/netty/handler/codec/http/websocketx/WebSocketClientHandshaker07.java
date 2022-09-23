// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpResponse;
import java.util.Iterator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import java.util.Map;
import java.net.URI;
import org.jboss.netty.logging.InternalLogger;

public class WebSocketClientHandshaker07 extends WebSocketClientHandshaker
{
    private static final InternalLogger logger;
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;
    
    public WebSocketClientHandshaker07(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final Map<String, String> customHeaders, final long maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength);
        this.allowExtensions = allowExtensions;
    }
    
    @Override
    public ChannelFuture handshake(final Channel channel) {
        final URI wsURL = this.getWebSocketUrl();
        String path = wsURL.getPath();
        if (wsURL.getQuery() != null && wsURL.getQuery().length() > 0) {
            path = wsURL.getPath() + '?' + wsURL.getQuery();
        }
        if (path == null || path.length() == 0) {
            path = "/";
        }
        final byte[] nonce = WebSocketUtil.randomBytes(16);
        final String key = WebSocketUtil.base64(ChannelBuffers.wrappedBuffer(nonce));
        final String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        final ChannelBuffer sha1 = WebSocketUtil.sha1(ChannelBuffers.copiedBuffer(acceptSeed, CharsetUtil.US_ASCII));
        this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
        if (WebSocketClientHandshaker07.logger.isDebugEnabled()) {
            WebSocketClientHandshaker07.logger.debug(String.format("WS Version 07 Client Handshake key: %s. Expected response: %s.", key, this.expectedChallengeResponseString));
        }
        final HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        request.headers().add("Upgrade", "WebSocket".toLowerCase());
        request.headers().add("Connection", "Upgrade");
        request.headers().add("Sec-WebSocket-Key", key);
        request.headers().add("Host", wsURL.getHost());
        final int wsPort = wsURL.getPort();
        String originValue = "http://" + wsURL.getHost();
        if (wsPort != 80 && wsPort != 443) {
            originValue = originValue + ':' + wsPort;
        }
        request.headers().add("Sec-WebSocket-Origin", originValue);
        final String expectedSubprotocol = this.getExpectedSubprotocol();
        if (expectedSubprotocol != null && expectedSubprotocol.length() > 0) {
            request.headers().add("Sec-WebSocket-Protocol", expectedSubprotocol);
        }
        request.headers().add("Sec-WebSocket-Version", "7");
        if (this.customHeaders != null) {
            for (final Map.Entry<String, String> e : this.customHeaders.entrySet()) {
                request.headers().add(e.getKey(), e.getValue());
            }
        }
        final ChannelFuture handshakeFuture = new DefaultChannelFuture(channel, false);
        final ChannelFuture future = channel.write(request);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) {
                final ChannelPipeline p = future.getChannel().getPipeline();
                p.addAfter(p.getContext(HttpRequestEncoder.class).getName(), "ws-encoder", new WebSocket07FrameEncoder(true));
                if (future.isSuccess()) {
                    handshakeFuture.setSuccess();
                }
                else {
                    handshakeFuture.setFailure(future.getCause());
                }
            }
        });
        return handshakeFuture;
    }
    
    @Override
    public void finishHandshake(final Channel channel, final HttpResponse response) {
        final HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        if (!response.getStatus().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response status: " + response.getStatus());
        }
        final String upgrade = response.headers().get("Upgrade");
        if (!"WebSocket".equalsIgnoreCase(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + response.headers().get("Upgrade"));
        }
        final String connection = response.headers().get("Connection");
        if (!"Upgrade".equalsIgnoreCase(connection)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + response.headers().get("Connection"));
        }
        final String accept = response.headers().get("Sec-WebSocket-Accept");
        if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
            throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", accept, this.expectedChallengeResponseString));
        }
        final String subprotocol = response.headers().get("Sec-WebSocket-Protocol");
        this.setActualSubprotocol(subprotocol);
        this.setHandshakeComplete();
        WebSocketClientHandshaker.replaceDecoder(channel, new WebSocket07FrameDecoder(false, this.allowExtensions, this.getMaxFramePayloadLength()));
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
    }
}
