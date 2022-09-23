// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.ClosedChannelException;
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Selector;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.nio.channels.SelectionKey;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;

public final class NioServerBoss extends AbstractNioSelector implements Boss
{
    NioServerBoss(final Executor bossExecutor) {
        super(bossExecutor);
    }
    
    NioServerBoss(final Executor bossExecutor, final ThreadNameDeterminer determiner) {
        super(bossExecutor, determiner);
    }
    
    void bind(final NioServerSocketChannel channel, final ChannelFuture future, final SocketAddress localAddress) {
        this.registerTask(new RegisterTask(channel, future, localAddress));
    }
    
    @Override
    protected void close(final SelectionKey k) {
        final NioServerSocketChannel ch = (NioServerSocketChannel)k.attachment();
        this.close(ch, Channels.succeededFuture(ch));
    }
    
    void close(final NioServerSocketChannel channel, final ChannelFuture future) {
        final boolean bound = channel.isBound();
        try {
            channel.socket.close();
            this.increaseCancelledKeys();
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
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    @Override
    protected void process(final Selector selector) {
        final Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }
        final Iterator<SelectionKey> i = selectedKeys.iterator();
        while (i.hasNext()) {
            final SelectionKey k = i.next();
            i.remove();
            final NioServerSocketChannel channel = (NioServerSocketChannel)k.attachment();
            try {
                while (true) {
                    final SocketChannel acceptedSocket = channel.socket.accept();
                    if (acceptedSocket == null) {
                        break;
                    }
                    registerAcceptedChannel(channel, acceptedSocket, this.thread);
                }
            }
            catch (CancelledKeyException e) {
                k.cancel();
                channel.close();
            }
            catch (SocketTimeoutException e2) {}
            catch (ClosedChannelException e3) {}
            catch (Throwable t) {
                if (NioServerBoss.logger.isWarnEnabled()) {
                    NioServerBoss.logger.warn("Failed to accept a connection.", t);
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex) {}
            }
        }
    }
    
    private static void registerAcceptedChannel(final NioServerSocketChannel parent, final SocketChannel acceptedSocket, final Thread currentThread) {
        try {
            final ChannelSink sink = parent.getPipeline().getSink();
            final ChannelPipeline pipeline = parent.getConfig().getPipelineFactory().getPipeline();
            final NioWorker worker = parent.workerPool.nextWorker();
            worker.register(new NioAcceptedSocketChannel(parent.getFactory(), pipeline, parent, sink, acceptedSocket, worker, currentThread), null);
        }
        catch (Exception e) {
            if (NioServerBoss.logger.isWarnEnabled()) {
                NioServerBoss.logger.warn("Failed to initialize an accepted socket.", e);
            }
            try {
                acceptedSocket.close();
            }
            catch (IOException e2) {
                if (!NioServerBoss.logger.isWarnEnabled()) {
                    return;
                }
                NioServerBoss.logger.warn("Failed to close a partially accepted socket.", e2);
            }
        }
    }
    
    @Override
    protected int select(final Selector selector) throws IOException {
        return selector.select();
    }
    
    @Override
    protected ThreadRenamingRunnable newThreadRenamingRunnable(final int id, final ThreadNameDeterminer determiner) {
        return new ThreadRenamingRunnable(this, "New I/O server boss #" + id, determiner);
    }
    
    @Override
    protected Runnable createRegisterTask(final Channel channel, final ChannelFuture future) {
        return new RegisterTask((NioServerSocketChannel)channel, future, null);
    }
    
    private final class RegisterTask implements Runnable
    {
        private final NioServerSocketChannel channel;
        private final ChannelFuture future;
        private final SocketAddress localAddress;
        
        public RegisterTask(final NioServerSocketChannel channel, final ChannelFuture future, final SocketAddress localAddress) {
            this.channel = channel;
            this.future = future;
            this.localAddress = localAddress;
        }
        
        public void run() {
            boolean bound = false;
            boolean registered = false;
            try {
                this.channel.socket.socket().bind(this.localAddress, this.channel.getConfig().getBacklog());
                bound = true;
                this.future.setSuccess();
                Channels.fireChannelBound(this.channel, this.channel.getLocalAddress());
                this.channel.socket.register(NioServerBoss.this.selector, 16, this.channel);
                registered = true;
            }
            catch (Throwable t) {
                this.future.setFailure(t);
                Channels.fireExceptionCaught(this.channel, t);
            }
            finally {
                if (!registered && bound) {
                    NioServerBoss.this.close(this.channel, this.future);
                }
            }
        }
    }
}
