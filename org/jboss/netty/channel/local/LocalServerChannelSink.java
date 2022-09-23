// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import java.net.SocketAddress;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.AbstractChannelSink;

final class LocalServerChannelSink extends AbstractChannelSink
{
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final Channel channel = e.getChannel();
        if (channel instanceof DefaultLocalServerChannel) {
            handleServerChannel(e);
        }
        else if (channel instanceof DefaultLocalChannel) {
            handleAcceptedChannel(e);
        }
    }
    
    private static void handleServerChannel(final ChannelEvent e) {
        if (!(e instanceof ChannelStateEvent)) {
            return;
        }
        final ChannelStateEvent event = (ChannelStateEvent)e;
        final DefaultLocalServerChannel channel = (DefaultLocalServerChannel)event.getChannel();
        final ChannelFuture future = event.getFuture();
        final ChannelState state = event.getState();
        final Object value = event.getValue();
        switch (state) {
            case OPEN: {
                if (Boolean.FALSE.equals(value)) {
                    close(channel, future);
                    break;
                }
                break;
            }
            case BOUND: {
                if (value != null) {
                    bind(channel, future, (LocalAddress)value);
                    break;
                }
                close(channel, future);
                break;
            }
        }
    }
    
    private static void handleAcceptedChannel(final ChannelEvent e) {
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
                case BOUND:
                case CONNECTED: {
                    if (value == null) {
                        channel.closeNow(future);
                        break;
                    }
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
    
    private static void bind(final DefaultLocalServerChannel channel, final ChannelFuture future, final LocalAddress localAddress) {
        try {
            if (!LocalChannelRegistry.register(localAddress, channel)) {
                throw new ChannelException("address already in use: " + localAddress);
            }
            if (!channel.bound.compareAndSet(false, true)) {
                throw new ChannelException("already bound");
            }
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
    
    private static void close(final DefaultLocalServerChannel channel, final ChannelFuture future) {
        try {
            if (channel.setClosed()) {
                future.setSuccess();
                final LocalAddress localAddress = channel.localAddress;
                if (channel.bound.compareAndSet(true, false)) {
                    channel.localAddress = null;
                    LocalChannelRegistry.unregister(localAddress);
                    Channels.fireChannelUnbound(channel);
                }
                Channels.fireChannelClosed(channel);
            }
            else {
                future.setSuccess();
            }
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
}
