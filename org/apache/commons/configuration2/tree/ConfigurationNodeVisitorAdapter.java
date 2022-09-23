// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

public class ConfigurationNodeVisitorAdapter<T> implements ConfigurationNodeVisitor<T>
{
    @Override
    public void visitBeforeChildren(final T node, final NodeHandler<T> handler) {
    }
    
    @Override
    public void visitAfterChildren(final T node, final NodeHandler<T> handler) {
    }
    
    @Override
    public boolean terminate() {
        return false;
    }
}
