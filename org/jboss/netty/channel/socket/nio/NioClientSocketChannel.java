// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import java.nio.channels.SocketChannel;
import org.jboss.netty.util.Timeout;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.logging.InternalLogger;

final class NioClientSocketChannel extends NioSocketChannel
{
    private static final InternalLogger logger;
    volatile ChannelFuture connectFuture;
    volatile boolean boundManually;
    long connectDeadlineNanos;
    volatile SocketAddress requestedRemoteAddress;
    volatile Timeout timoutTimer;
    
    private static java.nio.channels.SocketChannel newSocket() {
        java.nio.channels.SocketChannel socket;
        try {
            socket = java.nio.channels.SocketChannel.open();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
        boolean success = false;
        try {
            socket.configureBlocking(false);
            success = true;
        }
        catch (IOException e2) {
            throw new ChannelException("Failed to enter non-blocking mode.", e2);
        }
        finally {
            if (!success) {
                try {
                    socket.close();
                }
                catch (IOException e3) {
                    if (NioClientSocketChannel.logger.isWarnEnabled()) {
                        NioClientSocketChannel.logger.warn("Failed to close a partially initialized socket.", e3);
                    }
                }
            }
        }
        return socket;
    }
    
    NioClientSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final NioWorker worker) {
        super(null, factory, pipeline, sink, newSocket(), worker);
        Channels.fireChannelOpen(this);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NioClientSocketChannel.class);
    }
}
