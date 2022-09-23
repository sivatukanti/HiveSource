// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;

class NioServerSocketPipelineSink extends AbstractNioChannelSink
{
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final Channel channel = e.getChannel();
        if (channel instanceof NioServerSocketChannel) {
            handleServerSocket(e);
        }
        else if (channel instanceof NioSocketChannel) {
            handleAcceptedSocket(e);
        }
    }
    
    private static void handleServerSocket(final ChannelEvent e) {
        if (!(e instanceof ChannelStateEvent)) {
            return;
        }
        final ChannelStateEvent event = (ChannelStateEvent)e;
        final NioServerSocketChannel channel = (NioServerSocketChannel)event.getChannel();
        final ChannelFuture future = event.getFuture();
        final ChannelState state = event.getState();
        final Object value = event.getValue();
        switch (state) {
            case OPEN: {
                if (Boolean.FALSE.equals(value)) {
                    ((NioServerBoss)channel.boss).close(channel, future);
                    break;
                }
                break;
            }
            case BOUND: {
                if (value != null) {
                    ((NioServerBoss)channel.boss).bind(channel, future, (SocketAddress)value);
                    break;
                }
                ((NioServerBoss)channel.boss).close(channel, future);
                break;
            }
        }
    }
    
    private static void handleAcceptedSocket(final ChannelEvent e) {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent event = (ChannelStateEvent)e;
            final NioSocketChannel channel = (NioSocketChannel)event.getChannel();
            final ChannelFuture future = event.getFuture();
            final ChannelState state = event.getState();
            final Object value = event.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        channel.worker.close(channel, future);
                        break;
                    }
                    break;
                }
                case BOUND:
                case CONNECTED: {
                    if (value == null) {
                        channel.worker.close(channel, future);
                        break;
                    }
                    break;
                }
                case INTEREST_OPS: {
                    channel.worker.setInterestOps(channel, future, (int)value);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            final MessageEvent event2 = (MessageEvent)e;
            final NioSocketChannel channel = (NioSocketChannel)event2.getChannel();
            final boolean offered = channel.writeBufferQueue.offer(event2);
            assert offered;
            channel.worker.writeFromUserCode(channel);
        }
    }
}
