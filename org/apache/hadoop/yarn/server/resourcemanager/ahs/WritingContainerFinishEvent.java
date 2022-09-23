// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.api.records.ContainerId;

public class WritingContainerFinishEvent extends WritingApplicationHistoryEvent
{
    private ContainerId containerId;
    private ContainerFinishData containerFinish;
    
    public WritingContainerFinishEvent(final ContainerId containerId, final ContainerFinishData containerFinish) {
        super(WritingHistoryEventType.CONTAINER_FINISH);
        this.containerId = containerId;
        this.containerFinish = containerFinish;
    }
    
    @Override
    public int hashCode() {
        return this.containerId.getApplicationAttemptId().getApplicationId().hashCode();
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    public ContainerFinishData getContainerFinishData() {
        return this.containerFinish;
    }
}
