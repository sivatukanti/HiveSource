// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;

public class RMAppRunningOnNodeEvent extends RMAppEvent
{
    private final NodeId node;
    
    public RMAppRunningOnNodeEvent(final ApplicationId appId, final NodeId node) {
        super(appId, RMAppEventType.APP_RUNNING_ON_NODE);
        this.node = node;
    }
    
    public NodeId getNodeId() {
        return this.node;
    }
}
