// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelConfig;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import org.jboss.netty.channel.socket.DefaultSocketChannelConfig;
import java.net.SocketException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.SocketChannelConfig;
import java.net.Socket;
import org.jboss.netty.channel.socket.SocketChannel;

abstract class OioSocketChannel extends AbstractOioChannel implements SocketChannel
{
    final Socket socket;
    private final SocketChannelConfig config;
    
    OioSocketChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final Socket socket) {
        super(parent, factory, pipeline, sink);
        this.socket = socket;
        try {
            socket.setSoTimeout(1000);
        }
        catch (SocketException e) {
            throw new ChannelException("Failed to configure the OioSocketChannel socket timeout.", e);
        }
        this.config = new DefaultSocketChannelConfig(socket);
    }
    
    public SocketChannelConfig getConfig() {
        return this.config;
    }
    
    abstract PushbackInputStream getInputStream();
    
    abstract OutputStream getOutputStream();
    
    @Override
    boolean isSocketBound() {
        return this.socket.isBound();
    }
    
    @Override
    boolean isSocketConnected() {
        return this.socket.isConnected();
    }
    
    @Override
    InetSocketAddress getLocalSocketAddress() throws Exception {
        return (InetSocketAddress)this.socket.getLocalSocketAddress();
    }
    
    @Override
    InetSocketAddress getRemoteSocketAddress() throws Exception {
        return (InetSocketAddress)this.socket.getRemoteSocketAddress();
    }
    
    @Override
    void closeSocket() throws IOException {
        this.socket.close();
    }
    
    @Override
    boolean isSocketClosed() {
        return this.socket.isClosed();
    }
}
