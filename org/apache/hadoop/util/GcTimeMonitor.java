// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

public class GcTimeMonitor extends Thread
{
    private final long maxGcTimePercentage;
    private final long observationWindowMs;
    private final long sleepIntervalMs;
    private final GcTimeAlertHandler alertHandler;
    private final List<GarbageCollectorMXBean> gcBeans;
    private final TsAndData[] gcDataBuf;
    private int bufSize;
    private int startIdx;
    private int endIdx;
    private long startTime;
    private final GcData curData;
    private volatile boolean shouldRun;
    
    public GcTimeMonitor(final long observationWindowMs, final long sleepIntervalMs, final int maxGcTimePercentage, final GcTimeAlertHandler alertHandler) {
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.curData = new GcData();
        this.shouldRun = true;
        Preconditions.checkArgument(observationWindowMs > 0L);
        Preconditions.checkArgument(sleepIntervalMs > 0L && sleepIntervalMs < observationWindowMs);
        Preconditions.checkArgument(maxGcTimePercentage >= 0 && maxGcTimePercentage <= 100);
        this.observationWindowMs = observationWindowMs;
        this.sleepIntervalMs = sleepIntervalMs;
        this.maxGcTimePercentage = maxGcTimePercentage;
        this.alertHandler = alertHandler;
        this.bufSize = (int)(observationWindowMs / sleepIntervalMs + 2L);
        Preconditions.checkArgument(this.bufSize <= 131072);
        this.gcDataBuf = new TsAndData[this.bufSize];
        for (int i = 0; i < this.bufSize; ++i) {
            this.gcDataBuf[i] = new TsAndData();
        }
        this.setDaemon(true);
        this.setName("GcTimeMonitor obsWindow = " + observationWindowMs + ", sleepInterval = " + sleepIntervalMs + ", maxGcTimePerc = " + maxGcTimePercentage);
    }
    
    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        this.curData.timestamp = this.startTime;
        this.gcDataBuf[this.startIdx].setValues(this.startTime, 0L);
        while (this.shouldRun) {
            try {
                Thread.sleep(this.sleepIntervalMs);
            }
            catch (InterruptedException ie) {
                return;
            }
            this.calculateGCTimePercentageWithinObservedInterval();
            if (this.alertHandler != null && this.curData.gcTimePercentage > this.maxGcTimePercentage) {
                this.alertHandler.alert(this.curData.clone());
            }
        }
    }
    
    public void shutdown() {
        this.shouldRun = false;
    }
    
    public GcData getLatestGcData() {
        return this.curData.clone();
    }
    
    private void calculateGCTimePercentageWithinObservedInterval() {
        final long prevTotalGcTime = this.curData.totalGcTime;
        long totalGcTime = 0L;
        long totalGcCount = 0L;
        for (final GarbageCollectorMXBean gcBean : this.gcBeans) {
            totalGcTime += gcBean.getCollectionTime();
            totalGcCount += gcBean.getCollectionCount();
        }
        final long gcTimeWithinSleepInterval = totalGcTime - prevTotalGcTime;
        final long ts = System.currentTimeMillis();
        final long gcMonitorRunTime = ts - this.startTime;
        this.endIdx = (this.endIdx + 1) % this.bufSize;
        this.gcDataBuf[this.endIdx].setValues(ts, gcTimeWithinSleepInterval);
        final long startObsWindowTs = ts - this.observationWindowMs;
        while (this.gcDataBuf[this.startIdx].ts < startObsWindowTs && this.startIdx != this.endIdx) {
            this.startIdx = (this.startIdx + 1) % this.bufSize;
        }
        long gcTimeWithinObservationWindow = Math.min(this.gcDataBuf[this.startIdx].gcPause, this.gcDataBuf[this.startIdx].ts - startObsWindowTs);
        if (this.startIdx != this.endIdx) {
            for (int i = (this.startIdx + 1) % this.bufSize; i != this.endIdx; i = (i + 1) % this.bufSize) {
                gcTimeWithinObservationWindow += this.gcDataBuf[i].gcPause;
            }
        }
        this.curData.update(ts, gcMonitorRunTime, totalGcTime, totalGcCount, (int)(gcTimeWithinObservationWindow * 100L / Math.min(this.observationWindowMs, gcMonitorRunTime)));
    }
    
    public static class GcData implements Cloneable
    {
        private long timestamp;
        private long gcMonitorRunTime;
        private long totalGcTime;
        private long totalGcCount;
        private int gcTimePercentage;
        
        public long getTimestamp() {
            return this.timestamp;
        }
        
        public long getGcMonitorRunTime() {
            return this.gcMonitorRunTime;
        }
        
        public long getAccumulatedGcTime() {
            return this.totalGcTime;
        }
        
        public long getAccumulatedGcCount() {
            return this.totalGcCount;
        }
        
        public int getGcTimePercentage() {
            return this.gcTimePercentage;
        }
        
        private synchronized void update(final long inTimestamp, final long inGcMonitorRunTime, final long inTotalGcTime, final long inTotalGcCount, final int inGcTimePercentage) {
            this.timestamp = inTimestamp;
            this.gcMonitorRunTime = inGcMonitorRunTime;
            this.totalGcTime = inTotalGcTime;
            this.totalGcCount = inTotalGcCount;
            this.gcTimePercentage = inGcTimePercentage;
        }
        
        public synchronized GcData clone() {
            try {
                return (GcData)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static class TsAndData
    {
        private long ts;
        private long gcPause;
        
        void setValues(final long inTs, final long inGcPause) {
            this.ts = inTs;
            this.gcPause = inGcPause;
        }
    }
    
    public interface GcTimeAlertHandler
    {
        void alert(final GcData p0);
    }
}
