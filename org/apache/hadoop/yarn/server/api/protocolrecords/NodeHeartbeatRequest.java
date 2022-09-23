// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;

public abstract class NodeHeartbeatRequest
{
    public static NodeHeartbeatRequest newInstance(final NodeStatus nodeStatus, final MasterKey lastKnownContainerTokenMasterKey, final MasterKey lastKnownNMTokenMasterKey) {
        final NodeHeartbeatRequest nodeHeartbeatRequest = Records.newRecord(NodeHeartbeatRequest.class);
        nodeHeartbeatRequest.setNodeStatus(nodeStatus);
        nodeHeartbeatRequest.setLastKnownContainerTokenMasterKey(lastKnownContainerTokenMasterKey);
        nodeHeartbeatRequest.setLastKnownNMTokenMasterKey(lastKnownNMTokenMasterKey);
        return nodeHeartbeatRequest;
    }
    
    public abstract NodeStatus getNodeStatus();
    
    public abstract void setNodeStatus(final NodeStatus p0);
    
    public abstract MasterKey getLastKnownContainerTokenMasterKey();
    
    public abstract void setLastKnownContainerTokenMasterKey(final MasterKey p0);
    
    public abstract MasterKey getLastKnownNMTokenMasterKey();
    
    public abstract void setLastKnownNMTokenMasterKey(final MasterKey p0);
}
