// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.Comparator;
import java.util.Collections;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FSParentQueue extends FSQueue
{
    private static final Log LOG;
    private final List<FSQueue> childQueues;
    private Resource demand;
    private int runnableApps;
    
    public FSParentQueue(final String name, final FairScheduler scheduler, final FSParentQueue parent) {
        super(name, scheduler, parent);
        this.childQueues = new ArrayList<FSQueue>();
        this.demand = Resources.createResource(0);
    }
    
    public void addChildQueue(final FSQueue child) {
        this.childQueues.add(child);
    }
    
    @Override
    public void recomputeShares() {
        this.policy.computeShares(this.childQueues, this.getFairShare());
        for (final FSQueue childQueue : this.childQueues) {
            childQueue.getMetrics().setFairShare(childQueue.getFairShare());
            childQueue.recomputeShares();
        }
    }
    
    public void recomputeSteadyShares() {
        this.policy.computeSteadyShares(this.childQueues, this.getSteadyFairShare());
        for (final FSQueue childQueue : this.childQueues) {
            childQueue.getMetrics().setSteadyFairShare(childQueue.getSteadyFairShare());
            if (childQueue instanceof FSParentQueue) {
                ((FSParentQueue)childQueue).recomputeSteadyShares();
            }
        }
    }
    
    @Override
    public void updatePreemptionVariables() {
        super.updatePreemptionVariables();
        for (final FSQueue childQueue : this.childQueues) {
            childQueue.updatePreemptionVariables();
        }
    }
    
    @Override
    public Resource getDemand() {
        return this.demand;
    }
    
    @Override
    public Resource getResourceUsage() {
        final Resource usage = Resources.createResource(0);
        for (final FSQueue child : this.childQueues) {
            Resources.addTo(usage, child.getResourceUsage());
        }
        return usage;
    }
    
    @Override
    public void updateDemand() {
        final Resource maxRes = this.scheduler.getAllocationConfiguration().getMaxResources(this.getName());
        this.demand = Resources.createResource(0);
        for (final FSQueue childQueue : this.childQueues) {
            childQueue.updateDemand();
            final Resource toAdd = childQueue.getDemand();
            if (FSParentQueue.LOG.isDebugEnabled()) {
                FSParentQueue.LOG.debug("Counting resource from " + childQueue.getName() + " " + toAdd + "; Total resource consumption for " + this.getName() + " now " + this.demand);
            }
            this.demand = Resources.add(this.demand, toAdd);
            this.demand = Resources.componentwiseMin(this.demand, maxRes);
            if (Resources.equals(this.demand, maxRes)) {
                break;
            }
        }
        if (FSParentQueue.LOG.isDebugEnabled()) {
            FSParentQueue.LOG.debug("The updated demand for " + this.getName() + " is " + this.demand + "; the max is " + maxRes);
        }
    }
    
    private synchronized QueueUserACLInfo getUserAclInfo(final UserGroupInformation user) {
        final QueueUserACLInfo userAclInfo = this.recordFactory.newRecordInstance(QueueUserACLInfo.class);
        final List<QueueACL> operations = new ArrayList<QueueACL>();
        for (final QueueACL operation : QueueACL.values()) {
            if (this.hasAccess(operation, user)) {
                operations.add(operation);
            }
        }
        userAclInfo.setQueueName(this.getQueueName());
        userAclInfo.setUserAcls(operations);
        return userAclInfo;
    }
    
    @Override
    public synchronized List<QueueUserACLInfo> getQueueUserAclInfo(final UserGroupInformation user) {
        final List<QueueUserACLInfo> userAcls = new ArrayList<QueueUserACLInfo>();
        userAcls.add(this.getUserAclInfo(user));
        for (final FSQueue child : this.childQueues) {
            userAcls.addAll(child.getQueueUserAclInfo(user));
        }
        return userAcls;
    }
    
    @Override
    public Resource assignContainer(final FSSchedulerNode node) {
        Resource assigned = Resources.none();
        if (!this.assignContainerPreCheck(node)) {
            return assigned;
        }
        Collections.sort(this.childQueues, this.policy.getComparator());
        for (final FSQueue child : this.childQueues) {
            assigned = child.assignContainer(node);
            if (!Resources.equals(assigned, Resources.none())) {
                break;
            }
        }
        return assigned;
    }
    
    @Override
    public RMContainer preemptContainer() {
        RMContainer toBePreempted = null;
        FSQueue candidateQueue = null;
        final Comparator<Schedulable> comparator = this.policy.getComparator();
        for (final FSQueue queue : this.childQueues) {
            if (candidateQueue == null || comparator.compare(queue, candidateQueue) > 0) {
                candidateQueue = queue;
            }
        }
        if (candidateQueue != null) {
            toBePreempted = candidateQueue.preemptContainer();
        }
        return toBePreempted;
    }
    
    @Override
    public List<FSQueue> getChildQueues() {
        return this.childQueues;
    }
    
    @Override
    public void setPolicy(final SchedulingPolicy policy) throws AllocationConfigurationException {
        final boolean allowed = SchedulingPolicy.isApplicableTo(policy, (byte)((this.parent == null) ? 4 : 2));
        if (!allowed) {
            this.throwPolicyDoesnotApplyException(policy);
        }
        super.policy = policy;
    }
    
    public void incrementRunnableApps() {
        ++this.runnableApps;
    }
    
    public void decrementRunnableApps() {
        --this.runnableApps;
    }
    
    @Override
    public int getNumRunnableApps() {
        return this.runnableApps;
    }
    
    @Override
    public void collectSchedulerApplications(final Collection<ApplicationAttemptId> apps) {
        for (final FSQueue childQueue : this.childQueues) {
            childQueue.collectSchedulerApplications(apps);
        }
    }
    
    @Override
    public ActiveUsersManager getActiveUsersManager() {
        return null;
    }
    
    @Override
    public void recoverContainer(final Resource clusterResource, final SchedulerApplicationAttempt schedulerAttempt, final RMContainer rmContainer) {
    }
    
    static {
        LOG = LogFactory.getLog(FSParentQueue.class.getName());
    }
}
