// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import java.util.List;
import java.util.ArrayList;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Selector;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.nio.channels.SelectionKey;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.Worker;

abstract class AbstractNioWorker extends AbstractNioSelector implements Worker
{
    protected final SocketSendBufferPool sendBufferPool;
    
    AbstractNioWorker(final Executor executor) {
        super(executor);
        this.sendBufferPool = new SocketSendBufferPool();
    }
    
    AbstractNioWorker(final Executor executor, final ThreadNameDeterminer determiner) {
        super(executor, determiner);
        this.sendBufferPool = new SocketSendBufferPool();
    }
    
    public void executeInIoThread(final Runnable task) {
        this.executeInIoThread(task, false);
    }
    
    public void executeInIoThread(final Runnable task, final boolean alwaysAsync) {
        if (!alwaysAsync && this.isIoThread()) {
            task.run();
        }
        else {
            this.registerTask(task);
        }
    }
    
    @Override
    protected void close(final SelectionKey k) {
        final AbstractNioChannel<?> ch = (AbstractNioChannel<?>)k.attachment();
        this.close(ch, Channels.succeededFuture(ch));
    }
    
    @Override
    protected ThreadRenamingRunnable newThreadRenamingRunnable(final int id, final ThreadNameDeterminer determiner) {
        return new ThreadRenamingRunnable(this, "New I/O worker #" + id, determiner);
    }
    
    @Override
    public void run() {
        super.run();
        this.sendBufferPool.releaseExternalResources();
    }
    
