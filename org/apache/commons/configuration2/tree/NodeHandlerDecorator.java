// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Set;
import java.util.List;

public abstract class NodeHandlerDecorator<T> implements NodeHandler<T>
{
    @Override
    public String nodeName(final T node) {
        return this.getDecoratedNodeHandler().nodeName(node);
    }
    
    @Override
    public Object getValue(final T node) {
        return this.getDecoratedNodeHandler().getValue(node);
    }
    
    @Override
    public T getParent(final T node) {
        return this.getDecoratedNodeHandler().getParent(node);
    }
    
    @Override
    public List<T> getChildren(final T node) {
        return this.getDecoratedNodeHandler().getChildren(node);
    }
    
    @Override
    public <C> List<T> getMatchingChildren(final T node, final NodeMatcher<C> matcher, final C criterion) {
        return this.getDecoratedNodeHandler().getMatchingChildren(node, matcher, criterion);
    }
    
    @Override
    public <C> int getMatchingChildrenCount(final T node, final NodeMatcher<C> matcher, final C criterion) {
        return this.getDecoratedNodeHandler().getMatchingChildrenCount(node, matcher, criterion);
    }
    
    @Override
    public List<T> getChildren(final T node, final String name) {
        return this.getDecoratedNodeHandler().getChildren(node, name);
    }
    
    @Override
    public T getChild(final T node, final int index) {
        return this.getDecoratedNodeHandler().getChild(node, index);
    }
    
    @Override
    public int indexOfChild(final T parent, final T child) {
        return this.getDecoratedNodeHandler().indexOfChild(parent, child);
    }
    
    @Override
    public int getChildrenCount(final T node, final String name) {
        return this.getDecoratedNodeHandler().getChildrenCount(node, name);
    }
    
    @Override
    public Set<String> getAttributes(final T node) {
        return this.getDecoratedNodeHandler().getAttributes(node);
    }
    
    @Override
    public boolean hasAttributes(final T node) {
        return this.getDecoratedNodeHandler().hasAttributes(node);
    }
    
    @Override
    public Object getAttributeValue(final T node, final String name) {
        return this.getDecoratedNodeHandler().getAttributeValue(node, name);
    }
    
    @Override
    public boolean isDefined(final T node) {
        return this.getDecoratedNodeHandler().isDefined(node);
    }
    
    @Override
    public T getRootNode() {
        return this.getDecoratedNodeHandler().getRootNode();
    }
    
    protected abstract NodeHandler<T> getDecoratedNodeHandler();
}
