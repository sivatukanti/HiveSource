// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeResourceUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.api.records.Priority;
import java.util.Collections;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.UpdatedContainerInfo;
import java.util.Set;
import java.util.HashSet;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRejectedEvent;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.Iterator;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.SystemClock;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Comparator;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public class FairScheduler extends AbstractYarnScheduler<FSAppAttempt, FSSchedulerNode>
{
    private FairSchedulerConfiguration conf;
    private Resource incrAllocation;
    private QueueManager queueMgr;
    private volatile Clock clock;
    private boolean usePortForNodeName;
    private static final Log LOG;
    private static final ResourceCalculator RESOURCE_CALCULATOR;
    public static final Resource CONTAINER_RESERVED;
    protected long updateInterval;
    private final int UPDATE_DEBUG_FREQUENCY = 5;
    private int updatesToSkipForDebug;
    @VisibleForTesting
    Thread updateThread;
    @VisibleForTesting
    Thread schedulingThread;
    protected final long THREAD_JOIN_TIMEOUT_MS = 1000L;
    FSQueueMetrics rootMetrics;
    FSOpDurations fsOpDurations;
    protected long lastPreemptionUpdateTime;
    private long lastPreemptCheckTime;
    protected boolean preemptionEnabled;
    protected float preemptionUtilizationThreshold;
    protected long preemptionInterval;
    protected long waitTimeBeforeKill;
    private List<RMContainer> warnedContainers;
    protected boolean sizeBasedWeight;
    protected WeightAdjuster weightAdjuster;
    protected boolean continuousSchedulingEnabled;
    protected int continuousSchedulingSleepMs;
    private Comparator<NodeId> nodeAvailableResourceComparator;
    protected double nodeLocalityThreshold;
    protected double rackLocalityThreshold;
    protected long nodeLocalityDelayMs;
    protected long rackLocalityDelayMs;
    private FairSchedulerEventLog eventLog;
    protected boolean assignMultiple;
    protected int maxAssign;
    @VisibleForTesting
    final MaxRunningAppsEnforcer maxRunningEnforcer;
    private AllocationFileLoaderService allocsLoader;
    @VisibleForTesting
    AllocationConfiguration allocConf;
    
    public FairScheduler() {
        super(FairScheduler.class.getName());
        this.updatesToSkipForDebug = 5;
        this.warnedContainers = new ArrayList<RMContainer>();
        this.nodeAvailableResourceComparator = new NodeAvailableResourceComparator();
        this.clock = new SystemClock();
        this.allocsLoader = new AllocationFileLoaderService();
        this.queueMgr = new QueueManager(this);
        this.maxRunningEnforcer = new MaxRunningAppsEnforcer(this);
    }
    
    private void validateConf(final Configuration conf) {
        final int minMem = conf.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        final int maxMem = conf.getInt("yarn.scheduler.maximum-allocation-mb", 8192);
        if (minMem < 0 || minMem > maxMem) {
            throw new YarnRuntimeException("Invalid resource scheduler memory allocation configuration, yarn.scheduler.minimum-allocation-mb=" + minMem + ", " + "yarn.scheduler.maximum-allocation-mb" + "=" + maxMem + ", min should equal greater than 0" + ", max should be no smaller than min.");
        }
        final int minVcores = conf.getInt("yarn.scheduler.minimum-allocation-vcores", 1);
        final int maxVcores = conf.getInt("yarn.scheduler.maximum-allocation-vcores", 4);
        if (minVcores < 0 || minVcores > maxVcores) {
            throw new YarnRuntimeException("Invalid resource scheduler vcores allocation configuration, yarn.scheduler.minimum-allocation-vcores=" + minVcores + ", " + "yarn.scheduler.maximum-allocation-vcores" + "=" + maxVcores + ", min should equal greater than 0" + ", max should be no smaller than min.");
        }
    }
    
    public FairSchedulerConfiguration getConf() {
        return this.conf;
    }
    
    public QueueManager getQueueManager() {
        return this.queueMgr;
    }
    
    protected synchronized void update() {
        final long start = this.getClock().getTime();
        this.updateStarvationStats();
        final FSQueue rootQueue = this.queueMgr.getRootQueue();
        rootQueue.updateDemand();
        rootQueue.setFairShare(this.clusterResource);
        rootQueue.recomputeShares();
        if (FairScheduler.LOG.isDebugEnabled() && --this.updatesToSkipForDebug < 0) {
            this.updatesToSkipForDebug = 5;
            FairScheduler.LOG.debug("Cluster Capacity: " + this.clusterResource + "  Allocations: " + this.rootMetrics.getAllocatedResources() + "  Availability: " + Resource.newInstance(this.rootMetrics.getAvailableMB(), this.rootMetrics.getAvailableVirtualCores()) + "  Demand: " + rootQueue.getDemand());
        }
        final long duration = this.getClock().getTime() - start;
        this.fsOpDurations.addUpdateCallDuration(duration);
    }
    
    private void updateStarvationStats() {
        this.lastPreemptionUpdateTime = this.clock.getTime();
        for (final FSLeafQueue sched : this.queueMgr.getLeafQueues()) {
            sched.updateStarvationStats();
        }
    }
    
    protected synchronized void preemptTasksIfNecessary() {
        if (!this.shouldAttemptPreemption()) {
            return;
        }
        final long curTime = this.getClock().getTime();
        if (curTime - this.lastPreemptCheckTime < this.preemptionInterval) {
            return;
        }
        this.lastPreemptCheckTime = curTime;
        final Resource resToPreempt = Resources.clone(Resources.none());
        for (final FSLeafQueue sched : this.queueMgr.getLeafQueues()) {
            Resources.addTo(resToPreempt, this.resToPreempt(sched, curTime));
        }
        if (Resources.greaterThan(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, resToPreempt, Resources.none())) {
            this.preemptResources(resToPreempt);
        }
    }
    
    protected void preemptResources(final Resource toPreempt) {
        final long start = this.getClock().getTime();
        if (Resources.equals(toPreempt, Resources.none())) {
            return;
        }
        final Iterator<RMContainer> warnedIter = this.warnedContainers.iterator();
        while (warnedIter.hasNext()) {
            final RMContainer container = warnedIter.next();
            if ((container.getState() == RMContainerState.RUNNING || container.getState() == RMContainerState.ALLOCATED) && Resources.greaterThan(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, toPreempt, Resources.none())) {
                this.warnOrKillContainer(container);
                Resources.subtractFrom(toPreempt, container.getContainer().getResource());
            }
            else {
                warnedIter.remove();
            }
        }
        try {
            for (final FSLeafQueue queue : this.getQueueManager().getLeafQueues()) {
                for (final FSAppAttempt app : queue.getRunnableAppSchedulables()) {
                    app.resetPreemptedResources();
                }
            }
            while (Resources.greaterThan(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, toPreempt, Resources.none())) {
                final RMContainer container = this.getQueueManager().getRootQueue().preemptContainer();
                if (container == null) {
                    break;
                }
                this.warnOrKillContainer(container);
                this.warnedContainers.add(container);
                Resources.subtractFrom(toPreempt, container.getContainer().getResource());
            }
        }
        finally {
            for (final FSLeafQueue queue2 : this.getQueueManager().getLeafQueues()) {
                for (final FSAppAttempt app2 : queue2.getRunnableAppSchedulables()) {
                    app2.clearPreemptedResources();
                }
            }
        }
        final long duration = this.getClock().getTime() - start;
        this.fsOpDurations.addPreemptCallDuration(duration);
    }
    
    protected void warnOrKillContainer(final RMContainer container) {
        final ApplicationAttemptId appAttemptId = container.getApplicationAttemptId();
        final FSAppAttempt app = this.getSchedulerApp(appAttemptId);
        final FSLeafQueue queue = app.getQueue();
        FairScheduler.LOG.info("Preempting container (prio=" + container.getContainer().getPriority() + "res=" + container.getContainer().getResource() + ") from queue " + queue.getName());
        final Long time = app.getContainerPreemptionTime(container);
        if (time != null) {
            if (time + this.waitTimeBeforeKill < this.getClock().getTime()) {
                final ContainerStatus status = SchedulerUtils.createPreemptedContainerStatus(container.getContainerId(), "Container preempted by scheduler");
                this.recoverResourceRequestForContainer(container);
                this.completedContainer(container, status, RMContainerEventType.KILL);
                FairScheduler.LOG.info("Killing container" + container + " (after waiting for premption for " + (this.getClock().getTime() - time) + "ms)");
            }
        }
        else {
            app.addPreemption(container, this.getClock().getTime());
        }
    }
    
    protected Resource resToPreempt(final FSLeafQueue sched, final long curTime) {
        final long minShareTimeout = sched.getMinSharePreemptionTimeout();
        final long fairShareTimeout = sched.getFairSharePreemptionTimeout();
        Resource resDueToMinShare = Resources.none();
        Resource resDueToFairShare = Resources.none();
        if (curTime - sched.getLastTimeAtMinShare() > minShareTimeout) {
            final Resource target = Resources.min(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, sched.getMinShare(), sched.getDemand());
            resDueToMinShare = Resources.max(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, Resources.none(), Resources.subtract(target, sched.getResourceUsage()));
        }
        if (curTime - sched.getLastTimeAtFairShareThreshold() > fairShareTimeout) {
            final Resource target = Resources.min(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, sched.getFairShare(), sched.getDemand());
            resDueToFairShare = Resources.max(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, Resources.none(), Resources.subtract(target, sched.getResourceUsage()));
        }
        final Resource resToPreempt = Resources.max(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, resDueToMinShare, resDueToFairShare);
        if (Resources.greaterThan(FairScheduler.RESOURCE_CALCULATOR, this.clusterResource, resToPreempt, Resources.none())) {
            final String message = "Should preempt " + resToPreempt + " res for queue " + sched.getName() + ": resDueToMinShare = " + resDueToMinShare + ", resDueToFairShare = " + resDueToFairShare;
            FairScheduler.LOG.info(message);
        }
        return resToPreempt;
    }
    
    public synchronized RMContainerTokenSecretManager getContainerTokenSecretManager() {
        return this.rmContext.getContainerTokenSecretManager();
    }
    
    public synchronized ResourceWeights getAppWeight(final FSAppAttempt app) {
        double weight = 1.0;
        if (this.sizeBasedWeight) {
            weight = Math.log1p(app.getDemand().getMemory()) / Math.log(2.0);
        }
        weight *= app.getPriority().getPriority();
        if (this.weightAdjuster != null) {
            weight = this.weightAdjuster.adjustWeight(app, weight);
        }
        final ResourceWeights resourceWeights = app.getResourceWeights();
        resourceWeights.setWeight((float)weight);
        return resourceWeights;
    }
    
    public Resource getIncrementResourceCapability() {
        return this.incrAllocation;
    }
    
    private FSSchedulerNode getFSSchedulerNode(final NodeId nodeId) {
        return (FSSchedulerNode)this.nodes.get(nodeId);
    }
    
    public double getNodeLocalityThreshold() {
        return this.nodeLocalityThreshold;
    }
    
    public double getRackLocalityThreshold() {
        return this.rackLocalityThreshold;
    }
    
    public long getNodeLocalityDelayMs() {
        return this.nodeLocalityDelayMs;
    }
    
    public long getRackLocalityDelayMs() {
        return this.rackLocalityDelayMs;
    }
    
    public boolean isContinuousSchedulingEnabled() {
        return this.continuousSchedulingEnabled;
    }
    
    public synchronized int getContinuousSchedulingSleepMs() {
        return this.continuousSchedulingSleepMs;
    }
    
    public Clock getClock() {
        return this.clock;
    }
    
    @VisibleForTesting
    void setClock(final Clock clock) {
        this.clock = clock;
    }
    
    public FairSchedulerEventLog getEventLog() {
        return this.eventLog;
    }
    
    protected synchronized void addApplication(final ApplicationId applicationId, final String queueName, final String user, final boolean isAppRecovering) {
        if (queueName == null || queueName.isEmpty()) {
            final String message = "Reject application " + applicationId + " submitted by user " + user + " with an empty queue name.";
            FairScheduler.LOG.info(message);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
            return;
        }
        final RMApp rmApp = this.rmContext.getRMApps().get(applicationId);
        final FSLeafQueue queue = this.assignToQueue(rmApp, queueName, user);
        if (queue == null) {
            return;
        }
        final UserGroupInformation userUgi = UserGroupInformation.createRemoteUser(user);
        if (!queue.hasAccess(QueueACL.SUBMIT_APPLICATIONS, userUgi) && !queue.hasAccess(QueueACL.ADMINISTER_QUEUE, userUgi)) {
            final String msg = "User " + userUgi.getUserName() + " cannot submit applications to queue " + queue.getName();
            FairScheduler.LOG.info(msg);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, msg));
            return;
        }
        final SchedulerApplication<FSAppAttempt> application = new SchedulerApplication<FSAppAttempt>(queue, user);
        this.applications.put(applicationId, (SchedulerApplication<T>)application);
        queue.getMetrics().submitApp(user);
        FairScheduler.LOG.info("Accepted application " + applicationId + " from user: " + user + ", in queue: " + queueName + ", currently num of applications: " + this.applications.size());
        if (isAppRecovering) {
            if (FairScheduler.LOG.isDebugEnabled()) {
                FairScheduler.LOG.debug(applicationId + " is recovering. Skip notifying APP_ACCEPTED");
            }
        }
        else {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(applicationId, RMAppEventType.APP_ACCEPTED));
        }
    }
    
    protected synchronized void addApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final boolean transferStateFromPreviousAttempt, final boolean isAttemptRecovering) {
        final SchedulerApplication<FSAppAttempt> application = (SchedulerApplication<FSAppAttempt>)this.applications.get(applicationAttemptId.getApplicationId());
        final String user = application.getUser();
        final FSLeafQueue queue = (FSLeafQueue)application.getQueue();
        final FSAppAttempt attempt = new FSAppAttempt(this, applicationAttemptId, user, queue, new ActiveUsersManager(this.getRootQueueMetrics()), this.rmContext);
        if (transferStateFromPreviousAttempt) {
            attempt.transferStateFromPreviousAttempt(application.getCurrentAppAttempt());
        }
        application.setCurrentAppAttempt(attempt);
        final boolean runnable = this.maxRunningEnforcer.canAppBeRunnable(queue, user);
        queue.addApp(attempt, runnable);
        if (runnable) {
            this.maxRunningEnforcer.trackRunnableApp(attempt);
        }
        else {
            this.maxRunningEnforcer.trackNonRunnableApp(attempt);
        }
        queue.getMetrics().submitAppAttempt(user);
        FairScheduler.LOG.info("Added Application Attempt " + applicationAttemptId + " to scheduler from user: " + user);
        if (isAttemptRecovering) {
            if (FairScheduler.LOG.isDebugEnabled()) {
                FairScheduler.LOG.debug(applicationAttemptId + " is recovering. Skipping notifying ATTEMPT_ADDED");
            }
        }
        else {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptEvent(applicationAttemptId, RMAppAttemptEventType.ATTEMPT_ADDED));
        }
    }
    
    @VisibleForTesting
    FSLeafQueue assignToQueue(final RMApp rmApp, String queueName, final String user) {
        FSLeafQueue queue = null;
        String appRejectMsg = null;
        try {
            final QueuePlacementPolicy placementPolicy = this.allocConf.getPlacementPolicy();
            queueName = placementPolicy.assignAppToQueue(queueName, user);
            if (queueName == null) {
                appRejectMsg = "Application rejected by queue placement policy";
            }
            else {
                queue = this.queueMgr.getLeafQueue(queueName, true);
                if (queue == null) {
                    appRejectMsg = queueName + " is not a leaf queue";
                }
            }
        }
        catch (IOException ioe) {
            appRejectMsg = "Error assigning app to queue " + queueName;
        }
        if (appRejectMsg != null && rmApp != null) {
            FairScheduler.LOG.error(appRejectMsg);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(rmApp.getApplicationId(), appRejectMsg));
            return null;
        }
        if (rmApp != null) {
            rmApp.setQueue(queue.getName());
        }
        else {
            FairScheduler.LOG.error("Couldn't find RM app to set queue name on");
        }
        return queue;
    }
    
    private synchronized void removeApplication(final ApplicationId applicationId, final RMAppState finalState) {
        final SchedulerApplication<FSAppAttempt> application = (SchedulerApplication<FSAppAttempt>)this.applications.get(applicationId);
        if (application == null) {
            FairScheduler.LOG.warn("Couldn't find application " + applicationId);
            return;
        }
        application.stop(finalState);
        this.applications.remove(applicationId);
    }
    
    private synchronized void removeApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final RMAppAttemptState rmAppAttemptFinalState, final boolean keepContainers) {
        FairScheduler.LOG.info("Application " + applicationAttemptId + " is done." + " finalState=" + rmAppAttemptFinalState);
        final SchedulerApplication<FSAppAttempt> application = (SchedulerApplication<FSAppAttempt>)this.applications.get(applicationAttemptId.getApplicationId());
        final FSAppAttempt attempt = this.getSchedulerApp(applicationAttemptId);
        if (attempt == null || application == null) {
            FairScheduler.LOG.info("Unknown application " + applicationAttemptId + " has completed!");
            return;
        }
        for (final RMContainer rmContainer : attempt.getLiveContainers()) {
            if (keepContainers && rmContainer.getState().equals(RMContainerState.RUNNING)) {
                FairScheduler.LOG.info("Skip killing " + rmContainer.getContainerId());
            }
            else {
                this.completedContainer(rmContainer, SchedulerUtils.createAbnormalContainerStatus(rmContainer.getContainerId(), "Container of a completed application"), RMContainerEventType.KILL);
            }
        }
        for (final RMContainer rmContainer : attempt.getReservedContainers()) {
            this.completedContainer(rmContainer, SchedulerUtils.createAbnormalContainerStatus(rmContainer.getContainerId(), "Application Complete"), RMContainerEventType.KILL);
        }
        attempt.stop(rmAppAttemptFinalState);
        final FSLeafQueue queue = this.queueMgr.getLeafQueue(attempt.getQueue().getQueueName(), false);
        final boolean wasRunnable = queue.removeApp(attempt);
        if (wasRunnable) {
            this.maxRunningEnforcer.untrackRunnableApp(attempt);
            this.maxRunningEnforcer.updateRunnabilityOnAppRemoval(attempt, attempt.getQueue());
        }
        else {
            this.maxRunningEnforcer.untrackNonRunnableApp(attempt);
        }
    }
    
    @Override
    protected synchronized void completedContainer(final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event) {
        if (rmContainer == null) {
            FairScheduler.LOG.info("Null container completed...");
            return;
        }
        final Container container = rmContainer.getContainer();
        final FSAppAttempt application = ((AbstractYarnScheduler<FSAppAttempt, N>)this).getCurrentAttemptForContainer(container.getId());
        final ApplicationId appId = container.getId().getApplicationAttemptId().getApplicationId();
        if (application == null) {
            FairScheduler.LOG.info("Container " + container + " of" + " unknown application attempt " + appId + " completed with event " + event);
            return;
        }
        final FSSchedulerNode node = this.getFSSchedulerNode(container.getNodeId());
        if (rmContainer.getState() == RMContainerState.RESERVED) {
            application.unreserve(rmContainer.getReservedPriority(), node);
        }
        else {
            application.containerCompleted(rmContainer, containerStatus, event);
            node.releaseContainer(container);
            this.updateRootQueueMetrics();
        }
        FairScheduler.LOG.info("Application attempt " + application.getApplicationAttemptId() + " released container " + container.getId() + " on node: " + node + " with event: " + event);
    }
    
    private synchronized void addNode(final RMNode node) {
        this.nodes.put(node.getNodeID(), (N)new FSSchedulerNode(node, this.usePortForNodeName));
        Resources.addTo(this.clusterResource, node.getTotalCapability());
        this.updateRootQueueMetrics();
        this.queueMgr.getRootQueue().setSteadyFairShare(this.clusterResource);
        this.queueMgr.getRootQueue().recomputeSteadyShares();
        FairScheduler.LOG.info("Added node " + node.getNodeAddress() + " cluster capacity: " + this.clusterResource);
    }
    
    private synchronized void removeNode(final RMNode rmNode) {
        final FSSchedulerNode node = this.getFSSchedulerNode(rmNode.getNodeID());
        if (node == null) {
            return;
        }
        Resources.subtractFrom(this.clusterResource, rmNode.getTotalCapability());
        this.updateRootQueueMetrics();
        final List<RMContainer> runningContainers = node.getRunningContainers();
        for (final RMContainer container : runningContainers) {
            this.completedContainer(container, SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container released on a *lost* node"), RMContainerEventType.KILL);
        }
        final RMContainer reservedContainer = node.getReservedContainer();
        if (reservedContainer != null) {
            this.completedContainer(reservedContainer, SchedulerUtils.createAbnormalContainerStatus(reservedContainer.getContainerId(), "Container released on a *lost* node"), RMContainerEventType.KILL);
        }
        this.nodes.remove(rmNode.getNodeID());
        this.queueMgr.getRootQueue().setSteadyFairShare(this.clusterResource);
        this.queueMgr.getRootQueue().recomputeSteadyShares();
        FairScheduler.LOG.info("Removed node " + rmNode.getNodeAddress() + " cluster capacity: " + this.clusterResource);
    }
    
    @Override
    public Allocation allocate(final ApplicationAttemptId appAttemptId, final List<ResourceRequest> ask, final List<ContainerId> release, final List<String> blacklistAdditions, final List<String> blacklistRemovals) {
        final FSAppAttempt application = this.getSchedulerApp(appAttemptId);
        if (application == null) {
            FairScheduler.LOG.info("Calling allocate on removed or non existant application " + appAttemptId);
            return FairScheduler.EMPTY_ALLOCATION;
        }
        SchedulerUtils.normalizeRequests(ask, new DominantResourceCalculator(), this.clusterResource, this.minimumAllocation, this.maximumAllocation, this.incrAllocation);
        if (!application.getUnmanagedAM() && ask.size() == 1 && application.getLiveContainers().isEmpty()) {
            application.setAMResource(ask.get(0).getCapability());
        }
        this.releaseContainers(release, application);
        synchronized (application) {
            if (!ask.isEmpty()) {
                if (FairScheduler.LOG.isDebugEnabled()) {
                    FairScheduler.LOG.debug("allocate: pre-update applicationAttemptId=" + appAttemptId + " application=" + application.getApplicationId());
                }
                application.showRequests();
                application.updateResourceRequests(ask);
                application.showRequests();
            }
            if (FairScheduler.LOG.isDebugEnabled()) {
                FairScheduler.LOG.debug("allocate: post-update applicationAttemptId=" + appAttemptId + " #ask=" + ask.size() + " reservation= " + application.getCurrentReservation());
                FairScheduler.LOG.debug("Preempting " + application.getPreemptionContainers().size() + " container(s)");
            }
            final Set<ContainerId> preemptionContainerIds = new HashSet<ContainerId>();
            for (final RMContainer container : application.getPreemptionContainers()) {
                preemptionContainerIds.add(container.getContainerId());
            }
            application.updateBlacklist(blacklistAdditions, blacklistRemovals);
            final SchedulerApplicationAttempt.ContainersAndNMTokensAllocation allocation = application.pullNewlyAllocatedContainersAndNMTokens();
            return new Allocation(allocation.getContainerList(), application.getHeadroom(), preemptionContainerIds, null, null, allocation.getNMTokenList());
        }
    }
    
    private synchronized void nodeUpdate(final RMNode nm) {
        final long start = this.getClock().getTime();
        if (FairScheduler.LOG.isDebugEnabled()) {
            FairScheduler.LOG.debug("nodeUpdate: " + nm + " cluster capacity: " + this.clusterResource);
        }
        this.eventLog.log("HEARTBEAT", nm.getHostName());
        final FSSchedulerNode node = this.getFSSchedulerNode(nm.getNodeID());
        final List<UpdatedContainerInfo> containerInfoList = nm.pullContainerUpdates();
        final List<ContainerStatus> newlyLaunchedContainers = new ArrayList<ContainerStatus>();
        final List<ContainerStatus> completedContainers = new ArrayList<ContainerStatus>();
        for (final UpdatedContainerInfo containerInfo : containerInfoList) {
            newlyLaunchedContainers.addAll(containerInfo.getNewlyLaunchedContainers());
            completedContainers.addAll(containerInfo.getCompletedContainers());
        }
        for (final ContainerStatus launchedContainer : newlyLaunchedContainers) {
            this.containerLaunchedOnNode(launchedContainer.getContainerId(), node);
        }
        for (final ContainerStatus completedContainer : completedContainers) {
            final ContainerId containerId = completedContainer.getContainerId();
            FairScheduler.LOG.debug("Container FINISHED: " + containerId);
            this.completedContainer(this.getRMContainer(containerId), completedContainer, RMContainerEventType.FINISHED);
        }
        if (this.continuousSchedulingEnabled) {
            if (!completedContainers.isEmpty()) {
                this.attemptScheduling(node);
            }
        }
        else {
            this.attemptScheduling(node);
        }
        final long duration = this.getClock().getTime() - start;
        this.fsOpDurations.addNodeUpdateDuration(duration);
    }
    
    void continuousSchedulingAttempt() throws InterruptedException {
        final long start = this.getClock().getTime();
        final List<NodeId> nodeIdList = new ArrayList<NodeId>(this.nodes.keySet());
        synchronized (this) {
            Collections.sort(nodeIdList, this.nodeAvailableResourceComparator);
        }
        for (final NodeId nodeId : nodeIdList) {
            final FSSchedulerNode node = this.getFSSchedulerNode(nodeId);
            try {
                if (node == null || !Resources.fitsIn(this.minimumAllocation, node.getAvailableResource())) {
                    continue;
                }
                this.attemptScheduling(node);
            }
            catch (Throwable ex) {
                FairScheduler.LOG.error("Error while attempting scheduling for node " + node + ": " + ex.toString(), ex);
            }
        }
        final long duration = this.getClock().getTime() - start;
        this.fsOpDurations.addContinuousSchedulingRunDuration(duration);
    }
    
    private synchronized void attemptScheduling(final FSSchedulerNode node) {
        if (this.rmContext.isWorkPreservingRecoveryEnabled() && !this.rmContext.isSchedulerReadyForAllocatingContainers()) {
            return;
        }
        FSAppAttempt reservedAppSchedulable = node.getReservedAppSchedulable();
        if (reservedAppSchedulable != null) {
            final Priority reservedPriority = node.getReservedContainer().getReservedPriority();
            if (!reservedAppSchedulable.hasContainerForNode(reservedPriority, node)) {
                FairScheduler.LOG.info("Releasing reservation that cannot be satisfied for application " + reservedAppSchedulable.getApplicationAttemptId() + " on node " + node);
                reservedAppSchedulable.unreserve(reservedPriority, node);
                reservedAppSchedulable = null;
            }
            else {
                if (FairScheduler.LOG.isDebugEnabled()) {
                    FairScheduler.LOG.debug("Trying to fulfill reservation for application " + reservedAppSchedulable.getApplicationAttemptId() + " on node: " + node);
                }
                node.getReservedAppSchedulable().assignReservedContainer(node);
            }
        }
        if (reservedAppSchedulable == null) {
            int assignedContainers = 0;
            while (node.getReservedContainer() == null) {
                boolean assignedContainer = false;
                if (!this.queueMgr.getRootQueue().assignContainer(node).equals(Resources.none())) {
                    ++assignedContainers;
                    assignedContainer = true;
                }
                if (!assignedContainer) {
                    break;
                }
                if (!this.assignMultiple) {
                    break;
                }
                if (assignedContainers >= this.maxAssign && this.maxAssign > 0) {
                    break;
                }
            }
        }
        this.updateRootQueueMetrics();
    }
    
    public FSAppAttempt getSchedulerApp(final ApplicationAttemptId appAttemptId) {
        return super.getApplicationAttempt(appAttemptId);
    }
    
    public static ResourceCalculator getResourceCalculator() {
        return FairScheduler.RESOURCE_CALCULATOR;
    }
    
    private void updateRootQueueMetrics() {
        this.rootMetrics.setAvailableResourcesToQueue(Resources.subtract(this.clusterResource, this.rootMetrics.getAllocatedResources()));
    }
    
    private boolean shouldAttemptPreemption() {
        return this.preemptionEnabled && this.preemptionUtilizationThreshold < Math.max(this.rootMetrics.getAllocatedMB() / (float)this.clusterResource.getMemory(), this.rootMetrics.getAllocatedVirtualCores() / (float)this.clusterResource.getVirtualCores());
    }
    
    @Override
    public QueueMetrics getRootQueueMetrics() {
        return this.rootMetrics;
    }
    
    @Override
    public void handle(final SchedulerEvent event) {
        switch (event.getType()) {
            case NODE_ADDED: {
                if (!(event instanceof NodeAddedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final NodeAddedSchedulerEvent nodeAddedEvent = (NodeAddedSchedulerEvent)event;
                this.addNode(nodeAddedEvent.getAddedRMNode());
                this.recoverContainersOnNode(nodeAddedEvent.getContainerReports(), nodeAddedEvent.getAddedRMNode());
                break;
            }
            case NODE_REMOVED: {
                if (!(event instanceof NodeRemovedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final NodeRemovedSchedulerEvent nodeRemovedEvent = (NodeRemovedSchedulerEvent)event;
                this.removeNode(nodeRemovedEvent.getRemovedRMNode());
                break;
            }
            case NODE_UPDATE: {
                if (!(event instanceof NodeUpdateSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final NodeUpdateSchedulerEvent nodeUpdatedEvent = (NodeUpdateSchedulerEvent)event;
                this.nodeUpdate(nodeUpdatedEvent.getRMNode());
                break;
            }
            case APP_ADDED: {
                if (!(event instanceof AppAddedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final AppAddedSchedulerEvent appAddedEvent = (AppAddedSchedulerEvent)event;
                this.addApplication(appAddedEvent.getApplicationId(), appAddedEvent.getQueue(), appAddedEvent.getUser(), appAddedEvent.getIsAppRecovering());
                break;
            }
            case APP_REMOVED: {
                if (!(event instanceof AppRemovedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final AppRemovedSchedulerEvent appRemovedEvent = (AppRemovedSchedulerEvent)event;
                this.removeApplication(appRemovedEvent.getApplicationID(), appRemovedEvent.getFinalState());
                break;
            }
            case NODE_RESOURCE_UPDATE: {
                if (!(event instanceof NodeResourceUpdateSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final NodeResourceUpdateSchedulerEvent nodeResourceUpdatedEvent = (NodeResourceUpdateSchedulerEvent)event;
                this.updateNodeResource(nodeResourceUpdatedEvent.getRMNode(), nodeResourceUpdatedEvent.getResourceOption());
                break;
            }
            case APP_ATTEMPT_ADDED: {
                if (!(event instanceof AppAttemptAddedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final AppAttemptAddedSchedulerEvent appAttemptAddedEvent = (AppAttemptAddedSchedulerEvent)event;
                this.addApplicationAttempt(appAttemptAddedEvent.getApplicationAttemptId(), appAttemptAddedEvent.getTransferStateFromPreviousAttempt(), appAttemptAddedEvent.getIsAttemptRecovering());
                break;
            }
            case APP_ATTEMPT_REMOVED: {
                if (!(event instanceof AppAttemptRemovedSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final AppAttemptRemovedSchedulerEvent appAttemptRemovedEvent = (AppAttemptRemovedSchedulerEvent)event;
                this.removeApplicationAttempt(appAttemptRemovedEvent.getApplicationAttemptID(), appAttemptRemovedEvent.getFinalAttemptState(), appAttemptRemovedEvent.getKeepContainersAcrossAppAttempts());
                break;
            }
            case CONTAINER_EXPIRED: {
                if (!(event instanceof ContainerExpiredSchedulerEvent)) {
                    throw new RuntimeException("Unexpected event type: " + event);
                }
                final ContainerExpiredSchedulerEvent containerExpiredEvent = (ContainerExpiredSchedulerEvent)event;
                final ContainerId containerId = containerExpiredEvent.getContainerId();
                this.completedContainer(this.getRMContainer(containerId), SchedulerUtils.createAbnormalContainerStatus(containerId, "Container expired since it was unused"), RMContainerEventType.EXPIRE);
                break;
            }
            default: {
                FairScheduler.LOG.error("Unknown event arrived at FairScheduler: " + event.toString());
                break;
            }
        }
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) throws Exception {
    }
    
    @Override
    public synchronized void setRMContext(final RMContext rmContext) {
        this.rmContext = rmContext;
    }
    
    private void initScheduler(final Configuration conf) throws IOException {
        synchronized (this) {
            this.validateConf(this.conf = new FairSchedulerConfiguration(conf));
            this.minimumAllocation = this.conf.getMinimumAllocation();
            this.maximumAllocation = this.conf.getMaximumAllocation();
            this.incrAllocation = this.conf.getIncrementAllocation();
            this.continuousSchedulingEnabled = this.conf.isContinuousSchedulingEnabled();
            this.continuousSchedulingSleepMs = this.conf.getContinuousSchedulingSleepMs();
            this.nodeLocalityThreshold = this.conf.getLocalityThresholdNode();
            this.rackLocalityThreshold = this.conf.getLocalityThresholdRack();
            this.nodeLocalityDelayMs = this.conf.getLocalityDelayNodeMs();
            this.rackLocalityDelayMs = this.conf.getLocalityDelayRackMs();
            this.preemptionEnabled = this.conf.getPreemptionEnabled();
            this.preemptionUtilizationThreshold = this.conf.getPreemptionUtilizationThreshold();
            this.assignMultiple = this.conf.getAssignMultiple();
            this.maxAssign = this.conf.getMaxAssign();
            this.sizeBasedWeight = this.conf.getSizeBasedWeight();
            this.preemptionInterval = this.conf.getPreemptionInterval();
            this.waitTimeBeforeKill = this.conf.getWaitTimeBeforeKill();
            this.usePortForNodeName = this.conf.getUsePortForNodeName();
            this.updateInterval = this.conf.getUpdateInterval();
            if (this.updateInterval < 0L) {
                this.updateInterval = 500L;
                FairScheduler.LOG.warn("yarn.scheduler.fair.update-interval-ms is invalid, so using default value 500 ms instead");
            }
            this.rootMetrics = FSQueueMetrics.forQueue("root", null, true, conf);
            this.fsOpDurations = FSOpDurations.getInstance(true);
            this.applications = new ConcurrentHashMap<ApplicationId, SchedulerApplication<T>>();
            (this.eventLog = new FairSchedulerEventLog()).init(this.conf);
            this.allocConf = new AllocationConfiguration(conf);
            try {
                this.queueMgr.initialize(conf);
            }
            catch (Exception e) {
                throw new IOException("Failed to start FairScheduler", e);
            }
            (this.updateThread = new UpdateThread()).setName("FairSchedulerUpdateThread");
            this.updateThread.setDaemon(true);
            if (this.continuousSchedulingEnabled) {
                (this.schedulingThread = new ContinuousSchedulingThread()).setName("FairSchedulerContinuousScheduling");
                this.schedulingThread.setDaemon(true);
            }
        }
        this.allocsLoader.init(conf);
        this.allocsLoader.setReloadListener(new AllocationReloadListener());
        try {
            this.allocsLoader.reloadAllocations();
        }
        catch (Exception e2) {
            throw new IOException("Failed to initialize FairScheduler", e2);
        }
    }
    
    private synchronized void startSchedulerThreads() {
        Preconditions.checkNotNull(this.updateThread, (Object)"updateThread is null");
        Preconditions.checkNotNull(this.allocsLoader, (Object)"allocsLoader is null");
        this.updateThread.start();
        if (this.continuousSchedulingEnabled) {
            Preconditions.checkNotNull(this.schedulingThread, (Object)"schedulingThread is null");
            this.schedulingThread.start();
        }
        this.allocsLoader.start();
    }
    
    @Override
    public void serviceInit(final Configuration conf) throws Exception {
        this.initScheduler(conf);
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        this.startSchedulerThreads();
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        synchronized (this) {
            if (this.updateThread != null) {
                this.updateThread.interrupt();
                this.updateThread.join(1000L);
            }
            if (this.continuousSchedulingEnabled && this.schedulingThread != null) {
                this.schedulingThread.interrupt();
                this.schedulingThread.join(1000L);
            }
            if (this.allocsLoader != null) {
                this.allocsLoader.stop();
            }
        }
        super.serviceStop();
    }
    
    @Override
    public void reinitialize(final Configuration conf, final RMContext rmContext) throws IOException {
        try {
            this.allocsLoader.reloadAllocations();
        }
        catch (Exception e) {
            FairScheduler.LOG.error("Failed to reload allocations file", e);
        }
    }
    
    @Override
    public QueueInfo getQueueInfo(final String queueName, final boolean includeChildQueues, final boolean recursive) throws IOException {
        if (!this.queueMgr.exists(queueName)) {
            throw new IOException("queue " + queueName + " does not exist");
        }
        return this.queueMgr.getQueue(queueName).getQueueInfo(includeChildQueues, recursive);
    }
    
    @Override
    public List<QueueUserACLInfo> getQueueUserAclInfo() {
        UserGroupInformation user;
        try {
            user = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ioe) {
            return new ArrayList<QueueUserACLInfo>();
        }
        return this.queueMgr.getRootQueue().getQueueUserAclInfo(user);
    }
    
    @Override
    public int getNumClusterNodes() {
        return this.nodes.size();
    }
    
    @Override
    public synchronized boolean checkAccess(final UserGroupInformation callerUGI, final QueueACL acl, final String queueName) {
        final FSQueue queue = this.getQueueManager().getQueue(queueName);
        if (queue == null) {
            if (FairScheduler.LOG.isDebugEnabled()) {
                FairScheduler.LOG.debug("ACL not found for queue access-type " + acl + " for queue " + queueName);
            }
            return false;
        }
        return queue.hasAccess(acl, callerUGI);
    }
    
    public AllocationConfiguration getAllocationConfiguration() {
        return this.allocConf;
    }
    
    @Override
    public List<ApplicationAttemptId> getAppsInQueue(final String queueName) {
        final FSQueue queue = this.queueMgr.getQueue(queueName);
        if (queue == null) {
            return null;
        }
        final List<ApplicationAttemptId> apps = new ArrayList<ApplicationAttemptId>();
        queue.collectSchedulerApplications(apps);
        return apps;
    }
    
    @Override
    public synchronized String moveApplication(final ApplicationId appId, final String queueName) throws YarnException {
        final SchedulerApplication<FSAppAttempt> app = (SchedulerApplication<FSAppAttempt>)this.applications.get(appId);
        if (app == null) {
            throw new YarnException("App to be moved " + appId + " not found.");
        }
        final FSAppAttempt attempt = app.getCurrentAppAttempt();
        synchronized (attempt) {
            final FSLeafQueue oldQueue = (FSLeafQueue)app.getQueue();
            final FSLeafQueue targetQueue = this.queueMgr.getLeafQueue(queueName, false);
            if (targetQueue == null) {
                throw new YarnException("Target queue " + queueName + " not found or is not a leaf queue.");
            }
            if (targetQueue == oldQueue) {
                return oldQueue.getQueueName();
            }
            if (oldQueue.getRunnableAppSchedulables().contains(attempt)) {
                this.verifyMoveDoesNotViolateConstraints(attempt, oldQueue, targetQueue);
            }
            this.executeMove(app, attempt, oldQueue, targetQueue);
            return targetQueue.getQueueName();
        }
    }
    
    private void verifyMoveDoesNotViolateConstraints(final FSAppAttempt app, final FSLeafQueue oldQueue, final FSLeafQueue targetQueue) throws YarnException {
        final String queueName = targetQueue.getQueueName();
        final ApplicationAttemptId appAttId = app.getApplicationAttemptId();
        final FSQueue lowestCommonAncestor = this.findLowestCommonAncestorQueue(oldQueue, targetQueue);
        final Resource consumption = app.getCurrentConsumption();
        for (FSQueue cur = targetQueue; cur != lowestCommonAncestor; cur = cur.getParent()) {
            if (cur.getNumRunnableApps() == this.allocConf.getQueueMaxApps(cur.getQueueName())) {
                throw new YarnException("Moving app attempt " + appAttId + " to queue " + queueName + " would violate queue maxRunningApps constraints on" + " queue " + cur.getQueueName());
            }
            if (!Resources.fitsIn(Resources.add(cur.getResourceUsage(), consumption), cur.getMaxShare())) {
                throw new YarnException("Moving app attempt " + appAttId + " to queue " + queueName + " would violate queue maxShare constraints on" + " queue " + cur.getQueueName());
            }
        }
    }
    
    private void executeMove(final SchedulerApplication<FSAppAttempt> app, final FSAppAttempt attempt, final FSLeafQueue oldQueue, final FSLeafQueue newQueue) {
        final boolean wasRunnable = oldQueue.removeApp(attempt);
        final boolean nowRunnable = this.maxRunningEnforcer.canAppBeRunnable(newQueue, attempt.getUser());
        if (wasRunnable && !nowRunnable) {
            throw new IllegalStateException("Should have already verified that app " + attempt.getApplicationId() + " would be runnable in new queue");
        }
        if (wasRunnable) {
            this.maxRunningEnforcer.untrackRunnableApp(attempt);
        }
        else if (nowRunnable) {
            this.maxRunningEnforcer.untrackNonRunnableApp(attempt);
        }
        attempt.move(newQueue);
        app.setQueue(newQueue);
        newQueue.addApp(attempt, nowRunnable);
        if (nowRunnable) {
            this.maxRunningEnforcer.trackRunnableApp(attempt);
        }
        if (wasRunnable) {
            this.maxRunningEnforcer.updateRunnabilityOnAppRemoval(attempt, oldQueue);
        }
    }
    
    @VisibleForTesting
    FSQueue findLowestCommonAncestorQueue(final FSQueue queue1, final FSQueue queue2) {
        final String name1 = queue1.getName();
        final String name2 = queue2.getName();
        int lastPeriodIndex = -1;
        for (int i = 0; i < Math.max(name1.length(), name2.length()); ++i) {
            if (name1.length() <= i || name2.length() <= i || name1.charAt(i) != name2.charAt(i)) {
                return this.queueMgr.getQueue(name1.substring(0, lastPeriodIndex));
            }
            if (name1.charAt(i) == '.') {
                lastPeriodIndex = i;
            }
        }
        return queue1;
    }
    
    @Override
    public synchronized void updateNodeResource(final RMNode nm, final ResourceOption resourceOption) {
        super.updateNodeResource(nm, resourceOption);
        this.updateRootQueueMetrics();
        this.queueMgr.getRootQueue().setSteadyFairShare(this.clusterResource);
        this.queueMgr.getRootQueue().recomputeSteadyShares();
    }
    
    @Override
    public EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulingResourceTypes() {
        return EnumSet.of(YarnServiceProtos.SchedulerResourceTypes.MEMORY, YarnServiceProtos.SchedulerResourceTypes.CPU);
    }
    
    static {
        LOG = LogFactory.getLog(FairScheduler.class);
        RESOURCE_CALCULATOR = new DefaultResourceCalculator();
        CONTAINER_RESERVED = Resources.createResource(-1);
    }
    
    private class UpdateThread extends Thread
    {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(FairScheduler.this.updateInterval);
                    final long start = FairScheduler.this.getClock().getTime();
                    FairScheduler.this.update();
                    FairScheduler.this.preemptTasksIfNecessary();
                    final long duration = FairScheduler.this.getClock().getTime() - start;
                    FairScheduler.this.fsOpDurations.addUpdateThreadRunDuration(duration);
                    continue;
                }
                catch (InterruptedException ie) {
                    FairScheduler.LOG.warn("Update thread interrupted. Exiting.");
                    return;
                }
                catch (Exception e) {
                    FairScheduler.LOG.error("Exception in fair scheduler UpdateThread", e);
                    continue;
                }
                break;
            }
        }
    }
    
    private class ContinuousSchedulingThread extends Thread
    {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    FairScheduler.this.continuousSchedulingAttempt();
                    Thread.sleep(FairScheduler.this.getContinuousSchedulingSleepMs());
                    continue;
                }
                catch (InterruptedException e) {
                    FairScheduler.LOG.warn("Continuous scheduling thread interrupted. Exiting.", e);
                    return;
                }
                break;
            }
        }
    }
    
    private class NodeAvailableResourceComparator implements Comparator<NodeId>
    {
        @Override
        public int compare(final NodeId n1, final NodeId n2) {
            if (!FairScheduler.this.nodes.containsKey(n1)) {
                return 1;
            }
            if (!FairScheduler.this.nodes.containsKey(n2)) {
                return -1;
            }
            return FairScheduler.RESOURCE_CALCULATOR.compare(FairScheduler.this.clusterResource, FairScheduler.this.nodes.get(n2).getAvailableResource(), FairScheduler.this.nodes.get(n1).getAvailableResource());
        }
    }
    
    private class AllocationReloadListener implements AllocationFileLoaderService.Listener
    {
        @Override
        public void onReload(final AllocationConfiguration queueInfo) {
            synchronized (FairScheduler.this) {
                FairScheduler.this.allocConf = queueInfo;
                FairScheduler.this.allocConf.getDefaultSchedulingPolicy().initialize(FairScheduler.this.clusterResource);
                FairScheduler.this.queueMgr.updateAllocationConfiguration(FairScheduler.this.allocConf);
            }
        }
    }
}
