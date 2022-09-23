// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.ContainerResourceDecrease;
import org.apache.hadoop.yarn.api.records.ContainerResourceIncrease;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.api.records.AMCommand;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AllocateResponse
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static AllocateResponse newInstance(final int responseId, final List<ContainerStatus> completedContainers, final List<Container> allocatedContainers, final List<NodeReport> updatedNodes, final Resource availResources, final AMCommand command, final int numClusterNodes, final PreemptionMessage preempt, final List<NMToken> nmTokens) {
        final AllocateResponse response = Records.newRecord(AllocateResponse.class);
        response.setNumClusterNodes(numClusterNodes);
        response.setResponseId(responseId);
        response.setCompletedContainersStatuses(completedContainers);
        response.setAllocatedContainers(allocatedContainers);
        response.setUpdatedNodes(updatedNodes);
        response.setAvailableResources(availResources);
        response.setAMCommand(command);
        response.setPreemptionMessage(preempt);
        response.setNMTokens(nmTokens);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static AllocateResponse newInstance(final int responseId, final List<ContainerStatus> completedContainers, final List<Container> allocatedContainers, final List<NodeReport> updatedNodes, final Resource availResources, final AMCommand command, final int numClusterNodes, final PreemptionMessage preempt, final List<NMToken> nmTokens, final List<ContainerResourceIncrease> increasedContainers, final List<ContainerResourceDecrease> decreasedContainers) {
        final AllocateResponse response = newInstance(responseId, completedContainers, allocatedContainers, updatedNodes, availResources, command, numClusterNodes, preempt, nmTokens);
        response.setIncreasedContainers(increasedContainers);
        response.setDecreasedContainers(decreasedContainers);
        return response;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static AllocateResponse newInstance(final int responseId, final List<ContainerStatus> completedContainers, final List<Container> allocatedContainers, final List<NodeReport> updatedNodes, final Resource availResources, final AMCommand command, final int numClusterNodes, final PreemptionMessage preempt, final List<NMToken> nmTokens, final Token amRMToken, final List<ContainerResourceIncrease> increasedContainers, final List<ContainerResourceDecrease> decreasedContainers) {
        final AllocateResponse response = newInstance(responseId, completedContainers, allocatedContainers, updatedNodes, availResources, command, numClusterNodes, preempt, nmTokens, increasedContainers, decreasedContainers);
        response.setAMRMToken(amRMToken);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract AMCommand getAMCommand();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAMCommand(final AMCommand p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getResponseId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setResponseId(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<Container> getAllocatedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAllocatedContainers(final List<Container> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getAvailableResources();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAvailableResources(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerStatus> getCompletedContainersStatuses();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setCompletedContainersStatuses(final List<ContainerStatus> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<NodeReport> getUpdatedNodes();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUpdatedNodes(final List<NodeReport> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getNumClusterNodes();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNumClusterNodes(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract PreemptionMessage getPreemptionMessage();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setPreemptionMessage(final PreemptionMessage p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<NMToken> getNMTokens();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNMTokens(final List<NMToken> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerResourceIncrease> getIncreasedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setIncreasedContainers(final List<ContainerResourceIncrease> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerResourceDecrease> getDecreasedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setDecreasedContainers(final List<ContainerResourceDecrease> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract Token getAMRMToken();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAMRMToken(final Token p0);
}
