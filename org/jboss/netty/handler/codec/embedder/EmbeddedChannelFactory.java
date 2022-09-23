// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;

final class EmbeddedChannelFactory implements ChannelFactory
{
    static final ChannelFactory INSTANCE;
    
    private EmbeddedChannelFactory() {
    }
    
    public Channel newChannel(final ChannelPipeline pipeline) {
        throw new UnsupportedOperationException();
    }
    
    public void releaseExternalResources() {
    }
    
    public void shutdown() {
    }
    
    static {
        INSTANCE = new EmbeddedChannelFactory();
    }
}
