// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timer;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

@ChannelHandler.Sharable
public class WriteTimeoutHandler extends SimpleChannelDownstreamHandler implements ExternalResourceReleasable
{
    static final WriteTimeoutException EXCEPTION;
    private final Timer timer;
    private final long timeoutMillis;
    
    public WriteTimeoutHandler(final Timer timer, final int timeoutSeconds) {
        this(timer, timeoutSeconds, TimeUnit.SECONDS);
    }
    
    public WriteTimeoutHandler(final Timer timer, final long timeout, final TimeUnit unit) {
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
    
    protected long getTimeoutMillis(final MessageEvent e) {
        return this.timeoutMillis;
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final long timeoutMillis = this.getTimeoutMillis(e);
        if (timeoutMillis > 0L) {
            final ChannelFuture future = e.getFuture();
            final Timeout timeout = this.timer.newTimeout(new WriteTimeoutTask(ctx, future), timeoutMillis, TimeUnit.MILLISECONDS);
            future.addListener(new TimeoutCanceller(timeout));
        }
        super.writeRequested(ctx, e);
    }
    
    protected void writeTimedOut(final ChannelHandlerContext ctx) throws Exception {
        Channels.fireExceptionCaught(ctx, WriteTimeoutHandler.EXCEPTION);
    }
    
    static {
        EXCEPTION = new WriteTimeoutException();
    }
    
    private final class WriteTimeoutTask implements TimerTask
    {
        private final ChannelHandlerContext ctx;
        private final ChannelFuture future;
        
        WriteTimeoutTask(final ChannelHandlerContext ctx, final ChannelFuture future) {
            this.ctx = ctx;
            this.future = future;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (timeout.isCancelled()) {
                return;
            }
            if (!this.ctx.getChannel().isOpen()) {
                return;
            }
            if (this.future.setFailure(WriteTimeoutHandler.EXCEPTION)) {
                this.fireWriteTimeOut(this.ctx);
            }
        }
        
        private void fireWriteTimeOut(final ChannelHandlerContext ctx) {
            ctx.getPipeline().execute(new Runnable() {
                public void run() {
                    try {
                        WriteTimeoutHandler.this.writeTimedOut(ctx);
                    }
                    catch (Throwable t) {
                        Channels.fireExceptionCaught(ctx, t);
                    }
                }
            });
        }
    }
    
    private static final class TimeoutCanceller implements ChannelFutureListener
    {
        private final Timeout timeout;
        
        TimeoutCanceller(final Timeout timeout) {
            this.timeout = timeout;
        }
        
        public void operationComplete(final ChannelFuture future) throws Exception {
            this.timeout.cancel();
        }
    }
}
