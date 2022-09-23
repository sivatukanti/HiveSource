// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;

public class RMAppFinishedAttemptEvent extends RMAppEvent
{
    private final String diagnostics;
    
    public RMAppFinishedAttemptEvent(final ApplicationId appId, final String diagnostics) {
        super(appId, RMAppEventType.ATTEMPT_FINISHED);
        this.diagnostics = diagnostics;
    }
    
    public String getDiagnostics() {
        return this.diagnostics;
    }
}
