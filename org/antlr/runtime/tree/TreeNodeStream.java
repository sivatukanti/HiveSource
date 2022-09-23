// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.TokenStream;
import org.antlr.runtime.IntStream;

public interface TreeNodeStream extends IntStream
{
    Object get(final int p0);
    
    Object LT(final int p0);
    
    Object getTreeSource();
    
    TokenStream getTokenStream();
    
    TreeAdaptor getTreeAdaptor();
    
    void setUniqueNavigationNodes(final boolean p0);
    
    void reset();
    
    String toString(final Object p0, final Object p1);
    
    void replaceChildren(final Object p0, final int p1, final int p2, final Object p3);
}
