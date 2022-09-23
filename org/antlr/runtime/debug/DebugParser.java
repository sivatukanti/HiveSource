// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import java.io.IOException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Parser;

public class DebugParser extends Parser
{
    protected DebugEventListener dbg;
    public boolean isCyclicDecision;
    
    public DebugParser(final TokenStream input, final DebugEventListener dbg, final RecognizerSharedState state) {
        super((input instanceof DebugTokenStream) ? input : new DebugTokenStream(input, dbg), state);
        this.dbg = null;
        this.isCyclicDecision = false;
        this.setDebugListener(dbg);
    }
    
    public DebugParser(final TokenStream input, final RecognizerSharedState state) {
        super((input instanceof DebugTokenStream) ? input : new DebugTokenStream(input, null), state);
        this.dbg = null;
        this.isCyclicDecision = false;
    }
    
    public DebugParser(final TokenStream input, final DebugEventListener dbg) {
        this((input instanceof DebugTokenStream) ? input : new DebugTokenStream(input, dbg), dbg, null);
    }
    
    public void setDebugListener(final DebugEventListener dbg) {
        if (this.input instanceof DebugTokenStream) {
            ((DebugTokenStream)this.input).setDebugListener(dbg);
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
    
    public void reportError(final RecognitionException e) {
        super.reportError(e);
        this.dbg.recognitionException(e);
    }
}
