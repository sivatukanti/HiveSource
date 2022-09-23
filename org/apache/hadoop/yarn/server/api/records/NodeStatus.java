// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeId;

public abstract class NodeStatus
{
    public static NodeStatus newInstance(final NodeId nodeId, final int responseId, final List<ContainerStatus> containerStatuses, final List<ApplicationId> keepAliveApplications, final NodeHealthStatus nodeHealthStatus) {
        final NodeStatus nodeStatus = Records.newRecord(NodeStatus.class);
        nodeStatus.setResponseId(responseId);
        nodeStatus.setNodeId(nodeId);
        nodeStatus.setContainersStatuses(containerStatuses);
        nodeStatus.setKeepAliveApplications(keepAliveApplications);
        nodeStatus.setNodeHealthStatus(nodeHealthStatus);
        return nodeStatus;
    }
    
    public abstract NodeId getNodeId();
    
    public abstract int getResponseId();
    
    public abstract List<ContainerStatus> getContainersStatuses();
    
    public abstract void setContainersStatuses(final List<ContainerStatus> p0);
    
    public abstract List<ApplicationId> getKeepAliveApplications();
    
    public abstract void setKeepAliveApplications(final List<ApplicationId> p0);
    
    public abstract NodeHealthStatus getNodeHealthStatus();
    
    public abstract void setNodeHealthStatus(final NodeHealthStatus p0);
    
    public abstract void setNodeId(final NodeId p0);
    
    public abstract void setResponseId(final int p0);
}
