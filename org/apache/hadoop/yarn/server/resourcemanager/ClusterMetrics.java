// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.metrics2.lib.Interns;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@Metrics(context = "yarn")
public class ClusterMetrics
{
    private static AtomicBoolean isInitialized;
    @Metric({ "# of active NMs" })
    MutableGaugeInt numActiveNMs;
    @Metric({ "# of decommissioned NMs" })
    MutableGaugeInt numDecommissionedNMs;
    @Metric({ "# of lost NMs" })
    MutableGaugeInt numLostNMs;
    @Metric({ "# of unhealthy NMs" })
    MutableGaugeInt numUnhealthyNMs;
    @Metric({ "# of Rebooted NMs" })
    MutableGaugeInt numRebootedNMs;
    private static final MetricsInfo RECORD_INFO;
    private static volatile ClusterMetrics INSTANCE;
    private static MetricsRegistry registry;
    
    public static ClusterMetrics getMetrics() {
        if (!ClusterMetrics.isInitialized.get()) {
            synchronized (ClusterMetrics.class) {
                if (ClusterMetrics.INSTANCE == null) {
                    ClusterMetrics.INSTANCE = new ClusterMetrics();
                    registerMetrics();
                    ClusterMetrics.isInitialized.set(true);
                }
            }
        }
        return ClusterMetrics.INSTANCE;
    }
    
    private static void registerMetrics() {
        (ClusterMetrics.registry = new MetricsRegistry(ClusterMetrics.RECORD_INFO)).tag(ClusterMetrics.RECORD_INFO, "ResourceManager");
        final MetricsSystem ms = DefaultMetricsSystem.instance();
        if (ms != null) {
            ms.register("ClusterMetrics", "Metrics for the Yarn Cluster", ClusterMetrics.INSTANCE);
        }
    }
    
    @VisibleForTesting
    static synchronized void destroy() {
        ClusterMetrics.isInitialized.set(false);
        ClusterMetrics.INSTANCE = null;
    }
    
    public int getNumActiveNMs() {
        return this.numActiveNMs.value();
    }
    
    public int getNumDecommisionedNMs() {
        return this.numDecommissionedNMs.value();
    }
    
    public void incrDecommisionedNMs() {
        this.numDecommissionedNMs.incr();
    }
    
    public void setDecommisionedNMs(final int num) {
        this.numDecommissionedNMs.set(num);
    }
    
    public void decrDecommisionedNMs() {
        this.numDecommissionedNMs.decr();
    }
    
    public int getNumLostNMs() {
        return this.numLostNMs.value();
    }
    
    public void incrNumLostNMs() {
        this.numLostNMs.incr();
    }
    
    public void decrNumLostNMs() {
        this.numLostNMs.decr();
    }
    
    public int getUnhealthyNMs() {
        return this.numUnhealthyNMs.value();
    }
    
    public void incrNumUnhealthyNMs() {
        this.numUnhealthyNMs.incr();
    }
    
    public void decrNumUnhealthyNMs() {
        this.numUnhealthyNMs.decr();
    }
    
    public int getNumRebootedNMs() {
        return this.numRebootedNMs.value();
    }
    
    public void incrNumRebootedNMs() {
        this.numRebootedNMs.incr();
    }
    
    public void decrNumRebootedNMs() {
        this.numRebootedNMs.decr();
    }
    
    public void incrNumActiveNodes() {
        this.numActiveNMs.incr();
    }
    
    public void decrNumActiveNodes() {
        this.numActiveNMs.decr();
    }
    
    static {
        ClusterMetrics.isInitialized = new AtomicBoolean(false);
        RECORD_INFO = Interns.info("ClusterMetrics", "Metrics for the Yarn Cluster");
        ClusterMetrics.INSTANCE = null;
    }
}
