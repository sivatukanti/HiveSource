// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;

public class RMNodeFinishedContainersPulledByAMEvent extends RMNodeEvent
{
    private List<ContainerId> containers;
    
    public RMNodeFinishedContainersPulledByAMEvent(final NodeId nodeId, final List<ContainerId> containers) {
        super(nodeId, RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM);
        this.containers = containers;
    }
    
    public List<ContainerId> getContainers() {
        return this.containers;
    }
}
