// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public class NodeReport
{
    private final Resource usedResources;
    private final int numContainers;
    
    public NodeReport(final Resource used, final int numContainers) {
        this.usedResources = used;
        this.numContainers = numContainers;
    }
    
    public Resource getUsedResources() {
        return this.usedResources;
    }
    
    public int getNumContainers() {
        return this.numContainers;
    }
}
