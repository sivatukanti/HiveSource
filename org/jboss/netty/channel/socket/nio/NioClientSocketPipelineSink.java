// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.nio.channels.SocketChannel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.logging.InternalLogger;

class NioClientSocketPipelineSink extends AbstractNioChannelSink
{
    static final InternalLogger logger;
    private final BossPool<NioClientBoss> bossPool;
    
    NioClientSocketPipelineSink(final BossPool<NioClientBoss> bossPool) {
        this.bossPool = bossPool;
    }
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent event = (ChannelStateEvent)e;
            final NioClientSocketChannel channel = (NioClientSocketChannel)event.getChannel();
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
                case BOUND: {
                    if (value != null) {
                        bind(channel, future, (SocketAddress)value);
                        break;
                    }
                    channel.worker.close(channel, future);
                    break;
                }
                case CONNECTED: {
                    if (value != null) {
                        this.connect(channel, future, (SocketAddress)value);
                        break;
                    }
                    channel.worker.close(channel, future);
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
            final NioSocketChannel channel2 = (NioSocketChannel)event2.getChannel();
            final boolean offered = channel2.writeBufferQueue.offer(event2);
            assert offered;
            channel2.worker.writeFromUserCode(channel2);
        }
    }
    
    private static void bind(final NioClientSocketChannel channel, final ChannelFuture future, final SocketAddress localAddress) {
        try {
            ((SocketChannel)channel.channel).socket().bind(localAddress);
            channel.boundManually = true;
            channel.setBound();
            future.setSuccess();
            Channels.fireChannelBound(channel, channel.getLocalAddress());
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    private void connect(final NioClientSocketChannel channel, final ChannelFuture cf, final SocketAddress remoteAddress) {
        channel.requestedRemoteAddress = remoteAddress;
        try {
            if (((SocketChannel)channel.channel).connect(remoteAddress)) {
                channel.worker.register(channel, cf);
            }
            else {
                channel.getCloseFuture().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture f) throws Exception {
                        if (!cf.isDone()) {
                            cf.setFailure(new ClosedChannelException());
                        }
                    }
                });
                cf.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                channel.connectFuture = cf;
                this.nextBoss().register(channel, cf);
            }
        }
        catch (Throwable t) {
            if (t instanceof ConnectException) {
                final Throwable newT = new ConnectException(t.getMessage() + ": " + remoteAddress);
                newT.setStackTrace(t.getStackTrace());
                t = newT;
            }
            cf.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
            channel.worker.close(channel, Channels.succeededFuture(channel));
        }
    }
    
    private NioClientBoss nextBoss() {
        return this.bossPool.nextBoss();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NioClientSocketPipelineSink.class);
    }
}
