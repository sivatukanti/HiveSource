// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Collection;
import java.util.Map;

class ModelTransaction
{
    private static final int MAX_REPLACEMENTS = 200;
    private static final int LEVEL_UNKNOWN = -1;
    private final TreeData currentData;
    private final ImmutableNode queryRoot;
    private final NodeSelector rootNodeSelector;
    private final NodeKeyResolver<ImmutableNode> resolver;
    private final Map<ImmutableNode, ImmutableNode> replacementMapping;
    private final Map<ImmutableNode, ImmutableNode> replacedNodes;
    private final Map<ImmutableNode, ImmutableNode> parentMapping;
    private final Collection<ImmutableNode> addedNodes;
    private final Collection<ImmutableNode> removedNodes;
    private final Collection<ImmutableNode> allRemovedNodes;
    private final SortedMap<Integer, Map<ImmutableNode, Operations>> operations;
    private Map<ImmutableNode, Object> newReferences;
    private ImmutableNode newRoot;
    
    public ModelTransaction(final TreeData treeData, final NodeSelector selector, final NodeKeyResolver<ImmutableNode> resolver) {
        this.currentData = treeData;
        this.resolver = resolver;
        this.replacementMapping = this.getCurrentData().copyReplacementMapping();
        this.replacedNodes = new HashMap<ImmutableNode, ImmutableNode>();
        this.parentMapping = this.getCurrentData().copyParentMapping();
        this.operations = new TreeMap<Integer, Map<ImmutableNode, Operations>>();
        this.addedNodes = new LinkedList<ImmutableNode>();
        this.removedNodes = new LinkedList<ImmutableNode>();
        this.allRemovedNodes = new LinkedList<ImmutableNode>();
        this.queryRoot = this.initQueryRoot(treeData, selector);
        this.rootNodeSelector = selector;
    }
    
    public NodeKeyResolver<ImmutableNode> getResolver() {
        return this.resolver;
    }
    
    public ImmutableNode getQueryRoot() {
        return this.queryRoot;
    }
    
    public void addAddNodesOperation(final ImmutableNode parent, final Collection<? extends ImmutableNode> newNodes) {
        final ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNewNodes(newNodes);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }
    
    public void addAddNodeOperation(final ImmutableNode parent, final ImmutableNode newChild) {
        final ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNewNode(newChild);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }
    
    public void addAttributeOperation(final ImmutableNode target, final String name, final Object value) {
        this.fetchOperations(target, -1).addOperation(new AddAttributeOperation(name, value));
    }
    
    public void addAttributesOperation(final ImmutableNode target, final Map<String, Object> attributes) {
        this.fetchOperations(target, -1).addOperation(new AddAttributesOperation(attributes));
    }
    
    public void addRemoveNodeOperation(final ImmutableNode parent, final ImmutableNode node) {
        final ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNodeToRemove(node);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }
    
    public void addRemoveAttributeOperation(final ImmutableNode target, final String name) {
        this.fetchOperations(target, -1).addOperation(new RemoveAttributeOperation(name));
    }
    
    public void addClearNodeValueOperation(final ImmutableNode target) {
        this.addChangeNodeValueOperation(target, null);
    }
    
    public void addChangeNodeValueOperation(final ImmutableNode target, final Object newValue) {
        this.fetchOperations(target, -1).addOperation(new ChangeNodeValueOperation(newValue));
    }
    
    public void addChangeNodeNameOperation(final ImmutableNode target, final String newName) {
        this.fetchOperations(target, -1).addOperation(new ChangeNodeNameOperation(newName));
    }
    
    public void addNewReferences(final Map<ImmutableNode, ?> refs) {
        this.fetchReferenceMap().putAll(refs);
    }
    
    public void addNewReference(final ImmutableNode node, final Object ref) {
        this.fetchReferenceMap().put(node, ref);
    }
    
