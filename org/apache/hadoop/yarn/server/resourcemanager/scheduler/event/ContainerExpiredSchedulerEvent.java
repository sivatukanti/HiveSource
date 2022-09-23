// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.api.records.ContainerId;

public class ContainerExpiredSchedulerEvent extends SchedulerEvent
{
    private final ContainerId containerId;
    
    public ContainerExpiredSchedulerEvent(final ContainerId containerId) {
        super(SchedulerEventType.CONTAINER_EXPIRED);
        this.containerId = containerId;
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
}
