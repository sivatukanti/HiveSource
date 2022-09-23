// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.metrics2.lib.Interns;
import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableRate;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsSource;

@InterfaceAudience.Private
@InterfaceStability.Unstable
@Metrics(context = "fairscheduler-op-durations")
public class FSOpDurations implements MetricsSource
{
    @Metric({ "Duration for a continuous scheduling run" })
    MutableRate continuousSchedulingRun;
    @Metric({ "Duration to handle a node update" })
    MutableRate nodeUpdateCall;
    @Metric({ "Duration for a update thread run" })
    MutableRate updateThreadRun;
    @Metric({ "Duration for an update call" })
    MutableRate updateCall;
    @Metric({ "Duration for a preempt call" })
    MutableRate preemptCall;
    private static final MetricsInfo RECORD_INFO;
    private final MetricsRegistry registry;
    private boolean isExtended;
    private static final FSOpDurations INSTANCE;
    
    public static FSOpDurations getInstance(final boolean isExtended) {
        FSOpDurations.INSTANCE.setExtended(isExtended);
        return FSOpDurations.INSTANCE;
    }
    
    private FSOpDurations() {
        this.isExtended = false;
        (this.registry = new MetricsRegistry(FSOpDurations.RECORD_INFO)).tag(FSOpDurations.RECORD_INFO, "FSOpDurations");
        final MetricsSystem ms = DefaultMetricsSystem.instance();
        if (ms != null) {
            ms.register(FSOpDurations.RECORD_INFO.name(), FSOpDurations.RECORD_INFO.description(), this);
        }
    }
    
    private synchronized void setExtended(final boolean isExtended) {
        if (isExtended == FSOpDurations.INSTANCE.isExtended) {
            return;
        }
        this.continuousSchedulingRun.setExtended(isExtended);
        this.nodeUpdateCall.setExtended(isExtended);
        this.updateThreadRun.setExtended(isExtended);
        this.updateCall.setExtended(isExtended);
        this.preemptCall.setExtended(isExtended);
        FSOpDurations.INSTANCE.isExtended = isExtended;
    }
    
    @Override
    public synchronized void getMetrics(final MetricsCollector collector, final boolean all) {
        this.registry.snapshot(collector.addRecord(this.registry.info()), all);
    }
    
    public void addContinuousSchedulingRunDuration(final long value) {
        this.continuousSchedulingRun.add(value);
    }
    
    public void addNodeUpdateDuration(final long value) {
        this.nodeUpdateCall.add(value);
    }
    
    public void addUpdateThreadRunDuration(final long value) {
        this.updateThreadRun.add(value);
    }
    
    public void addUpdateCallDuration(final long value) {
        this.updateCall.add(value);
    }
    
    public void addPreemptCallDuration(final long value) {
        this.preemptCall.add(value);
    }
    
    static {
        RECORD_INFO = Interns.info("FSOpDurations", "Durations of FairScheduler calls or thread-runs");
        INSTANCE = new FSOpDurations();
    }
}
