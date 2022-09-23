// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface Schedulable
{
    String getName();
    
    Resource getDemand();
    
    Resource getResourceUsage();
    
    Resource getMinShare();
    
    Resource getMaxShare();
    
    ResourceWeights getWeights();
    
    long getStartTime();
    
    Priority getPriority();
    
    void updateDemand();
    
    Resource assignContainer(final FSSchedulerNode p0);
    
    RMContainer preemptContainer();
    
    Resource getFairShare();
    
    void setFairShare(final Resource p0);
}
