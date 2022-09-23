// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class AppRemovedSchedulerEvent extends SchedulerEvent
{
    private final ApplicationId applicationId;
    private final RMAppState finalState;
    
    public AppRemovedSchedulerEvent(final ApplicationId applicationId, final RMAppState finalState) {
        super(SchedulerEventType.APP_REMOVED);
        this.applicationId = applicationId;
        this.finalState = finalState;
    }
    
    public ApplicationId getApplicationID() {
        return this.applicationId;
    }
    
    public RMAppState getFinalState() {
        return this.finalState;
    }
}
