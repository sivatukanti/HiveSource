// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Token;

public interface TreeAdaptor
{
    Object create(final Token p0);
    
    Object dupNode(final Object p0);
    
    Object dupTree(final Object p0);
    
    Object nil();
    
    Object errorNode(final TokenStream p0, final Token p1, final Token p2, final RecognitionException p3);
    
    boolean isNil(final Object p0);
    
    void addChild(final Object p0, final Object p1);
    
    Object becomeRoot(final Object p0, final Object p1);
    
    Object rulePostProcessing(final Object p0);
    
    int getUniqueID(final Object p0);
    
    Object becomeRoot(final Token p0, final Object p1);
    
    Object create(final int p0, final Token p1);
    
    Object create(final int p0, final Token p1, final String p2);
    
    Object create(final int p0, final String p1);
    
    int getType(final Object p0);
    
    void setType(final Object p0, final int p1);
    
    String getText(final Object p0);
    
    void setText(final Object p0, final String p1);
    
    Token getToken(final Object p0);
    
    void setTokenBoundaries(final Object p0, final Token p1, final Token p2);
    
    int getTokenStartIndex(final Object p0);
    
    int getTokenStopIndex(final Object p0);
    
    Object getChild(final Object p0, final int p1);
    
    void setChild(final Object p0, final int p1, final Object p2);
    
    Object deleteChild(final Object p0, final int p1);
    
    int getChildCount(final Object p0);
    
    Object getParent(final Object p0);
    
    void setParent(final Object p0, final Object p1);
    
    int getChildIndex(final Object p0);
    
    void setChildIndex(final Object p0, final int p1);
    
    void replaceChildren(final Object p0, final int p1, final int p2, final Object p3);
}
