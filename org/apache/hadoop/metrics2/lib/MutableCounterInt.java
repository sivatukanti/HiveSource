// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableCounterInt extends MutableCounter
{
    private AtomicInteger value;
    
    MutableCounterInt(final MetricsInfo info, final int initValue) {
        super(info);
        (this.value = new AtomicInteger()).set(initValue);
    }
    
    @Override
    public void incr() {
        this.incr(1);
    }
    
    public synchronized void incr(final int delta) {
        this.value.addAndGet(delta);
        this.setChanged();
    }
    
    public int value() {
        return this.value.get();
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            builder.addCounter(this.info(), this.value());
            this.clearChanged();
        }
    }
}
