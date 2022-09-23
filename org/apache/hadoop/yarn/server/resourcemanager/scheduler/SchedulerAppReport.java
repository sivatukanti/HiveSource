// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.Collection;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
@InterfaceAudience.LimitedPrivate({ "yarn" })
public class SchedulerAppReport
{
    private final Collection<RMContainer> live;
    private final Collection<RMContainer> reserved;
    private final boolean pending;
    
    public SchedulerAppReport(final SchedulerApplicationAttempt app) {
        this.live = app.getLiveContainers();
        this.reserved = app.getReservedContainers();
        this.pending = app.isPending();
    }
    
    public Collection<RMContainer> getLiveContainers() {
        return this.live;
    }
    
    public Collection<RMContainer> getReservedContainers() {
        return this.reserved;
    }
    
    public boolean isPending() {
        return this.pending;
    }
}
