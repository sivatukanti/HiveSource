// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.logging.InternalLogger;

public class TrafficCounter
{
    private static final InternalLogger logger;
    private final AtomicLong currentWrittenBytes;
    private final AtomicLong currentReadBytes;
    private long writingTime;
    private long readingTime;
    private final AtomicLong cumulativeWrittenBytes;
    private final AtomicLong cumulativeReadBytes;
    private long lastCumulativeTime;
    private long lastWriteThroughput;
    private long lastReadThroughput;
    final AtomicLong lastTime;
    private volatile long lastWrittenBytes;
    private volatile long lastReadBytes;
    private volatile long lastWritingTime;
    private volatile long lastReadingTime;
    private final AtomicLong realWrittenBytes;
    private long realWriteThroughput;
    final AtomicLong checkInterval;
    final String name;
    final AbstractTrafficShapingHandler trafficShapingHandler;
    final Timer timer;
    TimerTask timerTask;
    volatile Timeout timeout;
    volatile boolean monitorActive;
    
    public static long milliSecondFromNano() {
        return System.nanoTime() / 1000000L;
    }
    
    public void start() {
        if (this.monitorActive) {
            return;
        }
        this.lastTime.set(milliSecondFromNano());
        if (this.checkInterval.get() > 0L && this.timer != null) {
            this.monitorActive = true;
            this.timerTask = new TrafficMonitoringTask(this.trafficShapingHandler, this);
            this.timeout = this.timer.newTimeout(this.timerTask, this.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }
    
    public void stop() {
        if (!this.monitorActive) {
            return;
        }
        this.monitorActive = false;
        this.resetAccounting(milliSecondFromNano());
        if (this.trafficShapingHandler != null) {
            this.trafficShapingHandler.doAccounting(this);
        }
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }
    
    void resetAccounting(final long newLastTime) {
        final long interval = newLastTime - this.lastTime.getAndSet(newLastTime);
        if (interval == 0L) {
            return;
        }
        this.lastReadBytes = this.currentReadBytes.getAndSet(0L);
        this.lastWrittenBytes = this.currentWrittenBytes.getAndSet(0L);
        this.lastReadThroughput = this.lastReadBytes * 1000L / interval;
        this.lastWriteThroughput = this.lastWrittenBytes * 1000L / interval;
        this.realWriteThroughput = this.realWrittenBytes.getAndSet(0L) * 1000L / interval;
        this.lastWritingTime = Math.max(this.lastWritingTime, this.writingTime);
        this.lastReadingTime = Math.max(this.lastReadingTime, this.readingTime);
    }
    
    public TrafficCounter(final AbstractTrafficShapingHandler trafficShapingHandler, final Timer timer, final String name, final long checkInterval) {
        this.currentWrittenBytes = new AtomicLong();
        this.currentReadBytes = new AtomicLong();
        this.cumulativeWrittenBytes = new AtomicLong();
        this.cumulativeReadBytes = new AtomicLong();
        this.lastTime = new AtomicLong();
        this.realWrittenBytes = new AtomicLong();
        this.checkInterval = new AtomicLong(1000L);
        if (trafficShapingHandler == null) {
            throw new IllegalArgumentException("TrafficShapingHandler must not be null");
        }
        this.trafficShapingHandler = trafficShapingHandler;
        this.timer = timer;
        this.name = name;
        this.lastCumulativeTime = System.currentTimeMillis();
        this.writingTime = milliSecondFromNano();
        this.readingTime = this.writingTime;
        this.lastWritingTime = this.writingTime;
        this.lastReadingTime = this.writingTime;
        this.configure(checkInterval);
    }
    
    public void configure(final long newcheckInterval) {
        final long newInterval = newcheckInterval / 10L * 10L;
        if (this.checkInterval.getAndSet(newInterval) != newInterval) {
            if (newInterval <= 0L) {
                this.stop();
                this.lastTime.set(milliSecondFromNano());
            }
            else {
                this.start();
            }
        }
    }
    
    void bytesRecvFlowControl(final long recv) {
        this.currentReadBytes.addAndGet(recv);
        this.cumulativeReadBytes.addAndGet(recv);
    }
    
    void bytesWriteFlowControl(final long write) {
        this.currentWrittenBytes.addAndGet(write);
        this.cumulativeWrittenBytes.addAndGet(write);
    }
    
    void bytesRealWriteFlowControl(final long write) {
        this.realWrittenBytes.addAndGet(write);
    }
    
    public long getCheckInterval() {
        return this.checkInterval.get();
    }
    
    public long getLastReadThroughput() {
        return this.lastReadThroughput;
    }
    
    public long getLastWriteThroughput() {
        return this.lastWriteThroughput;
    }
    
    public long getLastReadBytes() {
        return this.lastReadBytes;
    }
    
    public long getLastWrittenBytes() {
        return this.lastWrittenBytes;
    }
    
    public long getCurrentReadBytes() {
        return this.currentReadBytes.get();
    }
    
    public long getCurrentWrittenBytes() {
        return this.currentWrittenBytes.get();
    }
    
    public long getLastTime() {
        return this.lastTime.get();
    }
    
    public long getCumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }
    
    public long getCumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }
    
    public long getLastCumulativeTime() {
        return this.lastCumulativeTime;
    }
    
    public AtomicLong getRealWrittenBytes() {
        return this.realWrittenBytes;
    }
    
    public long getRealWriteThroughput() {
        return this.realWriteThroughput;
    }
    
    public void resetCumulativeTime() {
        this.lastCumulativeTime = System.currentTimeMillis();
        this.cumulativeReadBytes.set(0L);
        this.cumulativeWrittenBytes.set(0L);
    }
    
