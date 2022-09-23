// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class RMContainerEvent extends AbstractEvent<RMContainerEventType>
{
    private final ContainerId containerId;
    
    public RMContainerEvent(final ContainerId containerId, final RMContainerEventType type) {
        super(type);
        this.containerId = containerId;
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
}
