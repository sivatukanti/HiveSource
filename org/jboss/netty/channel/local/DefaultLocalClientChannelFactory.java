// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;

public class DefaultLocalClientChannelFactory implements LocalClientChannelFactory
{
    private final ChannelSink sink;
    
    public DefaultLocalClientChannelFactory() {
        this.sink = new LocalClientChannelSink();
    }
    
    public LocalChannel newChannel(final ChannelPipeline pipeline) {
        return new DefaultLocalChannel(null, this, pipeline, this.sink, null);
    }
    
    public void releaseExternalResources() {
    }
    
    public void shutdown() {
    }
}
