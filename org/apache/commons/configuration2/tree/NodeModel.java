// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Collection;

public interface NodeModel<T>
{
    void setRootNode(final T p0);
    
    NodeHandler<T> getNodeHandler();
    
    void addProperty(final String p0, final Iterable<?> p1, final NodeKeyResolver<T> p2);
    
    void addNodes(final String p0, final Collection<? extends T> p1, final NodeKeyResolver<T> p2);
    
    void setProperty(final String p0, final Object p1, final NodeKeyResolver<T> p2);
    
    Object clearTree(final String p0, final NodeKeyResolver<T> p1);
    
    void clearProperty(final String p0, final NodeKeyResolver<T> p1);
    
    void clear(final NodeKeyResolver<T> p0);
    
    ImmutableNode getInMemoryRepresentation();
}
