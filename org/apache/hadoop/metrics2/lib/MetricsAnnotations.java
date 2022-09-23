// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class MetricsAnnotations
{
    public static MetricsSource makeSource(final Object source) {
        return new MetricsSourceBuilder(source, DefaultMetricsFactory.getAnnotatedMetricsFactory()).build();
    }
    
    public static MetricsSourceBuilder newSourceBuilder(final Object source) {
        return new MetricsSourceBuilder(source, DefaultMetricsFactory.getAnnotatedMetricsFactory());
    }
}
