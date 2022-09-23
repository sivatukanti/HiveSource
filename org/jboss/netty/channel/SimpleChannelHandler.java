// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.InternalLogger;

public class SimpleChannelHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    private static final InternalLogger logger;
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            this.messageReceived(ctx, (MessageEvent)e);
        }
        else if (e instanceof WriteCompletionEvent) {
            final WriteCompletionEvent evt = (WriteCompletionEvent)e;
            this.writeComplete(ctx, evt);
        }
        else if (e instanceof ChildChannelStateEvent) {
            final ChildChannelStateEvent evt2 = (ChildChannelStateEvent)e;
            if (evt2.getChildChannel().isOpen()) {
                this.childChannelOpen(ctx, evt2);
            }
            else {
                this.childChannelClosed(ctx, evt2);
            }
        }
        else if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent evt3 = (ChannelStateEvent)e;
            switch (evt3.getState()) {
                case OPEN: {
                    if (Boolean.TRUE.equals(evt3.getValue())) {
                        this.channelOpen(ctx, evt3);
                        break;
                    }
                    this.channelClosed(ctx, evt3);
                    break;
                }
                case BOUND: {
                    if (evt3.getValue() != null) {
                        this.channelBound(ctx, evt3);
                        break;
                    }
                    this.channelUnbound(ctx, evt3);
                    break;
                }
                case CONNECTED: {
                    if (evt3.getValue() != null) {
                        this.channelConnected(ctx, evt3);
                        break;
                    }
                    this.channelDisconnected(ctx, evt3);
                    break;
                }
                case INTEREST_OPS: {
                    this.channelInterestChanged(ctx, evt3);
                    break;
                }
                default: {
                    ctx.sendUpstream(e);
                    break;
                }
            }
        }
        else if (e instanceof ExceptionEvent) {
            this.exceptionCaught(ctx, (ExceptionEvent)e);
        }
        else {
            ctx.sendUpstream(e);
        }
    }
    
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        if (this == ctx.getPipeline().getLast()) {
            SimpleChannelHandler.logger.warn("EXCEPTION, please implement " + this.getClass().getName() + ".exceptionCaught() for proper handling.", e.getCause());
        }
        ctx.sendUpstream(e);
    }
    
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelBound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelInterestChanged(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void writeComplete(final ChannelHandlerContext ctx, final WriteCompletionEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void childChannelOpen(final ChannelHandlerContext ctx, final ChildChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void childChannelClosed(final ChannelHandlerContext ctx, final ChildChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            this.writeRequested(ctx, (MessageEvent)e);
        }
        else if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent evt = (ChannelStateEvent)e;
            switch (evt.getState()) {
                case OPEN: {
                    if (!Boolean.TRUE.equals(evt.getValue())) {
                        this.closeRequested(ctx, evt);
                        break;
                    }
                    break;
                }
                case BOUND: {
                    if (evt.getValue() != null) {
                        this.bindRequested(ctx, evt);
                        break;
                    }
                    this.unbindRequested(ctx, evt);
                    break;
                }
                case CONNECTED: {
                    if (evt.getValue() != null) {
                        this.connectRequested(ctx, evt);
                        break;
                    }
                    this.disconnectRequested(ctx, evt);
                    break;
                }
                case INTEREST_OPS: {
                    this.setInterestOpsRequested(ctx, evt);
                    break;
                }
                default: {
                    ctx.sendDownstream(e);
                    break;
                }
            }
        }
        else {
            ctx.sendDownstream(e);
        }
    }
    
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void bindRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void connectRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void setInterestOpsRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void disconnectRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void unbindRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    public void closeRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.sendDownstream(e);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SimpleChannelHandler.class.getName());
    }
}
