// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.MetricType;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.AbstractMetric;

class MetricGaugeInt extends AbstractMetric
{
    final int value;
    
    MetricGaugeInt(final MetricsInfo info, final int value) {
        super(info);
        this.value = value;
    }
    
    @Override
    public Integer value() {
        return this.value;
    }
    
    @Override
    public MetricType type() {
        return MetricType.GAUGE;
    }
    
    @Override
    public void visit(final MetricsVisitor visitor) {
        visitor.gauge(this, this.value);
    }
}
