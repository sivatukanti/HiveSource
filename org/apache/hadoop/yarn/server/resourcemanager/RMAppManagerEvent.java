// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class RMAppManagerEvent extends AbstractEvent<RMAppManagerEventType>
{
    private final ApplicationId appId;
    
    public RMAppManagerEvent(final ApplicationId appId, final RMAppManagerEventType type) {
        super(type);
        this.appId = appId;
    }
    
    public ApplicationId getApplicationId() {
        return this.appId;
    }
}
