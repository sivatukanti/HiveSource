// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.yarn.event.EventHandler;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.Collection;
import java.util.Arrays;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.RMAuditLogger;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import java.util.HashMap;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FSAppAttempt extends SchedulerApplicationAttempt implements Schedulable
{
    private static final Log LOG;
    private static final DefaultResourceCalculator RESOURCE_CALCULATOR;
    private long startTime;
    private Priority priority;
    private ResourceWeights resourceWeights;
    private Resource demand;
    private FairScheduler scheduler;
    private Resource fairShare;
    private Resource preemptedResources;
    private RMContainerComparator comparator;
    private final Map<RMContainer, Long> preemptionMap;
    private final Map<Priority, NodeType> allowedLocalityLevel;
    
    public FSAppAttempt(final FairScheduler scheduler, final ApplicationAttemptId applicationAttemptId, final String user, final FSLeafQueue queue, final ActiveUsersManager activeUsersManager, final RMContext rmContext) {
        super(applicationAttemptId, user, queue, activeUsersManager, rmContext);
        this.demand = Resources.createResource(0);
        this.fairShare = Resources.createResource(0, 0);
        this.preemptedResources = Resources.createResource(0);
        this.comparator = new RMContainerComparator();
        this.preemptionMap = new HashMap<RMContainer, Long>();
        this.allowedLocalityLevel = new HashMap<Priority, NodeType>();
        this.scheduler = scheduler;
        this.startTime = scheduler.getClock().getTime();
        this.priority = Priority.newInstance(1);
        this.resourceWeights = new ResourceWeights();
    }
    
    public ResourceWeights getResourceWeights() {
        return this.resourceWeights;
    }
    
    public QueueMetrics getMetrics() {
        return this.queue.getMetrics();
    }
    
    public synchronized void containerCompleted(final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event) {
        final Container container = rmContainer.getContainer();
        final ContainerId containerId = container.getId();
        this.newlyAllocatedContainers.remove(rmContainer);
        ((EventHandler<RMContainerFinishedEvent>)rmContainer).handle(new RMContainerFinishedEvent(containerId, containerStatus, event));
        FSAppAttempt.LOG.info("Completed container: " + rmContainer.getContainerId() + " in state: " + rmContainer.getState() + " event:" + event);
        this.liveContainers.remove(rmContainer.getContainerId());
        RMAuditLogger.logSuccess(this.getUser(), "AM Released Container", "SchedulerApp", this.getApplicationId(), containerId);
        final Resource containerResource = rmContainer.getContainer().getResource();
        this.queue.getMetrics().releaseResources(this.getUser(), 1, containerResource);
        Resources.subtractFrom(this.currentConsumption, containerResource);
        this.preemptionMap.remove(rmContainer);
        this.lastMemoryAggregateAllocationUpdateTime = -1L;
    }
    
    private synchronized void unreserveInternal(final Priority priority, final FSSchedulerNode node) {
        final Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        final RMContainer reservedContainer = reservedContainers.remove(node.getNodeID());
        if (reservedContainers.isEmpty()) {
            this.reservedContainers.remove(priority);
        }
        this.resetReReservations(priority);
        final Resource resource = reservedContainer.getContainer().getResource();
        Resources.subtractFrom(this.currentReservation, resource);
        FSAppAttempt.LOG.info("Application " + this.getApplicationId() + " unreserved " + " on node " + node + ", currently has " + reservedContainers.size() + " at priority " + priority + "; currentReservation " + this.currentReservation);
    }
    
    @Override
    public synchronized Resource getHeadroom() {
        final FSQueue queue = (FSQueue)this.queue;
        final SchedulingPolicy policy = queue.getPolicy();
        final Resource queueFairShare = queue.getFairShare();
        final Resource queueUsage = queue.getResourceUsage();
        final Resource clusterResource = this.scheduler.getClusterResource();
        final Resource clusterUsage = this.scheduler.getRootQueueMetrics().getAllocatedResources();
        final Resource clusterAvailableResource = Resources.subtract(clusterResource, clusterUsage);
        final Resource headroom = policy.getHeadroom(queueFairShare, queueUsage, clusterAvailableResource);
        if (FSAppAttempt.LOG.isDebugEnabled()) {
            FSAppAttempt.LOG.debug("Headroom calculation for " + this.getName() + ":" + "Min(" + "(queueFairShare=" + queueFairShare + " - queueUsage=" + queueUsage + ")," + " clusterAvailableResource=" + clusterAvailableResource + "(clusterResource=" + clusterResource + " - clusterUsage=" + clusterUsage + ")" + "Headroom=" + headroom);
        }
        return headroom;
    }
    
    public synchronized float getLocalityWaitFactor(final Priority priority, final int clusterNodes) {
        final int requiredResources = Math.max(this.getResourceRequests(priority).size() - 1, 0);
        return Math.min(requiredResources / (float)clusterNodes, 1.0f);
    }
    
    public synchronized NodeType getAllowedLocalityLevel(final Priority priority, final int numNodes, double nodeLocalityThreshold, double rackLocalityThreshold) {
        if (nodeLocalityThreshold > 1.0) {
            nodeLocalityThreshold = 1.0;
        }
        if (rackLocalityThreshold > 1.0) {
            rackLocalityThreshold = 1.0;
        }
        if (nodeLocalityThreshold < 0.0 || rackLocalityThreshold < 0.0) {
            return NodeType.OFF_SWITCH;
        }
        if (!this.allowedLocalityLevel.containsKey(priority)) {
            this.allowedLocalityLevel.put(priority, NodeType.NODE_LOCAL);
            return NodeType.NODE_LOCAL;
        }
        final NodeType allowed = this.allowedLocalityLevel.get(priority);
        if (allowed.equals(NodeType.OFF_SWITCH)) {
            return NodeType.OFF_SWITCH;
        }
        final double threshold = allowed.equals(NodeType.NODE_LOCAL) ? nodeLocalityThreshold : rackLocalityThreshold;
        if (this.getSchedulingOpportunities(priority) > numNodes * threshold) {
            if (allowed.equals(NodeType.NODE_LOCAL)) {
                this.allowedLocalityLevel.put(priority, NodeType.RACK_LOCAL);
                this.resetSchedulingOpportunities(priority);
            }
            else if (allowed.equals(NodeType.RACK_LOCAL)) {
                this.allowedLocalityLevel.put(priority, NodeType.OFF_SWITCH);
                this.resetSchedulingOpportunities(priority);
            }
        }
        return this.allowedLocalityLevel.get(priority);
    }
    
    public synchronized NodeType getAllowedLocalityLevelByTime(final Priority priority, final long nodeLocalityDelayMs, final long rackLocalityDelayMs, final long currentTimeMs) {
        if (nodeLocalityDelayMs < 0L || rackLocalityDelayMs < 0L) {
            return NodeType.OFF_SWITCH;
        }
        if (!this.allowedLocalityLevel.containsKey(priority)) {
            this.allowedLocalityLevel.put(priority, NodeType.NODE_LOCAL);
            return NodeType.NODE_LOCAL;
        }
        final NodeType allowed = this.allowedLocalityLevel.get(priority);
        if (allowed.equals(NodeType.OFF_SWITCH)) {
            return NodeType.OFF_SWITCH;
        }
        long waitTime = currentTimeMs;
        if (this.lastScheduledContainer.containsKey(priority)) {
            waitTime -= this.lastScheduledContainer.get(priority);
        }
        else {
            waitTime -= this.getStartTime();
        }
        final long thresholdTime = allowed.equals(NodeType.NODE_LOCAL) ? nodeLocalityDelayMs : rackLocalityDelayMs;
        if (waitTime > thresholdTime) {
            if (allowed.equals(NodeType.NODE_LOCAL)) {
                this.allowedLocalityLevel.put(priority, NodeType.RACK_LOCAL);
                this.resetSchedulingOpportunities(priority, currentTimeMs);
            }
            else if (allowed.equals(NodeType.RACK_LOCAL)) {
                this.allowedLocalityLevel.put(priority, NodeType.OFF_SWITCH);
                this.resetSchedulingOpportunities(priority, currentTimeMs);
            }
        }
        return this.allowedLocalityLevel.get(priority);
    }
    
    public synchronized RMContainer allocate(final NodeType type, final FSSchedulerNode node, final Priority priority, final ResourceRequest request, final Container container) {
        final NodeType allowed = this.allowedLocalityLevel.get(priority);
        if (allowed != null) {
            if (allowed.equals(NodeType.OFF_SWITCH) && (type.equals(NodeType.NODE_LOCAL) || type.equals(NodeType.RACK_LOCAL))) {
                this.resetAllowedLocalityLevel(priority, type);
            }
            else if (allowed.equals(NodeType.RACK_LOCAL) && type.equals(NodeType.NODE_LOCAL)) {
                this.resetAllowedLocalityLevel(priority, type);
            }
        }
        if (this.getTotalRequiredResources(priority) <= 0) {
            return null;
        }
        final RMContainer rmContainer = new RMContainerImpl(container, this.getApplicationAttemptId(), node.getNodeID(), this.appSchedulingInfo.getUser(), this.rmContext);
        this.newlyAllocatedContainers.add(rmContainer);
        this.liveContainers.put(container.getId(), rmContainer);
        final List<ResourceRequest> resourceRequestList = this.appSchedulingInfo.allocate(type, node, priority, request, container);
        Resources.addTo(this.currentConsumption, container.getResource());
        ((RMContainerImpl)rmContainer).setResourceRequests(resourceRequestList);
        rmContainer.handle(new RMContainerEvent(container.getId(), RMContainerEventType.START));
        if (FSAppAttempt.LOG.isDebugEnabled()) {
            FSAppAttempt.LOG.debug("allocate: applicationAttemptId=" + container.getId().getApplicationAttemptId() + " container=" + container.getId() + " host=" + container.getNodeId().getHost() + " type=" + type);
        }
        RMAuditLogger.logSuccess(this.getUser(), "AM Allocated Container", "SchedulerApp", this.getApplicationId(), container.getId());
        return rmContainer;
    }
    
    public synchronized void resetAllowedLocalityLevel(final Priority priority, final NodeType level) {
        final NodeType old = this.allowedLocalityLevel.get(priority);
        FSAppAttempt.LOG.info("Raising locality level from " + old + " to " + level + " at " + " priority " + priority);
        this.allowedLocalityLevel.put(priority, level);
    }
    
    public void addPreemption(final RMContainer container, final long time) {
        assert this.preemptionMap.get(container) == null;
        this.preemptionMap.put(container, time);
        Resources.addTo(this.preemptedResources, container.getAllocatedResource());
    }
    
    public Long getContainerPreemptionTime(final RMContainer container) {
        return this.preemptionMap.get(container);
    }
    
    public Set<RMContainer> getPreemptionContainers() {
        return this.preemptionMap.keySet();
    }
    
    @Override
    public FSLeafQueue getQueue() {
        return (FSLeafQueue)super.getQueue();
    }
    
    public Resource getPreemptedResources() {
        return this.preemptedResources;
    }
    
    public void resetPreemptedResources() {
        this.preemptedResources = Resources.createResource(0);
        for (final RMContainer container : this.getPreemptionContainers()) {
            Resources.addTo(this.preemptedResources, container.getAllocatedResource());
        }
    }
    
    public void clearPreemptedResources() {
        this.preemptedResources.setMemory(0);
        this.preemptedResources.setVirtualCores(0);
    }
    
    public Container createContainer(final FSSchedulerNode node, final Resource capability, final Priority priority) {
        final NodeId nodeId = node.getRMNode().getNodeID();
        final ContainerId containerId = BuilderUtils.newContainerId(this.getApplicationAttemptId(), this.getNewContainerId());
        final Container container = BuilderUtils.newContainer(containerId, nodeId, node.getRMNode().getHttpAddress(), capability, priority, null);
        return container;
    }
    
    private void reserve(final Priority priority, final FSSchedulerNode node, final Container container, final boolean alreadyReserved) {
        FSAppAttempt.LOG.info("Making reservation: node=" + node.getNodeName() + " app_id=" + this.getApplicationId());
        if (!alreadyReserved) {
            this.getMetrics().reserveResource(this.getUser(), container.getResource());
            final RMContainer rmContainer = super.reserve(node, priority, null, container);
            node.reserveResource(this, priority, rmContainer);
        }
        else {
            final RMContainer rmContainer = node.getReservedContainer();
            super.reserve(node, priority, rmContainer, container);
            node.reserveResource(this, priority, rmContainer);
        }
    }
    
    public void unreserve(final Priority priority, final FSSchedulerNode node) {
        final RMContainer rmContainer = node.getReservedContainer();
        this.unreserveInternal(priority, node);
        node.unreserveResource(this);
        this.getMetrics().unreserveResource(this.getUser(), rmContainer.getContainer().getResource());
    }
    
    private Resource assignContainer(final FSSchedulerNode node, final ResourceRequest request, final NodeType type, final boolean reserved) {
        final Resource capability = request.getCapability();
        final Resource available = node.getAvailableResource();
        Container container = null;
        if (reserved) {
            container = node.getReservedContainer().getContainer();
        }
        else {
            container = this.createContainer(node, capability, request.getPriority());
        }
        if (!Resources.fitsIn(capability, available)) {
            this.reserve(request.getPriority(), node, container, reserved);
            return FairScheduler.CONTAINER_RESERVED;
        }
        final RMContainer allocatedContainer = this.allocate(type, node, request.getPriority(), request, container);
        if (allocatedContainer == null) {
            if (reserved) {
                this.unreserve(request.getPriority(), node);
            }
            return Resources.none();
        }
        if (reserved) {
            this.unreserve(request.getPriority(), node);
        }
        node.allocateContainer(allocatedContainer);
        if (this.getLiveContainers().size() == 1 && !this.getUnmanagedAM()) {
            this.getQueue().addAMResourceUsage(container.getResource());
            this.setAmRunning(true);
        }
        return container.getResource();
    }
    
    private Resource assignContainer(final FSSchedulerNode node, final boolean reserved) {
        if (FSAppAttempt.LOG.isDebugEnabled()) {
            FSAppAttempt.LOG.debug("Node offered to app: " + this.getName() + " reserved: " + reserved);
        }
        final Collection<Priority> prioritiesToTry = reserved ? Arrays.asList(node.getReservedContainer().getReservedPriority()) : this.getPriorities();
        synchronized (this) {
            for (final Priority priority : prioritiesToTry) {
                if (this.getTotalRequiredResources(priority) > 0) {
                    if (!this.hasContainerForNode(priority, node)) {
                        continue;
                    }
                    this.addSchedulingOpportunity(priority);
                    if (this.getLiveContainers().size() == 0 && !this.getUnmanagedAM() && !this.getQueue().canRunAppAM(this.getAMResource())) {
                        return Resources.none();
                    }
                    final ResourceRequest rackLocalRequest = this.getResourceRequest(priority, node.getRackName());
                    final ResourceRequest localRequest = this.getResourceRequest(priority, node.getNodeName());
                    if (localRequest != null && !localRequest.getRelaxLocality()) {
                        FSAppAttempt.LOG.warn("Relax locality off is not supported on local request: " + localRequest);
                    }
                    NodeType allowedLocality;
                    if (this.scheduler.isContinuousSchedulingEnabled()) {
                        allowedLocality = this.getAllowedLocalityLevelByTime(priority, this.scheduler.getNodeLocalityDelayMs(), this.scheduler.getRackLocalityDelayMs(), this.scheduler.getClock().getTime());
                    }
                    else {
                        allowedLocality = this.getAllowedLocalityLevel(priority, this.scheduler.getNumClusterNodes(), this.scheduler.getNodeLocalityThreshold(), this.scheduler.getRackLocalityThreshold());
                    }
                    if (rackLocalRequest != null && rackLocalRequest.getNumContainers() != 0 && localRequest != null && localRequest.getNumContainers() != 0) {
                        return this.assignContainer(node, localRequest, NodeType.NODE_LOCAL, reserved);
                    }
                    if (rackLocalRequest != null && !rackLocalRequest.getRelaxLocality()) {
                        continue;
                    }
                    if (rackLocalRequest != null && rackLocalRequest.getNumContainers() != 0 && (allowedLocality.equals(NodeType.RACK_LOCAL) || allowedLocality.equals(NodeType.OFF_SWITCH))) {
                        return this.assignContainer(node, rackLocalRequest, NodeType.RACK_LOCAL, reserved);
                    }
                    final ResourceRequest offSwitchRequest = this.getResourceRequest(priority, "*");
                    if (offSwitchRequest != null && !offSwitchRequest.getRelaxLocality()) {
                        continue;
                    }
                    if (offSwitchRequest != null && offSwitchRequest.getNumContainers() != 0 && allowedLocality.equals(NodeType.OFF_SWITCH)) {
                        return this.assignContainer(node, offSwitchRequest, NodeType.OFF_SWITCH, reserved);
                    }
                    continue;
                }
            }
        }
        return Resources.none();
    }
    
    public Resource assignReservedContainer(final FSSchedulerNode node) {
        final RMContainer rmContainer = node.getReservedContainer();
        final Priority priority = rmContainer.getReservedPriority();
        if (this.getTotalRequiredResources(priority) == 0) {
            this.unreserve(priority, node);
            return Resources.none();
        }
        if (!Resources.fitsIn(node.getReservedContainer().getReservedResource(), node.getAvailableResource())) {
            return Resources.none();
        }
        return this.assignContainer(node, true);
    }
    
    public boolean hasContainerForNode(final Priority prio, final FSSchedulerNode node) {
        final ResourceRequest anyRequest = this.getResourceRequest(prio, "*");
        final ResourceRequest rackRequest = this.getResourceRequest(prio, node.getRackName());
        final ResourceRequest nodeRequest = this.getResourceRequest(prio, node.getNodeName());
        return anyRequest != null && anyRequest.getNumContainers() > 0 && (anyRequest.getRelaxLocality() || (rackRequest != null && rackRequest.getNumContainers() > 0)) && (rackRequest == null || rackRequest.getRelaxLocality() || (nodeRequest != null && nodeRequest.getNumContainers() > 0)) && Resources.lessThanOrEqual(FSAppAttempt.RESOURCE_CALCULATOR, null, anyRequest.getCapability(), node.getRMNode().getTotalCapability());
    }
    
    @Override
    public String getName() {
        return this.getApplicationId().toString();
    }
    
    @Override
    public Resource getDemand() {
        return this.demand;
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
    
    @Override
    public Resource getMinShare() {
        return Resources.none();
    }
    
    @Override
    public Resource getMaxShare() {
        return Resources.unbounded();
    }
    
    @Override
    public Resource getResourceUsage() {
        return Resources.subtract(this.getCurrentConsumption(), this.getPreemptedResources());
    }
    
    @Override
    public ResourceWeights getWeights() {
        return this.scheduler.getAppWeight(this);
    }
    
    @Override
    public Priority getPriority() {
        return this.priority;
    }
    
    @Override
    public Resource getFairShare() {
        return this.fairShare;
    }
    
    @Override
    public void setFairShare(final Resource fairShare) {
        this.fairShare = fairShare;
    }
    
    @Override
    public void updateDemand() {
        Resources.addTo(this.demand = Resources.createResource(0), this.getCurrentConsumption());
        synchronized (this) {
            for (final Priority p : this.getPriorities()) {
                for (final ResourceRequest r : this.getResourceRequests(p).values()) {
                    final Resource total = Resources.multiply(r.getCapability(), r.getNumContainers());
                    Resources.addTo(this.demand, total);
                }
            }
        }
    }
    
    @Override
    public Resource assignContainer(final FSSchedulerNode node) {
        return this.assignContainer(node, false);
    }
    
    @Override
    public RMContainer preemptContainer() {
        if (FSAppAttempt.LOG.isDebugEnabled()) {
            FSAppAttempt.LOG.debug("App " + this.getName() + " is going to preempt a running " + "container");
        }
        RMContainer toBePreempted = null;
        for (final RMContainer container : this.getLiveContainers()) {
            if (!this.getPreemptionContainers().contains(container) && (toBePreempted == null || this.comparator.compare(toBePreempted, container) > 0)) {
                toBePreempted = container;
            }
        }
        return toBePreempted;
    }
    
    static {
        LOG = LogFactory.getLog(FSAppAttempt.class);
        RESOURCE_CALCULATOR = new DefaultResourceCalculator();
    }
    
    static class RMContainerComparator implements Comparator<RMContainer>, Serializable
    {
        @Override
        public int compare(final RMContainer c1, final RMContainer c2) {
            final int ret = c1.getContainer().getPriority().compareTo(c2.getContainer().getPriority());
            if (ret == 0) {
                return c2.getContainerId().compareTo(c1.getContainerId());
            }
            return ret;
        }
    }
}
