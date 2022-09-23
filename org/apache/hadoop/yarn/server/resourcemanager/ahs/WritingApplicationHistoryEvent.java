// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.event.AbstractEvent;

public class WritingApplicationHistoryEvent extends AbstractEvent<WritingHistoryEventType>
{
    public WritingApplicationHistoryEvent(final WritingHistoryEventType type) {
        super(type);
    }
}
