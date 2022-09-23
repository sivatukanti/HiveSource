// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class DefaultLocalServerChannelFactory implements LocalServerChannelFactory
{
    private final DefaultChannelGroup group;
    private final ChannelSink sink;
    
    public DefaultLocalServerChannelFactory() {
        this.group = new DefaultChannelGroup();
        this.sink = new LocalServerChannelSink();
    }
    
    public LocalServerChannel newChannel(final ChannelPipeline pipeline) {
        final LocalServerChannel channel = new DefaultLocalServerChannel(this, pipeline, this.sink);
        this.group.add(channel);
        return channel;
    }
    
    public void releaseExternalResources() {
        this.group.close().awaitUninterruptibly();
    }
    
    public void shutdown() {
    }
}
