// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import java.util.Iterator;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Sets;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import java.util.HashMap;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import java.util.Set;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.api.records.QueueState;
import org.apache.hadoop.yarn.api.records.Resource;

public abstract class AbstractCSQueue implements CSQueue
{
    CSQueue parent;
    final String queueName;
    float capacity;
    float maximumCapacity;
    float absoluteCapacity;
    float absoluteMaxCapacity;
    float absoluteUsedCapacity;
    float usedCapacity;
    volatile int numContainers;
    final Resource minimumAllocation;
    final Resource maximumAllocation;
    QueueState state;
    final QueueMetrics metrics;
    final ResourceCalculator resourceCalculator;
    Set<String> accessibleLabels;
    RMNodeLabelsManager labelManager;
    String defaultLabelExpression;
    Resource usedResources;
    QueueInfo queueInfo;
    Map<String, Float> absoluteCapacityByNodeLabels;
    Map<String, Float> capacitiyByNodeLabels;
    Map<String, Resource> usedResourcesByNodeLabels;
    Map<String, Float> absoluteMaxCapacityByNodeLabels;
    Map<String, Float> maxCapacityByNodeLabels;
    Map<QueueACL, AccessControlList> acls;
    boolean reservationsContinueLooking;
    private final RecordFactory recordFactory;
    
    public AbstractCSQueue(final CapacitySchedulerContext cs, final String queueName, final CSQueue parent, final CSQueue old) throws IOException {
        this.absoluteUsedCapacity = 0.0f;
        this.usedCapacity = 0.0f;
        this.usedResources = Resources.createResource(0, 0);
        this.usedResourcesByNodeLabels = new HashMap<String, Resource>();
        this.acls = new HashMap<QueueACL, AccessControlList>();
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.minimumAllocation = cs.getMinimumResourceCapability();
        this.maximumAllocation = cs.getMaximumResourceCapability();
        this.labelManager = cs.getRMContext().getNodeLabelManager();
        this.parent = parent;
        this.queueName = queueName;
        this.resourceCalculator = cs.getResourceCalculator();
        this.queueInfo = this.recordFactory.newRecordInstance(QueueInfo.class);
        this.metrics = ((old != null) ? old.getMetrics() : QueueMetrics.forQueue(this.getQueuePath(), parent, cs.getConfiguration().getEnableUserMetrics(), cs.getConf()));
        this.accessibleLabels = cs.getConfiguration().getAccessibleNodeLabels(this.getQueuePath());
        this.defaultLabelExpression = cs.getConfiguration().getDefaultNodeLabelExpression(this.getQueuePath());
        this.queueInfo.setQueueName(queueName);
        if (this.accessibleLabels == null && parent != null) {
            this.accessibleLabels = parent.getAccessibleNodeLabels();
        }
        SchedulerUtils.checkIfLabelInClusterNodeLabels(this.labelManager, this.accessibleLabels);
        if (this.defaultLabelExpression == null && parent != null && this.accessibleLabels.containsAll(parent.getAccessibleNodeLabels())) {
            this.defaultLabelExpression = parent.getDefaultNodeLabelExpression();
        }
        this.capacitiyByNodeLabels = cs.getConfiguration().getNodeLabelCapacities(this.getQueuePath(), this.accessibleLabels, this.labelManager);
        this.maxCapacityByNodeLabels = cs.getConfiguration().getMaximumNodeLabelCapacities(this.getQueuePath(), this.accessibleLabels, this.labelManager);
    }
    
    @Override
    public synchronized float getCapacity() {
        return this.capacity;
    }
    
    @Override
    public synchronized float getAbsoluteCapacity() {
        return this.absoluteCapacity;
    }
    
    @Override
    public float getAbsoluteMaximumCapacity() {
        return this.absoluteMaxCapacity;
    }
    
    @Override
    public synchronized float getAbsoluteUsedCapacity() {
        return this.absoluteUsedCapacity;
    }
    
    @Override
    public float getMaximumCapacity() {
        return this.maximumCapacity;
    }
    
    @Override
    public synchronized float getUsedCapacity() {
        return this.usedCapacity;
    }
    
    @Override
    public synchronized Resource getUsedResources() {
        return this.usedResources;
    }
    
    public synchronized int getNumContainers() {
        return this.numContainers;
    }
    
    @Override
    public synchronized QueueState getState() {
        return this.state;
    }
    
