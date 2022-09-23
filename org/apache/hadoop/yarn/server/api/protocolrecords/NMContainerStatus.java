// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerId;

public abstract class NMContainerStatus
{
    public static NMContainerStatus newInstance(final ContainerId containerId, final ContainerState containerState, final Resource allocatedResource, final String diagnostics, final int containerExitStatus, final Priority priority, final long creationTime) {
        final NMContainerStatus status = Records.newRecord(NMContainerStatus.class);
        status.setContainerId(containerId);
        status.setContainerState(containerState);
        status.setAllocatedResource(allocatedResource);
        status.setDiagnostics(diagnostics);
        status.setContainerExitStatus(containerExitStatus);
        status.setPriority(priority);
        status.setCreationTime(creationTime);
        return status;
    }
    
    public abstract ContainerId getContainerId();
    
    public abstract void setContainerId(final ContainerId p0);
    
    public abstract Resource getAllocatedResource();
    
    public abstract void setAllocatedResource(final Resource p0);
    
    public abstract String getDiagnostics();
    
    public abstract void setDiagnostics(final String p0);
    
    public abstract ContainerState getContainerState();
    
    public abstract void setContainerState(final ContainerState p0);
    
    public abstract int getContainerExitStatus();
    
    public abstract void setContainerExitStatus(final int p0);
    
    public abstract Priority getPriority();
    
    public abstract void setPriority(final Priority p0);
    
    public abstract long getCreationTime();
    
    public abstract void setCreationTime(final long p0);
}
