// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import java.util.Collection;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface MetricsRecord
{
    long timestamp();
    
    String name();
    
    String description();
    
    String context();
    
    Collection<MetricsTag> tags();
    
    Iterable<AbstractMetric> metrics();
}
