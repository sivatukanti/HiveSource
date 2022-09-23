// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

class NodeTracker
{
    private final Map<NodeSelector, TrackedNodeData> trackedNodes;
    
    public NodeTracker() {
        this(Collections.emptyMap());
    }
    
    private NodeTracker(final Map<NodeSelector, TrackedNodeData> map) {
        this.trackedNodes = map;
    }
    
    public NodeTracker trackNode(final ImmutableNode root, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler) {
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        final TrackedNodeData trackData = newState.get(selector);
        newState.put(selector, trackDataForAddedObserver(root, selector, resolver, handler, trackData));
        return new NodeTracker(newState);
    }
    
    public NodeTracker trackNodes(final Collection<NodeSelector> selectors, final Collection<ImmutableNode> nodes) {
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        final Iterator<ImmutableNode> itNodes = nodes.iterator();
        for (final NodeSelector selector : selectors) {
            final ImmutableNode node = itNodes.next();
            TrackedNodeData trackData = newState.get(selector);
            if (trackData == null) {
                trackData = new TrackedNodeData(node);
            }
            else {
                trackData = trackData.observerAdded();
            }
            newState.put(selector, trackData);
        }
        return new NodeTracker(newState);
    }
    
    public NodeTracker untrackNode(final NodeSelector selector) {
        final TrackedNodeData trackData = this.getTrackedNodeData(selector);
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        final TrackedNodeData newTrackData = trackData.observerRemoved();
        if (newTrackData == null) {
            newState.remove(selector);
        }
        else {
            newState.put(selector, newTrackData);
        }
        return new NodeTracker(newState);
    }
    
    public ImmutableNode getTrackedNode(final NodeSelector selector) {
        return this.getTrackedNodeData(selector).getNode();
    }
    
    public boolean isTrackedNodeDetached(final NodeSelector selector) {
        return this.getTrackedNodeData(selector).isDetached();
    }
    
    public InMemoryNodeModel getDetachedNodeModel(final NodeSelector selector) {
        return this.getTrackedNodeData(selector).getDetachedModel();
    }
    
    public NodeTracker update(final ImmutableNode root, final NodeSelector txTarget, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler) {
        if (this.trackedNodes.isEmpty()) {
            return this;
        }
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>();
        for (final Map.Entry<NodeSelector, TrackedNodeData> e : this.trackedNodes.entrySet()) {
            newState.put(e.getKey(), determineUpdatedTrackedNodeData(root, txTarget, resolver, handler, e));
        }
        return new NodeTracker(newState);
    }
    
    public NodeTracker detachAllTrackedNodes() {
        if (this.trackedNodes.isEmpty()) {
            return this;
        }
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>();
        for (final Map.Entry<NodeSelector, TrackedNodeData> e : this.trackedNodes.entrySet()) {
            final TrackedNodeData newData = e.getValue().isDetached() ? e.getValue() : e.getValue().detach(null);
            newState.put(e.getKey(), newData);
        }
        return new NodeTracker(newState);
    }
    
    public NodeTracker replaceAndDetachTrackedNode(final NodeSelector selector, final ImmutableNode newNode) {
        final Map<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        newState.put(selector, this.getTrackedNodeData(selector).detach(newNode));
        return new NodeTracker(newState);
    }
    
    private TrackedNodeData getTrackedNodeData(final NodeSelector selector) {
        final TrackedNodeData trackData = this.trackedNodes.get(selector);
        if (trackData == null) {
            throw new ConfigurationRuntimeException("No tracked node found: " + selector);
        }
        return trackData;
    }
    
    private static TrackedNodeData determineUpdatedTrackedNodeData(final ImmutableNode root, final NodeSelector txTarget, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler, final Map.Entry<NodeSelector, TrackedNodeData> e) {
        if (e.getValue().isDetached()) {
            return e.getValue();
        }
        ImmutableNode newTarget;
        try {
            newTarget = e.getKey().select(root, resolver, handler);
        }
        catch (Exception ex) {
            newTarget = null;
        }
        if (newTarget == null) {
            return detachedTrackedNodeData(txTarget, e);
        }
        return e.getValue().updateNode(newTarget);
    }
    
    private static TrackedNodeData detachedTrackedNodeData(final NodeSelector txTarget, final Map.Entry<NodeSelector, TrackedNodeData> e) {
        final ImmutableNode newNode = e.getKey().equals(txTarget) ? createEmptyTrackedNode(e.getValue()) : null;
        return e.getValue().detach(newNode);
    }
    
    private static ImmutableNode createEmptyTrackedNode(final TrackedNodeData data) {
        return new ImmutableNode.Builder().name(data.getNode().getNodeName()).create();
    }
    
    private static TrackedNodeData trackDataForAddedObserver(final ImmutableNode root, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler, final TrackedNodeData trackData) {
        if (trackData != null) {
            return trackData.observerAdded();
        }
        final ImmutableNode target = selector.select(root, resolver, handler);
        if (target == null) {
            throw new ConfigurationRuntimeException("Selector does not select unique node: " + selector);
        }
        return new TrackedNodeData(target);
    }
    
    private static class TrackedNodeData
    {
        private final ImmutableNode node;
        private final int observerCount;
        private final InMemoryNodeModel detachedModel;
        
        public TrackedNodeData(final ImmutableNode nd) {
            this(nd, 1, null);
        }
        
        private TrackedNodeData(final ImmutableNode nd, final int obsCount, final InMemoryNodeModel detachedNodeModel) {
            this.node = nd;
            this.observerCount = obsCount;
            this.detachedModel = detachedNodeModel;
        }
        
        public ImmutableNode getNode() {
            return (this.getDetachedModel() != null) ? this.getDetachedModel().getRootNode() : this.node;
        }
        
        public InMemoryNodeModel getDetachedModel() {
            return this.detachedModel;
        }
        
        public boolean isDetached() {
            return this.getDetachedModel() != null;
        }
        
        public TrackedNodeData observerAdded() {
            return new TrackedNodeData(this.node, this.observerCount + 1, this.getDetachedModel());
        }
        
        public TrackedNodeData observerRemoved() {
            return (this.observerCount <= 1) ? null : new TrackedNodeData(this.node, this.observerCount - 1, this.getDetachedModel());
        }
        
        public TrackedNodeData updateNode(final ImmutableNode newNode) {
            return new TrackedNodeData(newNode, this.observerCount, this.getDetachedModel());
        }
        
        public TrackedNodeData detach(final ImmutableNode newNode) {
            final ImmutableNode newTrackedNode = (newNode != null) ? newNode : this.getNode();
            return new TrackedNodeData(newTrackedNode, this.observerCount, new InMemoryNodeModel(newTrackedNode));
        }
    }
}
