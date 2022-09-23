// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import java.util.List;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
@InterfaceAudience.LimitedPrivate({ "yarn" })
public interface Queue
{
    String getQueueName();
    
    QueueMetrics getMetrics();
    
    QueueInfo getQueueInfo(final boolean p0, final boolean p1);
    
    List<QueueUserACLInfo> getQueueUserAclInfo(final UserGroupInformation p0);
    
    boolean hasAccess(final QueueACL p0, final UserGroupInformation p1);
    
    ActiveUsersManager getActiveUsersManager();
    
    void recoverContainer(final Resource p0, final SchedulerApplicationAttempt p1, final RMContainer p2);
    
    Set<String> getAccessibleNodeLabels();
    
    String getDefaultNodeLabelExpression();
}
