// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;

public class RMNodeCleanContainerEvent extends RMNodeEvent
{
    private ContainerId contId;
    
    public RMNodeCleanContainerEvent(final NodeId nodeId, final ContainerId contId) {
        super(nodeId, RMNodeEventType.CLEANUP_CONTAINER);
        this.contId = contId;
    }
    
    public ContainerId getContainerId() {
        return this.contId;
    }
}
