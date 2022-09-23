// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.IOException;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;
import org.jboss.netty.logging.InternalLogger;

class OioServerSocketPipelineSink extends AbstractOioChannelSink
{
    static final InternalLogger logger;
    final Executor workerExecutor;
    private final ThreadNameDeterminer determiner;
    
    OioServerSocketPipelineSink(final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        this.workerExecutor = workerExecutor;
        this.determiner = determiner;
    }
    
    public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) throws Exception {
        final Channel channel = e.getChannel();
        if (channel instanceof OioServerSocketChannel) {
            this.handleServerSocket(e);
        }
        else if (channel instanceof OioAcceptedSocketChannel) {
            handleAcceptedSocket(e);
        }
    }
    
    private void handleServerSocket(final ChannelEvent e) {
        if (!(e instanceof ChannelStateEvent)) {
            return;
        }
        final ChannelStateEvent event = (ChannelStateEvent)e;
        final OioServerSocketChannel channel = (OioServerSocketChannel)event.getChannel();
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
                    this.bind(channel, future, (SocketAddress)value);
                    break;
                }
                close(channel, future);
                break;
            }
        }
    }
    
    private static void handleAcceptedSocket(final ChannelEvent e) {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent event = (ChannelStateEvent)e;
            final OioAcceptedSocketChannel channel = (OioAcceptedSocketChannel)event.getChannel();
            final ChannelFuture future = event.getFuture();
            final ChannelState state = event.getState();
            final Object value = event.getValue();
            switch (state) {
                case OPEN: {
                    if (Boolean.FALSE.equals(value)) {
                        AbstractOioWorker.close(channel, future);
                        break;
                    }
                    break;
                }
                case BOUND:
                case CONNECTED: {
                    if (value == null) {
                        AbstractOioWorker.close(channel, future);
                        break;
                    }
                    break;
                }
                case INTEREST_OPS: {
                    AbstractOioWorker.setInterestOps(channel, future, (int)value);
                    break;
                }
            }
        }
        else if (e instanceof MessageEvent) {
            final MessageEvent event2 = (MessageEvent)e;
            final OioSocketChannel channel2 = (OioSocketChannel)event2.getChannel();
            final ChannelFuture future = event2.getFuture();
            final Object message = event2.getMessage();
            OioWorker.write(channel2, future, message);
        }
    }
    
    private void bind(final OioServerSocketChannel channel, final ChannelFuture future, SocketAddress localAddress) {
        boolean bound = false;
        boolean bossStarted = false;
        try {
            channel.socket.bind(localAddress, channel.getConfig().getBacklog());
            bound = true;
            future.setSuccess();
            localAddress = channel.getLocalAddress();
            Channels.fireChannelBound(channel, localAddress);
            final Executor bossExecutor = ((OioServerSocketChannelFactory)channel.getFactory()).bossExecutor;
            DeadLockProofWorker.start(bossExecutor, new ThreadRenamingRunnable(new Boss(channel), "Old I/O server boss (" + channel + ')', this.determiner));
            bossStarted = true;
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
        finally {
            if (!bossStarted && bound) {
                close(channel, future);
            }
        }
    }
    
    private static void close(final OioServerSocketChannel channel, final ChannelFuture future) {
        final boolean bound = channel.isBound();
        try {
            channel.socket.close();
            channel.shutdownLock.lock();
            try {
                if (channel.setClosed()) {
                    future.setSuccess();
                    if (bound) {
                        Channels.fireChannelUnbound(channel);
                    }
                    Channels.fireChannelClosed(channel);
                }
                else {
                    future.setSuccess();
                }
            }
            finally {
                channel.shutdownLock.unlock();
            }
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(OioServerSocketPipelineSink.class);
    }
    
    private final class Boss implements Runnable
    {
        private final OioServerSocketChannel channel;
        
        Boss(final OioServerSocketChannel channel) {
            this.channel = channel;
        }
        
        public void run() {
            this.channel.shutdownLock.lock();
            try {
                while (this.channel.isBound()) {
                    try {
                        final Socket acceptedSocket = this.channel.socket.accept();
                        try {
                            final ChannelPipeline pipeline = this.channel.getConfig().getPipelineFactory().getPipeline();
                            final OioAcceptedSocketChannel acceptedChannel = new OioAcceptedSocketChannel(this.channel, this.channel.getFactory(), pipeline, OioServerSocketPipelineSink.this, acceptedSocket);
                            DeadLockProofWorker.start(OioServerSocketPipelineSink.this.workerExecutor, new ThreadRenamingRunnable(new OioWorker(acceptedChannel), "Old I/O server worker (parentId: " + this.channel.getId() + ", " + this.channel + ')', OioServerSocketPipelineSink.this.determiner));
                        }
                        catch (Exception e) {
                            if (OioServerSocketPipelineSink.logger.isWarnEnabled()) {
                                OioServerSocketPipelineSink.logger.warn("Failed to initialize an accepted socket.", e);
                            }
                            try {
                                acceptedSocket.close();
                            }
                            catch (IOException e2) {
                                if (!OioServerSocketPipelineSink.logger.isWarnEnabled()) {
                                    continue;
                                }
                                OioServerSocketPipelineSink.logger.warn("Failed to close a partially accepted socket.", e2);
                            }
                        }
                    }
                    catch (SocketTimeoutException e4) {}
                    catch (Throwable e3) {
                        if (!this.channel.socket.isBound() || this.channel.socket.isClosed()) {
                            break;
                        }
                        if (OioServerSocketPipelineSink.logger.isWarnEnabled()) {
                            OioServerSocketPipelineSink.logger.warn("Failed to accept a connection.", e3);
                        }
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex) {}
                    }
                }
            }
            finally {
                this.channel.shutdownLock.unlock();
            }
        }
    }
}
