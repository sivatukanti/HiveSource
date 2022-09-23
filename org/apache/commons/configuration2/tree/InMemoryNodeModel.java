// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryNodeModel implements NodeModel<ImmutableNode>
{
    private static final NodeHandler<ImmutableNode> DUMMY_HANDLER;
    private final AtomicReference<TreeData> structure;
    
    public InMemoryNodeModel() {
        this(null);
    }
    
    public InMemoryNodeModel(final ImmutableNode root) {
        this.structure = new AtomicReference<TreeData>(this.createTreeData(initialRootNode(root), null));
    }
    
    public ImmutableNode getRootNode() {
        return this.getTreeData().getRootNode();
    }
    
    @Override
    public NodeHandler<ImmutableNode> getNodeHandler() {
        return this.getReferenceNodeHandler();
    }
    
    @Override
    public void addProperty(final String key, final Iterable<?> values, final NodeKeyResolver<ImmutableNode> resolver) {
        this.addProperty(key, null, values, resolver);
    }
    
    public void addProperty(final String key, final NodeSelector selector, final Iterable<?> values, final NodeKeyResolver<ImmutableNode> resolver) {
        if (valuesNotEmpty(values)) {
            this.updateModel(new TransactionInitializer() {
                @Override
                public boolean initTransaction(final ModelTransaction tx) {
                    InMemoryNodeModel.this.initializeAddTransaction(tx, key, values, resolver);
                    return true;
                }
            }, selector, resolver);
        }
    }
    
    @Override
    public void addNodes(final String key, final Collection<? extends ImmutableNode> nodes, final NodeKeyResolver<ImmutableNode> resolver) {
        this.addNodes(key, null, nodes, resolver);
    }
    
    public void addNodes(final String key, final NodeSelector selector, final Collection<? extends ImmutableNode> nodes, final NodeKeyResolver<ImmutableNode> resolver) {
        if (nodes != null && !nodes.isEmpty()) {
            this.updateModel(new TransactionInitializer() {
                @Override
                public boolean initTransaction(final ModelTransaction tx) {
                    final List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, tx.getCurrentData());
                    if (results.size() == 1) {
                        if (results.get(0).isAttributeResult()) {
                            throw attributeKeyException(key);
                        }
                        tx.addAddNodesOperation(results.get(0).getNode(), nodes);
                    }
                    else {
                        final NodeAddData<ImmutableNode> addData = resolver.resolveAddKey(tx.getQueryRoot(), key, tx.getCurrentData());
                        if (addData.isAttribute()) {
                            throw attributeKeyException(key);
                        }
                        final ImmutableNode newNode = new ImmutableNode.Builder(nodes.size()).name(addData.getNewNodeName()).addChildren(nodes).create();
                        addNodesByAddData(tx, addData, Collections.singleton(newNode));
                    }
                    return true;
                }
            }, selector, resolver);
        }
    }
    
    @Override
    public void setProperty(final String key, final Object value, final NodeKeyResolver<ImmutableNode> resolver) {
        this.setProperty(key, null, value, resolver);
    }
    
    public void setProperty(final String key, final NodeSelector selector, final Object value, final NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(new TransactionInitializer() {
            @Override
            public boolean initTransaction(final ModelTransaction tx) {
                boolean added = false;
                final NodeUpdateData<ImmutableNode> updateData = resolver.resolveUpdateKey(tx.getQueryRoot(), key, value, tx.getCurrentData());
                if (!updateData.getNewValues().isEmpty()) {
                    InMemoryNodeModel.this.initializeAddTransaction(tx, key, updateData.getNewValues(), resolver);
                    added = true;
                }
                final boolean cleared = initializeClearTransaction(tx, updateData.getRemovedNodes());
                final boolean updated = initializeUpdateTransaction(tx, updateData.getChangedValues());
                return added || cleared || updated;
            }
        }, selector, resolver);
    }
    
    @Override
    public List<QueryResult<ImmutableNode>> clearTree(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        return this.clearTree(key, null, resolver);
    }
    
    public List<QueryResult<ImmutableNode>> clearTree(final String key, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver) {
        final List<QueryResult<ImmutableNode>> removedElements = new LinkedList<QueryResult<ImmutableNode>>();
        this.updateModel(new TransactionInitializer() {
            @Override
            public boolean initTransaction(final ModelTransaction tx) {
                boolean changes = false;
                final TreeData currentStructure = tx.getCurrentData();
                final List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, currentStructure);
                removedElements.clear();
                removedElements.addAll(results);
                for (final QueryResult<ImmutableNode> result : results) {
                    if (result.isAttributeResult()) {
                        tx.addRemoveAttributeOperation(result.getNode(), result.getAttributeName());
                    }
                    else {
                        if (result.getNode() == currentStructure.getRootNode()) {
                            InMemoryNodeModel.this.clear(resolver);
                            return false;
                        }
                        tx.addRemoveNodeOperation(currentStructure.getParent(result.getNode()), result.getNode());
                    }
                    changes = true;
                }
                return changes;
            }
        }, selector, resolver);
        return removedElements;
    }
    
    @Override
    public void clearProperty(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        this.clearProperty(key, null, resolver);
    }
    
    public void clearProperty(final String key, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(new TransactionInitializer() {
            @Override
            public boolean initTransaction(final ModelTransaction tx) {
                final List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, tx.getCurrentData());
                return initializeClearTransaction(tx, results);
            }
        }, selector, resolver);
    }
    
    @Override
    public void clear(final NodeKeyResolver<ImmutableNode> resolver) {
        final ImmutableNode newRoot = new ImmutableNode.Builder().name(this.getRootNode().getNodeName()).create();
        this.setRootNode(newRoot);
    }
    
    @Override
    public ImmutableNode getInMemoryRepresentation() {
        return this.getTreeData().getRootNode();
    }
    
    @Override
    public void setRootNode(final ImmutableNode newRoot) {
        this.structure.set(this.createTreeData(initialRootNode(newRoot), this.structure.get()));
    }
    
    public void replaceRoot(final ImmutableNode newRoot, final NodeKeyResolver<ImmutableNode> resolver) {
        if (newRoot == null) {
            throw new IllegalArgumentException("Replaced root node must not be null!");
        }
        final TreeData current = this.structure.get();
        final TreeData temp = this.createTreeDataForRootAndTracker(newRoot, current.getNodeTracker());
        this.structure.set(temp.updateNodeTracker(temp.getNodeTracker().update(newRoot, null, resolver, temp)));
    }
    
    public void mergeRoot(final ImmutableNode node, final String rootName, final Map<ImmutableNode, ?> references, final Object rootRef, final NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(new TransactionInitializer() {
            @Override
            public boolean initTransaction(final ModelTransaction tx) {
                final TreeData current = tx.getCurrentData();
                final String newRootName = determineRootName(current.getRootNode(), node, rootName);
                if (newRootName != null) {
                    tx.addChangeNodeNameOperation(current.getRootNode(), newRootName);
                }
                tx.addAddNodesOperation(current.getRootNode(), node.getChildren());
                tx.addAttributesOperation(current.getRootNode(), node.getAttributes());
                if (node.getValue() != null) {
                    tx.addChangeNodeValueOperation(current.getRootNode(), node.getValue());
                }
                if (references != null) {
                    tx.addNewReferences(references);
                }
                if (rootRef != null) {
                    tx.addNewReference(current.getRootNode(), rootRef);
                }
                return true;
            }
        }, null, resolver);
    }
    
    public void trackNode(final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver) {
        boolean done;
        do {
            final TreeData current = this.structure.get();
            final NodeTracker newTracker = current.getNodeTracker().trackNode(current.getRootNode(), selector, resolver, current);
            done = this.structure.compareAndSet(current, current.updateNodeTracker(newTracker));
        } while (!done);
    }
    
    public Collection<NodeSelector> selectAndTrackNodes(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        final Mutable<Collection<NodeSelector>> refSelectors = new MutableObject<Collection<NodeSelector>>();
        boolean done;
        do {
            final TreeData current = this.structure.get();
            final List<ImmutableNode> nodes = resolver.resolveNodeKey(current.getRootNode(), key, current);
            if (nodes.isEmpty()) {
                return (Collection<NodeSelector>)Collections.emptyList();
            }
            done = this.structure.compareAndSet(current, createSelectorsForTrackedNodes(refSelectors, nodes, current, resolver));
        } while (!done);
        return refSelectors.getValue();
    }
    
    public Collection<NodeSelector> trackChildNodes(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        final Mutable<Collection<NodeSelector>> refSelectors = new MutableObject<Collection<NodeSelector>>();
        boolean done;
        do {
            refSelectors.setValue((Collection<NodeSelector>)Collections.emptyList());
            final TreeData current = this.structure.get();
            final List<ImmutableNode> nodes = resolver.resolveNodeKey(current.getRootNode(), key, current);
            if (nodes.size() == 1) {
                final ImmutableNode node = nodes.get(0);
                done = (node.getChildren().isEmpty() || this.structure.compareAndSet(current, createSelectorsForTrackedNodes(refSelectors, node.getChildren(), current, resolver)));
            }
            else {
                done = true;
            }
        } while (!done);
        return refSelectors.getValue();
    }
    
    public NodeSelector trackChildNodeWithCreation(final String key, final String childName, final NodeKeyResolver<ImmutableNode> resolver) {
        final MutableObject<NodeSelector> refSelector = new MutableObject<NodeSelector>();
        boolean done;
        do {
            final TreeData current = this.structure.get();
            final List<ImmutableNode> nodes = resolver.resolveNodeKey(current.getRootNode(), key, current);
            if (nodes.size() != 1) {
                throw new ConfigurationRuntimeException("Key does not select a single node: " + key);
            }
            final ImmutableNode parent = nodes.get(0);
            final TreeData newData = createDataWithTrackedChildNode(current, parent, childName, resolver, refSelector);
            done = this.structure.compareAndSet(current, newData);
        } while (!done);
        return refSelector.getValue();
    }
    
    public ImmutableNode getTrackedNode(final NodeSelector selector) {
        return this.structure.get().getNodeTracker().getTrackedNode(selector);
    }
    
    public void replaceTrackedNode(final NodeSelector selector, final ImmutableNode newNode) {
        if (newNode == null) {
            throw new IllegalArgumentException("Replacement node must not be null!");
        }
        boolean done;
        do {
            final TreeData currentData = this.structure.get();
            done = (this.replaceDetachedTrackedNode(currentData, selector, newNode) || this.replaceActiveTrackedNode(currentData, selector, newNode));
        } while (!done);
    }
    
    public NodeHandler<ImmutableNode> getTrackedNodeHandler(final NodeSelector selector) {
        final TreeData currentData = this.structure.get();
        final InMemoryNodeModel detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector);
        return (detachedNodeModel != null) ? detachedNodeModel.getNodeHandler() : new TrackedNodeHandler(currentData.getNodeTracker().getTrackedNode(selector), currentData);
    }
    
    public boolean isTrackedNodeDetached(final NodeSelector selector) {
        return this.structure.get().getNodeTracker().isTrackedNodeDetached(selector);
    }
    
    public void untrackNode(final NodeSelector selector) {
        boolean done;
        do {
            final TreeData current = this.structure.get();
            final NodeTracker newTracker = current.getNodeTracker().untrackNode(selector);
            done = this.structure.compareAndSet(current, current.updateNodeTracker(newTracker));
        } while (!done);
    }
    
    public ReferenceNodeHandler getReferenceNodeHandler() {
        return this.getTreeData();
    }
    
    TreeData getTreeData() {
        return this.structure.get();
    }
    
    static void updateParentMapping(final Map<ImmutableNode, ImmutableNode> parents, final ImmutableNode root) {
        NodeTreeWalker.INSTANCE.walkBFS(root, new ConfigurationNodeVisitorAdapter<ImmutableNode>() {
            @Override
            public void visitBeforeChildren(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
                for (final ImmutableNode c : node.getChildren()) {
                    parents.put(c, node);
                }
            }
        }, InMemoryNodeModel.DUMMY_HANDLER);
    }
    
    static boolean checkIfNodeDefined(final ImmutableNode node) {
        return node.getValue() != null || !node.getChildren().isEmpty() || !node.getAttributes().isEmpty();
    }
    
    private void initializeAddTransaction(final ModelTransaction tx, final String key, final Iterable<?> values, final NodeKeyResolver<ImmutableNode> resolver) {
        final NodeAddData<ImmutableNode> addData = resolver.resolveAddKey(tx.getQueryRoot(), key, tx.getCurrentData());
        if (addData.isAttribute()) {
            addAttributeProperty(tx, addData, values);
        }
        else {
            addNodeProperty(tx, addData, values);
        }
    }
    
    private TreeData createTreeData(final ImmutableNode root, final TreeData current) {
        final NodeTracker newTracker = (current != null) ? current.getNodeTracker().detachAllTrackedNodes() : new NodeTracker();
        return this.createTreeDataForRootAndTracker(root, newTracker);
    }
    
    private TreeData createTreeDataForRootAndTracker(final ImmutableNode root, final NodeTracker newTracker) {
        return new TreeData(root, this.createParentMapping(root), Collections.emptyMap(), newTracker, new ReferenceTracker());
    }
    
    private static void addNodeProperty(final ModelTransaction tx, final NodeAddData<ImmutableNode> addData, final Iterable<?> values) {
        final Collection<ImmutableNode> newNodes = createNodesToAdd(addData.getNewNodeName(), values);
        addNodesByAddData(tx, addData, newNodes);
    }
    
    private static void addNodesByAddData(final ModelTransaction tx, final NodeAddData<ImmutableNode> addData, final Collection<ImmutableNode> newNodes) {
        if (addData.getPathNodes().isEmpty()) {
            tx.addAddNodesOperation(addData.getParent(), newNodes);
        }
        else {
            final ImmutableNode newChild = createNodeToAddWithPath(addData, newNodes);
            tx.addAddNodeOperation(addData.getParent(), newChild);
        }
    }
    
    private static void addAttributeProperty(final ModelTransaction tx, final NodeAddData<ImmutableNode> addData, final Iterable<?> values) {
        if (addData.getPathNodes().isEmpty()) {
            tx.addAttributeOperation(addData.getParent(), addData.getNewNodeName(), values.iterator().next());
        }
        else {
            final int pathNodeCount = addData.getPathNodes().size();
            final ImmutableNode childWithAttribute = new ImmutableNode.Builder().name(addData.getPathNodes().get(pathNodeCount - 1)).addAttribute(addData.getNewNodeName(), values.iterator().next()).create();
            final ImmutableNode newChild = (pathNodeCount > 1) ? createNodeOnPath(addData.getPathNodes().subList(0, pathNodeCount - 1).iterator(), Collections.singleton(childWithAttribute)) : childWithAttribute;
            tx.addAddNodeOperation(addData.getParent(), newChild);
        }
    }
    
    private static Collection<ImmutableNode> createNodesToAdd(final String newNodeName, final Iterable<?> values) {
        final Collection<ImmutableNode> nodes = new LinkedList<ImmutableNode>();
        for (final Object value : values) {
            nodes.add(new ImmutableNode.Builder().name(newNodeName).value(value).create());
        }
        return nodes;
    }
    
    private static ImmutableNode createNodeToAddWithPath(final NodeAddData<ImmutableNode> addData, final Collection<ImmutableNode> newNodes) {
        return createNodeOnPath(addData.getPathNodes().iterator(), newNodes);
    }
    
    private static ImmutableNode createNodeOnPath(final Iterator<String> it, final Collection<ImmutableNode> newNodes) {
        final String nodeName = it.next();
        ImmutableNode.Builder builder;
        if (it.hasNext()) {
            builder = new ImmutableNode.Builder(1);
            builder.addChild(createNodeOnPath(it, newNodes));
        }
        else {
            builder = new ImmutableNode.Builder(newNodes.size());
            builder.addChildren(newNodes);
        }
        return builder.name(nodeName).create();
    }
    
    private static boolean initializeClearTransaction(final ModelTransaction tx, final Collection<QueryResult<ImmutableNode>> results) {
        for (final QueryResult<ImmutableNode> result : results) {
            if (result.isAttributeResult()) {
                tx.addRemoveAttributeOperation(result.getNode(), result.getAttributeName());
            }
            else {
                tx.addClearNodeValueOperation(result.getNode());
            }
        }
        return !results.isEmpty();
    }
    
    private static boolean initializeUpdateTransaction(final ModelTransaction tx, final Map<QueryResult<ImmutableNode>, Object> changedValues) {
        for (final Map.Entry<QueryResult<ImmutableNode>, Object> e : changedValues.entrySet()) {
            if (e.getKey().isAttributeResult()) {
                tx.addAttributeOperation(e.getKey().getNode(), e.getKey().getAttributeName(), e.getValue());
            }
            else {
                tx.addChangeNodeValueOperation(e.getKey().getNode(), e.getValue());
            }
        }
        return !changedValues.isEmpty();
    }
    
    private static ImmutableNode initialRootNode(final ImmutableNode providedRoot) {
        return (providedRoot != null) ? providedRoot : new ImmutableNode.Builder().create();
    }
    
    private static String determineRootName(final ImmutableNode rootNode, final ImmutableNode node, final String rootName) {
        if (rootName != null) {
            return rootName;
        }
        if (rootNode.getNodeName() == null) {
            return node.getNodeName();
        }
        return null;
    }
    
    private Map<ImmutableNode, ImmutableNode> createParentMapping(final ImmutableNode root) {
        final Map<ImmutableNode, ImmutableNode> parents = new HashMap<ImmutableNode, ImmutableNode>();
        updateParentMapping(parents, root);
        return parents;
    }
    
    private void updateModel(final TransactionInitializer txInit, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver) {
        boolean done;
        do {
            final TreeData currentData = this.getTreeData();
            done = (this.executeTransactionOnDetachedTrackedNode(txInit, selector, currentData, resolver) || this.executeTransactionOnCurrentStructure(txInit, selector, currentData, resolver));
        } while (!done);
    }
    
    private boolean executeTransactionOnCurrentStructure(final TransactionInitializer txInit, final NodeSelector selector, final TreeData currentData, final NodeKeyResolver<ImmutableNode> resolver) {
        final ModelTransaction tx = new ModelTransaction(currentData, selector, resolver);
        boolean done;
        if (!txInit.initTransaction(tx)) {
            done = true;
        }
        else {
            final TreeData newData = tx.execute();
            done = this.structure.compareAndSet(tx.getCurrentData(), newData);
        }
        return done;
    }
    
    private boolean executeTransactionOnDetachedTrackedNode(final TransactionInitializer txInit, final NodeSelector selector, final TreeData currentData, final NodeKeyResolver<ImmutableNode> resolver) {
        if (selector != null) {
            final InMemoryNodeModel detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector);
            if (detachedNodeModel != null) {
                detachedNodeModel.updateModel(txInit, null, resolver);
                return true;
            }
        }
        return false;
    }
    
    private boolean replaceDetachedTrackedNode(final TreeData currentData, final NodeSelector selector, final ImmutableNode newNode) {
        final InMemoryNodeModel detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector);
        if (detachedNodeModel != null) {
            detachedNodeModel.setRootNode(newNode);
            return true;
        }
        return false;
    }
    
    private boolean replaceActiveTrackedNode(final TreeData currentData, final NodeSelector selector, final ImmutableNode newNode) {
        final NodeTracker newTracker = currentData.getNodeTracker().replaceAndDetachTrackedNode(selector, newNode);
        return this.structure.compareAndSet(currentData, currentData.updateNodeTracker(newTracker));
    }
    
    private static TreeData createSelectorsForTrackedNodes(final Mutable<Collection<NodeSelector>> refSelectors, final List<ImmutableNode> nodes, final TreeData current, final NodeKeyResolver<ImmutableNode> resolver) {
        final List<NodeSelector> selectors = new ArrayList<NodeSelector>(nodes.size());
        final Map<ImmutableNode, String> cache = new HashMap<ImmutableNode, String>();
        for (final ImmutableNode node : nodes) {
            selectors.add(new NodeSelector(resolver.nodeKey(node, cache, current)));
        }
        refSelectors.setValue(selectors);
        final NodeTracker newTracker = current.getNodeTracker().trackNodes(selectors, nodes);
        return current.updateNodeTracker(newTracker);
    }
    
    private static TreeData updateDataWithNewTrackedNode(final TreeData current, final ImmutableNode node, final NodeKeyResolver<ImmutableNode> resolver, final MutableObject<NodeSelector> refSelector) {
        final NodeSelector selector = new NodeSelector(resolver.nodeKey(node, new HashMap<ImmutableNode, String>(), current));
        refSelector.setValue(selector);
        final NodeTracker newTracker = current.getNodeTracker().trackNodes(Collections.singleton(selector), Collections.singleton(node));
        return current.updateNodeTracker(newTracker);
    }
    
    private static TreeData createDataWithTrackedChildNode(final TreeData current, final ImmutableNode parent, final String childName, final NodeKeyResolver<ImmutableNode> resolver, final MutableObject<NodeSelector> refSelector) {
        final List<ImmutableNode> namedChildren = current.getChildren(parent, childName);
        TreeData newData;
        if (!namedChildren.isEmpty()) {
            newData = updateDataWithNewTrackedNode(current, namedChildren.get(0), resolver, refSelector);
        }
        else {
            final ImmutableNode child = new ImmutableNode.Builder().name(childName).create();
            final ModelTransaction tx = new ModelTransaction(current, null, resolver);
            tx.addAddNodeOperation(parent, child);
            newData = updateDataWithNewTrackedNode(tx.execute(), child, resolver, refSelector);
        }
        return newData;
    }
    
    private static boolean valuesNotEmpty(final Iterable<?> values) {
        return values.iterator().hasNext();
    }
    
    private static RuntimeException attributeKeyException(final String key) {
        return new IllegalArgumentException("New nodes cannot be added to an attribute key: " + key);
    }
    
    static {
        DUMMY_HANDLER = new TreeData(null, Collections.emptyMap(), Collections.emptyMap(), null, new ReferenceTracker());
    }
    
    private interface TransactionInitializer
    {
        boolean initTransaction(final ModelTransaction p0);
    }
}
