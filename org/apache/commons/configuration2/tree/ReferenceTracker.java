// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class ReferenceTracker
{
    private final Map<ImmutableNode, Object> references;
    private final List<Object> removedReferences;
    
    private ReferenceTracker(final Map<ImmutableNode, Object> refs, final List<Object> removedRefs) {
        this.references = refs;
        this.removedReferences = removedRefs;
    }
    
    public ReferenceTracker() {
        this(Collections.emptyMap(), Collections.emptyList());
    }
    
    public ReferenceTracker addReferences(final Map<ImmutableNode, ?> refs) {
        final Map<ImmutableNode, Object> newRefs = new HashMap<ImmutableNode, Object>(this.references);
        newRefs.putAll(refs);
        return new ReferenceTracker(newRefs, this.removedReferences);
    }
    
    public ReferenceTracker updateReferences(final Map<ImmutableNode, ImmutableNode> replacedNodes, final Collection<ImmutableNode> removedNodes) {
        if (!this.references.isEmpty()) {
            Map<ImmutableNode, Object> newRefs = null;
            for (final Map.Entry<ImmutableNode, ImmutableNode> e : replacedNodes.entrySet()) {
                final Object ref = this.references.get(e.getKey());
                if (ref != null) {
                    if (newRefs == null) {
                        newRefs = new HashMap<ImmutableNode, Object>(this.references);
                    }
                    newRefs.put(e.getValue(), ref);
                    newRefs.remove(e.getKey());
                }
            }
            List<Object> newRemovedRefs = (newRefs != null) ? new LinkedList<Object>(this.removedReferences) : null;
            for (final ImmutableNode node : removedNodes) {
                final Object ref2 = this.references.get(node);
                if (ref2 != null) {
                    if (newRefs == null) {
                        newRefs = new HashMap<ImmutableNode, Object>(this.references);
                    }
                    newRefs.remove(node);
                    if (newRemovedRefs == null) {
                        newRemovedRefs = new LinkedList<Object>(this.removedReferences);
                    }
                    newRemovedRefs.add(ref2);
                }
            }
            if (newRefs != null) {
                return new ReferenceTracker(newRefs, newRemovedRefs);
            }
        }
        return this;
    }
    
    public Object getReference(final ImmutableNode node) {
        return this.references.get(node);
    }
    
    public List<Object> getRemovedReferences() {
        return Collections.unmodifiableList((List<?>)this.removedReferences);
    }
}
