// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.queue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import org.jboss.netty.channel.ChannelStateEvent;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import java.util.concurrent.LinkedBlockingQueue;
import org.jboss.netty.channel.ChannelEvent;
import java.util.concurrent.BlockingQueue;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class BlockingReadHandler<E> extends SimpleChannelUpstreamHandler
{
    private final BlockingQueue<ChannelEvent> queue;
    private volatile boolean closed;
    
    public BlockingReadHandler() {
        this((BlockingQueue)new LinkedBlockingQueue());
    }
    
    public BlockingReadHandler(final BlockingQueue<ChannelEvent> queue) {
        if (queue == null) {
            throw new NullPointerException("queue");
        }
        this.queue = queue;
    }
    
    protected BlockingQueue<ChannelEvent> getQueue() {
        return this.queue;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public E read() throws IOException, InterruptedException {
        final ChannelEvent e = this.readEvent();
        if (e == null) {
            return null;
        }
        if (e instanceof MessageEvent) {
            return this.getMessage((MessageEvent)e);
        }
        if (e instanceof ExceptionEvent) {
            throw (IOException)new IOException().initCause(((ExceptionEvent)e).getCause());
        }
        throw new IllegalStateException();
    }
    
    public E read(final long timeout, final TimeUnit unit) throws IOException, InterruptedException {
        final ChannelEvent e = this.readEvent(timeout, unit);
        if (e == null) {
            return null;
        }
        if (e instanceof MessageEvent) {
            return this.getMessage((MessageEvent)e);
        }
        if (e instanceof ExceptionEvent) {
            throw (IOException)new IOException().initCause(((ExceptionEvent)e).getCause());
        }
        throw new IllegalStateException();
    }
    
    public ChannelEvent readEvent() throws InterruptedException {
        detectDeadLock();
        if (this.isClosed() && this.getQueue().isEmpty()) {
            return null;
        }
        final ChannelEvent e = this.getQueue().take();
        if (!(e instanceof ChannelStateEvent)) {
            return e;
        }
        assert this.closed;
        return null;
    }
    
    public ChannelEvent readEvent(final long timeout, final TimeUnit unit) throws InterruptedException, BlockingReadTimeoutException {
        detectDeadLock();
        if (this.isClosed() && this.getQueue().isEmpty()) {
            return null;
        }
        final ChannelEvent e = this.getQueue().poll(timeout, unit);
        if (e == null) {
            throw new BlockingReadTimeoutException();
        }
        if (!(e instanceof ChannelStateEvent)) {
            return e;
        }
        assert this.closed;
        return null;
    }
    
    private static void detectDeadLock() {
        if (DeadLockProofWorker.PARENT.get() != null) {
            throw new IllegalStateException("read*(...) in I/O thread causes a dead lock or sudden performance drop. Implement a state machine or call read*() from a different thread.");
        }
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        this.getQueue().put(e);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        this.getQueue().put(e);
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.closed = true;
        this.getQueue().put(e);
    }
    
    private E getMessage(final MessageEvent e) {
        return (E)e.getMessage();
    }
}
