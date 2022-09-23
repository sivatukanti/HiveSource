// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;

public interface PlanContext
{
    long getStep();
    
    ReservationAgent getReservationAgent();
    
    Planner getReplanner();
    
    SharingPolicy getSharingPolicy();
    
    ResourceCalculator getResourceCalculator();
    
    Resource getMinimumAllocation();
    
    Resource getMaximumAllocation();
    
    String getQueueName();
    
    QueueMetrics getQueueMetrics();
    
    boolean getMoveOnExpiry();
}
