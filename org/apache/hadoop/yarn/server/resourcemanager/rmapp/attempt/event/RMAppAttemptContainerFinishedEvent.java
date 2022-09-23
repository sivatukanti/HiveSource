// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;

public class RMAppAttemptContainerFinishedEvent extends RMAppAttemptEvent
{
    private final ContainerStatus containerStatus;
    private final NodeId nodeId;
    
    public RMAppAttemptContainerFinishedEvent(final ApplicationAttemptId appAttemptId, final ContainerStatus containerStatus, final NodeId nodeId) {
        super(appAttemptId, RMAppAttemptEventType.CONTAINER_FINISHED);
        this.containerStatus = containerStatus;
        this.nodeId = nodeId;
    }
    
    public ContainerStatus getContainerStatus() {
        return this.containerStatus;
    }
    
    public NodeId getNodeId() {
        return this.nodeId;
    }
}
