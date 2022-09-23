// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerAppUtils;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FSLeafQueue extends FSQueue
{
    private static final Log LOG;
    private final List<FSAppAttempt> runnableApps;
    private final List<FSAppAttempt> nonRunnableApps;
    private Resource demand;
    private long lastTimeAtMinShare;
    private long lastTimeAtFairShareThreshold;
    private Resource amResourceUsage;
    private final ActiveUsersManager activeUsersManager;
    
    public FSLeafQueue(final String name, final FairScheduler scheduler, final FSParentQueue parent) {
        super(name, scheduler, parent);
        this.runnableApps = new ArrayList<FSAppAttempt>();
        this.nonRunnableApps = new ArrayList<FSAppAttempt>();
        this.demand = Resources.createResource(0);
        this.lastTimeAtMinShare = scheduler.getClock().getTime();
        this.lastTimeAtFairShareThreshold = scheduler.getClock().getTime();
        this.activeUsersManager = new ActiveUsersManager(this.getMetrics());
        this.amResourceUsage = Resource.newInstance(0, 0);
    }
    
    public void addApp(final FSAppAttempt app, final boolean runnable) {
        if (runnable) {
            this.runnableApps.add(app);
        }
        else {
            this.nonRunnableApps.add(app);
        }
    }
    
    void addAppSchedulable(final FSAppAttempt appSched) {
        this.runnableApps.add(appSched);
    }
    
    public boolean removeApp(final FSAppAttempt app) {
        if (this.runnableApps.remove(app)) {
            if (app.isAmRunning() && app.getAMResource() != null) {
                Resources.subtractFrom(this.amResourceUsage, app.getAMResource());
            }
            return true;
        }
        if (this.nonRunnableApps.remove(app)) {
            return false;
        }
        throw new IllegalStateException("Given app to remove " + app + " does not exist in queue " + this);
    }
    
    public Collection<FSAppAttempt> getRunnableAppSchedulables() {
        return this.runnableApps;
    }
    
    public List<FSAppAttempt> getNonRunnableAppSchedulables() {
        return this.nonRunnableApps;
    }
    
    @Override
    public void collectSchedulerApplications(final Collection<ApplicationAttemptId> apps) {
        for (final FSAppAttempt appSched : this.runnableApps) {
            apps.add(appSched.getApplicationAttemptId());
        }
        for (final FSAppAttempt appSched : this.nonRunnableApps) {
            apps.add(appSched.getApplicationAttemptId());
        }
    }
    
    @Override
    public void setPolicy(final SchedulingPolicy policy) throws AllocationConfigurationException {
        if (!SchedulingPolicy.isApplicableTo(policy, (byte)1)) {
            this.throwPolicyDoesnotApplyException(policy);
        }
        super.policy = policy;
    }
    
    @Override
    public void recomputeShares() {
        this.policy.computeShares(this.getRunnableAppSchedulables(), this.getFairShare());
    }
    
    @Override
    public Resource getDemand() {
        return this.demand;
    }
    
    @Override
    public Resource getResourceUsage() {
        final Resource usage = Resources.createResource(0);
        for (final FSAppAttempt app : this.runnableApps) {
            Resources.addTo(usage, app.getResourceUsage());
        }
        for (final FSAppAttempt app : this.nonRunnableApps) {
            Resources.addTo(usage, app.getResourceUsage());
        }
        return usage;
    }
    
    public Resource getAmResourceUsage() {
        return this.amResourceUsage;
    }
    
    @Override
    public void updateDemand() {
        final Resource maxRes = this.scheduler.getAllocationConfiguration().getMaxResources(this.getName());
        this.demand = Resources.createResource(0);
        for (final FSAppAttempt sched : this.runnableApps) {
            if (Resources.equals(this.demand, maxRes)) {
                break;
            }
            this.updateDemandForApp(sched, maxRes);
        }
        for (final FSAppAttempt sched : this.nonRunnableApps) {
            if (Resources.equals(this.demand, maxRes)) {
                break;
            }
            this.updateDemandForApp(sched, maxRes);
        }
        if (FSLeafQueue.LOG.isDebugEnabled()) {
            FSLeafQueue.LOG.debug("The updated demand for " + this.getName() + " is " + this.demand + "; the max is " + maxRes);
        }
    }
    
    private void updateDemandForApp(final FSAppAttempt sched, final Resource maxRes) {
        sched.updateDemand();
        final Resource toAdd = sched.getDemand();
        if (FSLeafQueue.LOG.isDebugEnabled()) {
            FSLeafQueue.LOG.debug("Counting resource from " + sched.getName() + " " + toAdd + "; Total resource consumption for " + this.getName() + " now " + this.demand);
        }
        this.demand = Resources.add(this.demand, toAdd);
        this.demand = Resources.componentwiseMin(this.demand, maxRes);
    }
    
    @Override
    public Resource assignContainer(final FSSchedulerNode node) {
        Resource assigned = Resources.none();
        if (FSLeafQueue.LOG.isDebugEnabled()) {
            FSLeafQueue.LOG.debug("Node " + node.getNodeName() + " offered to queue: " + this.getName());
        }
        if (!this.assignContainerPreCheck(node)) {
            return assigned;
        }
        final Comparator<Schedulable> comparator = this.policy.getComparator();
        Collections.sort(this.runnableApps, comparator);
        for (final FSAppAttempt sched : this.runnableApps) {
            if (SchedulerAppUtils.isBlacklisted(sched, node, FSLeafQueue.LOG)) {
                continue;
            }
            assigned = sched.assignContainer(node);
            if (!assigned.equals(Resources.none())) {
                break;
            }
        }
        return assigned;
    }
    
    @Override
    public RMContainer preemptContainer() {
        RMContainer toBePreempted = null;
        if (!this.preemptContainerPreCheck()) {
            return toBePreempted;
        }
        if (FSLeafQueue.LOG.isDebugEnabled()) {
            FSLeafQueue.LOG.debug("Queue " + this.getName() + " is going to preempt a container " + "from its applications.");
        }
        final Comparator<Schedulable> comparator = this.policy.getComparator();
        FSAppAttempt candidateSched = null;
        for (final FSAppAttempt sched : this.runnableApps) {
            if (candidateSched == null || comparator.compare(sched, candidateSched) > 0) {
                candidateSched = sched;
            }
        }
        if (candidateSched != null) {
            toBePreempted = candidateSched.preemptContainer();
        }
        return toBePreempted;
    }
    
    @Override
    public List<FSQueue> getChildQueues() {
        return new ArrayList<FSQueue>(1);
    }
    
    @Override
    public List<QueueUserACLInfo> getQueueUserAclInfo(final UserGroupInformation user) {
        final QueueUserACLInfo userAclInfo = this.recordFactory.newRecordInstance(QueueUserACLInfo.class);
        final List<QueueACL> operations = new ArrayList<QueueACL>();
        for (final QueueACL operation : QueueACL.values()) {
            if (this.hasAccess(operation, user)) {
                operations.add(operation);
            }
        }
        userAclInfo.setQueueName(this.getQueueName());
        userAclInfo.setUserAcls(operations);
        return Collections.singletonList(userAclInfo);
    }
    
    public long getLastTimeAtMinShare() {
        return this.lastTimeAtMinShare;
    }
    
    private void setLastTimeAtMinShare(final long lastTimeAtMinShare) {
        this.lastTimeAtMinShare = lastTimeAtMinShare;
    }
    
    public long getLastTimeAtFairShareThreshold() {
        return this.lastTimeAtFairShareThreshold;
    }
    
    private void setLastTimeAtFairShareThreshold(final long lastTimeAtFairShareThreshold) {
        this.lastTimeAtFairShareThreshold = lastTimeAtFairShareThreshold;
    }
    
    @Override
    public int getNumRunnableApps() {
        return this.runnableApps.size();
    }
    
    @Override
    public ActiveUsersManager getActiveUsersManager() {
        return this.activeUsersManager;
    }
    
    public boolean canRunAppAM(final Resource amResource) {
        final float maxAMShare = this.scheduler.getAllocationConfiguration().getQueueMaxAMShare(this.getName());
        if (Math.abs(maxAMShare + 1.0f) < 1.0E-4) {
            return true;
        }
        final Resource maxAMResource = Resources.multiply(this.getFairShare(), maxAMShare);
        final Resource ifRunAMResource = Resources.add(this.amResourceUsage, amResource);
        return !this.policy.checkIfAMResourceUsageOverLimit(ifRunAMResource, maxAMResource);
    }
    
    public void addAMResourceUsage(final Resource amResource) {
        if (amResource != null) {
            Resources.addTo(this.amResourceUsage, amResource);
        }
    }
    
    @Override
    public void recoverContainer(final Resource clusterResource, final SchedulerApplicationAttempt schedulerAttempt, final RMContainer rmContainer) {
    }
    
    public void updateStarvationStats() {
        final long now = this.scheduler.getClock().getTime();
        if (!this.isStarvedForMinShare()) {
            this.setLastTimeAtMinShare(now);
        }
        if (!this.isStarvedForFairShare()) {
            this.setLastTimeAtFairShareThreshold(now);
        }
    }
    
    private boolean preemptContainerPreCheck() {
        return this.parent.getPolicy().checkIfUsageOverFairShare(this.getResourceUsage(), this.getFairShare());
    }
    
    @VisibleForTesting
    boolean isStarvedForMinShare() {
        return this.isStarved(this.getMinShare());
    }
    
    @VisibleForTesting
    boolean isStarvedForFairShare() {
        return this.isStarved(Resources.multiply(this.getFairShare(), this.getFairSharePreemptionThreshold()));
    }
    
    private boolean isStarved(final Resource share) {
        final Resource desiredShare = Resources.min(FairScheduler.getResourceCalculator(), this.scheduler.getClusterResource(), share, this.getDemand());
        return Resources.lessThan(FairScheduler.getResourceCalculator(), this.scheduler.getClusterResource(), this.getResourceUsage(), desiredShare);
    }
    
    static {
        LOG = LogFactory.getLog(FSLeafQueue.class.getName());
    }
}
