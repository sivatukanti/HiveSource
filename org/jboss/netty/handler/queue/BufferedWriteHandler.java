// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.queue;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.ChannelStateEvent;
import java.util.Iterator;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBuffers;
import java.util.List;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import java.util.Queue;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelHandler;

public class BufferedWriteHandler extends SimpleChannelHandler implements LifeCycleAwareChannelHandler
{
    private final Queue<MessageEvent> queue;
    private final boolean consolidateOnFlush;
    private volatile ChannelHandlerContext ctx;
    private final AtomicBoolean flush;
    
    public BufferedWriteHandler() {
        this(false);
    }
    
    public BufferedWriteHandler(final Queue<MessageEvent> queue) {
        this(queue, false);
    }
    
    public BufferedWriteHandler(final boolean consolidateOnFlush) {
        this(new ConcurrentLinkedQueue<MessageEvent>(), consolidateOnFlush);
    }
    
    public BufferedWriteHandler(final Queue<MessageEvent> queue, final boolean consolidateOnFlush) {
        this.flush = new AtomicBoolean(false);
        if (queue == null) {
            throw new NullPointerException("queue");
        }
        this.queue = queue;
        this.consolidateOnFlush = consolidateOnFlush;
    }
    
    public boolean isConsolidateOnFlush() {
        return this.consolidateOnFlush;
    }
    
    protected Queue<MessageEvent> getQueue() {
        return this.queue;
    }
    
    public void flush() {
        this.flush(this.consolidateOnFlush);
    }
    
    public void flush(final boolean consolidateOnFlush) {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        final Channel channel = ctx.getChannel();
        final boolean acquired;
        if (acquired = this.flush.compareAndSet(false, true)) {
            final Queue<MessageEvent> queue = this.getQueue();
            if (consolidateOnFlush) {
                if (queue.isEmpty()) {
                    this.flush.set(false);
                    return;
                }
                List<MessageEvent> pendingWrites = new ArrayList<MessageEvent>();
                while (true) {
                    final MessageEvent e = queue.poll();
                    if (e == null) {
                        break;
                    }
                    if (!(e.getMessage() instanceof ChannelBuffer)) {
                        if ((pendingWrites = this.consolidatedWrite(pendingWrites)) == null) {
                            pendingWrites = new ArrayList<MessageEvent>();
                        }
                        ctx.sendDownstream(e);
                    }
                    else {
                        pendingWrites.add(e);
                    }
                }
                this.consolidatedWrite(pendingWrites);
            }
            else {
                while (true) {
                    final MessageEvent e2 = queue.poll();
                    if (e2 == null) {
                        break;
                    }
                    ctx.sendDownstream(e2);
                }
            }
            this.flush.set(false);
        }
        if (acquired && (!channel.isConnected() || (channel.isWritable() && !this.queue.isEmpty()))) {
            this.flush(consolidateOnFlush);
        }
    }
    
    private List<MessageEvent> consolidatedWrite(final List<MessageEvent> pendingWrites) {
        final int size = pendingWrites.size();
        if (size == 1) {
            this.ctx.sendDownstream(pendingWrites.remove(0));
            return pendingWrites;
        }
        if (size == 0) {
            return pendingWrites;
        }
        final ChannelBuffer[] data = new ChannelBuffer[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = (ChannelBuffer)pendingWrites.get(i).getMessage();
        }
        final ChannelBuffer composite = ChannelBuffers.wrappedBuffer(data);
        final ChannelFuture future = Channels.future(this.ctx.getChannel());
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    for (final MessageEvent e : pendingWrites) {
                        e.getFuture().setSuccess();
                    }
                }
                else {
                    final Throwable cause = future.getCause();
                    for (final MessageEvent e2 : pendingWrites) {
                        e2.getFuture().setFailure(cause);
                    }
                }
            }
        });
        Channels.write(this.ctx, future, composite);
        return null;
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (this.ctx == null) {
            this.ctx = ctx;
        }
        else {
            assert this.ctx == ctx;
        }
        this.getQueue().add(e);
    }
    
    @Override
    public void disconnectRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        try {
            this.flush(this.consolidateOnFlush);
        }
        finally {
            ctx.sendDownstream(e);
        }
    }
    
    @Override
    public void closeRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        try {
            this.flush(this.consolidateOnFlush);
        }
        finally {
            ctx.sendDownstream(e);
        }
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        Throwable cause = null;
        while (true) {
            final MessageEvent ev = this.queue.poll();
            if (ev == null) {
                break;
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            ev.getFuture().setFailure(cause);
        }
        if (cause != null) {
            Channels.fireExceptionCaught(ctx.getChannel(), cause);
        }
        super.channelClosed(ctx, e);
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
        this.flush(this.consolidateOnFlush);
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
        Throwable cause = null;
        while (true) {
            final MessageEvent ev = this.queue.poll();
            if (ev == null) {
                break;
            }
            if (cause == null) {
                cause = new IOException("Unable to flush message");
            }
            ev.getFuture().setFailure(cause);
        }
        if (cause != null) {
            Channels.fireExceptionCaughtLater(ctx.getChannel(), cause);
        }
    }
}
