// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;

public class NodeUpdateSchedulerEvent extends SchedulerEvent
{
    private final RMNode rmNode;
    
    public NodeUpdateSchedulerEvent(final RMNode rmNode) {
        super(SchedulerEventType.NODE_UPDATE);
        this.rmNode = rmNode;
    }
    
    public RMNode getRMNode() {
        return this.rmNode;
    }
}
