// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetContainerReportRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetContainerReportRequest newInstance(final ContainerId containerId) {
        final GetContainerReportRequest request = Records.newRecord(GetContainerReportRequest.class);
        request.setContainerId(containerId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerId getContainerId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerId(final ContainerId p0);
}
