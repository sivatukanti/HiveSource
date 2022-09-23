// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.SerializedException;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class StopContainersResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static StopContainersResponse newInstance(final List<ContainerId> succeededRequests, final Map<ContainerId, SerializedException> failedRequests) {
        final StopContainersResponse response = Records.newRecord(StopContainersResponse.class);
        response.setFailedRequests(failedRequests);
        response.setSuccessfullyStoppedContainers(succeededRequests);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerId> getSuccessfullyStoppedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setSuccessfullyStoppedContainers(final List<ContainerId> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<ContainerId, SerializedException> getFailedRequests();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFailedRequests(final Map<ContainerId, SerializedException> p0);
}
