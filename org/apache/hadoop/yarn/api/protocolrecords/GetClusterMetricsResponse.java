// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetClusterMetricsResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetClusterMetricsResponse newInstance(final YarnClusterMetrics metrics) {
        final GetClusterMetricsResponse response = Records.newRecord(GetClusterMetricsResponse.class);
        response.setClusterMetrics(metrics);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract YarnClusterMetrics getClusterMetrics();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setClusterMetrics(final YarnClusterMetrics p0);
}
