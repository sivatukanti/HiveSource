// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.metrics2.util.SampleQuantiles;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.apache.hadoop.metrics2.util.QuantileEstimator;
import org.apache.hadoop.metrics2.MetricsInfo;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.util.Quantile;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableQuantiles extends MutableMetric
{
    @VisibleForTesting
    public static final Quantile[] quantiles;
    private final MetricsInfo numInfo;
    private final MetricsInfo[] quantileInfos;
    private final int interval;
    private QuantileEstimator estimator;
    private long previousCount;
    private ScheduledFuture<?> scheduledTask;
    @VisibleForTesting
    protected Map<Quantile, Long> previousSnapshot;
    private static final ScheduledExecutorService scheduler;
    
    public MutableQuantiles(final String name, final String description, final String sampleName, final String valueName, final int interval) {
        this.previousCount = 0L;
        this.scheduledTask = null;
        this.previousSnapshot = null;
        final String ucName = StringUtils.capitalize(name);
        final String usName = StringUtils.capitalize(sampleName);
        final String uvName = StringUtils.capitalize(valueName);
        final String desc = StringUtils.uncapitalize(description);
        final String lsName = StringUtils.uncapitalize(sampleName);
        final String lvName = StringUtils.uncapitalize(valueName);
        this.numInfo = Interns.info(ucName + "Num" + usName, String.format("Number of %s for %s with %ds interval", lsName, desc, interval));
        this.quantileInfos = new MetricsInfo[MutableQuantiles.quantiles.length];
        final String nameTemplate = ucName + "%dthPercentile" + uvName;
        final String descTemplate = "%d percentile " + lvName + " with " + interval + " second interval for " + desc;
        for (int i = 0; i < MutableQuantiles.quantiles.length; ++i) {
            final int percentile = (int)(100.0 * MutableQuantiles.quantiles[i].quantile);
            this.quantileInfos[i] = Interns.info(String.format(nameTemplate, percentile), String.format(descTemplate, percentile));
        }
        this.estimator = new SampleQuantiles(MutableQuantiles.quantiles);
        this.interval = interval;
        this.scheduledTask = MutableQuantiles.scheduler.scheduleAtFixedRate(new RolloverSample(this), interval, interval, TimeUnit.SECONDS);
    }
    
    @Override
    public synchronized void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            builder.addGauge(this.numInfo, this.previousCount);
            for (int i = 0; i < MutableQuantiles.quantiles.length; ++i) {
                long newValue = 0L;
                if (this.previousSnapshot != null) {
                    newValue = this.previousSnapshot.get(MutableQuantiles.quantiles[i]);
                }
                builder.addGauge(this.quantileInfos[i], newValue);
            }
            if (this.changed()) {
                this.clearChanged();
            }
        }
    }
    
    public synchronized void add(final long value) {
        this.estimator.insert(value);
    }
    
    public int getInterval() {
        return this.interval;
    }
    
    public void stop() {
        if (this.scheduledTask != null) {
            this.scheduledTask.cancel(false);
        }
        this.scheduledTask = null;
    }
    
    @VisibleForTesting
    public synchronized QuantileEstimator getEstimator() {
        return this.estimator;
    }
    
    public synchronized void setEstimator(final QuantileEstimator quantileEstimator) {
        this.estimator = quantileEstimator;
    }
    
    static {
        quantiles = new Quantile[] { new Quantile(0.5, 0.05), new Quantile(0.75, 0.025), new Quantile(0.9, 0.01), new Quantile(0.95, 0.005), new Quantile(0.99, 0.001) };
        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("MutableQuantiles-%d").build());
    }
    
    private static class RolloverSample implements Runnable
    {
        MutableQuantiles parent;
        
        public RolloverSample(final MutableQuantiles parent) {
            this.parent = parent;
        }
        
        @Override
        public void run() {
            synchronized (this.parent) {
                this.parent.previousCount = this.parent.estimator.getCount();
                this.parent.previousSnapshot = this.parent.estimator.snapshot();
                this.parent.estimator.clear();
            }
            this.parent.setChanged();
        }
    }
}
