// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;
import java.util.List;

public interface Tree
{
    public static final Tree INVALID_NODE = new CommonTree(Token.INVALID_TOKEN);
    
    Tree getChild(final int p0);
    
    int getChildCount();
    
    Tree getParent();
    
    void setParent(final Tree p0);
    
    boolean hasAncestor(final int p0);
    
    Tree getAncestor(final int p0);
    
    List getAncestors();
    
    int getChildIndex();
    
    void setChildIndex(final int p0);
    
    void freshenParentAndChildIndexes();
    
    void addChild(final Tree p0);
    
    void setChild(final int p0, final Tree p1);
    
    Object deleteChild(final int p0);
    
    void replaceChildren(final int p0, final int p1, final Object p2);
    
    boolean isNil();
    
    int getTokenStartIndex();
    
    void setTokenStartIndex(final int p0);
    
    int getTokenStopIndex();
    
    void setTokenStopIndex(final int p0);
    
    Tree dupNode();
    
    int getType();
    
    String getText();
    
    int getLine();
    
    int getCharPositionInLine();
    
    String toStringTree();
    
    String toString();
}
