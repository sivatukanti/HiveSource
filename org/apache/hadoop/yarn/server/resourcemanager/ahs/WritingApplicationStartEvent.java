// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class WritingApplicationStartEvent extends WritingApplicationHistoryEvent
{
    private ApplicationId appId;
    private ApplicationStartData appStart;
    
    public WritingApplicationStartEvent(final ApplicationId appId, final ApplicationStartData appStart) {
        super(WritingHistoryEventType.APP_START);
        this.appId = appId;
        this.appStart = appStart;
    }
    
    @Override
    public int hashCode() {
        return this.appId.hashCode();
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
    
    public ApplicationStartData getApplicationStartData() {
        return this.appStart;
    }
}
