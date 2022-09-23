// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.ObjectUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;

public class MergeCombiner extends NodeCombiner
{
    @Override
    public ImmutableNode combine(final ImmutableNode node1, final ImmutableNode node2) {
        final ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        result.value(node1.getValue());
        this.addAttributes(result, node1, node2);
        final List<ImmutableNode> children2 = new LinkedList<ImmutableNode>(node2.getChildren());
        for (final ImmutableNode child1 : node1.getChildren()) {
            final ImmutableNode child2 = this.canCombine(node2, child1, children2);
            if (child2 != null) {
                result.addChild(this.combine(child1, child2));
                children2.remove(child2);
            }
            else {
                result.addChild(child1);
            }
        }
        for (final ImmutableNode c : children2) {
            result.addChild(c);
        }
        return result.create();
    }
    
    protected void addAttributes(final ImmutableNode.Builder result, final ImmutableNode node1, final ImmutableNode node2) {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.putAll(node1.getAttributes());
        for (final Map.Entry<String, Object> e : node2.getAttributes().entrySet()) {
            if (!attributes.containsKey(e.getKey())) {
                attributes.put(e.getKey(), e.getValue());
            }
        }
        result.addAttributes(attributes);
    }
    
    protected ImmutableNode canCombine(final ImmutableNode node2, final ImmutableNode child, final List<ImmutableNode> children2) {
        final Map<String, Object> attrs1 = child.getAttributes();
        final List<ImmutableNode> nodes = new ArrayList<ImmutableNode>();
        final List<ImmutableNode> children3 = MergeCombiner.HANDLER.getChildren(node2, child.getNodeName());
        for (final ImmutableNode node3 : children3) {
            if (matchAttributes(attrs1, node3)) {
                nodes.add(node3);
            }
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        if (nodes.size() > 1 && !this.isListNode(child)) {
            for (final ImmutableNode node3 : nodes) {
                children2.remove(node3);
            }
        }
        return null;
    }
    
    private static boolean matchAttributes(final Map<String, Object> attrs1, final ImmutableNode node) {
        final Map<String, Object> attrs2 = node.getAttributes();
        for (final Map.Entry<String, Object> e : attrs1.entrySet()) {
            if (attrs2.containsKey(e.getKey()) && !ObjectUtils.equals(e.getValue(), attrs2.get(e.getKey()))) {
                return false;
            }
        }
        return true;
    }
}
