// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channels;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import org.jboss.netty.util.internal.ThreadLocalBoolean;
import org.jboss.netty.channel.ChannelConfig;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.Channel;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.MessageEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.channel.AbstractChannel;

abstract class AbstractNioChannel<C extends SelectableChannel> extends AbstractChannel
{
    final AbstractNioWorker worker;
    final Object writeLock;
    final Runnable writeTask;
    final AtomicBoolean writeTaskInTaskQueue;
    final WriteRequestQueue writeBufferQueue;
    final AtomicInteger writeBufferSize;
    final AtomicInteger highWaterMarkCounter;
    MessageEvent currentWriteEvent;
    SocketSendBufferPool.SendBuffer currentWriteBuffer;
    boolean inWriteNowLoop;
    boolean writeSuspended;
    private volatile InetSocketAddress localAddress;
    volatile InetSocketAddress remoteAddress;
    final C channel;
    
    protected AbstractNioChannel(final Integer id, final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final AbstractNioWorker worker, final C ch) {
        super(id, parent, factory, pipeline, sink);
        this.writeLock = new Object();
        this.writeTask = new WriteTask();
        this.writeTaskInTaskQueue = new AtomicBoolean();
        this.writeBufferQueue = new WriteRequestQueue();
        this.writeBufferSize = new AtomicInteger();
        this.highWaterMarkCounter = new AtomicInteger();
        this.worker = worker;
        this.channel = ch;
    }
    
    protected AbstractNioChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final AbstractNioWorker worker, final C ch) {
        super(parent, factory, pipeline, sink);
        this.writeLock = new Object();
        this.writeTask = new WriteTask();
        this.writeTaskInTaskQueue = new AtomicBoolean();
        this.writeBufferQueue = new WriteRequestQueue();
        this.writeBufferSize = new AtomicInteger();
        this.highWaterMarkCounter = new AtomicInteger();
        this.worker = worker;
        this.channel = ch;
    }
    
    public AbstractNioWorker getWorker() {
        return this.worker;
    }
    
    public InetSocketAddress getLocalAddress() {
        InetSocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                localAddress = this.getLocalSocketAddress();
                if (localAddress.getAddress().isAnyLocalAddress()) {
                    return localAddress;
                }
                this.localAddress = localAddress;
            }
            catch (Throwable t) {
                return null;
            }
        }
        return localAddress;
    }
    
    public InetSocketAddress getRemoteAddress() {
        InetSocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                remoteAddress = (this.remoteAddress = this.getRemoteSocketAddress());
            }
            catch (Throwable t) {
                return null;
            }
        }
        return remoteAddress;
    }
    
    public abstract NioChannelConfig getConfig();
    
    @Override
    protected int getInternalInterestOps() {
        return super.getInternalInterestOps();
    }
    
    @Override
    protected void setInternalInterestOps(final int interestOps) {
        super.setInternalInterestOps(interestOps);
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    abstract InetSocketAddress getLocalSocketAddress() throws Exception;
    
    abstract InetSocketAddress getRemoteSocketAddress() throws Exception;
    
    final class WriteRequestQueue
    {
        private final ThreadLocalBoolean notifying;
        private final Queue<MessageEvent> queue;
        
        public WriteRequestQueue() {
            this.notifying = new ThreadLocalBoolean();
            this.queue = new ConcurrentLinkedQueue<MessageEvent>();
        }
        
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
        
        public boolean offer(final MessageEvent e) {
            final boolean success = this.queue.offer(e);
            assert success;
            final int messageSize = this.getMessageSize(e);
            final int newWriteBufferSize = AbstractNioChannel.this.writeBufferSize.addAndGet(messageSize);
            final int highWaterMark = AbstractNioChannel.this.getConfig().getWriteBufferHighWaterMark();
            if (newWriteBufferSize >= highWaterMark && newWriteBufferSize - messageSize < highWaterMark) {
                AbstractNioChannel.this.highWaterMarkCounter.incrementAndGet();
                if (AbstractChannel.this.setUnwritable()) {
                    if (AbstractNioWorker.isIoThread(AbstractNioChannel.this)) {
                        if (!this.notifying.get()) {
                            this.notifying.set(Boolean.TRUE);
                            Channels.fireChannelInterestChanged(AbstractNioChannel.this);
                            this.notifying.set(Boolean.FALSE);
                        }
                    }
                    else {
                        AbstractNioChannel.this.worker.executeInIoThread(new Runnable() {
                            public void run() {
                                if (AbstractNioChannel.this.writeBufferSize.get() >= highWaterMark || AbstractChannel.this.setWritable()) {
                                    Channels.fireChannelInterestChanged(AbstractNioChannel.this);
                                }
                            }
                        });
                    }
                }
            }
            return true;
        }
        
        public MessageEvent poll() {
            final MessageEvent e = this.queue.poll();
            if (e != null) {
                final int messageSize = this.getMessageSize(e);
                final int newWriteBufferSize = AbstractNioChannel.this.writeBufferSize.addAndGet(-messageSize);
                final int lowWaterMark = AbstractNioChannel.this.getConfig().getWriteBufferLowWaterMark();
                if ((newWriteBufferSize == 0 || newWriteBufferSize < lowWaterMark) && newWriteBufferSize + messageSize >= lowWaterMark) {
                    AbstractNioChannel.this.highWaterMarkCounter.decrementAndGet();
                    if (AbstractNioChannel.this.isConnected()) {
                        assert AbstractNioWorker.isIoThread(AbstractNioChannel.this);
                        if (AbstractChannel.this.setWritable()) {
                            this.notifying.set(Boolean.TRUE);
                            Channels.fireChannelInterestChanged(AbstractNioChannel.this);
                            this.notifying.set(Boolean.FALSE);
                        }
                    }
                }
            }
            return e;
        }
        
        private int getMessageSize(final MessageEvent e) {
            final Object m = e.getMessage();
            if (m instanceof ChannelBuffer) {
                return ((ChannelBuffer)m).readableBytes();
            }
            return 0;
        }
    }
    
    private final class WriteTask implements Runnable
    {
        WriteTask() {
        }
        
        public void run() {
            AbstractNioChannel.this.writeTaskInTaskQueue.set(false);
            AbstractNioChannel.this.worker.writeFromTaskLoop(AbstractNioChannel.this);
        }
    }
}
