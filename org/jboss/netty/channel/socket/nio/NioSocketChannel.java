// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.socket.SocketChannelConfig;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.Channel;
import java.nio.channels.SocketChannel;

public class NioSocketChannel extends AbstractNioChannel<java.nio.channels.SocketChannel> implements SocketChannel
{
    private static final int ST_OPEN = 0;
    private static final int ST_BOUND = 1;
    private static final int ST_CONNECTED = 2;
    private static final int ST_CLOSED = -1;
    volatile int state;
    private final NioSocketChannelConfig config;
    
    public NioSocketChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final java.nio.channels.SocketChannel socket, final NioWorker worker) {
        super(parent, factory, pipeline, sink, worker, socket);
        this.state = 0;
        this.config = new DefaultNioSocketChannelConfig(socket.socket());
    }
    
    @Override
    public NioWorker getWorker() {
        return (NioWorker)super.getWorker();
    }
    
    @Override
    public NioSocketChannelConfig getConfig() {
        return this.config;
    }
    
    @Override
    public boolean isOpen() {
        return this.state >= 0;
    }
    
    public boolean isBound() {
        return this.state >= 1;
    }
    
    public boolean isConnected() {
        return this.state == 2;
    }
    
    final void setBound() {
        assert this.state == 0 : "Invalid state: " + this.state;
        this.state = 1;
    }
    
    final void setConnected() {
        if (this.state != -1) {
            this.state = 2;
        }
    }
    
    @Override
    protected boolean setClosed() {
        if (super.setClosed()) {
            this.state = -1;
            return true;
        }
        return false;
    }
    
    @Override
    InetSocketAddress getLocalSocketAddress() throws Exception {
        return (InetSocketAddress)((java.nio.channels.SocketChannel)this.channel).socket().getLocalSocketAddress();
    }
    
    @Override
    InetSocketAddress getRemoteSocketAddress() throws Exception {
        return (InetSocketAddress)((java.nio.channels.SocketChannel)this.channel).socket().getRemoteSocketAddress();
    }
    
    @Override
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(this.getRemoteAddress())) {
            return super.write(message, null);
        }
        return this.getUnsupportedOperationFuture();
    }
}
