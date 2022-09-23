// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class ApplicationFinishedEvent extends SystemMetricsEvent
{
    private ApplicationId appId;
    private String diagnosticsInfo;
    private FinalApplicationStatus appStatus;
    private YarnApplicationState state;
    private ApplicationAttemptId latestAppAttemptId;
    
    public ApplicationFinishedEvent(final ApplicationId appId, final String diagnosticsInfo, final FinalApplicationStatus appStatus, final YarnApplicationState state, final ApplicationAttemptId latestAppAttemptId, final long finishedTime) {
        super(SystemMetricsEventType.APP_FINISHED, finishedTime);
        this.appId = appId;
        this.diagnosticsInfo = diagnosticsInfo;
        this.appStatus = appStatus;
        this.latestAppAttemptId = latestAppAttemptId;
        this.state = state;
    }
    
    @Override
    public int hashCode() {
        return this.appId.hashCode();
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public FinalApplicationStatus getFinalApplicationStatus() {
        return this.appStatus;
    }
    
    public YarnApplicationState getYarnApplicationState() {
        return this.state;
    }
    
    public ApplicationAttemptId getLatestApplicationAttemptId() {
        return this.latestAppAttemptId;
    }
}
