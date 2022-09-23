// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class StartContainersResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static StartContainersResponse newInstance(final Map<String, ByteBuffer> servicesMetaData, final List<ContainerId> succeededContainers, final Map<ContainerId, SerializedException> failedContainers) {
        final StartContainersResponse response = Records.newRecord(StartContainersResponse.class);
        response.setAllServicesMetaData(servicesMetaData);
        response.setSuccessfullyStartedContainers(succeededContainers);
        response.setFailedRequests(failedContainers);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerId> getSuccessfullyStartedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setSuccessfullyStartedContainers(final List<ContainerId> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<ContainerId, SerializedException> getFailedRequests();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFailedRequests(final Map<ContainerId, SerializedException> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<String, ByteBuffer> getAllServicesMetaData();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAllServicesMetaData(final Map<String, ByteBuffer> p0);
}
