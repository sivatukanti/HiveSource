// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class CSAssignment
{
    private final Resource resource;
    private NodeType type;
    private final RMContainer excessReservation;
    private final FiCaSchedulerApp application;
    private final boolean skipped;
    
    public CSAssignment(final Resource resource, final NodeType type) {
        this.resource = resource;
        this.type = type;
        this.application = null;
        this.excessReservation = null;
        this.skipped = false;
    }
    
    public CSAssignment(final FiCaSchedulerApp application, final RMContainer excessReservation) {
        this.resource = excessReservation.getContainer().getResource();
        this.type = NodeType.NODE_LOCAL;
        this.application = application;
        this.excessReservation = excessReservation;
        this.skipped = false;
    }
    
    public CSAssignment(final boolean skipped) {
        this.resource = Resources.createResource(0, 0);
        this.type = NodeType.NODE_LOCAL;
        this.application = null;
        this.excessReservation = null;
        this.skipped = skipped;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public NodeType getType() {
        return this.type;
    }
    
    public void setType(final NodeType type) {
        this.type = type;
    }
    
    public FiCaSchedulerApp getApplication() {
        return this.application;
    }
    
    public RMContainer getExcessReservation() {
        return this.excessReservation;
    }
    
    public boolean getSkipped() {
        return this.skipped;
    }
    
    @Override
    public String toString() {
        return this.resource.getMemory() + ":" + this.type;
    }
}
