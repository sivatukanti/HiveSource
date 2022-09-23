// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

public class AppAttemptRegisteredEvent extends SystemMetricsEvent
{
    private ApplicationAttemptId appAttemptId;
    private String host;
    private int rpcPort;
    private String trackingUrl;
    private String originalTrackingUrl;
    private ContainerId masterContainerId;
    
    public AppAttemptRegisteredEvent(final ApplicationAttemptId appAttemptId, final String host, final int rpcPort, final String trackingUrl, final String originalTrackingUrl, final ContainerId masterContainerId, final long registeredTime) {
        super(SystemMetricsEventType.APP_ATTEMPT_REGISTERED, registeredTime);
        this.appAttemptId = appAttemptId;
        this.host = host;
        this.rpcPort = rpcPort;
        this.trackingUrl = trackingUrl;
        this.originalTrackingUrl = originalTrackingUrl;
        this.masterContainerId = masterContainerId;
    }
    
    @Override
    public int hashCode() {
        return this.appAttemptId.getApplicationId().hashCode();
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getRpcPort() {
        return this.rpcPort;
    }
    
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    
    public String getOriginalTrackingURL() {
        return this.originalTrackingUrl;
    }
    
    public ContainerId getMasterContainerId() {
        return this.masterContainerId;
    }
}
