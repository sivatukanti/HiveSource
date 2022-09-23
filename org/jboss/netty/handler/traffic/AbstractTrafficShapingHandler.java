// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.DefaultObjectSizeEstimator;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.SimpleChannelHandler;

public abstract class AbstractTrafficShapingHandler extends SimpleChannelHandler implements ExternalResourceReleasable
{
    static InternalLogger logger;
    public static final long DEFAULT_CHECK_INTERVAL = 1000L;
    public static final long DEFAULT_MAX_TIME = 15000L;
    static final long DEFAULT_MAX_SIZE = 4194304L;
    static final long MINIMAL_WAIT = 10L;
    static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
    static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
    static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;
    protected TrafficCounter trafficCounter;
    private ObjectSizeEstimator objectSizeEstimator;
    protected Timer timer;
    volatile Timeout timeout;
    private volatile long writeLimit;
    private volatile long readLimit;
    protected volatile long checkInterval;
    protected volatile long maxTime;
    volatile long maxWriteDelay;
    volatile long maxWriteSize;
    final AtomicBoolean release;
    final int index;
    
    int userDefinedWritabilityIndex() {
        if (this instanceof GlobalChannelTrafficShapingHandler) {
            return 3;
        }
        if (this instanceof GlobalTrafficShapingHandler) {
            return 2;
        }
        return 1;
    }
    
    private void init(final ObjectSizeEstimator newObjectSizeEstimator, final Timer newTimer, final long newWriteLimit, final long newReadLimit, final long newCheckInterval, final long newMaxTime) {
        if (newMaxTime <= 0L) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.objectSizeEstimator = newObjectSizeEstimator;
        this.timer = newTimer;
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        this.checkInterval = newCheckInterval;
        this.maxTime = newMaxTime;
    }
    
    void setTrafficCounter(final TrafficCounter newTrafficCounter) {
        this.trafficCounter = newTrafficCounter;
    }
    
    protected AbstractTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, checkInterval, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, 1000L, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(objectSizeEstimator, timer, writeLimit, readLimit, 1000L, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final Timer timer) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(new SimpleObjectSizeEstimator(), timer, 0L, 0L, 1000L, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(objectSizeEstimator, timer, 0L, 0L, 1000L, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final Timer timer, final long checkInterval) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(new SimpleObjectSizeEstimator(), timer, 0L, 0L, checkInterval, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long checkInterval) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(objectSizeEstimator, timer, 0L, 0L, checkInterval, 15000L);
    }
    
