// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;

public class RMContainerReservedEvent extends RMContainerEvent
{
    private final Resource reservedResource;
    private final NodeId reservedNode;
    private final Priority reservedPriority;
    
    public RMContainerReservedEvent(final ContainerId containerId, final Resource reservedResource, final NodeId reservedNode, final Priority reservedPriority) {
        super(containerId, RMContainerEventType.RESERVED);
        this.reservedResource = reservedResource;
        this.reservedNode = reservedNode;
        this.reservedPriority = reservedPriority;
    }
    
    public Resource getReservedResource() {
        return this.reservedResource;
    }
    
    public NodeId getReservedNode() {
        return this.reservedNode;
    }
    
    public Priority getReservedPriority() {
        return this.reservedPriority;
    }
}
