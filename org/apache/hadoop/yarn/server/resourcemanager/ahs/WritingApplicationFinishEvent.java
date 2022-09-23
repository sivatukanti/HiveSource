// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class WritingApplicationFinishEvent extends WritingApplicationHistoryEvent
{
    private ApplicationId appId;
    private ApplicationFinishData appFinish;
    
    public WritingApplicationFinishEvent(final ApplicationId appId, final ApplicationFinishData appFinish) {
        super(WritingHistoryEventType.APP_FINISH);
        this.appId = appId;
        this.appFinish = appFinish;
    }
    
    @Override
    public int hashCode() {
        return this.appId.hashCode();
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
    
    public ApplicationFinishData getApplicationFinishData() {
        return this.appFinish;
    }
}
