// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class MetricsRecordBuilder
{
    public abstract MetricsRecordBuilder tag(final MetricsInfo p0, final String p1);
    
    public abstract MetricsRecordBuilder add(final MetricsTag p0);
    
    public abstract MetricsRecordBuilder add(final AbstractMetric p0);
    
    public abstract MetricsRecordBuilder setContext(final String p0);
    
    public abstract MetricsRecordBuilder addCounter(final MetricsInfo p0, final int p1);
    
    public abstract MetricsRecordBuilder addCounter(final MetricsInfo p0, final long p1);
    
    public abstract MetricsRecordBuilder addGauge(final MetricsInfo p0, final int p1);
    
    public abstract MetricsRecordBuilder addGauge(final MetricsInfo p0, final long p1);
    
    public abstract MetricsRecordBuilder addGauge(final MetricsInfo p0, final float p1);
    
    public abstract MetricsRecordBuilder addGauge(final MetricsInfo p0, final double p1);
    
    public abstract MetricsCollector parent();
    
    public MetricsCollector endRecord() {
        return this.parent();
    }
}
