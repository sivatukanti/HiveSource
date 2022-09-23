// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;

class FindNodeVisitor<T> extends ConfigurationNodeVisitorAdapter<T>
{
    private final T searchNode;
    private boolean found;
    
    public FindNodeVisitor(final T node) {
        this.searchNode = node;
    }
    
    @Override
    public void visitBeforeChildren(final T node, final NodeHandler<T> handler) {
        if (node.equals(this.searchNode)) {
            this.found = true;
        }
    }
    
    @Override
    public boolean terminate() {
        return this.found;
    }
    
    public boolean isFound() {
        return this.found;
    }
    
    public void reset() {
        this.found = false;
    }
}
