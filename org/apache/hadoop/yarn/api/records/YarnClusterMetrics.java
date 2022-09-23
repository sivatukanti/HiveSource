// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class YarnClusterMetrics
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static YarnClusterMetrics newInstance(final int numNodeManagers) {
        final YarnClusterMetrics metrics = Records.newRecord(YarnClusterMetrics.class);
        metrics.setNumNodeManagers(numNodeManagers);
        return metrics;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getNumNodeManagers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNumNodeManagers(final int p0);
}
