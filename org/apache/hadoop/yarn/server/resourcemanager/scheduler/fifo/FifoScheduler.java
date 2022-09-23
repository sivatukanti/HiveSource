// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.utils.Lock;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeResourceUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.UpdatedContainerInfo;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerAppUtils;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import java.util.Collections;
import java.util.Arrays;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.HashMap;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Evolving
public class FifoScheduler extends AbstractYarnScheduler<FiCaSchedulerApp, FiCaSchedulerNode> implements Configurable
{
    private static final Log LOG;
    private static final RecordFactory recordFactory;
    Configuration conf;
    private boolean usePortForNodeName;
    private ActiveUsersManager activeUsersManager;
    private static final String DEFAULT_QUEUE_NAME = "default";
    private QueueMetrics metrics;
    private final ResourceCalculator resourceCalculator;
    private final Queue DEFAULT_QUEUE;
    private Resource usedResource;
    
    public FifoScheduler() {
        super(FifoScheduler.class.getName());
        this.resourceCalculator = new DefaultResourceCalculator();
        this.DEFAULT_QUEUE = new Queue() {
            @Override
            public String getQueueName() {
                return "default";
            }
            
            @Override
            public QueueMetrics getMetrics() {
                return FifoScheduler.this.metrics;
            }
            
            @Override
            public QueueInfo getQueueInfo(final boolean includeChildQueues, final boolean recursive) {
                final QueueInfo queueInfo = FifoScheduler.recordFactory.newRecordInstance(QueueInfo.class);
                queueInfo.setQueueName(FifoScheduler.this.DEFAULT_QUEUE.getQueueName());
                queueInfo.setCapacity(1.0f);
                if (FifoScheduler.this.clusterResource.getMemory() == 0) {
                    queueInfo.setCurrentCapacity(0.0f);
                }
                else {
                    queueInfo.setCurrentCapacity(FifoScheduler.this.usedResource.getMemory() / (float)FifoScheduler.this.clusterResource.getMemory());
                }
                queueInfo.setMaximumCapacity(1.0f);
                queueInfo.setChildQueues(new ArrayList<QueueInfo>());
                queueInfo.setQueueState(QueueState.RUNNING);
                return queueInfo;
            }
            
            public Map<QueueACL, AccessControlList> getQueueAcls() {
                final Map<QueueACL, AccessControlList> acls = new HashMap<QueueACL, AccessControlList>();
                for (final QueueACL acl : QueueACL.values()) {
                    acls.put(acl, new AccessControlList("*"));
                }
                return acls;
            }
            
            @Override
            public List<QueueUserACLInfo> getQueueUserAclInfo(final UserGroupInformation unused) {
                final QueueUserACLInfo queueUserAclInfo = FifoScheduler.recordFactory.newRecordInstance(QueueUserACLInfo.class);
                queueUserAclInfo.setQueueName("default");
                queueUserAclInfo.setUserAcls(Arrays.asList(QueueACL.values()));
                return Collections.singletonList(queueUserAclInfo);
            }
            
            @Override
            public boolean hasAccess(final QueueACL acl, final UserGroupInformation user) {
                return this.getQueueAcls().get(acl).isUserAllowed(user);
            }
            
            @Override
            public ActiveUsersManager getActiveUsersManager() {
                return FifoScheduler.this.activeUsersManager;
            }
            
            @Override
            public void recoverContainer(final Resource clusterResource, final SchedulerApplicationAttempt schedulerAttempt, final RMContainer rmContainer) {
                if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
                    return;
                }
                FifoScheduler.this.increaseUsedResources(rmContainer);
                FifoScheduler.this.updateAppHeadRoom(schedulerAttempt);
                FifoScheduler.this.updateAvailableResourcesMetrics();
            }
            
            @Override
            public Set<String> getAccessibleNodeLabels() {
                return null;
            }
            
            @Override
            public String getDefaultNodeLabelExpression() {
                return null;
            }
        };
        this.usedResource = FifoScheduler.recordFactory.newRecordInstance(Resource.class);
    }
    
    private synchronized void initScheduler(final Configuration conf) {
        this.validateConf(conf);
        this.applications = new ConcurrentSkipListMap<ApplicationId, SchedulerApplication<T>>();
        this.minimumAllocation = Resources.createResource(conf.getInt("yarn.scheduler.minimum-allocation-mb", 1024));
        this.maximumAllocation = Resources.createResource(conf.getInt("yarn.scheduler.maximum-allocation-mb", 8192));
        this.usePortForNodeName = conf.getBoolean("yarn.scheduler.include-port-in-node-name", false);
        this.metrics = QueueMetrics.forQueue("default", null, false, conf);
        this.activeUsersManager = new ActiveUsersManager(this.metrics);
    }
    
    @Override
    public void serviceInit(final Configuration conf) throws Exception {
        this.initScheduler(conf);
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        super.serviceStop();
    }
    
    @Override
    public synchronized void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    private void validateConf(final Configuration conf) {
        final int minMem = conf.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        final int maxMem = conf.getInt("yarn.scheduler.maximum-allocation-mb", 8192);
        if (minMem <= 0 || minMem > maxMem) {
            throw new YarnRuntimeException("Invalid resource scheduler memory allocation configuration, yarn.scheduler.minimum-allocation-mb=" + minMem + ", " + "yarn.scheduler.maximum-allocation-mb" + "=" + maxMem + ", min and max should be greater than 0" + ", max should be no smaller than min.");
        }
    }
    
    @Override
    public synchronized Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public int getNumClusterNodes() {
        return this.nodes.size();
    }
    
    @Override
    public synchronized void setRMContext(final RMContext rmContext) {
        this.rmContext = rmContext;
    }
    
    @Override
    public synchronized void reinitialize(final Configuration conf, final RMContext rmContext) throws IOException {
        this.setConf(conf);
    }
    
    @Override
    public Allocation allocate(final ApplicationAttemptId applicationAttemptId, final List<ResourceRequest> ask, final List<ContainerId> release, final List<String> blacklistAdditions, final List<String> blacklistRemovals) {
        final FiCaSchedulerApp application = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getApplicationAttempt(applicationAttemptId);
        if (application == null) {
            FifoScheduler.LOG.error("Calling allocate on removed or non existant application " + applicationAttemptId);
            return FifoScheduler.EMPTY_ALLOCATION;
        }
        SchedulerUtils.normalizeRequests(ask, this.resourceCalculator, this.clusterResource, this.minimumAllocation, this.maximumAllocation);
        this.releaseContainers(release, application);
        synchronized (application) {
            if (application.isStopped()) {
                FifoScheduler.LOG.info("Calling allocate on a stopped application " + applicationAttemptId);
                return FifoScheduler.EMPTY_ALLOCATION;
            }
            if (!ask.isEmpty()) {
                FifoScheduler.LOG.debug("allocate: pre-update applicationId=" + applicationAttemptId + " application=" + application);
                application.showRequests();
                application.updateResourceRequests(ask);
                FifoScheduler.LOG.debug("allocate: post-update applicationId=" + applicationAttemptId + " application=" + application);
                application.showRequests();
                FifoScheduler.LOG.debug("allocate: applicationId=" + applicationAttemptId + " #ask=" + ask.size());
            }
            application.updateBlacklist(blacklistAdditions, blacklistRemovals);
            final SchedulerApplicationAttempt.ContainersAndNMTokensAllocation allocation = application.pullNewlyAllocatedContainersAndNMTokens();
            return new Allocation(allocation.getContainerList(), application.getHeadroom(), null, null, null, allocation.getNMTokenList());
        }
    }
    
    private FiCaSchedulerNode getNode(final NodeId nodeId) {
        return (FiCaSchedulerNode)this.nodes.get(nodeId);
    }
    
    @VisibleForTesting
    public synchronized void addApplication(final ApplicationId applicationId, final String queue, final String user, final boolean isAppRecovering) {
        final SchedulerApplication<FiCaSchedulerApp> application = new SchedulerApplication<FiCaSchedulerApp>(this.DEFAULT_QUEUE, user);
        this.applications.put(applicationId, (SchedulerApplication<T>)application);
        this.metrics.submitApp(user);
        FifoScheduler.LOG.info("Accepted application " + applicationId + " from user: " + user + ", currently num of applications: " + this.applications.size());
        if (isAppRecovering) {
            if (FifoScheduler.LOG.isDebugEnabled()) {
                FifoScheduler.LOG.debug(applicationId + " is recovering. Skip notifying APP_ACCEPTED");
            }
        }
        else {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(applicationId, RMAppEventType.APP_ACCEPTED));
        }
    }
    
    @VisibleForTesting
    public synchronized void addApplicationAttempt(final ApplicationAttemptId appAttemptId, final boolean transferStateFromPreviousAttempt, final boolean isAttemptRecovering) {
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(appAttemptId.getApplicationId());
        final String user = application.getUser();
        final FiCaSchedulerApp schedulerApp = new FiCaSchedulerApp(appAttemptId, user, this.DEFAULT_QUEUE, this.activeUsersManager, this.rmContext);
        if (transferStateFromPreviousAttempt) {
            schedulerApp.transferStateFromPreviousAttempt(application.getCurrentAppAttempt());
        }
        application.setCurrentAppAttempt(schedulerApp);
        this.metrics.submitAppAttempt(user);
        FifoScheduler.LOG.info("Added Application Attempt " + appAttemptId + " to scheduler from user " + application.getUser());
        if (isAttemptRecovering) {
            if (FifoScheduler.LOG.isDebugEnabled()) {
                FifoScheduler.LOG.debug(appAttemptId + " is recovering. Skipping notifying ATTEMPT_ADDED");
            }
        }
        else {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptEvent(appAttemptId, RMAppAttemptEventType.ATTEMPT_ADDED));
        }
    }
    
    private synchronized void doneApplication(final ApplicationId applicationId, final RMAppState finalState) {
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(applicationId);
        if (application == null) {
            FifoScheduler.LOG.warn("Couldn't find application " + applicationId);
            return;
        }
        this.activeUsersManager.deactivateApplication(application.getUser(), applicationId);
        application.stop(finalState);
        this.applications.remove(applicationId);
    }
    
    private synchronized void doneApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final RMAppAttemptState rmAppAttemptFinalState, final boolean keepContainers) throws IOException {
        final FiCaSchedulerApp attempt = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getApplicationAttempt(applicationAttemptId);
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(applicationAttemptId.getApplicationId());
        if (application == null || attempt == null) {
            throw new IOException("Unknown application " + applicationAttemptId + " has completed!");
        }
        for (final RMContainer container : attempt.getLiveContainers()) {
            if (keepContainers && container.getState().equals(RMContainerState.RUNNING)) {
                FifoScheduler.LOG.info("Skip killing " + container.getContainerId());
            }
            else {
                this.completedContainer(container, SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container of a completed application"), RMContainerEventType.KILL);
            }
        }
        attempt.stop(rmAppAttemptFinalState);
    }
    
    private void assignContainers(final FiCaSchedulerNode node) {
        FifoScheduler.LOG.debug("assignContainers: node=" + node.getRMNode().getNodeAddress() + " #applications=" + this.applications.size());
        for (final Map.Entry<ApplicationId, SchedulerApplication<FiCaSchedulerApp>> e : this.applications.entrySet()) {
            final FiCaSchedulerApp application = e.getValue().getCurrentAppAttempt();
            if (application == null) {
                continue;
            }
            FifoScheduler.LOG.debug("pre-assignContainers");
            application.showRequests();
            synchronized (application) {
                if (SchedulerAppUtils.isBlacklisted(application, node, FifoScheduler.LOG)) {
                    continue;
                }
                for (final Priority priority : application.getPriorities()) {
                    final int maxContainers = this.getMaxAllocatableContainers(application, priority, node, NodeType.OFF_SWITCH);
                    if (maxContainers > 0) {
                        final int assignedContainers = this.assignContainersOnNode(node, application, priority);
                        if (assignedContainers == 0) {
                            break;
                        }
                        continue;
                    }
                }
            }
            FifoScheduler.LOG.debug("post-assignContainers");
            application.showRequests();
            if (Resources.lessThan(this.resourceCalculator, this.clusterResource, node.getAvailableResource(), this.minimumAllocation)) {
                break;
            }
        }
        for (final SchedulerApplication<FiCaSchedulerApp> application2 : this.applications.values()) {
            final FiCaSchedulerApp attempt = application2.getCurrentAppAttempt();
            if (attempt == null) {
                continue;
            }
            this.updateAppHeadRoom(attempt);
        }
    }
    
    private int getMaxAllocatableContainers(final FiCaSchedulerApp application, final Priority priority, final FiCaSchedulerNode node, final NodeType type) {
        int maxContainers = 0;
        final ResourceRequest offSwitchRequest = application.getResourceRequest(priority, "*");
        if (offSwitchRequest != null) {
            maxContainers = offSwitchRequest.getNumContainers();
        }
        if (type == NodeType.OFF_SWITCH) {
            return maxContainers;
        }
        if (type == NodeType.RACK_LOCAL) {
            final ResourceRequest rackLocalRequest = application.getResourceRequest(priority, node.getRMNode().getRackName());
            if (rackLocalRequest == null) {
                return maxContainers;
            }
            maxContainers = Math.min(maxContainers, rackLocalRequest.getNumContainers());
        }
        if (type == NodeType.NODE_LOCAL) {
            final ResourceRequest nodeLocalRequest = application.getResourceRequest(priority, node.getRMNode().getNodeAddress());
            if (nodeLocalRequest != null) {
                maxContainers = Math.min(maxContainers, nodeLocalRequest.getNumContainers());
            }
        }
        return maxContainers;
    }
    
    private int assignContainersOnNode(final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority) {
        final int nodeLocalContainers = this.assignNodeLocalContainers(node, application, priority);
        final int rackLocalContainers = this.assignRackLocalContainers(node, application, priority);
        final int offSwitchContainers = this.assignOffSwitchContainers(node, application, priority);
        FifoScheduler.LOG.debug("assignContainersOnNode: node=" + node.getRMNode().getNodeAddress() + " application=" + application.getApplicationId().getId() + " priority=" + priority.getPriority() + " #assigned=" + (nodeLocalContainers + rackLocalContainers + offSwitchContainers));
        return nodeLocalContainers + rackLocalContainers + offSwitchContainers;
    }
    
    private int assignNodeLocalContainers(final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority) {
        int assignedContainers = 0;
        final ResourceRequest request = application.getResourceRequest(priority, node.getNodeName());
        if (request != null) {
            final ResourceRequest rackRequest = application.getResourceRequest(priority, node.getRMNode().getRackName());
            if (rackRequest == null || rackRequest.getNumContainers() <= 0) {
                return 0;
            }
            final int assignableContainers = Math.min(this.getMaxAllocatableContainers(application, priority, node, NodeType.NODE_LOCAL), request.getNumContainers());
            assignedContainers = this.assignContainer(node, application, priority, assignableContainers, request, NodeType.NODE_LOCAL);
        }
        return assignedContainers;
    }
    
    private int assignRackLocalContainers(final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority) {
        int assignedContainers = 0;
        final ResourceRequest request = application.getResourceRequest(priority, node.getRMNode().getRackName());
        if (request != null) {
            final ResourceRequest offSwitchRequest = application.getResourceRequest(priority, "*");
            if (offSwitchRequest.getNumContainers() <= 0) {
                return 0;
            }
            final int assignableContainers = Math.min(this.getMaxAllocatableContainers(application, priority, node, NodeType.RACK_LOCAL), request.getNumContainers());
            assignedContainers = this.assignContainer(node, application, priority, assignableContainers, request, NodeType.RACK_LOCAL);
        }
        return assignedContainers;
    }
    
    private int assignOffSwitchContainers(final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority) {
        int assignedContainers = 0;
        final ResourceRequest request = application.getResourceRequest(priority, "*");
        if (request != null) {
            assignedContainers = this.assignContainer(node, application, priority, request.getNumContainers(), request, NodeType.OFF_SWITCH);
        }
        return assignedContainers;
    }
    
    private int assignContainer(final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final int assignableContainers, final ResourceRequest request, final NodeType type) {
        FifoScheduler.LOG.debug("assignContainers: node=" + node.getRMNode().getNodeAddress() + " application=" + application.getApplicationId().getId() + " priority=" + priority.getPriority() + " assignableContainers=" + assignableContainers + " request=" + request + " type=" + type);
        final Resource capability = request.getCapability();
        final int availableContainers = node.getAvailableResource().getMemory() / capability.getMemory();
        final int assignedContainers = Math.min(assignableContainers, availableContainers);
        if (assignedContainers > 0) {
            for (int i = 0; i < assignedContainers; ++i) {
                final NodeId nodeId = node.getRMNode().getNodeID();
                final ContainerId containerId = BuilderUtils.newContainerId(application.getApplicationAttemptId(), application.getNewContainerId());
                final Container container = BuilderUtils.newContainer(containerId, nodeId, node.getRMNode().getHttpAddress(), capability, priority, null);
                final RMContainer rmContainer = application.allocate(type, node, priority, request, container);
                node.allocateContainer(rmContainer);
                this.increaseUsedResources(rmContainer);
            }
        }
        return assignedContainers;
    }
    
    private synchronized void nodeUpdate(final RMNode rmNode) {
        final FiCaSchedulerNode node = this.getNode(rmNode.getNodeID());
        final List<UpdatedContainerInfo> containerInfoList = rmNode.pullContainerUpdates();
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
            FifoScheduler.LOG.debug("Container FINISHED: " + containerId);
            this.completedContainer(this.getRMContainer(containerId), completedContainer, RMContainerEventType.FINISHED);
        }
        if (this.rmContext.isWorkPreservingRecoveryEnabled() && !this.rmContext.isSchedulerReadyForAllocatingContainers()) {
            return;
        }
        if (Resources.greaterThanOrEqual(this.resourceCalculator, this.clusterResource, node.getAvailableResource(), this.minimumAllocation)) {
            FifoScheduler.LOG.debug("Node heartbeat " + rmNode.getNodeID() + " available resource = " + node.getAvailableResource());
            this.assignContainers(node);
            FifoScheduler.LOG.debug("Node after allocation " + rmNode.getNodeID() + " resource = " + node.getAvailableResource());
        }
        this.updateAvailableResourcesMetrics();
    }
    
    private void increaseUsedResources(final RMContainer rmContainer) {
        Resources.addTo(this.usedResource, rmContainer.getAllocatedResource());
    }
    
    private void updateAppHeadRoom(final SchedulerApplicationAttempt schedulerAttempt) {
        schedulerAttempt.setHeadroom(Resources.subtract(this.clusterResource, this.usedResource));
    }
    
    private void updateAvailableResourcesMetrics() {
        this.metrics.setAvailableResourcesToQueue(Resources.subtract(this.clusterResource, this.usedResource));
    }
    
    @Override
    public void handle(final SchedulerEvent event) {
        switch (event.getType()) {
            case NODE_ADDED: {
                final NodeAddedSchedulerEvent nodeAddedEvent = (NodeAddedSchedulerEvent)event;
                this.addNode(nodeAddedEvent.getAddedRMNode());
                this.recoverContainersOnNode(nodeAddedEvent.getContainerReports(), nodeAddedEvent.getAddedRMNode());
                break;
            }
            case NODE_REMOVED: {
                final NodeRemovedSchedulerEvent nodeRemovedEvent = (NodeRemovedSchedulerEvent)event;
                this.removeNode(nodeRemovedEvent.getRemovedRMNode());
                break;
            }
            case NODE_RESOURCE_UPDATE: {
                final NodeResourceUpdateSchedulerEvent nodeResourceUpdatedEvent = (NodeResourceUpdateSchedulerEvent)event;
                this.updateNodeResource(nodeResourceUpdatedEvent.getRMNode(), nodeResourceUpdatedEvent.getResourceOption());
                break;
            }
            case NODE_UPDATE: {
                final NodeUpdateSchedulerEvent nodeUpdatedEvent = (NodeUpdateSchedulerEvent)event;
                this.nodeUpdate(nodeUpdatedEvent.getRMNode());
                break;
            }
            case APP_ADDED: {
                final AppAddedSchedulerEvent appAddedEvent = (AppAddedSchedulerEvent)event;
                this.addApplication(appAddedEvent.getApplicationId(), appAddedEvent.getQueue(), appAddedEvent.getUser(), appAddedEvent.getIsAppRecovering());
                break;
            }
            case APP_REMOVED: {
                final AppRemovedSchedulerEvent appRemovedEvent = (AppRemovedSchedulerEvent)event;
                this.doneApplication(appRemovedEvent.getApplicationID(), appRemovedEvent.getFinalState());
                break;
            }
            case APP_ATTEMPT_ADDED: {
                final AppAttemptAddedSchedulerEvent appAttemptAddedEvent = (AppAttemptAddedSchedulerEvent)event;
                this.addApplicationAttempt(appAttemptAddedEvent.getApplicationAttemptId(), appAttemptAddedEvent.getTransferStateFromPreviousAttempt(), appAttemptAddedEvent.getIsAttemptRecovering());
                break;
            }
            case APP_ATTEMPT_REMOVED: {
                final AppAttemptRemovedSchedulerEvent appAttemptRemovedEvent = (AppAttemptRemovedSchedulerEvent)event;
                try {
                    this.doneApplicationAttempt(appAttemptRemovedEvent.getApplicationAttemptID(), appAttemptRemovedEvent.getFinalAttemptState(), appAttemptRemovedEvent.getKeepContainersAcrossAppAttempts());
                }
                catch (IOException ie) {
                    FifoScheduler.LOG.error("Unable to remove application " + appAttemptRemovedEvent.getApplicationAttemptID(), ie);
                }
                break;
            }
            case CONTAINER_EXPIRED: {
                final ContainerExpiredSchedulerEvent containerExpiredEvent = (ContainerExpiredSchedulerEvent)event;
                final ContainerId containerid = containerExpiredEvent.getContainerId();
                this.completedContainer(this.getRMContainer(containerid), SchedulerUtils.createAbnormalContainerStatus(containerid, "Container expired since it was unused"), RMContainerEventType.EXPIRE);
                break;
            }
            default: {
                FifoScheduler.LOG.error("Invalid eventtype " + ((AbstractEvent<Object>)event).getType() + ". Ignoring!");
                break;
            }
        }
    }
    
    @Lock({ FifoScheduler.class })
    @Override
    protected synchronized void completedContainer(final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event) {
        if (rmContainer == null) {
            FifoScheduler.LOG.info("Null container completed...");
            return;
        }
        final Container container = rmContainer.getContainer();
        final FiCaSchedulerApp application = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getCurrentAttemptForContainer(container.getId());
        final ApplicationId appId = container.getId().getApplicationAttemptId().getApplicationId();
        final FiCaSchedulerNode node = this.getNode(container.getNodeId());
        if (application == null) {
            FifoScheduler.LOG.info("Unknown application: " + appId + " released container " + container.getId() + " on node: " + node + " with event: " + event);
            return;
        }
        application.containerCompleted(rmContainer, containerStatus, event);
        node.releaseContainer(container);
        Resources.subtractFrom(this.usedResource, container.getResource());
        FifoScheduler.LOG.info("Application attempt " + application.getApplicationAttemptId() + " released container " + container.getId() + " on node: " + node + " with event: " + event);
    }
    
    private synchronized void removeNode(final RMNode nodeInfo) {
        final FiCaSchedulerNode node = this.getNode(nodeInfo.getNodeID());
        if (node == null) {
            return;
        }
        for (final RMContainer container : node.getRunningContainers()) {
            this.completedContainer(container, SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container released on a *lost* node"), RMContainerEventType.KILL);
        }
        this.nodes.remove(nodeInfo.getNodeID());
        Resources.subtractFrom(this.clusterResource, node.getRMNode().getTotalCapability());
    }
    
    @Override
    public QueueInfo getQueueInfo(final String queueName, final boolean includeChildQueues, final boolean recursive) {
        return this.DEFAULT_QUEUE.getQueueInfo(false, false);
    }
    
    @Override
    public List<QueueUserACLInfo> getQueueUserAclInfo() {
        return this.DEFAULT_QUEUE.getQueueUserAclInfo(null);
    }
    
    private synchronized void addNode(final RMNode nodeManager) {
        this.nodes.put(nodeManager.getNodeID(), (N)new FiCaSchedulerNode(nodeManager, this.usePortForNodeName));
        Resources.addTo(this.clusterResource, nodeManager.getTotalCapability());
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) {
    }
    
    @Override
    public RMContainer getRMContainer(final ContainerId containerId) {
        final FiCaSchedulerApp attempt = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getCurrentAttemptForContainer(containerId);
        return (attempt == null) ? null : attempt.getRMContainer(containerId);
    }
    
    @Override
    public QueueMetrics getRootQueueMetrics() {
        return this.DEFAULT_QUEUE.getMetrics();
    }
    
    @Override
    public synchronized boolean checkAccess(final UserGroupInformation callerUGI, final QueueACL acl, final String queueName) {
        return this.DEFAULT_QUEUE.hasAccess(acl, callerUGI);
    }
    
    @Override
    public synchronized List<ApplicationAttemptId> getAppsInQueue(final String queueName) {
        if (queueName.equals(this.DEFAULT_QUEUE.getQueueName())) {
            final List<ApplicationAttemptId> attempts = new ArrayList<ApplicationAttemptId>(this.applications.size());
            for (final SchedulerApplication<FiCaSchedulerApp> app : this.applications.values()) {
                attempts.add(app.getCurrentAppAttempt().getApplicationAttemptId());
            }
            return attempts;
        }
        return null;
    }
    
    public Resource getUsedResource() {
        return this.usedResource;
    }
    
    static {
        LOG = LogFactory.getLog(FifoScheduler.class);
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
    }
}
