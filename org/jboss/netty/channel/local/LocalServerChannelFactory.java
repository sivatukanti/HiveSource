// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;

public interface LocalServerChannelFactory extends ServerChannelFactory
{
    LocalServerChannel newChannel(final ChannelPipeline p0);
}
