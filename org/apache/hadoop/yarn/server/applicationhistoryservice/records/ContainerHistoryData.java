// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContainerHistoryData
{
    private ContainerId containerId;
    private Resource allocatedResource;
    private NodeId assignedNode;
    private Priority priority;
    private long startTime;
    private long finishTime;
    private String diagnosticsInfo;
    private int containerExitStatus;
    private ContainerState containerState;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ContainerHistoryData newInstance(final ContainerId containerId, final Resource allocatedResource, final NodeId assignedNode, final Priority priority, final long startTime, final long finishTime, final String diagnosticsInfo, final int containerExitCode, final ContainerState containerState) {
        final ContainerHistoryData containerHD = new ContainerHistoryData();
        containerHD.setContainerId(containerId);
        containerHD.setAllocatedResource(allocatedResource);
        containerHD.setAssignedNode(assignedNode);
        containerHD.setPriority(priority);
        containerHD.setStartTime(startTime);
        containerHD.setFinishTime(finishTime);
        containerHD.setDiagnosticsInfo(diagnosticsInfo);
        containerHD.setContainerExitStatus(containerExitCode);
        containerHD.setContainerState(containerState);
        return containerHD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setContainerId(final ContainerId containerId) {
        this.containerId = containerId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public Resource getAllocatedResource() {
        return this.allocatedResource;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setAllocatedResource(final Resource resource) {
        this.allocatedResource = resource;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public NodeId getAssignedNode() {
        return this.assignedNode;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setAssignedNode(final NodeId nodeId) {
        this.assignedNode = nodeId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public Priority getPriority() {
        return this.priority;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setPriority(final Priority priority) {
        this.priority = priority;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public long getStartTime() {
        return this.startTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public long getFinishTime() {
        return this.finishTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setFinishTime(final long finishTime) {
        this.finishTime = finishTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setDiagnosticsInfo(final String diagnosticsInfo) {
        this.diagnosticsInfo = diagnosticsInfo;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public int getContainerExitStatus() {
        return this.containerExitStatus;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setContainerExitStatus(final int containerExitStatus) {
        this.containerExitStatus = containerExitStatus;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public ContainerState getContainerState() {
        return this.containerState;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setContainerState(final ContainerState containerState) {
        this.containerState = containerState;
    }
}
