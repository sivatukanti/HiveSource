// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelConfig;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.DefaultServerSocketChannelConfig;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelConfig;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.AbstractServerChannel;

class NioServerSocketChannel extends AbstractServerChannel implements ServerSocketChannel
{
    private static final InternalLogger logger;
    final java.nio.channels.ServerSocketChannel socket;
    final Boss boss;
    final WorkerPool<NioWorker> workerPool;
    private final ServerSocketChannelConfig config;
    
    NioServerSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final Boss boss, final WorkerPool<NioWorker> workerPool) {
        super(factory, pipeline, sink);
        this.boss = boss;
        this.workerPool = workerPool;
        try {
            this.socket = java.nio.channels.ServerSocketChannel.open();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a server socket.", e);
        }
        try {
            this.socket.configureBlocking(false);
        }
        catch (IOException e) {
            try {
                this.socket.close();
            }
            catch (IOException e2) {
                if (NioServerSocketChannel.logger.isWarnEnabled()) {
                    NioServerSocketChannel.logger.warn("Failed to close a partially initialized socket.", e2);
                }
            }
            throw new ChannelException("Failed to enter non-blocking mode.", e);
        }
        this.config = new DefaultServerSocketChannelConfig(this.socket.socket());
        Channels.fireChannelOpen(this);
    }
    
    public ServerSocketChannelConfig getConfig() {
        return this.config;
    }
    
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)this.socket.socket().getLocalSocketAddress();
    }
    
    public InetSocketAddress getRemoteAddress() {
        return null;
    }
    
    public boolean isBound() {
        return this.isOpen() && this.socket.socket().isBound();
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
    }
}
