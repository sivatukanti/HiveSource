// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class DebugEventRepeater implements DebugEventListener
{
    protected DebugEventListener listener;
    
    public DebugEventRepeater(final DebugEventListener listener) {
        this.listener = listener;
    }
    
    public void enterRule(final String grammarFileName, final String ruleName) {
        this.listener.enterRule(grammarFileName, ruleName);
    }
    
    public void exitRule(final String grammarFileName, final String ruleName) {
        this.listener.exitRule(grammarFileName, ruleName);
    }
    
    public void enterAlt(final int alt) {
        this.listener.enterAlt(alt);
    }
    
    public void enterSubRule(final int decisionNumber) {
        this.listener.enterSubRule(decisionNumber);
    }
    
    public void exitSubRule(final int decisionNumber) {
        this.listener.exitSubRule(decisionNumber);
    }
    
    public void enterDecision(final int decisionNumber, final boolean couldBacktrack) {
        this.listener.enterDecision(decisionNumber, couldBacktrack);
    }
    
    public void exitDecision(final int decisionNumber) {
        this.listener.exitDecision(decisionNumber);
    }
    
    public void location(final int line, final int pos) {
        this.listener.location(line, pos);
    }
    
    public void consumeToken(final Token token) {
        this.listener.consumeToken(token);
    }
    
    public void consumeHiddenToken(final Token token) {
        this.listener.consumeHiddenToken(token);
    }
    
    public void LT(final int i, final Token t) {
        this.listener.LT(i, t);
    }
    
    public void mark(final int i) {
        this.listener.mark(i);
    }
    
    public void rewind(final int i) {
        this.listener.rewind(i);
    }
    
    public void rewind() {
        this.listener.rewind();
    }
    
    public void beginBacktrack(final int level) {
        this.listener.beginBacktrack(level);
    }
    
    public void endBacktrack(final int level, final boolean successful) {
        this.listener.endBacktrack(level, successful);
    }
    
    public void recognitionException(final RecognitionException e) {
        this.listener.recognitionException(e);
    }
    
    public void beginResync() {
        this.listener.beginResync();
    }
    
    public void endResync() {
        this.listener.endResync();
    }
    
    public void semanticPredicate(final boolean result, final String predicate) {
        this.listener.semanticPredicate(result, predicate);
    }
    
    public void commence() {
        this.listener.commence();
    }
    
    public void terminate() {
        this.listener.terminate();
    }
    
    public void consumeNode(final Object t) {
        this.listener.consumeNode(t);
    }
    
    public void LT(final int i, final Object t) {
        this.listener.LT(i, t);
    }
    
    public void nilNode(final Object t) {
        this.listener.nilNode(t);
    }
    
    public void errorNode(final Object t) {
        this.listener.errorNode(t);
    }
    
    public void createNode(final Object t) {
        this.listener.createNode(t);
    }
    
    public void createNode(final Object node, final Token token) {
        this.listener.createNode(node, token);
    }
    
    public void becomeRoot(final Object newRoot, final Object oldRoot) {
        this.listener.becomeRoot(newRoot, oldRoot);
    }
    
    public void addChild(final Object root, final Object child) {
        this.listener.addChild(root, child);
    }
    
    public void setTokenBoundaries(final Object t, final int tokenStartIndex, final int tokenStopIndex) {
        this.listener.setTokenBoundaries(t, tokenStartIndex, tokenStopIndex);
    }
}
