// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class WebSocketServerProtocolHandshakeHandler extends SimpleChannelUpstreamHandler
{
    private static final InternalLogger logger;
    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    
    public WebSocketServerProtocolHandshakeHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest)e.getMessage();
            if (req.getMethod() != HttpMethod.GET) {
                sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
                return;
            }
            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.getPipeline(), req, this.websocketPath), this.subprotocols, this.allowExtensions);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
            }
            else {
                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.getChannel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            Channels.fireExceptionCaught(ctx, future.getCause());
                        }
                    }
                });
                WebSocketServerProtocolHandler.setHandshaker(ctx, handshaker);
                ctx.getPipeline().replace(this, "WS403Responder", WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
            }
        }
    }
    
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        WebSocketServerProtocolHandshakeHandler.logger.error("Exception Caught", cause);
        ctx.getChannel().close();
    }
    
    private static void sendHttpResponse(final ChannelHandlerContext ctx, final HttpRequest req, final HttpResponse res) {
        final ChannelFuture f = ctx.getChannel().write(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().getCode() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private static String getWebSocketLocation(final ChannelPipeline cp, final HttpRequest req, final String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        return protocol + "://" + req.headers().get("Host") + path;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketServerProtocolHandshakeHandler.class);
    }
}
