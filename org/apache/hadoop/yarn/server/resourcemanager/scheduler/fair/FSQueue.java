// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class FSQueue implements Queue, Schedulable
{
    private Resource fairShare;
    private Resource steadyFairShare;
    private final String name;
    protected final FairScheduler scheduler;
    private final FSQueueMetrics metrics;
    protected final FSParentQueue parent;
    protected final RecordFactory recordFactory;
    protected SchedulingPolicy policy;
    private long fairSharePreemptionTimeout;
    private long minSharePreemptionTimeout;
    private float fairSharePreemptionThreshold;
    
    public FSQueue(final String name, final FairScheduler scheduler, final FSParentQueue parent) {
        this.fairShare = Resources.createResource(0, 0);
        this.steadyFairShare = Resources.createResource(0, 0);
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.policy = SchedulingPolicy.DEFAULT_POLICY;
        this.fairSharePreemptionTimeout = Long.MAX_VALUE;
        this.minSharePreemptionTimeout = Long.MAX_VALUE;
        this.fairSharePreemptionThreshold = 0.5f;
        this.name = name;
        this.scheduler = scheduler;
        (this.metrics = FSQueueMetrics.forQueue(this.getName(), parent, true, scheduler.getConf())).setMinShare(this.getMinShare());
        this.metrics.setMaxShare(this.getMaxShare());
        this.parent = parent;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getQueueName() {
        return this.name;
    }
    
    public SchedulingPolicy getPolicy() {
        return this.policy;
    }
    
    public FSParentQueue getParent() {
        return this.parent;
    }
    
    protected void throwPolicyDoesnotApplyException(final SchedulingPolicy policy) throws AllocationConfigurationException {
        throw new AllocationConfigurationException("SchedulingPolicy " + policy + " does not apply to queue " + this.getName());
    }
    
    public abstract void setPolicy(final SchedulingPolicy p0) throws AllocationConfigurationException;
    
    @Override
    public ResourceWeights getWeights() {
        return this.scheduler.getAllocationConfiguration().getQueueWeight(this.getName());
    }
    
    @Override
    public Resource getMinShare() {
        return this.scheduler.getAllocationConfiguration().getMinResources(this.getName());
    }
    
    @Override
    public Resource getMaxShare() {
        return this.scheduler.getAllocationConfiguration().getMaxResources(this.getName());
    }
    
    @Override
    public long getStartTime() {
        return 0L;
    }
    
    @Override
    public Priority getPriority() {
        final Priority p = this.recordFactory.newRecordInstance(Priority.class);
        p.setPriority(1);
        return p;
    }
    
    @Override
    public QueueInfo getQueueInfo(final boolean includeChildQueues, final boolean recursive) {
        final QueueInfo queueInfo = this.recordFactory.newRecordInstance(QueueInfo.class);
        queueInfo.setQueueName(this.getQueueName());
        queueInfo.setCapacity(this.getFairShare().getMemory() / (float)this.scheduler.getClusterResource().getMemory());
        queueInfo.setCapacity(this.getResourceUsage().getMemory() / (float)this.scheduler.getClusterResource().getMemory());
        final ArrayList<QueueInfo> childQueueInfos = new ArrayList<QueueInfo>();
        if (includeChildQueues) {
            final Collection<FSQueue> childQueues = this.getChildQueues();
            for (final FSQueue child : childQueues) {
                childQueueInfos.add(child.getQueueInfo(recursive, recursive));
            }
        }
        queueInfo.setChildQueues(childQueueInfos);
        queueInfo.setQueueState(QueueState.RUNNING);
        return queueInfo;
    }
    
    @Override
    public FSQueueMetrics getMetrics() {
        return this.metrics;
    }
    
    @Override
    public Resource getFairShare() {
        return this.fairShare;
    }
    
    @Override
    public void setFairShare(final Resource fairShare) {
        this.fairShare = fairShare;
        this.metrics.setFairShare(fairShare);
    }
    
    public Resource getSteadyFairShare() {
        return this.steadyFairShare;
    }
    
    public void setSteadyFairShare(final Resource steadyFairShare) {
        this.steadyFairShare = steadyFairShare;
        this.metrics.setSteadyFairShare(steadyFairShare);
    }
    
    @Override
    public boolean hasAccess(final QueueACL acl, final UserGroupInformation user) {
        return this.scheduler.getAllocationConfiguration().hasAccess(this.name, acl, user);
    }
    
    public long getFairSharePreemptionTimeout() {
        return this.fairSharePreemptionTimeout;
    }
    
    public void setFairSharePreemptionTimeout(final long fairSharePreemptionTimeout) {
        this.fairSharePreemptionTimeout = fairSharePreemptionTimeout;
    }
    
    public long getMinSharePreemptionTimeout() {
        return this.minSharePreemptionTimeout;
    }
    
    public void setMinSharePreemptionTimeout(final long minSharePreemptionTimeout) {
        this.minSharePreemptionTimeout = minSharePreemptionTimeout;
    }
    
    public float getFairSharePreemptionThreshold() {
        return this.fairSharePreemptionThreshold;
    }
    
    public void setFairSharePreemptionThreshold(final float fairSharePreemptionThreshold) {
        this.fairSharePreemptionThreshold = fairSharePreemptionThreshold;
    }
    
    public abstract void recomputeShares();
    
    public void updatePreemptionVariables() {
        this.minSharePreemptionTimeout = this.scheduler.getAllocationConfiguration().getMinSharePreemptionTimeout(this.getName());
        if (this.minSharePreemptionTimeout == -1L && this.parent != null) {
            this.minSharePreemptionTimeout = this.parent.getMinSharePreemptionTimeout();
        }
        this.fairSharePreemptionTimeout = this.scheduler.getAllocationConfiguration().getFairSharePreemptionTimeout(this.getName());
        if (this.fairSharePreemptionTimeout == -1L && this.parent != null) {
            this.fairSharePreemptionTimeout = this.parent.getFairSharePreemptionTimeout();
        }
        this.fairSharePreemptionThreshold = this.scheduler.getAllocationConfiguration().getFairSharePreemptionThreshold(this.getName());
        if (this.fairSharePreemptionThreshold < 0.0f && this.parent != null) {
            this.fairSharePreemptionThreshold = this.parent.getFairSharePreemptionThreshold();
        }
    }
    
    public abstract List<FSQueue> getChildQueues();
    
    public abstract void collectSchedulerApplications(final Collection<ApplicationAttemptId> p0);
    
    public abstract int getNumRunnableApps();
    
    protected boolean assignContainerPreCheck(final FSSchedulerNode node) {
        return Resources.fitsIn(this.getResourceUsage(), this.scheduler.getAllocationConfiguration().getMaxResources(this.getName())) && node.getReservedContainer() == null;
    }
    
    public boolean isActive() {
        return this.getNumRunnableApps() > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, demand=%s, running=%s, share=%s, w=%s]", this.getName(), this.getDemand(), this.getResourceUsage(), this.fairShare, this.getWeights());
    }
    
    @Override
    public Set<String> getAccessibleNodeLabels() {
        return null;
    }
    
    @Override
    public String getDefaultNodeLabelExpression() {
        return null;
    }
}
