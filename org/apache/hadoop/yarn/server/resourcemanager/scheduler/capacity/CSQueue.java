// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.List;
import org.apache.hadoop.yarn.api.records.QueueState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;

@InterfaceStability.Stable
@InterfaceAudience.Private
public interface CSQueue extends Queue
{
    CSQueue getParent();
    
    void setParent(final CSQueue p0);
    
    String getQueueName();
    
    String getQueuePath();
    
    float getCapacity();
    
    float getAbsActualCapacity();
    
    float getAbsoluteCapacity();
    
    float getMaximumCapacity();
    
    float getAbsoluteMaximumCapacity();
    
    float getAbsoluteUsedCapacity();
    
    void setUsedCapacity(final float p0);
    
    void setAbsoluteUsedCapacity(final float p0);
    
    float getUsedCapacity();
    
    Resource getUsedResources();
    
    QueueState getState();
    
    List<CSQueue> getChildQueues();
    
    boolean hasAccess(final QueueACL p0, final UserGroupInformation p1);
    
    void submitApplication(final ApplicationId p0, final String p1, final String p2) throws AccessControlException;
    
    void submitApplicationAttempt(final FiCaSchedulerApp p0, final String p1);
    
    void finishApplication(final ApplicationId p0, final String p1);
    
    void finishApplicationAttempt(final FiCaSchedulerApp p0, final String p1);
    
    CSAssignment assignContainers(final Resource p0, final FiCaSchedulerNode p1, final boolean p2);
    
    void completedContainer(final Resource p0, final FiCaSchedulerApp p1, final FiCaSchedulerNode p2, final RMContainer p3, final ContainerStatus p4, final RMContainerEventType p5, final CSQueue p6, final boolean p7);
    
    int getNumApplications();
    
    void reinitialize(final CSQueue p0, final Resource p1) throws IOException;
    
    void updateClusterResource(final Resource p0);
    
    ActiveUsersManager getActiveUsersManager();
    
    void collectSchedulerApplications(final Collection<ApplicationAttemptId> p0);
    
    void detachContainer(final Resource p0, final FiCaSchedulerApp p1, final RMContainer p2);
    
    void attachContainer(final Resource p0, final FiCaSchedulerApp p1, final RMContainer p2);
    
    float getAbsoluteCapacityByNodeLabel(final String p0);
    
    float getAbsoluteMaximumCapacityByNodeLabel(final String p0);
    
    float getCapacityByNodeLabel(final String p0);
}
