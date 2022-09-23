// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class WritingApplicationAttemptStartEvent extends WritingApplicationHistoryEvent
{
    private ApplicationAttemptId appAttemptId;
    private ApplicationAttemptStartData appAttemptStart;
    
    public WritingApplicationAttemptStartEvent(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStartData appAttemptStart) {
        super(WritingHistoryEventType.APP_ATTEMPT_START);
        this.appAttemptId = appAttemptId;
        this.appAttemptStart = appAttemptStart;
    }
    
    @Override
    public int hashCode() {
        return this.appAttemptId.getApplicationId().hashCode();
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
    
    public ApplicationAttemptStartData getApplicationAttemptStartData() {
        return this.appAttemptStart;
    }
}
