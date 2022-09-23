// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerResourceIncreaseRequest;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AllocateRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static AllocateRequest newInstance(final int responseID, final float appProgress, final List<ResourceRequest> resourceAsk, final List<ContainerId> containersToBeReleased, final ResourceBlacklistRequest resourceBlacklistRequest) {
        return newInstance(responseID, appProgress, resourceAsk, containersToBeReleased, resourceBlacklistRequest, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static AllocateRequest newInstance(final int responseID, final float appProgress, final List<ResourceRequest> resourceAsk, final List<ContainerId> containersToBeReleased, final ResourceBlacklistRequest resourceBlacklistRequest, final List<ContainerResourceIncreaseRequest> increaseRequests) {
        final AllocateRequest allocateRequest = Records.newRecord(AllocateRequest.class);
        allocateRequest.setResponseId(responseID);
        allocateRequest.setProgress(appProgress);
        allocateRequest.setAskList(resourceAsk);
        allocateRequest.setReleaseList(containersToBeReleased);
        allocateRequest.setResourceBlacklistRequest(resourceBlacklistRequest);
        allocateRequest.setIncreaseRequests(increaseRequests);
        return allocateRequest;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getResponseId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setResponseId(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract float getProgress();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setProgress(final float p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ResourceRequest> getAskList();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setAskList(final List<ResourceRequest> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerId> getReleaseList();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setReleaseList(final List<ContainerId> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ResourceBlacklistRequest getResourceBlacklistRequest();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setResourceBlacklistRequest(final ResourceBlacklistRequest p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerResourceIncreaseRequest> getIncreaseRequests();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setIncreaseRequests(final List<ContainerResourceIncreaseRequest> p0);
}
