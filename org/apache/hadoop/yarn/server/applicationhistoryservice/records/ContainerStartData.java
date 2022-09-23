// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ContainerStartData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ContainerStartData newInstance(final ContainerId containerId, final Resource allocatedResource, final NodeId assignedNode, final Priority priority, final long startTime) {
        final ContainerStartData containerSD = Records.newRecord(ContainerStartData.class);
        containerSD.setContainerId(containerId);
        containerSD.setAllocatedResource(allocatedResource);
        containerSD.setAssignedNode(assignedNode);
        containerSD.setPriority(priority);
        containerSD.setStartTime(startTime);
        return containerSD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerId getContainerId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerId(final ContainerId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract Resource getAllocatedResource();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setAllocatedResource(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract NodeId getAssignedNode();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setAssignedNode(final NodeId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract Priority getPriority();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setPriority(final Priority p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getStartTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setStartTime(final long p0);
}
