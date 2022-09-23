// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public class RMStateStoreAppAttemptEvent extends RMStateStoreEvent
{
    RMStateStore.ApplicationAttemptState attemptState;
    
    public RMStateStoreAppAttemptEvent(final RMStateStore.ApplicationAttemptState attemptState) {
        super(RMStateStoreEventType.STORE_APP_ATTEMPT);
        this.attemptState = attemptState;
    }
    
    public RMStateStore.ApplicationAttemptState getAppAttemptState() {
        return this.attemptState;
    }
}
