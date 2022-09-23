// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;

public class RMAppAttemptUnregistrationEvent extends RMAppAttemptEvent
{
    private final String finalTrackingUrl;
    private final FinalApplicationStatus finalStatus;
    private final String diagnostics;
    
    public RMAppAttemptUnregistrationEvent(final ApplicationAttemptId appAttemptId, final String trackingUrl, final FinalApplicationStatus finalStatus, final String diagnostics) {
        super(appAttemptId, RMAppAttemptEventType.UNREGISTERED);
        this.finalTrackingUrl = trackingUrl;
        this.finalStatus = finalStatus;
        this.diagnostics = diagnostics;
    }
    
    public String getFinalTrackingUrl() {
        return this.finalTrackingUrl;
    }
    
    public FinalApplicationStatus getFinalApplicationStatus() {
        return this.finalStatus;
    }
    
    public String getDiagnostics() {
        return this.diagnostics;
    }
}
