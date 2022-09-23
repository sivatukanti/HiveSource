// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.TreeAdaptor;

public class TraceDebugEventListener extends BlankDebugEventListener
{
    TreeAdaptor adaptor;
    
    public TraceDebugEventListener(final TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    
    public void enterRule(final String ruleName) {
        System.out.println("enterRule " + ruleName);
    }
    
    public void exitRule(final String ruleName) {
        System.out.println("exitRule " + ruleName);
    }
    
    public void enterSubRule(final int decisionNumber) {
        System.out.println("enterSubRule");
    }
    
    public void exitSubRule(final int decisionNumber) {
        System.out.println("exitSubRule");
    }
    
    public void location(final int line, final int pos) {
        System.out.println("location " + line + ":" + pos);
    }
    
    public void consumeNode(final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        System.out.println("consumeNode " + ID + " " + text + " " + type);
    }
    
    public void LT(final int i, final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        System.out.println("LT " + i + " " + ID + " " + text + " " + type);
    }
    
    public void nilNode(final Object t) {
        System.out.println("nilNode " + this.adaptor.getUniqueID(t));
    }
    
    public void createNode(final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        System.out.println("create " + ID + ": " + text + ", " + type);
    }
    
    public void createNode(final Object node, final Token token) {
        final int ID = this.adaptor.getUniqueID(node);
        final String text = this.adaptor.getText(node);
        final int tokenIndex = token.getTokenIndex();
        System.out.println("create " + ID + ": " + tokenIndex);
    }
    
    public void becomeRoot(final Object newRoot, final Object oldRoot) {
        System.out.println("becomeRoot " + this.adaptor.getUniqueID(newRoot) + ", " + this.adaptor.getUniqueID(oldRoot));
    }
    
    public void addChild(final Object root, final Object child) {
        System.out.println("addChild " + this.adaptor.getUniqueID(root) + ", " + this.adaptor.getUniqueID(child));
    }
    
    public void setTokenBoundaries(final Object t, final int tokenStartIndex, final int tokenStopIndex) {
        System.out.println("setTokenBoundaries " + this.adaptor.getUniqueID(t) + ", " + tokenStartIndex + ", " + tokenStopIndex);
    }
}
