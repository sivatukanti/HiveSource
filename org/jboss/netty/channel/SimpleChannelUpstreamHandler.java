// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.List;
import org.jboss.netty.logging.InternalLogger;

public class SimpleChannelUpstreamHandler implements ChannelUpstreamHandler
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
        final ChannelPipeline pipeline = ctx.getPipeline();
        ChannelHandler last = pipeline.getLast();
        if (!(last instanceof ChannelUpstreamHandler) && ctx instanceof DefaultChannelPipeline) {
            final List<String> names = ctx.getPipeline().getNames();
            for (int i = names.size() - 1; i >= 0; --i) {
                final ChannelHandler handler = ctx.getPipeline().get(names.get(i));
                if (handler instanceof ChannelUpstreamHandler) {
                    last = handler;
                    break;
                }
            }
        }
        if (this == last) {
            SimpleChannelUpstreamHandler.logger.warn("EXCEPTION, please implement " + this.getClass().getName() + ".exceptionCaught() for proper handling.", e.getCause());
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
    
    static {
        logger = InternalLoggerFactory.getInstance(SimpleChannelUpstreamHandler.class.getName());
    }
}
