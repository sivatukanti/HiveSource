// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.TimerTask;
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
public class ReadTimeoutHandler extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler, ExternalResourceReleasable
{
    static final ReadTimeoutException EXCEPTION;
    final Timer timer;
    final long timeoutMillis;
    
    public ReadTimeoutHandler(final Timer timer, final int timeoutSeconds) {
        this(timer, timeoutSeconds, TimeUnit.SECONDS);
    }
    
    public ReadTimeoutHandler(final Timer timer, final long timeout, final TimeUnit unit) {
        if (timer == null) {
            throw new NullPointerException("timer");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        this.timer = timer;
        if (timeout <= 0L) {
            this.timeoutMillis = 0L;
        }
        else {
            this.timeoutMillis = Math.max(unit.toMillis(timeout), 1L);
        }
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
        if (this.timeoutMillis > 0L) {
            state.timeout = this.timer.newTimeout(new ReadTimeoutTask(ctx), this.timeoutMillis, TimeUnit.MILLISECONDS);
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
        if (state.timeout != null) {
            state.timeout.cancel();
            state.timeout = null;
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
    
    protected void readTimedOut(final ChannelHandlerContext ctx) throws Exception {
        Channels.fireExceptionCaught(ctx, ReadTimeoutHandler.EXCEPTION);
    }
    
    static {
        EXCEPTION = new ReadTimeoutException();
    }
    
    private final class ReadTimeoutTask implements TimerTask
    {
        private final ChannelHandlerContext ctx;
        
        ReadTimeoutTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (timeout.isCancelled()) {
                return;
            }
            if (!this.ctx.getChannel().isOpen()) {
                return;
            }
            final State state = (State)this.ctx.getAttachment();
            final long currentTime = System.currentTimeMillis();
            final long nextDelay = ReadTimeoutHandler.this.timeoutMillis - (currentTime - state.lastReadTime);
            if (nextDelay <= 0L) {
                state.timeout = ReadTimeoutHandler.this.timer.newTimeout(this, ReadTimeoutHandler.this.timeoutMillis, TimeUnit.MILLISECONDS);
                this.fireReadTimedOut(this.ctx);
            }
            else {
                state.timeout = ReadTimeoutHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
        
        private void fireReadTimedOut(final ChannelHandlerContext ctx) throws Exception {
            ctx.getPipeline().execute(new Runnable() {
                public void run() {
                    try {
                        ReadTimeoutHandler.this.readTimedOut(ctx);
                    }
                    catch (Throwable t) {
                        Channels.fireExceptionCaught(ctx, t);
                    }
                }
            });
        }
    }
    
    private static final class State
    {
        int state;
        volatile Timeout timeout;
        volatile long lastReadTime;
        
        State() {
            this.lastReadTime = System.currentTimeMillis();
        }
    }
}
