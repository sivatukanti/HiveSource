// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.server.api.records.NodeAction;

public interface NodeHeartbeatResponse
{
    int getResponseId();
    
    NodeAction getNodeAction();
    
    List<ContainerId> getContainersToCleanup();
    
    List<ContainerId> getContainersToBeRemovedFromNM();
    
    List<ApplicationId> getApplicationsToCleanup();
    
    void setResponseId(final int p0);
    
    void setNodeAction(final NodeAction p0);
    
    MasterKey getContainerTokenMasterKey();
    
    void setContainerTokenMasterKey(final MasterKey p0);
    
    MasterKey getNMTokenMasterKey();
    
    void setNMTokenMasterKey(final MasterKey p0);
    
    void addAllContainersToCleanup(final List<ContainerId> p0);
    
    void addContainersToBeRemovedFromNM(final List<ContainerId> p0);
    
    void addAllApplicationsToCleanup(final List<ApplicationId> p0);
    
    long getNextHeartBeatInterval();
    
    void setNextHeartBeatInterval(final long p0);
    
    String getDiagnosticsMessage();
    
    void setDiagnosticsMessage(final String p0);
    
    Map<ApplicationId, ByteBuffer> getSystemCredentialsForApps();
    
    void setSystemCredentialsForApps(final Map<ApplicationId, ByteBuffer> p0);
}
