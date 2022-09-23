// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import org.jboss.netty.util.ThreadRenamingRunnable;
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

class OioDatagramPipelineSink extends AbstractOioChannelSink
{
    private final Executor workerExecutor;
    private final ThreadNameDeterminer determiner;
    
    OioDatagramPipelineSink(final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        this.workerExecutor = workerExecutor;
        this.determiner = determiner;
    }
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final OioDatagramChannel channel = (OioDatagramChannel)e.getChannel();
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
                        this.bind(channel, future, (SocketAddress)value);
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
                    OioDatagramWorker.disconnect(channel, future);
                    break;
                }
                case INTEREST_OPS: {
                    AbstractOioWorker.setInterestOps(channel, future, (int)value);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            final MessageEvent evt = (MessageEvent)e;
            OioDatagramWorker.write(channel, future, evt.getMessage(), evt.getRemoteAddress());
        }
    }
    
    private void bind(final OioDatagramChannel channel, final ChannelFuture future, final SocketAddress localAddress) {
        boolean bound = false;
        boolean workerStarted = false;
        try {
            channel.socket.bind(localAddress);
            bound = true;
            future.setSuccess();
            Channels.fireChannelBound(channel, channel.getLocalAddress());
            DeadLockProofWorker.start(this.workerExecutor, new ThreadRenamingRunnable(new OioDatagramWorker(channel), "Old I/O datagram worker (" + channel + ')', this.determiner));
            workerStarted = true;
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
        finally {
            if (bound && !workerStarted) {
                AbstractOioWorker.close(channel, future);
            }
        }
    }
    
    private void connect(final OioDatagramChannel channel, final ChannelFuture future, final SocketAddress remoteAddress) {
        final boolean bound = channel.isBound();
        boolean connected = false;
        boolean workerStarted = false;
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel.remoteAddress = null;
        try {
            channel.socket.connect(remoteAddress);
            connected = true;
            future.setSuccess();
            if (!bound) {
                Channels.fireChannelBound(channel, channel.getLocalAddress());
            }
            Channels.fireChannelConnected(channel, channel.getRemoteAddress());
            final String threadName = "Old I/O datagram worker (" + channel + ')';
            if (!bound) {
                DeadLockProofWorker.start(this.workerExecutor, new ThreadRenamingRunnable(new OioDatagramWorker(channel), threadName, this.determiner));
            }
            else {
                final Thread workerThread = channel.workerThread;
                if (workerThread != null) {
                    try {
                        workerThread.setName(threadName);
                    }
                    catch (SecurityException ex) {}
                }
            }
            workerStarted = true;
        }
        catch (Throwable t) {
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
