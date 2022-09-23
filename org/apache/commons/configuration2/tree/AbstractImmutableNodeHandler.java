// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractImmutableNodeHandler implements NodeHandler<ImmutableNode>
{
    @Override
    public String nodeName(final ImmutableNode node) {
        return node.getNodeName();
    }
    
    @Override
    public Object getValue(final ImmutableNode node) {
        return node.getValue();
    }
    
    @Override
    public List<ImmutableNode> getChildren(final ImmutableNode node) {
        return node.getChildren();
    }
    
    @Override
    public <C> int getMatchingChildrenCount(final ImmutableNode node, final NodeMatcher<C> matcher, final C criterion) {
        return this.getMatchingChildren(node, matcher, criterion).size();
    }
    
    @Override
    public <C> List<ImmutableNode> getMatchingChildren(final ImmutableNode node, final NodeMatcher<C> matcher, final C criterion) {
        final List<ImmutableNode> result = new ArrayList<ImmutableNode>(node.getChildren().size());
        for (final ImmutableNode c : node.getChildren()) {
            if (matcher.matches(c, this, criterion)) {
                result.add(c);
            }
        }
        return Collections.unmodifiableList((List<? extends ImmutableNode>)result);
    }
    
    @Override
    public List<ImmutableNode> getChildren(final ImmutableNode node, final String name) {
        return this.getMatchingChildren(node, (NodeMatcher<String>)NodeNameMatchers.EQUALS, name);
    }
    
    @Override
    public ImmutableNode getChild(final ImmutableNode node, final int index) {
        return node.getChildren().get(index);
    }
    
    @Override
    public int indexOfChild(final ImmutableNode parent, final ImmutableNode child) {
        return parent.getChildren().indexOf(child);
    }
    
    @Override
    public int getChildrenCount(final ImmutableNode node, final String name) {
        if (name == null) {
            return node.getChildren().size();
        }
        return this.getMatchingChildrenCount(node, (NodeMatcher<String>)NodeNameMatchers.EQUALS, name);
    }
    
    @Override
    public Set<String> getAttributes(final ImmutableNode node) {
        return node.getAttributes().keySet();
    }
    
    @Override
    public boolean hasAttributes(final ImmutableNode node) {
        return !node.getAttributes().isEmpty();
    }
    
    @Override
    public Object getAttributeValue(final ImmutableNode node, final String name) {
        return node.getAttributes().get(name);
    }
    
    @Override
    public boolean isDefined(final ImmutableNode node) {
        return checkIfNodeDefined(node);
    }
    
    static boolean checkIfNodeDefined(final ImmutableNode node) {
        return node.getValue() != null || !node.getChildren().isEmpty() || !node.getAttributes().isEmpty();
    }
}
