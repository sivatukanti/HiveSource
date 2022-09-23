// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;

public class RMAppAttemptContainerAllocatedEvent extends RMAppAttemptEvent
{
    public RMAppAttemptContainerAllocatedEvent(final ApplicationAttemptId appAttemptId) {
        super(appAttemptId, RMAppAttemptEventType.CONTAINER_ALLOCATED);
    }
}
