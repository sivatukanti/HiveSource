// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;

public class RMAppNodeUpdateEvent extends RMAppEvent
{
    private final RMNode node;
    private final RMAppNodeUpdateType updateType;
    
    public RMAppNodeUpdateEvent(final ApplicationId appId, final RMNode node, final RMAppNodeUpdateType updateType) {
        super(appId, RMAppEventType.NODE_UPDATE);
        this.node = node;
        this.updateType = updateType;
    }
    
    public RMNode getNode() {
        return this.node;
    }
    
    public RMAppNodeUpdateType getUpdateType() {
        return this.updateType;
    }
    
    public enum RMAppNodeUpdateType
    {
        NODE_USABLE, 
        NODE_UNUSABLE;
    }
}
