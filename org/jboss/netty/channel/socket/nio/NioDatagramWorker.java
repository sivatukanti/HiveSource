// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.MessageEvent;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.WritableByteChannel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.buffer.ChannelBuffer;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;

public class NioDatagramWorker extends AbstractNioWorker
{
    private final SocketReceiveBufferAllocator bufferAllocator;
    
    NioDatagramWorker(final Executor executor) {
        super(executor);
        this.bufferAllocator = new SocketReceiveBufferAllocator();
    }
    
    @Override
    protected boolean read(final SelectionKey key) {
        final NioDatagramChannel channel = (NioDatagramChannel)key.attachment();
        final ReceiveBufferSizePredictor predictor = channel.getConfig().getReceiveBufferSizePredictor();
        final ChannelBufferFactory bufferFactory = channel.getConfig().getBufferFactory();
        final DatagramChannel nioChannel = (DatagramChannel)key.channel();
        final int predictedRecvBufSize = predictor.nextReceiveBufferSize();
        final ByteBuffer byteBuffer = this.bufferAllocator.get(predictedRecvBufSize).order(bufferFactory.getDefaultOrder());
        boolean failure = true;
        SocketAddress remoteAddress = null;
        try {
            remoteAddress = nioChannel.receive(byteBuffer);
            failure = false;
        }
        catch (ClosedChannelException e) {}
        catch (Throwable t) {
            Channels.fireExceptionCaught(channel, t);
        }
        if (remoteAddress != null) {
            byteBuffer.flip();
            final int readBytes = byteBuffer.remaining();
            if (readBytes > 0) {
                predictor.previousReceiveBufferSize(readBytes);
                final ChannelBuffer buffer = bufferFactory.getBuffer(readBytes);
                buffer.setBytes(0, byteBuffer);
                buffer.writerIndex(readBytes);
                predictor.previousReceiveBufferSize(readBytes);
                Channels.fireMessageReceived(channel, buffer, remoteAddress);
            }
        }
        if (failure) {
            key.cancel();
            this.close(channel, Channels.succeededFuture(channel));
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean scheduleWriteIfNecessary(final AbstractNioChannel<?> channel) {
        final Thread workerThread = this.thread;
        if (workerThread == null || Thread.currentThread() != workerThread) {
            if (channel.writeTaskInTaskQueue.compareAndSet(false, true)) {
                this.registerTask(channel.writeTask);
            }
            return true;
        }
        return false;
    }
    
    static void disconnect(final NioDatagramChannel channel, final ChannelFuture future) {
        final boolean connected = channel.isConnected();
        final boolean iothread = AbstractNioWorker.isIoThread(channel);
        try {
            channel.getDatagramChannel().disconnect();
            future.setSuccess();
            if (connected) {
                if (iothread) {
                    Channels.fireChannelDisconnected(channel);
                }
                else {
                    Channels.fireChannelDisconnectedLater(channel);
                }
            }
        }
        catch (Throwable t) {
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught(channel, t);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, t);
            }
        }
    }
    
    @Override
    protected Runnable createRegisterTask(final Channel channel, final ChannelFuture future) {
        return new ChannelRegistionTask((NioDatagramChannel)channel, future);
    }
    
    public void writeFromUserCode(final AbstractNioChannel<?> channel) {
        if (!channel.isBound()) {
            AbstractNioWorker.cleanUpWriteBuffer(channel);
            return;
        }
        if (this.scheduleWriteIfNecessary(channel)) {
            return;
        }
        if (channel.writeSuspended) {
            return;
        }
        if (channel.inWriteNowLoop) {
            return;
        }
        this.write0(channel);
    }
    
    @Override
    protected void write0(final AbstractNioChannel<?> channel) {
        boolean addOpWrite = false;
        boolean removeOpWrite = false;
        long writtenBytes = 0L;
        final SocketSendBufferPool sendBufferPool = this.sendBufferPool;
        final DatagramChannel ch = ((NioDatagramChannel)channel).getDatagramChannel();
        final AbstractNioChannel.WriteRequestQueue writeBuffer = channel.writeBufferQueue;
        final int writeSpinCount = channel.getConfig().getWriteSpinCount();
        synchronized (channel.writeLock) {
            channel.inWriteNowLoop = true;
            while (true) {
                MessageEvent evt = channel.currentWriteEvent;
                SocketSendBufferPool.SendBuffer buf;
                if (evt == null) {
                    if ((channel.currentWriteEvent = (evt = writeBuffer.poll())) == null) {
                        removeOpWrite = true;
                        channel.writeSuspended = false;
                        break;
                    }
                    buf = (channel.currentWriteBuffer = sendBufferPool.acquire(evt.getMessage()));
                }
                else {
                    buf = channel.currentWriteBuffer;
                }
                try {
                    long localWrittenBytes = 0L;
                    final SocketAddress raddr = evt.getRemoteAddress();
                    if (raddr == null) {
                        for (int i = writeSpinCount; i > 0; --i) {
                            localWrittenBytes = buf.transferTo(ch);
                            if (localWrittenBytes != 0L) {
                                writtenBytes += localWrittenBytes;
                                break;
                            }
                            if (buf.finished()) {
                                break;
                            }
                        }
                    }
                    else {
                        for (int i = writeSpinCount; i > 0; --i) {
                            localWrittenBytes = buf.transferTo(ch, raddr);
                            if (localWrittenBytes != 0L) {
                                writtenBytes += localWrittenBytes;
                                break;
                            }
                            if (buf.finished()) {
                                break;
                            }
                        }
                    }
                    if (localWrittenBytes <= 0L && !buf.finished()) {
                        addOpWrite = true;
                        channel.writeSuspended = true;
                        break;
                    }
                    buf.release();
                    final ChannelFuture future = evt.getFuture();
                    channel.currentWriteEvent = null;
                    channel.currentWriteBuffer = null;
                    evt = null;
                    buf = null;
                    future.setSuccess();
                }
                catch (AsynchronousCloseException e) {}
                catch (Throwable t) {
                    buf.release();
                    final ChannelFuture future2 = evt.getFuture();
                    channel.currentWriteEvent = null;
                    channel.currentWriteBuffer = null;
                    buf = null;
                    evt = null;
                    future2.setFailure(t);
                    Channels.fireExceptionCaught(channel, t);
                }
            }
            channel.inWriteNowLoop = false;
            if (addOpWrite) {
                this.setOpWrite(channel);
            }
            else if (removeOpWrite) {
                this.clearOpWrite(channel);
            }
        }
        Channels.fireWriteComplete(channel, writtenBytes);
    }
    
    @Override
    public void run() {
        super.run();
        this.bufferAllocator.releaseExternalResources();
    }
    
    private final class ChannelRegistionTask implements Runnable
    {
        private final NioDatagramChannel channel;
        private final ChannelFuture future;
        
        ChannelRegistionTask(final NioDatagramChannel channel, final ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }
        
        public void run() {
            final SocketAddress localAddress = this.channel.getLocalAddress();
            if (localAddress == null) {
                if (this.future != null) {
                    this.future.setFailure(new ClosedChannelException());
                }
                NioDatagramWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                return;
            }
            try {
                this.channel.getDatagramChannel().register(NioDatagramWorker.this.selector, this.channel.getInternalInterestOps(), this.channel);
                if (this.future != null) {
                    this.future.setSuccess();
                }
            }
            catch (IOException e) {
                if (this.future != null) {
                    this.future.setFailure(e);
                }
                NioDatagramWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                if (!(e instanceof ClosedChannelException)) {
                    throw new ChannelException("Failed to register a socket to the selector.", e);
                }
            }
        }
    }
}
