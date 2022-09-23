// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public abstract class IpFilteringHandlerImpl implements ChannelUpstreamHandler, IpFilteringHandler
{
    private IpFilterListener listener;
    
    protected abstract boolean accept(final ChannelHandlerContext p0, final ChannelEvent p1, final InetSocketAddress p2) throws Exception;
    
    protected ChannelFuture handleRefusedChannel(final ChannelHandlerContext ctx, final ChannelEvent e, final InetSocketAddress inetSocketAddress) throws Exception {
        if (this.listener == null) {
            return null;
        }
        return this.listener.refused(ctx, e, inetSocketAddress);
    }
    
    protected ChannelFuture handleAllowedChannel(final ChannelHandlerContext ctx, final ChannelEvent e, final InetSocketAddress inetSocketAddress) throws Exception {
        if (this.listener == null) {
            return null;
        }
        return this.listener.allowed(ctx, e, inetSocketAddress);
    }
    
    protected boolean isBlocked(final ChannelHandlerContext ctx) {
        return ctx.getAttachment() != null;
    }
    
    protected boolean continues(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        return this.listener != null && this.listener.continues(ctx, e);
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent evt = (ChannelStateEvent)e;
            switch (evt.getState()) {
                case OPEN:
                case BOUND: {
                    if (evt.getValue() != Boolean.TRUE) {
                        ctx.sendUpstream(e);
                        return;
                    }
                    if (this.isBlocked(ctx) && !this.continues(ctx, evt)) {
                        return;
                    }
                    ctx.sendUpstream(e);
                    return;
                }
                case CONNECTED: {
                    if (evt.getValue() != null) {
                        final InetSocketAddress inetSocketAddress = (InetSocketAddress)e.getChannel().getRemoteAddress();
                        if (!this.accept(ctx, e, inetSocketAddress)) {
                            ctx.setAttachment(Boolean.TRUE);
                            final ChannelFuture future = this.handleRefusedChannel(ctx, e, inetSocketAddress);
                            if (future != null) {
                                future.addListener(ChannelFutureListener.CLOSE);
                            }
                            else {
                                Channels.close(e.getChannel());
                            }
                            if (this.isBlocked(ctx) && !this.continues(ctx, evt)) {
                                return;
                            }
                        }
                        else {
                            this.handleAllowedChannel(ctx, e, inetSocketAddress);
                        }
                        ctx.setAttachment(null);
                        break;
                    }
                    if (this.isBlocked(ctx) && !this.continues(ctx, evt)) {
                        return;
                    }
                    break;
                }
            }
        }
        if (this.isBlocked(ctx) && !this.continues(ctx, e)) {
            return;
        }
        ctx.sendUpstream(e);
    }
    
    public void setIpFilterListener(final IpFilterListener listener) {
        this.listener = listener;
    }
    
    public void removeIpFilterListener() {
        this.listener = null;
    }
}
