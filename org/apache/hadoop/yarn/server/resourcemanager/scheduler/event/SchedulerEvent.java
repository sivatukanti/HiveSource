// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.event.AbstractEvent;

public class SchedulerEvent extends AbstractEvent<SchedulerEventType>
{
    public SchedulerEvent(final SchedulerEventType type) {
        super(type);
    }
}
