// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.Channels;
import java.nio.channels.SocketChannel;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;

final class NioAcceptedSocketChannel extends NioSocketChannel
{
    final Thread bossThread;
    
    NioAcceptedSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final Channel parent, final ChannelSink sink, final java.nio.channels.SocketChannel socket, final NioWorker worker, final Thread bossThread) {
        super(parent, factory, pipeline, sink, socket, worker);
        this.bossThread = bossThread;
        this.setConnected();
        Channels.fireChannelOpen(this);
    }
}
