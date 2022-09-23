// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica;

import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.RMAuditLogger;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.HashSet;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityHeadroomProvider;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FiCaSchedulerApp extends SchedulerApplicationAttempt
{
    private static final Log LOG;
    private final Set<ContainerId> containersToPreempt;
    private CapacityHeadroomProvider headroomProvider;
    
    public FiCaSchedulerApp(final ApplicationAttemptId applicationAttemptId, final String user, final Queue queue, final ActiveUsersManager activeUsersManager, final RMContext rmContext) {
        super(applicationAttemptId, user, queue, activeUsersManager, rmContext);
        this.containersToPreempt = new HashSet<ContainerId>();
    }
    
    public synchronized boolean containerCompleted(final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event) {
        if (null == this.liveContainers.remove(rmContainer.getContainerId())) {
            return false;
        }
        this.newlyAllocatedContainers.remove(rmContainer);
        final Container container = rmContainer.getContainer();
        final ContainerId containerId = container.getId();
        ((EventHandler<RMContainerFinishedEvent>)rmContainer).handle(new RMContainerFinishedEvent(containerId, containerStatus, event));
        FiCaSchedulerApp.LOG.info("Completed container: " + rmContainer.getContainerId() + " in state: " + rmContainer.getState() + " event:" + event);
        this.containersToPreempt.remove(rmContainer.getContainerId());
        RMAuditLogger.logSuccess(this.getUser(), "AM Released Container", "SchedulerApp", this.getApplicationId(), containerId);
        final Resource containerResource = rmContainer.getContainer().getResource();
        this.queue.getMetrics().releaseResources(this.getUser(), 1, containerResource);
        Resources.subtractFrom(this.currentConsumption, containerResource);
        this.lastMemoryAggregateAllocationUpdateTime = -1L;
        return true;
    }
    
    public synchronized RMContainer allocate(final NodeType type, final FiCaSchedulerNode node, final Priority priority, final ResourceRequest request, final Container container) {
        if (this.isStopped) {
            return null;
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
        if (FiCaSchedulerApp.LOG.isDebugEnabled()) {
            FiCaSchedulerApp.LOG.debug("allocate: applicationAttemptId=" + container.getId().getApplicationAttemptId() + " container=" + container.getId() + " host=" + container.getNodeId().getHost() + " type=" + type);
        }
        RMAuditLogger.logSuccess(this.getUser(), "AM Allocated Container", "SchedulerApp", this.getApplicationId(), container.getId());
        return rmContainer;
    }
    
    public synchronized boolean unreserve(final FiCaSchedulerNode node, final Priority priority) {
        final Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        if (reservedContainers != null) {
            final RMContainer reservedContainer = reservedContainers.remove(node.getNodeID());
            if (reservedContainer != null && reservedContainer.getContainer() != null && reservedContainer.getContainer().getResource() != null) {
                if (reservedContainers.isEmpty()) {
                    this.reservedContainers.remove(priority);
                }
                this.resetReReservations(priority);
                final Resource resource = reservedContainer.getContainer().getResource();
                Resources.subtractFrom(this.currentReservation, resource);
                FiCaSchedulerApp.LOG.info("Application " + this.getApplicationId() + " unreserved " + " on node " + node + ", currently has " + reservedContainers.size() + " at priority " + priority + "; currentReservation " + this.currentReservation);
                return true;
            }
        }
        return false;
    }
    
    public synchronized float getLocalityWaitFactor(final Priority priority, final int clusterNodes) {
        final int requiredResources = Math.max(this.getResourceRequests(priority).size() - 1, 0);
        return Math.min(requiredResources / (float)clusterNodes, 1.0f);
    }
    
    public synchronized Resource getTotalPendingRequests() {
        final Resource ret = Resource.newInstance(0, 0);
        for (final ResourceRequest rr : this.appSchedulingInfo.getAllResourceRequests()) {
            if (ResourceRequest.isAnyLocation(rr.getResourceName())) {
                Resources.addTo(ret, Resources.multiply(rr.getCapability(), rr.getNumContainers()));
            }
        }
        return ret;
    }
    
    public synchronized void addPreemptContainer(final ContainerId cont) {
        if (this.liveContainers.containsKey(cont)) {
            this.containersToPreempt.add(cont);
        }
    }
    
    public synchronized Allocation getAllocation(final ResourceCalculator rc, final Resource clusterResource, final Resource minimumAllocation) {
        final Set<ContainerId> currentContPreemption = Collections.unmodifiableSet((Set<? extends ContainerId>)new HashSet<ContainerId>(this.containersToPreempt));
        this.containersToPreempt.clear();
        final Resource tot = Resource.newInstance(0, 0);
        for (final ContainerId c : currentContPreemption) {
            Resources.addTo(tot, this.liveContainers.get(c).getContainer().getResource());
        }
        final int numCont = (int)Math.ceil(Resources.divide(rc, clusterResource, tot, minimumAllocation));
        final ResourceRequest rr = ResourceRequest.newInstance(Priority.UNDEFINED, "*", minimumAllocation, numCont);
        final ContainersAndNMTokensAllocation allocation = this.pullNewlyAllocatedContainersAndNMTokens();
        return new Allocation(allocation.getContainerList(), this.getHeadroom(), null, currentContPreemption, Collections.singletonList(rr), allocation.getNMTokenList());
    }
    
    public synchronized NodeId getNodeIdToUnreserve(final Priority priority, final Resource capability) {
        final Map<NodeId, RMContainer> reservedContainers = this.reservedContainers.get(priority);
        if (reservedContainers != null && !reservedContainers.isEmpty()) {
            for (final Map.Entry<NodeId, RMContainer> entry : reservedContainers.entrySet()) {
                if (Resources.fitsIn(capability, entry.getValue().getContainer().getResource())) {
                    if (FiCaSchedulerApp.LOG.isDebugEnabled()) {
                        FiCaSchedulerApp.LOG.debug("unreserving node with reservation size: " + entry.getValue().getContainer().getResource() + " in order to allocate container with size: " + capability);
                    }
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    public synchronized void setHeadroomProvider(final CapacityHeadroomProvider headroomProvider) {
        this.headroomProvider = headroomProvider;
    }
    
    public synchronized CapacityHeadroomProvider getHeadroomProvider() {
        return this.headroomProvider;
    }
    
    @Override
    public synchronized Resource getHeadroom() {
        if (this.headroomProvider != null) {
            return this.headroomProvider.getHeadroom();
        }
        return super.getHeadroom();
    }
    
    @Override
    public synchronized void transferStateFromPreviousAttempt(final SchedulerApplicationAttempt appAttempt) {
        super.transferStateFromPreviousAttempt(appAttempt);
        this.headroomProvider = ((FiCaSchedulerApp)appAttempt).getHeadroomProvider();
    }
    
    static {
        LOG = LogFactory.getLog(FiCaSchedulerApp.class);
    }
}
