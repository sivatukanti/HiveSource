// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelConfig;
import java.net.NetworkInterface;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import java.net.InetAddress;
import org.jboss.netty.channel.Channels;
import java.net.DatagramSocket;
import org.jboss.netty.channel.socket.DefaultDatagramChannelConfig;
import java.net.SocketException;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannelConfig;
import java.net.MulticastSocket;
import org.jboss.netty.channel.socket.DatagramChannel;

final class OioDatagramChannel extends AbstractOioChannel implements DatagramChannel
{
    final MulticastSocket socket;
    private final DatagramChannelConfig config;
    
    OioDatagramChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(null, factory, pipeline, sink);
        try {
            this.socket = new MulticastSocket((SocketAddress)null);
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a datagram socket.", e);
        }
        try {
            this.socket.setSoTimeout(10);
            this.socket.setBroadcast(false);
        }
        catch (SocketException e2) {
            throw new ChannelException("Failed to configure the datagram socket timeout.", e2);
        }
        this.config = new DefaultDatagramChannelConfig(this.socket);
        Channels.fireChannelOpen(this);
    }
    
    public DatagramChannelConfig getConfig() {
        return this.config;
    }
    
    public ChannelFuture joinGroup(final InetAddress multicastAddress) {
        this.ensureBound();
        try {
            this.socket.joinGroup(multicastAddress);
            return Channels.succeededFuture(this);
        }
        catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
    public ChannelFuture joinGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        this.ensureBound();
        try {
            this.socket.joinGroup(multicastAddress, networkInterface);
            return Channels.succeededFuture(this);
        }
        catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
    private void ensureBound() {
        if (!this.isBound()) {
            throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
        }
    }
    
    public ChannelFuture leaveGroup(final InetAddress multicastAddress) {
        try {
            this.socket.leaveGroup(multicastAddress);
            return Channels.succeededFuture(this);
        }
        catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
    public ChannelFuture leaveGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface);
            return Channels.succeededFuture(this);
        }
        catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
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
    void closeSocket() {
        this.socket.close();
    }
    
    @Override
    boolean isSocketClosed() {
        return this.socket.isClosed();
    }
}
