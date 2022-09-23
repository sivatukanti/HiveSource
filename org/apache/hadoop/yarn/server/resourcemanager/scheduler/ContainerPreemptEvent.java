// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class ContainerPreemptEvent extends AbstractEvent<ContainerPreemptEventType>
{
    private final ApplicationAttemptId aid;
    private final RMContainer container;
    
    public ContainerPreemptEvent(final ApplicationAttemptId aid, final RMContainer container, final ContainerPreemptEventType type) {
        super(type);
        this.aid = aid;
        this.container = container;
    }
    
    public RMContainer getContainer() {
        return this.container;
    }
    
    public ApplicationAttemptId getAppId() {
        return this.aid;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" ").append(this.getAppId());
        sb.append(" ").append(this.getContainer().getContainerId());
        return sb.toString();
    }
}
