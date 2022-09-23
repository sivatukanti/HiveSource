// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Map;
import java.util.Iterator;

public class OverrideCombiner extends NodeCombiner
{
    @Override
    public ImmutableNode combine(final ImmutableNode node1, final ImmutableNode node2) {
        final ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        for (final ImmutableNode child : node1.getChildren()) {
            final ImmutableNode child2 = this.canCombine(node1, node2, child);
            if (child2 != null) {
                result.addChild(this.combine(child, child2));
            }
            else {
                result.addChild(child);
            }
        }
        for (final ImmutableNode child : node2.getChildren()) {
            if (OverrideCombiner.HANDLER.getChildrenCount(node1, child.getNodeName()) < 1) {
                result.addChild(child);
            }
        }
        this.addAttributes(result, node1, node2);
        result.value((node1.getValue() != null) ? node1.getValue() : node2.getValue());
        return result.create();
    }
    
    protected void addAttributes(final ImmutableNode.Builder result, final ImmutableNode node1, final ImmutableNode node2) {
        result.addAttributes(node1.getAttributes());
        for (final String attr : node2.getAttributes().keySet()) {
            if (!node1.getAttributes().containsKey(attr)) {
                result.addAttribute(attr, OverrideCombiner.HANDLER.getAttributeValue(node2, attr));
            }
        }
    }
    
    protected ImmutableNode canCombine(final ImmutableNode node1, final ImmutableNode node2, final ImmutableNode child) {
        if (OverrideCombiner.HANDLER.getChildrenCount(node2, child.getNodeName()) == 1 && OverrideCombiner.HANDLER.getChildrenCount(node1, child.getNodeName()) == 1 && !this.isListNode(child)) {
            return OverrideCombiner.HANDLER.getChildren(node2, child.getNodeName()).get(0);
        }
        return null;
    }
}
