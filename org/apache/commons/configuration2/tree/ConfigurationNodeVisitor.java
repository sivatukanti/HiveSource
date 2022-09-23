// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

public interface ConfigurationNodeVisitor<T>
{
    void visitBeforeChildren(final T p0, final NodeHandler<T> p1);
    
    void visitAfterChildren(final T p0, final NodeHandler<T> p1);
    
    boolean terminate();
}
