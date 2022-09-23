// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;

public interface ClientSocketChannelFactory extends ChannelFactory
{
    SocketChannel newChannel(final ChannelPipeline p0);
}
