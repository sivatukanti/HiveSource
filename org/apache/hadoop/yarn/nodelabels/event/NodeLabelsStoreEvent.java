// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels.event;

import org.apache.hadoop.yarn.event.AbstractEvent;

public class NodeLabelsStoreEvent extends AbstractEvent<NodeLabelsStoreEventType>
{
    public NodeLabelsStoreEvent(final NodeLabelsStoreEventType type) {
        super(type);
    }
}
