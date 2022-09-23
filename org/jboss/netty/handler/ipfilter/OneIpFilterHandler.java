// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentHashMap;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class OneIpFilterHandler extends IpFilteringHandlerImpl
{
    private final ConcurrentMap<InetAddress, Boolean> connectedSet;
    
    public OneIpFilterHandler() {
        this.connectedSet = new ConcurrentHashMap<InetAddress, Boolean>();
    }
    
    @Override
    protected boolean accept(final ChannelHandlerContext ctx, final ChannelEvent e, final InetSocketAddress inetSocketAddress) throws Exception {
        final InetAddress inetAddress = inetSocketAddress.getAddress();
        if (this.connectedSet.containsKey(inetAddress)) {
            return false;
        }
        this.connectedSet.put(inetAddress, Boolean.TRUE);
        return true;
    }
    
    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        super.handleUpstream(ctx, e);
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent evt = (ChannelStateEvent)e;
            if (evt.getState() == ChannelState.CONNECTED && evt.getValue() == null && this.isBlocked(ctx)) {
                final InetSocketAddress inetSocketAddress = (InetSocketAddress)e.getChannel().getRemoteAddress();
                this.connectedSet.remove(inetSocketAddress.getAddress());
            }
        }
    }
}
