// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerId;

public class ContainerFinishedEvent extends SystemMetricsEvent
{
    private ContainerId containerId;
    private String diagnosticsInfo;
    private int containerExitStatus;
    private ContainerState state;
    
    public ContainerFinishedEvent(final ContainerId containerId, final String diagnosticsInfo, final int containerExitStatus, final ContainerState state, final long finishedTime) {
        super(SystemMetricsEventType.CONTAINER_FINISHED, finishedTime);
        this.containerId = containerId;
        this.diagnosticsInfo = diagnosticsInfo;
        this.containerExitStatus = containerExitStatus;
        this.state = state;
    }
    
    @Override
    public int hashCode() {
        return this.containerId.getApplicationAttemptId().getApplicationId().hashCode();
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public int getContainerExitStatus() {
        return this.containerExitStatus;
    }
    
    public ContainerState getContainerState() {
        return this.state;
    }
}
