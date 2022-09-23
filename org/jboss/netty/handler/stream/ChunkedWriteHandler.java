// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.stream;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.io.IOException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channels;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import java.util.Queue;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class ChunkedWriteHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler, LifeCycleAwareChannelHandler
{
    private static final InternalLogger logger;
    private final Queue<MessageEvent> queue;
    private volatile ChannelHandlerContext ctx;
    private final AtomicBoolean flush;
    private MessageEvent currentEvent;
    private volatile boolean flushNeeded;
    
    public ChunkedWriteHandler() {
        this.queue = new ConcurrentLinkedQueue<MessageEvent>();
        this.flush = new AtomicBoolean(false);
    }
    
    public void resumeTransfer() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        try {
            this.flush(ctx, false);
        }
        catch (Exception e) {
            if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                ChunkedWriteHandler.logger.warn("Unexpected exception while sending chunks.", e);
            }
        }
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }
        final boolean offered = this.queue.offer((MessageEvent)e);
        assert offered;
        final Channel channel = ctx.getChannel();
        if (channel.isWritable() || !channel.isConnected()) {
            this.flush(this.ctx = ctx, false);
        }
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            final ChannelStateEvent cse = (ChannelStateEvent)e;
            switch (cse.getState()) {
                case INTEREST_OPS: {
                    this.flush(ctx, true);
                    break;
                }
                case OPEN: {
                    if (!Boolean.TRUE.equals(cse.getValue())) {
                        this.flush(ctx, true);
                        break;
                    }
                    break;
                }
            }
        }
        ctx.sendUpstream(e);
    }
    
    private void discard(final ChannelHandlerContext ctx, final boolean fireNow) {
        ClosedChannelException cause = null;
        while (true) {
            MessageEvent currentEvent = this.currentEvent;
            if (this.currentEvent == null) {
                currentEvent = this.queue.poll();
            }
            else {
                this.currentEvent = null;
            }
            if (currentEvent == null) {
                break;
            }
            final Object m = currentEvent.getMessage();
            if (m instanceof ChunkedInput) {
                closeInput((ChunkedInput)m);
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            currentEvent.getFuture().setFailure(cause);
        }
        if (cause != null) {
            if (fireNow) {
                Channels.fireExceptionCaught(ctx.getChannel(), cause);
            }
            else {
                Channels.fireExceptionCaughtLater(ctx.getChannel(), cause);
            }
        }
    }
    
    private void flush(final ChannelHandlerContext ctx, final boolean fireNow) throws Exception {
        final Channel channel = ctx.getChannel();
        boolean suspend = false;
        this.flushNeeded = true;
        final boolean acquired;
        if (acquired = this.flush.compareAndSet(false, true)) {
            this.flushNeeded = false;
            try {
                if (!channel.isConnected()) {
                    this.discard(ctx, fireNow);
                    return;
                }
                while (channel.isWritable()) {
                    if (this.currentEvent == null) {
                        this.currentEvent = this.queue.poll();
                    }
                    if (this.currentEvent == null) {
                        break;
                    }
                    if (this.currentEvent.getFuture().isDone()) {
                        this.currentEvent = null;
                    }
                    else {
                        final MessageEvent currentEvent = this.currentEvent;
                        final Object m = currentEvent.getMessage();
                        if (m instanceof ChunkedInput) {
                            final ChunkedInput chunks = (ChunkedInput)m;
                            Object chunk;
                            boolean endOfInput;
                            try {
                                chunk = chunks.nextChunk();
                                endOfInput = chunks.isEndOfInput();
                                if (chunk == null) {
                                    chunk = ChannelBuffers.EMPTY_BUFFER;
                                    suspend = !endOfInput;
                                }
                                else {
                                    suspend = false;
                                }
                            }
                            catch (Throwable t) {
                                this.currentEvent = null;
                                currentEvent.getFuture().setFailure(t);
                                if (fireNow) {
                                    Channels.fireExceptionCaught(ctx, t);
                                }
                                else {
                                    Channels.fireExceptionCaughtLater(ctx, t);
                                }
                                closeInput(chunks);
                                break;
                            }
                            if (suspend) {
                                break;
                            }
                            ChannelFuture writeFuture;
                            if (endOfInput) {
                                this.currentEvent = null;
                                writeFuture = currentEvent.getFuture();
                                writeFuture.addListener(new ChannelFutureListener() {
                                    public void operationComplete(final ChannelFuture future) throws Exception {
                                        ChunkedWriteHandler.closeInput(chunks);
                                    }
                                });
                            }
                            else {
                                writeFuture = Channels.future(channel);
                                writeFuture.addListener(new ChannelFutureListener() {
                                    public void operationComplete(final ChannelFuture future) throws Exception {
                                        if (!future.isSuccess()) {
                                            currentEvent.getFuture().setFailure(future.getCause());
                                            ChunkedWriteHandler.closeInput((ChunkedInput)currentEvent.getMessage());
                                        }
                                    }
                                });
                            }
                            Channels.write(ctx, writeFuture, chunk, currentEvent.getRemoteAddress());
                        }
                        else {
                            this.currentEvent = null;
                            ctx.sendDownstream(currentEvent);
                        }
                    }
                    if (!channel.isConnected()) {
                        this.discard(ctx, fireNow);
                        return;
                    }
                }
            }
            finally {
                this.flush.set(false);
            }
        }
        if (acquired && (!channel.isConnected() || (channel.isWritable() && !this.queue.isEmpty() && !suspend) || this.flushNeeded)) {
            this.flush(ctx, fireNow);
        }
    }
    
    static void closeInput(final ChunkedInput chunks) {
        try {
            chunks.close();
        }
        catch (Throwable t) {
            if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                ChunkedWriteHandler.logger.warn("Failed to close a chunked input.", t);
            }
        }
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
        this.flush(ctx, false);
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
        Throwable cause = null;
        boolean fireExceptionCaught = false;
        while (true) {
            MessageEvent currentEvent = this.currentEvent;
            if (this.currentEvent == null) {
                currentEvent = this.queue.poll();
            }
            else {
                this.currentEvent = null;
            }
            if (currentEvent == null) {
                break;
            }
            final Object m = currentEvent.getMessage();
            if (m instanceof ChunkedInput) {
                closeInput((ChunkedInput)m);
            }
            if (cause == null) {
                cause = new IOException("Unable to flush event, discarding");
            }
            currentEvent.getFuture().setFailure(cause);
            fireExceptionCaught = true;
        }
        if (fireExceptionCaught) {
            Channels.fireExceptionCaughtLater(ctx.getChannel(), cause);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    }
}
