// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class SchedulerNode
{
    private static final Log LOG;
    private Resource availableResource;
    private Resource usedResource;
    private Resource totalResourceCapability;
    private RMContainer reservedContainer;
    private volatile int numContainers;
    private final Map<ContainerId, RMContainer> launchedContainers;
    private final RMNode rmNode;
    private final String nodeName;
    
    public SchedulerNode(final RMNode node, final boolean usePortForNodeName) {
        this.availableResource = Resource.newInstance(0, 0);
        this.usedResource = Resource.newInstance(0, 0);
        this.launchedContainers = new HashMap<ContainerId, RMContainer>();
        this.rmNode = node;
        this.availableResource = Resources.clone(node.getTotalCapability());
        this.totalResourceCapability = Resources.clone(node.getTotalCapability());
        if (usePortForNodeName) {
            this.nodeName = this.rmNode.getHostName() + ":" + node.getNodeID().getPort();
        }
        else {
            this.nodeName = this.rmNode.getHostName();
        }
    }
    
    public RMNode getRMNode() {
        return this.rmNode;
    }
    
    public synchronized void setTotalResource(final Resource resource) {
        this.totalResourceCapability = resource;
        this.availableResource = Resources.subtract(this.totalResourceCapability, this.usedResource);
    }
    
    public NodeId getNodeID() {
        return this.rmNode.getNodeID();
    }
    
    public String getHttpAddress() {
        return this.rmNode.getHttpAddress();
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    public String getRackName() {
        return this.rmNode.getRackName();
    }
    
    public synchronized void allocateContainer(final RMContainer rmContainer) {
        final Container container = rmContainer.getContainer();
        this.deductAvailableResource(container.getResource());
        ++this.numContainers;
        this.launchedContainers.put(container.getId(), rmContainer);
        SchedulerNode.LOG.info("Assigned container " + container.getId() + " of capacity " + container.getResource() + " on host " + this.rmNode.getNodeAddress() + ", which has " + this.numContainers + " containers, " + this.getUsedResource() + " used and " + this.getAvailableResource() + " available after allocation");
    }
    
    public synchronized Resource getAvailableResource() {
        return this.availableResource;
    }
    
    public synchronized Resource getUsedResource() {
        return this.usedResource;
    }
    
    public synchronized Resource getTotalResource() {
        return this.totalResourceCapability;
    }
    
    public synchronized boolean isValidContainer(final ContainerId containerId) {
        return this.launchedContainers.containsKey(containerId);
    }
    
    private synchronized void updateResource(final Container container) {
        this.addAvailableResource(container.getResource());
        --this.numContainers;
    }
    
    public synchronized void releaseContainer(final Container container) {
        if (!this.isValidContainer(container.getId())) {
            SchedulerNode.LOG.error("Invalid container released " + container);
            return;
        }
        if (null != this.launchedContainers.remove(container.getId())) {
            this.updateResource(container);
        }
        SchedulerNode.LOG.info("Released container " + container.getId() + " of capacity " + container.getResource() + " on host " + this.rmNode.getNodeAddress() + ", which currently has " + this.numContainers + " containers, " + this.getUsedResource() + " used and " + this.getAvailableResource() + " available" + ", release resources=" + true);
    }
    
    private synchronized void addAvailableResource(final Resource resource) {
        if (resource == null) {
            SchedulerNode.LOG.error("Invalid resource addition of null resource for " + this.rmNode.getNodeAddress());
            return;
        }
        Resources.addTo(this.availableResource, resource);
        Resources.subtractFrom(this.usedResource, resource);
    }
    
    private synchronized void deductAvailableResource(final Resource resource) {
        if (resource == null) {
            SchedulerNode.LOG.error("Invalid deduction of null resource for " + this.rmNode.getNodeAddress());
            return;
        }
        Resources.subtractFrom(this.availableResource, resource);
        Resources.addTo(this.usedResource, resource);
    }
    
    public abstract void reserveResource(final SchedulerApplicationAttempt p0, final Priority p1, final RMContainer p2);
    
    public abstract void unreserveResource(final SchedulerApplicationAttempt p0);
    
    @Override
    public String toString() {
        return "host: " + this.rmNode.getNodeAddress() + " #containers=" + this.getNumContainers() + " available=" + this.getAvailableResource().getMemory() + " used=" + this.getUsedResource().getMemory();
    }
    
    public int getNumContainers() {
        return this.numContainers;
    }
    
    public synchronized List<RMContainer> getRunningContainers() {
        return new ArrayList<RMContainer>(this.launchedContainers.values());
    }
    
    public synchronized RMContainer getReservedContainer() {
        return this.reservedContainer;
    }
    
    protected synchronized void setReservedContainer(final RMContainer reservedContainer) {
        this.reservedContainer = reservedContainer;
    }
    
    public synchronized void recoverContainer(final RMContainer rmContainer) {
        if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
            return;
        }
        this.allocateContainer(rmContainer);
    }
    
    static {
        LOG = LogFactory.getLog(SchedulerNode.class);
    }
}
