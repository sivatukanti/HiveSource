// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;

public class RMAppFailedAttemptEvent extends RMAppEvent
{
    private final String diagnostics;
    private final boolean transferStateFromPreviousAttempt;
    
    public RMAppFailedAttemptEvent(final ApplicationId appId, final RMAppEventType event, final String diagnostics, final boolean transferStateFromPreviousAttempt) {
        super(appId, event);
        this.diagnostics = diagnostics;
        this.transferStateFromPreviousAttempt = transferStateFromPreviousAttempt;
    }
    
    public String getDiagnostics() {
        return this.diagnostics;
    }
    
    public boolean getTransferStateFromPreviousAttempt() {
        return this.transferStateFromPreviousAttempt;
    }
}
