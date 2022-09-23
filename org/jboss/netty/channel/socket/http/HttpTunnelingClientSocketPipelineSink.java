// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.http;

import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.AbstractChannelSink;

final class HttpTunnelingClientSocketPipelineSink extends AbstractChannelSink
{
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final HttpTunnelingClientSocketChannel channel = (HttpTunnelingClientSocketChannel)e.getChannel();
        final ChannelFuture future = e.getFuture();
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent stateEvent = (ChannelStateEvent)e;
            final ChannelState state = stateEvent.getState();
            final Object value = stateEvent.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        channel.closeReal(future);
                        break;
                    }
                    break;
                }
                case BOUND: {
                    if (value != null) {
                        channel.bindReal((SocketAddress)value, future);
                        break;
                    }
                    channel.unbindReal(future);
                    break;
                }
                case CONNECTED: {
                    if (value != null) {
                        channel.connectReal((SocketAddress)value, future);
                        break;
                    }
                    channel.closeReal(future);
                    break;
                }
                case INTEREST_OPS: {
                    channel.setInterestOpsReal((int)value, future);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            channel.writeReal((ChannelBuffer)((MessageEvent)e).getMessage(), future);
        }
    }
}
