// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.Channel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.channel.ChannelFutureListener;

public abstract class WebSocketServerHandshaker
{
    public static final String SUB_PROTOCOL_WILDCARD = "*";
    private final String webSocketUrl;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final long maxFramePayloadLength;
    private String selectedSubprotocol;
    public static final ChannelFutureListener HANDSHAKE_LISTENER;
    
    protected WebSocketServerHandshaker(final WebSocketVersion version, final String webSocketUrl, final String subprotocols) {
        this(version, webSocketUrl, subprotocols, Long.MAX_VALUE);
    }
    
    protected WebSocketServerHandshaker(final WebSocketVersion version, final String webSocketUrl, final String subprotocols, final long maxFramePayloadLength) {
        this.version = version;
        this.webSocketUrl = webSocketUrl;
        if (subprotocols != null) {
            final String[] subprotocolArray = StringUtil.split(subprotocols, ',');
            for (int i = 0; i < subprotocolArray.length; ++i) {
                subprotocolArray[i] = subprotocolArray[i].trim();
            }
            this.subprotocols = subprotocolArray;
        }
        else {
            this.subprotocols = new String[0];
        }
        this.maxFramePayloadLength = maxFramePayloadLength;
    }
    
    public String getWebSocketUrl() {
        return this.webSocketUrl;
    }
    
    public Set<String> getSubprotocols() {
        final Set<String> ret = new LinkedHashSet<String>();
        Collections.addAll(ret, this.subprotocols);
        return ret;
    }
    
    public WebSocketVersion getVersion() {
        return this.version;
    }
    
    public long getMaxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }
    
    public abstract ChannelFuture handshake(final Channel p0, final HttpRequest p1);
    
    protected ChannelFuture writeHandshakeResponse(final Channel channel, final HttpResponse res, final ChannelHandler encoder, final ChannelHandler decoder) {
        final ChannelPipeline p = channel.getPipeline();
        if (p.get(HttpChunkAggregator.class) != null) {
            p.remove(HttpChunkAggregator.class);
        }
        final String httpEncoderName = p.getContext(HttpResponseEncoder.class).getName();
        p.addAfter(httpEncoderName, "wsencoder", encoder);
        p.get(HttpRequestDecoder.class).replace("wsdecoder", decoder);
        final ChannelFuture future = channel.write(res);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) {
                p.remove(httpEncoderName);
            }
        });
        return future;
    }
    
    public abstract ChannelFuture close(final Channel p0, final CloseWebSocketFrame p1);
    
    protected String selectSubprotocol(final String requestedSubprotocols) {
        if (requestedSubprotocols == null || this.subprotocols.length == 0) {
            return null;
        }
        final String[] arr$;
        final String[] requestedSubprotocolArray = arr$ = StringUtil.split(requestedSubprotocols, ',');
        for (final String p : arr$) {
            final String requestedSubprotocol = p.trim();
            for (final String supportedSubprotocol : this.subprotocols) {
                if ("*".equals(supportedSubprotocol) || requestedSubprotocol.equals(supportedSubprotocol)) {
                    return requestedSubprotocol;
                }
            }
        }
        return null;
    }
    
    public String getSelectedSubprotocol() {
        return this.selectedSubprotocol;
    }
    
    protected void setSelectedSubprotocol(final String value) {
        this.selectedSubprotocol = value;
    }
    
    static {
        HANDSHAKE_LISTENER = new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Channels.fireExceptionCaught(future.getChannel(), future.getCause());
                }
            }
        };
    }
}
