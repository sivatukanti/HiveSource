// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ServerChannelFactory extends ChannelFactory
{
    ServerChannel newChannel(final ChannelPipeline p0);
}
