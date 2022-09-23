// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.util.Clock;

public interface PlanFollower extends Runnable
{
    void init(final Clock p0, final ResourceScheduler p1, final Collection<Plan> p2);
    
    void synchronizePlan(final Plan p0);
    
    void setPlans(final Collection<Plan> p0);
}
