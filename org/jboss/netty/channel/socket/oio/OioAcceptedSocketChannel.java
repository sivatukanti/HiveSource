// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.net.SocketAddress;
import org.jboss.netty.channel.Channels;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import java.net.Socket;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.Channel;
import java.io.OutputStream;
import java.io.PushbackInputStream;

class OioAcceptedSocketChannel extends OioSocketChannel
{
    private final PushbackInputStream in;
    private final OutputStream out;
    
    OioAcceptedSocketChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final Socket socket) {
        super(parent, factory, pipeline, sink, socket);
        try {
            this.in = new PushbackInputStream(socket.getInputStream(), 1);
        }
        catch (IOException e) {
            throw new ChannelException("Failed to obtain an InputStream.", e);
        }
        try {
            this.out = socket.getOutputStream();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to obtain an OutputStream.", e);
        }
        Channels.fireChannelOpen(this);
        Channels.fireChannelBound(this, this.getLocalAddress());
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
