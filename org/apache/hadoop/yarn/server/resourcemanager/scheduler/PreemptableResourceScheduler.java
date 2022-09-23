// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;

public interface PreemptableResourceScheduler extends ResourceScheduler
{
    void dropContainerReservation(final RMContainer p0);
    
    void preemptContainer(final ApplicationAttemptId p0, final RMContainer p1);
    
    void killContainer(final RMContainer p0);
}
