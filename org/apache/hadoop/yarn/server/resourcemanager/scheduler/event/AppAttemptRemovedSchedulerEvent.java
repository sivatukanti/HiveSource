// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class AppAttemptRemovedSchedulerEvent extends SchedulerEvent
{
    private final ApplicationAttemptId applicationAttemptId;
    private final RMAppAttemptState finalAttemptState;
    private final boolean keepContainersAcrossAppAttempts;
    
    public AppAttemptRemovedSchedulerEvent(final ApplicationAttemptId applicationAttemptId, final RMAppAttemptState finalAttemptState, final boolean keepContainers) {
        super(SchedulerEventType.APP_ATTEMPT_REMOVED);
        this.applicationAttemptId = applicationAttemptId;
        this.finalAttemptState = finalAttemptState;
        this.keepContainersAcrossAppAttempts = keepContainers;
    }
    
    public ApplicationAttemptId getApplicationAttemptID() {
        return this.applicationAttemptId;
    }
    
    public RMAppAttemptState getFinalAttemptState() {
        return this.finalAttemptState;
    }
    
    public boolean getKeepContainersAcrossAppAttempts() {
        return this.keepContainersAcrossAppAttempts;
    }
}
