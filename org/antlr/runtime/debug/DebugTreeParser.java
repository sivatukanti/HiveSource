// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;
import java.io.IOException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

public class DebugTreeParser extends TreeParser
{
    protected DebugEventListener dbg;
    public boolean isCyclicDecision;
    
    public DebugTreeParser(final TreeNodeStream input, final DebugEventListener dbg, final RecognizerSharedState state) {
        super((input instanceof DebugTreeNodeStream) ? input : new DebugTreeNodeStream(input, dbg), state);
        this.dbg = null;
        this.isCyclicDecision = false;
        this.setDebugListener(dbg);
    }
    
    public DebugTreeParser(final TreeNodeStream input, final RecognizerSharedState state) {
        super((input instanceof DebugTreeNodeStream) ? input : new DebugTreeNodeStream(input, null), state);
        this.dbg = null;
        this.isCyclicDecision = false;
    }
    
    public DebugTreeParser(final TreeNodeStream input, final DebugEventListener dbg) {
        this((input instanceof DebugTreeNodeStream) ? input : new DebugTreeNodeStream(input, dbg), dbg, null);
    }
    
    public void setDebugListener(final DebugEventListener dbg) {
        if (this.input instanceof DebugTreeNodeStream) {
            ((DebugTreeNodeStream)this.input).setDebugListener(dbg);
        }
        this.dbg = dbg;
    }
    
    public DebugEventListener getDebugListener() {
        return this.dbg;
    }
    
    public void reportError(final IOException e) {
        System.err.println(e);
        e.printStackTrace(System.err);
    }
    
    public void reportError(final RecognitionException e) {
        this.dbg.recognitionException(e);
    }
    
    protected Object getMissingSymbol(final IntStream input, final RecognitionException e, final int expectedTokenType, final BitSet follow) {
        final Object o = super.getMissingSymbol(input, e, expectedTokenType, follow);
        this.dbg.consumeNode(o);
        return o;
    }
    
    public void beginResync() {
        this.dbg.beginResync();
    }
    
    public void endResync() {
        this.dbg.endResync();
    }
    
    public void beginBacktrack(final int level) {
        this.dbg.beginBacktrack(level);
    }
    
    public void endBacktrack(final int level, final boolean successful) {
        this.dbg.endBacktrack(level, successful);
    }
}
