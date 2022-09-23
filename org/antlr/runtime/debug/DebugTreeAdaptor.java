// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.TreeAdaptor;

public class DebugTreeAdaptor implements TreeAdaptor
{
    protected DebugEventListener dbg;
    protected TreeAdaptor adaptor;
    
    public DebugTreeAdaptor(final DebugEventListener dbg, final TreeAdaptor adaptor) {
        this.dbg = dbg;
        this.adaptor = adaptor;
    }
    
    public Object create(final Token payload) {
        if (payload.getTokenIndex() < 0) {
            return this.create(payload.getType(), payload.getText());
        }
        final Object node = this.adaptor.create(payload);
        this.dbg.createNode(node, payload);
        return node;
    }
    
    public Object errorNode(final TokenStream input, final Token start, final Token stop, final RecognitionException e) {
        final Object node = this.adaptor.errorNode(input, start, stop, e);
        if (node != null) {
            this.dbg.errorNode(node);
        }
        return node;
    }
    
    public Object dupTree(final Object tree) {
        final Object t = this.adaptor.dupTree(tree);
        this.simulateTreeConstruction(t);
        return t;
    }
    
    protected void simulateTreeConstruction(final Object t) {
        this.dbg.createNode(t);
        for (int n = this.adaptor.getChildCount(t), i = 0; i < n; ++i) {
            final Object child = this.adaptor.getChild(t, i);
            this.simulateTreeConstruction(child);
            this.dbg.addChild(t, child);
        }
    }
    
    public Object dupNode(final Object treeNode) {
        final Object d = this.adaptor.dupNode(treeNode);
        this.dbg.createNode(d);
        return d;
    }
    
    public Object nil() {
        final Object node = this.adaptor.nil();
        this.dbg.nilNode(node);
        return node;
    }
    
    public boolean isNil(final Object tree) {
        return this.adaptor.isNil(tree);
    }
    
    public void addChild(final Object t, final Object child) {
        if (t == null || child == null) {
            return;
        }
        this.adaptor.addChild(t, child);
        this.dbg.addChild(t, child);
    }
    
    public Object becomeRoot(final Object newRoot, final Object oldRoot) {
        final Object n = this.adaptor.becomeRoot(newRoot, oldRoot);
        this.dbg.becomeRoot(newRoot, oldRoot);
        return n;
    }
    
    public Object rulePostProcessing(final Object root) {
        return this.adaptor.rulePostProcessing(root);
    }
    
    public void addChild(final Object t, final Token child) {
        final Object n = this.create(child);
        this.addChild(t, n);
    }
    
    public Object becomeRoot(final Token newRoot, final Object oldRoot) {
        final Object n = this.create(newRoot);
        this.adaptor.becomeRoot(n, oldRoot);
        this.dbg.becomeRoot(newRoot, oldRoot);
        return n;
    }
    
    public Object create(final int tokenType, final Token fromToken) {
        final Object node = this.adaptor.create(tokenType, fromToken);
        this.dbg.createNode(node);
        return node;
    }
    
    public Object create(final int tokenType, final Token fromToken, final String text) {
        final Object node = this.adaptor.create(tokenType, fromToken, text);
        this.dbg.createNode(node);
        return node;
    }
    
    public Object create(final int tokenType, final String text) {
        final Object node = this.adaptor.create(tokenType, text);
        this.dbg.createNode(node);
        return node;
    }
    
    public int getType(final Object t) {
        return this.adaptor.getType(t);
    }
    
    public void setType(final Object t, final int type) {
        this.adaptor.setType(t, type);
    }
    
    public String getText(final Object t) {
        return this.adaptor.getText(t);
    }
    
    public void setText(final Object t, final String text) {
        this.adaptor.setText(t, text);
    }
    
    public Token getToken(final Object t) {
        return this.adaptor.getToken(t);
    }
    
    public void setTokenBoundaries(final Object t, final Token startToken, final Token stopToken) {
        this.adaptor.setTokenBoundaries(t, startToken, stopToken);
        if (t != null && startToken != null && stopToken != null) {
            this.dbg.setTokenBoundaries(t, startToken.getTokenIndex(), stopToken.getTokenIndex());
        }
    }
    
    public int getTokenStartIndex(final Object t) {
        return this.adaptor.getTokenStartIndex(t);
    }
    
    public int getTokenStopIndex(final Object t) {
        return this.adaptor.getTokenStopIndex(t);
    }
    
    public Object getChild(final Object t, final int i) {
        return this.adaptor.getChild(t, i);
    }
    
    public void setChild(final Object t, final int i, final Object child) {
        this.adaptor.setChild(t, i, child);
    }
    
    public Object deleteChild(final Object t, final int i) {
        return this.deleteChild(t, i);
    }
    
    public int getChildCount(final Object t) {
        return this.adaptor.getChildCount(t);
    }
    
    public int getUniqueID(final Object node) {
        return this.adaptor.getUniqueID(node);
    }
    
    public Object getParent(final Object t) {
        return this.adaptor.getParent(t);
    }
    
    public int getChildIndex(final Object t) {
        return this.adaptor.getChildIndex(t);
    }
    
    public void setParent(final Object t, final Object parent) {
        this.adaptor.setParent(t, parent);
    }
    
    public void setChildIndex(final Object t, final int index) {
        this.adaptor.setChildIndex(t, index);
    }
    
    public void replaceChildren(final Object parent, final int startChildIndex, final int stopChildIndex, final Object t) {
        this.adaptor.replaceChildren(parent, startChildIndex, stopChildIndex, t);
    }
    
    public DebugEventListener getDebugListener() {
        return this.dbg;
    }
    
    public void setDebugListener(final DebugEventListener dbg) {
        this.dbg = dbg;
    }
    
    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }
}
