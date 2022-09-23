// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;

public class RMAppAttemptStatusupdateEvent extends RMAppAttemptEvent
{
    private final float progress;
    
    public RMAppAttemptStatusupdateEvent(final ApplicationAttemptId appAttemptId, final float progress) {
        super(appAttemptId, RMAppAttemptEventType.STATUS_UPDATE);
        this.progress = progress;
    }
    
    public float getProgress() {
        return this.progress;
    }
}
