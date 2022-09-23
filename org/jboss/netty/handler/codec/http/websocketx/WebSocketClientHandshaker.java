// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import java.util.Map;
import java.net.URI;

public abstract class WebSocketClientHandshaker
{
    private final URI webSocketUrl;
    private final WebSocketVersion version;
    private volatile boolean handshakeComplete;
    private final String expectedSubprotocol;
    private volatile String actualSubprotocol;
    protected final Map<String, String> customHeaders;
    private final long maxFramePayloadLength;
    
    protected WebSocketClientHandshaker(final URI webSocketUrl, final WebSocketVersion version, final String subprotocol, final Map<String, String> customHeaders) {
        this(webSocketUrl, version, subprotocol, customHeaders, Long.MAX_VALUE);
    }
    
    protected WebSocketClientHandshaker(final URI webSocketUrl, final WebSocketVersion version, final String subprotocol, final Map<String, String> customHeaders, final long maxFramePayloadLength) {
        this.webSocketUrl = webSocketUrl;
        this.version = version;
        this.expectedSubprotocol = subprotocol;
        this.customHeaders = customHeaders;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }
    
    public URI getWebSocketUrl() {
        return this.webSocketUrl;
    }
    
    public WebSocketVersion getVersion() {
        return this.version;
    }
    
    public long getMaxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }
    
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }
    
    protected void setHandshakeComplete() {
        this.handshakeComplete = true;
    }
    
    public String getExpectedSubprotocol() {
        return this.expectedSubprotocol;
    }
    
    public String getActualSubprotocol() {
        return this.actualSubprotocol;
    }
    
    protected void setActualSubprotocol(final String actualSubprotocol) {
        this.actualSubprotocol = actualSubprotocol;
    }
    
    public abstract ChannelFuture handshake(final Channel p0) throws Exception;
    
    public abstract void finishHandshake(final Channel p0, final HttpResponse p1);
    
    static void replaceDecoder(final Channel channel, final ChannelHandler wsDecoder) {
        final ChannelPipeline p = channel.getPipeline();
        final ChannelHandlerContext httpDecoderCtx = p.getContext(HttpResponseDecoder.class);
        if (httpDecoderCtx == null) {
            throw new IllegalStateException("can't find an HTTP decoder from the pipeline");
        }
        p.addAfter(httpDecoderCtx.getName(), "ws-decoder", wsDecoder);
        p.remove(httpDecoderCtx.getName());
    }
}
