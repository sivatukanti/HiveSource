// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class AppAttemptAddedSchedulerEvent extends SchedulerEvent
{
    private final ApplicationAttemptId applicationAttemptId;
    private final boolean transferStateFromPreviousAttempt;
    private final boolean isAttemptRecovering;
    
    public AppAttemptAddedSchedulerEvent(final ApplicationAttemptId applicationAttemptId, final boolean transferStateFromPreviousAttempt) {
        this(applicationAttemptId, transferStateFromPreviousAttempt, false);
    }
    
    public AppAttemptAddedSchedulerEvent(final ApplicationAttemptId applicationAttemptId, final boolean transferStateFromPreviousAttempt, final boolean isAttemptRecovering) {
        super(SchedulerEventType.APP_ATTEMPT_ADDED);
        this.applicationAttemptId = applicationAttemptId;
        this.transferStateFromPreviousAttempt = transferStateFromPreviousAttempt;
        this.isAttemptRecovering = isAttemptRecovering;
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.applicationAttemptId;
    }
    
    public boolean getTransferStateFromPreviousAttempt() {
        return this.transferStateFromPreviousAttempt;
    }
    
    public boolean getIsAttemptRecovering() {
        return this.isAttemptRecovering;
    }
}