    @Override
    protected void process(final Selector selector) throws IOException {
        final Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }
        final Iterator<SelectionKey> i = selectedKeys.iterator();
        while (i.hasNext()) {
            final SelectionKey k = i.next();
            i.remove();
            try {
                final int readyOps = k.readyOps();
                if (((readyOps & 0x1) != 0x0 || readyOps == 0) && !this.read(k)) {
                    continue;
                }
                if ((readyOps & 0x4) != 0x0) {
                    this.writeFromSelectorLoop(k);
                }
            }
            catch (CancelledKeyException e) {
                this.close(k);
            }
            if (this.cleanUpCancelledKeys()) {
                break;
            }
        }
    }
    
    void writeFromUserCode(final AbstractNioChannel<?> channel) {
        if (!channel.isConnected()) {
            cleanUpWriteBuffer(channel);
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
    
    void writeFromTaskLoop(final AbstractNioChannel<?> ch) {
        if (!ch.writeSuspended) {
            this.write0(ch);
        }
    }
    
    void writeFromSelectorLoop(final SelectionKey k) {
        final AbstractNioChannel<?> ch = (AbstractNioChannel<?>)k.attachment();
        ch.writeSuspended = false;
        this.write0(ch);
    }
    
    protected abstract boolean scheduleWriteIfNecessary(final AbstractNioChannel<?> p0);
    
    protected void write0(final AbstractNioChannel<?> channel) {
        boolean open = true;
        boolean addOpWrite = false;
        boolean removeOpWrite = false;
        final boolean iothread = isIoThread(channel);
        long writtenBytes = 0L;
        final SocketSendBufferPool sendBufferPool = this.sendBufferPool;
        final WritableByteChannel ch = (WritableByteChannel)channel.channel;
        final AbstractNioChannel.WriteRequestQueue writeBuffer = channel.writeBufferQueue;
        final int writeSpinCount = channel.getConfig().getWriteSpinCount();
        List<Throwable> causes = null;
        synchronized (channel.writeLock) {
            channel.inWriteNowLoop = true;
            while (true) {
                MessageEvent evt = channel.currentWriteEvent;
                SocketSendBufferPool.SendBuffer buf = null;
                ChannelFuture future = null;
                try {
                    if (evt == null) {
                        if ((channel.currentWriteEvent = (evt = writeBuffer.poll())) == null) {
                            removeOpWrite = true;
                            channel.writeSuspended = false;
                            break;
                        }
                        future = evt.getFuture();
                        buf = (channel.currentWriteBuffer = sendBufferPool.acquire(evt.getMessage()));
                    }
                    else {
                        future = evt.getFuture();
                        buf = channel.currentWriteBuffer;
                    }
                    long localWrittenBytes = 0L;
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
                    if (!buf.finished()) {
                        addOpWrite = true;
                        channel.writeSuspended = true;
                        if (writtenBytes > 0L) {
                            future.setProgress(localWrittenBytes, buf.writtenBytes(), buf.totalBytes());
                        }
                        break;
                    }
                    buf.release();
                    channel.currentWriteEvent = null;
                    channel.currentWriteBuffer = null;
                    evt = null;
                    buf = null;
                    future.setSuccess();
                }
                catch (AsynchronousCloseException e) {}
                catch (Throwable t) {
                    if (buf != null) {
                        buf.release();
                    }
                    channel.currentWriteEvent = null;
                    channel.currentWriteBuffer = null;
                    buf = null;
                    evt = null;
                    if (future != null) {
                        future.setFailure(t);
                    }
                    if (iothread) {
                        if (causes == null) {
                            causes = new ArrayList<Throwable>(1);
                        }
                        causes.add(t);
                    }
                    else {
                        Channels.fireExceptionCaughtLater(channel, t);
                    }
                    if (!(t instanceof IOException)) {
                        continue;
                    }
                    open = false;
                }
            }
            channel.inWriteNowLoop = false;
            if (open) {
                if (addOpWrite) {
                    this.setOpWrite(channel);
                }
                else if (removeOpWrite) {
                    this.clearOpWrite(channel);
                }
            }
        }
        if (causes != null) {
            for (final Throwable cause : causes) {
                Channels.fireExceptionCaught(channel, cause);
            }
        }
        if (!open) {
            this.close(channel, Channels.succeededFuture(channel));
        }
        if (iothread) {
            Channels.fireWriteComplete(channel, writtenBytes);
        }
        else {
            Channels.fireWriteCompleteLater(channel, writtenBytes);
        }
    }
    
    static boolean isIoThread(final AbstractNioChannel<?> channel) {
        return Thread.currentThread() == channel.worker.thread;
    }
    
    protected void setOpWrite(final AbstractNioChannel<?> channel) {
        final Selector selector = this.selector;
        final SelectionKey key = ((SelectableChannel)channel.channel).keyFor(selector);
        if (key == null) {
            return;
        }
        if (!key.isValid()) {
            this.close(key);
            return;
        }
        int interestOps = channel.getInternalInterestOps();
        if ((interestOps & 0x4) == 0x0) {
            interestOps |= 0x4;
            key.interestOps(interestOps);
            channel.setInternalInterestOps(interestOps);
        }
    }
    
    protected void clearOpWrite(final AbstractNioChannel<?> channel) {
        final Selector selector = this.selector;
        final SelectionKey key = ((SelectableChannel)channel.channel).keyFor(selector);
        if (key == null) {
            return;
        }
        if (!key.isValid()) {
            this.close(key);
            return;
        }
        int interestOps = channel.getInternalInterestOps();
        if ((interestOps & 0x4) != 0x0) {
            interestOps &= 0xFFFFFFFB;
            key.interestOps(interestOps);
            channel.setInternalInterestOps(interestOps);
        }
    }
    
    protected void close(final AbstractNioChannel<?> channel, final ChannelFuture future) {
        final boolean connected = channel.isConnected();
        final boolean bound = channel.isBound();
        final boolean iothread = isIoThread(channel);
        try {
            ((AbstractInterruptibleChannel)channel.channel).close();
            this.increaseCancelledKeys();
            if (channel.setClosed()) {
                future.setSuccess();
                if (connected) {
                    if (iothread) {
                        Channels.fireChannelDisconnected(channel);
                    }
                    else {
                        Channels.fireChannelDisconnectedLater(channel);
                    }
                }
                if (bound) {
                    if (iothread) {
                        Channels.fireChannelUnbound(channel);
                    }
                    else {
                        Channels.fireChannelUnboundLater(channel);
                    }
                }
                cleanUpWriteBuffer(channel);
                if (iothread) {
                    Channels.fireChannelClosed(channel);
                }
                else {
                    Channels.fireChannelClosedLater(channel);
                }
            }
            else {
                future.setSuccess();
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
    
    protected static void cleanUpWriteBuffer(final AbstractNioChannel<?> channel) {
        Exception cause = null;
        boolean fireExceptionCaught = false;
        synchronized (channel.writeLock) {
            MessageEvent evt = channel.currentWriteEvent;
            if (evt != null) {
                if (channel.isOpen()) {
                    cause = new NotYetConnectedException();
                }
                else {
                    cause = new ClosedChannelException();
                }
                final ChannelFuture future = evt.getFuture();
                if (channel.currentWriteBuffer != null) {
                    channel.currentWriteBuffer.release();
                    channel.currentWriteBuffer = null;
                }
                channel.currentWriteEvent = null;
                evt = null;
                future.setFailure(cause);
                fireExceptionCaught = true;
            }
            final AbstractNioChannel.WriteRequestQueue writeBuffer = channel.writeBufferQueue;
            while (true) {
                evt = writeBuffer.poll();
                if (evt == null) {
                    break;
                }
                if (cause == null) {
                    if (channel.isOpen()) {
                        cause = new NotYetConnectedException();
                    }
                    else {
                        cause = new ClosedChannelException();
                    }
                    fireExceptionCaught = true;
                }
                evt.getFuture().setFailure(cause);
            }
        }
        if (fireExceptionCaught) {
            if (isIoThread(channel)) {
                Channels.fireExceptionCaught(channel, cause);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, cause);
            }
        }
    }
    
    void setInterestOps(final AbstractNioChannel<?> channel, final ChannelFuture future, final int interestOps) {
        final boolean iothread = isIoThread(channel);
        if (!iothread) {
            channel.getPipeline().execute(new Runnable() {
                public void run() {
                    AbstractNioWorker.this.setInterestOps(channel, future, interestOps);
                }
            });
            return;
        }
        boolean changed = false;
        try {
            final Selector selector = this.selector;
            final SelectionKey key = ((SelectableChannel)channel.channel).keyFor(selector);
            final int newInterestOps = (interestOps & 0xFFFFFFFB) | (channel.getInternalInterestOps() & 0x4);
            if (key == null || selector == null) {
                if (channel.getInternalInterestOps() != newInterestOps) {
                    changed = true;
                }
                channel.setInternalInterestOps(newInterestOps);
                future.setSuccess();
                if (changed) {
                    if (iothread) {
                        Channels.fireChannelInterestChanged(channel);
                    }
                    else {
                        Channels.fireChannelInterestChangedLater(channel);
                    }
                }
                return;
            }
            if (channel.getInternalInterestOps() != newInterestOps) {
                changed = true;
                key.interestOps(newInterestOps);
                if (Thread.currentThread() != this.thread && this.wakenUp.compareAndSet(false, true)) {
                    selector.wakeup();
                }
                channel.setInternalInterestOps(newInterestOps);
            }
            future.setSuccess();
            if (changed) {
                Channels.fireChannelInterestChanged(channel);
            }
        }
        catch (CancelledKeyException e) {
            final ClosedChannelException cce = new ClosedChannelException();
            future.setFailure(cce);
            Channels.fireExceptionCaught(channel, cce);
        }
        catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught(channel, t);
        }
    }
    
    protected abstract boolean read(final SelectionKey p0);
}
