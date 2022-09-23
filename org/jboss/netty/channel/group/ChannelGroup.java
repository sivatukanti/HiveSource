// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import java.util.Set;

public interface ChannelGroup extends Set<Channel>, Comparable<ChannelGroup>
{
    String getName();
    
    Channel find(final Integer p0);
    
    ChannelGroupFuture setInterestOps(final int p0);
    
    ChannelGroupFuture setReadable(final boolean p0);
    
    ChannelGroupFuture write(final Object p0);
    
    ChannelGroupFuture write(final Object p0, final SocketAddress p1);
    
    ChannelGroupFuture disconnect();
    
    ChannelGroupFuture unbind();
    
    ChannelGroupFuture close();
}
