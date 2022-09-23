// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;

public class RMAppAttemptLaunchFailedEvent extends RMAppAttemptEvent
{
    private final String message;
    
    public RMAppAttemptLaunchFailedEvent(final ApplicationAttemptId appAttemptId, final String message) {
        super(appAttemptId, RMAppAttemptEventType.LAUNCH_FAILED);
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}
