// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;

public class NodeAddedSchedulerEvent extends SchedulerEvent
{
    private final RMNode rmNode;
    private final List<NMContainerStatus> containerReports;
    
    public NodeAddedSchedulerEvent(final RMNode rmNode) {
        super(SchedulerEventType.NODE_ADDED);
        this.rmNode = rmNode;
        this.containerReports = null;
    }
    
    public NodeAddedSchedulerEvent(final RMNode rmNode, final List<NMContainerStatus> containerReports) {
        super(SchedulerEventType.NODE_ADDED);
        this.rmNode = rmNode;
        this.containerReports = containerReports;
    }
    
    public RMNode getAddedRMNode() {
        return this.rmNode;
    }
    
    public List<NMContainerStatus> getContainerReports() {
        return this.containerReports;
    }
}
