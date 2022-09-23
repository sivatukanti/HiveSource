// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;

public class RMAppRecoverEvent extends RMAppEvent
{
    private final RMStateStore.RMState state;
    
    public RMAppRecoverEvent(final ApplicationId appId, final RMStateStore.RMState state) {
        super(appId, RMAppEventType.RECOVER);
        this.state = state;
    }
    
    public RMStateStore.RMState getRMState() {
        return this.state;
    }
}
