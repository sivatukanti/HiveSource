// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class RMAppAttemptEvent extends AbstractEvent<RMAppAttemptEventType>
{
    private final ApplicationAttemptId appAttemptId;
    
    public RMAppAttemptEvent(final ApplicationAttemptId appAttemptId, final RMAppAttemptEventType type) {
        super(type);
        this.appAttemptId = appAttemptId;
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
}
