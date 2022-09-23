// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timer;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

@ChannelHandler.Sharable
public class IdleStateHandler extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler, ExternalResourceReleasable
{
    final Timer timer;
    final long readerIdleTimeMillis;
    final long writerIdleTimeMillis;
    final long allIdleTimeMillis;
    
    public IdleStateHandler(final Timer timer, final int readerIdleTimeSeconds, final int writerIdleTimeSeconds, final int allIdleTimeSeconds) {
        this(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
    }
    
    public IdleStateHandler(final Timer timer, final long readerIdleTime, final long writerIdleTime, final long allIdleTime, final TimeUnit unit) {
        if (timer == null) {
            throw new NullPointerException("timer");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        this.timer = timer;
        if (readerIdleTime <= 0L) {
            this.readerIdleTimeMillis = 0L;
        }
        else {
            this.readerIdleTimeMillis = Math.max(unit.toMillis(readerIdleTime), 1L);
        }
        if (writerIdleTime <= 0L) {
            this.writerIdleTimeMillis = 0L;
        }
        else {
            this.writerIdleTimeMillis = Math.max(unit.toMillis(writerIdleTime), 1L);
        }
        if (allIdleTime <= 0L) {
            this.allIdleTimeMillis = 0L;
        }
        else {
            this.allIdleTimeMillis = Math.max(unit.toMillis(allIdleTime), 1L);
        }
    }
    
    public long getReaderIdleTimeInMillis() {
        return this.readerIdleTimeMillis;
    }
    
    public long getWriterIdleTimeInMillis() {
        return this.writerIdleTimeMillis;
    }
    
    public long getAllIdleTimeInMillis() {
        return this.allIdleTimeMillis;
    }
    
    public void releaseExternalResources() {
        this.timer.stop();
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.getPipeline().isAttached()) {
            this.initialize(ctx);
        }
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
        destroy(ctx);
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.initialize(ctx);
        ctx.sendUpstream(e);
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        destroy(ctx);
        ctx.sendUpstream(e);
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final State state = (State)ctx.getAttachment();
        state.lastReadTime = System.currentTimeMillis();
        ctx.sendUpstream(e);
    }
    
    @Override
    public void writeComplete(final ChannelHandlerContext ctx, final WriteCompletionEvent e) throws Exception {
        if (e.getWrittenAmount() > 0L) {
            final State state = (State)ctx.getAttachment();
            state.lastWriteTime = System.currentTimeMillis();
        }
        ctx.sendUpstream(e);
    }
    
    private void initialize(final ChannelHandlerContext ctx) {
        final State state = state(ctx);
        synchronized (state) {
            switch (state.state) {
                case 1:
                case 2: {
                    return;
                }
                default: {
                    state.state = 1;
                    break;
                }
            }
        }
        final State state2 = state;
        final State state3 = state;
        final long currentTimeMillis = System.currentTimeMillis();
        state3.lastWriteTime = currentTimeMillis;
        state2.lastReadTime = currentTimeMillis;
        if (this.readerIdleTimeMillis > 0L) {
            state.readerIdleTimeout = this.timer.newTimeout(new ReaderIdleTimeoutTask(ctx), this.readerIdleTimeMillis, TimeUnit.MILLISECONDS);
        }
        if (this.writerIdleTimeMillis > 0L) {
            state.writerIdleTimeout = this.timer.newTimeout(new WriterIdleTimeoutTask(ctx), this.writerIdleTimeMillis, TimeUnit.MILLISECONDS);
        }
        if (this.allIdleTimeMillis > 0L) {
            state.allIdleTimeout = this.timer.newTimeout(new AllIdleTimeoutTask(ctx), this.allIdleTimeMillis, TimeUnit.MILLISECONDS);
        }
    }
    
    private static void destroy(final ChannelHandlerContext ctx) {
        final State state = state(ctx);
        synchronized (state) {
            if (state.state != 1) {
                return;
            }
            state.state = 2;
        }
        if (state.readerIdleTimeout != null) {
            state.readerIdleTimeout.cancel();
            state.readerIdleTimeout = null;
        }
        if (state.writerIdleTimeout != null) {
            state.writerIdleTimeout.cancel();
            state.writerIdleTimeout = null;
        }
        if (state.allIdleTimeout != null) {
            state.allIdleTimeout.cancel();
            state.allIdleTimeout = null;
        }
    }
    
    private static State state(final ChannelHandlerContext ctx) {
        State state;
        synchronized (ctx) {
            state = (State)ctx.getAttachment();
            if (state != null) {
                return state;
            }
            state = new State();
            ctx.setAttachment(state);
        }
        return state;
    }
    
    private void fireChannelIdle(final ChannelHandlerContext ctx, final IdleState state, final long lastActivityTimeMillis) {
        ctx.getPipeline().execute(new Runnable() {
            public void run() {
                try {
                    IdleStateHandler.this.channelIdle(ctx, state, lastActivityTimeMillis);
                }
                catch (Throwable t) {
                    Channels.fireExceptionCaught(ctx, t);
                }
            }
        });
    }
    
    protected void channelIdle(final ChannelHandlerContext ctx, final IdleState state, final long lastActivityTimeMillis) throws Exception {
        ctx.sendUpstream(new DefaultIdleStateEvent(ctx.getChannel(), state, lastActivityTimeMillis));
    }
    
    private final class ReaderIdleTimeoutTask implements TimerTask
    {
        private final ChannelHandlerContext ctx;
        
        ReaderIdleTimeoutTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (timeout.isCancelled() || !this.ctx.getChannel().isOpen()) {
                return;
            }
            final State state = (State)this.ctx.getAttachment();
            final long currentTime = System.currentTimeMillis();
            final long lastReadTime = state.lastReadTime;
            final long nextDelay = IdleStateHandler.this.readerIdleTimeMillis - (currentTime - lastReadTime);
            if (nextDelay <= 0L) {
                state.readerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.readerIdleTimeMillis, TimeUnit.MILLISECONDS);
                IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.READER_IDLE, lastReadTime);
            }
            else {
                state.readerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    private final class WriterIdleTimeoutTask implements TimerTask
    {
        private final ChannelHandlerContext ctx;
        
        WriterIdleTimeoutTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (timeout.isCancelled() || !this.ctx.getChannel().isOpen()) {
                return;
            }
            final State state = (State)this.ctx.getAttachment();
            final long currentTime = System.currentTimeMillis();
            final long lastWriteTime = state.lastWriteTime;
            final long nextDelay = IdleStateHandler.this.writerIdleTimeMillis - (currentTime - lastWriteTime);
            if (nextDelay <= 0L) {
                state.writerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.writerIdleTimeMillis, TimeUnit.MILLISECONDS);
                IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.WRITER_IDLE, lastWriteTime);
            }
            else {
                state.writerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    private final class AllIdleTimeoutTask implements TimerTask
    {
        private final ChannelHandlerContext ctx;
        
        AllIdleTimeoutTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (timeout.isCancelled() || !this.ctx.getChannel().isOpen()) {
                return;
            }
            final State state = (State)this.ctx.getAttachment();
            final long currentTime = System.currentTimeMillis();
            final long lastIoTime = Math.max(state.lastReadTime, state.lastWriteTime);
            final long nextDelay = IdleStateHandler.this.allIdleTimeMillis - (currentTime - lastIoTime);
            if (nextDelay <= 0L) {
                state.allIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.allIdleTimeMillis, TimeUnit.MILLISECONDS);
                IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.ALL_IDLE, lastIoTime);
            }
            else {
                state.allIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    private static final class State
    {
        int state;
        volatile Timeout readerIdleTimeout;
        volatile long lastReadTime;
        volatile Timeout writerIdleTimeout;
        volatile long lastWriteTime;
        volatile Timeout allIdleTimeout;
        
        State() {
        }
    }
}
