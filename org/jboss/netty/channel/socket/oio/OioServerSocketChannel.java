// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelConfig;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.DefaultServerSocketChannelConfig;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import java.util.concurrent.locks.ReentrantLock;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelConfig;
import java.util.concurrent.locks.Lock;
import java.net.ServerSocket;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.AbstractServerChannel;

class OioServerSocketChannel extends AbstractServerChannel implements ServerSocketChannel
{
    private static final InternalLogger logger;
    final ServerSocket socket;
    final Lock shutdownLock;
    private final ServerSocketChannelConfig config;
    
    OioServerSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(factory, pipeline, sink);
        this.shutdownLock = new ReentrantLock();
        try {
            this.socket = new ServerSocket();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a server socket.", e);
        }
        try {
            this.socket.setSoTimeout(1000);
        }
        catch (IOException e) {
            try {
                this.socket.close();
            }
            catch (IOException e2) {
                if (OioServerSocketChannel.logger.isWarnEnabled()) {
                    OioServerSocketChannel.logger.warn("Failed to close a partially initialized socket.", e2);
                }
            }
            throw new ChannelException("Failed to set the server socket timeout.", e);
        }
        this.config = new DefaultServerSocketChannelConfig(this.socket);
        Channels.fireChannelOpen(this);
    }
    
    public ServerSocketChannelConfig getConfig() {
        return this.config;
    }
    
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)this.socket.getLocalSocketAddress();
    }
    
    public InetSocketAddress getRemoteAddress() {
        return null;
    }
    
    public boolean isBound() {
        return this.isOpen() && this.socket.isBound();
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
    }
}
