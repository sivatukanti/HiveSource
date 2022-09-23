// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;

public class NodeResourceUpdateSchedulerEvent extends SchedulerEvent
{
    private final RMNode rmNode;
    private final ResourceOption resourceOption;
    
    public NodeResourceUpdateSchedulerEvent(final RMNode rmNode, final ResourceOption resourceOption) {
        super(SchedulerEventType.NODE_RESOURCE_UPDATE);
        this.rmNode = rmNode;
        this.resourceOption = resourceOption;
    }
    
    public RMNode getRMNode() {
        return this.rmNode;
    }
    
    public ResourceOption getResourceOption() {
        return this.resourceOption;
    }
}
