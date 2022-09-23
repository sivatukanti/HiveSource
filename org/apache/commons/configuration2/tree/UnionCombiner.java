// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class UnionCombiner extends NodeCombiner
{
    @Override
    public ImmutableNode combine(final ImmutableNode node1, final ImmutableNode node2) {
        final ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        result.addAttributes(node2.getAttributes());
        result.addAttributes(node1.getAttributes());
        final List<ImmutableNode> children2 = new LinkedList<ImmutableNode>(node2.getChildren());
        for (final ImmutableNode child1 : node1.getChildren()) {
            final ImmutableNode child2 = this.findCombineNode(node1, node2, child1);
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
    
    protected ImmutableNode findCombineNode(final ImmutableNode node1, final ImmutableNode node2, final ImmutableNode child) {
        if (child.getValue() == null && !this.isListNode(child) && UnionCombiner.HANDLER.getChildrenCount(node1, child.getNodeName()) == 1 && UnionCombiner.HANDLER.getChildrenCount(node2, child.getNodeName()) == 1) {
            final ImmutableNode child2 = UnionCombiner.HANDLER.getChildren(node2, child.getNodeName()).get(0);
            if (child2.getValue() == null) {
                return child2;
            }
        }
        return null;
    }
}
