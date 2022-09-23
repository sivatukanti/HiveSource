// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.TreeSet;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.Priority;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AppSchedulingInfo
{
    private static final Log LOG;
    private final ApplicationAttemptId applicationAttemptId;
    final ApplicationId applicationId;
    private String queueName;
    Queue queue;
    final String user;
    private final AtomicLong containerIdCounter;
    private final int EPOCH_BIT_SHIFT = 40;
    final Set<Priority> priorities;
    final Map<Priority, Map<String, ResourceRequest>> requests;
    private Set<String> blacklist;
    private ActiveUsersManager activeUsersManager;
    boolean pending;
    
    public AppSchedulingInfo(final ApplicationAttemptId appAttemptId, final String user, final Queue queue, final ActiveUsersManager activeUsersManager, final long epoch) {
        this.priorities = new TreeSet<Priority>(new org.apache.hadoop.yarn.server.resourcemanager.resource.Priority.Comparator());
        this.requests = new HashMap<Priority, Map<String, ResourceRequest>>();
        this.blacklist = new HashSet<String>();
        this.pending = true;
        this.applicationAttemptId = appAttemptId;
        this.applicationId = appAttemptId.getApplicationId();
        this.queue = queue;
        this.queueName = queue.getQueueName();
        this.user = user;
        this.activeUsersManager = activeUsersManager;
        this.containerIdCounter = new AtomicLong(epoch << 40);
    }
    
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.applicationAttemptId;
    }
    
    public String getQueueName() {
        return this.queueName;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public synchronized boolean isPending() {
        return this.pending;
    }
    
    private synchronized void clearRequests() {
        this.priorities.clear();
        this.requests.clear();
        AppSchedulingInfo.LOG.info("Application " + this.applicationId + " requests cleared");
    }
    
    public long getNewContainerId() {
        return this.containerIdCounter.incrementAndGet();
    }
    
    public synchronized void updateResourceRequests(final List<ResourceRequest> requests, final boolean recoverPreemptedRequest) {
        final QueueMetrics metrics = this.queue.getMetrics();
        for (final ResourceRequest request : requests) {
            final Priority priority = request.getPriority();
            final String resourceName = request.getResourceName();
            boolean updatePendingResources = false;
            ResourceRequest lastRequest = null;
            if (resourceName.equals("*")) {
                if (AppSchedulingInfo.LOG.isDebugEnabled()) {
                    AppSchedulingInfo.LOG.debug("update: application=" + this.applicationId + " request=" + request);
                }
                updatePendingResources = true;
                if (request.getNumContainers() > 0) {
                    this.activeUsersManager.activateApplication(this.user, this.applicationId);
                }
            }
            Map<String, ResourceRequest> asks = this.requests.get(priority);
            if (asks == null) {
                asks = new HashMap<String, ResourceRequest>();
                this.requests.put(priority, asks);
                this.priorities.add(priority);
            }
            lastRequest = asks.get(resourceName);
            if (recoverPreemptedRequest && lastRequest != null) {
                request.setNumContainers(lastRequest.getNumContainers() + 1);
            }
            asks.put(resourceName, request);
            if (updatePendingResources) {
                if (request.getNumContainers() <= 0) {
                    AppSchedulingInfo.LOG.info("checking for deactivate... ");
                    this.checkForDeactivation();
                }
                final int lastRequestContainers = (lastRequest != null) ? lastRequest.getNumContainers() : 0;
                final Resource lastRequestCapability = (lastRequest != null) ? lastRequest.getCapability() : Resources.none();
                metrics.incrPendingResources(this.user, request.getNumContainers(), request.getCapability());
                metrics.decrPendingResources(this.user, lastRequestContainers, lastRequestCapability);
            }
        }
    }
    
    public synchronized void updateBlacklist(final List<String> blacklistAdditions, final List<String> blacklistRemovals) {
        if (blacklistAdditions != null) {
            this.blacklist.addAll(blacklistAdditions);
        }
        if (blacklistRemovals != null) {
            this.blacklist.removeAll(blacklistRemovals);
        }
    }
    
    public synchronized Collection<Priority> getPriorities() {
        return this.priorities;
    }
    
    public synchronized Map<String, ResourceRequest> getResourceRequests(final Priority priority) {
        return this.requests.get(priority);
    }
    
    public synchronized List<ResourceRequest> getAllResourceRequests() {
        final List<ResourceRequest> ret = new ArrayList<ResourceRequest>();
        for (final Map<String, ResourceRequest> r : this.requests.values()) {
            ret.addAll(r.values());
        }
        return ret;
    }
    
    public synchronized ResourceRequest getResourceRequest(final Priority priority, final String resourceName) {
        final Map<String, ResourceRequest> nodeRequests = this.requests.get(priority);
        return (nodeRequests == null) ? null : nodeRequests.get(resourceName);
    }
    
    public synchronized Resource getResource(final Priority priority) {
        final ResourceRequest request = this.getResourceRequest(priority, "*");
        return request.getCapability();
    }
    
    public synchronized boolean isBlacklisted(final String resourceName) {
        return this.blacklist.contains(resourceName);
    }
    
    public synchronized List<ResourceRequest> allocate(final NodeType type, final SchedulerNode node, final Priority priority, final ResourceRequest request, final Container container) {
        final List<ResourceRequest> resourceRequests = new ArrayList<ResourceRequest>();
        if (type == NodeType.NODE_LOCAL) {
            this.allocateNodeLocal(node, priority, request, container, resourceRequests);
        }
        else if (type == NodeType.RACK_LOCAL) {
            this.allocateRackLocal(node, priority, request, container, resourceRequests);
        }
        else {
            this.allocateOffSwitch(node, priority, request, container, resourceRequests);
        }
        final QueueMetrics metrics = this.queue.getMetrics();
        if (this.pending) {
            this.pending = false;
            metrics.runAppAttempt(this.applicationId, this.user);
        }
        if (AppSchedulingInfo.LOG.isDebugEnabled()) {
            AppSchedulingInfo.LOG.debug("allocate: applicationId=" + this.applicationId + " container=" + container.getId() + " host=" + container.getNodeId().toString() + " user=" + this.user + " resource=" + request.getCapability());
        }
        metrics.allocateResources(this.user, 1, request.getCapability(), true);
        return resourceRequests;
    }
    
    private synchronized void allocateNodeLocal(final SchedulerNode node, final Priority priority, final ResourceRequest nodeLocalRequest, final Container container, final List<ResourceRequest> resourceRequests) {
        nodeLocalRequest.setNumContainers(nodeLocalRequest.getNumContainers() - 1);
        if (nodeLocalRequest.getNumContainers() == 0) {
            this.requests.get(priority).remove(node.getNodeName());
        }
        final ResourceRequest rackLocalRequest = this.requests.get(priority).get(node.getRackName());
        rackLocalRequest.setNumContainers(rackLocalRequest.getNumContainers() - 1);
        if (rackLocalRequest.getNumContainers() == 0) {
            this.requests.get(priority).remove(node.getRackName());
        }
        final ResourceRequest offRackRequest = this.requests.get(priority).get("*");
        this.decrementOutstanding(offRackRequest);
        resourceRequests.add(this.cloneResourceRequest(nodeLocalRequest));
        resourceRequests.add(this.cloneResourceRequest(rackLocalRequest));
        resourceRequests.add(this.cloneResourceRequest(offRackRequest));
    }
    
    private synchronized void allocateRackLocal(final SchedulerNode node, final Priority priority, final ResourceRequest rackLocalRequest, final Container container, final List<ResourceRequest> resourceRequests) {
        rackLocalRequest.setNumContainers(rackLocalRequest.getNumContainers() - 1);
        if (rackLocalRequest.getNumContainers() == 0) {
            this.requests.get(priority).remove(node.getRackName());
        }
        final ResourceRequest offRackRequest = this.requests.get(priority).get("*");
        this.decrementOutstanding(offRackRequest);
        resourceRequests.add(this.cloneResourceRequest(rackLocalRequest));
        resourceRequests.add(this.cloneResourceRequest(offRackRequest));
    }
    
    private synchronized void allocateOffSwitch(final SchedulerNode node, final Priority priority, final ResourceRequest offSwitchRequest, final Container container, final List<ResourceRequest> resourceRequests) {
        this.decrementOutstanding(offSwitchRequest);
        resourceRequests.add(this.cloneResourceRequest(offSwitchRequest));
    }
    
    private synchronized void decrementOutstanding(final ResourceRequest offSwitchRequest) {
        final int numOffSwitchContainers = offSwitchRequest.getNumContainers() - 1;
        offSwitchRequest.setNumContainers(numOffSwitchContainers);
        if (numOffSwitchContainers == 0) {
            this.checkForDeactivation();
        }
    }
    
    private synchronized void checkForDeactivation() {
        boolean deactivate = true;
        for (final Priority priority : this.getPriorities()) {
            final ResourceRequest request = this.getResourceRequest(priority, "*");
            if (request.getNumContainers() > 0) {
                deactivate = false;
                break;
            }
        }
        if (deactivate) {
            this.activeUsersManager.deactivateApplication(this.user, this.applicationId);
        }
    }
    
    public synchronized void move(final Queue newQueue) {
        final QueueMetrics oldMetrics = this.queue.getMetrics();
        final QueueMetrics newMetrics = newQueue.getMetrics();
        for (final Map<String, ResourceRequest> asks : this.requests.values()) {
            final ResourceRequest request = asks.get("*");
            if (request != null) {
                oldMetrics.decrPendingResources(this.user, request.getNumContainers(), request.getCapability());
                newMetrics.incrPendingResources(this.user, request.getNumContainers(), request.getCapability());
            }
        }
        oldMetrics.moveAppFrom(this);
        newMetrics.moveAppTo(this);
        this.activeUsersManager.deactivateApplication(this.user, this.applicationId);
        (this.activeUsersManager = newQueue.getActiveUsersManager()).activateApplication(this.user, this.applicationId);
        this.queue = newQueue;
        this.queueName = newQueue.getQueueName();
    }
    
    public synchronized void stop(final RMAppAttemptState rmAppAttemptFinalState) {
        final QueueMetrics metrics = this.queue.getMetrics();
        for (final Map<String, ResourceRequest> asks : this.requests.values()) {
            final ResourceRequest request = asks.get("*");
            if (request != null) {
                metrics.decrPendingResources(this.user, request.getNumContainers(), request.getCapability());
            }
        }
        metrics.finishAppAttempt(this.applicationId, this.pending, this.user);
        this.clearRequests();
    }
    
    public synchronized void setQueue(final Queue queue) {
        this.queue = queue;
    }
    
    public synchronized Set<String> getBlackList() {
        return this.blacklist;
    }
    
    public synchronized void transferStateFromPreviousAppSchedulingInfo(final AppSchedulingInfo appInfo) {
        this.blacklist = appInfo.getBlackList();
    }
    
    public synchronized void recoverContainer(final RMContainer rmContainer) {
        final QueueMetrics metrics = this.queue.getMetrics();
        if (this.pending) {
            this.pending = false;
            metrics.runAppAttempt(this.applicationId, this.user);
        }
        if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
            return;
        }
        metrics.allocateResources(this.user, 1, rmContainer.getAllocatedResource(), false);
    }
    
    public ResourceRequest cloneResourceRequest(final ResourceRequest request) {
        final ResourceRequest newRequest = ResourceRequest.newInstance(request.getPriority(), request.getResourceName(), request.getCapability(), 1, request.getRelaxLocality());
        return newRequest;
    }
    
    static {
        LOG = LogFactory.getLog(AppSchedulingInfo.class);
    }
}
