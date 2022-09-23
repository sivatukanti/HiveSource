// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.List;

public class RMNodeReconnectEvent extends RMNodeEvent
{
    private RMNode reconnectedNode;
    private List<ApplicationId> runningApplications;
    
    public RMNodeReconnectEvent(final NodeId nodeId, final RMNode newNode, final List<ApplicationId> runningApps) {
        super(nodeId, RMNodeEventType.RECONNECTED);
        this.reconnectedNode = newNode;
        this.runningApplications = runningApps;
    }
    
    public RMNode getReconnectedNode() {
        return this.reconnectedNode;
    }
    
    public List<ApplicationId> getRunningApplications() {
        return this.runningApplications;
    }
}
