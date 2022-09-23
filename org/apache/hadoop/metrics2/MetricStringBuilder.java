// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricStringBuilder extends MetricsRecordBuilder
{
    private final StringBuilder builder;
    private final String prefix;
    private final String suffix;
    private final String separator;
    private final MetricsCollector parent;
    
    public MetricStringBuilder(final MetricsCollector parent, final String prefix, final String separator, final String suffix) {
        this.builder = new StringBuilder(256);
        this.parent = parent;
        this.prefix = prefix;
        this.suffix = suffix;
        this.separator = separator;
    }
    
    public MetricStringBuilder add(final MetricsInfo info, final Object value) {
        return this.tuple(info.name(), value.toString());
    }
    
    public MetricStringBuilder tuple(final String key, final String value) {
        this.builder.append(this.prefix).append(key).append(this.separator).append(value).append(this.suffix);
        return this;
    }
    
    @Override
    public MetricsRecordBuilder tag(final MetricsInfo info, final String value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder add(final MetricsTag tag) {
        return this.tuple(tag.name(), tag.value());
    }
    
    @Override
    public MetricsRecordBuilder add(final AbstractMetric metric) {
        this.add(metric.info(), metric.toString());
        return this;
    }
    
    @Override
    public MetricsRecordBuilder setContext(final String value) {
        return this.tuple("context", value);
    }
    
    @Override
    public MetricsRecordBuilder addCounter(final MetricsInfo info, final int value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder addCounter(final MetricsInfo info, final long value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final int value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final long value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final float value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final double value) {
        return this.add(info, value);
    }
    
    @Override
    public MetricsCollector parent() {
        return this.parent;
    }
    
    @Override
    public String toString() {
        return this.builder.toString();
    }
}
