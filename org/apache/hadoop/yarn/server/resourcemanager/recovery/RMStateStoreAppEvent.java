// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public class RMStateStoreAppEvent extends RMStateStoreEvent
{
    private final RMStateStore.ApplicationState appState;
    
    public RMStateStoreAppEvent(final RMStateStore.ApplicationState appState) {
        super(RMStateStoreEventType.STORE_APP);
        this.appState = appState;
    }
    
    public RMStateStore.ApplicationState getAppState() {
        return this.appState;
    }
}
