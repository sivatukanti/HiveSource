// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;

@Metrics(context = "yarn")
public class FSQueueMetrics extends QueueMetrics
{
    @Metric({ "Fair share of memory in MB" })
    MutableGaugeInt fairShareMB;
    @Metric({ "Fair share of CPU in vcores" })
    MutableGaugeInt fairShareVCores;
    @Metric({ "Steady fair share of memory in MB" })
    MutableGaugeInt steadyFairShareMB;
    @Metric({ "Steady fair share of CPU in vcores" })
    MutableGaugeInt steadyFairShareVCores;
    @Metric({ "Minimum share of memory in MB" })
    MutableGaugeInt minShareMB;
    @Metric({ "Minimum share of CPU in vcores" })
    MutableGaugeInt minShareVCores;
    @Metric({ "Maximum share of memory in MB" })
    MutableGaugeInt maxShareMB;
    @Metric({ "Maximum share of CPU in vcores" })
    MutableGaugeInt maxShareVCores;
    
    FSQueueMetrics(final MetricsSystem ms, final String queueName, final Queue parent, final boolean enableUserMetrics, final Configuration conf) {
        super(ms, queueName, parent, enableUserMetrics, conf);
    }
    
    public void setFairShare(final Resource resource) {
        this.fairShareMB.set(resource.getMemory());
        this.fairShareVCores.set(resource.getVirtualCores());
    }
    
    public int getFairShareMB() {
        return this.fairShareMB.value();
    }
    
    public int getFairShareVirtualCores() {
        return this.fairShareVCores.value();
    }
    
    public void setSteadyFairShare(final Resource resource) {
        this.steadyFairShareMB.set(resource.getMemory());
        this.steadyFairShareVCores.set(resource.getVirtualCores());
    }
    
    public int getSteadyFairShareMB() {
        return this.steadyFairShareMB.value();
    }
    
    public int getSteadyFairShareVCores() {
        return this.steadyFairShareVCores.value();
    }
    
    public void setMinShare(final Resource resource) {
        this.minShareMB.set(resource.getMemory());
        this.minShareVCores.set(resource.getVirtualCores());
    }
    
    public int getMinShareMB() {
        return this.minShareMB.value();
    }
    
    public int getMinShareVirtualCores() {
        return this.minShareVCores.value();
    }
    
    public void setMaxShare(final Resource resource) {
        this.maxShareMB.set(resource.getMemory());
        this.maxShareVCores.set(resource.getVirtualCores());
    }
    
    public int getMaxShareMB() {
        return this.maxShareMB.value();
    }
    
    public int getMaxShareVirtualCores() {
        return this.maxShareVCores.value();
    }
    
    public static synchronized FSQueueMetrics forQueue(final String queueName, final Queue parent, final boolean enableUserMetrics, final Configuration conf) {
        final MetricsSystem ms = DefaultMetricsSystem.instance();
        QueueMetrics metrics = FSQueueMetrics.queueMetrics.get(queueName);
        if (metrics == null) {
            metrics = new FSQueueMetrics(ms, queueName, parent, enableUserMetrics, conf).tag(FSQueueMetrics.QUEUE_INFO, queueName);
            if (ms != null) {
                metrics = ms.register(QueueMetrics.sourceName(queueName).toString(), "Metrics for queue: " + queueName, metrics);
            }
            FSQueueMetrics.queueMetrics.put(queueName, metrics);
        }
        return (FSQueueMetrics)metrics;
    }
}
