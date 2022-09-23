// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;

public interface NioSelector extends Runnable
{
    void register(final Channel p0, final ChannelFuture p1);
    
    void rebuildSelector();
    
    void shutdown();
}
