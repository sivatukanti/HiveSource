// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class MetricsFilter implements MetricsPlugin
{
    public abstract boolean accepts(final String p0);
    
    public abstract boolean accepts(final MetricsTag p0);
    
    public abstract boolean accepts(final Iterable<MetricsTag> p0);
    
    public boolean accepts(final MetricsRecord record) {
        return this.accepts(record.name()) && this.accepts(record.tags());
    }
}
