// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;

public abstract class RegisterNodeManagerRequest
{
    public static RegisterNodeManagerRequest newInstance(final NodeId nodeId, final int httpPort, final Resource resource, final String nodeManagerVersionId, final List<NMContainerStatus> containerStatuses, final List<ApplicationId> runningApplications) {
        final RegisterNodeManagerRequest request = Records.newRecord(RegisterNodeManagerRequest.class);
        request.setHttpPort(httpPort);
        request.setResource(resource);
        request.setNodeId(nodeId);
        request.setNMVersion(nodeManagerVersionId);
        request.setContainerStatuses(containerStatuses);
        request.setRunningApplications(runningApplications);
        return request;
    }
    
    public abstract NodeId getNodeId();
    
    public abstract int getHttpPort();
    
    public abstract Resource getResource();
    
    public abstract String getNMVersion();
    
    public abstract List<NMContainerStatus> getNMContainerStatuses();
    
    public abstract List<ApplicationId> getRunningApplications();
    
    public abstract void setNodeId(final NodeId p0);
    
    public abstract void setHttpPort(final int p0);
    
    public abstract void setResource(final Resource p0);
    
    public abstract void setNMVersion(final String p0);
    
    public abstract void setContainerStatuses(final List<NMContainerStatus> p0);
    
    public abstract void setRunningApplications(final List<ApplicationId> p0);
}
