// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import java.net.Socket;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import java.io.OutputStream;
import java.io.PushbackInputStream;

class OioClientSocketChannel extends OioSocketChannel
{
    volatile PushbackInputStream in;
    volatile OutputStream out;
    
    OioClientSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(null, factory, pipeline, sink, new Socket());
        Channels.fireChannelOpen(this);
    }
    
    @Override
    PushbackInputStream getInputStream() {
        return this.in;
    }
    
    @Override
    OutputStream getOutputStream() {
        return this.out;
    }
}
