// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class SimpleChannelDownstreamHandler implements ChannelDownstreamHandler
{
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
}
