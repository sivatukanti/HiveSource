// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;

public class RMContainerRecoverEvent extends RMContainerEvent
{
    private final NMContainerStatus containerReport;
    
    public RMContainerRecoverEvent(final ContainerId containerId, final NMContainerStatus containerReport) {
        super(containerId, RMContainerEventType.RECOVER);
        this.containerReport = containerReport;
    }
    
    public NMContainerStatus getContainerReport() {
        return this.containerReport;
    }
}
