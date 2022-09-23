// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import java.util.List;
import java.util.Iterator;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import java.util.LinkedList;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler extends AbstractTrafficShapingHandler
{
    private final ConcurrentMap<Integer, PerChannel> channelQueues;
    private final AtomicLong queuesSize;
    long maxGlobalWriteSize;
    
    void createGlobalTrafficCounter() {
        if (this.timer != null) {
            final TrafficCounter tc = new TrafficCounter(this, this.timer, "GlobalTC", this.checkInterval);
            this.setTrafficCounter(tc);
            tc.start();
        }
    }
    
    public GlobalTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        super(timer, writeLimit, readLimit, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        super(timer, writeLimit, readLimit, checkInterval, maxTime);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit) {
        super(timer, writeLimit, readLimit);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final Timer timer, final long checkInterval) {
        super(timer, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final Timer timer) {
        super(timer);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public GlobalTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer) {
        super(objectSizeEstimator, timer);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter();
    }
    
    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }
    
    public void setMaxGlobalWriteSize(final long maxGlobalWriteSize) {
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }
    
    public long queuesSize() {
        return this.queuesSize.get();
    }
    
    private synchronized PerChannel getOrSetPerChannel(final ChannelHandlerContext ctx) {
        final Integer key = ctx.getChannel().hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = new PerChannel();
            perChannel.messagesQueue = new LinkedList<ToSend>();
            perChannel.ctx = ctx;
            perChannel.queueSize = 0L;
            perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
            this.channelQueues.put(key, perChannel);
        }
        return perChannel;
    }
    
    @Override
    long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
        final Integer key = ctx.getChannel().hashCode();
        final PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null && wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
            wait = this.maxTime;
        }
        return wait;
    }
    
    @Override
    void informReadOperation(final ChannelHandlerContext ctx, final long now) {
        final Integer key = ctx.getChannel().hashCode();
        final PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }
    
    @Override
    void submitWrite(final ChannelHandlerContext ctx, final MessageEvent evt, final long size, final long writedelay, final long now) throws Exception {
        final PerChannel perChannel = this.getOrSetPerChannel(ctx);
        boolean globalSizeExceeded = false;
        final Channel channel = ctx.getChannel();
        long delay;
        ToSend newToSend;
        synchronized (perChannel) {
            if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
                if (!channel.isConnected()) {
                    return;
                }
                if (this.trafficCounter != null) {
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                }
                ctx.sendDownstream(evt);
                perChannel.lastWriteTimestamp = now;
                return;
            }
            else {
                delay = writedelay;
                if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
                    delay = this.maxTime;
                }
                if (this.timer == null) {
                    Thread.sleep(delay);
                    if (!ctx.getChannel().isConnected()) {
                        return;
                    }
                    if (this.trafficCounter != null) {
                        this.trafficCounter.bytesRealWriteFlowControl(size);
                    }
                    ctx.sendDownstream(evt);
                    perChannel.lastWriteTimestamp = now;
                    return;
                }
                else {
                    if (!ctx.getChannel().isConnected()) {
                        return;
                    }
                    newToSend = new ToSend(delay + now, evt, size);
                    perChannel.messagesQueue.add(newToSend);
                    final PerChannel perChannel2 = perChannel;
                    perChannel2.queueSize += size;
                    this.queuesSize.addAndGet(size);
                    this.checkWriteSuspend(ctx, delay, perChannel.queueSize);
                    if (this.queuesSize.get() > this.maxGlobalWriteSize) {
                        globalSizeExceeded = true;
                    }
                }
            }
        }
        if (globalSizeExceeded) {
            this.setWritable(ctx, false);
        }
        final long futureNow = newToSend.relativeTimeAction;
        final PerChannel forSchedule = perChannel;
        this.timer.newTimeout(new TimerTask() {
            public void run(final Timeout timeout) throws Exception {
                GlobalTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private void sendAllValid(final ChannelHandlerContext ctx, final PerChannel perChannel, final long now) throws Exception {
        final Channel channel = ctx.getChannel();
        if (!channel.isConnected()) {
            return;
        }
        synchronized (perChannel) {
            while (!perChannel.messagesQueue.isEmpty()) {
                final ToSend newToSend = perChannel.messagesQueue.remove(0);
                if (newToSend.relativeTimeAction > now) {
                    perChannel.messagesQueue.add(0, newToSend);
                    break;
                }
                if (!channel.isConnected()) {
                    break;
                }
                final long size = newToSend.size;
                if (this.trafficCounter != null) {
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                }
                perChannel.queueSize -= size;
                this.queuesSize.addAndGet(-size);
                ctx.sendDownstream(newToSend.toSend);
                perChannel.lastWriteTimestamp = now;
            }
            if (perChannel.messagesQueue.isEmpty()) {
                this.releaseWriteSuspended(ctx);
            }
        }
    }
    
    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.getOrSetPerChannel(ctx);
        super.channelConnected(ctx, e);
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        final Integer key = ctx.getChannel().hashCode();
        final PerChannel perChannel = this.channelQueues.remove(key);
        if (perChannel != null) {
            synchronized (perChannel) {
                this.queuesSize.addAndGet(-perChannel.queueSize);
                perChannel.messagesQueue.clear();
            }
        }
        super.channelClosed(ctx, e);
    }
    
    @Override
    public void releaseExternalResources() {
        for (final PerChannel perChannel : this.channelQueues.values()) {
            if (perChannel != null && perChannel.ctx != null && perChannel.ctx.getChannel().isConnected()) {
                final Channel channel = perChannel.ctx.getChannel();
                synchronized (perChannel) {
                    for (final ToSend toSend : perChannel.messagesQueue) {
                        if (!channel.isConnected()) {
                            break;
                        }
                        perChannel.ctx.sendDownstream(toSend.toSend);
                    }
                    perChannel.messagesQueue.clear();
                }
            }
        }
        this.channelQueues.clear();
        this.queuesSize.set(0L);
        super.releaseExternalResources();
    }
    
    private static final class PerChannel
    {
        List<ToSend> messagesQueue;
        ChannelHandlerContext ctx;
        long queueSize;
        long lastWriteTimestamp;
        long lastReadTimestamp;
    }
    
    private static final class ToSend
    {
        final long relativeTimeAction;
        final MessageEvent toSend;
        final long size;
        
        private ToSend(final long delay, final MessageEvent toSend, final long size) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
            this.size = size;
        }
    }
}
