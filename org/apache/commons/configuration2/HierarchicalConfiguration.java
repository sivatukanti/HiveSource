// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.List;
import java.util.Collection;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeModelSupport;

public interface HierarchicalConfiguration<T> extends Configuration, ImmutableHierarchicalConfiguration, NodeModelSupport<T>
{
    void setExpressionEngine(final ExpressionEngine p0);
    
    void addNodes(final String p0, final Collection<? extends T> p1);
    
    HierarchicalConfiguration<T> configurationAt(final String p0, final boolean p1);
    
    HierarchicalConfiguration<T> configurationAt(final String p0);
    
    List<HierarchicalConfiguration<T>> configurationsAt(final String p0);
    
    List<HierarchicalConfiguration<T>> configurationsAt(final String p0, final boolean p1);
    
    List<HierarchicalConfiguration<T>> childConfigurationsAt(final String p0);
    
    List<HierarchicalConfiguration<T>> childConfigurationsAt(final String p0, final boolean p1);
    
    void clearTree(final String p0);
}
