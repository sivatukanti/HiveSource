// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import java.net.NetworkInterface;
import org.jboss.netty.channel.ChannelFuture;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.Channel;

public interface DatagramChannel extends Channel
{
    DatagramChannelConfig getConfig();
    
    InetSocketAddress getLocalAddress();
    
    InetSocketAddress getRemoteAddress();
    
    ChannelFuture joinGroup(final InetAddress p0);
    
    ChannelFuture joinGroup(final InetSocketAddress p0, final NetworkInterface p1);
    
    ChannelFuture leaveGroup(final InetAddress p0);
    
    ChannelFuture leaveGroup(final InetSocketAddress p0, final NetworkInterface p1);
}