    protected AbstractTrafficShapingHandler(final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, checkInterval, maxTime);
    }
    
    protected AbstractTrafficShapingHandler(final ObjectSizeEstimator objectSizeEstimator, final Timer timer, final long writeLimit, final long readLimit, final long checkInterval, final long maxTime) {
        this.checkInterval = 1000L;
        this.maxTime = 15000L;
        this.maxWriteDelay = 4000L;
        this.maxWriteSize = 4194304L;
        this.release = new AtomicBoolean(false);
        this.index = this.userDefinedWritabilityIndex();
        this.init(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
    }
    
    public void configure(final long newWriteLimit, final long newReadLimit, final long newCheckInterval) {
        this.configure(newWriteLimit, newReadLimit);
        this.configure(newCheckInterval);
    }
    
    public void configure(final long newWriteLimit, final long newReadLimit) {
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }
    
    public void configure(final long newCheckInterval) {
        this.setCheckInterval(newCheckInterval);
    }
    
    public long getWriteLimit() {
        return this.writeLimit;
    }
    
    public void setWriteLimit(final long writeLimit) {
        this.writeLimit = writeLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }
    
    public long getReadLimit() {
        return this.readLimit;
    }
    
    public void setReadLimit(final long readLimit) {
        this.readLimit = readLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }
    
    public long getCheckInterval() {
        return this.checkInterval;
    }
    
    public void setCheckInterval(final long newCheckInterval) {
        this.checkInterval = newCheckInterval;
        if (this.trafficCounter != null) {
            this.trafficCounter.configure(this.checkInterval);
        }
    }
    
    public long getMaxTimeWait() {
        return this.maxTime;
    }
    
    public void setMaxTimeWait(final long maxTime) {
        if (maxTime <= 0L) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.maxTime = maxTime;
    }
    
    public long getMaxWriteDelay() {
        return this.maxWriteDelay;
    }
    
    public void setMaxWriteDelay(final long maxWriteDelay) {
        if (maxWriteDelay <= 0L) {
            throw new IllegalArgumentException("maxWriteDelay must be positive");
        }
        this.maxWriteDelay = maxWriteDelay;
    }
    
    public long getMaxWriteSize() {
        return this.maxWriteSize;
    }
    
    public void setMaxWriteSize(final long maxWriteSize) {
        this.maxWriteSize = maxWriteSize;
    }
    
    protected void doAccounting(final TrafficCounter counter) {
    }
    
    void releaseReadSuspended(final ChannelHandlerContext ctx) {
        final ReadWriteStatus rws = checkAttachment(ctx);
        rws.readSuspend = false;
        ctx.getChannel().setReadable(true);
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        final long now = TrafficCounter.milliSecondFromNano();
        try {
            final ReadWriteStatus rws = checkAttachment(ctx);
            final long size = this.calculateSize(evt.getMessage());
            if (size > 0L && this.trafficCounter != null) {
                long wait = this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime, now);
                wait = this.checkWaitReadTime(ctx, wait, now);
                if (wait >= 10L) {
                    if (this.release.get()) {
                        return;
                    }
                    final Channel channel = ctx.getChannel();
                    if (channel != null && channel.isConnected()) {
                        if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                            AbstractTrafficShapingHandler.logger.debug("Read suspend: " + wait + ':' + channel.isReadable() + ':' + rws.readSuspend);
                        }
                        if (this.timer == null) {
                            Thread.sleep(wait);
                            return;
                        }
                        if (channel.isReadable() && !rws.readSuspend) {
                            rws.readSuspend = true;
                            channel.setReadable(false);
                            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                                AbstractTrafficShapingHandler.logger.debug("Suspend final status => " + channel.isReadable() + ':' + rws.readSuspend);
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
    
    long checkWaitReadTime(final ChannelHandlerContext ctx, final long wait, final long now) {
        return wait;
    }
    
    void informReadOperation(final ChannelHandlerContext ctx, final long now) {
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        long wait = 0L;
        final long size = this.calculateSize(evt.getMessage());
        final long now = TrafficCounter.milliSecondFromNano();
        final Channel channel = ctx.getChannel();
        try {
            if (size > 0L && this.trafficCounter != null) {
                wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime, now);
                if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                    AbstractTrafficShapingHandler.logger.debug("Write suspend: " + wait + ':' + channel.isWritable() + ':' + channel.getUserDefinedWritability(this.index));
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
    
    @Deprecated
    protected void internalSubmitWrite(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        ctx.sendDownstream(evt);
    }
    
    @Deprecated
    protected void submitWrite(final ChannelHandlerContext ctx, final MessageEvent evt, final long delay) throws Exception {
        this.submitWrite(ctx, evt, this.calculateSize(evt.getMessage()), delay, TrafficCounter.milliSecondFromNano());
    }
    
    abstract void submitWrite(final ChannelHandlerContext p0, final MessageEvent p1, final long p2, final long p3, final long p4) throws Exception;
    
    void setWritable(final ChannelHandlerContext ctx, final boolean writable) {
        final Channel channel = ctx.getChannel();
        if (channel.isConnected()) {
            channel.setUserDefinedWritability(this.index, writable);
        }
    }
    
    void checkWriteSuspend(final ChannelHandlerContext ctx, final long delay, final long queueSize) {
        if (queueSize > this.maxWriteSize || delay > this.maxWriteDelay) {
            this.setWritable(ctx, false);
        }
    }
    
    void releaseWriteSuspended(final ChannelHandlerContext ctx) {
        this.setWritable(ctx, true);
    }
    
    public TrafficCounter getTrafficCounter() {
        return this.trafficCounter;
    }
    
    public void releaseExternalResources() {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }
        this.release.set(true);
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }
    
    static ReadWriteStatus checkAttachment(final ChannelHandlerContext ctx) {
        ReadWriteStatus rws = (ReadWriteStatus)ctx.getAttachment();
        if (rws == null) {
            rws = new ReadWriteStatus();
            ctx.setAttachment(rws);
        }
        return rws;
    }
    
    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        checkAttachment(ctx);
        this.setWritable(ctx, true);
        super.channelConnected(ctx, e);
    }
    
    protected long calculateSize(final Object obj) {
        return this.objectSizeEstimator.estimateSize(obj);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(290).append("TrafficShaping with Write Limit: ").append(this.writeLimit).append(" Read Limit: ").append(this.readLimit).append(" CheckInterval: ").append(this.checkInterval).append(" maxDelay: ").append(this.maxWriteDelay).append(" maxSize: ").append(this.maxWriteSize).append(" and Counter: ");
        if (this.trafficCounter != null) {
            builder.append(this.trafficCounter);
        }
        else {
            builder.append("none");
        }
        return builder.toString();
    }
    
    static {
        AbstractTrafficShapingHandler.logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
    }
    
    static final class ReadWriteStatus
    {
        volatile boolean readSuspend;
        volatile TimerTask reopenReadTimerTask;
    }
    
    public static class SimpleObjectSizeEstimator extends DefaultObjectSizeEstimator
    {
        @Override
        public int estimateSize(final Object o) {
            int size;
            if (o instanceof ChannelBuffer) {
                size = ((ChannelBuffer)o).readableBytes();
            }
            else {
                size = super.estimateSize(o);
            }
            return size;
        }
    }
    
    class ReopenReadTimerTask implements TimerTask
    {
        final ChannelHandlerContext ctx;
        
        ReopenReadTimerTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        public void run(final Timeout timeoutArg) throws Exception {
            if (AbstractTrafficShapingHandler.this.release.get()) {
                return;
            }
            final ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(this.ctx);
            final Channel channel = this.ctx.getChannel();
            if (!channel.isConnected()) {
                return;
            }
            if (!channel.isReadable() && !rws.readSuspend) {
                if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                    AbstractTrafficShapingHandler.logger.debug("Not unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                }
                rws.readSuspend = false;
            }
            else {
                if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                    if (channel.isReadable() && rws.readSuspend) {
                        AbstractTrafficShapingHandler.logger.debug("Unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                    }
                    else {
                        AbstractTrafficShapingHandler.logger.debug("Normal unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                    }
                }
                rws.readSuspend = false;
                channel.setReadable(true);
            }
            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                AbstractTrafficShapingHandler.logger.debug("Unsupsend final status => " + channel.isReadable() + ':' + rws.readSuspend);
            }
        }
    }
}
