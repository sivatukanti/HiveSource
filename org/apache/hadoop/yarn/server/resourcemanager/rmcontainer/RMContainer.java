// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.EventHandler;

public interface RMContainer extends EventHandler<RMContainerEvent>
{
    ContainerId getContainerId();
    
    ApplicationAttemptId getApplicationAttemptId();
    
    RMContainerState getState();
    
    Container getContainer();
    
    Resource getReservedResource();
    
    NodeId getReservedNode();
    
    Priority getReservedPriority();
    
    Resource getAllocatedResource();
    
    NodeId getAllocatedNode();
    
    Priority getAllocatedPriority();
    
    long getCreationTime();
    
    long getFinishTime();
    
    String getDiagnosticsInfo();
    
    String getLogURL();
    
    int getContainerExitStatus();
    
    ContainerState getContainerState();
    
    ContainerReport createContainerReport();
    
    boolean isAMContainer();
    
    List<ResourceRequest> getResourceRequests();
}
