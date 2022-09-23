// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.monitor;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.PreemptableResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEvent;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.conf.Configuration;

public interface SchedulingEditPolicy
{
    void init(final Configuration p0, final EventHandler<ContainerPreemptEvent> p1, final PreemptableResourceScheduler p2);
    
    void editSchedule();
    
    long getMonitoringInterval();
    
    String getPolicyName();
}
