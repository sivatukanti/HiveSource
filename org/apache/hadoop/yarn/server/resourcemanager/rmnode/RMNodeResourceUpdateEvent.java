// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ResourceOption;

public class RMNodeResourceUpdateEvent extends RMNodeEvent
{
    private final ResourceOption resourceOption;
    
    public RMNodeResourceUpdateEvent(final NodeId nodeId, final ResourceOption resourceOption) {
        super(nodeId, RMNodeEventType.RESOURCE_UPDATE);
        this.resourceOption = resourceOption;
    }
    
    public ResourceOption getResourceOption() {
        return this.resourceOption;
    }
}
