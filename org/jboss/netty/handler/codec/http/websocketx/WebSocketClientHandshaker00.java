// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpResponse;
import java.util.Iterator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.buffer.ChannelBuffers;
import java.nio.ByteBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import java.util.Map;
import java.net.URI;
import org.jboss.netty.buffer.ChannelBuffer;

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker
{
    private ChannelBuffer expectedChallengeResponseBytes;
    
    public WebSocketClientHandshaker00(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final Map<String, String> customHeaders) {
        this(webSocketURL, version, subprotocol, customHeaders, Long.MAX_VALUE);
    }
    
    public WebSocketClientHandshaker00(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final Map<String, String> customHeaders, final long maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength);
    }
    
    @Override
    public ChannelFuture handshake(final Channel channel) {
        final int spaces1 = WebSocketUtil.randomNumber(1, 12);
        final int spaces2 = WebSocketUtil.randomNumber(1, 12);
        final int max1 = Integer.MAX_VALUE / spaces1;
        final int max2 = Integer.MAX_VALUE / spaces2;
        final int number1 = WebSocketUtil.randomNumber(0, max1);
        final int number2 = WebSocketUtil.randomNumber(0, max2);
        final int product1 = number1 * spaces1;
        final int product2 = number2 * spaces2;
        String key1 = Integer.toString(product1);
        String key2 = Integer.toString(product2);
        key1 = insertRandomCharacters(key1);
        key2 = insertRandomCharacters(key2);
        key1 = insertSpaces(key1, spaces1);
        key2 = insertSpaces(key2, spaces2);
        final byte[] key3 = WebSocketUtil.randomBytes(8);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(number1);
        final byte[] number1Array = buffer.array();
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(number2);
        final byte[] number2Array = buffer.array();
        final byte[] challenge = new byte[16];
        System.arraycopy(number1Array, 0, challenge, 0, 4);
        System.arraycopy(number2Array, 0, challenge, 4, 4);
        System.arraycopy(key3, 0, challenge, 8, 8);
        this.expectedChallengeResponseBytes = WebSocketUtil.md5(ChannelBuffers.wrappedBuffer(challenge));
        final URI wsURL = this.getWebSocketUrl();
        String path = wsURL.getPath();
        if (wsURL.getQuery() != null && wsURL.getQuery().length() > 0) {
            path = wsURL.getPath() + '?' + wsURL.getQuery();
        }
        if (path == null || path.length() == 0) {
            path = "/";
        }
        final HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        request.headers().add("Upgrade", "WebSocket");
        request.headers().add("Connection", "Upgrade");
        request.headers().add("Host", wsURL.getHost());
        final int wsPort = wsURL.getPort();
        String originValue = "http://" + wsURL.getHost();
        if (wsPort != 80 && wsPort != 443) {
            originValue = originValue + ':' + wsPort;
        }
        request.headers().add("Origin", originValue);
        request.headers().add("Sec-WebSocket-Key1", key1);
        request.headers().add("Sec-WebSocket-Key2", key2);
        final String expectedSubprotocol = this.getExpectedSubprotocol();
        if (expectedSubprotocol != null && expectedSubprotocol.length() != 0) {
            request.headers().add("Sec-WebSocket-Protocol", expectedSubprotocol);
        }
        if (this.customHeaders != null) {
            for (final Map.Entry<String, String> e : this.customHeaders.entrySet()) {
                request.headers().add(e.getKey(), e.getValue());
            }
        }
        request.headers().set("Content-Length", key3.length);
        request.setContent(ChannelBuffers.copiedBuffer(key3));
        final ChannelFuture handshakeFuture = new DefaultChannelFuture(channel, false);
        final ChannelFuture future = channel.write(request);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) {
                final ChannelPipeline p = future.getChannel().getPipeline();
                p.replace(HttpRequestEncoder.class, "ws-encoder", new WebSocket00FrameEncoder());
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
        final HttpResponseStatus status = new HttpResponseStatus(101, "WebSocket Protocol Handshake");
        if (!response.getStatus().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response status: " + response.getStatus());
        }
        final String upgrade = response.headers().get("Upgrade");
        if (!"WebSocket".equals(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }
        final String connection = response.headers().get("Connection");
        if (!"Upgrade".equals(connection)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + connection);
        }
        final ChannelBuffer challenge = response.getContent();
        if (!challenge.equals(this.expectedChallengeResponseBytes)) {
            throw new WebSocketHandshakeException("Invalid challenge");
        }
        final String subprotocol = response.headers().get("Sec-WebSocket-Protocol");
        this.setActualSubprotocol(subprotocol);
        this.setHandshakeComplete();
        WebSocketClientHandshaker.replaceDecoder(channel, new WebSocket00FrameDecoder(this.getMaxFramePayloadLength()));
    }
    
    private static String insertRandomCharacters(String key) {
        final int count = WebSocketUtil.randomNumber(1, 12);
        final char[] randomChars = new char[count];
        for (int randCount = 0; randCount < count; ++randCount) {
            final int rand = (int)(Math.random() * 126.0 + 33.0);
            if ((33 < rand && rand < 47) || (58 < rand && rand < 126)) {
                randomChars[randCount] = (char)rand;
            }
        }
        for (int i = 0; i < count; ++i) {
            final int split = WebSocketUtil.randomNumber(0, key.length());
            final String part1 = key.substring(0, split);
            final String part2 = key.substring(split);
            key = part1 + randomChars[i] + part2;
        }
        return key;
    }
    
    private static String insertSpaces(String key, final int spaces) {
        for (int i = 0; i < spaces; ++i) {
            final int split = WebSocketUtil.randomNumber(1, key.length() - 1);
            final String part1 = key.substring(0, split);
            final String part2 = key.substring(split);
            key = part1 + ' ' + part2;
        }
        return key;
    }
}
