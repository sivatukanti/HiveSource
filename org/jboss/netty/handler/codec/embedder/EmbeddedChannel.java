// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.DefaultChannelConfig;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.AbstractChannel;

class EmbeddedChannel extends AbstractChannel
{
    private static final Integer DUMMY_ID;
    private final ChannelConfig config;
    private final SocketAddress localAddress;
    private final SocketAddress remoteAddress;
    
    EmbeddedChannel(final ChannelPipeline pipeline, final ChannelSink sink) {
        super(EmbeddedChannel.DUMMY_ID, null, EmbeddedChannelFactory.INSTANCE, pipeline, sink);
        this.localAddress = new EmbeddedSocketAddress();
        this.remoteAddress = new EmbeddedSocketAddress();
        this.config = new DefaultChannelConfig();
    }
    
    public ChannelConfig getConfig() {
        return this.config;
    }
    
    public SocketAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }
    
    public boolean isBound() {
        return true;
    }
    
    public boolean isConnected() {
        return true;
    }
    
    static {
        DUMMY_ID = 0;
    }
}
