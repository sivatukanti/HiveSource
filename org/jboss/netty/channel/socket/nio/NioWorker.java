// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.net.SocketAddress;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;

public class NioWorker extends AbstractNioWorker
{
    private final SocketReceiveBufferAllocator recvBufferPool;
    
    public NioWorker(final Executor executor) {
        super(executor);
        this.recvBufferPool = new SocketReceiveBufferAllocator();
    }
    
    public NioWorker(final Executor executor, final ThreadNameDeterminer determiner) {
        super(executor, determiner);
        this.recvBufferPool = new SocketReceiveBufferAllocator();
    }
    
    @Override
    protected boolean read(final SelectionKey k) {
        final SocketChannel ch = (SocketChannel)k.channel();
        final NioSocketChannel channel = (NioSocketChannel)k.attachment();
        final ReceiveBufferSizePredictor predictor = channel.getConfig().getReceiveBufferSizePredictor();
        final int predictedRecvBufSize = predictor.nextReceiveBufferSize();
        final ChannelBufferFactory bufferFactory = channel.getConfig().getBufferFactory();
        int ret = 0;
        int readBytes = 0;
        boolean failure = true;
        final ByteBuffer bb = this.recvBufferPool.get(predictedRecvBufSize).order(bufferFactory.getDefaultOrder());
        try {
            while ((ret = ch.read(bb)) > 0) {
                readBytes += ret;
                if (!bb.hasRemaining()) {
                    break;
                }
            }
            failure = false;
        }
        catch (ClosedChannelException e) {}
        catch (Throwable t) {
            Channels.fireExceptionCaught(channel, t);
        }
        if (readBytes > 0) {
            bb.flip();
            final ChannelBuffer buffer = bufferFactory.getBuffer(readBytes);
            buffer.setBytes(0, bb);
            buffer.writerIndex(readBytes);
            predictor.previousReceiveBufferSize(readBytes);
            Channels.fireMessageReceived(channel, buffer);
        }
        if (ret < 0 || failure) {
            k.cancel();
            this.close(channel, Channels.succeededFuture(channel));
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean scheduleWriteIfNecessary(final AbstractNioChannel<?> channel) {
        final Thread currentThread = Thread.currentThread();
        final Thread workerThread = this.thread;
        if (currentThread != workerThread) {
            if (channel.writeTaskInTaskQueue.compareAndSet(false, true)) {
                this.registerTask(channel.writeTask);
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected Runnable createRegisterTask(final Channel channel, final ChannelFuture future) {
        final boolean server = !(channel instanceof NioClientSocketChannel);
        return new RegisterTask((NioSocketChannel)channel, future, server);
    }
    
    @Override
    public void run() {
        super.run();
        this.recvBufferPool.releaseExternalResources();
    }
    
    private final class RegisterTask implements Runnable
    {
        private final NioSocketChannel channel;
        private final ChannelFuture future;
        private final boolean server;
        
        RegisterTask(final NioSocketChannel channel, final ChannelFuture future, final boolean server) {
            this.channel = channel;
            this.future = future;
            this.server = server;
        }
        
        public void run() {
            final SocketAddress localAddress = this.channel.getLocalAddress();
            final SocketAddress remoteAddress = this.channel.getRemoteAddress();
            if (localAddress == null || remoteAddress == null) {
                if (this.future != null) {
                    this.future.setFailure(new ClosedChannelException());
                }
                NioWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                return;
            }
            try {
                if (this.server) {
                    ((SocketChannel)this.channel.channel).configureBlocking(false);
                }
                ((SocketChannel)this.channel.channel).register(NioWorker.this.selector, this.channel.getInternalInterestOps(), this.channel);
                if (this.future != null) {
                    this.channel.setConnected();
                    this.future.setSuccess();
                }
                if (this.server || !((NioClientSocketChannel)this.channel).boundManually) {
                    Channels.fireChannelBound(this.channel, localAddress);
                }
                Channels.fireChannelConnected(this.channel, remoteAddress);
            }
            catch (IOException e) {
                if (this.future != null) {
                    this.future.setFailure(e);
                }
                NioWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                if (!(e instanceof ClosedChannelException)) {
                    throw new ChannelException("Failed to register a socket to the selector.", e);
                }
            }
        }
    }
}
