// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultServerChannelConfig;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.AbstractServerChannel;

final class DefaultLocalServerChannel extends AbstractServerChannel implements LocalServerChannel
{
    final ChannelConfig channelConfig;
    final AtomicBoolean bound;
    volatile LocalAddress localAddress;
    
    DefaultLocalServerChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(factory, pipeline, sink);
        this.bound = new AtomicBoolean();
        this.channelConfig = new DefaultServerChannelConfig();
        Channels.fireChannelOpen(this);
    }
    
    public ChannelConfig getConfig() {
        return this.channelConfig;
    }
    
    public boolean isBound() {
        return this.isOpen() && this.bound.get();
    }
    
    public LocalAddress getLocalAddress() {
        return this.isBound() ? this.localAddress : null;
    }
    
    public LocalAddress getRemoteAddress() {
        return null;
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
}