    public TreeData execute() {
        this.executeOperations();
        this.updateParentMapping();
        return new TreeData(this.newRoot, this.parentMapping, this.replacementMapping, this.currentData.getNodeTracker().update(this.newRoot, this.rootNodeSelector, this.getResolver(), this.getCurrentData()), this.updateReferenceTracker());
    }
    
    public TreeData getCurrentData() {
        return this.currentData;
    }
    
    ImmutableNode getParent(final ImmutableNode node) {
        return this.getCurrentData().getParent(node);
    }
    
    Operations fetchOperations(final ImmutableNode target, final int level) {
        final Integer nodeLevel = (level == -1) ? this.level(target) : level;
        Map<ImmutableNode, Operations> levelOperations = this.operations.get(nodeLevel);
        if (levelOperations == null) {
            levelOperations = new HashMap<ImmutableNode, Operations>();
            this.operations.put(nodeLevel, levelOperations);
        }
        Operations ops = levelOperations.get(target);
        if (ops == null) {
            ops = new Operations();
            levelOperations.put(target, ops);
        }
        return ops;
    }
    
    private ImmutableNode initQueryRoot(final TreeData treeData, final NodeSelector selector) {
        return (selector == null) ? treeData.getRootNode() : treeData.getNodeTracker().getTrackedNode(selector);
    }
    
    private int level(final ImmutableNode node) {
        ImmutableNode current = this.getCurrentData().getParent(node);
        int level = 0;
        while (current != null) {
            ++level;
            current = this.getCurrentData().getParent(current);
        }
        return level;
    }
    
    private void executeOperations() {
        while (!this.operations.isEmpty()) {
            final Integer level = this.operations.lastKey();
            final Map<ImmutableNode, Operations> levelOps = this.operations.remove(level);
            for (final Map.Entry<ImmutableNode, Operations> e : levelOps.entrySet()) {
                e.getValue().apply(e.getKey(), level);
            }
        }
    }
    
    private void updateParentMapping() {
        this.replacementMapping.putAll(this.replacedNodes);
        if (this.replacementMapping.size() > 200) {
            this.rebuildParentMapping();
        }
        else {
            this.updateParentMappingForAddedNodes();
            this.updateParentMappingForRemovedNodes();
        }
    }
    
    private void rebuildParentMapping() {
        this.replacementMapping.clear();
        this.parentMapping.clear();
        InMemoryNodeModel.updateParentMapping(this.parentMapping, this.newRoot);
    }
    
    private void updateParentMappingForAddedNodes() {
        for (final ImmutableNode node : this.addedNodes) {
            InMemoryNodeModel.updateParentMapping(this.parentMapping, node);
        }
    }
    
    private void updateParentMappingForRemovedNodes() {
        for (final ImmutableNode node : this.removedNodes) {
            this.removeNodesFromParentAndReplacementMapping(node);
        }
    }
    
    private void removeNodesFromParentAndReplacementMapping(final ImmutableNode root) {
        NodeTreeWalker.INSTANCE.walkBFS(root, new ConfigurationNodeVisitorAdapter<ImmutableNode>() {
            @Override
            public void visitBeforeChildren(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
                ModelTransaction.this.allRemovedNodes.add(node);
                ModelTransaction.this.parentMapping.remove(node);
                ModelTransaction.this.removeNodeFromReplacementMapping(node);
            }
        }, this.getCurrentData());
    }
    
    private void removeNodeFromReplacementMapping(final ImmutableNode node) {
        ImmutableNode replacement = node;
        do {
            replacement = this.replacementMapping.remove(replacement);
        } while (replacement != null);
    }
    
    private ReferenceTracker updateReferenceTracker() {
        ReferenceTracker tracker = this.currentData.getReferenceTracker();
        if (this.newReferences != null) {
            tracker = tracker.addReferences(this.newReferences);
        }
        return tracker.updateReferences(this.replacedNodes, this.allRemovedNodes);
    }
    
    private Map<ImmutableNode, Object> fetchReferenceMap() {
        if (this.newReferences == null) {
            this.newReferences = new HashMap<ImmutableNode, Object>();
        }
        return this.newReferences;
    }
    
