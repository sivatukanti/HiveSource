// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class RMAppStartAttemptEvent extends RMAppAttemptEvent
{
    private final boolean transferStateFromPreviousAttempt;
    
    public RMAppStartAttemptEvent(final ApplicationAttemptId appAttemptId, final boolean transferStateFromPreviousAttempt) {
        super(appAttemptId, RMAppAttemptEventType.START);
        this.transferStateFromPreviousAttempt = transferStateFromPreviousAttempt;
    }
    
    public boolean getTransferStateFromPreviousAttempt() {
        return this.transferStateFromPreviousAttempt;
    }
}
