// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;

public class RMNodeStatusEvent extends RMNodeEvent
{
    private final NodeHealthStatus nodeHealthStatus;
    private final List<ContainerStatus> containersCollection;
    private final NodeHeartbeatResponse latestResponse;
    private final List<ApplicationId> keepAliveAppIds;
    
    public RMNodeStatusEvent(final NodeId nodeId, final NodeHealthStatus nodeHealthStatus, final List<ContainerStatus> collection, final List<ApplicationId> keepAliveAppIds, final NodeHeartbeatResponse latestResponse) {
        super(nodeId, RMNodeEventType.STATUS_UPDATE);
        this.nodeHealthStatus = nodeHealthStatus;
        this.containersCollection = collection;
        this.keepAliveAppIds = keepAliveAppIds;
        this.latestResponse = latestResponse;
    }
    
    public NodeHealthStatus getNodeHealthStatus() {
        return this.nodeHealthStatus;
    }
    
    public List<ContainerStatus> getContainers() {
        return this.containersCollection;
    }
    
    public NodeHeartbeatResponse getLatestResponse() {
        return this.latestResponse;
    }
    
    public List<ApplicationId> getKeepAliveAppIds() {
        return this.keepAliveAppIds;
    }
}
