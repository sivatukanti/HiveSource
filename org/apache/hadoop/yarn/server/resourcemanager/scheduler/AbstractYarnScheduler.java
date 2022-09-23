// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import java.util.EnumSet;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMoveEvent;
import com.google.common.util.concurrent.SettableFuture;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.RMAuditLogger;
import java.util.TimerTask;
import java.util.Timer;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.Token;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerRecoverEvent;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeCleanContainerEvent;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public abstract class AbstractYarnScheduler<T extends SchedulerApplicationAttempt, N extends SchedulerNode> extends AbstractService implements ResourceScheduler
{
    private static final Log LOG;
    protected Map<NodeId, N> nodes;
    protected Resource clusterResource;
    protected Resource minimumAllocation;
    protected Resource maximumAllocation;
    protected RMContext rmContext;
    protected Map<ApplicationId, SchedulerApplication<T>> applications;
    protected int nmExpireInterval;
    protected static final List<Container> EMPTY_CONTAINER_LIST;
    protected static final Allocation EMPTY_ALLOCATION;
    
    public AbstractYarnScheduler(final String name) {
        super(name);
        this.nodes = new ConcurrentHashMap<NodeId, N>();
        this.clusterResource = Resource.newInstance(0, 0);
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        this.nmExpireInterval = conf.getInt("yarn.nm.liveness-monitor.expiry-interval-ms", 600000);
        this.createReleaseCache();
        super.serviceInit(conf);
    }
    
    public synchronized List<Container> getTransferredContainers(final ApplicationAttemptId currentAttempt) {
        final ApplicationId appId = currentAttempt.getApplicationId();
        final SchedulerApplication<T> app = this.applications.get(appId);
        final List<Container> containerList = new ArrayList<Container>();
        final RMApp appImpl = this.rmContext.getRMApps().get(appId);
        if (appImpl.getApplicationSubmissionContext().getUnmanagedAM()) {
            return containerList;
        }
        final Collection<RMContainer> liveContainers = app.getCurrentAppAttempt().getLiveContainers();
        final ContainerId amContainerId = this.rmContext.getRMApps().get(appId).getCurrentAppAttempt().getMasterContainer().getId();
        for (final RMContainer rmContainer : liveContainers) {
            if (!rmContainer.getContainerId().equals(amContainerId)) {
                containerList.add(rmContainer.getContainer());
            }
        }
        return containerList;
    }
    
    public Map<ApplicationId, SchedulerApplication<T>> getSchedulerApplications() {
        return this.applications;
    }
    
    @Override
    public Resource getClusterResource() {
        return this.clusterResource;
    }
    
    @Override
    public Resource getMinimumResourceCapability() {
        return this.minimumAllocation;
    }
    
    @Override
    public Resource getMaximumResourceCapability() {
        return this.maximumAllocation;
    }
    
    protected void containerLaunchedOnNode(final ContainerId containerId, final SchedulerNode node) {
        final SchedulerApplicationAttempt application = this.getCurrentAttemptForContainer(containerId);
        if (application == null) {
            AbstractYarnScheduler.LOG.info("Unknown application " + containerId.getApplicationAttemptId().getApplicationId() + " launched container " + containerId + " on node: " + node);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeCleanContainerEvent(node.getNodeID(), containerId));
            return;
        }
        application.containerLaunchedOnNode(containerId, node.getNodeID());
    }
    
    public T getApplicationAttempt(final ApplicationAttemptId applicationAttemptId) {
        final SchedulerApplication<T> app = this.applications.get(applicationAttemptId.getApplicationId());
        return (T)((app == null) ? null : app.getCurrentAppAttempt());
    }
    
    @Override
    public SchedulerAppReport getSchedulerAppInfo(final ApplicationAttemptId appAttemptId) {
        final SchedulerApplicationAttempt attempt = this.getApplicationAttempt(appAttemptId);
        if (attempt == null) {
            if (AbstractYarnScheduler.LOG.isDebugEnabled()) {
                AbstractYarnScheduler.LOG.debug("Request for appInfo of unknown attempt " + appAttemptId);
            }
            return null;
        }
        return new SchedulerAppReport(attempt);
    }
    
    @Override
    public ApplicationResourceUsageReport getAppResourceUsageReport(final ApplicationAttemptId appAttemptId) {
        final SchedulerApplicationAttempt attempt = this.getApplicationAttempt(appAttemptId);
        if (attempt == null) {
            if (AbstractYarnScheduler.LOG.isDebugEnabled()) {
                AbstractYarnScheduler.LOG.debug("Request for appInfo of unknown attempt " + appAttemptId);
            }
            return null;
        }
        return attempt.getResourceUsageReport();
    }
    
    public T getCurrentAttemptForContainer(final ContainerId containerId) {
        return this.getApplicationAttempt(containerId.getApplicationAttemptId());
    }
    
    @Override
    public RMContainer getRMContainer(final ContainerId containerId) {
        final SchedulerApplicationAttempt attempt = this.getCurrentAttemptForContainer(containerId);
        return (attempt == null) ? null : attempt.getRMContainer(containerId);
    }
    
    @Override
    public SchedulerNodeReport getNodeReport(final NodeId nodeId) {
        final N node = this.nodes.get(nodeId);
        return (node == null) ? null : new SchedulerNodeReport(node);
    }
    
    @Override
    public String moveApplication(final ApplicationId appId, final String newQueue) throws YarnException {
        throw new YarnException(this.getClass().getSimpleName() + " does not support moving apps between queues");
    }
    
    @Override
    public void removeQueue(final String queueName) throws YarnException {
        throw new YarnException(this.getClass().getSimpleName() + " does not support removing queues");
    }
    
    @Override
    public void addQueue(final Queue newQueue) throws YarnException {
        throw new YarnException(this.getClass().getSimpleName() + " does not support this operation");
    }
    
    @Override
    public void setEntitlement(final String queue, final QueueEntitlement entitlement) throws YarnException {
        throw new YarnException(this.getClass().getSimpleName() + " does not support this operation");
    }
    
    private void killOrphanContainerOnNode(final RMNode node, final NMContainerStatus container) {
        if (!container.getContainerState().equals(ContainerState.COMPLETE)) {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeCleanContainerEvent(node.getNodeID(), container.getContainerId()));
        }
    }
    
    public synchronized void recoverContainersOnNode(final List<NMContainerStatus> containerReports, final RMNode nm) {
        if (!this.rmContext.isWorkPreservingRecoveryEnabled() || containerReports == null || (containerReports != null && containerReports.isEmpty())) {
            return;
        }
        for (final NMContainerStatus container : containerReports) {
            final ApplicationId appId = container.getContainerId().getApplicationAttemptId().getApplicationId();
            final RMApp rmApp = this.rmContext.getRMApps().get(appId);
            if (rmApp == null) {
                AbstractYarnScheduler.LOG.error("Skip recovering container " + container + " for unknown application.");
                this.killOrphanContainerOnNode(nm, container);
            }
            else if (rmApp.getApplicationSubmissionContext().getUnmanagedAM()) {
                AbstractYarnScheduler.LOG.info("Skip recovering container " + container + " for unmanaged AM." + rmApp.getApplicationId());
                this.killOrphanContainerOnNode(nm, container);
            }
            else {
                final SchedulerApplication<T> schedulerApp = this.applications.get(appId);
                if (schedulerApp == null) {
                    AbstractYarnScheduler.LOG.info("Skip recovering container  " + container + " for unknown SchedulerApplication. Application current state is " + rmApp.getState());
                    this.killOrphanContainerOnNode(nm, container);
                }
                else {
                    AbstractYarnScheduler.LOG.info("Recovering container " + container);
                    final SchedulerApplicationAttempt schedulerAttempt = schedulerApp.getCurrentAppAttempt();
                    if (!rmApp.getApplicationSubmissionContext().getKeepContainersAcrossApplicationAttempts() && (schedulerAttempt.isStopped() || !schedulerAttempt.getApplicationAttemptId().equals(container.getContainerId().getApplicationAttemptId()))) {
                        AbstractYarnScheduler.LOG.info("Skip recovering container " + container + " for already stopped attempt.");
                        this.killOrphanContainerOnNode(nm, container);
                    }
                    else {
                        final RMContainer rmContainer = this.recoverAndCreateContainer(container, nm);
                        ((EventHandler<RMContainerRecoverEvent>)rmContainer).handle(new RMContainerRecoverEvent(container.getContainerId(), container));
                        this.nodes.get(nm.getNodeID()).recoverContainer(rmContainer);
                        final Queue queue = schedulerAttempt.getQueue();
                        queue.recoverContainer(this.clusterResource, schedulerAttempt, rmContainer);
                        schedulerAttempt.recoverContainer(rmContainer);
                        final RMAppAttempt appAttempt = rmApp.getCurrentAppAttempt();
                        if (appAttempt != null) {
                            final Container masterContainer = appAttempt.getMasterContainer();
                            if (masterContainer != null && masterContainer.getId().equals(rmContainer.getContainerId())) {
                                ((RMContainerImpl)rmContainer).setAMContainer(true);
                            }
                        }
                        synchronized (schedulerAttempt) {
                            final Set<ContainerId> releases = schedulerAttempt.getPendingRelease();
                            if (!releases.contains(container.getContainerId())) {
                                continue;
                            }
                            ((EventHandler<RMContainerFinishedEvent>)rmContainer).handle(new RMContainerFinishedEvent(container.getContainerId(), SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container released by application"), RMContainerEventType.RELEASED));
                            releases.remove(container.getContainerId());
                            AbstractYarnScheduler.LOG.info(container.getContainerId() + " is released by application.");
                        }
                    }
                }
            }
        }
    }
    
    private RMContainer recoverAndCreateContainer(final NMContainerStatus status, final RMNode node) {
        final Container container = Container.newInstance(status.getContainerId(), node.getNodeID(), node.getHttpAddress(), status.getAllocatedResource(), status.getPriority(), null);
        final ApplicationAttemptId attemptId = container.getId().getApplicationAttemptId();
        final RMContainer rmContainer = new RMContainerImpl(container, attemptId, node.getNodeID(), this.applications.get(attemptId.getApplicationId()).getUser(), this.rmContext, status.getCreationTime());
        return rmContainer;
    }
    
    protected void recoverResourceRequestForContainer(final RMContainer rmContainer) {
        final List<ResourceRequest> requests = rmContainer.getResourceRequests();
        if (requests == null) {
            return;
        }
        final SchedulerApplicationAttempt schedulerAttempt = this.getCurrentAttemptForContainer(rmContainer.getContainerId());
        if (schedulerAttempt != null) {
            schedulerAttempt.recoverResourceRequests(requests);
        }
    }
    
    protected void createReleaseCache() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (final SchedulerApplication<T> app : AbstractYarnScheduler.this.applications.values()) {
                    final T attempt = app.getCurrentAppAttempt();
                    synchronized (attempt) {
                        for (final ContainerId containerId : attempt.getPendingRelease()) {
                            RMAuditLogger.logFailure(app.getUser(), "AM Released Container", "Unauthorized access or invalid container", "Scheduler", "Trying to release container not owned by app or with invalid id.", attempt.getApplicationId(), containerId);
                        }
                        attempt.getPendingRelease().clear();
                    }
                }
                AbstractYarnScheduler.LOG.info("Release request cache is cleaned up");
            }
        }, this.nmExpireInterval);
    }
    
    protected abstract void completedContainer(final RMContainer p0, final ContainerStatus p1, final RMContainerEventType p2);
    
    protected void releaseContainers(final List<ContainerId> containers, final SchedulerApplicationAttempt attempt) {
        for (final ContainerId containerId : containers) {
            final RMContainer rmContainer = this.getRMContainer(containerId);
            if (rmContainer == null) {
                if (System.currentTimeMillis() - ResourceManager.getClusterTimeStamp() < this.nmExpireInterval) {
                    AbstractYarnScheduler.LOG.info(containerId + " doesn't exist. Add the container" + " to the release request cache as it maybe on recovery.");
                    synchronized (attempt) {
                        attempt.getPendingRelease().add(containerId);
                    }
                }
                else {
                    RMAuditLogger.logFailure(attempt.getUser(), "AM Released Container", "Unauthorized access or invalid container", "Scheduler", "Trying to release container not owned by app or with invalid id.", attempt.getApplicationId(), containerId);
                }
            }
            this.completedContainer(rmContainer, SchedulerUtils.createAbnormalContainerStatus(containerId, "Container released by application"), RMContainerEventType.RELEASED);
        }
    }
    
    public SchedulerNode getSchedulerNode(final NodeId nodeId) {
        return this.nodes.get(nodeId);
    }
    
    @Override
    public synchronized void moveAllApps(final String sourceQueue, final String destQueue) throws YarnException {
        try {
            this.getQueueInfo(destQueue, false, false);
        }
        catch (IOException e) {
            AbstractYarnScheduler.LOG.warn(e);
            throw new YarnException(e);
        }
        final List<ApplicationAttemptId> apps = this.getAppsInQueue(sourceQueue);
        if (apps == null) {
            final String errMsg = "The specified Queue: " + sourceQueue + " doesn't exist";
            AbstractYarnScheduler.LOG.warn(errMsg);
            throw new YarnException(errMsg);
        }
        for (final ApplicationAttemptId app : apps) {
            final SettableFuture<Object> future = SettableFuture.create();
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppMoveEvent(app.getApplicationId(), destQueue, future));
        }
    }
    
    @Override
    public synchronized void killAllAppsInQueue(final String queueName) throws YarnException {
        final List<ApplicationAttemptId> apps = this.getAppsInQueue(queueName);
        if (apps == null) {
            final String errMsg = "The specified Queue: " + queueName + " doesn't exist";
            AbstractYarnScheduler.LOG.warn(errMsg);
            throw new YarnException(errMsg);
        }
        for (final ApplicationAttemptId app : apps) {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(app.getApplicationId(), RMAppEventType.KILL));
        }
    }
    
    public synchronized void updateNodeResource(final RMNode nm, final ResourceOption resourceOption) {
        final SchedulerNode node = this.getSchedulerNode(nm.getNodeID());
        final Resource newResource = resourceOption.getResource();
        final Resource oldResource = node.getTotalResource();
        if (!oldResource.equals(newResource)) {
            AbstractYarnScheduler.LOG.info("Update resource on node: " + node.getNodeName() + " from: " + oldResource + ", to: " + newResource);
            node.setTotalResource(newResource);
            Resources.subtractFrom(this.clusterResource, oldResource);
            Resources.addTo(this.clusterResource, newResource);
        }
        else {
            AbstractYarnScheduler.LOG.warn("Update resource on node: " + node.getNodeName() + " with the same resource: " + newResource);
        }
    }
    
    @Override
    public EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulingResourceTypes() {
        return EnumSet.of(YarnServiceProtos.SchedulerResourceTypes.MEMORY);
    }
    
    @Override
    public Set<String> getPlanQueues() throws YarnException {
        throw new YarnException(this.getClass().getSimpleName() + " does not support reservations");
    }
    
    static {
        LOG = LogFactory.getLog(AbstractYarnScheduler.class);
        EMPTY_CONTAINER_LIST = new ArrayList<Container>();
        EMPTY_ALLOCATION = new Allocation(AbstractYarnScheduler.EMPTY_CONTAINER_LIST, Resources.createResource(0), null, null, null);
    }
}
