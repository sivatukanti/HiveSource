// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParseTreeBuilder extends BlankDebugEventListener
{
    public static final String EPSILON_PAYLOAD = "<epsilon>";
    Stack callStack;
    List hiddenTokens;
    int backtracking;
    
    public ParseTreeBuilder(final String grammarName) {
        this.callStack = new Stack();
        this.hiddenTokens = new ArrayList();
        this.backtracking = 0;
        final ParseTree root = this.create("<grammar " + grammarName + ">");
        this.callStack.push(root);
    }
    
    public ParseTree getTree() {
        return (ParseTree)this.callStack.elementAt(0);
    }
    
    public ParseTree create(final Object payload) {
        return new ParseTree(payload);
    }
    
    public ParseTree epsilonNode() {
        return this.create("<epsilon>");
    }
    
    public void enterDecision(final int d, final boolean couldBacktrack) {
        ++this.backtracking;
    }
    
    public void exitDecision(final int i) {
        --this.backtracking;
    }
    
    public void enterRule(final String filename, final String ruleName) {
        if (this.backtracking > 0) {
            return;
        }
        final ParseTree parentRuleNode = this.callStack.peek();
        final ParseTree ruleNode = this.create(ruleName);
        parentRuleNode.addChild(ruleNode);
        this.callStack.push(ruleNode);
    }
    
    public void exitRule(final String filename, final String ruleName) {
        if (this.backtracking > 0) {
            return;
        }
        final ParseTree ruleNode = this.callStack.peek();
        if (ruleNode.getChildCount() == 0) {
            ruleNode.addChild(this.epsilonNode());
        }
        this.callStack.pop();
    }
    
    public void consumeToken(final Token token) {
        if (this.backtracking > 0) {
            return;
        }
        final ParseTree ruleNode = this.callStack.peek();
        final ParseTree elementNode = this.create(token);
        elementNode.hiddenTokens = this.hiddenTokens;
        this.hiddenTokens = new ArrayList();
        ruleNode.addChild(elementNode);
    }
    
    public void consumeHiddenToken(final Token token) {
        if (this.backtracking > 0) {
            return;
        }
        this.hiddenTokens.add(token);
    }
    
    public void recognitionException(final RecognitionException e) {
        if (this.backtracking > 0) {
            return;
        }
        final ParseTree ruleNode = this.callStack.peek();
        final ParseTree errorNode = this.create(e);
        ruleNode.addChild(errorNode);
    }
}
