// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink.ganglia;

import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsVisitor;

class GangliaMetricVisitor implements MetricsVisitor
{
    private static final String INT32 = "int32";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private String type;
    private AbstractGangliaSink.GangliaSlope slope;
    
    String getType() {
        return this.type;
    }
    
    AbstractGangliaSink.GangliaSlope getSlope() {
        return this.slope;
    }
    
    @Override
    public void gauge(final MetricsInfo info, final int value) {
        this.type = "int32";
        this.slope = null;
    }
    
    @Override
    public void gauge(final MetricsInfo info, final long value) {
        this.type = "float";
        this.slope = null;
    }
    
    @Override
    public void gauge(final MetricsInfo info, final float value) {
        this.type = "float";
        this.slope = null;
    }
    
    @Override
    public void gauge(final MetricsInfo info, final double value) {
        this.type = "double";
        this.slope = null;
    }
    
    @Override
    public void counter(final MetricsInfo info, final int value) {
        this.type = "int32";
        this.slope = AbstractGangliaSink.GangliaSlope.positive;
    }
    
    @Override
    public void counter(final MetricsInfo info, final long value) {
        this.type = "float";
        this.slope = AbstractGangliaSink.GangliaSlope.positive;
    }
}
