// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.net.ConnectException;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import org.jboss.netty.util.ThreadRenamingRunnable;
import java.io.PushbackInputStream;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;

class OioClientSocketPipelineSink extends AbstractOioChannelSink
{
    private final Executor workerExecutor;
    private final ThreadNameDeterminer determiner;
    
    OioClientSocketPipelineSink(final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        this.workerExecutor = workerExecutor;
        this.determiner = determiner;
    }
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final OioClientSocketChannel channel = (OioClientSocketChannel)e.getChannel();
        final ChannelFuture future = e.getFuture();
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent stateEvent = (ChannelStateEvent)e;
            final ChannelState state = stateEvent.getState();
            final Object value = stateEvent.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        AbstractOioWorker.close(channel, future);
                        break;
                    }
                    break;
                }
                case BOUND: {
                    if (value != null) {
                        bind(channel, future, (SocketAddress)value);
                        break;
                    }
                    AbstractOioWorker.close(channel, future);
                    break;
                }
                case CONNECTED: {
                    if (value != null) {
                        this.connect(channel, future, (SocketAddress)value);
                        break;
                    }
                    AbstractOioWorker.close(channel, future);
                    break;
                }
                case INTEREST_OPS: {
                    AbstractOioWorker.setInterestOps(channel, future, (int)value);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            OioWorker.write(channel, future, ((MessageEvent)e).getMessage());
        }
    }
    
    private static void bind(final OioClientSocketChannel channel, final ChannelFuture future, final SocketAddress localAddress) {
        try {
            channel.socket.bind(localAddress);
            future.setSuccess();
            Channels.fireChannelBound(channel, channel.getLocalAddress());
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    private void connect(final OioClientSocketChannel channel, final ChannelFuture future, final SocketAddress remoteAddress) {
        final boolean bound = channel.isBound();
        boolean connected = false;
        boolean workerStarted = false;
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        try {
            channel.socket.connect(remoteAddress, channel.getConfig().getConnectTimeoutMillis());
            connected = true;
            channel.in = new PushbackInputStream(channel.socket.getInputStream(), 1);
            channel.out = channel.socket.getOutputStream();
            future.setSuccess();
            if (!bound) {
                Channels.fireChannelBound(channel, channel.getLocalAddress());
            }
            Channels.fireChannelConnected(channel, channel.getRemoteAddress());
            DeadLockProofWorker.start(this.workerExecutor, new ThreadRenamingRunnable(new OioWorker(channel), "Old I/O client worker (" + channel + ')', this.determiner));
            workerStarted = true;
        }
        catch (Throwable t) {
            if (t instanceof ConnectException && t instanceof ConnectException) {
                final Throwable newT = new ConnectException(t.getMessage() + ": " + remoteAddress);
                newT.setStackTrace(t.getStackTrace());
                t = newT;
            }
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
        finally {
            if (connected && !workerStarted) {
                AbstractOioWorker.close(channel, future);
            }
        }
    }
}
