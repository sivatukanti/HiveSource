// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.impl.MetricsCollectorImpl;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.io.IOException;
import java.util.function.Function;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableRollingAverages extends MutableMetric implements Closeable
{
    private MutableRatesWithAggregation innerMetrics;
    @VisibleForTesting
    static final ScheduledExecutorService SCHEDULER;
    private ScheduledFuture<?> scheduledTask;
    @Nullable
    private Map<String, MutableRate> currentSnapshot;
    private final String avgInfoNameTemplate;
    private final String avgInfoDescTemplate;
    private int numWindows;
    private Map<String, LinkedBlockingDeque<SumAndCount>> averages;
    private static final long WINDOW_SIZE_MS_DEFAULT = 300000L;
    private static final int NUM_WINDOWS_DEFAULT = 36;
    
    public MutableRollingAverages(String metricValueName) {
        this.innerMetrics = new MutableRatesWithAggregation();
        this.scheduledTask = null;
        this.averages = new ConcurrentHashMap<String, LinkedBlockingDeque<SumAndCount>>();
        if (metricValueName == null) {
            metricValueName = "";
        }
        this.avgInfoNameTemplate = "[%s]RollingAvg" + StringUtils.capitalize(metricValueName);
        this.avgInfoDescTemplate = "Rolling average " + StringUtils.uncapitalize(metricValueName) + " for %s";
        this.numWindows = 36;
        this.scheduledTask = MutableRollingAverages.SCHEDULER.scheduleAtFixedRate(new RatesRoller(this), 300000L, 300000L, TimeUnit.MILLISECONDS);
    }
    
    @VisibleForTesting
    synchronized void replaceScheduledTask(final int windows, final long interval, final TimeUnit timeUnit) {
        this.numWindows = windows;
        this.scheduledTask.cancel(true);
        this.scheduledTask = MutableRollingAverages.SCHEDULER.scheduleAtFixedRate(new RatesRoller(this), interval, interval, timeUnit);
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            for (final Map.Entry<String, LinkedBlockingDeque<SumAndCount>> entry : this.averages.entrySet()) {
                final String name = entry.getKey();
                final MetricsInfo avgInfo = Interns.info(String.format(this.avgInfoNameTemplate, StringUtils.capitalize(name)), String.format(this.avgInfoDescTemplate, StringUtils.uncapitalize(name)));
                double totalSum = 0.0;
                long totalCount = 0L;
                for (final SumAndCount sumAndCount : entry.getValue()) {
                    totalCount += sumAndCount.getCount();
                    totalSum += sumAndCount.getSum();
                }
                if (totalCount != 0L) {
                    builder.addGauge(avgInfo, totalSum / totalCount);
                }
            }
            if (this.changed()) {
                this.clearChanged();
            }
        }
    }
    
    public void collectThreadLocalStates() {
        this.innerMetrics.collectThreadLocalStates();
    }
    
    public void add(final String name, final long value) {
        this.innerMetrics.add(name, value);
    }
    
    private synchronized void rollOverAvgs() {
        if (this.currentSnapshot == null) {
            return;
        }
        for (final Map.Entry<String, MutableRate> entry : this.currentSnapshot.entrySet()) {
            final MutableRate rate = entry.getValue();
            final LinkedBlockingDeque<SumAndCount> deque = this.averages.computeIfAbsent(entry.getKey(), new Function<String, LinkedBlockingDeque<SumAndCount>>() {
                @Override
                public LinkedBlockingDeque<SumAndCount> apply(final String k) {
                    return new LinkedBlockingDeque<SumAndCount>(MutableRollingAverages.this.numWindows);
                }
            });
            final SumAndCount sumAndCount = new SumAndCount(rate.lastStat().total(), rate.lastStat().numSamples());
            if (!deque.offerLast(sumAndCount)) {
                deque.pollFirst();
                deque.offerLast(sumAndCount);
            }
        }
        this.setChanged();
    }
    
    @Override
    public void close() throws IOException {
        if (this.scheduledTask != null) {
            this.scheduledTask.cancel(false);
        }
        this.scheduledTask = null;
    }
    
    public synchronized Map<String, Double> getStats(final long minSamples) {
        final Map<String, Double> stats = new HashMap<String, Double>();
        for (final Map.Entry<String, LinkedBlockingDeque<SumAndCount>> entry : this.averages.entrySet()) {
            final String name = entry.getKey();
            double totalSum = 0.0;
            long totalCount = 0L;
            for (final SumAndCount sumAndCount : entry.getValue()) {
                totalCount += sumAndCount.getCount();
                totalSum += sumAndCount.getSum();
            }
            if (totalCount > minSamples) {
                stats.put(name, totalSum / totalCount);
            }
        }
        return stats;
    }
    
    static {
        SCHEDULER = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("MutableRollingAverages-%d").build());
    }
    
    private static class SumAndCount
    {
        private final double sum;
        private final long count;
        
        SumAndCount(final double sum, final long count) {
            this.sum = sum;
            this.count = count;
        }
        
        public double getSum() {
            return this.sum;
        }
        
        public long getCount() {
            return this.count;
        }
    }
    
    private static class RatesRoller implements Runnable
    {
        private final MutableRollingAverages parent;
        
        RatesRoller(final MutableRollingAverages parent) {
            this.parent = parent;
        }
        
        @Override
        public void run() {
            synchronized (this.parent) {
                final MetricsCollectorImpl mc = new MetricsCollectorImpl();
                final MetricsRecordBuilder rb = mc.addRecord("RatesRoller");
                this.parent.innerMetrics.snapshot(rb, true);
                Preconditions.checkState(mc.getRecords().size() == 1, (Object)"There must be only one record and it's named with 'RatesRoller'");
                this.parent.currentSnapshot = this.parent.innerMetrics.getGlobalMetrics();
                this.parent.rollOverAvgs();
            }
            this.parent.setChanged();
        }
    }
}
