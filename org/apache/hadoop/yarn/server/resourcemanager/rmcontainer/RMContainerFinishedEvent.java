// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;

public class RMContainerFinishedEvent extends RMContainerEvent
{
    private final ContainerStatus remoteContainerStatus;
    
    public RMContainerFinishedEvent(final ContainerId containerId, final ContainerStatus containerStatus, final RMContainerEventType event) {
        super(containerId, event);
        this.remoteContainerStatus = containerStatus;
    }
    
    public ContainerStatus getRemoteContainerStatus() {
        return this.remoteContainerStatus;
    }
}
