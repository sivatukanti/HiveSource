// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.commons.lang.StringUtils;
import java.util.HashSet;
import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Collection;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Resource;
import java.io.IOException;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.util.TreeSet;
import java.util.List;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.factories.RecordFactory;
import java.util.Comparator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ParentQueue extends AbstractCSQueue
{
    private static final Log LOG;
    protected final Set<CSQueue> childQueues;
    private final boolean rootQueue;
    final Comparator<CSQueue> queueComparator;
    volatile int numApplications;
    private final RecordFactory recordFactory;
    private static float PRECISION;
    
    public ParentQueue(final CapacitySchedulerContext cs, final String queueName, final CSQueue parent, final CSQueue old) throws IOException {
        super(cs, queueName, parent, old);
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.queueComparator = cs.getQueueComparator();
        this.rootQueue = (parent == null);
        final float rawCapacity = cs.getConfiguration().getCapacity(this.getQueuePath());
        if (this.rootQueue && rawCapacity != 100.0f) {
            throw new IllegalArgumentException("Illegal capacity of " + rawCapacity + " for queue " + queueName + ". Must be " + 100.0f);
        }
        final float capacity = rawCapacity / 100.0f;
        final float parentAbsoluteCapacity = this.rootQueue ? 1.0f : parent.getAbsoluteCapacity();
        final float absoluteCapacity = parentAbsoluteCapacity * capacity;
        final float maximumCapacity = cs.getConfiguration().getMaximumCapacity(this.getQueuePath()) / 100.0f;
        final float absoluteMaxCapacity = CSQueueUtils.computeAbsoluteMaximumCapacity(maximumCapacity, parent);
        final QueueState state = cs.getConfiguration().getState(this.getQueuePath());
        final Map<QueueACL, AccessControlList> acls = cs.getConfiguration().getAcls(this.getQueuePath());
        this.queueInfo.setChildQueues(new ArrayList<QueueInfo>());
        this.setupQueueConfigs(cs.getClusterResource(), capacity, absoluteCapacity, maximumCapacity, absoluteMaxCapacity, state, acls, this.accessibleLabels, this.defaultLabelExpression, this.capacitiyByNodeLabels, this.maxCapacityByNodeLabels, cs.getConfiguration().getReservationContinueLook());
        this.childQueues = new TreeSet<CSQueue>(this.queueComparator);
        ParentQueue.LOG.info("Initialized parent-queue " + queueName + " name=" + queueName + ", fullname=" + this.getQueuePath());
    }
    
    @Override
    synchronized void setupQueueConfigs(final Resource clusterResource, final float capacity, final float absoluteCapacity, final float maximumCapacity, final float absoluteMaxCapacity, final QueueState state, final Map<QueueACL, AccessControlList> acls, final Set<String> accessibleLabels, final String defaultLabelExpression, final Map<String, Float> nodeLabelCapacities, final Map<String, Float> maximumCapacitiesByLabel, final boolean reservationContinueLooking) throws IOException {
        super.setupQueueConfigs(clusterResource, capacity, absoluteCapacity, maximumCapacity, absoluteMaxCapacity, state, acls, accessibleLabels, defaultLabelExpression, nodeLabelCapacities, maximumCapacitiesByLabel, reservationContinueLooking);
        final StringBuilder aclsString = new StringBuilder();
        for (final Map.Entry<QueueACL, AccessControlList> e : acls.entrySet()) {
            aclsString.append(e.getKey() + ":" + e.getValue().getAclString());
        }
        final StringBuilder labelStrBuilder = new StringBuilder();
        if (accessibleLabels != null) {
            for (final String s : accessibleLabels) {
                labelStrBuilder.append(s);
                labelStrBuilder.append(",");
            }
        }
        ParentQueue.LOG.info(this.queueName + ", capacity=" + capacity + ", asboluteCapacity=" + absoluteCapacity + ", maxCapacity=" + maximumCapacity + ", asboluteMaxCapacity=" + absoluteMaxCapacity + ", state=" + state + ", acls=" + (Object)aclsString + ", labels=" + labelStrBuilder.toString() + "\n" + ", reservationsContinueLooking=" + this.reservationsContinueLooking);
    }
    
    void setChildQueues(final Collection<CSQueue> childQueues) {
        float childCapacities = 0.0f;
        for (final CSQueue queue : childQueues) {
            childCapacities += queue.getCapacity();
        }
        final float delta = Math.abs(1.0f - childCapacities);
        if ((this.capacity > 0.0f && delta > ParentQueue.PRECISION) || (this.capacity == 0.0f && childCapacities > 0.0f)) {
            throw new IllegalArgumentException("Illegal capacity of " + childCapacities + " for children of queue " + this.queueName);
        }
        for (final String nodeLabel : this.labelManager.getClusterNodeLabels()) {
            final float capacityByLabel = this.getCapacityByNodeLabel(nodeLabel);
            float sum = 0.0f;
            for (final CSQueue queue2 : childQueues) {
                sum += queue2.getCapacityByNodeLabel(nodeLabel);
            }
            if ((capacityByLabel > 0.0f && Math.abs(1.0f - sum) > ParentQueue.PRECISION) || (capacityByLabel == 0.0f && sum > 0.0f)) {
                throw new IllegalArgumentException("Illegal capacity of " + sum + " for children of queue " + this.queueName + " for label=" + nodeLabel);
            }
        }
        this.childQueues.clear();
        this.childQueues.addAll(childQueues);
        if (ParentQueue.LOG.isDebugEnabled()) {
            ParentQueue.LOG.debug("setChildQueues: " + this.getChildQueuesToPrint());
        }
    }
    
    @Override
    public String getQueuePath() {
        final String parentPath = (this.parent == null) ? "" : (this.parent.getQueuePath() + ".");
        return parentPath + this.getQueueName();
    }
    
    @Override
    public synchronized QueueInfo getQueueInfo(final boolean includeChildQueues, final boolean recursive) {
        this.queueInfo.setCurrentCapacity(this.usedCapacity);
        final List<QueueInfo> childQueuesInfo = new ArrayList<QueueInfo>();
        if (includeChildQueues) {
            for (final CSQueue child : this.childQueues) {
                childQueuesInfo.add(child.getQueueInfo(recursive, recursive));
            }
        }
        this.queueInfo.setChildQueues(childQueuesInfo);
        return this.queueInfo;
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
        for (final CSQueue child : this.childQueues) {
            userAcls.addAll(child.getQueueUserAclInfo(user));
        }
        return userAcls;
    }
    
    @Override
    public String toString() {
        return this.queueName + ": " + "numChildQueue= " + this.childQueues.size() + ", " + "capacity=" + this.capacity + ", " + "absoluteCapacity=" + this.absoluteCapacity + ", " + "usedResources=" + this.usedResources + "usedCapacity=" + this.getUsedCapacity() + ", " + "numApps=" + this.getNumApplications() + ", " + "numContainers=" + this.getNumContainers();
    }
    
    @Override
    public synchronized void reinitialize(final CSQueue newlyParsedQueue, final Resource clusterResource) throws IOException {
        if (!(newlyParsedQueue instanceof ParentQueue) || !newlyParsedQueue.getQueuePath().equals(this.getQueuePath())) {
            throw new IOException("Trying to reinitialize " + this.getQueuePath() + " from " + newlyParsedQueue.getQueuePath());
        }
        final ParentQueue newlyParsedParentQueue = (ParentQueue)newlyParsedQueue;
        this.setupQueueConfigs(clusterResource, newlyParsedParentQueue.capacity, newlyParsedParentQueue.absoluteCapacity, newlyParsedParentQueue.maximumCapacity, newlyParsedParentQueue.absoluteMaxCapacity, newlyParsedParentQueue.state, newlyParsedParentQueue.acls, newlyParsedParentQueue.accessibleLabels, newlyParsedParentQueue.defaultLabelExpression, newlyParsedParentQueue.capacitiyByNodeLabels, newlyParsedParentQueue.maxCapacityByNodeLabels, newlyParsedParentQueue.reservationsContinueLooking);
        final Map<String, CSQueue> currentChildQueues = this.getQueues(this.childQueues);
        final Map<String, CSQueue> newChildQueues = this.getQueues(newlyParsedParentQueue.childQueues);
        for (final Map.Entry<String, CSQueue> e : newChildQueues.entrySet()) {
            final String newChildQueueName = e.getKey();
            final CSQueue newChildQueue = e.getValue();
            final CSQueue childQueue = currentChildQueues.get(newChildQueueName);
            if (childQueue != null) {
                childQueue.reinitialize(newChildQueue, clusterResource);
                ParentQueue.LOG.info(this.getQueueName() + ": re-configured queue: " + childQueue);
            }
            else {
                newChildQueue.setParent(this);
                currentChildQueues.put(newChildQueueName, newChildQueue);
                ParentQueue.LOG.info(this.getQueueName() + ": added new child queue: " + newChildQueue);
            }
        }
        this.childQueues.clear();
        this.childQueues.addAll(currentChildQueues.values());
    }
    
    Map<String, CSQueue> getQueues(final Set<CSQueue> queues) {
        final Map<String, CSQueue> queuesMap = new HashMap<String, CSQueue>();
        for (final CSQueue queue : queues) {
            queuesMap.put(queue.getQueueName(), queue);
        }
        return queuesMap;
    }
    
    @Override
    public void submitApplication(final ApplicationId applicationId, final String user, final String queue) throws AccessControlException {
        synchronized (this) {
            if (queue.equals(this.queueName)) {
                throw new AccessControlException("Cannot submit application to non-leaf queue: " + this.queueName);
            }
            if (this.state != QueueState.RUNNING) {
                throw new AccessControlException("Queue " + this.getQueuePath() + " is STOPPED. Cannot accept submission of application: " + applicationId);
            }
            this.addApplication(applicationId, user);
        }
        if (this.parent != null) {
            try {
                this.parent.submitApplication(applicationId, user, queue);
            }
            catch (AccessControlException ace) {
                ParentQueue.LOG.info("Failed to submit application to parent-queue: " + this.parent.getQueuePath(), ace);
                this.removeApplication(applicationId, user);
                throw ace;
            }
        }
    }
    
    @Override
    public void submitApplicationAttempt(final FiCaSchedulerApp application, final String userName) {
    }
    
    @Override
    public void finishApplicationAttempt(final FiCaSchedulerApp application, final String queue) {
    }
    
    private synchronized void addApplication(final ApplicationId applicationId, final String user) {
        ++this.numApplications;
        ParentQueue.LOG.info("Application added - appId: " + applicationId + " user: " + user + " leaf-queue of parent: " + this.getQueueName() + " #applications: " + this.getNumApplications());
    }
    
    @Override
    public void finishApplication(final ApplicationId application, final String user) {
        synchronized (this) {
            this.removeApplication(application, user);
        }
        if (this.parent != null) {
            this.parent.finishApplication(application, user);
        }
    }
    
    private synchronized void removeApplication(final ApplicationId applicationId, final String user) {
        --this.numApplications;
        ParentQueue.LOG.info("Application removed - appId: " + applicationId + " user: " + user + " leaf-queue of parent: " + this.getQueueName() + " #applications: " + this.getNumApplications());
    }
    
    @Override
    public synchronized CSAssignment assignContainers(final Resource clusterResource, final FiCaSchedulerNode node, final boolean needToUnreserve) {
        final CSAssignment assignment = new CSAssignment(Resources.createResource(0, 0), NodeType.NODE_LOCAL);
        if (!SchedulerUtils.checkQueueAccessToNode(this.accessibleLabels, this.labelManager.getLabelsOnNode(node.getNodeID()))) {
            return assignment;
        }
        while (this.canAssign(clusterResource, node)) {
            if (ParentQueue.LOG.isDebugEnabled()) {
                ParentQueue.LOG.debug("Trying to assign containers to child-queue of " + this.getQueueName());
            }
            boolean localNeedToUnreserve = false;
            final Set<String> nodeLabels = this.labelManager.getLabelsOnNode(node.getNodeID());
            if (!this.canAssignToThisQueue(clusterResource, nodeLabels)) {
                localNeedToUnreserve = this.assignToQueueIfUnreserve(clusterResource);
                if (!localNeedToUnreserve) {
                    break;
                }
            }
            final CSAssignment assignedToChild = this.assignContainersToChildQueues(clusterResource, node, localNeedToUnreserve | needToUnreserve);
            assignment.setType(assignedToChild.getType());
            if (!Resources.greaterThan(this.resourceCalculator, clusterResource, assignedToChild.getResource(), Resources.none())) {
                break;
            }
            super.allocateResource(clusterResource, assignedToChild.getResource(), nodeLabels);
            Resources.addTo(assignment.getResource(), assignedToChild.getResource());
            ParentQueue.LOG.info("assignedContainer queue=" + this.getQueueName() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            if (ParentQueue.LOG.isDebugEnabled()) {
                ParentQueue.LOG.debug("ParentQ=" + this.getQueueName() + " assignedSoFarInThisIteration=" + assignment.getResource() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity());
            }
            if (this.rootQueue && assignment.getType() != NodeType.OFF_SWITCH) {
                continue;
            }
            if (ParentQueue.LOG.isDebugEnabled() && this.rootQueue && assignment.getType() == NodeType.OFF_SWITCH) {
                ParentQueue.LOG.debug("Not assigning more than one off-switch container, assignments so far: " + assignment);
                break;
            }
            break;
        }
        return assignment;
    }
    
    private synchronized boolean canAssignToThisQueue(final Resource clusterResource, final Set<String> nodeLabels) {
        final Set<String> labelCanAccess = new HashSet<String>(this.accessibleLabels.contains("*") ? nodeLabels : Sets.intersection(this.accessibleLabels, nodeLabels));
        if (nodeLabels.isEmpty()) {
            labelCanAccess.add("");
        }
        boolean canAssign = true;
        for (final String label : labelCanAccess) {
            if (!this.usedResourcesByNodeLabels.containsKey(label)) {
                this.usedResourcesByNodeLabels.put(label, Resources.createResource(0));
            }
            final float currentAbsoluteLabelUsedCapacity = Resources.divide(this.resourceCalculator, clusterResource, this.usedResourcesByNodeLabels.get(label), this.labelManager.getResourceByLabel(label, clusterResource));
            if (currentAbsoluteLabelUsedCapacity >= this.getAbsoluteMaximumCapacityByNodeLabel(label)) {
                if (ParentQueue.LOG.isDebugEnabled()) {
                    ParentQueue.LOG.debug(this.getQueueName() + " used=" + this.usedResources + " current-capacity (" + this.usedResourcesByNodeLabels.get(label) + ") " + " >= max-capacity (" + this.labelManager.getResourceByLabel(label, clusterResource) + ")");
                }
                canAssign = false;
                break;
            }
        }
        return canAssign;
    }
    
    private synchronized boolean assignToQueueIfUnreserve(final Resource clusterResource) {
        if (this.reservationsContinueLooking) {
            final Resource reservedResources = Resources.createResource(this.getMetrics().getReservedMB(), this.getMetrics().getReservedVirtualCores());
            final float capacityWithoutReservedCapacity = Resources.divide(this.resourceCalculator, clusterResource, Resources.subtract(this.usedResources, reservedResources), clusterResource);
            if (capacityWithoutReservedCapacity <= this.absoluteMaxCapacity) {
                if (ParentQueue.LOG.isDebugEnabled()) {
                    ParentQueue.LOG.debug("parent: try to use reserved: " + this.getQueueName() + " usedResources: " + this.usedResources.getMemory() + " clusterResources: " + clusterResource.getMemory() + " reservedResources: " + reservedResources.getMemory() + " currentCapacity " + this.usedResources.getMemory() / (float)clusterResource.getMemory() + " potentialNewWithoutReservedCapacity: " + capacityWithoutReservedCapacity + " ( " + " max-capacity: " + this.absoluteMaxCapacity + ")");
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean canAssign(final Resource clusterResource, final FiCaSchedulerNode node) {
        return node.getReservedContainer() == null && Resources.greaterThanOrEqual(this.resourceCalculator, clusterResource, node.getAvailableResource(), this.minimumAllocation);
    }
    
    private synchronized CSAssignment assignContainersToChildQueues(final Resource cluster, final FiCaSchedulerNode node, final boolean needToUnreserve) {
        CSAssignment assignment = new CSAssignment(Resources.createResource(0, 0), NodeType.NODE_LOCAL);
        this.printChildQueues();
        final Iterator<CSQueue> iter = this.childQueues.iterator();
        while (iter.hasNext()) {
            final CSQueue childQueue = iter.next();
            if (ParentQueue.LOG.isDebugEnabled()) {
                ParentQueue.LOG.debug("Trying to assign to queue: " + childQueue.getQueuePath() + " stats: " + childQueue);
            }
            assignment = childQueue.assignContainers(cluster, node, needToUnreserve);
            if (ParentQueue.LOG.isDebugEnabled()) {
                ParentQueue.LOG.debug("Assigned to queue: " + childQueue.getQueuePath() + " stats: " + childQueue + " --> " + assignment.getResource() + ", " + assignment.getType());
            }
            if (Resources.greaterThan(this.resourceCalculator, cluster, assignment.getResource(), Resources.none())) {
                iter.remove();
                ParentQueue.LOG.info("Re-sorting assigned queue: " + childQueue.getQueuePath() + " stats: " + childQueue);
                this.childQueues.add(childQueue);
                if (ParentQueue.LOG.isDebugEnabled()) {
                    this.printChildQueues();
                    break;
                }
                break;
            }
        }
        return assignment;
    }
    
    String getChildQueuesToPrint() {
        final StringBuilder sb = new StringBuilder();
        for (final CSQueue q : this.childQueues) {
            sb.append(q.getQueuePath() + "usedCapacity=(" + q.getUsedCapacity() + "), " + " label=(" + StringUtils.join(q.getAccessibleNodeLabels().iterator(), ",") + ")");
        }
        return sb.toString();
    }
    
    private void printChildQueues() {
        if (ParentQueue.LOG.isDebugEnabled()) {
            ParentQueue.LOG.debug("printChildQueues - queue: " + this.getQueuePath() + " child-queues: " + this.getChildQueuesToPrint());
        }
    }
    
    @Override
    public void completedContainer(final Resource clusterResource, final FiCaSchedulerApp application, final FiCaSchedulerNode node, final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event, final CSQueue completedChildQueue, final boolean sortQueues) {
        if (application != null) {
            synchronized (this) {
                super.releaseResource(clusterResource, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(node.getNodeID()));
                ParentQueue.LOG.info("completedContainer queue=" + this.getQueueName() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            }
            if (sortQueues) {
                final Iterator<CSQueue> iter = this.childQueues.iterator();
                while (iter.hasNext()) {
                    final CSQueue csqueue = iter.next();
                    if (csqueue.equals(completedChildQueue)) {
                        iter.remove();
                        ParentQueue.LOG.info("Re-sorting completed queue: " + csqueue.getQueuePath() + " stats: " + csqueue);
                        this.childQueues.add(csqueue);
                        break;
                    }
                }
            }
            if (this.parent != null) {
                this.parent.completedContainer(clusterResource, application, node, rmContainer, null, event, this, sortQueues);
            }
        }
    }
    
    @Override
    public synchronized void updateClusterResource(final Resource clusterResource) {
        for (final CSQueue childQueue : this.childQueues) {
            childQueue.updateClusterResource(clusterResource);
        }
        CSQueueUtils.updateQueueStatistics(this.resourceCalculator, this, this.parent, clusterResource, this.minimumAllocation);
    }
    
    @Override
    public synchronized List<CSQueue> getChildQueues() {
        return new ArrayList<CSQueue>(this.childQueues);
    }
    
    @Override
    public void recoverContainer(final Resource clusterResource, final SchedulerApplicationAttempt attempt, final RMContainer rmContainer) {
        if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
            return;
        }
        synchronized (this) {
            super.allocateResource(clusterResource, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
        }
        if (this.parent != null) {
            this.parent.recoverContainer(clusterResource, attempt, rmContainer);
        }
    }
    
    @Override
    public ActiveUsersManager getActiveUsersManager() {
        return null;
    }
    
    @Override
    public void collectSchedulerApplications(final Collection<ApplicationAttemptId> apps) {
        for (final CSQueue queue : this.childQueues) {
            queue.collectSchedulerApplications(apps);
        }
    }
    
    @Override
    public void attachContainer(final Resource clusterResource, final FiCaSchedulerApp application, final RMContainer rmContainer) {
        if (application != null) {
            super.allocateResource(clusterResource, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
            ParentQueue.LOG.info("movedContainer queueMoveIn=" + this.getQueueName() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            if (this.parent != null) {
                this.parent.attachContainer(clusterResource, application, rmContainer);
            }
        }
    }
    
    @Override
    public void detachContainer(final Resource clusterResource, final FiCaSchedulerApp application, final RMContainer rmContainer) {
        if (application != null) {
            super.releaseResource(clusterResource, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
            ParentQueue.LOG.info("movedContainer queueMoveOut=" + this.getQueueName() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            if (this.parent != null) {
                this.parent.detachContainer(clusterResource, application, rmContainer);
            }
        }
    }
    
    @Override
    public float getAbsActualCapacity() {
        return this.absoluteCapacity;
    }
    
    @Override
    public synchronized int getNumApplications() {
        return this.numApplications;
    }
    
    static {
        LOG = LogFactory.getLog(ParentQueue.class);
        ParentQueue.PRECISION = 5.0E-4f;
    }
}
