// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

public interface NodeMatcher<C>
{
     <T> boolean matches(final T p0, final NodeHandler<T> p1, final C p2);
}