    @Deprecated
    public long readTimeToWait(final long size, final long limitTraffic, final long maxTime) {
        return this.readTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
    }
    
    public long readTimeToWait(final long size, final long limitTraffic, final long maxTime, final long now) {
        this.bytesRecvFlowControl(size);
        if (size == 0L || limitTraffic == 0L) {
            return 0L;
        }
        final long lastTimeCheck = this.lastTime.get();
        final long sum = this.currentReadBytes.get();
        final long localReadingTime = this.readingTime;
        final long lastRB = this.lastReadBytes;
        final long interval = now - lastTimeCheck;
        final long pastDelay = Math.max(this.lastReadingTime - lastTimeCheck, 0L);
        if (interval > 10L) {
            long time = (sum * 1000L / limitTraffic - interval + pastDelay) / 10L * 10L;
            if (time > 10L) {
                if (TrafficCounter.logger.isDebugEnabled()) {
                    TrafficCounter.logger.debug("Time: " + time + ':' + sum + ':' + interval + ':' + pastDelay);
                }
                if (time > maxTime && now + time - localReadingTime > maxTime) {
                    time = maxTime;
                }
                this.readingTime = Math.max(localReadingTime, now + time);
                return time;
            }
            this.readingTime = Math.max(localReadingTime, now);
            return 0L;
        }
        else {
            final long lastsum = sum + lastRB;
            final long lastinterval = interval + this.checkInterval.get();
            long time2 = (lastsum * 1000L / limitTraffic - lastinterval + pastDelay) / 10L * 10L;
            if (time2 > 10L) {
                if (TrafficCounter.logger.isDebugEnabled()) {
                    TrafficCounter.logger.debug("Time: " + time2 + ':' + lastsum + ':' + lastinterval + ':' + pastDelay);
                }
                if (time2 > maxTime && now + time2 - localReadingTime > maxTime) {
                    time2 = maxTime;
                }
                this.readingTime = Math.max(localReadingTime, now + time2);
                return time2;
            }
            this.readingTime = Math.max(localReadingTime, now);
            return 0L;
        }
    }
    
    @Deprecated
    public long writeTimeToWait(final long size, final long limitTraffic, final long maxTime) {
        return this.writeTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
    }
    
    public long writeTimeToWait(final long size, final long limitTraffic, final long maxTime, final long now) {
        this.bytesWriteFlowControl(size);
        if (size == 0L || limitTraffic == 0L) {
            return 0L;
        }
        final long lastTimeCheck = this.lastTime.get();
        final long sum = this.currentWrittenBytes.get();
        final long lastWB = this.lastWrittenBytes;
        final long localWritingTime = this.writingTime;
        final long pastDelay = Math.max(this.lastWritingTime - lastTimeCheck, 0L);
        final long interval = now - lastTimeCheck;
        if (interval > 10L) {
            long time = (sum * 1000L / limitTraffic - interval + pastDelay) / 10L * 10L;
            if (time > 10L) {
                if (TrafficCounter.logger.isDebugEnabled()) {
                    TrafficCounter.logger.debug("Time: " + time + ':' + sum + ':' + interval + ':' + pastDelay);
                }
                if (time > maxTime && now + time - localWritingTime > maxTime) {
                    time = maxTime;
                }
                this.writingTime = Math.max(localWritingTime, now + time);
                return time;
            }
            this.writingTime = Math.max(localWritingTime, now);
            return 0L;
        }
        else {
            final long lastsum = sum + lastWB;
            final long lastinterval = interval + this.checkInterval.get();
            long time2 = (lastsum * 1000L / limitTraffic - lastinterval + pastDelay) / 10L * 10L;
            if (time2 > 10L) {
                if (TrafficCounter.logger.isDebugEnabled()) {
                    TrafficCounter.logger.debug("Time: " + time2 + ':' + lastsum + ':' + lastinterval + ':' + pastDelay);
                }
                if (time2 > maxTime && now + time2 - localWritingTime > maxTime) {
                    time2 = maxTime;
                }
                this.writingTime = Math.max(localWritingTime, now + time2);
                return time2;
            }
            this.writingTime = Math.max(localWritingTime, now);
            return 0L;
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return new StringBuilder(165).append("Monitor ").append(this.name).append(" Current Speed Read: ").append(this.lastReadThroughput >> 10).append(" KB/s, ").append("Asked Write: ").append(this.lastWriteThroughput >> 10).append(" KB/s, ").append("Real Write: ").append(this.realWriteThroughput >> 10).append(" KB/s, ").append("Current Read: ").append(this.currentReadBytes.get() >> 10).append(" KB, ").append("Current asked Write: ").append(this.currentWrittenBytes.get() >> 10).append(" KB, ").append("Current real Write: ").append(this.realWrittenBytes.get() >> 10).append(" KB").toString();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
    }
    
    private static final class TrafficMonitoringTask implements TimerTask
    {
        private final AbstractTrafficShapingHandler trafficShapingHandler1;
        private final TrafficCounter counter;
        
        TrafficMonitoringTask(final AbstractTrafficShapingHandler trafficShapingHandler, final TrafficCounter counter) {
            this.trafficShapingHandler1 = trafficShapingHandler;
            this.counter = counter;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (!this.counter.monitorActive) {
                return;
            }
            this.counter.resetAccounting(TrafficCounter.milliSecondFromNano());
            if (this.trafficShapingHandler1 != null) {
                this.trafficShapingHandler1.doAccounting(this.counter);
            }
            this.counter.timer.newTimeout(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }
}
