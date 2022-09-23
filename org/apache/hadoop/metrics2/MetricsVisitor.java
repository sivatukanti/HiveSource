// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface MetricsVisitor
{
    void gauge(final MetricsInfo p0, final int p1);
    
    void gauge(final MetricsInfo p0, final long p1);
    
    void gauge(final MetricsInfo p0, final float p1);
    
    void gauge(final MetricsInfo p0, final double p1);
    
    void counter(final MetricsInfo p0, final int p1);
    
    void counter(final MetricsInfo p0, final long p1);
}
