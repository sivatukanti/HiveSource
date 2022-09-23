// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.ChannelFutureListener;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;

class NioDatagramPipelineSink extends AbstractNioChannelSink
{
    private final WorkerPool<NioDatagramWorker> workerPool;
    
    NioDatagramPipelineSink(final WorkerPool<NioDatagramWorker> workerPool) {
        this.workerPool = workerPool;
    }
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final NioDatagramChannel channel = (NioDatagramChannel)e.getChannel();
        final ChannelFuture future = e.getFuture();
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent stateEvent = (ChannelStateEvent)e;
            final ChannelState state = stateEvent.getState();
            final Object value = stateEvent.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        channel.worker.close(channel, future);
                        break;
                    }
                    break;
                }
                case BOUND: {
                    if (value != null) {
                        bind(channel, future, (InetSocketAddress)value);
                        break;
                    }
                    channel.worker.close(channel, future);
                    break;
                }
                case CONNECTED: {
                    if (value != null) {
                        connect(channel, future, (InetSocketAddress)value);
                        break;
                    }
                    NioDatagramWorker.disconnect(channel, future);
                    break;
                }
                case INTEREST_OPS: {
                    channel.worker.setInterestOps(channel, future, (int)value);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            final MessageEvent event = (MessageEvent)e;
            final boolean offered = channel.writeBufferQueue.offer(event);
            assert offered;
            channel.worker.writeFromUserCode(channel);
        }
    }
    
    private static void close(final NioDatagramChannel channel, final ChannelFuture future) {
        try {
            channel.getDatagramChannel().socket().close();
            if (channel.setClosed()) {
                future.setSuccess();
                if (channel.isBound()) {
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
    
    private static void bind(final NioDatagramChannel channel, final ChannelFuture future, final InetSocketAddress address) {
        boolean bound = false;
        boolean started = false;
        try {
            channel.getDatagramChannel().socket().bind(address);
            bound = true;
            future.setSuccess();
            Channels.fireChannelBound(channel, address);
            channel.worker.register(channel, null);
            started = true;
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
        finally {
            if (!started && bound) {
                close(channel, future);
            }
        }
    }
    
    private static void connect(final NioDatagramChannel channel, final ChannelFuture future, final InetSocketAddress remoteAddress) {
        final boolean bound = channel.isBound();
        boolean connected = false;
        boolean workerStarted = false;
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel.remoteAddress = null;
        try {
            channel.getDatagramChannel().connect(remoteAddress);
            connected = true;
            future.setSuccess();
            if (!bound) {
                Channels.fireChannelBound(channel, channel.getLocalAddress());
            }
            Channels.fireChannelConnected(channel, channel.getRemoteAddress());
            if (!bound) {
                channel.worker.register(channel, future);
            }
            workerStarted = true;
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
        finally {
            if (connected && !workerStarted) {
                channel.worker.close(channel, future);
            }
        }
    }
    
    NioDatagramWorker nextWorker() {
        return this.workerPool.nextWorker();
    }
}
