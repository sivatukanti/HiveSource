// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class RMNodeCleanAppEvent extends RMNodeEvent
{
    private ApplicationId appId;
    
    public RMNodeCleanAppEvent(final NodeId nodeId, final ApplicationId appId) {
        super(nodeId, RMNodeEventType.CLEANUP_APP);
        this.appId = appId;
    }
    
    public ApplicationId getAppId() {
        return this.appId;
    }
}
