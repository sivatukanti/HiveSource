// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.nodelabels;

import org.apache.hadoop.security.UserGroupInformation;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import org.apache.hadoop.yarn.util.resource.Resources;
import com.google.common.collect.ImmutableSet;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.security.authorize.AccessControlList;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager;

public class RMNodeLabelsManager extends CommonNodeLabelsManager
{
    ConcurrentMap<String, Queue> queueCollections;
    protected AccessControlList adminAcl;
    
    public RMNodeLabelsManager() {
        this.queueCollections = new ConcurrentHashMap<String, Queue>();
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        super.serviceInit(conf);
        this.adminAcl = new AccessControlList(conf.get("yarn.admin.acl", "*"));
    }
    
    @Override
    public void addLabelsToNode(final Map<NodeId, Set<String>> addedLabelsToNode) throws IOException {
        try {
            this.writeLock.lock();
            final Map<String, Host> before = this.cloneNodeMap(addedLabelsToNode.keySet());
            super.addLabelsToNode(addedLabelsToNode);
            final Map<String, Host> after = this.cloneNodeMap(addedLabelsToNode.keySet());
            this.updateResourceMappings(before, after);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    protected void checkRemoveFromClusterNodeLabelsOfQueue(final Collection<String> labelsToRemove) throws IOException {
        for (String label : labelsToRemove) {
            label = this.normalizeLabel(label);
            for (final Map.Entry<String, Queue> entry : this.queueCollections.entrySet()) {
                final String queueName = entry.getKey();
                final Set<String> queueLabels = entry.getValue().acccessibleNodeLabels;
                if (queueLabels.contains(label)) {
                    throw new IOException("Cannot remove label=" + label + ", because queue=" + queueName + " is using this label. " + "Please remove label on queue before remove the label");
                }
            }
        }
    }
    
    @Override
    public void removeFromClusterNodeLabels(final Collection<String> labelsToRemove) throws IOException {
        try {
            this.writeLock.lock();
            this.checkRemoveFromClusterNodeLabelsOfQueue(labelsToRemove);
            final Map<String, Host> before = this.cloneNodeMap();
            super.removeFromClusterNodeLabels(labelsToRemove);
            this.updateResourceMappings(before, this.nodeCollections);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void removeLabelsFromNode(final Map<NodeId, Set<String>> removeLabelsFromNode) throws IOException {
        try {
            this.writeLock.lock();
            final Map<String, Host> before = this.cloneNodeMap(removeLabelsFromNode.keySet());
            super.removeLabelsFromNode(removeLabelsFromNode);
            final Map<String, Host> after = this.cloneNodeMap(removeLabelsFromNode.keySet());
            this.updateResourceMappings(before, after);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void replaceLabelsOnNode(final Map<NodeId, Set<String>> replaceLabelsToNode) throws IOException {
        try {
            this.writeLock.lock();
            final Map<String, Host> before = this.cloneNodeMap(replaceLabelsToNode.keySet());
            super.replaceLabelsOnNode(replaceLabelsToNode);
            final Map<String, Host> after = this.cloneNodeMap(replaceLabelsToNode.keySet());
            this.updateResourceMappings(before, after);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void activateNode(final NodeId nodeId, final Resource resource) {
        try {
            this.writeLock.lock();
            final Map<String, Host> before = this.cloneNodeMap(ImmutableSet.of(nodeId));
            this.createHostIfNonExisted(nodeId.getHost());
            try {
                this.createNodeIfNonExisted(nodeId);
            }
            catch (IOException e) {
                RMNodeLabelsManager.LOG.error("This shouldn't happen, cannot get host in nodeCollection associated to the node being activated");
                return;
            }
            final Node nm = this.getNMInNodeSet(nodeId);
            nm.resource = resource;
            nm.running = true;
            final Map<String, Host> after = this.cloneNodeMap(ImmutableSet.of(nodeId));
            this.updateResourceMappings(before, after);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void deactivateNode(final NodeId nodeId) {
        try {
            this.writeLock.lock();
            final Map<String, Host> before = this.cloneNodeMap(ImmutableSet.of(nodeId));
            final Node nm = this.getNMInNodeSet(nodeId);
            if (null != nm) {
                nm.running = false;
                nm.resource = Resource.newInstance(0, 0);
            }
            final Map<String, Host> after = this.cloneNodeMap(ImmutableSet.of(nodeId));
            this.updateResourceMappings(before, after);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void updateNodeResource(final NodeId node, final Resource newResource) throws IOException {
        this.deactivateNode(node);
        this.activateNode(node, newResource);
    }
    
    public void reinitializeQueueLabels(final Map<String, Set<String>> queueToLabels) {
        try {
            this.writeLock.lock();
            this.queueCollections.clear();
            for (final Map.Entry<String, Set<String>> entry : queueToLabels.entrySet()) {
                final String queue = entry.getKey();
                final Queue q = new Queue();
                this.queueCollections.put(queue, q);
                final Set<String> labels = entry.getValue();
                if (labels.contains("*")) {
                    continue;
                }
                q.acccessibleNodeLabels.addAll(labels);
                for (final Host host : this.nodeCollections.values()) {
                    for (final Map.Entry<NodeId, Node> nentry : host.nms.entrySet()) {
                        final NodeId nodeId = nentry.getKey();
                        final Node nm = nentry.getValue();
                        if (nm.running && this.isNodeUsableByQueue(this.getLabelsByNode(nodeId), q)) {
                            Resources.addTo(q.resource, nm.resource);
                        }
                    }
                }
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public Resource getQueueResource(final String queueName, final Set<String> queueLabels, final Resource clusterResource) {
        try {
            this.readLock.lock();
            if (queueLabels.contains("*")) {
                return clusterResource;
            }
            final Queue q = this.queueCollections.get(queueName);
            if (null == q) {
                return Resources.none();
            }
            return q.resource;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public Set<String> getLabelsOnNode(final NodeId nodeId) {
        try {
            this.readLock.lock();
            final Set<String> nodeLabels = this.getLabelsByNode(nodeId);
            return Collections.unmodifiableSet((Set<? extends String>)nodeLabels);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public boolean containsNodeLabel(final String label) {
        try {
            this.readLock.lock();
            return label != null && (label.isEmpty() || this.labelCollections.containsKey(label));
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private Map<String, Host> cloneNodeMap(final Set<NodeId> nodesToCopy) {
        final Map<String, Host> map = new HashMap<String, Host>();
        for (final NodeId nodeId : nodesToCopy) {
            if (!map.containsKey(nodeId.getHost())) {
                final Host originalN = this.nodeCollections.get(nodeId.getHost());
                if (null == originalN) {
                    continue;
                }
                final Host n = originalN.copy();
                n.nms.clear();
                map.put(nodeId.getHost(), n);
            }
            final Host n2 = map.get(nodeId.getHost());
            if (0 == nodeId.getPort()) {
                for (final Map.Entry<NodeId, Node> entry : this.nodeCollections.get(nodeId.getHost()).nms.entrySet()) {
                    n2.nms.put(entry.getKey(), entry.getValue().copy());
                }
            }
            else {
                final Node nm = this.getNMInNodeSet(nodeId);
                if (null == nm) {
                    continue;
                }
                n2.nms.put(nodeId, nm.copy());
            }
        }
        return map;
    }
    
    private void updateResourceMappings(final Map<String, Host> before, final Map<String, Host> after) {
        final Set<NodeId> allNMs = new HashSet<NodeId>();
        for (final Map.Entry<String, Host> entry : before.entrySet()) {
            allNMs.addAll(entry.getValue().nms.keySet());
        }
        for (final Map.Entry<String, Host> entry : after.entrySet()) {
            allNMs.addAll(entry.getValue().nms.keySet());
        }
        for (final NodeId nodeId : allNMs) {
            final Node oldNM;
            if ((oldNM = this.getNMInNodeSet(nodeId, before, true)) != null) {
                final Set<String> oldLabels = this.getLabelsByNode(nodeId, before);
                if (oldLabels.isEmpty()) {
                    final Label label = this.labelCollections.get("");
                    Resources.subtractFrom(label.resource, oldNM.resource);
                    for (final Queue q : this.queueCollections.values()) {
                        Resources.subtractFrom(q.resource, oldNM.resource);
                    }
                }
                else {
                    for (final String labelName : oldLabels) {
                        final Label label2 = this.labelCollections.get(labelName);
                        if (null == label2) {
                            continue;
                        }
                        Resources.subtractFrom(label2.resource, oldNM.resource);
                    }
                    for (final Queue q2 : this.queueCollections.values()) {
                        if (this.isNodeUsableByQueue(oldLabels, q2)) {
                            Resources.subtractFrom(q2.resource, oldNM.resource);
                        }
                    }
                }
            }
            final Node newNM;
            if ((newNM = this.getNMInNodeSet(nodeId, after, true)) != null) {
                final Set<String> newLabels = this.getLabelsByNode(nodeId, after);
                if (newLabels.isEmpty()) {
                    final Label label3 = this.labelCollections.get("");
                    Resources.addTo(label3.resource, newNM.resource);
                    for (final Queue q3 : this.queueCollections.values()) {
                        Resources.addTo(q3.resource, newNM.resource);
                    }
                }
                else {
                    for (final String labelName2 : newLabels) {
                        final Label label4 = this.labelCollections.get(labelName2);
                        Resources.addTo(label4.resource, newNM.resource);
                    }
                    for (final Queue q : this.queueCollections.values()) {
                        if (this.isNodeUsableByQueue(newLabels, q)) {
                            Resources.addTo(q.resource, newNM.resource);
                        }
                    }
                }
            }
        }
    }
    
    public Resource getResourceByLabel(String label, final Resource clusterResource) {
        label = this.normalizeLabel(label);
        try {
            this.readLock.lock();
            if (null == this.labelCollections.get(label)) {
                return Resources.none();
            }
            return this.labelCollections.get(label).resource;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private boolean isNodeUsableByQueue(final Set<String> nodeLabels, final Queue q) {
        if (nodeLabels == null || nodeLabels.isEmpty() || (nodeLabels.size() == 1 && nodeLabels.contains(""))) {
            return true;
        }
        for (final String label : nodeLabels) {
            if (q.acccessibleNodeLabels.contains(label)) {
                return true;
            }
        }
        return false;
    }
    
    private Map<String, Host> cloneNodeMap() {
        final Set<NodeId> nodesToCopy = new HashSet<NodeId>();
        for (final String nodeName : this.nodeCollections.keySet()) {
            nodesToCopy.add(NodeId.newInstance(nodeName, 0));
        }
        return this.cloneNodeMap(nodesToCopy);
    }
    
    public boolean checkAccess(final UserGroupInformation user) {
        return this.adminAcl.isUserAllowed(user);
    }
    
    protected static class Queue
    {
        protected Set<String> acccessibleNodeLabels;
        protected Resource resource;
        
        protected Queue() {
            this.acccessibleNodeLabels = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
            this.resource = Resource.newInstance(0, 0);
        }
    }
}