    @Override
    public QueueMetrics getMetrics() {
        return this.metrics;
    }
    
    @Override
    public String getQueueName() {
        return this.queueName;
    }
    
    @Override
    public synchronized CSQueue getParent() {
        return this.parent;
    }
    
    @Override
    public synchronized void setParent(final CSQueue newParentQueue) {
        this.parent = newParentQueue;
    }
    
    @Override
    public Set<String> getAccessibleNodeLabels() {
        return this.accessibleLabels;
    }
    
    @Override
    public boolean hasAccess(final QueueACL acl, final UserGroupInformation user) {
        synchronized (this) {
            if (this.acls.get(acl).isUserAllowed(user)) {
                return true;
            }
        }
        return this.parent != null && this.parent.hasAccess(acl, user);
    }
    
    @Override
    public synchronized void setUsedCapacity(final float usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
    
    @Override
    public synchronized void setAbsoluteUsedCapacity(final float absUsedCapacity) {
        this.absoluteUsedCapacity = absUsedCapacity;
    }
    
    synchronized void setMaxCapacity(final float maximumCapacity) {
        CSQueueUtils.checkMaxCapacity(this.getQueueName(), this.capacity, maximumCapacity);
        final float absMaxCapacity = CSQueueUtils.computeAbsoluteMaximumCapacity(maximumCapacity, this.parent);
        CSQueueUtils.checkAbsoluteCapacity(this.getQueueName(), this.absoluteCapacity, absMaxCapacity);
        this.maximumCapacity = maximumCapacity;
        this.absoluteMaxCapacity = absMaxCapacity;
    }
    
    @Override
    public float getAbsActualCapacity() {
        return this.absoluteCapacity;
    }
    
    @Override
    public String getDefaultNodeLabelExpression() {
        return this.defaultLabelExpression;
    }
    
    synchronized void setupQueueConfigs(final Resource clusterResource, final float capacity, final float absoluteCapacity, final float maximumCapacity, final float absoluteMaxCapacity, final QueueState state, final Map<QueueACL, AccessControlList> acls, final Set<String> labels, final String defaultLabelExpression, final Map<String, Float> nodeLabelCapacities, final Map<String, Float> maximumNodeLabelCapacities, final boolean reservationContinueLooking) throws IOException {
        CSQueueUtils.checkMaxCapacity(this.getQueueName(), capacity, maximumCapacity);
        CSQueueUtils.checkAbsoluteCapacity(this.getQueueName(), absoluteCapacity, absoluteMaxCapacity);
        this.capacity = capacity;
        this.absoluteCapacity = absoluteCapacity;
        this.maximumCapacity = maximumCapacity;
        this.absoluteMaxCapacity = absoluteMaxCapacity;
        this.state = state;
        this.acls = acls;
        this.accessibleLabels = labels;
        this.defaultLabelExpression = defaultLabelExpression;
        this.capacitiyByNodeLabels = new HashMap<String, Float>(nodeLabelCapacities);
        this.maxCapacityByNodeLabels = new HashMap<String, Float>(maximumNodeLabelCapacities);
        this.queueInfo.setAccessibleNodeLabels(this.accessibleLabels);
        this.queueInfo.setCapacity(this.capacity);
        this.queueInfo.setMaximumCapacity(this.maximumCapacity);
        this.queueInfo.setQueueState(this.state);
        this.queueInfo.setDefaultNodeLabelExpression(this.defaultLabelExpression);
        CSQueueUtils.updateQueueStatistics(this.resourceCalculator, this, this.parent, clusterResource, this.minimumAllocation);
        if (this.parent != null && this.parent.getParent() != null && this.parent.getAccessibleNodeLabels() != null && !this.parent.getAccessibleNodeLabels().contains("*")) {
            if (this.getAccessibleNodeLabels().contains("*")) {
                throw new IOException("Parent's accessible queue is not ANY(*), but child's accessible queue is *");
            }
            final Set<String> diff = Sets.difference(this.getAccessibleNodeLabels(), this.parent.getAccessibleNodeLabels());
            if (!diff.isEmpty()) {
                throw new IOException("Some labels of child queue is not a subset of parent queue, these labels=[" + StringUtils.join(diff, ",") + "]");
            }
        }
        this.absoluteCapacityByNodeLabels = CSQueueUtils.computeAbsoluteCapacityByNodeLabels(this.capacitiyByNodeLabels, this.parent);
        this.absoluteMaxCapacityByNodeLabels = CSQueueUtils.computeAbsoluteMaxCapacityByNodeLabels(maximumNodeLabelCapacities, this.parent);
        CSQueueUtils.checkAbsoluteCapacitiesByLabel(this.getQueueName(), this.absoluteCapacityByNodeLabels, this.absoluteCapacityByNodeLabels);
        this.reservationsContinueLooking = reservationContinueLooking;
    }
    
    @InterfaceAudience.Private
    public Resource getMaximumAllocation() {
        return this.maximumAllocation;
    }
    
    @InterfaceAudience.Private
    public Resource getMinimumAllocation() {
        return this.minimumAllocation;
    }
    
    synchronized void allocateResource(final Resource clusterResource, final Resource resource, final Set<String> nodeLabels) {
        Resources.addTo(this.usedResources, resource);
        if (nodeLabels == null || nodeLabels.isEmpty()) {
            if (!this.usedResourcesByNodeLabels.containsKey("")) {
                this.usedResourcesByNodeLabels.put("", Resources.createResource(0));
            }
            Resources.addTo(this.usedResourcesByNodeLabels.get(""), resource);
        }
        else {
            for (final String label : Sets.intersection(this.accessibleLabels, nodeLabels)) {
                if (!this.usedResourcesByNodeLabels.containsKey(label)) {
                    this.usedResourcesByNodeLabels.put(label, Resources.createResource(0));
                }
                Resources.addTo(this.usedResourcesByNodeLabels.get(label), resource);
            }
        }
        ++this.numContainers;
        CSQueueUtils.updateQueueStatistics(this.resourceCalculator, this, this.getParent(), clusterResource, this.minimumAllocation);
    }
    
    protected synchronized void releaseResource(final Resource clusterResource, final Resource resource, final Set<String> nodeLabels) {
        Resources.subtractFrom(this.usedResources, resource);
        if (null == nodeLabels || nodeLabels.isEmpty()) {
            if (!this.usedResourcesByNodeLabels.containsKey("")) {
                this.usedResourcesByNodeLabels.put("", Resources.createResource(0));
            }
            Resources.subtractFrom(this.usedResourcesByNodeLabels.get(""), resource);
        }
        else {
            for (final String label : Sets.intersection(this.accessibleLabels, nodeLabels)) {
                if (!this.usedResourcesByNodeLabels.containsKey(label)) {
                    this.usedResourcesByNodeLabels.put(label, Resources.createResource(0));
                }
                Resources.subtractFrom(this.usedResourcesByNodeLabels.get(label), resource);
            }
        }
        CSQueueUtils.updateQueueStatistics(this.resourceCalculator, this, this.getParent(), clusterResource, this.minimumAllocation);
        --this.numContainers;
    }
    
    @InterfaceAudience.Private
    @Override
    public float getCapacityByNodeLabel(final String label) {
        if (StringUtils.equals(label, "")) {
            if (null == this.parent) {
                return 1.0f;
            }
            return this.getCapacity();
        }
        else {
            if (!this.capacitiyByNodeLabels.containsKey(label)) {
                return 0.0f;
            }
            return this.capacitiyByNodeLabels.get(label);
        }
    }
    
    @InterfaceAudience.Private
    @Override
    public float getAbsoluteCapacityByNodeLabel(final String label) {
        if (StringUtils.equals(label, "")) {
            if (null == this.parent) {
                return 1.0f;
            }
            return this.getAbsoluteCapacity();
        }
        else {
            if (!this.absoluteCapacityByNodeLabels.containsKey(label)) {
                return 0.0f;
            }
            return this.absoluteCapacityByNodeLabels.get(label);
        }
    }
    
    @InterfaceAudience.Private
    @Override
    public float getAbsoluteMaximumCapacityByNodeLabel(final String label) {
        if (StringUtils.equals(label, "")) {
            return this.getAbsoluteMaximumCapacity();
        }
        if (!this.absoluteMaxCapacityByNodeLabels.containsKey(label)) {
            return 0.0f;
        }
        return this.absoluteMaxCapacityByNodeLabels.get(label);
    }
    
    @InterfaceAudience.Private
    public boolean getReservationContinueLooking() {
        return this.reservationsContinueLooking;
    }
    
    @InterfaceAudience.Private
    public Map<QueueACL, AccessControlList> getACLs() {
        return this.acls;
    }
}
