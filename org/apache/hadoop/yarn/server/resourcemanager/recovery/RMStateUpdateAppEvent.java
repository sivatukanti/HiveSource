// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public class RMStateUpdateAppEvent extends RMStateStoreEvent
{
    private final RMStateStore.ApplicationState appState;
    
    public RMStateUpdateAppEvent(final RMStateStore.ApplicationState appState) {
        super(RMStateStoreEventType.UPDATE_APP);
        this.appState = appState;
    }
    
    public RMStateStore.ApplicationState getAppState() {
        return this.appState;
    }
}
