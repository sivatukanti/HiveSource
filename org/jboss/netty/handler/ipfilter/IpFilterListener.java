// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;

public interface IpFilterListener
{
    ChannelFuture allowed(final ChannelHandlerContext p0, final ChannelEvent p1, final InetSocketAddress p2);
    
    ChannelFuture refused(final ChannelHandlerContext p0, final ChannelEvent p1, final InetSocketAddress p2);
    
    boolean continues(final ChannelHandlerContext p0, final ChannelEvent p1);
}
