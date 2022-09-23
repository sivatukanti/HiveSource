// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public class RMStateStoreRemoveAppEvent extends RMStateStoreEvent
{
    RMStateStore.ApplicationState appState;
    
    RMStateStoreRemoveAppEvent(final RMStateStore.ApplicationState appState) {
        super(RMStateStoreEventType.REMOVE_APP);
        this.appState = appState;
    }
    
    public RMStateStore.ApplicationState getAppState() {
        return this.appState;
    }
}
