// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import java.util.EnumSet;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.event.EventHandler;

public interface YarnScheduler extends EventHandler<SchedulerEvent>
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    QueueInfo getQueueInfo(final String p0, final boolean p1, final boolean p2) throws IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    List<QueueUserACLInfo> getQueueUserAclInfo();
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Unstable
    Resource getClusterResource();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    Resource getMinimumResourceCapability();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    Resource getMaximumResourceCapability();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    int getNumClusterNodes();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    Allocation allocate(final ApplicationAttemptId p0, final List<ResourceRequest> p1, final List<ContainerId> p2, final List<String> p3, final List<String> p4);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Stable
    SchedulerNodeReport getNodeReport(final NodeId p0);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Stable
    SchedulerAppReport getSchedulerAppInfo(final ApplicationAttemptId p0);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Evolving
    ApplicationResourceUsageReport getAppResourceUsageReport(final ApplicationAttemptId p0);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Evolving
    QueueMetrics getRootQueueMetrics();
    
    boolean checkAccess(final UserGroupInformation p0, final QueueACL p1, final String p2);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Stable
    List<ApplicationAttemptId> getAppsInQueue(final String p0);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Unstable
    RMContainer getRMContainer(final ContainerId p0);
    
    @InterfaceAudience.LimitedPrivate({ "yarn" })
    @InterfaceStability.Evolving
    String moveApplication(final ApplicationId p0, final String p1) throws YarnException;
    
    void moveAllApps(final String p0, final String p1) throws YarnException;
    
    void killAllAppsInQueue(final String p0) throws YarnException;
    
    void removeQueue(final String p0) throws YarnException;
    
    void addQueue(final Queue p0) throws YarnException;
    
    void setEntitlement(final String p0, final QueueEntitlement p1) throws YarnException;
    
    Set<String> getPlanQueues() throws YarnException;
    
    EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulingResourceTypes();
}
