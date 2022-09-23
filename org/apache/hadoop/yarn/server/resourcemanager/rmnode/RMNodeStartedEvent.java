// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import java.util.List;

public class RMNodeStartedEvent extends RMNodeEvent
{
    private List<NMContainerStatus> containerStatuses;
    private List<ApplicationId> runningApplications;
    
    public RMNodeStartedEvent(final NodeId nodeId, final List<NMContainerStatus> containerReports, final List<ApplicationId> runningApplications) {
        super(nodeId, RMNodeEventType.STARTED);
        this.containerStatuses = containerReports;
        this.runningApplications = runningApplications;
    }
    
    public List<NMContainerStatus> getNMContainerStatuses() {
        return this.containerStatuses;
    }
    
    public List<ApplicationId> getRunningApplications() {
        return this.runningApplications;
    }
}