    private static <E> Collection<E> concatenate(final Collection<E> col1, final Collection<? extends E> col2) {
        if (col2 == null) {
            return col1;
        }
        final Collection<E> result = (col1 != null) ? col1 : new ArrayList<E>(col2.size());
        result.addAll(col2);
        return result;
    }
    
    private static <E> Set<E> concatenate(final Set<E> set1, final Set<? extends E> set2) {
        if (set2 == null) {
            return set1;
        }
        final Set<E> result = (set1 != null) ? set1 : new HashSet<E>();
        result.addAll(set2);
        return result;
    }
    
    private static <K, V> Map<K, V> concatenate(final Map<K, V> map1, final Map<? extends K, ? extends V> map2) {
        if (map2 == null) {
            return map1;
        }
        final Map<K, V> result = (map1 != null) ? map1 : new HashMap<K, V>();
        result.putAll(map2);
        return result;
    }
    
    private static <E> Collection<E> append(final Collection<E> col, final E node) {
        final Collection<E> result = (col != null) ? col : new LinkedList<E>();
        result.add(node);
        return result;
    }
    
    private static <E> Set<E> append(final Set<E> col, final E elem) {
        final Set<E> result = (col != null) ? col : new HashSet<E>();
        result.add(elem);
        return result;
    }
    
    private static <K, V> Map<K, V> append(final Map<K, V> map, final K key, final V value) {
        final Map<K, V> result = (map != null) ? map : new HashMap<K, V>();
        result.put(key, value);
        return result;
    }
    
    private abstract class Operation
    {
        protected abstract ImmutableNode apply(final ImmutableNode p0, final Operations p1);
    }
    
    private class ChildrenUpdateOperation extends Operation
    {
        private Collection<ImmutableNode> newNodes;
        private Set<ImmutableNode> nodesToRemove;
        private Map<ImmutableNode, ImmutableNode> nodesToReplace;
        
        public void combine(final ChildrenUpdateOperation op) {
            this.newNodes = (Collection<ImmutableNode>)concatenate((Collection<Object>)this.newNodes, op.newNodes);
            this.nodesToReplace = (Map<ImmutableNode, ImmutableNode>)concatenate((Map<Object, Object>)this.nodesToReplace, op.nodesToReplace);
            this.nodesToRemove = (Set<ImmutableNode>)concatenate((Set<Object>)this.nodesToRemove, op.nodesToRemove);
        }
        
        public void addNewNode(final ImmutableNode node) {
            this.newNodes = (Collection<ImmutableNode>)append(this.newNodes, node);
        }
        
        public void addNewNodes(final Collection<? extends ImmutableNode> nodes) {
            this.newNodes = (Collection<ImmutableNode>)concatenate((Collection<Object>)this.newNodes, nodes);
        }
        
        public void addNodeToReplace(final ImmutableNode org, final ImmutableNode replacement) {
            this.nodesToReplace = (Map<ImmutableNode, ImmutableNode>)append(this.nodesToReplace, org, replacement);
        }
        
        public void addNodeToRemove(final ImmutableNode node) {
            this.nodesToRemove = (Set<ImmutableNode>)append(this.nodesToRemove, node);
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            final Map<ImmutableNode, ImmutableNode> replacements = this.fetchReplacementMap();
            final Set<ImmutableNode> removals = this.fetchRemovalSet();
            final List<ImmutableNode> resultNodes = new LinkedList<ImmutableNode>();
            for (final ImmutableNode nd : target.getChildren()) {
                final ImmutableNode repl = replacements.get(nd);
                if (repl != null) {
                    resultNodes.add(repl);
                    ModelTransaction.this.replacedNodes.put(nd, repl);
                }
                else if (removals.contains(nd)) {
                    ModelTransaction.this.removedNodes.add(nd);
                }
                else {
                    resultNodes.add(nd);
                }
            }
            concatenate((Collection<Object>)resultNodes, this.newNodes);
            operations.newNodesAdded(this.newNodes);
            return target.replaceChildren(resultNodes);
        }
        
