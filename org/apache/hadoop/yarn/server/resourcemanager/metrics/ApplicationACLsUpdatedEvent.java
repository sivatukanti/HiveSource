// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.api.records.ApplicationId;

public class ApplicationACLsUpdatedEvent extends SystemMetricsEvent
{
    private ApplicationId appId;
    private String viewAppACLs;
    
    public ApplicationACLsUpdatedEvent(final ApplicationId appId, final String viewAppACLs, final long updatedTime) {
        super(SystemMetricsEventType.APP_ACLS_UPDATED, updatedTime);
        this.appId = appId;
        this.viewAppACLs = viewAppACLs;
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
    
    public String getViewAppACLs() {
        return this.viewAppACLs;
    }
}
