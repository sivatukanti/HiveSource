// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public class SchedulerNodeReport
{
    private final Resource used;
    private final Resource avail;
    private final int num;
    
    public SchedulerNodeReport(final SchedulerNode node) {
        this.used = node.getUsedResource();
        this.avail = node.getAvailableResource();
        this.num = node.getNumContainers();
    }
    
    public Resource getUsedResource() {
        return this.used;
    }
    
    public Resource getAvailableResource() {
        return this.avail;
    }
    
    public int getNumContainers() {
        return this.num;
    }
}
