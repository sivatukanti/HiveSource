// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class MutableMetric
{
    private volatile boolean changed;
    
    public MutableMetric() {
        this.changed = true;
    }
    
    public abstract void snapshot(final MetricsRecordBuilder p0, final boolean p1);
    
    public void snapshot(final MetricsRecordBuilder builder) {
        this.snapshot(builder, false);
    }
    
    protected void setChanged() {
        this.changed = true;
    }
    
    protected void clearChanged() {
        this.changed = false;
    }
    
    public boolean changed() {
        return this.changed;
    }
}
