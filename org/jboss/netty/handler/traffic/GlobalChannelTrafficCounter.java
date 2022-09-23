// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.traffic;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timer;

public class GlobalChannelTrafficCounter extends TrafficCounter
{
    public GlobalChannelTrafficCounter(final GlobalChannelTrafficShapingHandler trafficShapingHandler, final Timer timer, final String name, final long checkInterval) {
        super(trafficShapingHandler, timer, name, checkInterval);
        if (timer == null) {
            throw new IllegalArgumentException("Timer must not be null");
        }
    }
    
    @Override
    public synchronized void start() {
        if (this.monitorActive) {
            return;
        }
        this.lastTime.set(TrafficCounter.milliSecondFromNano());
        final long localCheckInterval = this.checkInterval.get();
        if (localCheckInterval > 0L) {
            this.monitorActive = true;
            this.timerTask = new MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler, this);
            this.timeout = this.timer.newTimeout(this.timerTask, this.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public synchronized void stop() {
        if (!this.monitorActive) {
            return;
        }
        this.monitorActive = false;
        this.resetAccounting(TrafficCounter.milliSecondFromNano());
        this.trafficShapingHandler.doAccounting(this);
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }
    
    @Override
    public void resetCumulativeTime() {
        for (final GlobalChannelTrafficShapingHandler.PerChannel perChannel : ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values()) {
            perChannel.channelTrafficCounter.resetCumulativeTime();
        }
        super.resetCumulativeTime();
    }
    
    private static final class MixedTrafficMonitoringTask implements TimerTask
    {
        private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;
        private final TrafficCounter counter;
        
        MixedTrafficMonitoringTask(final GlobalChannelTrafficShapingHandler trafficShapingHandler, final TrafficCounter counter) {
            this.trafficShapingHandler1 = trafficShapingHandler;
            this.counter = counter;
        }
        
        public void run(final Timeout timeout) throws Exception {
            if (!this.counter.monitorActive) {
                return;
            }
            final long newLastTime = TrafficCounter.milliSecondFromNano();
            this.counter.resetAccounting(newLastTime);
            for (final GlobalChannelTrafficShapingHandler.PerChannel perChannel : this.trafficShapingHandler1.channelQueues.values()) {
                perChannel.channelTrafficCounter.resetAccounting(newLastTime);
            }
            this.trafficShapingHandler1.doAccounting(this.counter);
            this.counter.timer.newTimeout(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }
}
