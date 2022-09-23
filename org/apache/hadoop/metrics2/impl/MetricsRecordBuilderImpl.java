// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.Collections;
import org.apache.hadoop.metrics2.lib.Interns;
import com.google.common.collect.Lists;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.metrics2.MetricsFilter;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.AbstractMetric;
import java.util.List;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;

class MetricsRecordBuilderImpl extends MetricsRecordBuilder
{
    private final MetricsCollector parent;
    private final long timestamp;
    private final MetricsInfo recInfo;
    private final List<AbstractMetric> metrics;
    private final List<MetricsTag> tags;
    private final MetricsFilter recordFilter;
    private final MetricsFilter metricFilter;
    private final boolean acceptable;
    
    MetricsRecordBuilderImpl(final MetricsCollector parent, final MetricsInfo info, final MetricsFilter rf, final MetricsFilter mf, final boolean acceptable) {
        this.parent = parent;
        this.timestamp = Time.now();
        this.recInfo = info;
        this.metrics = (List<AbstractMetric>)Lists.newArrayList();
        this.tags = (List<MetricsTag>)Lists.newArrayList();
        this.recordFilter = rf;
        this.metricFilter = mf;
        this.acceptable = acceptable;
    }
    
    @Override
    public MetricsCollector parent() {
        return this.parent;
    }
    
    @Override
    public MetricsRecordBuilderImpl tag(final MetricsInfo info, final String value) {
        if (this.acceptable) {
            this.tags.add(Interns.tag(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl add(final MetricsTag tag) {
        this.tags.add(tag);
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl add(final AbstractMetric metric) {
        this.metrics.add(metric);
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addCounter(final MetricsInfo info, final int value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricCounterInt(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addCounter(final MetricsInfo info, final long value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricCounterLong(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addGauge(final MetricsInfo info, final int value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricGaugeInt(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addGauge(final MetricsInfo info, final long value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricGaugeLong(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addGauge(final MetricsInfo info, final float value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricGaugeFloat(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl addGauge(final MetricsInfo info, final double value) {
        if (this.acceptable && (this.metricFilter == null || this.metricFilter.accepts(info.name()))) {
            this.metrics.add(new MetricGaugeDouble(info, value));
        }
        return this;
    }
    
    @Override
    public MetricsRecordBuilderImpl setContext(final String value) {
        return this.tag(MsInfo.Context, value);
    }
    
    public MetricsRecordImpl getRecord() {
        if (this.acceptable && (this.recordFilter == null || this.recordFilter.accepts(this.tags))) {
            return new MetricsRecordImpl(this.recInfo, this.timestamp, this.tags(), this.metrics());
        }
        return null;
    }
    
    List<MetricsTag> tags() {
        return Collections.unmodifiableList((List<? extends MetricsTag>)this.tags);
    }
    
    List<AbstractMetric> metrics() {
        return Collections.unmodifiableList((List<? extends AbstractMetric>)this.metrics);
    }
}
