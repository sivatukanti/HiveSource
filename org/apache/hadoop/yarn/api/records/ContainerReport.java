// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ContainerReport
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ContainerReport newInstance(final ContainerId containerId, final Resource allocatedResource, final NodeId assignedNode, final Priority priority, final long creationTime, final long finishTime, final String diagnosticInfo, final String logUrl, final int containerExitStatus, final ContainerState containerState) {
        final ContainerReport report = Records.newRecord(ContainerReport.class);
        report.setContainerId(containerId);
        report.setAllocatedResource(allocatedResource);
        report.setAssignedNode(assignedNode);
        report.setPriority(priority);
        report.setCreationTime(creationTime);
        report.setFinishTime(finishTime);
        report.setDiagnosticsInfo(diagnosticInfo);
        report.setLogUrl(logUrl);
        report.setContainerExitStatus(containerExitStatus);
        report.setContainerState(containerState);
        return report;
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
    public abstract long getCreationTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setCreationTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getFinishTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setFinishTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getDiagnosticsInfo();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setDiagnosticsInfo(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getLogUrl();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setLogUrl(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerState getContainerState();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerState(final ContainerState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getContainerExitStatus();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerExitStatus(final int p0);
}