        private Map<ImmutableNode, ImmutableNode> fetchReplacementMap() {
            return (this.nodesToReplace != null) ? this.nodesToReplace : Collections.emptyMap();
        }
        
        private Set<ImmutableNode> fetchRemovalSet() {
            return (this.nodesToRemove != null) ? this.nodesToRemove : Collections.emptySet();
        }
    }
    
    private class AddAttributeOperation extends Operation
    {
        private final String attributeName;
        private final Object attributeValue;
        
        public AddAttributeOperation(final String name, final Object value) {
            this.attributeName = name;
            this.attributeValue = value;
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            return target.setAttribute(this.attributeName, this.attributeValue);
        }
    }
    
    private class AddAttributesOperation extends Operation
    {
        private final Map<String, Object> attributes;
        
        public AddAttributesOperation(final Map<String, Object> attrs) {
            this.attributes = attrs;
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            return target.setAttributes(this.attributes);
        }
    }
    
    private class RemoveAttributeOperation extends Operation
    {
        private final String attributeName;
        
        public RemoveAttributeOperation(final String name) {
            this.attributeName = name;
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            return target.removeAttribute(this.attributeName);
        }
    }
    
    private class ChangeNodeValueOperation extends Operation
    {
        private final Object newValue;
        
        public ChangeNodeValueOperation(final Object value) {
            this.newValue = value;
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            return target.setValue(this.newValue);
        }
    }
    
    private class ChangeNodeNameOperation extends Operation
    {
        private final String newName;
        
        public ChangeNodeNameOperation(final String name) {
            this.newName = name;
        }
        
        @Override
        protected ImmutableNode apply(final ImmutableNode target, final Operations operations) {
            return target.setName(this.newName);
        }
    }
    
    private class Operations
    {
        private ChildrenUpdateOperation childrenOperation;
        private Collection<Operation> operations;
        private Collection<ImmutableNode> addedNodesInOperation;
        
        public void addChildrenOperation(final ChildrenUpdateOperation co) {
            if (this.childrenOperation == null) {
                this.childrenOperation = co;
            }
            else {
                this.childrenOperation.combine(co);
            }
        }
        
        public void addOperation(final Operation op) {
            this.operations = (Collection<Operation>)append(this.operations, op);
        }
        
        public void newNodesAdded(final Collection<ImmutableNode> newNodes) {
            this.addedNodesInOperation = (Collection<ImmutableNode>)concatenate((Collection<Object>)this.addedNodesInOperation, newNodes);
        }
        
        public void apply(final ImmutableNode target, final int level) {
            ImmutableNode node = target;
            if (this.childrenOperation != null) {
                node = this.childrenOperation.apply(node, this);
            }
            if (this.operations != null) {
                for (final Operation op : this.operations) {
                    node = op.apply(node, this);
                }
            }
            this.handleAddedNodes(node);
            if (level == 0) {
                ModelTransaction.this.newRoot = node;
                ModelTransaction.this.replacedNodes.put(target, node);
            }
            else {
                this.propagateChange(target, node, level);
            }
        }
        
        private void propagateChange(final ImmutableNode target, final ImmutableNode node, final int level) {
            final ImmutableNode parent = ModelTransaction.this.getParent(target);
            final ChildrenUpdateOperation co = new ChildrenUpdateOperation();
            if (InMemoryNodeModel.checkIfNodeDefined(node)) {
                co.addNodeToReplace(target, node);
            }
            else {
                co.addNodeToRemove(target);
            }
            ModelTransaction.this.fetchOperations(parent, level - 1).addChildrenOperation(co);
        }
        
        private void handleAddedNodes(final ImmutableNode node) {
            if (this.addedNodesInOperation != null) {
                for (final ImmutableNode child : this.addedNodesInOperation) {
                    ModelTransaction.this.parentMapping.put(child, node);
                    ModelTransaction.this.addedNodes.add(child);
                }
            }
        }
    }
}
