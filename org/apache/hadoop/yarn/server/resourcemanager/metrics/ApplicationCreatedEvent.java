// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.ApplicationId;

public class ApplicationCreatedEvent extends SystemMetricsEvent
{
    private ApplicationId appId;
    private String name;
    private String type;
    private String user;
    private String queue;
    private long submittedTime;
    
    public ApplicationCreatedEvent(final ApplicationId appId, final String name, final String type, final String user, final String queue, final long submittedTime, final long createdTime) {
        super(SystemMetricsEventType.APP_CREATED, createdTime);
        this.appId = appId;
        this.name = name;
        this.type = type;
        this.user = user;
        this.queue = queue;
        this.submittedTime = submittedTime;
    }
    
    @Override
    public int hashCode() {
        return this.appId.hashCode();
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
    
    public String getApplicationName() {
        return this.name;
    }
    
    public String getApplicationType() {
        return this.type;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getQueue() {
        return this.queue;
    }
    
    public long getSubmittedTime() {
        return this.submittedTime;
    }
}
