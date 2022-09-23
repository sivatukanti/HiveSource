// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class MutableGauge extends MutableMetric
{
    private final MetricsInfo info;
    
    protected MutableGauge(final MetricsInfo info) {
        this.info = Preconditions.checkNotNull(info, (Object)"metric info");
    }
    
    protected MetricsInfo info() {
        return this.info;
    }
    
    public abstract void incr();
    
    public abstract void decr();
}
