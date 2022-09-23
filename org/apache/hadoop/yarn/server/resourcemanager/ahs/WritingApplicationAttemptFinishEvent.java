// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class WritingApplicationAttemptFinishEvent extends WritingApplicationHistoryEvent
{
    private ApplicationAttemptId appAttemptId;
    private ApplicationAttemptFinishData appAttemptFinish;
    
    public WritingApplicationAttemptFinishEvent(final ApplicationAttemptId appAttemptId, final ApplicationAttemptFinishData appAttemptFinish) {
        super(WritingHistoryEventType.APP_ATTEMPT_FINISH);
        this.appAttemptId = appAttemptId;
        this.appAttemptFinish = appAttemptFinish;
    }
    
    @Override
    public int hashCode() {
        return this.appAttemptId.getApplicationId().hashCode();
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
    
    public ApplicationAttemptFinishData getApplicationAttemptFinishData() {
        return this.appAttemptFinish;
    }
}
