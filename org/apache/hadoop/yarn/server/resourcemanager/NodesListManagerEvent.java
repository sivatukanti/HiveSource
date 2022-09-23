// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class NodesListManagerEvent extends AbstractEvent<NodesListManagerEventType>
{
    private final RMNode node;
    
    public NodesListManagerEvent(final NodesListManagerEventType type, final RMNode node) {
        super(type);
        this.node = node;
    }
    
    public RMNode getNode() {
        return this.node;
    }
}
