// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.io.IOException;
import org.jboss.netty.channel.ChannelSink;
import java.net.ConnectException;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.AbstractChannelSink;

final class LocalClientChannelSink extends AbstractChannelSink
{
    private static final InternalLogger logger;
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent event = (ChannelStateEvent)e;
            final DefaultLocalChannel channel = (DefaultLocalChannel)event.getChannel();
            final ChannelFuture future = event.getFuture();
            final ChannelState state = event.getState();
            final Object value = event.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        channel.closeNow(future);
                        break;
                    }
                    break;
                }
                case BOUND: {
                    if (value != null) {
                        bind(channel, future, (LocalAddress)value);
                        break;
                    }
                    channel.closeNow(future);
                    break;
                }
                case CONNECTED: {
                    if (value != null) {
                        this.connect(channel, future, (LocalAddress)value);
                        break;
                    }
                    channel.closeNow(future);
                    break;
                }
                case INTEREST_OPS: {
                    future.setSuccess();
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            final MessageEvent event2 = (MessageEvent)e;
            final DefaultLocalChannel channel = (DefaultLocalChannel)event2.getChannel();
            final boolean offered = channel.writeBuffer.offer(event2);
            assert offered;
            channel.flushWriteBuffer();
        }
    }
    
    private static void bind(final DefaultLocalChannel channel, final ChannelFuture future, final LocalAddress localAddress) {
        try {
            if (!LocalChannelRegistry.register(localAddress, channel)) {
                throw new ChannelException("address already in use: " + localAddress);
            }
            channel.setBound();
            channel.localAddress = localAddress;
            future.setSuccess();
            Channels.fireChannelBound(channel, localAddress);
        }
        catch (Throwable t) {
            LocalChannelRegistry.unregister(localAddress);
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    private void connect(final DefaultLocalChannel channel, final ChannelFuture future, final LocalAddress remoteAddress) {
        final Channel remoteChannel = LocalChannelRegistry.getChannel(remoteAddress);
        if (!(remoteChannel instanceof DefaultLocalServerChannel)) {
            future.setFailure(new ConnectException("connection refused: " + remoteAddress));
            return;
        }
        final DefaultLocalServerChannel serverChannel = (DefaultLocalServerChannel)remoteChannel;
        ChannelPipeline pipeline;
        try {
            pipeline = serverChannel.getConfig().getPipelineFactory().getPipeline();
        }
        catch (Exception e) {
            future.setFailure(e);
            Channels.fireExceptionCaught(channel, e);
            if (LocalClientChannelSink.logger.isWarnEnabled()) {
                LocalClientChannelSink.logger.warn("Failed to initialize an accepted socket.", e);
            }
            return;
        }
        future.setSuccess();
        final DefaultLocalChannel acceptedChannel = new DefaultLocalChannel(serverChannel, serverChannel.getFactory(), pipeline, this, channel);
        channel.pairedChannel = acceptedChannel;
        if (!channel.isBound()) {
            bind(channel, Channels.succeededFuture(channel), new LocalAddress("ephemeral"));
        }
        channel.remoteAddress = serverChannel.getLocalAddress();
        channel.setConnected();
        Channels.fireChannelConnected(channel, serverChannel.getLocalAddress());
        acceptedChannel.localAddress = serverChannel.getLocalAddress();
        try {
            acceptedChannel.setBound();
        }
        catch (IOException e2) {
            throw new Error(e2);
        }
        Channels.fireChannelBound(acceptedChannel, channel.getRemoteAddress());
        acceptedChannel.remoteAddress = channel.getLocalAddress();
        acceptedChannel.setConnected();
        Channels.fireChannelConnected(acceptedChannel, channel.getLocalAddress());
        channel.flushWriteBuffer();
        acceptedChannel.flushWriteBuffer();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(LocalClientChannelSink.class);
    }
}
