// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetContainerStatusesResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetContainerStatusesResponse newInstance(final List<ContainerStatus> statuses, final Map<ContainerId, SerializedException> failedRequests) {
        final GetContainerStatusesResponse response = Records.newRecord(GetContainerStatusesResponse.class);
        response.setContainerStatuses(statuses);
        response.setFailedRequests(failedRequests);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerStatus> getContainerStatuses();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainerStatuses(final List<ContainerStatus> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<ContainerId, SerializedException> getFailedRequests();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFailedRequests(final Map<ContainerId, SerializedException> p0);
}
