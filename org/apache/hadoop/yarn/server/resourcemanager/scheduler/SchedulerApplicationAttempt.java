// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AggregateAppResourceUsage;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeCleanContainerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerReservedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import java.util.HashSet;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import org.apache.hadoop.yarn.util.resource.Resources;
import com.google.common.collect.HashMultiset;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import java.util.Set;
import java.util.List;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.Resource;
import com.google.common.collect.Multiset;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SchedulerApplicationAttempt
{
    private static final Log LOG;
    private static final long MEM_AGGREGATE_ALLOCATION_CACHE_MSECS = 3000L;
    protected long lastMemoryAggregateAllocationUpdateTime;
    private long lastMemorySeconds;
    private long lastVcoreSeconds;
    protected final AppSchedulingInfo appSchedulingInfo;
    protected Map<ContainerId, RMContainer> liveContainers;
    protected final Map<Priority, Map<NodeId, RMContainer>> reservedContainers;
    private final Multiset<Priority> reReservations;
    protected final Resource currentReservation;
    private Resource resourceLimit;
    protected Resource currentConsumption;
    private Resource amResource;
    private boolean unmanagedAM;
    private boolean amRunning;
    private LogAggregationContext logAggregationContext;
    protected List<RMContainer> newlyAllocatedContainers;
    private Set<ContainerId> pendingRelease;
    Multiset<Priority> schedulingOpportunities;
    protected Map<Priority, Long> lastScheduledContainer;
    protected Queue queue;
    protected boolean isStopped;
    protected final RMContext rmContext;
    
    public SchedulerApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final String user, final Queue queue, final ActiveUsersManager activeUsersManager, final RMContext rmContext) {
        this.lastMemoryAggregateAllocationUpdateTime = 0L;
        this.lastMemorySeconds = 0L;
        this.lastVcoreSeconds = 0L;
        this.liveContainers = new HashMap<ContainerId, RMContainer>();
        this.reservedContainers = new HashMap<Priority, Map<NodeId, RMContainer>>();
        this.reReservations = (Multiset<Priority>)HashMultiset.create();
        this.currentReservation = Resource.newInstance(0, 0);
        this.resourceLimit = Resource.newInstance(0, 0);
        this.currentConsumption = Resource.newInstance(0, 0);
        this.amResource = Resources.none();
        this.unmanagedAM = true;
        this.amRunning = false;
        this.newlyAllocatedContainers = new ArrayList<RMContainer>();
        this.pendingRelease = null;
        this.schedulingOpportunities = (Multiset<Priority>)HashMultiset.create();
        this.lastScheduledContainer = new HashMap<Priority, Long>();
        this.isStopped = false;
        Preconditions.checkNotNull("RMContext should not be null", rmContext);
        this.rmContext = rmContext;
        this.appSchedulingInfo = new AppSchedulingInfo(applicationAttemptId, user, queue, activeUsersManager, rmContext.getEpoch());
        this.queue = queue;
        this.pendingRelease = new HashSet<ContainerId>();
        if (rmContext.getRMApps() != null && rmContext.getRMApps().containsKey(applicationAttemptId.getApplicationId())) {
            final ApplicationSubmissionContext appSubmissionContext = rmContext.getRMApps().get(applicationAttemptId.getApplicationId()).getApplicationSubmissionContext();
            if (appSubmissionContext != null) {
                this.unmanagedAM = appSubmissionContext.getUnmanagedAM();
                this.logAggregationContext = appSubmissionContext.getLogAggregationContext();
            }
        }
    }
    
    public synchronized Collection<RMContainer> getLiveContainers() {
        return new ArrayList<RMContainer>(this.liveContainers.values());
    }
    
    public boolean isPending() {
        return this.appSchedulingInfo.isPending();
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appSchedulingInfo.getApplicationAttemptId();
    }
    
    public ApplicationId getApplicationId() {
        return this.appSchedulingInfo.getApplicationId();
    }
    
    public String getUser() {
        return this.appSchedulingInfo.getUser();
    }
    
    public Map<String, ResourceRequest> getResourceRequests(final Priority priority) {
        return this.appSchedulingInfo.getResourceRequests(priority);
    }
    
    public Set<ContainerId> getPendingRelease() {
        return this.pendingRelease;
    }
    
    public long getNewContainerId() {
        return this.appSchedulingInfo.getNewContainerId();
    }
    
    public Collection<Priority> getPriorities() {
        return this.appSchedulingInfo.getPriorities();
    }
    
    public synchronized ResourceRequest getResourceRequest(final Priority priority, final String resourceName) {
        return this.appSchedulingInfo.getResourceRequest(priority, resourceName);
    }
    
    public synchronized int getTotalRequiredResources(final Priority priority) {
        return this.getResourceRequest(priority, "*").getNumContainers();
    }
    
    public synchronized Resource getResource(final Priority priority) {
        return this.appSchedulingInfo.getResource(priority);
    }
    
    public String getQueueName() {
        return this.appSchedulingInfo.getQueueName();
    }
    
    public Resource getAMResource() {
        return this.amResource;
    }
    
    public void setAMResource(final Resource amResource) {
        this.amResource = amResource;
    }
    
    public boolean isAmRunning() {
        return this.amRunning;
    }
    
    public void setAmRunning(final boolean bool) {
        this.amRunning = bool;
    }
    
    public boolean getUnmanagedAM() {
        return this.unmanagedAM;
    }
    
    public synchronized RMContainer getRMContainer(final ContainerId id) {
        return this.liveContainers.get(id);
    }
    
    protected synchronized void resetReReservations(final Priority priority) {
        this.reReservations.setCount(priority, 0);
    }
    
    protected synchronized void addReReservation(final Priority priority) {
        this.reReservations.add(priority);
    }
    
    public synchronized int getReReservations(final Priority priority) {
        return this.reReservations.count(priority);
    }
    
    @InterfaceStability.Stable
    @InterfaceAudience.Private
    public synchronized Resource getCurrentReservation() {
        return this.currentReservation;
    }
    
    public Queue getQueue() {
        return this.queue;
    }
    
    public synchronized void updateResourceRequests(final List<ResourceRequest> requests) {
        if (!this.isStopped) {
            this.appSchedulingInfo.updateResourceRequests(requests, false);
        }
    }
    
    public synchronized void recoverResourceRequests(final List<ResourceRequest> requests) {
        if (!this.isStopped) {
            this.appSchedulingInfo.updateResourceRequests(requests, true);
        }
    }
    
    public synchronized void stop(final RMAppAttemptState rmAppAttemptFinalState) {
        this.isStopped = true;
        this.appSchedulingInfo.stop(rmAppAttemptFinalState);
    }
    
    public synchronized boolean isStopped() {
        return this.isStopped;
    }
    
    public synchronized List<RMContainer> getReservedContainers() {
        final List<RMContainer> reservedContainers = new ArrayList<RMContainer>();
        for (final Map.Entry<Priority, Map<NodeId, RMContainer>> e : this.reservedContainers.entrySet()) {
            reservedContainers.addAll(e.getValue().values());
        }
        return reservedContainers;
    }
    
    public synchronized RMContainer reserve(final SchedulerNode node, final Priority priority, RMContainer rmContainer, final Container container) {
        if (rmContainer == null) {
            rmContainer = new RMContainerImpl(container, this.getApplicationAttemptId(), node.getNodeID(), this.appSchedulingInfo.getUser(), this.rmContext);
            Resources.addTo(this.currentReservation, container.getResource());
            this.resetReReservations(priority);
        }
        else {
            this.addReReservation(priority);
        }
        ((EventHandler<RMContainerReservedEvent>)rmContainer).handle(new RMContainerReservedEvent(container.getId(), container.getResource(), node.getNodeID(), priority));
        Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        if (reservedContainers == null) {
            reservedContainers = new HashMap<NodeId, RMContainer>();
            this.reservedContainers.put(priority, reservedContainers);
        }
        reservedContainers.put(node.getNodeID(), rmContainer);
        if (SchedulerApplicationAttempt.LOG.isDebugEnabled()) {
            SchedulerApplicationAttempt.LOG.debug("Application attempt " + this.getApplicationAttemptId() + " reserved container " + rmContainer + " on node " + node + ". This attempt currently has " + reservedContainers.size() + " reserved containers at priority " + priority + "; currentReservation " + this.currentReservation.getMemory());
        }
        return rmContainer;
    }
    
    public synchronized boolean isReserved(final SchedulerNode node, final Priority priority) {
        final Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        return reservedContainers != null && reservedContainers.containsKey(node.getNodeID());
    }
    
    public synchronized void setHeadroom(final Resource globalLimit) {
        this.resourceLimit = globalLimit;
    }
    
    public synchronized Resource getHeadroom() {
        if (this.resourceLimit.getMemory() < 0) {
            this.resourceLimit.setMemory(0);
        }
        return this.resourceLimit;
    }
    
    public synchronized int getNumReservedContainers(final Priority priority) {
        final Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        return (reservedContainers == null) ? 0 : reservedContainers.size();
    }
    
    public synchronized void containerLaunchedOnNode(final ContainerId containerId, final NodeId nodeId) {
        final RMContainer rmContainer = this.getRMContainer(containerId);
        if (rmContainer == null) {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeCleanContainerEvent(nodeId, containerId));
            return;
        }
        rmContainer.handle(new RMContainerEvent(containerId, RMContainerEventType.LAUNCHED));
    }
    
    public synchronized void showRequests() {
        if (SchedulerApplicationAttempt.LOG.isDebugEnabled()) {
            for (final Priority priority : this.getPriorities()) {
                final Map<String, ResourceRequest> requests = this.getResourceRequests(priority);
                if (requests != null) {
                    SchedulerApplicationAttempt.LOG.debug("showRequests: application=" + this.getApplicationId() + " headRoom=" + this.getHeadroom() + " currentConsumption=" + this.currentConsumption.getMemory());
                    for (final ResourceRequest request : requests.values()) {
                        SchedulerApplicationAttempt.LOG.debug("showRequests: application=" + this.getApplicationId() + " request=" + request);
                    }
                }
            }
        }
    }
    
    public Resource getCurrentConsumption() {
        return this.currentConsumption;
    }
    
    public synchronized ContainersAndNMTokensAllocation pullNewlyAllocatedContainersAndNMTokens() {
        final List<Container> returnContainerList = new ArrayList<Container>(this.newlyAllocatedContainers.size());
        final List<NMToken> nmTokens = new ArrayList<NMToken>();
        final Iterator<RMContainer> i = this.newlyAllocatedContainers.iterator();
        while (i.hasNext()) {
            final RMContainer rmContainer = i.next();
            final Container container = rmContainer.getContainer();
            try {
                container.setContainerToken(this.rmContext.getContainerTokenSecretManager().createContainerToken(container.getId(), container.getNodeId(), this.getUser(), container.getResource(), container.getPriority(), rmContainer.getCreationTime(), this.logAggregationContext));
                final NMToken nmToken = this.rmContext.getNMTokenSecretManager().createAndGetNMToken(this.getUser(), this.getApplicationAttemptId(), container);
                if (nmToken != null) {
                    nmTokens.add(nmToken);
                }
            }
            catch (IllegalArgumentException e) {
                SchedulerApplicationAttempt.LOG.error("Error trying to assign container token and NM token to an allocated container " + container.getId(), e);
                continue;
            }
            returnContainerList.add(container);
            i.remove();
            rmContainer.handle(new RMContainerEvent(rmContainer.getContainerId(), RMContainerEventType.ACQUIRED));
        }
        return new ContainersAndNMTokensAllocation(returnContainerList, nmTokens);
    }
    
    public synchronized void updateBlacklist(final List<String> blacklistAdditions, final List<String> blacklistRemovals) {
        if (!this.isStopped) {
            this.appSchedulingInfo.updateBlacklist(blacklistAdditions, blacklistRemovals);
        }
    }
    
    public boolean isBlacklisted(final String resourceName) {
        return this.appSchedulingInfo.isBlacklisted(resourceName);
    }
    
    public synchronized void addSchedulingOpportunity(final Priority priority) {
        this.schedulingOpportunities.setCount(priority, this.schedulingOpportunities.count(priority) + 1);
    }
    
    public synchronized void subtractSchedulingOpportunity(final Priority priority) {
        final int count = this.schedulingOpportunities.count(priority) - 1;
        this.schedulingOpportunities.setCount(priority, Math.max(count, 0));
    }
    
    public synchronized int getSchedulingOpportunities(final Priority priority) {
        return this.schedulingOpportunities.count(priority);
    }
    
    public synchronized void resetSchedulingOpportunities(final Priority priority) {
        this.resetSchedulingOpportunities(priority, System.currentTimeMillis());
    }
    
    public synchronized void resetSchedulingOpportunities(final Priority priority, final long currentTimeMs) {
        this.lastScheduledContainer.put(priority, currentTimeMs);
        this.schedulingOpportunities.setCount(priority, 0);
    }
    
    synchronized AggregateAppResourceUsage getRunningAggregateAppResourceUsage() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastMemoryAggregateAllocationUpdateTime > 3000L) {
            long memorySeconds = 0L;
            long vcoreSeconds = 0L;
            for (final RMContainer rmContainer : this.liveContainers.values()) {
                final long usedMillis = currentTimeMillis - rmContainer.getCreationTime();
                final Resource resource = rmContainer.getContainer().getResource();
                memorySeconds += resource.getMemory() * usedMillis / 1000L;
                vcoreSeconds += resource.getVirtualCores() * usedMillis / 1000L;
            }
            this.lastMemoryAggregateAllocationUpdateTime = currentTimeMillis;
            this.lastMemorySeconds = memorySeconds;
            this.lastVcoreSeconds = vcoreSeconds;
        }
        return new AggregateAppResourceUsage(this.lastMemorySeconds, this.lastVcoreSeconds);
    }
    
    public synchronized ApplicationResourceUsageReport getResourceUsageReport() {
        final AggregateAppResourceUsage resUsage = this.getRunningAggregateAppResourceUsage();
        return ApplicationResourceUsageReport.newInstance(this.liveContainers.size(), this.reservedContainers.size(), Resources.clone(this.currentConsumption), Resources.clone(this.currentReservation), Resources.add(this.currentConsumption, this.currentReservation), resUsage.getMemorySeconds(), resUsage.getVcoreSeconds());
    }
    
    public synchronized Map<ContainerId, RMContainer> getLiveContainersMap() {
        return this.liveContainers;
    }
    
    public synchronized Resource getResourceLimit() {
        return this.resourceLimit;
    }
    
    public synchronized Map<Priority, Long> getLastScheduledContainer() {
        return this.lastScheduledContainer;
    }
    
    public synchronized void transferStateFromPreviousAttempt(final SchedulerApplicationAttempt appAttempt) {
        this.liveContainers = appAttempt.getLiveContainersMap();
        this.currentConsumption = appAttempt.getCurrentConsumption();
        this.resourceLimit = appAttempt.getResourceLimit();
        this.lastScheduledContainer = appAttempt.getLastScheduledContainer();
        this.appSchedulingInfo.transferStateFromPreviousAppSchedulingInfo(appAttempt.appSchedulingInfo);
    }
    
    public synchronized void move(final Queue newQueue) {
        final QueueMetrics oldMetrics = this.queue.getMetrics();
        final QueueMetrics newMetrics = newQueue.getMetrics();
        final String user = this.getUser();
        for (final RMContainer liveContainer : this.liveContainers.values()) {
            final Resource resource = liveContainer.getContainer().getResource();
            oldMetrics.releaseResources(user, 1, resource);
            newMetrics.allocateResources(user, 1, resource, false);
        }
        for (final Map<NodeId, RMContainer> map : this.reservedContainers.values()) {
            for (final RMContainer reservedContainer : map.values()) {
                final Resource resource2 = reservedContainer.getReservedResource();
                oldMetrics.unreserveResource(user, resource2);
                newMetrics.reserveResource(user, resource2);
            }
        }
        this.appSchedulingInfo.move(newQueue);
        this.queue = newQueue;
    }
    
    public synchronized void recoverContainer(final RMContainer rmContainer) {
        this.appSchedulingInfo.recoverContainer(rmContainer);
        if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
            return;
        }
        SchedulerApplicationAttempt.LOG.info("SchedulerAttempt " + this.getApplicationAttemptId() + " is recovering container " + rmContainer.getContainerId());
        this.liveContainers.put(rmContainer.getContainerId(), rmContainer);
        Resources.addTo(this.currentConsumption, rmContainer.getContainer().getResource());
    }
    
    static {
        LOG = LogFactory.getLog(SchedulerApplicationAttempt.class);
    }
    
    public static class ContainersAndNMTokensAllocation
    {
        List<Container> containerList;
        List<NMToken> nmTokenList;
        
        public ContainersAndNMTokensAllocation(final List<Container> containerList, final List<NMToken> nmTokenList) {
            this.containerList = containerList;
            this.nmTokenList = nmTokenList;
        }
        
        public List<Container> getContainerList() {
            return this.containerList;
        }
        
        public List<NMToken> getNMTokenList() {
            return this.nmTokenList;
        }
    }
}
