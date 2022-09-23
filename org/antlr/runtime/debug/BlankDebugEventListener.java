// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class BlankDebugEventListener implements DebugEventListener
{
    public void enterRule(final String grammarFileName, final String ruleName) {
    }
    
    public void exitRule(final String grammarFileName, final String ruleName) {
    }
    
    public void enterAlt(final int alt) {
    }
    
    public void enterSubRule(final int decisionNumber) {
    }
    
    public void exitSubRule(final int decisionNumber) {
    }
    
    public void enterDecision(final int decisionNumber, final boolean couldBacktrack) {
    }
    
    public void exitDecision(final int decisionNumber) {
    }
    
    public void location(final int line, final int pos) {
    }
    
    public void consumeToken(final Token token) {
    }
    
    public void consumeHiddenToken(final Token token) {
    }
    
    public void LT(final int i, final Token t) {
    }
    
    public void mark(final int i) {
    }
    
    public void rewind(final int i) {
    }
    
    public void rewind() {
    }
    
    public void beginBacktrack(final int level) {
    }
    
    public void endBacktrack(final int level, final boolean successful) {
    }
    
    public void recognitionException(final RecognitionException e) {
    }
    
    public void beginResync() {
    }
    
    public void endResync() {
    }
    
    public void semanticPredicate(final boolean result, final String predicate) {
    }
    
    public void commence() {
    }
    
    public void terminate() {
    }
    
    public void consumeNode(final Object t) {
    }
    
    public void LT(final int i, final Object t) {
    }
    
    public void nilNode(final Object t) {
    }
    
    public void errorNode(final Object t) {
    }
    
    public void createNode(final Object t) {
    }
    
    public void createNode(final Object node, final Token token) {
    }
    
    public void becomeRoot(final Object newRoot, final Object oldRoot) {
    }
    
    public void addChild(final Object root, final Object child) {
    }
    
    public void setTokenBoundaries(final Object t, final int tokenStartIndex, final int tokenStopIndex) {
    }
}
