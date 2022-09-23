// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;

public interface LocalClientChannelFactory extends ChannelFactory
{
    LocalChannel newChannel(final ChannelPipeline p0);
}
