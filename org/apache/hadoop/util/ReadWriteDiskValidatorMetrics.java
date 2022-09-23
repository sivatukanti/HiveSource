// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.HashMap;
import org.apache.hadoop.metrics2.lib.Interns;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import java.util.Map;
import org.apache.hadoop.metrics2.lib.MutableQuantiles;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.lib.MutableGaugeLong;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableCounterInt;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ReadWriteDiskValidatorMetrics
{
    @Metric({ "# of disk failure" })
    MutableCounterInt failureCount;
    @Metric({ "Time of last failure" })
    MutableGaugeLong lastFailureTime;
    private final MetricsRegistry registry;
    private static final MetricsInfo RECORD_INFO;
    private final int[] quantileIntervals;
    private final MutableQuantiles[] fileReadQuantiles;
    private final MutableQuantiles[] fileWriteQuantiles;
    protected static final Map<String, ReadWriteDiskValidatorMetrics> DIR_METRICS;
    
    public ReadWriteDiskValidatorMetrics() {
        this.quantileIntervals = new int[] { 3600, 86400, 864000 };
        this.registry = new MetricsRegistry(ReadWriteDiskValidatorMetrics.RECORD_INFO);
        this.fileReadQuantiles = new MutableQuantiles[this.quantileIntervals.length];
        for (int i = 0; i < this.fileReadQuantiles.length; ++i) {
            final int interval = this.quantileIntervals[i];
            this.fileReadQuantiles[i] = this.registry.newQuantiles("readLatency" + interval + "s", "File read latency", "Ops", "latencyMicros", interval);
        }
        this.fileWriteQuantiles = new MutableQuantiles[this.quantileIntervals.length];
        for (int i = 0; i < this.fileWriteQuantiles.length; ++i) {
            final int interval = this.quantileIntervals[i];
            this.fileWriteQuantiles[i] = this.registry.newQuantiles("writeLatency" + interval + "s", "File write latency", "Ops", "latencyMicros", interval);
        }
    }
    
    public static synchronized ReadWriteDiskValidatorMetrics getMetric(final String dirName) {
        final MetricsSystem ms = DefaultMetricsSystem.instance();
        ReadWriteDiskValidatorMetrics metrics = ReadWriteDiskValidatorMetrics.DIR_METRICS.get(dirName);
        if (metrics == null) {
            metrics = new ReadWriteDiskValidatorMetrics();
            if (ms != null) {
                metrics = ms.register(sourceName(dirName), "Metrics for directory: " + dirName, metrics);
            }
            ReadWriteDiskValidatorMetrics.DIR_METRICS.put(dirName, metrics);
        }
        return metrics;
    }
    
    public void addWriteFileLatency(final long writeLatency) {
        if (this.fileWriteQuantiles != null) {
            for (final MutableQuantiles q : this.fileWriteQuantiles) {
                q.add(writeLatency);
            }
        }
    }
    
    public void addReadFileLatency(final long readLatency) {
        if (this.fileReadQuantiles != null) {
            for (final MutableQuantiles q : this.fileReadQuantiles) {
                q.add(readLatency);
            }
        }
    }
    
    protected static String sourceName(final String dirName) {
        final StringBuilder sb = new StringBuilder(ReadWriteDiskValidatorMetrics.RECORD_INFO.name());
        sb.append(",dir=").append(dirName);
        return sb.toString();
    }
    
    public void diskCheckFailed() {
        this.failureCount.incr();
        this.lastFailureTime.set(System.nanoTime());
    }
    
    @VisibleForTesting
    protected MutableQuantiles[] getFileReadQuantiles() {
        return this.fileReadQuantiles;
    }
    
    @VisibleForTesting
    protected MutableQuantiles[] getFileWriteQuantiles() {
        return this.fileWriteQuantiles;
    }
    
    static {
        RECORD_INFO = Interns.info("ReadWriteDiskValidatorMetrics", "Metrics for the DiskValidator");
        DIR_METRICS = new HashMap<String, ReadWriteDiskValidatorMetrics>();
    }
}
