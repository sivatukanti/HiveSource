// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class QueueInfo
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static QueueInfo newInstance(final String queueName, final float capacity, final float maximumCapacity, final float currentCapacity, final List<QueueInfo> childQueues, final List<ApplicationReport> applications, final QueueState queueState, final Set<String> accessibleNodeLabels, final String defaultNodeLabelExpression) {
        final QueueInfo queueInfo = Records.newRecord(QueueInfo.class);
        queueInfo.setQueueName(queueName);
        queueInfo.setCapacity(capacity);
        queueInfo.setMaximumCapacity(maximumCapacity);
        queueInfo.setCurrentCapacity(currentCapacity);
        queueInfo.setChildQueues(childQueues);
        queueInfo.setApplications(applications);
        queueInfo.setQueueState(queueState);
        queueInfo.setAccessibleNodeLabels(accessibleNodeLabels);
        queueInfo.setDefaultNodeLabelExpression(defaultNodeLabelExpression);
        return queueInfo;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueueName();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueueName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract float getCapacity();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setCapacity(final float p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract float getMaximumCapacity();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setMaximumCapacity(final float p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract float getCurrentCapacity();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setCurrentCapacity(final float p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<QueueInfo> getChildQueues();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setChildQueues(final List<QueueInfo> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ApplicationReport> getApplications();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplications(final List<ApplicationReport> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract QueueState getQueueState();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueueState(final QueueState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Set<String> getAccessibleNodeLabels();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAccessibleNodeLabels(final Set<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getDefaultNodeLabelExpression();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setDefaultNodeLabelExpression(final String p0);
}
