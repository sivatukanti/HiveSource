// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.log;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import java.util.Iterator;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.util.Timer;

public class LogThrottlingHelper
{
    public static final LogAction DO_NOT_LOG;
    private static final String DEFAULT_RECORDER_NAME = "__DEFAULT_RECORDER_NAME__";
    private final long minLogPeriodMs;
    private String primaryRecorderName;
    private final Timer timer;
    private final Map<String, LoggingAction> currentLogs;
    private long lastLogTimestampMs;
    
    public LogThrottlingHelper(final long minLogPeriodMs) {
        this(minLogPeriodMs, null);
    }
    
    public LogThrottlingHelper(final long minLogPeriodMs, final String primaryRecorderName) {
        this(minLogPeriodMs, primaryRecorderName, new Timer());
    }
    
    @VisibleForTesting
    LogThrottlingHelper(final long minLogPeriodMs, final String primaryRecorderName, final Timer timer) {
        this.lastLogTimestampMs = Long.MIN_VALUE;
        this.minLogPeriodMs = minLogPeriodMs;
        this.primaryRecorderName = primaryRecorderName;
        this.timer = timer;
        this.currentLogs = new HashMap<String, LoggingAction>();
    }
    
    public LogAction record(final double... values) {
        return this.record("__DEFAULT_RECORDER_NAME__", this.timer.monotonicNow(), values);
    }
    
    public LogAction record(final String recorderName, final long currentTimeMs, final double... values) {
        if (this.primaryRecorderName == null) {
            this.primaryRecorderName = recorderName;
        }
        LoggingAction currentLog = this.currentLogs.get(recorderName);
        if (currentLog == null || currentLog.hasLogged()) {
            currentLog = new LoggingAction(values.length);
            if (!this.currentLogs.containsKey(recorderName)) {
                currentLog.setShouldLog();
            }
            this.currentLogs.put(recorderName, currentLog);
        }
        currentLog.recordValues(values);
        if (this.primaryRecorderName.equals(recorderName) && currentTimeMs - this.minLogPeriodMs >= this.lastLogTimestampMs) {
            this.lastLogTimestampMs = currentTimeMs;
            for (final LoggingAction log : this.currentLogs.values()) {
                log.setShouldLog();
            }
        }
        if (currentLog.shouldLog()) {
            currentLog.setHasLogged();
            return currentLog;
        }
        return LogThrottlingHelper.DO_NOT_LOG;
    }
    
    static {
        DO_NOT_LOG = new NoLogAction();
    }
    
    private static class LoggingAction implements LogAction
    {
        private int count;
        private final SummaryStatistics[] stats;
        private boolean shouldLog;
        private boolean hasLogged;
        
        LoggingAction(final int valueCount) {
            this.count = 0;
            this.shouldLog = false;
            this.hasLogged = false;
            this.stats = new SummaryStatistics[valueCount];
            for (int i = 0; i < this.stats.length; ++i) {
                this.stats[i] = new SummaryStatistics();
            }
        }
        
        @Override
        public int getCount() {
            return this.count;
        }
        
        @Override
        public SummaryStatistics getStats(final int idx) {
            if (idx < 0 || idx >= this.stats.length) {
                throw new IllegalArgumentException("Requested stats at idx " + idx + " but this log only maintains " + this.stats.length + " stats");
            }
            return this.stats[idx];
        }
        
        @Override
        public boolean shouldLog() {
            return this.shouldLog;
        }
        
        private void setShouldLog() {
            this.shouldLog = true;
        }
        
        private boolean hasLogged() {
            return this.hasLogged;
        }
        
        private void setHasLogged() {
            this.hasLogged = true;
        }
        
        private void recordValues(final double... values) {
            if (values.length != this.stats.length) {
                throw new IllegalArgumentException("received " + values.length + " values but expected " + this.stats.length);
            }
            ++this.count;
            for (int i = 0; i < values.length; ++i) {
                this.stats[i].addValue(values[i]);
            }
        }
    }
    
    private static class NoLogAction implements LogAction
    {
        @Override
        public int getCount() {
            throw new IllegalStateException("Cannot be logged yet!");
        }
        
        @Override
        public SummaryStatistics getStats(final int idx) {
            throw new IllegalStateException("Cannot be logged yet!");
        }
        
        @Override
        public boolean shouldLog() {
            return false;
        }
    }
    
    public interface LogAction
    {
        int getCount();
        
        SummaryStatistics getStats(final int p0);
        
        boolean shouldLog();
    }
}
