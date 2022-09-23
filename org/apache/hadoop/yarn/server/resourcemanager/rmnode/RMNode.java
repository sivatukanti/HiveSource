// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import java.util.Set;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;

public interface RMNode
{
    public static final int OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT = -1;
    
    NodeId getNodeID();
    
    String getHostName();
    
    int getCommandPort();
    
    int getHttpPort();
    
    String getNodeAddress();
    
    String getHttpAddress();
    
    String getHealthReport();
    
    long getLastHealthReportTime();
    
    String getNodeManagerVersion();
    
    Resource getTotalCapability();
    
    String getRackName();
    
    Node getNode();
    
    NodeState getState();
    
    List<ContainerId> getContainersToCleanUp();
    
    List<ApplicationId> getAppsToCleanup();
    
    void updateNodeHeartbeatResponseForCleanup(final NodeHeartbeatResponse p0);
    
    NodeHeartbeatResponse getLastNodeHeartBeatResponse();
    
    List<UpdatedContainerInfo> pullContainerUpdates();
    
    Set<String> getNodeLabels();
}
