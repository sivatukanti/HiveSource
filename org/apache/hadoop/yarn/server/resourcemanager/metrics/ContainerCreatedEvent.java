// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;

public class ContainerCreatedEvent extends SystemMetricsEvent
{
    private ContainerId containerId;
    private Resource allocatedResource;
    private NodeId allocatedNode;
    private Priority allocatedPriority;
    
    public ContainerCreatedEvent(final ContainerId containerId, final Resource allocatedResource, final NodeId allocatedNode, final Priority allocatedPriority, final long createdTime) {
        super(SystemMetricsEventType.CONTAINER_CREATED, createdTime);
        this.containerId = containerId;
        this.allocatedResource = allocatedResource;
        this.allocatedNode = allocatedNode;
        this.allocatedPriority = allocatedPriority;
    }
    
    @Override
    public int hashCode() {
        return this.containerId.getApplicationAttemptId().getApplicationId().hashCode();
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    public Resource getAllocatedResource() {
        return this.allocatedResource;
    }
    
    public NodeId getAllocatedNode() {
        return this.allocatedNode;
    }
    
    public Priority getAllocatedPriority() {
        return this.allocatedPriority;
    }
}
