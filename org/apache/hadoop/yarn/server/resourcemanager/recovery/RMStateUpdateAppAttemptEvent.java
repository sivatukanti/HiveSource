// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public class RMStateUpdateAppAttemptEvent extends RMStateStoreEvent
{
    RMStateStore.ApplicationAttemptState attemptState;
    
    public RMStateUpdateAppAttemptEvent(final RMStateStore.ApplicationAttemptState attemptState) {
        super(RMStateStoreEventType.UPDATE_APP_ATTEMPT);
        this.attemptState = attemptState;
    }
    
    public RMStateStore.ApplicationAttemptState getAppAttemptState() {
        return this.attemptState;
    }
}
