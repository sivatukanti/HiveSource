// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;

public interface ServerSocketChannelFactory extends ServerChannelFactory
{
    ServerSocketChannel newChannel(final ChannelPipeline p0);
}
