// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;

public class RMAppRejectedEvent extends RMAppEvent
{
    private final String message;
    
    public RMAppRejectedEvent(final ApplicationId appId, final String message) {
        super(appId, RMAppEventType.APP_REJECTED);
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}
