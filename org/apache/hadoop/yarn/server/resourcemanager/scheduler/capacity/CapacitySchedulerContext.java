// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import java.util.Comparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.api.records.Resource;

public interface CapacitySchedulerContext
{
    CapacitySchedulerConfiguration getConfiguration();
    
    Resource getMinimumResourceCapability();
    
    Resource getMaximumResourceCapability();
    
    RMContainerTokenSecretManager getContainerTokenSecretManager();
    
    int getNumClusterNodes();
    
    RMContext getRMContext();
    
    Resource getClusterResource();
    
    Configuration getConf();
    
    Comparator<FiCaSchedulerApp> getApplicationComparator();
    
    ResourceCalculator getResourceCalculator();
    
    Comparator<CSQueue> getQueueComparator();
    
    FiCaSchedulerNode getNode(final NodeId p0);
}
