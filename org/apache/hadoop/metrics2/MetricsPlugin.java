// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface MetricsPlugin
{
    void init(final SubsetConfiguration p0);
}
