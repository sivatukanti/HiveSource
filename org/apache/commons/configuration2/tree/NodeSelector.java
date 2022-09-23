// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

public class NodeSelector
{
    private final List<String> nodeKeys;
    
    public NodeSelector(final String key) {
        this(Collections.singletonList(key));
    }
    
    private NodeSelector(final List<String> keys) {
        this.nodeKeys = keys;
    }
    
    public ImmutableNode select(final ImmutableNode root, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler) {
        List<ImmutableNode> nodes = new LinkedList<ImmutableNode>();
        final Iterator<String> itKeys = this.nodeKeys.iterator();
        this.getFilteredResults(root, resolver, handler, itKeys.next(), nodes);
        while (itKeys.hasNext()) {
            final String currentKey = itKeys.next();
            final List<ImmutableNode> currentResults = new LinkedList<ImmutableNode>();
            for (final ImmutableNode currentRoot : nodes) {
                this.getFilteredResults(currentRoot, resolver, handler, currentKey, currentResults);
            }
            nodes = currentResults;
        }
        return (nodes.size() == 1) ? nodes.get(0) : null;
    }
    
    public NodeSelector subSelector(final String subKey) {
        final List<String> keys = new ArrayList<String>(this.nodeKeys.size() + 1);
        keys.addAll(this.nodeKeys);
        keys.add(subKey);
        return new NodeSelector(keys);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodeSelector)) {
            return false;
        }
        final NodeSelector c = (NodeSelector)obj;
        return this.nodeKeys.equals(c.nodeKeys);
    }
    
    @Override
    public int hashCode() {
        return this.nodeKeys.hashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("keys", this.nodeKeys).toString();
    }
    
    private void getFilteredResults(final ImmutableNode root, final NodeKeyResolver<ImmutableNode> resolver, final NodeHandler<ImmutableNode> handler, final String key, final List<ImmutableNode> nodes) {
        final List<QueryResult<ImmutableNode>> results = resolver.resolveKey(root, key, handler);
        for (final QueryResult<ImmutableNode> result : results) {
            if (!result.isAttributeResult()) {
                nodes.add(result.getNode());
            }
        }
    }
}
