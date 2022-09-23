// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Set;
import java.util.List;

public interface NodeHandler<T>
{
    String nodeName(final T p0);
    
    Object getValue(final T p0);
    
    T getParent(final T p0);
    
    List<T> getChildren(final T p0);
    
    List<T> getChildren(final T p0, final String p1);
    
     <C> List<T> getMatchingChildren(final T p0, final NodeMatcher<C> p1, final C p2);
    
    T getChild(final T p0, final int p1);
    
    int indexOfChild(final T p0, final T p1);
    
    int getChildrenCount(final T p0, final String p1);
    
     <C> int getMatchingChildrenCount(final T p0, final NodeMatcher<C> p1, final C p2);
    
    Set<String> getAttributes(final T p0);
    
    boolean hasAttributes(final T p0);
    
    Object getAttributeValue(final T p0, final String p1);
    
    boolean isDefined(final T p0);
    
    T getRootNode();
}
