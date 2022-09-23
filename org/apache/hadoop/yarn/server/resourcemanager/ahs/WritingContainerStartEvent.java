// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.api.records.ContainerId;

public class WritingContainerStartEvent extends WritingApplicationHistoryEvent
{
    private ContainerId containerId;
    private ContainerStartData containerStart;
    
    public WritingContainerStartEvent(final ContainerId containerId, final ContainerStartData containerStart) {
        super(WritingHistoryEventType.CONTAINER_START);
        this.containerId = containerId;
        this.containerStart = containerStart;
    }
    
    @Override
    public int hashCode() {
        return this.containerId.getApplicationAttemptId().getApplicationId().hashCode();
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    public ContainerStartData getContainerStartData() {
        return this.containerStart;
    }
}
