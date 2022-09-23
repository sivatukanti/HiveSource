// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import java.util.List;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import java.util.AbstractCollection;
import java.util.Collection;
import org.jboss.netty.channel.ChannelEvent;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import java.util.LinkedList;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.Iterator;
import org.jboss.netty.util.ObjectSizeEstimator;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class GlobalChannelTrafficShapingHandler extends AbstractTrafficShapingHandler
{
    private static final InternalLogger logger;
    final ConcurrentMap<Integer, PerChannel> channelQueues;
    private final AtomicLong queuesSize;
    private final AtomicLong cumulativeWrittenBytes;
    private final AtomicLong cumulativeReadBytes;
    long maxGlobalWriteSize;
    private volatile long writeChannelLimit;
    private volatile long readChannelLimit;
    private static final float DEFAULT_DEVIATION = 0.1f;
    private static final float MAX_DEVIATION = 0.4f;
    private static final float DEFAULT_SLOWDOWN = 0.4f;
    private static final float DEFAULT_ACCELERATION = -0.1f;
    private volatile float maxDeviation;
    private volatile float accelerationFactor;
    private volatile float slowDownFactor;
    private volatile boolean readDeviationActive;
    private volatile boolean writeDeviationActive;
    
    void createGlobalTrafficCounter(final Timer timer) {
        this.setMaxDeviation(0.1f, 0.4f, -0.1f);
        if (timer == null) {
            throw new IllegalArgumentException("Timer must not be null");
        }
        final TrafficCounter tc = new GlobalChannelTrafficCounter(this, timer, "GlobalChannelTC", this.checkInterval);
        this.setTrafficCounter(tc);
        tc.start();
    }
    
    @Override
    int userDefinedWritabilityIndex() {
        return 3;
    }
    
    public GlobalChannelTrafficShapingHandler(final Timer timer, final long writeGlobalLimit, final long readGlobalLimit, final long writeChannelLimit, final long readChannelLimit, final long checkInterval, final long maxTime) {
        super(timer, writeGlobalLimit, readGlobalLimit, checkInterval, maxTime);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter(timer);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
    }
    
    public GlobalChannelTrafficShapingHandler(final Timer timer, final long writeGlobalLimit, final long readGlobalLimit, final long writeChannelLimit, final long readChannelLimit, final long checkInterval) {
        super(timer, writeGlobalLimit, readGlobalLimit, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final Timer timer, final long writeGlobalLimit, final long readGlobalLimit, final long writeChannelLimit, final long readChannelLimit) {
        super(timer, writeGlobalLimit, readGlobalLimit);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final Timer timer, final long checkInterval) {
        super(timer, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final Timer timer) {
        super(timer);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long writeChannelLimit, final long readChannelLimit, final long checkInterval, final long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long writeChannelLimit, final long readChannelLimit, final long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long writeChannelLimit, final long readChannelLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter(timer);
    }
    
    public GlobalChannelTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer) {
        super(objectSizeEstimator, timer);
        this.channelQueues = new ConcurrentHashMap<Integer, PerChannel>();
        this.queuesSize = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.maxGlobalWriteSize = 419430400L;
        this.createGlobalTrafficCounter(timer);
    }
    
    public float maxDeviation() {
        return this.maxDeviation;
    }
    
    public float accelerationFactor() {
        return this.accelerationFactor;
    }
    
    public float slowDownFactor() {
        return this.slowDownFactor;
    }
    
    public void setMaxDeviation(final float maxDeviation, final float slowDownFactor, final float accelerationFactor) {
        if (maxDeviation > 0.4f) {
            throw new IllegalArgumentException("maxDeviation must be <= 0.4");
        }
        if (slowDownFactor < 0.0f) {
            throw new IllegalArgumentException("slowDownFactor must be >= 0");
        }
        if (accelerationFactor > 0.0f) {
            throw new IllegalArgumentException("accelerationFactor must be <= 0");
        }
        this.maxDeviation = maxDeviation;
        this.accelerationFactor = 1.0f + accelerationFactor;
        this.slowDownFactor = 1.0f + slowDownFactor;
    }
    
    private void computeDeviationCumulativeBytes() {
        long maxWrittenBytes = 0L;
        long maxReadBytes = 0L;
        long minWrittenBytes = Long.MAX_VALUE;
        long minReadBytes = Long.MAX_VALUE;
        for (final PerChannel perChannel : this.channelQueues.values()) {
            long value = perChannel.channelTrafficCounter.getCumulativeWrittenBytes();
            if (maxWrittenBytes < value) {
                maxWrittenBytes = value;
            }
            if (minWrittenBytes > value) {
                minWrittenBytes = value;
            }
            value = perChannel.channelTrafficCounter.getCumulativeReadBytes();
            if (maxReadBytes < value) {
                maxReadBytes = value;
            }
            if (minReadBytes > value) {
                minReadBytes = value;
            }
        }
        final boolean multiple = this.channelQueues.size() > 1;
        this.readDeviationActive = (multiple && minReadBytes < maxReadBytes / 2L);
        this.writeDeviationActive = (multiple && minWrittenBytes < maxWrittenBytes / 2L);
        this.cumulativeWrittenBytes.set(maxWrittenBytes);
        this.cumulativeReadBytes.set(maxReadBytes);
    }
    
    @Override
    protected void doAccounting(final TrafficCounter counter) {
        this.computeDeviationCumulativeBytes();
        super.doAccounting(counter);
    }
    
    private long computeBalancedWait(final float maxLocal, final float maxGlobal, long wait) {
        if (maxGlobal == 0.0f) {
            return wait;
        }
        float ratio = maxLocal / maxGlobal;
        if (ratio > this.maxDeviation) {
            if (ratio < 1.0f - this.maxDeviation) {
                return wait;
            }
            ratio = this.slowDownFactor;
            if (wait < 10L) {
                wait = 10L;
            }
        }
        else {
            ratio = this.accelerationFactor;
        }
        return (long)(wait * ratio);
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
    
    public void configureChannel(final long newWriteLimit, final long newReadLimit) {
        this.writeChannelLimit = newWriteLimit;
        this.readChannelLimit = newReadLimit;
        final long now = TrafficCounter.milliSecondFromNano();
        for (final PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }
    
    public long getWriteChannelLimit() {
        return this.writeChannelLimit;
    }
    
    public void setWriteChannelLimit(final long writeLimit) {
        this.writeChannelLimit = writeLimit;
        final long now = TrafficCounter.milliSecondFromNano();
        for (final PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }
    
    public long getReadChannelLimit() {
        return this.readChannelLimit;
    }
    
    public void setReadChannelLimit(final long readLimit) {
        this.readChannelLimit = readLimit;
        final long now = TrafficCounter.milliSecondFromNano();
        for (final PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }
    
    public final void release() {
        this.trafficCounter.stop();
    }
    
    private PerChannel getOrSetPerChannel(final ChannelHandlerContext ctx) {
        final Channel channel = ctx.getChannel();
        final Integer key = channel.hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = new PerChannel();
            perChannel.messagesQueue = new LinkedList<ToSend>();
            perChannel.channelTrafficCounter = new TrafficCounter(this, null, "ChannelTC" + ctx.getChannel().hashCode(), this.checkInterval);
            perChannel.queueSize = 0L;
            perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
            this.channelQueues.put(key, perChannel);
        }
        return perChannel;
    }
    
    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.getOrSetPerChannel(ctx);
        this.trafficCounter.resetCumulativeTime();
        super.channelConnected(ctx, e);
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.trafficCounter.resetCumulativeTime();
        final Channel channel = ctx.getChannel();
        final Integer key = channel.hashCode();
        final PerChannel perChannel = this.channelQueues.remove(key);
        if (perChannel != null) {
            synchronized (perChannel) {
                this.queuesSize.addAndGet(-perChannel.queueSize);
                perChannel.messagesQueue.clear();
            }
        }
        this.releaseWriteSuspended(ctx);
        this.releaseReadSuspended(ctx);
        super.channelClosed(ctx, e);
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        final long now = TrafficCounter.milliSecondFromNano();
        try {
            final ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(ctx);
            final long size = this.calculateSize(evt.getMessage());
            if (size > 0L) {
                final long waitGlobal = this.trafficCounter.readTimeToWait(size, this.getReadLimit(), this.maxTime, now);
                final Integer key = ctx.getChannel().hashCode();
                final PerChannel perChannel = this.channelQueues.get(key);
                long wait = 0L;
                if (perChannel != null) {
                    wait = perChannel.channelTrafficCounter.readTimeToWait(size, this.readChannelLimit, this.maxTime, now);
                    if (this.readDeviationActive) {
                        long maxLocalRead = perChannel.channelTrafficCounter.getCumulativeReadBytes();
                        long maxGlobalRead = this.cumulativeReadBytes.get();
                        if (maxLocalRead <= 0L) {
                            maxLocalRead = 0L;
                        }
                        if (maxGlobalRead < maxLocalRead) {
                            maxGlobalRead = maxLocalRead;
                        }
                        wait = this.computeBalancedWait((float)maxLocalRead, (float)maxGlobalRead, wait);
                    }
                }
                if (wait < waitGlobal) {
                    wait = waitGlobal;
                }
                wait = this.checkWaitReadTime(ctx, wait, now);
                if (wait >= 10L) {
                    if (this.release.get()) {
                        return;
                    }
                    final Channel channel = ctx.getChannel();
                    if (channel != null && channel.isConnected()) {
                        if (GlobalChannelTrafficShapingHandler.logger.isDebugEnabled()) {
                            GlobalChannelTrafficShapingHandler.logger.debug("Read suspend: " + wait + ':' + channel.isReadable() + ':' + rws.readSuspend);
                        }
                        if (this.timer == null) {
                            Thread.sleep(wait);
                            return;
                        }
                        if (channel.isReadable() && !rws.readSuspend) {
                            rws.readSuspend = true;
                            channel.setReadable(false);
                            if (GlobalChannelTrafficShapingHandler.logger.isDebugEnabled()) {
                                GlobalChannelTrafficShapingHandler.logger.debug("Suspend final status => " + channel.isReadable() + ':' + rws.readSuspend);
                            }
                            if (rws.reopenReadTimerTask == null) {
                                rws.reopenReadTimerTask = new ReopenReadTimerTask(ctx);
                            }
                            this.timeout = this.timer.newTimeout(rws.reopenReadTimerTask, wait, TimeUnit.MILLISECONDS);
                        }
                    }
                }
            }
        }
        finally {
            this.informReadOperation(ctx, now);
            ctx.sendUpstream(evt);
        }
    }
    
    protected long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
        final Integer key = ctx.getChannel().hashCode();
        final PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null && wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
            wait = this.maxTime;
        }
        return wait;
    }
    
    protected void informReadOperation(final ChannelHandlerContext ctx, final long now) {
        final Integer key = ctx.getChannel().hashCode();
        final PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }
    
    protected long maximumCumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }
    
    protected long maximumCumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }
    
    public Collection<TrafficCounter> channelTrafficCounters() {
        return new AbstractCollection<TrafficCounter>() {
            @Override
            public Iterator<TrafficCounter> iterator() {
                return new Iterator<TrafficCounter>() {
                    final Iterator<PerChannel> iter = GlobalChannelTrafficShapingHandler.this.channelQueues.values().iterator();
                    
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    public TrafficCounter next() {
                        return this.iter.next().channelTrafficCounter;
                    }
                    
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            
            @Override
            public int size() {
                return GlobalChannelTrafficShapingHandler.this.channelQueues.size();
            }
        };
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        long wait = 0L;
        final long size = this.calculateSize(evt.getMessage());
        final long now = TrafficCounter.milliSecondFromNano();
        try {
            if (size > 0L) {
                final long waitGlobal = this.trafficCounter.writeTimeToWait(size, this.getWriteLimit(), this.maxTime, now);
                final Integer key = ctx.getChannel().hashCode();
                final PerChannel perChannel = this.channelQueues.get(key);
                if (perChannel != null) {
                    wait = perChannel.channelTrafficCounter.writeTimeToWait(size, this.writeChannelLimit, this.maxTime, now);
                    if (this.writeDeviationActive) {
                        long maxLocalWrite = perChannel.channelTrafficCounter.getCumulativeWrittenBytes();
                        long maxGlobalWrite = this.cumulativeWrittenBytes.get();
                        if (maxLocalWrite <= 0L) {
                            maxLocalWrite = 0L;
                        }
                        if (maxGlobalWrite < maxLocalWrite) {
                            maxGlobalWrite = maxLocalWrite;
                        }
                        wait = this.computeBalancedWait((float)maxLocalWrite, (float)maxGlobalWrite, wait);
                    }
                }
                if (wait < waitGlobal) {
                    wait = waitGlobal;
                }
                if (wait < 10L || this.release.get()) {
                    wait = 0L;
                }
            }
        }
        finally {
            this.submitWrite(ctx, evt, size, wait, now);
        }
    }
    
    protected void submitWrite(final ChannelHandlerContext ctx, final MessageEvent evt, final long size, final long writedelay, final long now) throws Exception {
        final Channel channel = ctx.getChannel();
        final Integer key = channel.hashCode();
        PerChannel perChannel = this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = this.getOrSetPerChannel(ctx);
        }
        long delay = writedelay;
        boolean globalSizeExceeded = false;
        ToSend newToSend;
        synchronized (perChannel) {
            if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
                if (!channel.isConnected()) {
                    return;
                }
                this.trafficCounter.bytesRealWriteFlowControl(size);
                perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
                ctx.sendDownstream(evt);
                perChannel.lastWriteTimestamp = now;
                return;
            }
            else {
                if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
                    delay = this.maxTime;
                }
                if (this.timer == null) {
                    Thread.sleep(delay);
                    if (!ctx.getChannel().isConnected()) {
                        return;
                    }
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                    perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
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
                GlobalChannelTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private void sendAllValid(final ChannelHandlerContext ctx, final PerChannel perChannel, final long now) throws Exception {
        synchronized (perChannel) {
            while (!perChannel.messagesQueue.isEmpty()) {
                final ToSend newToSend = perChannel.messagesQueue.remove(0);
                if (newToSend.relativeTimeAction > now) {
                    perChannel.messagesQueue.add(0, newToSend);
                    break;
                }
                if (!ctx.getChannel().isConnected()) {
                    break;
                }
                final long size = newToSend.size;
                this.trafficCounter.bytesRealWriteFlowControl(size);
                perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
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
    public String toString() {
        return new StringBuilder(340).append(super.toString()).append(" Write Channel Limit: ").append(this.writeChannelLimit).append(" Read Channel Limit: ").append(this.readChannelLimit).toString();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
    }
    
    static final class PerChannel
    {
        List<ToSend> messagesQueue;
        TrafficCounter channelTrafficCounter;
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
