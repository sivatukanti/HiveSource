// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class TreeData extends AbstractImmutableNodeHandler implements ReferenceNodeHandler
{
    private final ImmutableNode root;
    private final Map<ImmutableNode, ImmutableNode> parentMapping;
    private final Map<ImmutableNode, ImmutableNode> replacementMapping;
    private final Map<ImmutableNode, ImmutableNode> inverseReplacementMapping;
    private final NodeTracker nodeTracker;
    private final ReferenceTracker referenceTracker;
    
    public TreeData(final ImmutableNode root, final Map<ImmutableNode, ImmutableNode> parentMapping, final Map<ImmutableNode, ImmutableNode> replacements, final NodeTracker tracker, final ReferenceTracker refTracker) {
        this.root = root;
        this.parentMapping = parentMapping;
        this.replacementMapping = replacements;
        this.inverseReplacementMapping = this.createInverseMapping(replacements);
        this.nodeTracker = tracker;
        this.referenceTracker = refTracker;
    }
    
    @Override
    public ImmutableNode getRootNode() {
        return this.root;
    }
    
    public NodeTracker getNodeTracker() {
        return this.nodeTracker;
    }
    
    public ReferenceTracker getReferenceTracker() {
        return this.referenceTracker;
    }
    
    @Override
    public ImmutableNode getParent(final ImmutableNode node) {
        if (node == this.getRootNode()) {
            return null;
        }
        final ImmutableNode org = handleReplacements(node, this.inverseReplacementMapping);
        final ImmutableNode parent = this.parentMapping.get(org);
        if (parent == null) {
            throw new IllegalArgumentException("Cannot determine parent! " + node + " is not part of this model.");
        }
        return handleReplacements(parent, this.replacementMapping);
    }
    
    public Map<ImmutableNode, ImmutableNode> copyParentMapping() {
        return new HashMap<ImmutableNode, ImmutableNode>(this.parentMapping);
    }
    
    public Map<ImmutableNode, ImmutableNode> copyReplacementMapping() {
        return new HashMap<ImmutableNode, ImmutableNode>(this.replacementMapping);
    }
    
    public TreeData updateNodeTracker(final NodeTracker newTracker) {
        return new TreeData(this.root, this.parentMapping, this.replacementMapping, newTracker, this.referenceTracker);
    }
    
    public TreeData updateReferenceTracker(final ReferenceTracker newTracker) {
        return new TreeData(this.root, this.parentMapping, this.replacementMapping, this.nodeTracker, newTracker);
    }
    
    @Override
    public Object getReference(final ImmutableNode node) {
        return this.getReferenceTracker().getReference(node);
    }
    
    @Override
    public List<Object> removedReferences() {
        return this.getReferenceTracker().getRemovedReferences();
    }
    
    private static ImmutableNode handleReplacements(final ImmutableNode replace, final Map<ImmutableNode, ImmutableNode> mapping) {
        ImmutableNode node = replace;
        ImmutableNode org;
        do {
            org = mapping.get(node);
            if (org != null) {
                node = org;
            }
        } while (org != null);
        return node;
    }
    
    private Map<ImmutableNode, ImmutableNode> createInverseMapping(final Map<ImmutableNode, ImmutableNode> replacements) {
        final Map<ImmutableNode, ImmutableNode> inverseMapping = new HashMap<ImmutableNode, ImmutableNode>();
        for (final Map.Entry<ImmutableNode, ImmutableNode> e : replacements.entrySet()) {
            inverseMapping.put(e.getValue(), e.getKey());
        }
        return inverseMapping;
    }
}
