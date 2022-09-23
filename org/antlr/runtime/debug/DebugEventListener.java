// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public interface DebugEventListener
{
    public static final String PROTOCOL_VERSION = "2";
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    
    void enterRule(final String p0, final String p1);
    
    void enterAlt(final int p0);
    
    void exitRule(final String p0, final String p1);
    
    void enterSubRule(final int p0);
    
    void exitSubRule(final int p0);
    
    void enterDecision(final int p0, final boolean p1);
    
    void exitDecision(final int p0);
    
    void consumeToken(final Token p0);
    
    void consumeHiddenToken(final Token p0);
    
    void LT(final int p0, final Token p1);
    
    void mark(final int p0);
    
    void rewind(final int p0);
    
    void rewind();
    
    void beginBacktrack(final int p0);
    
    void endBacktrack(final int p0, final boolean p1);
    
    void location(final int p0, final int p1);
    
    void recognitionException(final RecognitionException p0);
    
    void beginResync();
    
    void endResync();
    
    void semanticPredicate(final boolean p0, final String p1);
    
    void commence();
    
    void terminate();
    
    void consumeNode(final Object p0);
    
    void LT(final int p0, final Object p1);
    
    void nilNode(final Object p0);
    
    void errorNode(final Object p0);
    
    void createNode(final Object p0);
    
    void createNode(final Object p0, final Token p1);
    
    void becomeRoot(final Object p0, final Object p1);
    
    void addChild(final Object p0, final Object p1);
    
    void setTokenBoundaries(final Object p0, final int p1, final int p2);
}
