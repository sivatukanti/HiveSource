// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.event.AbstractEvent;

public class SystemMetricsEvent extends AbstractEvent<SystemMetricsEventType>
{
    public SystemMetricsEvent(final SystemMetricsEventType type) {
        super(type);
    }
    
    public SystemMetricsEvent(final SystemMetricsEventType type, final long timestamp) {
        super(type, timestamp);
    }
}
