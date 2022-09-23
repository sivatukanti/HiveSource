// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelFuture;
import java.io.IOException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import org.jboss.netty.channel.socket.Worker;

abstract class AbstractOioWorker<C extends AbstractOioChannel> implements Worker
{
    private final Queue<Runnable> eventQueue;
    protected final C channel;
    protected volatile Thread thread;
    private volatile boolean done;
    
    protected AbstractOioWorker(final C channel) {
        this.eventQueue = new ConcurrentLinkedQueue<Runnable>();
        this.channel = channel;
        channel.worker = this;
    }
    
    public void run() {
        final AbstractOioChannel channel = this.channel;
        final Thread currentThread = Thread.currentThread();
        channel.workerThread = currentThread;
        this.thread = currentThread;
        while (this.channel.isOpen()) {
            synchronized (this.channel.interestOpsLock) {
                while (!this.channel.isReadable()) {
                    try {
                        this.channel.interestOpsLock.wait();
                    }
                    catch (InterruptedException e) {
                        if (!this.channel.isOpen()) {
                            break;
                        }
                        continue;
                    }
                }
            }
            boolean cont = false;
            try {
                cont = this.process();
            }
            catch (Throwable t) {
                final boolean readTimeout = t instanceof SocketTimeoutException;
                if (!readTimeout && !this.channel.isSocketClosed()) {
                    Channels.fireExceptionCaught(this.channel, t);
                }
                if (readTimeout) {
                    cont = true;
                }
            }
            finally {
                this.processEventQueue();
            }
            if (!cont) {
                break;
            }
        }
        synchronized (this.channel.interestOpsLock) {
            this.channel.workerThread = null;
        }
        close(this.channel, Channels.succeededFuture(this.channel), true);
        this.done = true;
        this.processEventQueue();
    }
    
    static boolean isIoThread(final AbstractOioChannel channel) {
        return Thread.currentThread() == channel.workerThread;
    }
    
    public void executeInIoThread(final Runnable task) {
        if (Thread.currentThread() == this.thread || this.done) {
            task.run();
        }
        else {
            final boolean added = this.eventQueue.offer(task);
            if (added) {}
        }
    }
    
    private void processEventQueue() {
        while (true) {
            final Runnable task = this.eventQueue.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }
    
    abstract boolean process() throws IOException;
    
    static void setInterestOps(final AbstractOioChannel channel, final ChannelFuture future, int interestOps) {
        final boolean iothread = isIoThread(channel);
        interestOps &= 0xFFFFFFFB;
        interestOps |= (channel.getInternalInterestOps() & 0x4);
        boolean changed = false;
        try {
            if (channel.getInternalInterestOps() != interestOps) {
                if ((interestOps & 0x1) != 0x0) {
                    channel.setInternalInterestOps(1);
                }
                else {
                    channel.setInternalInterestOps(0);
                }
                changed = true;
            }
            future.setSuccess();
            if (changed) {
                synchronized (channel.interestOpsLock) {
                    channel.setInternalInterestOps(interestOps);
                    final Thread currentThread = Thread.currentThread();
                    final Thread workerThread = channel.workerThread;
                    if (workerThread != null && currentThread != workerThread) {
                        workerThread.interrupt();
                    }
                }
                if (iothread) {
                    Channels.fireChannelInterestChanged(channel);
                }
                else {
                    Channels.fireChannelInterestChangedLater(channel);
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
    
    static void close(final AbstractOioChannel channel, final ChannelFuture future) {
        close(channel, future, isIoThread(channel));
    }
    
    private static void close(final AbstractOioChannel channel, final ChannelFuture future, final boolean iothread) {
        final boolean connected = channel.isConnected();
        final boolean bound = channel.isBound();
        try {
            channel.closeSocket();
            if (channel.setClosed()) {
                future.setSuccess();
                if (connected) {
                    final Thread currentThread = Thread.currentThread();
                    synchronized (channel.interestOpsLock) {
                        final Thread workerThread = channel.workerThread;
                        if (workerThread != null && currentThread != workerThread) {
                            workerThread.interrupt();
                        }
                    }
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
}
