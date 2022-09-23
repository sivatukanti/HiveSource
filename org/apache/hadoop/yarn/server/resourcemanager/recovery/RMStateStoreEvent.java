// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.event.AbstractEvent;

public class RMStateStoreEvent extends AbstractEvent<RMStateStoreEventType>
{
    public RMStateStoreEvent(final RMStateStoreEventType type) {
        super(type);
    }
}
