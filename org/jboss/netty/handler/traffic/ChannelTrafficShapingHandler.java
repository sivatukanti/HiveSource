// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import java.util.Iterator;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.LinkedList;
import org.jboss.netty.util.Timer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.Timeout;
import java.util.List;

public class ChannelTrafficShapingHandler extends AbstractTrafficShapingHandler
{
    private final List<ToSend> messagesQueue;
    private long queueSize;
    private volatile Timeout writeTimeout;
    private volatile ChannelHandlerContext ctx;
    
    public ChannelTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        super(timer, writeLimit, readLimit, checkInterval);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        super(timer, writeLimit, readLimit, checkInterval, maxTime);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit) {
        super(timer, writeLimit, readLimit);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final Timer timer, final long checkInterval) {
        super(timer, checkInterval);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final Timer timer) {
        super(timer);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    public ChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer) {
        super(objectSizeEstimator, timer);
        this.messagesQueue = new LinkedList<ToSend>();
    }
    
    @Override
    void submitWrite(final ChannelHandlerContext ctx, final MessageEvent evt, final long size, final long delay, final long now) throws Exception {
        if (ctx == null) {
            this.ctx = ctx;
        }
        final Channel channel = ctx.getChannel();
        ToSend newToSend;
        synchronized (this) {
            if (delay == 0L && this.messagesQueue.isEmpty()) {
                if (!channel.isConnected()) {
                    return;
                }
                if (this.trafficCounter != null) {
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                }
                ctx.sendDownstream(evt);
                return;
            }
            else if (this.timer == null) {
                Thread.sleep(delay);
                if (!channel.isConnected()) {
                    return;
                }
                if (this.trafficCounter != null) {
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                }
                ctx.sendDownstream(evt);
                return;
            }
            else {
                if (!channel.isConnected()) {
                    return;
                }
                newToSend = new ToSend(delay + now, evt);
                this.messagesQueue.add(newToSend);
                this.checkWriteSuspend(ctx, delay, this.queueSize += size);
            }
        }
        final long futureNow = newToSend.relativeTimeAction;
        this.writeTimeout = this.timer.newTimeout(new TimerTask() {
            public void run(final Timeout timeout) throws Exception {
                ChannelTrafficShapingHandler.this.sendAllValid(ctx, futureNow);
            }
        }, delay + 1L, TimeUnit.MILLISECONDS);
    }
    
    private void sendAllValid(final ChannelHandlerContext ctx, final long now) throws Exception {
        final Channel channel = ctx.getChannel();
        if (!channel.isConnected()) {
            return;
        }
        synchronized (this) {
            while (!this.messagesQueue.isEmpty()) {
                final ToSend newToSend = this.messagesQueue.remove(0);
                if (newToSend.relativeTimeAction > now) {
                    this.messagesQueue.add(0, newToSend);
                    break;
                }
                final long size = this.calculateSize(newToSend.toSend.getMessage());
                if (this.trafficCounter != null) {
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                }
                this.queueSize -= size;
                if (!channel.isConnected()) {
                    break;
                }
                ctx.sendDownstream(newToSend.toSend);
            }
            if (this.messagesQueue.isEmpty()) {
                this.releaseWriteSuspended(ctx);
            }
        }
    }
    
    public long queueSize() {
        return this.queueSize;
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }
        synchronized (this) {
            this.messagesQueue.clear();
        }
        if (this.writeTimeout != null) {
            this.writeTimeout.cancel();
        }
        super.channelClosed(ctx, e);
    }
    
    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.ctx = ctx;
        final ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(ctx);
        rws.readSuspend = true;
        ctx.getChannel().setReadable(false);
        if (this.trafficCounter == null && this.timer != null) {
            this.trafficCounter = new TrafficCounter(this, this.timer, "ChannelTC" + ctx.getChannel().getId(), this.checkInterval);
        }
        if (this.trafficCounter != null) {
            this.trafficCounter.start();
        }
        rws.readSuspend = false;
        ctx.getChannel().setReadable(true);
        super.channelConnected(ctx, e);
    }
    
    @Override
    public void releaseExternalResources() {
        final Channel channel = this.ctx.getChannel();
        synchronized (this) {
            if (this.ctx != null && this.ctx.getChannel().isConnected()) {
                for (final ToSend toSend : this.messagesQueue) {
                    if (!channel.isConnected()) {
                        break;
                    }
                    this.ctx.sendDownstream(toSend.toSend);
                }
            }
            this.messagesQueue.clear();
        }
        if (this.writeTimeout != null) {
            this.writeTimeout.cancel();
        }
        super.releaseExternalResources();
    }
    
    private static final class ToSend
    {
        final long relativeTimeAction;
        final MessageEvent toSend;
        
        private ToSend(final long delay, final MessageEvent toSend) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
        }
    }
}
