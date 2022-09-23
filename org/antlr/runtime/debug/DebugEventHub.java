// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import java.util.ArrayList;
import java.util.List;

public class DebugEventHub implements DebugEventListener
{
    protected List listeners;
    
    public DebugEventHub(final DebugEventListener listener) {
        (this.listeners = new ArrayList()).add(listener);
    }
    
    public DebugEventHub(final DebugEventListener a, final DebugEventListener b) {
        (this.listeners = new ArrayList()).add(a);
        this.listeners.add(b);
    }
    
    public void addListener(final DebugEventListener listener) {
        this.listeners.add(listener);
    }
    
    public void enterRule(final String grammarFileName, final String ruleName) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.enterRule(grammarFileName, ruleName);
        }
    }
    
    public void exitRule(final String grammarFileName, final String ruleName) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.exitRule(grammarFileName, ruleName);
        }
    }
    
    public void enterAlt(final int alt) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.enterAlt(alt);
        }
    }
    
    public void enterSubRule(final int decisionNumber) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.enterSubRule(decisionNumber);
        }
    }
    
    public void exitSubRule(final int decisionNumber) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.exitSubRule(decisionNumber);
        }
    }
    
    public void enterDecision(final int decisionNumber, final boolean couldBacktrack) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.enterDecision(decisionNumber, couldBacktrack);
        }
    }
    
    public void exitDecision(final int decisionNumber) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.exitDecision(decisionNumber);
        }
    }
    
    public void location(final int line, final int pos) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.location(line, pos);
        }
    }
    
    public void consumeToken(final Token token) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.consumeToken(token);
        }
    }
    
    public void consumeHiddenToken(final Token token) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.consumeHiddenToken(token);
        }
    }
    
    public void LT(final int index, final Token t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.LT(index, t);
        }
    }
    
    public void mark(final int index) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.mark(index);
        }
    }
    
    public void rewind(final int index) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.rewind(index);
        }
    }
    
    public void rewind() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.rewind();
        }
    }
    
    public void beginBacktrack(final int level) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.beginBacktrack(level);
        }
    }
    
    public void endBacktrack(final int level, final boolean successful) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.endBacktrack(level, successful);
        }
    }
    
    public void recognitionException(final RecognitionException e) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.recognitionException(e);
        }
    }
    
    public void beginResync() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.beginResync();
        }
    }
    
    public void endResync() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.endResync();
        }
    }
    
    public void semanticPredicate(final boolean result, final String predicate) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.semanticPredicate(result, predicate);
        }
    }
    
    public void commence() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.commence();
        }
    }
    
    public void terminate() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.terminate();
        }
    }
    
    public void consumeNode(final Object t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.consumeNode(t);
        }
    }
    
    public void LT(final int index, final Object t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.LT(index, t);
        }
    }
    
    public void nilNode(final Object t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.nilNode(t);
        }
    }
    
    public void errorNode(final Object t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.errorNode(t);
        }
    }
    
    public void createNode(final Object t) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.createNode(t);
        }
    }
    
    public void createNode(final Object node, final Token token) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.createNode(node, token);
        }
    }
    
    public void becomeRoot(final Object newRoot, final Object oldRoot) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.becomeRoot(newRoot, oldRoot);
        }
    }
    
    public void addChild(final Object root, final Object child) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.addChild(root, child);
        }
    }
    
    public void setTokenBoundaries(final Object t, final int tokenStartIndex, final int tokenStopIndex) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            final DebugEventListener listener = this.listeners.get(i);
            listener.setTokenBoundaries(t, tokenStartIndex, tokenStopIndex);
        }
    }
}
