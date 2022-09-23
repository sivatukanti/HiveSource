// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class WebSocketServerProtocolHandler extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler
{
    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    
    public WebSocketServerProtocolHandler(final String websocketPath) {
        this(websocketPath, null, false);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols) {
        this(websocketPath, subprotocols, false);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
        final ChannelPipeline cp = ctx.getPipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            ctx.getPipeline().addBefore(ctx.getName(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.websocketPath, this.subprotocols, this.allowExtensions));
        }
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof WebSocketFrame) {
            final WebSocketFrame frame = (WebSocketFrame)e.getMessage();
            if (frame instanceof CloseWebSocketFrame) {
                final WebSocketServerHandshaker handshaker = getHandshaker(ctx);
                handshaker.close(ctx.getChannel(), (CloseWebSocketFrame)frame);
                return;
            }
            if (frame instanceof PingWebSocketFrame) {
                ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
                return;
            }
        }
        ctx.sendUpstream(e);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof WebSocketHandshakeException) {
            final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            response.setContent(ChannelBuffers.wrappedBuffer(e.getCause().getMessage().getBytes()));
            ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
        }
        else {
            ctx.getChannel().close();
        }
    }
    
    static WebSocketServerHandshaker getHandshaker(final ChannelHandlerContext ctx) {
        return (WebSocketServerHandshaker)ctx.getAttachment();
    }
    
    static void setHandshaker(final ChannelHandlerContext ctx, final WebSocketServerHandshaker handshaker) {
        ctx.setAttachment(handshaker);
    }
    
    static ChannelHandler forbiddenHttpRequestResponder() {
        return new SimpleChannelHandler() {
            @Override
            public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
                if (!(e.getMessage() instanceof WebSocketFrame)) {
                    final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
                    ctx.getChannel().write(response);
                }
                else {
                    ctx.sendUpstream(e);
                }
            }
        };
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
    }
}
