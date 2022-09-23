// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.MetricType;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.AbstractMetric;

class MetricGaugeFloat extends AbstractMetric
{
    final float value;
    
    MetricGaugeFloat(final MetricsInfo info, final float value) {
        super(info);
        this.value = value;
    }
    
    @Override
    public Float value() {
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
