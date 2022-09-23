// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.MetricType;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.AbstractMetric;

class MetricCounterLong extends AbstractMetric
{
    final long value;
    
    MetricCounterLong(final MetricsInfo info, final long value) {
        super(info);
        this.value = value;
    }
    
    @Override
    public Long value() {
        return this.value;
    }
    
    @Override
    public MetricType type() {
        return MetricType.COUNTER;
    }
    
    @Override
    public void visit(final MetricsVisitor visitor) {
        visitor.counter(this, this.value);
    }
}
