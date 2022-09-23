// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class AppAttemptFinishedEvent extends SystemMetricsEvent
{
    private ApplicationAttemptId appAttemptId;
    private String trackingUrl;
    private String originalTrackingUrl;
    private String diagnosticsInfo;
    private FinalApplicationStatus appStatus;
    private YarnApplicationAttemptState state;
    
    public AppAttemptFinishedEvent(final ApplicationAttemptId appAttemptId, final String trackingUrl, final String originalTrackingUrl, final String diagnosticsInfo, final FinalApplicationStatus appStatus, final YarnApplicationAttemptState state, final long finishedTime) {
        super(SystemMetricsEventType.APP_ATTEMPT_FINISHED, finishedTime);
        this.appAttemptId = appAttemptId;
        this.trackingUrl = trackingUrl;
        this.originalTrackingUrl = originalTrackingUrl;
        this.diagnosticsInfo = diagnosticsInfo;
        this.appStatus = appStatus;
        this.state = state;
    }
    
    @Override
    public int hashCode() {
        return this.appAttemptId.getApplicationId().hashCode();
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
    
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    
    public String getOriginalTrackingURL() {
        return this.originalTrackingUrl;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public FinalApplicationStatus getFinalApplicationStatus() {
        return this.appStatus;
    }
    
    public YarnApplicationAttemptState getYarnApplicationAttemptState() {
        return this.state;
    }
}
